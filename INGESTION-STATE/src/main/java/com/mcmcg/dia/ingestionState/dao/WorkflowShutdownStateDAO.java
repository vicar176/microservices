package com.mcmcg.dia.ingestionState.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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
import com.mcmcg.dia.ingestionState.model.entity.WorkflowShutdownStateEntity;

/**
 * 
 * @author Victor Arias
 *
 */

@Repository("workflowShutdownStateDAO")
public class WorkflowShutdownStateDAO {

	private static final Logger LOG = Logger.getLogger(WorkflowShutdownStateDAO.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Resource(name = "queriesMap")
	private Map<String, String> queriesMap;

	private final static String WORKFLOWSHUTDOWNSTATE_FIND = "WORKFLOWSHUTDOWNSTATE_FIND";
	private final static String WORKFLOWSHUTDOWNSTATE_SAVE = "WORKFLOWSHUTDOWNSTATE_SAVE";
	private final static String WORKFLOWSHUTDOWNSTATE_UPDATE = "WORKFLOWSHUTDOWNSTATE_UPDATE";
	private final static int APPLICATION_ID = 1;
	@Resource(name = "EC2")
	private String WORKSTATION_NAME;

	public WorkflowShutdownStateEntity find() throws PersistenceException {
		WorkflowShutdownStateEntity entity = null;
		String query = queriesMap.get(WORKFLOWSHUTDOWNSTATE_FIND);
		LOG.debug(String.format("query [%s] ", query));
		try {
			entity = populateWorkflowShutdownState(query);
		} catch (DataAccessException ex) {
			LOG.error(ex);
			throw new PersistenceException("Error on find()", ex);
		} catch (Throwable t) {
			LOG.error(t);
			throw new PersistenceException("Error on find()", t);
		}
		return entity;
	}

	public boolean save(WorkflowShutdownStateEntity entity) {

		String query = queriesMap.get(WORKFLOWSHUTDOWNSTATE_SAVE);
		LOG.debug(String.format("query [%s] ", query));
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, entity.isShutdownState(),
				entity.getUpdatedBy(), entity.getUpdatedBy(), WORKSTATION_NAME, APPLICATION_ID);

		return jdbcTemplate.update(psCreator) > 0;
	}

	public boolean update(WorkflowShutdownStateEntity entity) {
		String query = queriesMap.get(WORKFLOWSHUTDOWNSTATE_UPDATE);
		LOG.debug(String.format("query [%s] ", query));
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, entity.isShutdownState(),
				entity.getUpdatedBy(), entity.getId());

		return jdbcTemplate.update(psCreator) > 0;
	}

	private WorkflowShutdownStateEntity populateWorkflowShutdownState(String query) {
		WorkflowShutdownStateEntity entity = null;

		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query);

		entity = jdbcTemplate.execute(psCreator, new PreparedStatementCallback<WorkflowShutdownStateEntity>() {

			@Override
			public WorkflowShutdownStateEntity doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException {
				ResultSet rs = ps.executeQuery();
				WorkflowShutdownStateEntity entity = null;
				if (rs.next()) {
					entity = new WorkflowShutdownStateEntity();
					entity.setShutdownState(rs.getBoolean("Shutdown_State"));
					entity.setId(rs.getLong("Workflow_Shutdown_StateId"));
					entity.setUpdatedBy(rs.getString("Changed_By"));
					Date updateDate = new Date(rs.getTimestamp("Changed_On_Date").getTime());
					entity.setUpdateDate(updateDate);
					LOG.info(rs.getTimestamp("Changed_On_Date"));
				}
				return entity;
			}
		});

		return entity;
	}

}
