package com.mcmcg.dia.iwfm.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.iwfm.entity.ConfigurationParameters;

@Repository
public class ConfigurationParametersDAO extends AuroraRepository {
	
	private static final Logger LOG = Logger.getLogger(ConfigurationParametersDAO.class);
	
	private static final String CONFIGURATIONPARAMETERS_FIND_ALL = "CONFIGURATIONPARAMETERS_FIND_ALL";
	private static final String CONFIGURATIONPARAMETERS_UPDATE_ONE = "CONFIGURATIONPARAMETERS_UPDATE_ONE";
	private static final String CONFIGURATIONPARAMETERS_FIND_ONE = "CONFIGURATIONPARAMETERS_FIND_ONE";
	
	public ConfigurationParametersDAO() {
	}

	/**
	 * 
	 * @return
	 * @throws PersistenceException
	 */
	public List<ConfigurationParameters> findAll() throws PersistenceException{
		
		String query = queriesMap.get(CONFIGURATIONPARAMETERS_FIND_ALL);
		List<ConfigurationParameters> parameterList= null;
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
	public ConfigurationParameters findOne (String key) throws PersistenceException{
		String query = queriesMap.get(CONFIGURATIONPARAMETERS_FIND_ONE);
		ConfigurationParameters parameter= null;
		try{
			parameter = populateConfigurationParameter(query, key);
		}
		catch (DataAccessException ex){
			LOG.warn(ex.getMessage());
			throw new PersistenceException("Error on findAll()", ex );
		}
		
		return parameter;
	}


	/**
	 * 
	 * @param key
	 * @param newValue
	 * @param user
	 * @return
	 * @throws PersistenceException
	 */
	public List<ConfigurationParameters> updateParameter(String key, String newValue, String user) throws PersistenceException{
		String query = queriesMap.get(CONFIGURATIONPARAMETERS_UPDATE_ONE);
		List<ConfigurationParameters> parameterList= null;
		try{
			parameterList = populateUpdatedParamaterList(query, key, newValue, user);
		}
		catch (DataAccessException ex){
			LOG.warn(ex.getMessage());
			throw new PersistenceException("Error on findAll()", ex );
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
	
	private List<ConfigurationParameters> populateUpdatedParamaterList(String query, String key, String newValue,
			String user) throws PersistenceException {
		
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, newValue, user, key);

		jdbcTemplate.update(psCreator);

		return findAll();
	}

	private ConfigurationParameters populateConfigurationParameter(String query, String key) {
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query, key);
		
		ConfigurationParameters parameter = jdbcTemplate.execute(psCreator, new PreparedStatementCallback<ConfigurationParameters>() {

			@Override
			public ConfigurationParameters doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException {
				ResultSet rs = ps.executeQuery();
				ConfigurationParameters parameter = null;
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
	private List<ConfigurationParameters> populateParamaterList(String query) {
		
		PreparedStatementCreator psCreator = new CustomPreparedStatementCreator(query);

		List<ConfigurationParameters> parameterList =jdbcTemplate.execute(psCreator, new PreparedStatementCallback<List<ConfigurationParameters>>() {

			@Override
			public List<ConfigurationParameters> doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException {
				ResultSet rs = ps.executeQuery();
				List<ConfigurationParameters> parameterList = new ArrayList<ConfigurationParameters>();
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
	private ConfigurationParameters populateConfigurationParameterEntity(ResultSet rs) throws SQLException {
		ConfigurationParameters parameter;
		parameter = new ConfigurationParameters();
		parameter.setKey(rs.getString("Parameter_Key"));
		parameter.setValue(rs.getString("Parameter_Value"));
		return parameter;
	}


}
