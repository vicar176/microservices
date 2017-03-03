package com.mcmcg.dia.media.metadata.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mcmcg.dia.media.metadata.annotation.Diagnostics;
import com.mcmcg.dia.media.metadata.aop.DiagnosticsAspect;
import com.mcmcg.dia.media.metadata.dao.MediaMetadataDAO;
import com.mcmcg.dia.media.metadata.dao.N1qlQueryDAO;
import com.mcmcg.dia.media.metadata.exception.PersistenceException;
import com.mcmcg.dia.media.metadata.exception.ServiceException;
import com.mcmcg.dia.media.metadata.model.MediaMetadataModel.AutoValidation;
import com.mcmcg.dia.media.metadata.model.MediaMetadataModel.DataElement;
import com.mcmcg.dia.media.metadata.model.MediaMetadataModel.Document;
import com.mcmcg.dia.media.metadata.model.MediaMetadataModel.FieldDefinition;
import com.mcmcg.dia.media.metadata.model.MediaMetadataModel.ManualValidation;
import com.mcmcg.dia.media.metadata.model.domain.MediaMetadata;
import com.mcmcg.dia.media.metadata.model.domain.PagedResponse;
import com.mcmcg.dia.media.metadata.model.entity.MediaMetadataEntity;
import com.mcmcg.dia.media.metadata.model.query.CustomQueryBuilder;
import com.mcmcg.dia.media.metadata.model.query.ExtractionExceptionDetailDataByPortfolio;
import com.mcmcg.dia.media.metadata.model.query.ExtractionExceptionDetailDataByPortfolio.TemplateFound;
import com.mcmcg.dia.media.metadata.model.query.ExtractionExceptionDetailDataByPortfolio.TemplateNotFound;
import com.mcmcg.dia.media.metadata.model.query.ExtractionExceptionMasterDataByPortfolio;
import com.mcmcg.dia.media.metadata.util.MediaMetadataUtil;
import com.mcmcg.dia.media.metadata.util.MetadataConvertUtil;

/**
 * 
 * @author Victor Arias
 *
 */
@Service
public class MediaMetadataService extends BaseService {

	private static final Logger LOG = Logger.getLogger(MediaMetadataService.class);

	@Autowired
	@Qualifier("mediaMetadataDAO")
	MediaMetadataDAO mediaMetadataDAO;

	@Value("${couchbase.cluster.bucket}")
	private String bucketName;

	@Autowired
	DiagnosticsAspect diagnosticsAspect;
	
	@Autowired
	private Bucket couchbaseBucket;
	
	
	public enum DataTypes {
		number, alphanumeric, date
	};

	public enum DocumentStatus {
		extracted, validated, translated, manualValidated, tagged, received, accountVerified, 
		reprocess, discarded, retry, templateNotFound, noTextLayer, ignore
	};

	/**
	 * 
	 * @param mediaMetadata
	 * @param documentId
	 * @return
	 * @throws ServiceException
	 */

	public MediaMetadata save(MediaMetadata mediaMetadata) throws ServiceException {
		return save(mediaMetadata, null);
	}

