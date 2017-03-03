package com.mcmcg.dia.batchmanager.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.mcmcg.dia.batchmanager.entity.BatchProfileJob;
import com.mcmcg.dia.batchmanager.exception.PersistenceException;

@Repository("batchProfileJobDAO")
public class BatchProfileJobDAO extends AuroraRepository{

	private static final Logger LOG = Logger.getLogger(BatchProfileJobDAO.class);

	private final static String BATCHPROFILEJOB_SAVE = "BATCHPROFILEJOB_SAVE";
	private final static String RETRIEVE_BATCH_PROFILE_JOB = "RETRIEVE_BATCH_PROFILE_JOB";//
	private final static String BATCHPROFILEJOB_FIND_BY_ID = "BATCHPROFILEJOB_FIND_BY_ID";
	private final static String BATCHPROFILEJOB_UPDATE_COUNTERS = "BATCHPROFILEJOB_UPDATE_COUNTERS";

	/**
	 * 
	 * @param entity
	 * @return KEY
	 * @throws PersistenceException
	 */
	public Long save(BatchProfileJob entity) throws PersistenceException {
		long key = -1;
		String query = queriesMap.get(BATCHPROFILEJOB_SAVE);
		LOG.debug(String.format("query [%s] ", query));
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, entity.getBatchProfileId(),
					entity.getVersion(), entity.getJobStatusId(), entity.getExecutionStartDateTime(),
					entity.getExecutionEndDateTime(), entity.getTotalDocumentsToBeProcessed(),
					entity.getDocumentsProcessed(), entity.getDocumentsInException(), entity.getDocumentsFailed(),
					entity.getDeleteTempDataAfter(), entity.getCreatedBy(), entity.getCreatedOnDate(),
					entity.getChangedBy(), entity.getChangedOnDate(), entity.getWorkstationName(),
					entity.getApplicationId());
			
			jdbcTemplate.update(psCreator, keyHolder);
			key = keyHolder.getKey().longValue();
			
		} catch (Throwable t) {
			String errorMsj = "An error occurred trying to update/save BatchProfileJob";
			LOG.error(errorMsj, t);
			throw new PersistenceException(errorMsj, t);
		}
		
		return key;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public BatchProfileJob findOne(Long key) throws PersistenceException {

		String query = queriesMap.get(BATCHPROFILEJOB_FIND_BY_ID);
		LOG.debug(String.format("query [%s] ", query));
		BatchProfileJob batchProfileJob = null;
		try {
			List<BatchProfileJob> results = new ArrayList<BatchProfileJob>();
			PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, key);
			results = populateAllBatchProfileJob(psCreator);

			if (!CollectionUtils.isEmpty(results)) {

				batchProfileJob = results.get(0);
			}

		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on findOne()" + ex.getMessage(), ex);
		}
		return batchProfileJob;

	}

	/**
	 * It will fetch all the required record from DB.
	 * 
	 * @return
	 * @throws PersistenceException
	 * @author salam4
	 */
	public List<BatchProfileJob> retrieveAllBatchProfileJobs() throws PersistenceException {
		LOG.info(" Start retriveBatchProfileJob() ");

		List<BatchProfileJob> results = new ArrayList<BatchProfileJob>();

		String query = queriesMap.get(RETRIEVE_BATCH_PROFILE_JOB);
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query);
		LOG.debug(String.format("query [%s] ", query));

		try {
			results = populateAllBatchProfileJob(psCreator);

		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on retriveBatchProfileJob()", ex);
		}
		LOG.info("End retriveBatchProfileJob() ");
		return results;

	}

	/**
	 * 
	 * @param id
	 * @param processed
	 * @param exception
	 * @param failed
	 * @return
	 */
	public boolean updateCounters(BatchProfileJob batchProfileJob) throws com.mcmcg.dia.batchmanager.exception.PersistenceException{
		
		String query = queriesMap.get(BATCHPROFILEJOB_UPDATE_COUNTERS);
		LOG.debug(String.format("query [%s] ", query));
		int updated = -1;
		try {
			
			long processed = batchProfileJob.getDocumentsProcessed();
			long exception = batchProfileJob.getDocumentsInException();
			long failed = batchProfileJob.getDocumentsFailed();
			long id = batchProfileJob.getBatchProfileJobId();
			String user = batchProfileJob.getChangedBy();
			
			PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, processed, exception, failed, user, id);
			updated = jdbcTemplate.update(psCreator);
			
		} catch (Throwable t) {
			String errorMsj = "An error occurred trying to update/save BatchProfileJob";
			LOG.error(errorMsj, t);
			throw new PersistenceException(errorMsj, t);
		}
		
		return updated > -1;
	}
	/******************************************************************
	 * 
	 * 
	 * 
	 * 
	 * PRIVATE METHODS
	 * 
	 * 
	 * 
	 * 
	 ******************************************************************/

	/**
	 * to populate BatchProfileJobs
	 * 
	 * @param results
	 * @param BatchProfileJobValueMapList
	 * @author salam4
	 */
	private List<BatchProfileJob> populateAllBatchProfileJob(PreparedStatementCreator psCreator) {
		LOG.debug(" Start populateAllBatchProfileJob() ");

		List<BatchProfileJob> entitiesList = jdbcTemplate.execute(psCreator,
				new PreparedStatementCallback<List<BatchProfileJob>>() {

					@Override
					public List<BatchProfileJob> doInPreparedStatement(PreparedStatement ps)
							throws SQLException, DataAccessException {
						ResultSet rs = ps.executeQuery();
						List<BatchProfileJob> entitiesList = new ArrayList<BatchProfileJob>();
						while (rs.next()) {
							BatchProfileJob batchProfile = populateBatchProfileJob(rs);
							entitiesList.add(batchProfile);
						}
						return entitiesList;
					}
				});

		LOG.debug(" End populateAllBatchProfileJob() ");

		return entitiesList;
	}

	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private BatchProfileJob populateBatchProfileJob(ResultSet rs) throws SQLException {
		BatchProfileJob batchProfile = new BatchProfileJob();
		batchProfile.setBatchProfileId(rs.getLong("BatchProfileId"));
		batchProfile.setBatchProfileJobId(rs.getLong("BatchProfileJobId"));
		batchProfile.setDocumentsFailed(rs.getLong("DocumentsFailed"));
		batchProfile.setDocumentsInException(rs.getLong("DocumentsInException"));
		batchProfile.setDocumentsProcessed(rs.getLong("DocumentsProcessed"));
		batchProfile.setJobStatusId(rs.getInt("JobStatusId"));
		batchProfile.setTotalDocumentsToBeProcessed(rs.getLong("TotalDocumentsToBeProcessed"));
		batchProfile.setDeleteTempDataAfter(rs.getInt("DeleteTempDataAfter"));
		Date startDate = new Date(rs.getTimestamp("ExecutionStartDateTime").getTime());
		Date endDate = new Date(rs.getTimestamp("ExecutionEndDateTime").getTime());

		batchProfile.setExecutionStartDateTime(startDate);
		batchProfile.setExecutionEndDateTime(endDate);
		return batchProfile;
	}

}
