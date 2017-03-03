package com.mcmcg.dia.profile.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mcmcg.dia.profile.aop.DiagnosticsAspect;
import com.mcmcg.dia.profile.dao.DocumentFieldDefinitionDAO;
import com.mcmcg.dia.profile.dao.DocumentFieldDefinitionHistoryDAO;
import com.mcmcg.dia.profile.exception.PersistenceException;
import com.mcmcg.dia.profile.exception.ServiceException;
import com.mcmcg.dia.profile.model.OaldProfileModel.DocumentType;
import com.mcmcg.dia.profile.model.domain.DocumentFieldsDefinition;
import com.mcmcg.dia.profile.model.entity.DocumentFieldsDefinitionEntity;
import com.mcmcg.dia.profile.model.entity.DocumentFieldsDefinitionHistoryEntity;
import com.mcmcg.dia.profile.util.MediaProfileUtil;

/**
 * 
 * @author Victor Arias
 *
 */

@Service
public class DocumentFieldDefinitionService extends BaseService {

	private static final Logger LOG = Logger.getLogger(DocumentFieldDefinitionService.class);

	@Autowired
	@Qualifier("documentFieldDefinitionDAO")
	DocumentFieldDefinitionDAO documentFieldDefinitionDAO;

	@Autowired
	@Qualifier("documentFieldDefinitionHistoryDAO")
	DocumentFieldDefinitionHistoryDAO documentFieldDefinitionHistoryDAO;

	@Autowired
	private DiagnosticsAspect diagnosticsAspect;

	public DocumentFieldsDefinition saveDocumentFieldsDefinition(DocumentFieldsDefinition documentFieldDefinition)
			throws ServiceException, PersistenceException {
		return saveDocumentFieldsDefinition(documentFieldDefinition, null);
	}

	public DocumentFieldsDefinition saveDocumentFieldsDefinition(DocumentFieldsDefinition documentFieldDefinition,
			String id) throws ServiceException, PersistenceException {
		DocumentFieldsDefinitionHistoryEntity documentHistory = null;
		try {
			DocumentType documentType = documentFieldDefinition.getDocumentType();
			if (documentType == null || StringUtils.isBlank(documentType.getCode()) || documentType.getId() == null
					|| documentType.getId() == 0) {
				String message = "DocumentType can not be null or empty";
				LOG.error(message);
				throw new ServiceException(message);
			}
			if (documentFieldDefinition.getFieldDefinitions() == null
					|| documentFieldDefinition.getFieldDefinitions().isEmpty()) {
				String message = "FieldDefinitions can not be null or empty";
				LOG.error(message);
				throw new ServiceException(message);
			}

			if (StringUtils.isBlank(id)) {
				validateDocumentCodeUnique(documentType.getCode());
				documentFieldDefinition.setVersion(1L);
			} else {
				if (StringUtils.equals(documentFieldDefinition.getId(), id)) {
					DocumentFieldsDefinitionEntity current = documentFieldDefinitionDAO.findOne(id);
					if (current != null) {
						if(!StringUtils.equals(current.getDocumentType().getCode(), documentType.getCode())){
							validateDocumentCodeUnique(documentType.getCode());
						}
						documentFieldDefinition.setCreateDate(current.getCreateDate());
						documentFieldDefinition.setVersion(current.getVersion() + 1);
					} else {
						String message = String.format("No DocumentFieldDefinition exist for ID %s", id);
						LOG.error(message);
						throw new ServiceException(message);
					}
				} else {
					String message = String.format("The ID %s does not match the JSON Object ID %s", id,
							documentFieldDefinition.getId());
					LOG.error(message);
					throw new ServiceException(message);
				}
			}
			save(documentFieldDefinition);
			documentHistory = new DocumentFieldsDefinitionHistoryEntity(documentFieldDefinition);
			documentFieldDefinitionHistoryDAO.save(documentHistory);
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException("Failed to run operation save for DocumentFieldsDefinition ", e);
		}

		return documentFieldDefinition;
	}

	public DocumentFieldsDefinition retrieveDocumentFieldDefinitionById(String documentFieldId)
			throws PersistenceException, ServiceException {

		DocumentFieldsDefinition documentfieldsDefinition = null;
		try {
			DocumentFieldsDefinitionEntity entity = documentFieldDefinitionDAO.findOne(documentFieldId);
			documentfieldsDefinition = MediaProfileUtil.buildDocumentFieldDefinitionFromEntity(entity);

		} catch (ParseException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException();
		}

		return documentfieldsDefinition;
	}