	public MediaMetadata save(MediaMetadata mediaMetadata, String id) throws ServiceException {

		LOG.info("Save MediaMetadata start");
		try {
			if (mediaMetadata != null) {

				if (StringUtils.isBlank(mediaMetadata.getDocumentDate())) {
					String message = "The documentDate should not be null or empty";
					ServiceException se = new ServiceException(message);
					LOG.error(message, se);
					throw se;
				}
				if (!MediaMetadataUtil.isValidFormat(mediaMetadata.getDocumentDate())) {
					String message = "The documentDate  is using a bad date format, it should be " + MediaMetadataUtil.DATE_FORMAT_SHORT;
					ServiceException se = new ServiceException(message);
					LOG.error(message, se);
					throw se;
				}

				// Validate Document
				validateDocument(mediaMetadata.getDocument());

				if (!validateDocumentStatus(mediaMetadata.getAutoValidation(), mediaMetadata.getDocumentStatus())) {
					String message = "The DocumentStatus does not match the autovalidated flag: true -> validated, false -> extracted";
					ServiceException se = new ServiceException(message);
					LOG.error(message, se);
					throw se;
				}
				if (mediaMetadata.getDocumentId() == null || mediaMetadata.getDocumentId() == 0) {
					String message = "Invalid MediaMetadata, the document Id is required";
					ServiceException se = new ServiceException(message);
					LOG.error(message, se);
					throw se;
				}
				if (StringUtils.isBlank(mediaMetadata.getDocumentDate())) {
					String message = "The documentDate should not be null or empty";
					ServiceException se = new ServiceException(message);
					LOG.error(message, se);
					throw se;
				}
				if (!MediaMetadataUtil.isValidDate(mediaMetadata.getDocumentDate()) || 
						!MediaMetadataUtil.isValidDate(mediaMetadata.getDocument().getDocumentDate())) {
					String message = "The documentDate  is using a bad date format, it should be " + MediaMetadataUtil.DATE_FORMAT_SHORT;
					ServiceException se = new ServiceException(message);
					LOG.error(message, se);
					throw se;
				}

				// Validate Document
				validateDocument(mediaMetadata.getDocument());

				if (!validateDocumentStatus(mediaMetadata.getAutoValidation(), mediaMetadata.getDocumentStatus())) {
					String message = "The documentStatus does not match the autovalidated flag: true -> validated, false -> extracted";
					ServiceException se = new ServiceException(message);
					LOG.error(message, se);
					throw se;
				}

				MediaMetadataEntity existentEntity = findDocByIdTest(mediaMetadata.getDocumentId());//mediaMetadataDAO.findByDocumentId(mediaMetadata.getDocumentId());
				
				if (StringUtils.isBlank(id)) {
					if (existentEntity == null) {
						mediaMetadata.setVersion(1L);
					} else {
						String message = String.format("A MediaMetadata already exists with documentId = %s",
								mediaMetadata.getDocumentId());
						ServiceException se = new ServiceException(message);
						LOG.error(message, se);
						throw se;
					}
				} else {
					if (!StringUtils.equals(id, mediaMetadata.getId())) {
						String message = "The id does not match with the id in the JSON object";
						ServiceException se = new ServiceException(message);
						LOG.error(message, se);
						throw se;
					}
					MediaMetadataEntity current = mediaMetadataDAO.findOne(id);
					if (current != null && existentEntity != null) {
						if (StringUtils.equals(current.getId(), existentEntity.getId())
								&& current.getDocumentId().equals(existentEntity.getDocumentId())) {
							mediaMetadata.setCreateDate(current.getCreateDate());
							mediaMetadata.setVersion(current.getVersion() + 1);
						} else {
							String message = String.format(
									"The MediaMetadata with ID = %s and DocumentId = %s "
											+ "does not match the MediaMetadata object in database with ID = %s and DocumentId = %s",
									current.getId(), current.getDocumentId(), existentEntity.getId(),
									existentEntity.getDocumentId());
							ServiceException se = new ServiceException(message);
							LOG.error(message, se);
							throw se;
						}
					} else {
						String message = String.format("No MediaMetadata exists with the id %s", id);
						ServiceException se = new ServiceException(message);
						LOG.error(message, se);
						throw se;
					}
				}

				saveInternal(mediaMetadata);

			} else {
				String message = "Metadata not valid";
				ServiceException se = new ServiceException(message);
				LOG.error(message, se);
				throw se;
			}
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException("Failed to run operation save for MediaMetadata " + e.getMessage(), e);
		}
		LOG.info("Save MediaMetadata end");
		return mediaMetadata;
	}

	/**
	 * 
	 * @param documentId
	 * @return
	 * @throws ServiceException
	 */
	@Diagnostics(area = "Couchbase-Reads")
	public MediaMetadata findByDocumentId(Long documentId) throws ServiceException {
		LOG.info("findByDocumentId MediaMetadata start");
		MediaMetadata metadata = null;

		try {
			//MediaMetadataEntity entity = mediaMetadataDAO.findByDocumentId(documentId);
			MediaMetadataEntity entity = findDocByIdTest(documentId);
			
			metadata = MetadataConvertUtil.convertMediaMetadataEntityToDomain(entity);
			LOG.debug("documentId = " + documentId + ", Metadata = " + metadata);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error(message, e);
			throw new ServiceException(message, e);
		}

		LOG.info("findByDocumentId MediaMetadata end");
		return metadata;
	}

	private MediaMetadataEntity findDocByIdTest(Long documentId) throws PersistenceException {
		
		MediaMetadataEntity entity = null;//mediaMetadataDAO.findByDocumentId(documentId);
		JsonDocument jsonDocument = couchbaseBucket.get(buildId(documentId));
		
		if(jsonDocument != null) {
			JsonObject jsonObject = jsonDocument.content();
			entity = MetadataConvertUtil.convertJsonObjectToMediaMetadataEntity(jsonObject);
			encryptComponent.decrypt(entity);
			entity.setId(jsonDocument.id());
		}
		
		return entity;
	}

