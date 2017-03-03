package com.mcmcg.dia.ingestionState.service;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.ingestionState.dao.WorkflowStateDAO;
import com.mcmcg.dia.ingestionState.exception.PersistenceException;
import com.mcmcg.dia.ingestionState.exception.ServiceException;
import com.mcmcg.dia.ingestionState.model.domain.WorkflowState;
import com.mcmcg.dia.ingestionState.model.entity.WorkflowStateEntity;
import com.mcmcg.dia.ingestionState.util.IngestionStateUtil;

/**
 * 
 * @author Victor Arias
 *
 */

@Service
public class WorkflowStateService {

	private static final Logger LOG = Logger.getLogger(WorkflowStateService.class);

	@Autowired
	@Qualifier("workflowStateDAO")
	private WorkflowStateDAO workflowStateDAO;

	@Resource
	private Map<Integer, String> statusMap;

	public WorkflowState save(WorkflowState domain) throws ServiceException {

		if (domain != null) {
			try {

				if (domain.getDocumentId() == null){
					String message = "WorkflowState documentId cannot be null";
					ServiceException se = new ServiceException(message);
					LOG.error(message, se);
					throw se;
				}
				
				WorkflowStateEntity current = workflowStateDAO.findByDocumentId(domain.getDocumentId());
				
				if(current != null) {
					domain.setId(current.getId());
					updateInternal(domain);
				} else {
					saveInternal(domain);
					Long createdId = workflowStateDAO.findByDocumentId(domain.getDocumentId()).getId();
					domain.setId(createdId);
				}
				
				/*if(validateUniqueDocumentId(domain.getDocumentId())) {
					if(saveInternal(domain)){
						Long createdId = workflowStateDAO.findByDocumentId(domain.getDocumentId()).getId();
						domain.setId(createdId);
					}
				} else {
					String message = "WorkflowState documentId already exists";
					ServiceException se = new ServiceException(message);
					LOG.error(message, se);
					throw se;
				}*/
				
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
			LOG.error("WorkflowState is null or not valid");
			throw new ServiceException("WorkflowState is not valid");
		}

		return domain;
	}

	/**
	 * 
	 * @param documentId
	 * @return
	 * @throws ServiceException
	 */
	public WorkflowState findByDocumentId(String documentId) throws ServiceException {
		WorkflowState domain = null;
		try {
			domain = IngestionStateUtil.convertWorkflowStateEntitytoDomain(workflowStateDAO.findByDocumentId(documentId));
		} catch (Exception e) {
			LOG.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return domain;
	}

	/**
	 * 
	 * @param documentId
	 * @param workflowState
	 * @param action
	 * @return
	 * @throws ServiceException
	 */
	public WorkflowState update(String documentId, WorkflowState workflowState, String action) throws ServiceException {
		WorkflowState domain = null;
		if (workflowState != null && documentId != null) {
			domain = findByDocumentId(documentId);
			//domain.setUpdateDate(IngestionStateUtil.formatDate(new Date()));
			domain.setUpdatedBy(workflowState.getUpdatedBy());
			switch (action) {
			case "rerun":
				if (workflowState.getForceRerun() != null) {
					domain.setForceRerun(workflowState.getForceRerun());
					workflowStateDAO.updateRerun(IngestionStateUtil.convertWorkflowStateToEntity(domain));
				} else {
					String errorMessage = "The Force Rerun attribute cannot be null";
					LOG.error(errorMessage);
					throw new ServiceException(errorMessage);
				}

				break;
			case "state":
				if (workflowState.getIngestionStateCode() != null) {
					if(validateIngestionStateCode(workflowState.getIngestionStateCode())) {
						domain.setIngestionStateCode(workflowState.getIngestionStateCode());
						workflowStateDAO.updateIngestionState(IngestionStateUtil.convertWorkflowStateToEntity(domain));
					}
				} else {
					String errorMessage = "The Ingestion State Code attribute is not valid";
					LOG.error(errorMessage);
					throw new ServiceException(errorMessage);
				}
				break;
			}
		} else {
			String message = "Unable to update WorkflowState";
			LOG.error(message);
			throw new ServiceException(message);
		}
		return domain;
	}

	/*******************
	 * PRIVATE METHODS *
	 *******************/

	private boolean validateIngestionStateCode(String state) {
		
		return statusMap.containsValue(state.toLowerCase());
	}
	
	private boolean validateUniqueDocumentId(String documentId) throws PersistenceException {
		return workflowStateDAO.findByDocumentId(documentId) == null;
	}

	private boolean saveInternal(WorkflowState domain) throws PersistenceException {
		return workflowStateDAO.save(IngestionStateUtil.convertWorkflowStateToEntity(domain));
	}
	
	private boolean updateInternal(WorkflowState domain) {
		return workflowStateDAO.update(IngestionStateUtil.convertWorkflowStateToEntity(domain));
	}

}