	public DocumentFieldsDefinition retrieveDocumentFieldDefinitionByDocTypeCode(String docTypeCode)
			throws PersistenceException, ServiceException {
		DocumentFieldsDefinition documentfieldsDefinition = null;
		try {
			DocumentFieldsDefinitionEntity entity = internalRetrieveDocumentFieldDefinitionByDocTypeCode(docTypeCode);
			documentfieldsDefinition = MediaProfileUtil.buildDocumentFieldDefinitionFromEntity(entity);
		} catch (ParseException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException();
		}
		return documentfieldsDefinition;
	}

	public List<DocumentFieldsDefinition> retrieveAllDocumentFieldsDefinition()
			throws PersistenceException, ServiceException {

		List<DocumentFieldsDefinition> domainList = new ArrayList<DocumentFieldsDefinition>();
		try {
			List<DocumentFieldsDefinitionEntity> entitiesList = documentFieldDefinitionDAO.findAll();
			for (DocumentFieldsDefinitionEntity entity : entitiesList) {
				domainList.add(MediaProfileUtil.buildDocumentFieldDefinitionFromEntity(entity));
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

	public DocumentFieldsDefinition retrieveByVersion(String id, Long version)
			throws ServiceException, PersistenceException {

		LOG.debug("FindByVersion DocumentFieldDefinition started");
		DocumentFieldsDefinition documentFieldsDefinition = null;
		try {
			DocumentFieldsDefinitionHistoryEntity historyEntity = documentFieldDefinitionHistoryDAO
					.findOne(id + "_" + version);
			documentFieldsDefinition = MediaProfileUtil.buildDocumentFieldDefinitionFromEntity(historyEntity);
			documentFieldsDefinition.setId(id);
		} catch (ParseException e) {
			LOG.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage(), e);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new PersistenceException(e.getMessage(), e);
		}
		LOG.debug("FindByVersion DocumentFieldDefinition finished");
		return documentFieldsDefinition;
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	public List<Long> retrieveVersionsById(String id)
			throws ServiceException, PersistenceException {

		LOG.debug("retrieveVersionsById Template Mapping started");
		List<Long> versionList = null;
		try {
			DocumentFieldsDefinitionEntity entity = documentFieldDefinitionDAO.findOne(id);
			if (entity != null){
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

	private void save(DocumentFieldsDefinition documentFieldsDefinition)
			throws JsonProcessingException, ParseException {
		
		long start = System.currentTimeMillis();
		
		if (StringUtils.isBlank(documentFieldsDefinition.getId())) {
			String id = UUID.randomUUID().toString();
			documentFieldsDefinition.setId(id);
		}
		documentFieldDefinitionDAO
				.save(MediaProfileUtil.buildDocumentFieldDefinitionEntityFromDomain(documentFieldsDefinition));
		
		long end = System.currentTimeMillis();
		
		diagnosticsAspect.log("saveDocumentFieldsDefinition", new Object[] {documentFieldsDefinition.getId()}, "Couchbase-Writes", start, end);
	}

	public DocumentFieldsDefinitionEntity internalRetrieveDocumentFieldDefinitionByDocTypeCode(String docTypeCode)
			throws PersistenceException {

		long start = System.currentTimeMillis();

		DocumentFieldsDefinitionEntity entity = null;
		try {
			entity = documentFieldDefinitionDAO.findByDocumentTypeCode(docTypeCode);
		} catch (Throwable t) {
			String message = String.format("Failed to find a Document Field Definition with document type code %s",
					docTypeCode);
			LOG.error(t.getMessage(), t);
			throw new PersistenceException(message);
		}

		long end = System.currentTimeMillis();
		
		diagnosticsAspect.log("retrieveDocumentFieldDefinitionByDocTypeCode", new Object[] {docTypeCode}, "Couchbase-Reads", start, end);
		
		return entity;
	}

	private void validateDocumentCodeUnique(String documentCode) throws ServiceException {

		DocumentFieldsDefinitionEntity entity = documentFieldDefinitionDAO.findByDocumentTypeCode(documentCode);
		if (entity != null) {
			String message = String.format("A DocumentFieldDefinition with documentTypeCode = %s already exists",
					documentCode);
			LOG.error(message);
			throw new ServiceException(message);
		}
	}

}