	/**
	 * 
	 * @param accountNumber
	 * @param originalDocumentType
	 * @param documentDate
	 * @return
	 * @throws ServiceException
	 */
	public MediaMetadata findByOriAcctNumDocTypeAndDocDate(Long accountNumber, String originalDocumentType,
			String documentDate) throws ServiceException {
		LOG.info("findByOriAcctNumDocTypeAndDocDate MediaMetadata start");
		MediaMetadata metadata = null;
		try {
			/*
			if(accountNumber == null || StringUtils.isBlank(originalDocumentType) || StringUtils.isBlank(documentDate)){
				String message = "The documentDate";
				ServiceException se = new ServiceException(message);
				LOG.error(message, se);
				throw se;
			}
			*/
			if (MediaMetadataUtil.isValidFormat(documentDate)) {
				MediaMetadataEntity entity = mediaMetadataDAO
						.findByAccountNumberAndOriginalDocumentTypeCodeAndDocumentDate(accountNumber,
								originalDocumentType, documentDate);
				metadata = MetadataConvertUtil.convertMediaMetadataEntityToDomain(entity);
			} else {
				String message = "The documentDate " + documentDate + " is using a bad date format, it should be "
						+ MediaMetadataUtil.DATE_FORMAT_SHORT;
				ServiceException se = new ServiceException(message);
				LOG.error(message, se);
				throw se;
			}
		} catch (ServiceException se){ 
			LOG.error(se.getMessage(), se);
			throw se;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		}

		LOG.info("findByOriAcctNumDocTypeAndDocDate MediaMetadata end");
		return metadata;
	}

	/**
	 * 
	 * @param documentId
	 * @param mediaMetadata
	 * @return
	 * @throws ServiceException
	 */
	public MediaMetadata updateAutoValidation(Long documentId, MediaMetadata mediaMetadata) throws ServiceException {
		MediaMetadata current = null;
		if (documentId != null && mediaMetadata != null) {
			AutoValidation autoValidation = mediaMetadata.getAutoValidation();
			if (autoValidation != null) {
				if (autoValidation.getAutoValidated() == null) {
					String message = "Autovalidated must not be null or empty, it should be True or False";
					ServiceException se = new ServiceException(message);
					LOG.error(message, se);
					throw se;
				} else {
					Date curDate = new Date();
					current = findByDocumentId(documentId);
					if (current != null) {
						current.setAutoValidation(autoValidation);
						current.setDocumentStatus(DocumentStatus.validated.toString());
						current.setUpdatedBy(mediaMetadata.getUpdatedBy());
						current.setUpdateDate(MediaMetadataUtil.formatDate(curDate));
						save(current, current.getId());
					} else {
						String message = String.format("No MediaMetadata exists with documentId = %s", documentId);
						ServiceException se = new ServiceException(message);
						LOG.error(message, se);
						throw se;
					}
				}
			} else {
				String message = "Autovalidation must not be null";
				ServiceException se = new ServiceException(message);
				LOG.error(message, se);
				throw se;
			}
		} else {
			String message = "MediaMetadata not valid";
			ServiceException se = new ServiceException(message);
			LOG.error(message, se);
			throw se;
		}

		return current;
	}

	/**
	 * 
	 * @param documentId
	 * @param mediaMetadata
	 * @return
	 * @throws ServiceException
	 */
	public MediaMetadata updateManualValidation(Long documentId, MediaMetadata mediaMetadata) throws ServiceException {
		MediaMetadata current = null;
		if (documentId != null && mediaMetadata != null) {
			ManualValidation manualValidation = mediaMetadata.getManualValidation();
			if (manualValidation == null) {
				String message = "Manual Validation must not be null";
				ServiceException se = new ServiceException(message);
				LOG.error(message, se);
				throw se;
			}
			Date curDate = new Date();
			current = findByDocumentId(documentId);
			if (current != null) {

				updateDataElementsValue(mediaMetadata, current);
				
				// current.setDataElements(mediaMetadata.getDataElements());
				current.setManualValidation(manualValidation);
				current.setDocumentStatus(DocumentStatus.manualValidated.toString());
				current.setUpdatedBy(mediaMetadata.getUpdatedBy());
				current.setUpdateDate(MediaMetadataUtil.formatDate(curDate));
				save(current, current.getId());
			} else {
				String message = String.format("No MediaMetadata exists with documentId = %s", documentId);
				ServiceException se = new ServiceException(message);
				LOG.error(message, se);
				throw se;
			}

		} else {
			String message = "MediaMetadata not valid";
			ServiceException se = new ServiceException(message);
			LOG.error(message, se);
			throw se;
		}

		return current;
	}

