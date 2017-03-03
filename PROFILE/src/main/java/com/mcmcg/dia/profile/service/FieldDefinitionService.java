package com.mcmcg.dia.profile.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.document.json.JsonObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mcmcg.dia.profile.dao.FieldDefinitionDAO;
import com.mcmcg.dia.profile.dao.FieldDefinitionHistoryDAO;
import com.mcmcg.dia.profile.dao.N1qlQueryDAO;
import com.mcmcg.dia.profile.exception.PersistenceException;
import com.mcmcg.dia.profile.exception.ServiceException;
import com.mcmcg.dia.profile.model.DocumentFieldsDefinitionModel.DocumentFieldDefinition;
import com.mcmcg.dia.profile.model.domain.DocumentFieldsDefinition;
import com.mcmcg.dia.profile.model.domain.FieldDefinition;
import com.mcmcg.dia.profile.model.domain.PagedResponse;
import com.mcmcg.dia.profile.model.entity.FieldDefinitionEntity;
import com.mcmcg.dia.profile.model.entity.FieldDefinitionHistoryEntity;
import com.mcmcg.dia.profile.model.query.CustomQueryBuilder;
import com.mcmcg.dia.profile.util.MediaProfileUtil;

@Service
public class FieldDefinitionService extends BaseService {

	private static final Logger LOG = Logger.getLogger(FieldDefinitionService.class);

	@Autowired
	protected ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	@Qualifier("fieldDefinitionDAO")
	private FieldDefinitionDAO fieldDefinitionDAO;

	@Autowired
	private DocumentFieldDefinitionService documentFieldDefinitionService;

	@Autowired
	@Qualifier("fieldDefinitionHistoryDAO")
	private FieldDefinitionHistoryDAO fieldDefinitionHistoryDAO;

	public FieldDefinition saveDocumentFieldsDefinition(FieldDefinition fieldDefinition)
			throws ServiceException, PersistenceException {
		return saveDocumentFieldsDefinition(fieldDefinition, null);
	}

	public FieldDefinition saveDocumentFieldsDefinition(FieldDefinition fieldDefinition, String id)
			throws ServiceException, PersistenceException {

		FieldDefinitionHistoryEntity fieldHistory = null;
		try {
			if (!validateFieldDefinition(fieldDefinition)) {
				String message = "The Field Definition is not valid, FieldName, FieldDescription and FieldType are required";
				LOG.error(message);
				throw new ServiceException(message);
			}
			if (StringUtils.isBlank(id)) {
				validateFieldNameUnique(fieldDefinition.getFieldName());
				fieldDefinition.setVersion(1L);
			} else {
				if (StringUtils.equals(fieldDefinition.getId(), id)) {
					FieldDefinitionEntity current = fieldDefinitionDAO.findOne(id);
					if (current != null) {
						if (!StringUtils.equals(current.getFieldName(), fieldDefinition.getFieldName())) {
							validateFieldNameUnique(fieldDefinition.getFieldName());
						}
						fieldDefinition.setCreateDate(current.getCreateDate());
						fieldDefinition.setVersion(current.getVersion() + 1);
						updateFieldDefinitionsInDocumentFields(fieldDefinition);
					} else {
						String message = String.format("No FieldDefinition exists for ID %s", id);
						LOG.error(message);
						throw new ServiceException(message);
					}
				} else {
					String message = String.format("The ID %s does not match the JSON Object ID %s", id,
							fieldDefinition.getId());
					LOG.error(message);
					throw new ServiceException(message);
				}
			}

			save(fieldDefinition);
			fieldHistory = new FieldDefinitionHistoryEntity(fieldDefinition);
			fieldDefinitionHistoryDAO.save(fieldHistory);

		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		} catch (PersistenceException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServiceException("Failed to run operation save for FieldsDefinition ", t);
		}

		return fieldDefinition;
	}

	public FieldDefinition retrieveByVersion(String id, Long version) throws ServiceException, PersistenceException {
		LOG.debug("FindByVersion FieldDefinition started");
		FieldDefinition fieldDefinition = null;
		try {
			FieldDefinitionHistoryEntity historyEntity = fieldDefinitionHistoryDAO.findOne(id + "_" + version);
			fieldDefinition = MediaProfileUtil.buildFieldDefinitionFromEntity(historyEntity);
			fieldDefinition.setId(id);
		} catch (ParseException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException(e.getMessage(), e);
		}
		LOG.debug("FindByVersion FieldDefinition finished");
		return fieldDefinition;
	}

	public FieldDefinition retrieveFieldDefinitionById(String fieldId) throws ServiceException, PersistenceException {
		FieldDefinition fieldDefinition = null;
		try {
			FieldDefinitionEntity entity = fieldDefinitionDAO.findOne(fieldId);
			fieldDefinition = MediaProfileUtil.buildFieldDefinitionFromEntity(entity);

		} catch (ParseException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException();
		}

		return fieldDefinition;
	}

