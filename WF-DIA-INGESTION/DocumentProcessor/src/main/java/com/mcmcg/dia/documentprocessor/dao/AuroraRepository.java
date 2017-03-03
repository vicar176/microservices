package com.mcmcg.dia.documentprocessor.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.mcmcg.dia.documentprocessor.entity.BaseModel;

/**
 * @author jaleman
 *
 */
public abstract class AuroraRepository {

	private static final Logger LOG = Logger.getLogger(AuroraRepository.class);

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Autowired
	protected NamedParameterJdbcTemplate  namedParameterJdbcTemplate;

	@Resource(name = "queriesMap")
	protected Map<String, String> queriesMap;

	public void waitTime() {
		Random random = new Random();
		long waitTime = 3000 + (random.nextInt(10) + 1) * 1000;
		LOG.debug("Waiting time ---> " + waitTime / 1000);
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			// nothing to do
		}
	}

	/**
	 * Util Method, Populate all common attributes of the entities
	 * 
	 * @param entity
	 * @param ResultSet
	 * @return BaseEntity - Child
	 * @throws SQLException
	 */
	public BaseModel populateBaseAtributes(BaseModel entity, ResultSet rs) throws SQLException {

		entity.setCreatedBy(rs.getString("Created_By"));
		entity.setCreatedOnDate(rs.getTimestamp("Created_On_Date"));
		entity.setChangedBy(rs.getString("Changed_By"));
		entity.setChangedOnDate(rs.getTimestamp("Changed_On_Date"));

		entity.setWorkstationName(rs.getString("Workstation_Name"));
		entity.setApplicationId(rs.getInt("Application_Id"));

		return entity;
	}

}