	private void updateDataElementsValue(MediaMetadata metadata, MediaMetadata currentMetadata)
			throws ServiceException {

		if (currentMetadata.getDataElements().size() != metadata.getDataElements().size()) {
			String message = "Data Elements size is not the same from the previously saved";
			ServiceException se = new ServiceException(message);
			LOG.error(message, se);
			throw se;
		}

		for (DataElement currentDataElement : currentMetadata.getDataElements()) {
			for (DataElement dataElement : metadata.getDataElements()) {
				if (StringUtils.equalsIgnoreCase(currentDataElement.getFieldDefinition().getFieldName(),
						dataElement.getFieldDefinition().getFieldName()))
					currentDataElement.setValue(dataElement.getValue());
			}
		}
	}
	
	public boolean deleteMetadata(Long documentId) throws ServiceException {
		
		boolean deleted = false;
		try {
			MediaMetadataEntity entity = mediaMetadataDAO.findByDocumentId(documentId);
			mediaMetadataDAO.delete(entity);
			entity = mediaMetadataDAO.findByDocumentId(documentId);
			if(entity == null) {
				deleted = true;
			}
		} catch(Throwable t) {
			String message = String.format("The metadata with document ID %s was not deleted", documentId);
			ServiceException se = new ServiceException(message, t);
			LOG.error(message, t);
			throw se;
		}
		
		return deleted;
	}

	/**
	 * 
	 * @param documentId
	 * @return
	 * @throws ServiceException
	 */
	public MediaMetadata validateDataElements(Long documentId) throws ServiceException {
		MediaMetadata metadata = null;
		if (documentId != null) {
			metadata = findByDocumentId(documentId);
			if (metadata != null) {
				List<DataElement> dataElements = metadata.getDataElements();
				for (DataElement element : dataElements) {
					element.setValidated(validateDataElement(element));
				}
				metadata.setDataElements(dataElements);
			} else {
				String message = String.format("No MediaMetadata exists with documentId = %s", documentId);
				ServiceException se = new ServiceException(message);
				LOG.error(message, se);
				throw se;
			}
		} else {
			String message = String.format("The document ID %s is not valid", documentId);
			ServiceException se = new ServiceException(message);
			LOG.error(message, se);
			throw se;
		}
		return metadata;
	}

	/**
	 * Update translated document type
	 * 
	 * @param documentId
	 * @param mediaMetadata
	 * @return MediaMetadata
	 * @throws ServiceException
	 * @throws ParseException
	 */
	public MediaMetadata updateStatementTranslation(Long documentId, MediaMetadata mediaMetadata)
			throws ServiceException {

		LOG.info("updateStatementTranslation start");
		MediaMetadata current = null;
		if (documentId != null && mediaMetadata != null) {

			// Find by document id
			current = findByDocumentId(documentId);

			if (current == null) {
				String message = String.format("Media MetaData with documentId %s was not found", documentId);
				ServiceException se = new ServiceException(message);
				LOG.error(message, se);
				throw se;
			}

			current.setDocument(mediaMetadata.getDocument());
			setGeneralRequiredAttributes(mediaMetadata, current, DocumentStatus.translated.toString());

			save(current, current.getId());

		} else {
			String message = "MediaMetadata not valid";
			ServiceException se = new ServiceException(message);
			LOG.error(message, se);
			throw se;
		}

		LOG.info("updateStatementTranslation end");

		return current;
	}

	/**
	 * 
	 * @param page
	 * @param size
	 * @param filter
	 * @param sort
	 * @return
	 */
	public PagedResponse<Object> retrievePortfolioByCriteria(String filter, String sort, boolean showSummary, int page,
			int size) throws PersistenceException, ServiceException {
		PagedResponse<Object> pagedResponse = null;

		if (showSummary) {

			pagedResponse = retrieveSummaryByPortfolio(filter, sort, page, size);

		} else {

			pagedResponse = retrievePortfolioIds(filter);

		}
		return pagedResponse;
	}

	/**
	 * 
	 * @param portfolioNumber
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 * @throws ServiceException
	 */
	public ExtractionExceptionDetailDataByPortfolio retrieveExtractedExceptionDataByPortfolio(Long portfolioNumber,
			String filter, String sort, int page, int size) throws ServiceException, PersistenceException {

		// Setting filter
		filter = String.format("%s|portfolioNumber=%d", filter, portfolioNumber);

		// Setting Templates Found
		PagedResponse<TemplateFound> templateFoundList = buildTemplatesFound(filter, sort, page, size);

		// Setting Templates Not Found
		PagedResponse<TemplateNotFound> templateNotFoundList = buildTemplatesNotFound(filter, sort, page, size);

		return new ExtractionExceptionDetailDataByPortfolio(templateFoundList, templateNotFoundList);
	}

