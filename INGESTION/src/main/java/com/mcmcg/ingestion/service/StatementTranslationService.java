package com.mcmcg.ingestion.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mcmcg.ingestion.domain.Account;
import com.mcmcg.ingestion.domain.DataElement;
import com.mcmcg.ingestion.domain.MediaDocument;
import com.mcmcg.ingestion.domain.MediaMetadataModel;
import com.mcmcg.ingestion.domain.Response;
import com.mcmcg.ingestion.domain.StatementTranslation;
import com.mcmcg.ingestion.domain.StatementTranslation.TranslateDocument;
import com.mcmcg.ingestion.domain.UpdateDocManagerRequest;
import com.mcmcg.ingestion.exception.IngestionServiceException;
import com.mcmcg.ingestion.exception.MediaServiceException;
import com.mcmcg.ingestion.service.media.AccountService;
import com.mcmcg.ingestion.service.media.IService;
import com.mcmcg.ingestion.service.media.MediaMetadataModelService;
import com.mcmcg.ingestion.service.media.StatementTranslationAccountService;
import com.mcmcg.ingestion.service.media.StatementTranslationUtilityService;
import com.mcmcg.ingestion.util.DocumentTypes;
import com.mcmcg.ingestion.util.EventCode;
import com.mcmcg.ingestion.util.IngestionUtils;
import com.mcmcg.ingestion.util.StatementTranslationUtil;

/**
 * 
 * @author wporras
 *
 */
@Service
public class StatementTranslationService extends BaseIngestionService {

	private static final Logger LOG = Logger.getLogger(StatementTranslationService.class);

	@SuppressWarnings("unchecked")
	@Override
	protected MediaMetadataModel executeService(MediaDocument mediaDocument, Object... params)
			throws IngestionServiceException, MediaServiceException {
		return translate(mediaDocument);
	}

	@Override
	protected String getDocumentStatus() {

		return IngestionUtils.TRANSLATED;
	}

	/**
	 * Run existing document type translation logic to rename/reclassify the
	 * statement documents to Chrgoff, Lastpmt, Lastbillstmt and Lastactivity
	 * 
	 * @param mediaDocument
	 * @return MediaMetadataModel
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	private MediaMetadataModel translate(MediaDocument mediaDocument)
			throws IngestionServiceException, MediaServiceException {

		LOG.info("Translate Statement: " + mediaDocument);
		MediaMetadataModel metadata = null;

		// 0. Only for stmt
		if (!StringUtils.equalsIgnoreCase(mediaDocument.getDocumentType(), DocumentTypes.STATEMENT.getType())) {
			return null;
		}

		// 1. Call account service to get account info with the statement info
		Account account = retrieveAccountInfoWithStatement(mediaDocument);

		// 2. Get Media Meta data to get extracted data
		MediaMetadataModel currentMetadata = getMediaMetadataModelByDocumentId(mediaDocument);

		// If extracted balance != null
		DataElement extractedBalance = getExtractedBalance(currentMetadata);
		if (extractedBalance != null && extractedBalance.getValidated()) {

			// 4. Get meta data for previous/following month
			String adjacentMetadataType = "";
			MediaMetadataModel adjacentMetadata = getAdjacentMediaMetadataModel(mediaDocument, adjacentMetadataType);

			// 5. Translate document type
			List<TranslateDocument> translateDocumentList = doStatementTranslation(mediaDocument, account,
					currentMetadata, adjacentMetadata, adjacentMetadataType);

			// If statement type <> "stmt" or null
			if (translateDocumentList == null || translateDocumentList.isEmpty()) {
				return null;
			}

			// else (save changes)
			for (TranslateDocument translateDocument : translateDocumentList) {
				if (!translateDocument.getTranslatedType().equalsIgnoreCase(DocumentTypes.STATEMENT.getType())) {

					// 6. Save all changes (couchbase, DB2 and S3)
					if (translateDocument.getType().equalsIgnoreCase("current"))
						metadata = saveAllChanges(currentMetadata, translateDocument);
					else
						metadata = saveAllChanges(adjacentMetadata, translateDocument);
				}
			}
		}

		return metadata;

	}

	private MediaMetadataModel saveAllChanges(MediaMetadataModel mediaMetadata, TranslateDocument translateDocument)
			throws MediaServiceException, IngestionServiceException {

		MediaMetadataModel metadata;

		// a. Call media meta data service to update the document
		metadata = postMetadata(mediaMetadata, translateDocument);

		// b. Update Document Image in DB2 with translated document type
		updateDocumentType(translateDocument.getTranslatedType(), mediaMetadata);

		LOG.info(String.format("Document ID (%s) translated to (%s) ", mediaMetadata.getDocumentId(),
				translateDocument.getTranslatedType()));
		return metadata;
	}

	/**
	 * Get the OALD Balance of the extracted data element
	 * 
	 * @param mediaDocument
	 * @return
	 */
	private DataElement getExtractedBalance(MediaMetadataModel MediaMetadataModel) {

		DataElement extractedBalance = null;

		if (MediaMetadataModel != null && MediaMetadataModel.getDataElements() != null
				&& !MediaMetadataModel.getDataElements().isEmpty()) {
			List<DataElement> dataElements = MediaMetadataModel.getDataElements();
			for (DataElement dataElement : dataElements) {
				if (dataElement.getFieldDefinition().getFieldName()
						.equals(StatementTranslationUtil.FIELD_STATEMENT_BALANCE)) {
					extractedBalance = dataElement;
				}
			}
		}
		return extractedBalance;
	}

