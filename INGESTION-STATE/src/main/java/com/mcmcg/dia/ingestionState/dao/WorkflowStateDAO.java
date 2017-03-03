package com.mcmcg.dia.ingestionState.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.ingestionState.exception.PersistenceException;
import com.mcmcg.dia.ingestionState.model.entity.WorkflowStateEntity;

/**
 * 
 * @author Victor Arias
 *
 */

@Repository("workflowStateDAO")
public class WorkflowStateDAO {
	
	private static final Logger LOG = Logger.getLogger(WorkflowStateDAO.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Resource(name="queriesMap")
	private Map<String, String> queriesMap;
	
	private final static String WORKFLOWSTATE_SAVE = "WORKFLOWSTATE_SAVE";
	private final static String WORKFLOWSTATE_FIND_ID = "WORKFLOWSTATE_FIND_ID";
	private final static String WORKFLOWSTATE_FIND_DOCUMENTID = "WORKFLOWSTATE_FIND_DOCUMENTID";
	private final static String WORKFLOWSTATE_UPDATE_STATE = "WORKFLOWSTATE_UPDATE_STATE";
	private final static String WORKFLOWSTATE_UPDATE_RERUN = "WORKFLOWSTATE_UPDATE_RERUN";
	private final static String WORKFLOWSTATE_UPDATE = "WORKFLOWSTATE_UPDATE";
	
	private final static int APPLICATION_ID = 1;
	@Resource(name = "EC2")
	private String WORKSTATION_NAME;

	public WorkflowStateEntity findByDocumentId(String documentId) throws PersistenceException {
		WorkflowStateEntity entity = null;
		String query = queriesMap.get(WORKFLOWSTATE_FIND_DOCUMENTID);
		LOG.debug(String.format("query [%s] ", query));
		try {
			entity = populateWorkflowState(documentId, query);
		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on findByDocumentId()", ex);
		} catch (Throwable t) {
			LOG.error(t);
			throw new PersistenceException("Error on findByDocumentId()", t);
		}
		return entity;
	}
	
	public WorkflowStateEntity findById(Long id) throws PersistenceException {
		WorkflowStateEntity entity = null;
		String query = queriesMap.get(WORKFLOWSTATE_FIND_ID);
		LOG.debug(String.format("query [%s] ", query));
		try {
			entity = populateWorkflowState(id, query);
		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on findById()", ex);
		} catch (Throwable t) {
			LOG.error(t);
			throw new PersistenceException("Error on findById()", t);
		}
		return entity;
	}
	
	public boolean save(WorkflowStateEntity entity) throws PersistenceException {
		
		String query = queriesMap.get(WORKFLOWSTATE_SAVE);
		LOG.debug(String.format("query [%s] ", query));
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(
				query, entity.getDocumentId(), entity.getForceRerun(),
				entity.getRerunNumber(), entity.getIngestionStateCode(),
				entity.getUpdatedBy(), entity.getUpdatedBy(), WORKSTATION_NAME, APPLICATION_ID);
		
		return jdbcTemplate.update(psCreator) > 0;
	}
	
	public boolean update(WorkflowStateEntity entity) {
		String query = queriesMap.get(WORKFLOWSTATE_UPDATE);
		LOG.debug(String.format("query [%s] ", query));
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(
				query, entity.getIngestionStateCode(), entity.getRerunNumber(), 
				entity.getForceRerun(), entity.getUpdatedBy(), entity.getId());
		
		return jdbcTemplate.update(psCreator) > 0;
	}
	
	public boolean updateRerun(WorkflowStateEntity entity) {
		String query = queriesMap.get(WORKFLOWSTATE_UPDATE_RERUN);
		LOG.debug(String.format("query [%s] ", query));
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(
				query, entity.getForceRerun(), entity.getUpdatedBy(), entity.getId());
		
		return jdbcTemplate.update(psCreator) > 0;
	}
	
	public boolean updateIngestionState(WorkflowStateEntity entity) {
		String query = queriesMap.get(WORKFLOWSTATE_UPDATE_STATE);
		LOG.debug(String.format("query [%s] ", query));
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(
				query, entity.getIngestionStateCode(), entity.getUpdatedBy(),  entity.getId());
		
		return jdbcTemplate.update(psCreator) > 0;
	}
	
	private WorkflowStateEntity populateWorkflowState(Object id, String query) throws DataAccessException {
		WorkflowStateEntity entity = null;
		
		//id could be Workflow_State_Id or Document_Id
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, id);
		
		entity = jdbcTemplate.execute(psCreator, new PreparedStatementCallback<WorkflowStateEntity>() {

			@Override
			public WorkflowStateEntity doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ResultSet rs = ps.executeQuery();
				WorkflowStateEntity entity = null;
				if(rs.next()) {
					entity = new WorkflowStateEntity();
					entity.setDocumentId(rs.getString("Document_Id"));
					entity.setForceRerun(rs.getBoolean("Force_Rerun"));
					entity.setIngestionStateCode(rs.getString("Ingestion_State_Code"));
					entity.setRerunNumber(rs.getInt("Rerun_Number"));
					entity.setId(rs.getLong("Workflow_State_Id"));
					entity.setUpdatedBy(rs.getString("Changed_By"));
				}
				return entity;
			}
		});
		
		return entity;
	}
	
}
