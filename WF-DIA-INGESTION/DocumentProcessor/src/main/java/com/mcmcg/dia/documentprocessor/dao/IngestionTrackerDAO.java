package com.mcmcg.dia.documentprocessor.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.documentprocessor.entity.IngestionTrackerEntity;
import com.mcmcg.dia.documentprocessor.exception.PersistenceException;
import com.mcmcg.dia.iwfm.domain.NewDocumentStatus;

/**
 * 
 * @author Victor Arias
 *
 */

@Repository
public class IngestionTrackerDAO extends AuroraRepository {

	private static final Logger LOG = Logger.getLogger(IngestionTrackerDAO.class);

	private final static String INGESTIONTRACKER_UPDATE = "INGESTIONTRACKER_UPDATE";
	private final static String INGESTIONTRACKER_UPDATE_WITH_BATCH_ID = "INGESTIONTRACKER_UPDATE_WITH_BATCH_ID";
	private final static String INGESTIONTRACKER_FIND_BATCH_ID = "INGESTIONTRACKER_FIND_BATCH_ID";
	private final static String INGESTIONTRACKER_SUMMARY = "INGESTIONTRACKER_SUMMARY";

	public boolean update(String documentId, NewDocumentStatus documentStatus) throws PersistenceException {
		boolean success = false;
		try {
			long batchId = findLastBatchId(documentId);
			if (batchId > 0) {
				success = update(documentId, documentStatus, batchId);
			} else {
				String query = queriesMap.get(INGESTIONTRACKER_UPDATE);
				LOG.debug(String.format("query [%s] ", query));
				PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query,
						documentStatus.getStatus(), documentStatus.getUpdatedBy(), documentId);

				success = jdbcTemplate.update(psCreator) > 0;
			}
		} catch (PersistenceException e) {
			LOG.error(e);
			throw e;
		}
		return success;
	}

	public boolean update(IngestionTrackerEntity entity) {
		/*
		 * String query = queriesMap.get(INGESTIONTRACKER_UPDATE_WITH_BATCH_ID);
		 * LOG.debug(String.format("query [%s] ", query));
		 * PreparedStatementCreator psCreator = new
		 * CustomPreparedStatementCreator(query, entity.getDocumentStatusCode(),
		 * entity.getChangedBy(), entity.getDocumentId(),
		 * entity.getBatchExecutionId());
		 */

		return update(entity.getDocumentId(),
				new NewDocumentStatus(entity.getDocumentStatusCode(), entity.getChangedBy()),
				entity.getBatchExecutionId());
	}

	/**
	 * Get an Ingestion Tracker Summary
	 * 
	 * @return List<Map<String, Object>>
	 * @throws PersistenceException
	 */
	public List<Map<String, Object>> getIngestionTrackerSummary() throws PersistenceException {

		LOG.debug("Start processing getIngestionTrackerSummary");
		String query = queriesMap.get(INGESTIONTRACKER_SUMMARY);

		try {
			return jdbcTemplate.queryForList(query);
		} catch (DataAccessException e) {
			String message = "Error on getting the Ingestion Tracker Summary";
			LOG.error(message, e);
			throw new PersistenceException(message, e);
		}
	}

	/*********************************************************************************************************
	 * 
	 * PRIVATE METHODS
	 * 
	 *********************************************************************************************************/

	private boolean update(String documentId, NewDocumentStatus documentStatus, Long batchId) {
		String query = queriesMap.get(INGESTIONTRACKER_UPDATE_WITH_BATCH_ID);
		LOG.debug(String.format("query [%s] ", query));

		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, documentStatus.getStatus(),
				documentStatus.getUpdatedBy(), documentId, batchId);

		return jdbcTemplate.update(psCreator) > 0;
	}

	private Long findLastBatchId(String documentId) throws PersistenceException {
		long batchId = 0;
		String query = queriesMap.get(INGESTIONTRACKER_FIND_BATCH_ID);
		LOG.debug(String.format("query [%s] ", query));
		try {
			PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, documentId);
			batchId = populateBatchId(psCreator);
		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on findByWorkflowIdIngestionStateCode()", ex);
		} catch (Throwable t) {
			LOG.error(t);
			throw new PersistenceException("Error on findByWorkflowIdIngestionStateCode()", t);
		}

		return batchId;
	}

	private Long populateBatchId(PreparedStatementCreator psCreator) throws SQLException, DataAccessException {
		Long batchId = jdbcTemplate.execute(psCreator, new PreparedStatementCallback<Long>() {

			@Override
			public Long doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ResultSet rs = ps.executeQuery();
				long batchId = 0;
				if (rs.next()) {
					batchId = rs.getLong("Batch_Execution_Id");
				}
				return batchId;
			}
		});

		return batchId;
	}

}
