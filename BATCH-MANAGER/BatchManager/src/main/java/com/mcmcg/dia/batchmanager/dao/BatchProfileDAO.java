package com.mcmcg.dia.batchmanager.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.batchmanager.domain.BatchProfileWithAction;
import com.mcmcg.dia.batchmanager.domain.SearchFilter;
import com.mcmcg.dia.batchmanager.entity.Action;
import com.mcmcg.dia.batchmanager.entity.BatchProfile;
import com.mcmcg.dia.batchmanager.entity.BatchProfileHistory;
import com.mcmcg.dia.batchmanager.entity.SearchFiltersBatchProfile;
import com.mcmcg.dia.batchmanager.entity.SearchFiltersBatchProfileHistory;

@Repository("batchProfileDAO")
public class BatchProfileDAO extends AuroraRepository {

	private static final Logger LOG = Logger.getLogger(BatchProfileDAO.class);

	private final static String BATCHPROFILE_SAVE = "BATCHPROFILE_SAVE";
	private final static String BATCHPROFILE_HISTORY_SAVE = "BATCHPROFILE_HISTORY_SAVE";
	private final static String BATCHPROFILE_ON_DEMAND_SAVE = "BATCHPROFILE_ON_DEMAND_SAVE";
	private final static String BATCHPROFILE_HISTORY_ON_DEMAND_SAVE = "BATCHPROFILE_HISTORY_ON_DEMAND_SAVE";
	private final static String FIND_SEARCH_FILTERS_BY_PROFILE_ID = "FIND_SEARCH_FILTERS_BY_PROFILE_ID";
	private final static String RETRIVE_BATCH_PROFILE = "RETRIVE_BATCH_PROFILE";
	private final static String RETRIVE_BATCH_PROFILE_ACTION = "RETRIVE_BATCH_PROFILE_ACTION";
	private final static String RETRIVE_SEARCH_FILTER = "RETRIVE_SEARCH_FILTER";
	private final static String SAVE_BATCH_SEARCH_FILTER_VALUES = "SAVE_BATCH_SEARCH_FILTER_VALUES";
	private final static String SAVE_BATCH_SEARCH_FILTER_HISTORY_VALUES = "SAVE_BATCH_SEARCH_FILTER_HISTORY_VALUES";
	private final static String FETCH_BATCH_VERSION = "FETCH_BATCH_VERSION";
	private final static String UPDATE_BATCH_STATUS = "UPDATE_BATCH_STATUS";
	private final static String DELETE_BATCH_SEARCH_FILTER_VALUES = "DELETE_BATCH_SEARCH_FILTER_VALUES";
	private final static String BATCHPROFILE_UPDATE = "BATCHPROFILE_UPDATE";
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss");

