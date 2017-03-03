package com.mcmcg.dia.ingestionState.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.ingestionState.dao.IngestionStepDAO;
import com.mcmcg.dia.ingestionState.dao.WorkflowStateDAO;
import com.mcmcg.dia.ingestionState.exception.PersistenceException;
import com.mcmcg.dia.ingestionState.exception.ServiceException;
import com.mcmcg.dia.ingestionState.model.domain.IngestionStep;
import com.mcmcg.dia.ingestionState.model.domain.PagedResponse;
import com.mcmcg.dia.ingestionState.model.entity.IngestionStepEntity;
import com.mcmcg.dia.ingestionState.model.entity.WorkflowStateEntity;
import com.mcmcg.dia.ingestionState.util.IngestionStateUtil;;

/**
 * 
 * @author Victor Arias
 *
 */
@Service
public class IngestionStepService {

	private static final Logger LOG = Logger.getLogger(IngestionStepService.class);

	@Autowired
	@Qualifier("ingestionStepDAO")
	private IngestionStepDAO ingestionStepDAO;
	
	@Autowired
	@Qualifier("workflowStateDAO")
	private WorkflowStateDAO workflowStateDAO;
	
	private List<String> documentIdList;

	private enum Status {

		success("Success"), skip("Skip"), failed("Failed");

		private String value;

		private Status(String value) {
			this.value = value;
		}

		@Override
		public String toString() {

			return value;
		}
	};

	public IngestionStep save(IngestionStep domain) throws ServiceException {
		if (domain != null) {
			try {
				if (!validateStatus(domain.getStatusCode())) {
					throw new ServiceException("The Status code is not valid");
				}
				domain.setUpdateDate(new Date());
				saveInternal(domain);
				domain.setId(ingestionStepDAO.findByWorkflowIdIngestionStateCode(domain.getWorkflowStateId(), domain.getIngestionStateCode()).getId());
			} catch (PersistenceException pe) {
				LOG.error(pe.getMessage(), pe);
				throw new ServiceException(pe.getMessage(), pe);
			} catch (ServiceException se) {
				LOG.error(se.getMessage(), se);
				throw se;
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				throw new ServiceException(e.getMessage(), e);
			} 
		} else {
			String message = "IngestionStep is null or not valid";
			ServiceException se = new ServiceException(message);
			LOG.error(message, se);
			throw se;
		}

		return domain;
	}