	/**
	 * 
	 * @param portfolioNumber
	 * @param filter
	 * @param sort
	 * @param page
	 * @param size
	 * @return
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	public PagedResponse<TemplateFound> retrieveTemplateFoundForExtractedException(Long portfolioNumber,
			String filter, String sort, int page, int size) throws ServiceException, PersistenceException {

		// Setting filter
		filter = String.format("%s|portfolioNumber=%d", filter, portfolioNumber);

		// Setting Templates Found
		PagedResponse<TemplateFound> templateFoundList = buildTemplatesFound(filter, sort, page, size);
		
		return templateFoundList;
	}
	
	/**
	 * 
	 * @param portfolioNumber
	 * @param filter
	 * @param sort
	 * @param page
	 * @param size
	 * @return
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	public PagedResponse<TemplateNotFound> retrieveTemplateNotFoundForExtractedException(Long portfolioNumber,
			String filter, String sort, int page, int size) throws ServiceException, PersistenceException {

		// Setting filter
		filter = String.format("%s|portfolioNumber=%d", filter, portfolioNumber);

		// Setting Templates Not Found
		PagedResponse<TemplateNotFound> templateNotFoundList = buildTemplatesNotFound(filter, sort, page, size);
		
		return templateNotFoundList;
	}
	
	/**
	 * 
	 * @param templateName
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 * @throws ServiceException
	 */
	public PagedResponse<Object> retrieveFailedDocumentsByPortfolio(Long portfolioNumber, String filter, String sort,
			int page, int size) throws PersistenceException, ServiceException {

		// String filter =
		// String.format("extraction.templateMappingProfile.name=%s",
		// templateName.trim())
		filter = "portfolioNumber=" + portfolioNumber + "|" + filter;
		// Count
		int total = getTotalCount(filter, null);

		List<Object> documents = null;
		if (total > 0){
			// Execute the query
			List<JsonObject> results = filter(N1qlQueryDAO.GET_FAILED_DOCUMENTS_BY_TEMPLATE_NAME, filter, null, sort, page,
					size);

			documents = parseJsonObjectToObject(results);
		}

		return new PagedResponse<Object>(total, CustomQueryBuilder.calculateStartIndex(page, size), size, documents);
	}

	/**
	 * 
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 * @throws ServiceException
	 * @throws PersistenceException
	 * @throws ParseException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public PagedResponse<Object> retrieveMediaMetadata(String filter, String sort, int page, int size)
			throws ServiceException, PersistenceException, ParseException, JsonParseException, JsonMappingException,
			IOException {
		// Count
		int total = getTotalCount(filter, null);

		List<Object> entities = null;

		if (total > 0){
			// Execute the query
			List<JsonObject> results = filter(N1qlQueryDAO.GET_ALL_METADATA, filter, null, sort, page, size);

			entities = parseJsonObjectToEntity(results, MediaMetadataEntity.class);
		}

		return new PagedResponse<Object>(total, CustomQueryBuilder.calculateStartIndex(page, size), size, entities);
	}

	/**
	 * 
	 * @param documentStatus
	 * @return
	 * @throws ServiceException
	 * @throws PersistenceException
	 * @throws ParseException
	 */
	public PagedResponse<Object> retrieveMediaMetadata(String filter)
			throws ServiceException, PersistenceException, ParseException {

		// Count
		int total = getTotalCount(filter, null);
		
		List<Object> documents = null;
		if (total > 0){
			// Execute the query
			List<JsonObject> results = filter(N1qlQueryDAO.GET_FAILED_DOCUMENTS_BY_TEMPLATE_NAME, filter, null, null, 0, 0);

			documents = parseJsonObjectToObject(results);
		}


		return new PagedResponse<Object>(total, 0, total, documents);
	}

	// ***************************************************************************************************
	//
	// PRIVATE METHODS
	//
	// ***************************************************************************************************
	private void saveInternal(MediaMetadata mediaMetadata) throws JsonProcessingException, ParseException {
		if (StringUtils.isBlank(mediaMetadata.getId())) {
			//String id = UUID.randomUUID().toString();
			//mediaMetadata.setId(id);
			mediaMetadata.setId(buildId(mediaMetadata.getDocumentId()));
		}
		long start = System.currentTimeMillis();
		mediaMetadataDAO.save(MetadataConvertUtil.convertMediaMetadataToEntity(mediaMetadata));
		long end = System.currentTimeMillis();
		
		diagnosticsAspect.log("saveInternal", new Object[]{mediaMetadata.getDocumentId()}, "Couchbase-Writes", start, end);
	}
	