	public Long save(BatchProfile entity) throws PersistenceException {
		Long key = 0L;
		String query = queriesMap.get(BATCHPROFILE_ON_DEMAND_SAVE);
		String historyQuery = queriesMap.get(BATCHPROFILE_HISTORY_ON_DEMAND_SAVE);
		BatchProfileHistory historyEntity = new BatchProfileHistory();
		BeanUtils.copyProperties(entity, historyEntity);
		LOG.debug(String.format("query [%s] ", query));
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			PreparedStatementCreator psCreator = saveStatement(query, entity,
					entity.getFrecuency() == null || entity.getFrecuency() == 0L);
			if (jdbcTemplate.update(psCreator, keyHolder) > 0) {
				key = keyHolder.getKey().longValue();
				historyEntity.setBatchProfileId(key);
			}
			// Updating History
			PreparedStatementCreator psCreatorHist = saveStatement(historyQuery, historyEntity,
					historyEntity.getFrecuency() == null || historyEntity.getFrecuency() == 0L);
			// Search Filters---Need To Update-->

			jdbcTemplate.update(psCreatorHist);
		} catch (Throwable t) {
			String errorMsj = "An error occurred trying to update/save BatchProfile";
			LOG.error(errorMsj, t);
			throw new PersistenceException(errorMsj, t);
		}
		return key;
	}

	public void createTempTable(Long batchProfileId) {
		String tableName = String.format("TempTable_%s_%s", batchProfileId);

	}

	private PreparedStatementCreator saveStatement(String query, BatchProfile entity, boolean onDemand) {
		PreparedStatementCreator psCreator = null;

		if (!onDemand) {
			psCreator = new CustomPreparedStatementCreator(query, entity.getActionId(), entity.getName(),
					entity.getDescription(), entity.getVersion(), entity.getBatchType(), entity.getCreationMethod(),
					entity.getFileName(), entity.getStatus(), entity.getFrecuency(), entity.getScheduleDate(),
					entity.getScheduleTime(), entity.getCreatedBy(), entity.getCreatedOnDate(), entity.getChangedBy(),
					entity.getChangedOnDate(), entity.getWorkstationName(), entity.getApplicationId());
		} else {
			if (entity instanceof BatchProfileHistory) {
				psCreator = new CustomPreparedStatementCreator(query, entity.getBatchProfileId(), entity.getActionId(),
						entity.getName(), entity.getDescription(), entity.getVersion(), entity.getBatchType(),
						entity.getCreationMethod(), entity.getFileName(), entity.getStatus(), entity.getCreatedBy(),
						entity.getCreatedOnDate(), entity.getChangedBy(), entity.getChangedOnDate(),
						entity.getWorkstationName(), entity.getApplicationId());
			} else {
				psCreator = new CustomPreparedStatementCreator(query, entity.getActionId(), entity.getName(),
						entity.getDescription(), entity.getVersion(), entity.getBatchType(), entity.getCreationMethod(),
						entity.getFileName(), entity.getStatus(), entity.getCreatedBy(), entity.getCreatedOnDate(),
						entity.getChangedBy(), entity.getChangedOnDate(), entity.getWorkstationName(),
						entity.getApplicationId());
			}

		}
		return psCreator;
	}

	public List<SearchFiltersBatchProfile> getSearchFiltersByBatchProfileId(Long batchProfileId)
			throws PersistenceException {

		String query = queriesMap.get(FIND_SEARCH_FILTERS_BY_PROFILE_ID);
		LOG.debug(String.format("query [%s] ", query));

		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, batchProfileId);

		List<SearchFiltersBatchProfile> listSearchFiltersBatchProfile = jdbcTemplate.execute(psCreator,
				new PreparedStatementCallback<List<SearchFiltersBatchProfile>>() {

					@Override
					public List<SearchFiltersBatchProfile> doInPreparedStatement(PreparedStatement ps)
							throws SQLException, DataAccessException {
						ResultSet rs = ps.executeQuery();
						List<SearchFiltersBatchProfile> searchFiltersBatchProfileList = new ArrayList<SearchFiltersBatchProfile>();
						while (rs.next()) {
							SearchFiltersBatchProfile searchFiltersBatchProfile = populateSearchFiltersBatchProfile(rs);
							searchFiltersBatchProfileList.add(searchFiltersBatchProfile);
						}
						return searchFiltersBatchProfileList;
					}
				});
		LOG.debug(" End SearchFiltersByBatchProfileId");

		return listSearchFiltersBatchProfile;
	}

	private SearchFiltersBatchProfile populateSearchFiltersBatchProfile(ResultSet rs) throws SQLException {
		SearchFiltersBatchProfile searchFiltersBatchProfile = new SearchFiltersBatchProfile();
		searchFiltersBatchProfile.setBatchProfileId(rs.getLong("BatchProfileId"));
		searchFiltersBatchProfile.setSearchFilterId(rs.getLong("FilterId"));
		searchFiltersBatchProfile.setValue(rs.getString("FilterValue"));
		searchFiltersBatchProfile.setVersion(rs.getLong("Version"));

		return searchFiltersBatchProfile;
	}

	/**
	 * It will fetch all the required record from DB.
	 * 
	 * @return
	 * @throws PersistenceException
	 * @author salam4
	 */
	public List<BatchProfileWithAction> retriveBatchProfileWithAction() throws PersistenceException {
		LOG.info(" Start retriveBatchProfileJob() ");

		List<BatchProfileWithAction> results = new ArrayList<BatchProfileWithAction>();

		String query = queriesMap.get(RETRIVE_BATCH_PROFILE);

		try {
			populateAllBatchProfileJob(results, jdbcTemplate.queryForList(query));

		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on retriveBatchProfileJob()", ex);
		}
		LOG.info("End retriveBatchProfileJob() ");
		return results;

	}

	/**
	 * to populate BatchProfileJobs
	 * 
	 * @param results
	 * @param BatchProfileJobValueMapList
	 * @author salam4
	 * 
	 */
	private void populateAllBatchProfileJob(List<BatchProfileWithAction> results,
			List<Map<String, Object>> BatchProfileValueMapList) {
		LOG.info(" Start populateAllBatchProfileJob() ");

		for (Map<String, Object> valueMap : BatchProfileValueMapList) {
			BatchProfile batchProfile = new BatchProfile();
			BatchProfileWithAction batchProfilewithAction = new BatchProfileWithAction();

			if (valueMap.get("BatchProfileId") != null)
				batchProfile.setBatchProfileId(new Long(valueMap.get("BatchProfileId").toString()));
			if (valueMap.get("ActionID") != null)
				batchProfile.setActionId(new Long(valueMap.get("ActionID").toString()));
			if (valueMap.get("Name") != null)
				batchProfile.setName(valueMap.get("Name").toString());
			if (valueMap.get("Description") != null)
				batchProfile.setDescription(valueMap.get("Description").toString());
			if (valueMap.get("Version") != null)
				batchProfile.setVersion((Integer) valueMap.get("Version"));
			if (valueMap.get("BatchType") != null)
				batchProfile.setBatchType(valueMap.get("BatchType").toString());
			if (valueMap.get("CreationMethod") != null)
				batchProfile.setCreationMethod(valueMap.get("CreationMethod").toString());
			if (valueMap.get("CSVFileName") != null)
				batchProfile.setFileName(valueMap.get("CSVFileName").toString());
			if (valueMap.get("Status") != null)
				batchProfile.setStatus((Integer) valueMap.get("Status"));
			if (valueMap.get("FrequencyId") != null)
				batchProfile.setFrecuency(new Long(valueMap.get("ActionID").toString()));
			try {
				if (valueMap.get("ScheduledDate") != null)
					batchProfile.setScheduleDate(dateFormatter.parse(valueMap.get("ScheduledDate").toString()));
				if (valueMap.get("ScheduledTime") != null)
					batchProfile.setScheduleTime(timeFormatter.parse(valueMap.get("ScheduledTime").toString()));
			} catch (ParseException e) {
				LOG.error(e);
			}
			batchProfilewithAction.setBatchProfile(batchProfile);
			batchProfilewithAction.setAction(getAction(batchProfile.getActionId()));

			results.add(batchProfilewithAction);
		}
		LOG.info(" End populateAllBatchProfileJob() ");
	}

	public Action getAction(long actionId) throws PersistenceException {
		LOG.info(" Start getAction() ");
		Action result = new Action();

		String query = queriesMap.get(RETRIVE_BATCH_PROFILE_ACTION);
		LOG.debug(String.format("query [%s] ", query));
		try {
			result = jdbcTemplate.queryForObject(query, new Object[] { actionId }, new RowMapper<Action>() {
				public Action mapRow(ResultSet rs, int rowNum) throws SQLException {
					Action data = new Action();
					data.setId(new Long(rs.getInt("ActionId")));
					data.setDescription(rs.getString("ActionDescription"));

					return data;
				}
			});
		} catch (EmptyResultDataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("No records found.", ex);
		} catch (DataAccessException ex) {
			LOG.error(ex);
			ex.printStackTrace();
			throw new PersistenceException("Error on fetching Action Data", ex);
		} catch (Exception ex) {
			LOG.error(ex);
			ex.printStackTrace();
			throw new PersistenceException("Error on getAction()", ex);
		}
		LOG.info("End retreiveServicerJobData() ");
		return result;

	}

	/**
	 * It will fetch all the required record from DB.
	 * 
	 * @return
	 * @throws PersistenceException
	 * @author salam4
	 */
	public Map<Long, SearchFilter> retriveSearchFilters() throws PersistenceException {
		LOG.info(" Start retriveSearchFilter() ");

		Map<Long, SearchFilter> results = new HashMap<Long, SearchFilter>();

		String query = queriesMap.get(RETRIVE_SEARCH_FILTER);

		try {
			populateAllSearchFilter(results, jdbcTemplate.queryForList(query));

		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on retriveSearchFilter()", ex);
		}
		LOG.info("End retriveSearchFilter() ");
		return results;

	}

	/**
	 * @param results
	 * @param SearchFilterValueMapList
	 */
	private void populateAllSearchFilter(Map<Long, SearchFilter> results,

			List<Map<String, Object>> SearchFilterValueMapList) {
		LOG.info(" Start populateAllSearchFilter() ");

		for (Map<String, Object> valueMap : SearchFilterValueMapList) {
			SearchFilter searchFilter = new SearchFilter();

			if (valueMap.get("FilterId") != null)
				searchFilter.setFilterId(new Long(valueMap.get("FilterId").toString()));
			if (valueMap.get("FilterCode") != null)
				searchFilter.setFilterCode(valueMap.get("FilterCode").toString());
			if (valueMap.get("FilterDescription") != null)
				searchFilter.setFilterDescription(valueMap.get("FilterDescription").toString());
			if (valueMap.get("FilterTypeId") != null)
				searchFilter.setFilterTypeId(new Long(valueMap.get("FilterTypeId").toString()));

			results.put(searchFilter.getFilterId(), searchFilter);
		}
		LOG.info(" End populateAllSearchFilter() ");

	}

	/**
	 * @param listSearchFiltersBatchProfile
	 * @return Inserts Search Filters, Search filter History
	 */
	public void saveBatchSearchFilterValues(final List<SearchFiltersBatchProfile> listSearchFiltersBatchProfile) {
		Long key = 0L;
		String query = queriesMap.get(SAVE_BATCH_SEARCH_FILTER_VALUES);
		String historyQuery = queriesMap.get(SAVE_BATCH_SEARCH_FILTER_HISTORY_VALUES);
		final List<SearchFiltersBatchProfileHistory> listSearchFilterHistory = new ArrayList<SearchFiltersBatchProfileHistory>();
		for (int i = 0; i < listSearchFiltersBatchProfile.size(); i++) {
			BeanUtils.copyProperties(listSearchFilterHistory.get(i), listSearchFiltersBatchProfile.get(i));
		}

		// Inserting in Batch

		// Inserting into SearchfilterValues
		int resultSearchFilter[] = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				SearchFiltersBatchProfile searchFiltersBatchProfile = listSearchFiltersBatchProfile.get(i);
				ps.setLong(1, searchFiltersBatchProfile.getBatchProfileId());
				ps.setLong(2, searchFiltersBatchProfile.getSearchFilterId());
				ps.setString(3, searchFiltersBatchProfile.getValue());
				ps.setLong(4, searchFiltersBatchProfile.getVersion());
				ps.setDate(5, (Date) searchFiltersBatchProfile.getCreatedOnDate());
				ps.setString(6, searchFiltersBatchProfile.getCreatedBy());
				ps.setDate(7, (Date) searchFiltersBatchProfile.getChangedOnDate());
				ps.setString(8, searchFiltersBatchProfile.getChangedBy());
				ps.setString(9, searchFiltersBatchProfile.getWorkstationName());
				ps.setInt(10, searchFiltersBatchProfile.getApplicationId());
			}

			@Override
			public int getBatchSize() {
				return listSearchFiltersBatchProfile.size();
			}
		});
		// Inserting into history table
		int resultSearchFilterHistory[] = jdbcTemplate.batchUpdate(historyQuery, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				SearchFiltersBatchProfileHistory searchFiltersBatchProfileHist = listSearchFilterHistory.get(i);
				ps.setLong(1, searchFiltersBatchProfileHist.getBatchProfileId());
				ps.setLong(2, searchFiltersBatchProfileHist.getSearchFilterId());
				ps.setString(3, searchFiltersBatchProfileHist.getValue());
				ps.setLong(4, searchFiltersBatchProfileHist.getVersion());
				ps.setDate(5, (Date) searchFiltersBatchProfileHist.getCreatedOnDate());
				ps.setString(6, searchFiltersBatchProfileHist.getCreatedBy());
				ps.setDate(7, (Date) searchFiltersBatchProfileHist.getChangedOnDate());
				ps.setString(8, searchFiltersBatchProfileHist.getChangedBy());
				ps.setString(9, searchFiltersBatchProfileHist.getWorkstationName());
				ps.setInt(10, searchFiltersBatchProfileHist.getApplicationId());
			}

			@Override
			public int getBatchSize() {
				return listSearchFilterHistory.size();
			}
		});

		// return false;

	}

	/**
	 * The method returns the version for batch
	 * 
	 * @param batchProfileId
	 * @return
	 */
	public Long getBatchProfileVersion(Long batchProfileId) {
		String query = queriesMap.get(FETCH_BATCH_VERSION);
		Long Version = 0L;
		Version = jdbcTemplate.queryForObject(query, new Object[] { batchProfileId }, Long.class);

		return Version;

	}

	/**
	 * @param status
	 * @param batchProfileId
	 * @return This will make batch active/Inactive
	 */
	public Boolean updateBatchStatus(Integer status, Long batchProfileId) {
		String query = queriesMap.get(UPDATE_BATCH_STATUS);
		int rows = jdbcTemplate.update(query, new Object[] { status, batchProfileId });
		if (rows > 0)
			return true;
		else
			return false;

	}

	/**
	 * This will delete search Filters if they alredy exsists so new one could
	 * be added
	 */
	private Boolean deleteBatchSearchFilterValues(Long batchProfileId) {
		String query = queriesMap.get(DELETE_BATCH_SEARCH_FILTER_VALUES);
		int rows = jdbcTemplate.update(query, new Object[] { batchProfileId });
		if (rows > 0)
			return true;
		else
			return false;

	}

	/**
	 * the method updates Batch Profile for Scheduled batch
	 * 
	 * @param batchProfile
	 * @return
	 */
	public boolean updateScheduledBatch(BatchProfile batchProfile) {
		String query = queriesMap.get(BATCHPROFILE_UPDATE);
		int rows = jdbcTemplate.update(query,
				new Object[] { batchProfile.getActionId(), batchProfile.getName(), batchProfile.getDescription(),
						batchProfile.getVersion(), batchProfile.getBatchType(), batchProfile.getCreationMethod(),
						batchProfile.getFileName(), batchProfile.getStatus(), batchProfile.getFrecuency(),
						batchProfile.getScheduleDate(), batchProfile.getScheduleTime(), batchProfile.getCreatedOnDate(),
						batchProfile.getCreatedBy(), batchProfile.getChangedOnDate(), batchProfile.getChangedBy(),
						batchProfile.getWorkstationName(), batchProfile.getApplicationId(),
						batchProfile.getBatchProfileId() });

		if (rows > 0)
			return true;
		else
			return false;

	}

	/**
	 * @param batchProfile
	 * @return
	 */
	public boolean saveScheduledledBatch(BatchProfile batchProfile) {
		Long key = 0L;
		String query = queriesMap.get(BATCHPROFILE_SAVE);
		String historyQuery = queriesMap.get(BATCHPROFILE_HISTORY_SAVE);
		BatchProfileHistory historyEntity = new BatchProfileHistory();
		BeanUtils.copyProperties(batchProfile, historyEntity);
		LOG.debug(String.format("query save batch profile[%s] ", query));
		KeyHolder keyHolder = new GeneratedKeyHolder();
		//Saving to BatchProfile
	
		int rows=jdbcTemplate.update(new CustomPreparedStatementCreator(query, batchProfile.getActionId(), batchProfile.getName(), batchProfile.getDescription(),
				batchProfile.getVersion(), batchProfile.getBatchType(), batchProfile.getCreationMethod(),
				batchProfile.getFileName(), batchProfile.getStatus(), batchProfile.getFrecuency(),
				batchProfile.getScheduleDate(), batchProfile.getScheduleTime(), batchProfile.getCreatedOnDate(),
				batchProfile.getCreatedBy(), batchProfile.getChangedOnDate(), batchProfile.getChangedBy(),
				batchProfile.getWorkstationName(), batchProfile.getApplicationId()
				 ),keyHolder);
		if (rows > 0) {
			key = keyHolder.getKey().longValue();
			historyEntity.setBatchProfileId(key);
		}
		
		//Saving to BatchProfile History
		int rowsHist=jdbcTemplate.update(new CustomPreparedStatementCreator(query,historyEntity.getBatchProfileId(),historyEntity.getActionId(), historyEntity.getName(), historyEntity.getDescription(),
				historyEntity.getVersion(), historyEntity.getBatchType(), historyEntity.getCreationMethod(),
				historyEntity.getFileName(), historyEntity.getStatus(), historyEntity.getFrecuency(),
				historyEntity.getScheduleDate(), historyEntity.getScheduleTime(), historyEntity.getCreatedOnDate(),
				historyEntity.getCreatedBy(), historyEntity.getChangedOnDate(), historyEntity.getChangedBy(),
				historyEntity.getWorkstationName(), historyEntity.getApplicationId()),keyHolder);
		if (rows > 0 && rowsHist>0 )
			return true;
		else
			return false;

	}
	// Logic in Save Batch Profile----
	// If the version exsist then update the exsiting Bacth Profile and add the
	// version by +1
	// Insert in Batch Profile history

	// if the version does not exsist then insert batch profile
	// Insert in the search filter history

	// Call the

	// Service Values
	// Within the service there will be one method to save batch Profile with
	// Search Filters and it would be transactional
	// All the Dao's would be called from this method
	// Create A Service to update the batchProfile

	// DAO
	// Create a new method for Save Batch Profile for Scheduled batches--- For
	// Insert and Update create two new queries in dao and call
	// Create a new Method to updtae the status(0/1) corresponding to a batch
	// profile id-- This would be a end point and would be called to deactivate
	// and activate a batch only
	// A new method to Get the version--- Every time a new entry is required add
	// one to version and then insert it in all three fields+1
	// Create a new Dao Method to save Search Filters for a batch Profile ID-
	// This will be used to enter in Search Filter History as well

}