	public IngestionStep findById(Long id) throws ServiceException, PersistenceException {
		IngestionStep domain = null;
		try {
			domain = IngestionStateUtil.convertIngestionStepEntityToDomain(ingestionStepDAO.findById(id));
		} catch (ParseException e) {
			LOG.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return domain;
	}

	public IngestionStep findLastByDocIdStCode(Long documentId, String ingestionStateCode) throws ServiceException {
		IngestionStep domain = null;
		IngestionStepEntity entity = null;
		try {
			WorkflowStateEntity wfEntity = workflowStateDAO.findByDocumentId(String.valueOf(documentId));
			
			if(wfEntity != null) {
				Long workflowStateId = wfEntity.getId();
				entity = ingestionStepDAO.findByWorkflowIdIngestionStateCode(workflowStateId, ingestionStateCode);
				domain = IngestionStateUtil.convertIngestionStepEntityToDomain(entity);
			} else {
				String message = "The documentId does not exists";
				ServiceException se = new ServiceException(message);
				throw se;
			}
			
		} catch (ServiceException se) {
			LOG.error(se);
			throw se;
		} catch (PersistenceException pe) {
			LOG.error(pe);
			throw new ServiceException("An error occurred while trying to find an IngestionStep with documentId = "
					+ documentId + ", ingestionStateCode = " + ingestionStateCode);
		} catch (Throwable e) {
			LOG.error(e);
			throw new ServiceException("An error occurred while trying to find an IngestionStep with documentId = "
					+ documentId + ", ingestionStateCode = " + ingestionStateCode);
		}

		return domain;
	}
	
	public List<String> findAllIngestionStates() throws ServiceException {
		List<String> ingestionStates = null;
		
		try {
			ingestionStates = ingestionStepDAO.findAllIngestionStates();
		} catch (PersistenceException pe) {
			ServiceException se = new ServiceException("An error occurred while trying to find all ingestion states");
			LOG.error(se.getMessage(), se);
			throw se;
		}
		
		return ingestionStates;
	}
	
	public List<IngestionStep> findByDocumentId(String documentId) throws ServiceException {
		List<IngestionStepEntity> entitiesList = null;
		List<IngestionStep> stepsList = null;
		try {
			entitiesList = ingestionStepDAO.findByDocumentId(documentId);
			stepsList = new ArrayList<IngestionStep>();
			for(IngestionStepEntity entity : entitiesList) {
				stepsList.add(IngestionStateUtil.convertIngestionStepEntityToDomain(entity));
			}
		} catch (PersistenceException e) {
			ServiceException se = new ServiceException("Unable to find steps by document Id");
			LOG.error(se);
			throw se;
		} catch (ParseException e) {
			ServiceException se = new ServiceException("Unable to find steps by document Id");
			LOG.error(se);
			throw se;
		}
		
		return stepsList;
	}
	
	public List<IngestionStep> findByWorkflowId(Long workflowId) throws ServiceException {
		List<IngestionStepEntity> entitiesList = null;
		List<IngestionStep> stepsList = null;
		try {
			entitiesList = ingestionStepDAO.findByWorkflowId(workflowId);
			stepsList = new ArrayList<IngestionStep>();
			for(IngestionStepEntity entity : entitiesList) {
				stepsList.add(IngestionStateUtil.convertIngestionStepEntityToDomain(entity));
			}
		} catch (PersistenceException e) {
			ServiceException se = new ServiceException("Unable to find steps by workflow Id");
			LOG.error(se);
			throw se;
		} catch (ParseException e) {
			ServiceException se = new ServiceException("Unable to find steps by workflow Id");
			LOG.error(se);
			throw se;
		}
		
		return stepsList;
	}
	
	public PagedResponse<Map<String, Object>> retrieveStepsFailed(String filter, String sort, int page, int size) throws ServiceException {
		PagedResponse<Map<String, Object>> response = null;
		try {
			int total = ingestionStepDAO.filteringCount(filter);
			List<Map<String, Object>> results = ingestionStepDAO.filtering(filter, sort, page, size);
			response = new PagedResponse<Map<String, Object>>(total, page, size, results);
		} catch (PersistenceException e) {
			ServiceException se = new ServiceException(e.getMessage());
			LOG.error(se);
			throw se;
		}
		return response;
	}
	
	public List<String> retrieveDocumentIdsFailed(String filter) throws ServiceException {
		try {
			documentIdList = ingestionStepDAO.filteringDocumentIds(filter);
		} catch (PersistenceException e) {
			ServiceException se = new ServiceException(e.getMessage());
			LOG.error(se);
			throw se;
		}
		return documentIdList;
	}

	/***********************
	 * * PRIVATE METHODS * *
	 ***********************/

	private void saveInternal(IngestionStep domain) throws PersistenceException {
		ingestionStepDAO.save(IngestionStateUtil.convertIngestionStepToEntity(domain));
	}

	private boolean validateStatus(String stepStatus) {
		boolean isValid = false;
		if (!StringUtils.isBlank(stepStatus) && (StringUtils.equalsIgnoreCase(stepStatus, Status.success.toString())
				|| StringUtils.equalsIgnoreCase(stepStatus, Status.failed.toString())
				|| StringUtils.equalsIgnoreCase(stepStatus, Status.skip.toString()))) {
			isValid = true;
		}
		return isValid;
	}

}
