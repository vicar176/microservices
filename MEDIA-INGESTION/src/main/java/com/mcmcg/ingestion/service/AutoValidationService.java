package com.mcmcg.ingestion.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.Account;
import com.mcmcg.ingestion.domain.DataElement;
import com.mcmcg.ingestion.domain.DocumentFieldsDefinitionModel;
import com.mcmcg.ingestion.domain.MediaDocument;
import com.mcmcg.ingestion.domain.MediaMetadataModel;
import com.mcmcg.ingestion.domain.MediaMetadataModel.AutoValidation;
import com.mcmcg.ingestion.domain.Response;
import com.mcmcg.ingestion.domain.TemplateMappingProfileModel;
import com.mcmcg.ingestion.exception.IngestionServiceException;
import com.mcmcg.ingestion.exception.MediaServiceException;
import com.mcmcg.ingestion.service.media.CreateSnippetsUtilityService;
import com.mcmcg.ingestion.service.media.IService;
import com.mcmcg.ingestion.service.media.MediaMetadataModelService;
import com.mcmcg.ingestion.util.EventCode;
import com.mcmcg.ingestion.util.IngestionUtils;
import com.mcmcg.ingestion.util.MediaMetadataModelUtil;

/**
 * 
 * @author Jose Aleman
 *
 */
@Service
public class AutoValidationService extends BaseIngestionService {

	private static final Logger LOG = Logger.getLogger(AutoValidationService.class);

	@SuppressWarnings("unchecked")
	@Override
	protected MediaMetadataModel executeService(MediaDocument mediaDocument, Object... params)
			throws IngestionServiceException, MediaServiceException {

		return validate(mediaDocument, params);

	}

	@Override
	protected String getDocumentStatus() {

		return IngestionUtils.VALIDATED;
	}

	/**
	 * 
	 * @param mediaDocument
	 * @return
	 */
	private MediaMetadataModel validate(MediaDocument mediaDocument, Object... params)
			throws IngestionServiceException, MediaServiceException {

		LOG.info("Validate document: " + mediaDocument.getDocumentId());

		// 1. Call metadata validation service
		MediaMetadataModel mediaMetadataModel = validateDocument(mediaDocument.getDocumentId());

		// 2. Call metadata service to save auto validated flag (PUT)
		mediaMetadataModel = saveAutoValidateState(mediaDocument, mediaMetadataModel);

		LOG.info("Validated document: " + mediaDocument.getDocumentId() + "  MediaMetadataModel: " + mediaMetadataModel);

		return mediaMetadataModel;
	}

	/**
	 * @param mediaDocument
	 * @param mediaMetadataModel
	 * @throws IngestionServiceException
	 */
	private MediaMetadataModel saveAutoValidateState(MediaDocument mediaDocument, MediaMetadataModel mediaMetadataModel)
			throws IngestionServiceException, MediaServiceException {

		// Setting autovalidation
		Response<MediaMetadataModel> response = null;

		DataElement invalid = validateExtractedElements(mediaMetadataModel.getDataElements());

		if (invalid != null) {
			response = putAutoValidation(mediaDocument.getDocumentId(), mediaMetadataModel, false);
			createSnippets(mediaDocument);
			String message = String.format("Failed while validating document [%s] for DataElement [%s]", mediaDocument.getDocumentId(),
					invalid.getFieldDefinition().getFieldName());
			throw new IngestionServiceException(message);
		}

		response = putAutoValidation(mediaDocument.getDocumentId(), mediaMetadataModel, true);

		if (response.getData() == null) {
			String message = String.format("Failed while saving metadata document [%s] for DataElement [%s]",
					mediaDocument.getDocumentId(), invalid);
			throw new IngestionServiceException(message);
		}
		LOG.debug(String.format("Document %s is valid", response.getData(), mediaDocument.getDocumentId()));

		return response.getData();
	}