	private String buildId(Long documentId) {
		String id = MediaMetadataEntity.class.getSimpleName() + "-" + documentId;
		return id;
	}

	/**
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 * @throws PersistenceException
	 * @throws ServiceException
	 */
	private PagedResponse<Object> retrieveSummaryByPortfolio(String filter, String sort, int page, int size)
			throws PersistenceException, ServiceException {
		// Count
		int total = getTotalCount(filter, "portfolioNumber, seller.name");

		List<Object> returnList = null;
		if (total > 0){
			// Execute the query
			List<JsonObject> results = filter(N1qlQueryDAO.GET_PORTFOLIO_SUMMARY, filter, "portfolioNumber, seller.name",
					sort, page, size);

			// Map results to List<ExtractionExceptionMasterDataByPortfolio>
			if (results != null && !results.isEmpty()) {
				returnList = new ArrayList<Object>();
				for (JsonObject jsonObject : results) {
					if (jsonObject.getLong("documentsFailed") > 0) {
						returnList.add(MetadataConvertUtil.convertJsonObjectToMediaMetadataGroupByPortfolio(jsonObject));
					}
				}
			}
		}

		return new PagedResponse<Object>(total, CustomQueryBuilder.calculateStartIndex(page, size), size, returnList);
	}

	/**
	 * 
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 * @throws PersistenceException
	 * @throws ServiceException
	 */
	private PagedResponse<Object> retrievePortfolioIds(String filter) throws PersistenceException, ServiceException {
		// Count
		int total = getTotalCount(N1qlQueryDAO.GET_COUNT_PORTFOLIOS_FOUND, filter, null);

		List<Object> returnList = null;
		if (total > 0){
			// Execute the query
			List<JsonObject> results = filter(N1qlQueryDAO.GET_PORTFOLIO_IDS, filter, null, null, 0, 0);

			// Map results to List<ExtractionExceptionMasterDataByPortfolio>
			if (results != null && !results.isEmpty()) {
				returnList = new ArrayList<Object>();
				for (JsonObject jsonObject : results) {
					returnList.add(jsonObject.get("portfolioNumber"));
				}
			}
		}

		return new PagedResponse<Object>(total, 1, total, returnList);
	}

	/**
	 * @param filter
	 * @param page
	 * @param size
	 * @throws ServiceException
	 */
	private ExtractionExceptionMasterDataByPortfolio buildExtractionExceptionMasterDataByPortfolio(String filter,
			int page, int size) throws PersistenceException, ServiceException {

		ExtractionExceptionMasterDataByPortfolio master = null;

		List<JsonObject> results = filter(N1qlQueryDAO.GET_PORTFOLIO_SUMMARY, filter, "portfolioNumber, seller.name",
				null, page, size);
		// Map results to List<ExtractionExceptionMasterDataByPortfolio>
		if (results != null && !results.isEmpty()) {
			master = MetadataConvertUtil.convertJsonObjectToMediaMetadataGroupByPortfolio(results.get(0));
		}

		return master;
	}

	/**
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 * @throws ServiceException
	 */
	private PagedResponse<TemplateNotFound> buildTemplatesNotFound(String filter, String sort, int page, int size)
			throws PersistenceException, ServiceException {
		// Setting groupBy
		StringBuilder groupByBuilder = new StringBuilder();
		groupByBuilder.append("originalDocumentType, ").append("originalLenderName, ")
			.append("seller, documentStatus");

		// Get Count
		int total = getTotalCount(N1qlQueryDAO.GET_COUNT_TEMPLATES_NOT_FOUND, filter, groupByBuilder.toString());

		List<TemplateNotFound> templateNotFoundList = null;
		if (total > 0){
			// Execute the query
			List<JsonObject> results = filter(N1qlQueryDAO.GET_METADATA_WITHOUT_TEMPLATE_NOT_EXTRACTED, filter,
					groupByBuilder.toString(), sort, page, size);


			if (results != null && !results.isEmpty()) {
				templateNotFoundList = new ArrayList<TemplateNotFound>();
				for (JsonObject jsonObject : results) {
					TemplateNotFound templateNotFound = MetadataConvertUtil.convertJsonObjectToTemplateNotFound(jsonObject);
					if (templateNotFound.getDocumentsFailed() > 0) {
						templateNotFoundList.add(templateNotFound);
					}
				}
			}
		}

		return new PagedResponse<TemplateNotFound>(total, CustomQueryBuilder.calculateStartIndex(page, size), size,
				templateNotFoundList);
	}

