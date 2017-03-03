package com.mcmcg.dia.ingestionState.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.ingestionState.dao.WorkflowShutdownStateDAO;
import com.mcmcg.dia.ingestionState.exception.PersistenceException;
import com.mcmcg.dia.ingestionState.exception.ServiceException;
import com.mcmcg.dia.ingestionState.model.domain.WorkflowShutdownState;
import com.mcmcg.dia.ingestionState.model.entity.WorkflowShutdownStateEntity;
import com.mcmcg.dia.ingestionState.util.IngestionStateUtil;

@Service
public class WorkflowShutdownStateService {

	private static final Logger LOG = Logger.getLogger(WorkflowStateService.class);

	@Autowired
	@Qualifier("workflowShutdownStateDAO")
	private WorkflowShutdownStateDAO workflowShutdownStateDAO;

	public WorkflowShutdownState save(WorkflowShutdownState domain) throws ServiceException, PersistenceException {

		try {
			
			if(StringUtils.isBlank(domain.getUpdatedBy())){
				String errorMsj = "Updated By is required!";
				ServiceException se = new ServiceException(errorMsj);
				LOG.error(errorMsj, se);
				throw se;
			}
			
			WorkflowShutdownStateEntity entity = internalFind();
			if (entity != null) {
				if (entity.isShutdownState() != domain.isShutdownState()) {
					saveInternal(domain);
				}
			} else {
				saveInternal(domain);
			}
			domain = IngestionStateUtil.convertWorkflowShutdownStateEntityToDomain(internalFind());
		} catch (ServiceException se) {
			LOG.error(se.getMessage(), se);
			throw se;
		} catch (PersistenceException pe) {
			LOG.error(pe.getMessage(), pe);
			throw pe;
		} catch (Throwable t) {
			LOG.error(t.getMessage(), t);
			throw new ServiceException(t.getMessage(), t);
		}

		return domain;
	}
	
	public WorkflowShutdownState find() throws ServiceException {
		WorkflowShutdownState domain = null;
		
		try {
			domain = IngestionStateUtil.convertWorkflowShutdownStateEntityToDomain(internalFind());
		} catch (ServiceException se) {
			LOG.error(se.getMessage(), se);
			throw se;
		}
		
		return domain;
	}

	private boolean saveInternal(WorkflowShutdownState domain) throws PersistenceException {
		return workflowShutdownStateDAO.save(IngestionStateUtil.convertWorkflowShutdownStateToEntity(domain));
	}
	
	private boolean updateInternal (WorkflowShutdownState domain) {
		WorkflowShutdownStateEntity entity = IngestionStateUtil.convertWorkflowShutdownStateToEntity(domain);
		return workflowShutdownStateDAO.update(entity);
	}

	private WorkflowShutdownStateEntity internalFind() throws ServiceException {

		WorkflowShutdownStateEntity entity = null;

		try {
			entity = workflowShutdownStateDAO.find();
		} catch (Throwable t) {
			ServiceException se = new ServiceException("Unable to find WorkflowShutdownState", t);
			LOG.error(se.getMessage(), se);
			throw se;
		}

		return entity;
	}
}
