package com.mcmcg.dia.documentprocessor.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.documentprocessor.dao.BatchExecutionDAO;
import com.mcmcg.dia.documentprocessor.entity.BatchExecutionEntity;
import com.mcmcg.dia.documentprocessor.exception.PersistenceException;
import com.mcmcg.dia.documentprocessor.exception.ServiceException;

/**
 * 
 * @author wporras
 *
 */
@Service
public class BatchExecutionService extends BaseService {

	private static final Logger LOG = Logger.getLogger(BatchExecutionService.class);

	@Autowired
	private BatchExecutionDAO batchExecutionDAO;

	/**
	 * Find all Batch Executions
	 * 
	 * @return List<BatchExecutionEntity>
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	public List<BatchExecutionEntity> findAll() throws ServiceException, PersistenceException {

		LOG.debug("Start processing findAll");

		try {
			return batchExecutionDAO.findAll();
		} catch (PersistenceException e) {
			throw e;
		} catch (Throwable t) {
			String message = "Error trying to get all the Batch Executions => " + t.getMessage();
			LOG.error(message, t);
			throw new ServiceException(message, t);
		}
	}

	public List<Map<String, Object>> getBatchExecutionsSummary() throws PersistenceException, ServiceException {
		LOG.debug("Start processing getBatchExecutionSummary");

		try {
			return batchExecutionDAO.getBatchExecutionsSummary();
		} catch (PersistenceException e) {
			throw e;
		} catch (Throwable t) {
			String message = "Error trying to get the Batch Executions Summary";
			LOG.error(message, t);
			throw new ServiceException(message, t);
		}
	}

}
