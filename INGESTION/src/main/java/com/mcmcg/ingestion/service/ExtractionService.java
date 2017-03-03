package com.mcmcg.ingestion.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.Account;
import com.mcmcg.ingestion.domain.DataElement;
import com.mcmcg.ingestion.domain.DocumentFieldsDefinitionModel;
import com.mcmcg.ingestion.domain.DocumentFieldsDefinitionModel.DocumentFieldDefinition.DocumentType;
import com.mcmcg.ingestion.domain.MediaDocument;
import com.mcmcg.ingestion.domain.MediaMetadataModel;
import com.mcmcg.ingestion.domain.Response;
import com.mcmcg.ingestion.domain.TemplateMappingProfileModel;
import com.mcmcg.ingestion.exception.IngestionServiceException;
import com.mcmcg.ingestion.exception.MediaServiceException;
import com.mcmcg.ingestion.service.media.ExtractionUtilityService;
import com.mcmcg.ingestion.service.media.IService;
import com.mcmcg.ingestion.service.media.S3UtilityService;
import com.mcmcg.ingestion.util.EventCode;
import com.mcmcg.ingestion.util.IngestionUtils;
import com.mcmcg.ingestion.util.MediaMetadataModelUtil;

/**
 * 
 * @author Jose Aleman
 *
 */
@Service
public class ExtractionService extends BaseIngestionService{
	
	private static final String DATAELEMENT_TYPE_OCR = "OCR";
	private static final Logger LOG = Logger.getLogger(ExtractionService.class);

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected MediaMetadataModel executeService(MediaDocument mediaDocument, Object ...params) throws IngestionServiceException, MediaServiceException {
		return extract(mediaDocument, params);
	}
	
	@Override
	protected String getDocumentStatus() {
		return IngestionUtils.EXTRACTED;
	}
	
	/**
	 * 
	 * @param mediaDocument
	 * @return
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	private MediaMetadataModel extract (MediaDocument mediaDocument, Object ...params) throws IngestionServiceException, MediaServiceException{
		boolean throwException = false;
		String documentStatus = null;

		LOG.info("Extract document: " + mediaDocument);
		
		MediaMetadataModel mediaMetadataModelSaved = getMediaMetadataByDocumentId(mediaDocument); //media-metadata.war
		//Pre-conditions
		//Make sure the file is 100% copied in S3
		evaluateIfDocumentIsPresentInS3(mediaDocument);  //media-utilities.war   
		
		//1. Call Document Field Service
		Account account = retrieveAccountInfo(mediaDocument);  //accounts.war  Datapower ------ DB2
		DocumentFieldsDefinitionModel documentFieldsDefinitionModel = retrieveDocumentFieldsDefinitionModel(mediaDocument, account, mediaMetadataModelSaved); //media-profiles.war
		
		
		//2. Check if document type is stmt
		if (!StringUtils.equalsIgnoreCase(mediaDocument.getDocumentType(), "stmt")){
			checkRequiredOALDDocuments(account, mediaDocument.getDocumentType());  //media-profiles.war
			
		}

		//3. If Document Field Definition is available
		if (documentFieldsDefinitionModel != null){

			//4. Call template mapping service to get template mapping profile
			TemplateMappingProfileModel template = retrieveTemplateMappingProfile(mediaDocument, account, documentFieldsDefinitionModel.getDocumentType(), mediaMetadataModelSaved);
			
			//5. Call data extraction service
			List<DataElement> dataElements = doExtraction(mediaDocument, template, account, documentFieldsDefinitionModel.getDocumentType()); //media-utilities.war   PDF and also uploads files to S3
			
			if (dataElements != null && dataElements.size() > 0){
				
				MediaMetadataModel mediaMetadataModel = MediaMetadataModelUtil.buildMediaMetadataModel(mediaMetadataModelSaved, account, template, dataElements, getDocumentStatus());

				// Will assign to DocStatus = NoTextLayer when OCR type is found in a dataelement
				throwException = checkForOCRFlag(mediaMetadataModel);
				
				documentStatus = mediaMetadataModel.getDocumentStatus();
				
				mediaMetadataModel = postMetadata(mediaDocument, mediaMetadataModel); //media-metadata.war
				
			}else{
				throwException = true;
				
				postMetadata(mediaDocument, MediaMetadataModelUtil.buildMediaMetadataModel(mediaDocument, account, template, null, getDocumentStatus(), null)); //media-metadata.war
				
				documentStatus = getDocumentStatus();
			}

			//Check if needs to throw an Exception cause discarded, templateNotFound, noTextLayer
			if (throwException){
				String message = String.format("Failed while extracting documentId [%d] -> [%s] ", mediaDocument.getDocumentId(), documentStatus); 
				throw new IngestionServiceException(message);
			}
			
			LOG.info("Metadata extracted: " + mediaMetadataModelSaved);

		}
		
		return mediaMetadataModelSaved;
	}

	/**
	 * @param dataElements
	 * @param mediaMetadataModelSaved
	 */
	private boolean checkForOCRFlag(MediaMetadataModel mediaMetadataModelSaved) {
		List<DataElement> dataElements = mediaMetadataModelSaved.getDataElements();
		
		int total = dataElements != null ? dataElements.size() : 0 ;
		for (DataElement dataElement : dataElements){
			if (StringUtils.equalsIgnoreCase(dataElement.getType(), DATAELEMENT_TYPE_OCR)){
				total--;
			}
		}

		boolean isReferenceAreaIncluded = (dataElements != null) ? StringUtils.equalsIgnoreCase(dataElements.get(0).getFieldDefinition().getFieldName().trim(), "referenceArea") : false;
		
		if (total <= 0 && isReferenceAreaIncluded){
			mediaMetadataModelSaved.setDocumentStatus(IngestionUtils.NO_TEXT_LAYER);
		}
		
		return (total <= 0 && isReferenceAreaIncluded);
	}
	
