package com.mcmcg.dia.ingestionState.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.ingestionState.exception.PersistenceException;
import com.mcmcg.dia.ingestionState.model.entity.IngestionStepEntity;
import com.mcmcg.dia.ingestionState.util.FilteringQueryUtils;

/**
 * 
 * @author Victor Arias
 *
 */

@Repository("ingestionStepDAO")
public class IngestionStepDAO {

	private static final Logger LOG = Logger.getLogger(IngestionStepDAO.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Resource(name = "queriesMap")
	private Map<String, String> queriesMap;

	private final static String INGESTIONSTATE_FIND_ALL = "INGESTIONSTATE_FIND_ALL";
	private final static String INGESTIONSTEP_SAVE = "INGESTIONSTEP_SAVE";
	private final static String INGESTIONSTEP_FIND_ID = "INGESTIONSTEP_FIND_ID";
	private final static String INGESTIONSTEP_FIND_WORKFLOWID_INGESTIONSTATECODE = "INGESTIONSTEP_FIND_WORKFLOWID_INGESTIONSTATECODE";
	private final static String INGESTIONSTEP_FIND_WORKFLOWID = "INGESTIONSTEP_FIND_WORKFLOWID";
	private final static String INGESTIONSTEP_FIND_DOCUMENTID = "INGESTIONSTEP_FIND_DOCUMENTID";
	private final static String INGESTIONSTEP_FILTERING = "INGESTIONSTEP_FILTERING";
	private final static String INGESTIONSTEP_FILTERING_COUNT = "INGESTIONSTEP_FILTERING_COUNT";
	private final static String INGESTIONSTEP_FILTERING_GROUPBY = "INGESTIONSTEP_FILTERING_GROUPBY";

	private final static int APPLICATION_ID = 1;
	@Resource(name = "EC2")
	private String WORKSTATION_NAME;

	private Map<String, String> filterValues;

	public IngestionStepDAO() {
		filterValues = new HashMap<String, String>();
		filterValues.put("date", "Ingestion_Step.Created_On_Date");
		filterValues.put("documentId", "Workflow_State.Document_Id");
		filterValues.put("step", "Ingestion_Step.Ingestion_State_Code");
		filterValues.put("description", "Ingestion_Step.Description");
	}

	public IngestionStepEntity findByWorkflowIdIngestionStateCode(Long workflowId, String ingestionStateCode)
			throws PersistenceException {
		IngestionStepEntity entity = null;
		String query = queriesMap.get(INGESTIONSTEP_FIND_WORKFLOWID_INGESTIONSTATECODE);
		LOG.debug(String.format("query [%s] ", query));
		try {
			PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, workflowId,
					ingestionStateCode);
			List<IngestionStepEntity> stepsList = populateIngestionStep(psCreator);
			if (stepsList.size() == 1) {
				entity = stepsList.get(0);
			}
		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on findByWorkflowIdIngestionStateCode()", ex);
		} catch (Throwable t) {
			LOG.error(t);
			throw new PersistenceException("Error on findByWorkflowIdIngestionStateCode()", t);
		}
		return entity;
	}

