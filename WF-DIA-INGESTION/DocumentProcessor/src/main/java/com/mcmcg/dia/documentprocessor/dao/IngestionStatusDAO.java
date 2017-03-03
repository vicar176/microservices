package com.mcmcg.dia.documentprocessor.dao;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.documentprocessor.exception.PersistenceException;

/**
 * 
 * @author wporras
 *
 */
@Repository
public class IngestionStatusDAO extends AuroraRepository {

	private static final Logger LOG = Logger.getLogger(IngestionStatusDAO.class);

	private final static String INGESTION_STATUS = "INGESTION_STATUS";

	/**
	 * Get an Ingestion Status
	 * 
	 * @return List<Map<String, Object>>
	 * @throws PersistenceException
	 */
	public List<Map<String, Object>> getIngestionStatus() throws PersistenceException {

		LOG.debug("Start processing getIngestionStatus");
		
		String query = queriesMap.get(INGESTION_STATUS);		
		try {
			return jdbcTemplate.queryForList(query);
		} catch (DataAccessException e) {
			String message = "Error on getting the Ingestion Status";
			LOG.error(message, e);
			throw new PersistenceException(message, e);
		}
	}

}