	/**
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 * @throws ServiceException
	 */
	private PagedResponse<TemplateFound> buildTemplatesFound(String filter, String sort, int page, int size)
			throws PersistenceException, ServiceException {
		// Setting groupBy
		StringBuilder groupByBuilder = new StringBuilder();
		groupByBuilder.append("extraction.templateMappingProfile.id,").append("extraction.templateMappingProfile.name,")
				.append("extraction.templateMappingProfile.updatedBy,")
				.append("extraction.templateMappingProfile.updateDate,")
				.append("extraction.templateMappingProfile.version, documentStatus");

		List<TemplateFound> templateFoundList = new LinkedList<TemplateFound>(); 
		
		int total = populateExtractionManagementResponse(filter, groupByBuilder, sort, page, size, templateFoundList);	

		return new PagedResponse<TemplateFound>(total, CustomQueryBuilder.calculateStartIndex(page, size), size, templateFoundList);
	}

	/**
	 * 
	 * @param filter
	 * @param groupByBuilder
	 * @param sort
	 * @param page
	 * @param size
	 * @param templateFoundSet
	 * @return
	 * @throws PersistenceException
	 * @throws ServiceException
	 */
	private int populateExtractionManagementResponse(String filter, StringBuilder groupByBuilder, String sort,
			int page, int size, List<TemplateFound> templateFoundList) 
												throws PersistenceException, ServiceException {
		// Get Count
		int total = getTotalCount(N1qlQueryDAO.GET_COUNT_TEMPLATES_FOUND, filter, groupByBuilder.toString());

		if (total > 0){
			// Execute the query
			Map<String, Integer> indexMap = new HashMap<String, Integer>();
			
			if (StringUtils.isNotEmpty(sort)){
				sort += ",-documentStatus";
			}else{
				sort = "-documentStatus";
			}
			List<JsonObject> results = filter(N1qlQueryDAO.GET_METADATA_WITH_TEMPLATE_NOT_EXTRACTED, filter,
											  groupByBuilder.toString(), sort, page, size);
			int index = 0;
			if (results != null && !results.isEmpty()) {
				for (JsonObject jsonObject : results) {
					
					TemplateFound templateFound = MetadataConvertUtil.convertJsonObjectToTemplateFound(jsonObject);
					
					if (!indexMap.containsKey(templateFound.getTemplateId())){
						indexMap.put(templateFound.getTemplateId(), index);
						templateFoundList.add(templateFound);
					}else{
						int position = indexMap.get(templateFound.getTemplateId());
						TemplateFound savedTemplateFound = templateFoundList.get(position);
						savedTemplateFound.setDocumentsFailed(savedTemplateFound.getDocumentsFailed() + templateFound.getDocumentsFailed());
					}
					++index;
				}

			}
			
		}

		return total;

	}

	/**
	 * 
	 * @param dataElement
	 * @return
	 */
	private boolean validateDataElement(DataElement dataElement) {
		boolean isValid = false;
		// validate that the value is of the type of dataType (numeric,
		// alphanumeric, date) and if it is required
		FieldDefinition fieldDefinition = dataElement.getFieldDefinition();
		String value = dataElement.getValue();
		String dataType = fieldDefinition.getFieldType().toLowerCase();
		LOG.debug(dataElement);
		if (!StringUtils.isBlank(value)) {
			if (StringUtils.equals(dataType, DataTypes.number.toString())) {
				isValid = MediaMetadataUtil.isNumeric(value);
				LOG.debug("validateDataElement: dataType=number " + "isValid=" + isValid);
			} else if (StringUtils.equals(dataType, DataTypes.alphanumeric.toString())) {
				isValid = !MediaMetadataUtil.isNumeric(value);
				LOG.debug("validateDataElement: dataType=alphanumeric " + "isValid=" + isValid);
			} else if (StringUtils.equals(dataType, DataTypes.date.toString())) {
				isValid = MediaMetadataUtil.isValidFormat(value);
				LOG.debug("validateDataElement: dataType=date " + "isValid=" + isValid);
			}
		}
		if (fieldDefinition.isFieldRequired() && StringUtils.isBlank(value)) {
			isValid = false;
		}
		return isValid;
	}