	/**
	 * Get Account info with the Statement info populated
	 * 
	 * @param mediaDocument
	 * @return Account
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	private Account retrieveAccountInfoWithStatement(MediaDocument mediaDocument)
			throws IngestionServiceException, MediaServiceException {

		String resource = AccountService.GET_ACCOUNT_BY_NUMBER_WITH_STATEMENT.replaceAll("accountNumber",
				mediaDocument.getAccountNumber().toString());
		Response<Account> response = accountService.execute(resource, IService.GET);

		if (response.getData() == null) {
			String message = "Account was not found for mediaDocument " + mediaDocument;
			LOG.error(message);
			throw new IngestionServiceException(message);
		}

		LOG.debug(String.format("Account Info: %s for MediaDocument %s", response.getData(), mediaDocument));
		return response.getData();
	}

	/**
	 * Get Document MediaMetadataModel by Id
	 * 
	 * @param mediaDocument
	 * @return MediaMetadataModel
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	private MediaMetadataModel getMediaMetadataModelByDocumentId(MediaDocument mediaDocument)
			throws IngestionServiceException, MediaServiceException {

		String resource = MediaMetadataModelService.PUT_OR_GET_MEDIAMETADATA + mediaDocument.getDocumentId();
		Response<MediaMetadataModel> response = mediaMetadataService.execute(resource, IService.GET);

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			LOG.debug(String.format("Media Metadata for the document %s: %s ", mediaDocument.getDocumentId(),
					mapper.writeValueAsString(response.getData())));
		} catch (JsonProcessingException e) {
			// nothing
		}
		return response.getData();
	}

	/**
	 * Get the Media Meta data for for previous/following month
	 * 
	 * @param mediaDocument
	 * @return MediaMetadataModel
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	private MediaMetadataModel getAdjacentMediaMetadataModel(MediaDocument mediaDocument, String adjacentMetadataType)
			throws IngestionServiceException, MediaServiceException {

		adjacentMetadataType = "prior";
		String resource = MediaMetadataModelService.GET_BY_ACCOUNTNUMBER_DOCUMENTTYPE_DOCUMENTDATE;
		Response<MediaMetadataModel> response = null;

		// Previous month
		String previousDate = IngestionUtils.addMonths(mediaDocument.getDocumentDate(), -1);
		resource = String.format(resource, mediaDocument.getAccountNumber(), DocumentTypes.STATEMENT.getType(),
				previousDate);
		response = mediaMetadataService.execute(resource, IService.GET);

		// Following month
		if (response != null && response.getData() == null
				&& response.getError().getCode() == EventCode.REQUEST_SUCCESS.getCode()) {

			String followingDate = IngestionUtils.addMonths(mediaDocument.getDocumentDate(), 1);
			resource = String.format(resource, mediaDocument.getAccountNumber(), DocumentTypes.STATEMENT.getType(),
					followingDate);
			response = mediaMetadataService.execute(resource, IService.GET);
			adjacentMetadataType = "following";
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			LOG.debug(String.format("Adjacent Media Metadata for the document %s: %s ", mediaDocument.getDocumentId(),
					mapper.writeValueAsString(response.getData())));
		} catch (JsonProcessingException e) {
			// nothing
		}
		return response.getData();
	}

	/**
	 * Call Utility Statement translation service to get new statement type
	 * 
	 * @param mediaDocument
	 * @param account
	 * @param currentMetadata
	 * @param AdjacentMetadata
	 * @return
	 * @throws MediaServiceException
	 */
	private List<TranslateDocument> doStatementTranslation(MediaDocument mediaDocument, Account account,
			MediaMetadataModel currentMetadata, MediaMetadataModel adjacentMetadata, String adjacentMetadataType)
			throws MediaServiceException {

		List<TranslateDocument> translateDocumentList = null;
		String resource = StatementTranslationUtilityService.PUT_STATEMENT_TRANSLATION;

		StatementTranslation statementTranslation = StatementTranslationUtil.buildStatementTranslation(mediaDocument,
				account, currentMetadata, adjacentMetadata, adjacentMetadataType);

		Response<StatementTranslation> response = statementTranslationUtilityService.execute(resource, IService.PUT,
				statementTranslation);

		if (response.getError().getCode() == EventCode.REQUEST_SUCCESS.getCode()) {
			translateDocumentList = response.getData().getDocumentList();
			LOG.debug(String.format("Estatement translated to: %s for MediaDocument %s", translateDocumentList,
					mediaDocument));
		} else {
			throw new MediaServiceException(response.getError().getMessage());
		}

		return translateDocumentList;
	}

