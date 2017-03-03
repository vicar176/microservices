package com.mcmcg.dia.documentprocessor.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.documentprocessor.entity.BatchExecutionEntity;
import com.mcmcg.dia.documentprocessor.exception.PersistenceException;
import com.mcmcg.dia.iwfm.domain.ExecutionStatus;

/**
 * 
 * @author wporras
 *
 */
@Repository
public class BatchExecutionDAO extends AuroraRepository {

	private static final Logger LOG = Logger.getLogger(BatchExecutionDAO.class);

	private final static String BATCHEXECUTION_UPDATE = "BATCHEXECUTION_UPDATE";
	private final static String BATCHEXECUTION_FIND_ALL = "BATCHEXECUTION_FIND_ALL";
	private final static String BATCHEXECUTION_FIND_RUNNING = "BATCHEXECUTION_FIND_RUNNING";
	private final static String BATCHEXECUTION_SUMMARY = "BATCHEXECUTION_SUMMARY";

	public boolean update(Long executionStatusId, ExecutionStatus executionStatus) {
		String query = queriesMap.get(BATCHEXECUTION_UPDATE);
		LOG.debug(String.format("query [%s] ", query));

		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query,
				executionStatus.getExecutionStatusCode(), executionStatus.getChangedBy(), executionStatusId);

		return jdbcTemplate.update(psCreator) > 0;
	}

	public List<Long> findRunningBatches() throws PersistenceException {
		String query = queriesMap.get(BATCHEXECUTION_FIND_RUNNING);
		LOG.debug(String.format("query [%s] ", query));
		List<Long> batchesIds = null;
		try {
			batchesIds = populateBatchesId(query);
		} catch (DataAccessException dae) {
			LOG.error(dae);
			throw new PersistenceException(dae.getMessage());
		}

		return batchesIds;
	}

	/**
	 * Find all Batch Executions
	 * 
	 * @return List<BatchExecutionEntity>
	 * @throws PersistenceException
	 */
	public List<BatchExecutionEntity> findAll() throws PersistenceException {

		LOG.debug("Start processing findAll");
		String query = queriesMap.get(BATCHEXECUTION_FIND_ALL);

		try {
			return populateBatchExecutionList(query);
		} catch (DataAccessException e) {
			String message = "Error on finding all Batch Executions => " + e.getMessage();
			LOG.error(message, e);
			throw new PersistenceException(message, e);
		}
	}

	private List<BatchExecutionEntity> populateBatchExecutionList(String query) {

		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query);

		List<BatchExecutionEntity> resultList = jdbcTemplate.execute(psCreator,
				new PreparedStatementCallback<List<BatchExecutionEntity>>() {

					@Override
					public List<BatchExecutionEntity> doInPreparedStatement(PreparedStatement ps)
							throws SQLException, DataAccessException {
						ResultSet rs = ps.executeQuery();
						List<BatchExecutionEntity> resultList = new ArrayList<BatchExecutionEntity>();
						while (rs.next()) {
							BatchExecutionEntity entity = new BatchExecutionEntity();
							entity.setBatchExecutionId(rs.getLong("Batch_Execution_Id"));
							entity.setExecutionDate(rs.getTimestamp("execution_Date"));
							entity.setExecutionStatusCode(rs.getString("Execution_Status_Code"));

							entity = (BatchExecutionEntity) populateBaseAtributes(entity, rs);

							resultList.add(entity);
						}
						return resultList;
					}

				});

		return resultList;
	}

	private List<Long> populateBatchesId(String query) throws DataAccessException {
		List<Long> fields = new ArrayList<Long>();

		List<Map<String, Object>> fieldList = jdbcTemplate.queryForList(query);
		if (fieldList != null) {
			for (Map<String, Object> fieldMap : fieldList) {
				Long value = (Long) fieldMap.get("Ingestion_State_Code");
				fields.add(value);
			}
		}

		return fields;
	}

	public List<Map<String, Object>> getBatchExecutionsSummary() throws PersistenceException {
		LOG.debug("Start processing getBatchExecutionSummary");

		String query = queriesMap.get(BATCHEXECUTION_SUMMARY);
		try {
			return jdbcTemplate.queryForList(query);
		} catch (DataAccessException e) {
			String message = "Error on getting the Batch Executions summary";
			LOG.error(message, e);
			throw new PersistenceException(message, e);
		}
	}

}