	/**
	 * @param mediaDocument
	 * @param account
	 * @param affinities
	 * @throws IngestionServiceException
	 */
	private List<DataElement> doExtraction(MediaDocument mediaDocument, TemplateMappingProfileModel template, 
										   Account account, DocumentType documentType)
			throws IngestionServiceException, MediaServiceException {
		//"/document-extractions?location=%s&bucket=%s"
		String resource = String.format(ExtractionUtilityService.PUT_EXTRACT_METADATA, mediaDocument.getFilename(), mediaDocument.getBucket()) ;
		Response<List<DataElement>> response = null;
		List<DataElement> returnList = new ArrayList<DataElement>();

		if (template != null){
			LOG.debug(IngestionUtils.getJsonObject(template));
			
			response = utilityService.execute( resource, IService.PUT, template);
			
			if (response.getError().getCode() == EventCode.REQUEST_SUCCESS.getCode()){
				returnList = response.getData();
				LOG.debug(String.format("List<DataElement> Info: %s for MediaDocument %s", returnList, mediaDocument));
			}else{
				postMetadata(mediaDocument, MediaMetadataModelUtil.buildMediaMetadataModel(mediaDocument, account, template, null, getDocumentStatus(), documentType));
				throw new MediaServiceException(response.getError().getMessage());
			}
		}
		
		return  returnList;
	}

	/**
	 * @param mediaDocument
	 * @throws MediaServiceException
	 */
	private void evaluateIfDocumentIsPresentInS3(MediaDocument mediaDocument)
			throws MediaServiceException, IngestionServiceException {
		int attemps = 1;
		boolean found = false;
		String resource = String.format(S3UtilityService.GET_FILE_BY_KEY, mediaDocument.getBucket()) + mediaDocument.getFilename();
		Response<Object> response = null;
		while (attemps++ <= 3 && !found){
			response = s3UtilityService.execute(resource, IService.GET);
			if (response.getError().getCode() == EventCode.SERVER_ERROR.getCode() || 
				response.getError().getCode() == EventCode.SERVICE_ERROR.getCode()){
				try{
					Thread.sleep(2000);
					LOG.debug("Attemp " + attemps + " to get file " +  mediaDocument.getFilename() );
				}
				catch(InterruptedException e){
					LOG.warn(e.getMessage(), e);
				}
			}else{
				found = (Boolean)response.getData();
			}
		}
		
		if (!found){
			String message = "Document was not found in S3 " + mediaDocument.getFilename() + ": " + response.getError().getMessage();
			LOG.error(message);
			throw new IngestionServiceException(message);
		}
	}
}
