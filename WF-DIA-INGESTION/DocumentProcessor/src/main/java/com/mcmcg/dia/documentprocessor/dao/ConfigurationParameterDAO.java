package com.mcmcg.dia.documentprocessor.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.documentprocessor.entity.ConfigurationParameter;
import com.mcmcg.dia.documentprocessor.exception.PersistenceException;

@Repository
public class ConfigurationParameterDAO extends AuroraRepository {
	
	private static final Logger LOG = Logger.getLogger(ConfigurationParameterDAO.class);
	
	private static final String CONFIGURATIONPARAMETERS_FIND_ALL = "CONFIGURATIONPARAMETERS_FIND_ALL";
	private static final String CONFIGURATIONPARAMETERS_UPDATE_ONE = "CONFIGURATIONPARAMETERS_UPDATE_ONE";
	private static final String CONFIGURATIONPARAMETERS_FIND_ONE = "CONFIGURATIONPARAMETERS_FIND_ONE";
	
	public ConfigurationParameterDAO() {
	}

	/**
	 * 
	 * @return
	 * @throws PersistenceException
	 */
	public List<ConfigurationParameter> findAll() throws PersistenceException{
		
		String query = queriesMap.get(CONFIGURATIONPARAMETERS_FIND_ALL);
		List<ConfigurationParameter> parameterList= null;
		try{
			parameterList = populateParamaterList(query);
		}
		catch (DataAccessException ex){
			LOG.warn(ex.getMessage());
			throw new PersistenceException("Error on findAll()", ex );
		}
		
		return parameterList;
	}

	/**
	 * 
	 * @param key
	 * @return
	 * @throws PersistenceException
	 */
	public ConfigurationParameter findOne (String key) throws PersistenceException{
		String query = queriesMap.get(CONFIGURATIONPARAMETERS_FIND_ONE);
		ConfigurationParameter parameter= null;
		try{
			parameter = populateConfigurationParameter(query, key);
		}
		catch (DataAccessException ex){
			LOG.warn(ex.getMessage());
			throw new PersistenceException("Error on findOne() => " + ex.getMessage(), ex );
		}
		
		return parameter;
	}


	/**
	 * 
	 * @param key
	 * @param newValue
	 * @return
	 * @throws PersistenceException
	 */
	public List<ConfigurationParameter> updateParameter(String key, String newValue, String updatedBy) throws PersistenceException{
		String query = queriesMap.get(CONFIGURATIONPARAMETERS_UPDATE_ONE);
		List<ConfigurationParameter> parameterList= null;
		try{
			parameterList = populateUpdatedParamaterList(query, key, newValue, updatedBy);
		}
		catch (DataAccessException ex){
			LOG.warn(ex.getMessage());
			throw new PersistenceException("Error on updateParameter()", ex );
		}
		
		return parameterList;
		
	}

	/****************************************************************************
	 * 
	 * 			PRIVATE METHODS
	 * 
	 * 
	 * 
	 ****************************************************************************/
	
	private List<ConfigurationParameter> populateUpdatedParamaterList(String query, String key, String newValue, String updatedBy) throws PersistenceException {
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, newValue, updatedBy, key);

		jdbcTemplate.update(psCreator);
		
		return findAll();
	}

	private ConfigurationParameter populateConfigurationParameter(String query, String key) {
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, key);
		
		ConfigurationParameter parameter = jdbcTemplate.execute(psCreator, new PreparedStatementCallback<ConfigurationParameter>() {

			@Override
			public ConfigurationParameter doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException {
				ResultSet rs = ps.executeQuery();
				ConfigurationParameter parameter = null;
				if (rs.next()){
					//Sets
					parameter = populateConfigurationParameterEntity(rs);
				}
				return parameter;
			}

		});		
		return parameter;
	}
	/**
	 * 
	 * @param query
	 * @return
	 */
	private List<ConfigurationParameter> populateParamaterList(String query) {
		
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query);

		List<ConfigurationParameter> parameterList =jdbcTemplate.execute(psCreator, new PreparedStatementCallback<List<ConfigurationParameter>>() {

			@Override
			public List<ConfigurationParameter> doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException {
				ResultSet rs = ps.executeQuery();
				List<ConfigurationParameter> parameterList = new ArrayList<ConfigurationParameter>();
				while (rs.next()){
					parameterList.add(populateConfigurationParameterEntity(rs));
				}
				return parameterList;
			}

		});		
		
		return parameterList;
	}
	
	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private ConfigurationParameter populateConfigurationParameterEntity(ResultSet rs) throws SQLException {
		ConfigurationParameter parameter;
		parameter = new ConfigurationParameter();
		parameter.setKey(rs.getString("Parameter_Key"));
		parameter.setValue(rs.getString("Parameter_Value"));
		return parameter;
	}


}