	/**
	 * Call media meta data service to update the document meta data.
	 * 
	 * @param mediaMetadataModel
	 * @param translateDocument
	 * @return MediaMetadataModel
	 * @throws MediaServiceException
	 * @throws IngestionServiceException
	 */
	private MediaMetadataModel postMetadata(MediaMetadataModel mediaMetadataModel, TranslateDocument translateDocument)
			throws MediaServiceException, IngestionServiceException {

		String resource = MediaMetadataModelService.PUT_STATEMENT_TRANSLATION.replaceAll("documentId",
				String.valueOf(mediaMetadataModel.getDocumentId()));

		mediaMetadataModel.getDocument().setDocumentType(translateDocument.getTranslatedType());
		mediaMetadataModel.getDocument().setTranslatedDocumentType(translateDocument.getTranslatedType());

		Response<MediaMetadataModel> response = mediaMetadataService.execute(resource, IService.PUT,
				mediaMetadataModel);

		if (response.getData() == null) {
			String message = "MediaMetadataModel could not be saved: " + mediaMetadataModel;
			LOG.error(message);
			throw new IngestionServiceException(message);
		}

		return response.getData();
	}

	/**
	 * 
	 * @param mediaDocument
	 * @param translatedDocumentType
	 * @return Boolean
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	private Boolean updateDocumentType(String translatedDocumentType, MediaMetadataModel mediaMetadata)
			throws IngestionServiceException, MediaServiceException {

		String resource = String.format(StatementTranslationAccountService.PUT_TRANSLARTED_DOC_TYPE_BY_ACCOUNTNUMBER,
				mediaMetadata.getAccountNumber(), mediaMetadata.getDocumentId());

		Response<Boolean> response = statementTranslationAccountService.execute(resource, IService.PUT,
				new UpdateDocManagerRequest(mediaMetadata.getDocumentId().toString(), translatedDocumentType));

		if (response.getData() == null ) {
			String message = "Document Id " + mediaMetadata.getDocumentId() + " couldn't be updated in the DB2 Table";
			LOG.error(message);
			LOG.error(response.getError().getMessage());
			throw new IngestionServiceException(message);
		}

		LOG.debug(String.format("Updated Document Type %s for Document type %s", response.getData().toString(),
				mediaMetadata.getDocumentId()));

		return response.getData();
	}
}