	public boolean save(IngestionStepEntity entity) {
		String query = queriesMap.get(INGESTIONSTEP_SAVE);
		LOG.debug(String.format("query [%s] ", query));
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, entity.getWorkflowStateId(),
				entity.getRerunNumber(), entity.getDescription(), entity.getStatusCode(),
				entity.getIngestionStateCode(), entity.getUpdatedBy(), entity.getCreateDate(), entity.getUpdateDate(),
				entity.getUpdatedBy(), WORKSTATION_NAME, APPLICATION_ID);
		return jdbcTemplate.update(psCreator) > 0;
	}

	public IngestionStepEntity findById(Long id) throws PersistenceException {
		IngestionStepEntity entity = null;
		String query = queriesMap.get(INGESTIONSTEP_FIND_ID);
		LOG.debug(String.format("query [%s] ", query));
		try {
			PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, id);
			List<IngestionStepEntity> stepsList = populateIngestionStep(psCreator);
			if (stepsList.size() == 1) {
				entity = stepsList.get(0);
			}
		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on findById()", ex);
		} catch (Throwable t) {
			LOG.error(t);
			throw new PersistenceException("Error on findById()", t);
		}
		return entity;
	}

	public List<String> findAllIngestionStates() throws PersistenceException {
		List<String> fields = null;
		String query = queriesMap.get(INGESTIONSTATE_FIND_ALL);
		LOG.debug(String.format("query [%s] ", query));

		try {
			fields = populateIngestionStates(query);
		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on findAllIngestionStates()", ex);
		} catch (Throwable t) {
			LOG.error(t);
			throw new PersistenceException("Error on findAllIngestionStates()", t);
		}

		return fields;
	}

	public List<IngestionStepEntity> findByWorkflowId(Long workflowStateId) throws PersistenceException {
		List<IngestionStepEntity> stepsList = null;
		String query = queriesMap.get(INGESTIONSTEP_FIND_WORKFLOWID);
		LOG.debug(String.format("query [%s] ", query));
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, workflowStateId);
		try {
			stepsList = populateIngestionStep(psCreator);
		} catch (DataAccessException | SQLException e) {
			PersistenceException pe = new PersistenceException(e.getMessage(), e);
			LOG.error(pe);
			throw pe;
		}
		return stepsList;
	}

	public List<IngestionStepEntity> findByDocumentId(String documentId) throws PersistenceException {
		List<IngestionStepEntity> stepsList = null;
		String query = queriesMap.get(INGESTIONSTEP_FIND_DOCUMENTID);
		LOG.debug(String.format("query [%s] ", query));
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, documentId);
		try {
			stepsList = populateIngestionStep(psCreator);
		} catch (DataAccessException | SQLException e) {
			PersistenceException pe = new PersistenceException(e.getMessage(), e);
			LOG.error(pe);
			throw pe;
		}
		return stepsList;
	}

	public List<Map<String, Object>> filtering(String filter, String sort, int page, int pageSize)
			throws PersistenceException {
		String query = queriesMap.get(INGESTIONSTEP_FILTERING);
		String filterClause = FilteringQueryUtils.buildFilterClause(filter, filterValues);
		String sortingClause = FilteringQueryUtils.buildSortingClause(sort, filterValues);
		String limitClause = FilteringQueryUtils.buildLimitClause(page, pageSize);
		String groupByClause = queriesMap.get(INGESTIONSTEP_FILTERING_GROUPBY);
		
		String finalQuery = query + filterClause + groupByClause + sortingClause + limitClause;
		LOG.debug(String.format("Filtering query: \n %s", finalQuery));
		List<Map<String, Object>> results = popoulateFailedIngestionStep(finalQuery);
		return results;
	}
	
	public List<String> filteringDocumentIds(String filter) throws PersistenceException {
		String query = queriesMap.get(INGESTIONSTEP_FILTERING);
		String groupByClause = queriesMap.get(INGESTIONSTEP_FILTERING_GROUPBY);
		String filterClause = FilteringQueryUtils.buildFilterClause(filter, filterValues);
		String finalQuery = query + filterClause + groupByClause;
		List<String> reprocessDocumentIds = popoulateReprocessDocumentIds(finalQuery);
		return reprocessDocumentIds;
	}

	public int filteringCount(String filter) throws PersistenceException {
		Integer count = 0;
		try {
			String countQuery = queriesMap.get(INGESTIONSTEP_FILTERING_COUNT);
			String query = queriesMap.get(INGESTIONSTEP_FILTERING);
			String groupByClause = queriesMap.get(INGESTIONSTEP_FILTERING_GROUPBY);
			String filterClause = FilteringQueryUtils.buildFilterClause(filter, filterValues);
			String finalQuery = query + filterClause + groupByClause;
			countQuery = countQuery.replace("?", finalQuery);
			count = jdbcTemplate.queryForObject(countQuery, Integer.class);
		} catch (Throwable t) {
			String message = StringUtils.EMPTY;
			if (!StringUtils.isBlank(t.getMessage())) {
				message = t.getMessage();
			} else {
				message = "An error occurred while trying to count the Failed Ingeston Steps";
			}
			LOG.error(message);
			throw new PersistenceException(message);
		}
		return count;
	}

	/***********************
	 * * PRIVATE METHODS * *
	 ***********************/

	private List<IngestionStepEntity> populateIngestionStep(PreparedStatementCreator psCreator)
			throws SQLException, DataAccessException {

		List<IngestionStepEntity> entitiesList = jdbcTemplate.execute(psCreator,
				new PreparedStatementCallback<List<IngestionStepEntity>>() {

					@Override
					public List<IngestionStepEntity> doInPreparedStatement(PreparedStatement ps)
							throws SQLException, DataAccessException {
						ResultSet rs = ps.executeQuery();
						List<IngestionStepEntity> entitiesList = new ArrayList<IngestionStepEntity>();
						while (rs.next()) {
							IngestionStepEntity entity = new IngestionStepEntity();
							entity.setDescription(rs.getString("Description"));
							entity.setStatusCode(rs.getString("Status_Code"));
							entity.setIngestionStateCode(rs.getString("Ingestion_State_Code"));
							entity.setRerunNumber(rs.getInt("Rerun_Number"));
							entity.setWorkflowStateId(rs.getLong("Workflow_State_Id"));
							entity.setId(rs.getLong("Ingestion_Step_Id"));
							entity.setUpdatedBy(rs.getString("Changed_By"));
							Date createDate = new Date(rs.getTimestamp("Created_On_Date").getTime());
							Date updateDate = new Date(rs.getTimestamp("Changed_On_Date").getTime());
							entity.setCreateDate(createDate);
							entity.setUpdateDate(updateDate);
							entitiesList.add(entity);
						}
						return entitiesList;
					}
				});

		return entitiesList;
	}

	private List<String> populateIngestionStates(String query) throws DataAccessException {
		List<String> fields = new ArrayList<String>();

		List<Map<String, Object>> fieldList = jdbcTemplate.queryForList(query);
		if (fieldList != null) {
			for (Map<String, Object> fieldMap : fieldList) {
				String value = (String) fieldMap.get("Ingestion_State_Code");
				fields.add(value);
			}
		}

		return fields;
	}

	private List<Map<String, Object>> popoulateFailedIngestionStep(String query) throws DataAccessException {

		List<Map<String, Object>> fieldList = jdbcTemplate.queryForList(query);

		return fieldList;
	}

	private List<String> popoulateReprocessDocumentIds(String query) throws DataAccessException {
		
		List<String> documentIdList = new ArrayList<String>();
		List<Map<String, Object>> stepsList = jdbcTemplate.queryForList(query);
		if (!stepsList.isEmpty()) {
			for (Map<String, Object> step : stepsList) {
				String documentId = (String) step.get("documentId");
				documentIdList.add(documentId);
			}
		}

		return documentIdList;
	}

}
