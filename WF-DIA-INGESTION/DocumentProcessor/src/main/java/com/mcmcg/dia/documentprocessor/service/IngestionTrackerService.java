package com.mcmcg.dia.documentprocessor.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.documentprocessor.dao.IngestionTrackerDAO;
import com.mcmcg.dia.documentprocessor.exception.PersistenceException;
import com.mcmcg.dia.documentprocessor.exception.ServiceException;

/**
 * 
 * @author wporras
 *
 */
@Service
public class IngestionTrackerService extends BaseService {

	private static final Logger LOG = Logger.getLogger(IngestionTrackerService.class);

	@Autowired
	private IngestionTrackerDAO ingestionTrackerDAO;

	/**
	 * Get an Ingestion Tracker Summary
	 * 
	 * @return List<Map<String, Object>>
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	public List<Map<String, Object>> getIngestionTrackerSummary() throws ServiceException, PersistenceException {

		LOG.debug("Start processing getIngestionTrackerSummary");

		try {
			return ingestionTrackerDAO.getIngestionTrackerSummary();
		} catch (PersistenceException e) {
			throw e;
		} catch (Throwable t) {
			String message = "Error trying to get the Ingestion Tracker Summary";
			LOG.error(message, t);
			throw new ServiceException(message, t);
		}
	}

}