	private boolean validateDocumentStatus(AutoValidation autoValidation, String status) {
		// validates the autovalidation by the status
		// status extracted -> autovalidated false
		// status validated -> autovalidated true

		boolean validated = false;
		if (autoValidation == null && !StringUtils.isBlank(status)) {
			validated = true;
		}
		if (autoValidation != null && !StringUtils.isBlank(status)) {
			if (StringUtils.equalsIgnoreCase(DocumentStatus.extracted.toString(), status)
					&& !autoValidation.getAutoValidated()) {
				validated = true;
			}
			if (StringUtils.equalsIgnoreCase(DocumentStatus.validated.toString(), status)
					&& autoValidation.getAutoValidated()) {
				validated = true;
			}
			if (StringUtils.equalsIgnoreCase(DocumentStatus.translated.toString(), status)
					&& autoValidation.getAutoValidated()) {
				validated = true;
			}
			if (StringUtils.equalsIgnoreCase(DocumentStatus.manualValidated.toString(), status)
					&& autoValidation.getAutoValidated()) {
				validated = true;
			}
			if (StringUtils.equalsIgnoreCase(DocumentStatus.tagged.toString(), status)
					&& autoValidation.getAutoValidated()) {
				validated = true;
			}
			if (StringUtils.equalsIgnoreCase(DocumentStatus.received.toString(), status)
					&& autoValidation.getAutoValidated()) {
				validated = true;
			}
			if (StringUtils.equalsIgnoreCase(DocumentStatus.accountVerified.toString(), status)
					&& autoValidation.getAutoValidated()) {
				validated = true;
			}
			if (StringUtils.equalsIgnoreCase(DocumentStatus.reprocess.toString(), status)
					&& autoValidation.getAutoValidated()) {
				validated = true;
			}
			if (StringUtils.equalsIgnoreCase(DocumentStatus.discarded.toString(), status)
					&& autoValidation.getAutoValidated()) {
				validated = true;
			}
			if (StringUtils.equalsIgnoreCase(DocumentStatus.noTextLayer.toString(), status)
					&& autoValidation.getAutoValidated()) {
				validated = true;
			}
			if (StringUtils.equalsIgnoreCase(DocumentStatus.ignore.toString(), status)
					&& autoValidation.getAutoValidated()) {
				validated = true;
			}
		}
		return validated;
	}

	/**
	 * Validates the document object is well populated
	 * 
	 * @param document
	 * @throws ServiceException
	 */
	private void validateDocument(Document document) throws ServiceException {

		if (document == null) {
			String message = "Document object is required";
			LOG.error(message);
			throw new ServiceException(message);
		}

		if (StringUtils.isBlank(document.getDocumentDate()) || StringUtils.isBlank(document.getDocumentNameString())
				|| StringUtils.isBlank(document.getDocumentType())
				|| StringUtils.isBlank(document.getOriginalDocumentType()) || document.getDocumentName() == null) {

			String message = "The Document object is not valid";
			LOG.error(message);
			throw new ServiceException(message);
		}
		
		/*if(!MediaMetadataUtil.isValidDate(document.getDocumentDate())){
			String message = String.format("The Document Date is not valid, the format should be %s", MediaMetadataUtil.DATE_FORMAT_SHORT);
			LOG.error(message);
			throw new ServiceException(message);
		}*/

		// Statement translation
		if (StringUtils.isNotBlank(document.getTranslatedDocumentType())) {
			if (!StringUtils.equals(document.getTranslatedDocumentType(), document.getDocumentType())) {

				String message = "If Translated Document Type is populated, it have to be equal to Document Type";
				LOG.error(message);
				throw new ServiceException(message);
			}
		}
	}

	/**
	 * Set General Attributes required to be updated every time the Media
	 * Metadata is updated update
	 * 
	 * @param metadata
	 * @param metadataNew
	 * @param DocumentStatus
	 * @return MediaMetadata
	 * @throws ServiceException
	 */
	private MediaMetadata setGeneralRequiredAttributes(MediaMetadata metadata, MediaMetadata metadataNew,
			String documentStatus) throws ServiceException {

		if (StringUtils.isBlank(metadata.getUpdatedBy())) {
			String message = "UpdateBy is required";
			LOG.error(message);
			throw new ServiceException(message);
		}
		metadataNew.setUpdatedBy(metadata.getUpdatedBy());

		if (StringUtils.isBlank(documentStatus)) {
			String message = "Document Status is required";
			LOG.error(message);
			throw new ServiceException(message);
		}
		metadataNew.setDocumentStatus(documentStatus);

		metadataNew.setUpdateDate(MediaMetadataUtil.formatDate(new Date()));

		return metadataNew;
	}

}