	public List<FieldDefinition> retrieveAllFieldDefinition() throws PersistenceException, ServiceException {

		List<FieldDefinition> domainList = new ArrayList<FieldDefinition>();
		try {
			List<FieldDefinitionEntity> entitiesList = fieldDefinitionDAO.findAll();
			for (FieldDefinitionEntity entity : entitiesList) {
				domainList.add(MediaProfileUtil.buildFieldDefinitionFromEntity(entity));
			}
		} catch (ParseException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException(e.getMessage(), e);
		}

		return domainList;
	}

	/**
	 * 
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 */
	public PagedResponse<Object> retrieveFieldDefinitions(String filter, String sort, int page, int size)
			throws ServiceException, PersistenceException {
		// Get Count
		int total = getTotalCount(N1qlQueryDAO.GET_COUNT_FIELDDEFINITION, filter, null);

		// Execute the query
		List<JsonObject> results = filter(N1qlQueryDAO.GET_ALL_FIELDDEFINITION, filter, null, sort, page, size);

		List<Object> parseResult = parseJsonObjectToObject(results);

		return new PagedResponse<Object>(total, CustomQueryBuilder.calculateStartIndex(page, size), size, parseResult);
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	public List<Long> retrieveVersionsById(String id) throws ServiceException, PersistenceException {

		LOG.debug("retrieveVersionsById Template Mapping started");
		List<Long> versionList = null;
		try {
			FieldDefinitionEntity entity = fieldDefinitionDAO.findOne(id);
			if (entity != null) {
				Long version = entity.getVersion();
				versionList = fillVersionsIntoList(versionList, version);
			}
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException();
		}
		LOG.debug("retrieveVersionsById Template Mapping finished");

		return versionList;
	}

	//////////////// PRIVATE METHODS///////////////////

	private void save(FieldDefinition fieldDefinition) throws JsonProcessingException, ParseException {
		if (StringUtils.isBlank(fieldDefinition.getId())) {
			String id = UUID.randomUUID().toString();
			fieldDefinition.setId(id);
		}
		fieldDefinitionDAO.save(MediaProfileUtil.buildFieldDefinitionEntityFromDomain(fieldDefinition));
	}

	private boolean validateFieldDefinition(FieldDefinition fieldDefinition) {
		boolean isValid = true;
		if (StringUtils.isBlank(fieldDefinition.getFieldName())
				|| StringUtils.isBlank(fieldDefinition.getFieldDescription())
				|| StringUtils.isBlank(fieldDefinition.getFieldType())) {
			isValid = false;
		}
		return isValid;
	}

	private void validateFieldNameUnique(String fieldName) throws ServiceException, PersistenceException {
		FieldDefinitionEntity entity = fieldDefinitionDAO.findByFieldName(fieldName);
		if (entity != null) {
			String message = "Field Name is already in use, please try again!";
			LOG.error(message);
			throw new ServiceException(message);
		}
	}

	private void updateFieldDefinitionsInDocumentFields(final FieldDefinition newFieldDefinition)
			throws PersistenceException, ServiceException {

		taskExecutor.execute(new Runnable() {

			@Override
			public void run() {
				String fieldId = newFieldDefinition.getId();
				String query = N1qlQueryDAO.GET_DOCUMENTFIELDS_WITH_FIELDDEFINITION;

				List<JsonObject> results = null;
				try {
					results = simpleFilter(query, fieldId);
				} catch (PersistenceException pe) {
					String errorMsj = "Error on updateFieldDefinitionsInDocumentFields method block 1 --->";
					LOG.error(errorMsj + pe.getMessage(), pe);
				}
				for (JsonObject jsonObj : results) {
					String documentFieldId = jsonObj.getString("id");
					DocumentFieldsDefinition documentField;
					try {
						documentField = documentFieldDefinitionService
								.retrieveDocumentFieldDefinitionById(documentFieldId);

						List<DocumentFieldDefinition> fields = documentField.getFieldDefinitions();
						for (DocumentFieldDefinition documentFieldDefinition : fields) {
							FieldDefinition fieldDefinition = documentFieldDefinition.getFieldDefinition();
							if (StringUtils.equals(fieldDefinition.getId(), fieldId)) {
								documentFieldDefinition.setFieldDefinition(newFieldDefinition);
							}
						}
						documentField.setFieldDefinitions(fields);
						documentField.setUpdatedBy(newFieldDefinition.getUpdatedBy());
						documentFieldDefinitionService.saveDocumentFieldsDefinition(documentField, documentFieldId);
					} catch (PersistenceException | ServiceException e) {
						String errorMsj = "Error on updateFieldDefinitionsInDocumentFields method block 2 --->";
						LOG.error(errorMsj + e.getMessage(), e);
					}
				}
			}
		});

	}

}
