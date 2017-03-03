package com.mcmcg.dia.documentprocessor.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.documentprocessor.dao.IngestionStatusDAO;
import com.mcmcg.dia.documentprocessor.exception.PersistenceException;
import com.mcmcg.dia.documentprocessor.exception.ServiceException;

/**
 * 
 * @author wporras
 *
 */
@Service
public class IngestionStatusService extends BaseService {

	private static final Logger LOG = Logger.getLogger(IngestionStatusService.class);

	@Autowired
	private IngestionStatusDAO ingestionStatusDAO;

	/**
	 * Get an Ingestion Tracker Summary
	 * 
	 * @return List<Map<String, Object>>
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	public List<Map<String, Object>> getIngestionStatus() throws ServiceException, PersistenceException {

		LOG.debug("Start processing getIngestionStatus");

		try {
			return ingestionStatusDAO.getIngestionStatus();
		} catch (PersistenceException e) {
			throw e;
		} catch (Throwable t) {
			String message = "Error trying to get the Ingestion Status";
			LOG.error(message, t);
			throw new ServiceException(message, t);
		}
	}

}