	/**
	 * @param documentId
	 * @param mediaMetadataModel
	 * @param isValid
	 * @return
	 * @throws MediaServiceException
	 */
	private Response<MediaMetadataModel> putAutoValidation(Long documentId, MediaMetadataModel mediaMetadataModel,
			boolean isValid) throws MediaServiceException {
		mediaMetadataModel.setAutoValidation(new AutoValidation(isValid, IngestionUtils.formatDate(new Date())));
		mediaMetadataModel.setDocumentStatus(IngestionUtils.VALIDATED);
		// LOG.debug(IngestionUtils.getJsonObject(MediaMetadataModel));
		String resource = MediaMetadataModelService.PUT_AUTO_VALIDATION.replaceAll("documentId",
				String.valueOf(documentId));
		Response<MediaMetadataModel> response = mediaMetadataService.execute(resource, IService.PUT,
				mediaMetadataModel);
		return response;
	}

	/**
	 * @param mediaDocument
	 * @return
	 * @throws IngestionServiceException
	 */
	private MediaMetadataModel validateDocument(Long documentId)
			throws IngestionServiceException, MediaServiceException {

		String resource = MediaMetadataModelService.PUT_DATA_ELEMENTS_VALIDATES.replaceAll("documentId",
				String.valueOf(documentId));
		Response<MediaMetadataModel> response = mediaMetadataService.execute(resource, IService.PUT);

		if (response.getError().getCode() != EventCode.REQUEST_SUCCESS.getCode()) {
			LOG.error(response.getError().getMessage());
			throw new IngestionServiceException(response.getError().getMessage());
		}

		LOG.debug(String.format("MediaMetadataModel Info: %s for document %s", response.getData(), documentId));

		return response.getData();
	}

	/**
	 * @param mediaDocument
	 * @param account
	 * @param affinities
	 * @throws IngestionServiceException
	 */
	private Boolean createSnippets(MediaDocument mediaDocument)
			throws IngestionServiceException, MediaServiceException {
		
		Boolean createSnippets = false;
		
		//1. Call Document Field Service
		Account account = retrieveAccountInfo(mediaDocument);  //accounts.war  Datapower ------ DB2
		MediaMetadataModel mediaMetadataModelSaved = getMediaMetadataByDocumentId(mediaDocument); //media-metadata.war
		DocumentFieldsDefinitionModel documentFieldsDefinitionModel = retrieveDocumentFieldsDefinitionModel(mediaDocument, account, mediaMetadataModelSaved); //media-profiles.war
		
		Response<Boolean> response = new Response<Boolean>();
		
		//3. If Document Field Definition is available
		if (documentFieldsDefinitionModel != null){
			//4. Call template mapping service to get template mapping profile
			TemplateMappingProfileModel template =retrieveTemplateMappingProfile(mediaDocument, account, 
					documentFieldsDefinitionModel.getDocumentType(), mediaMetadataModelSaved); //media-profiles.war

			String resource = String.format(CreateSnippetsUtilityService.PUT_CREATE_SNIPPETS, mediaDocument.getFilename(), mediaDocument.getBucket()) ;
			List<DataElement> returnList = new ArrayList<DataElement>();

			if (template != null){
				LOG.debug(IngestionUtils.getJsonObject(template));
				
				response = createSnippetsService.execute( resource, IService.PUT, template);
				
				if (response.getError().getCode() == EventCode.REQUEST_SUCCESS.getCode()){
					
					createSnippets = response.getData();
					LOG.debug(String.format("Snippets were created ", returnList, mediaDocument));
					
				}else{
					
					postMetadata(mediaDocument, MediaMetadataModelUtil.buildMediaMetadataModel(mediaDocument, account, template, null, getDocumentStatus(), null));
					throw new MediaServiceException(response.getError().getMessage());
				}
			}
		}
		
		return  createSnippets;
	}

	/**
	 * 
	 * @param dataElements
	 * @return
	 */
	private DataElement validateExtractedElements(List<DataElement> dataElements) {

		DataElement invalid = null;

		for (DataElement element : dataElements) {
			if (!element.getValidated() && element.getFieldDefinition().isFieldRequired()) {
				invalid = element;
				break;
			}
		}

		return invalid;
	}

}
