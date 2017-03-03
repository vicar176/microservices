/**
 * 
 */
package com.mcmcg.dia.batchmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;

/**
 * @author jaleman
 *
 */
public class CustomPreparedStatementCreator implements PreparedStatementCreator {

	private final String sql;
	private Object[] arguments;
	/**
	 * 
	 */
	public CustomPreparedStatementCreator(String sql, Object... args) {
		this.sql = sql;

		if (args != null){
			arguments = new Object[args.length];
			int index = 0;
			for (Object arg : args){
				arguments[index++] = arg;
			}
		}
	}

	/**
	 * 
	 */
	@Override
	public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
		PreparedStatement ps = null;
		
		if (StringUtils.containsIgnoreCase(sql, "INSERT")){
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		}else{
			ps = connection.prepareStatement(sql);
		}
		
		
		if (arguments != null){
			int index = 1;
			for (Object value : arguments){
				
				if (value instanceof Long){
					ps.setLong(index++, (Long)value);
				}
				
				if (value instanceof String){
					ps.setString(index++, (String)value);
				}
				
				if (value instanceof Integer){
					ps.setInt(index++, (Integer)value);
				}
				
				if (value instanceof Date){
					ps.setTimestamp(index++, new Timestamp(((Date)value).getTime()));
				}
				
				if (value instanceof Boolean) {
					ps.setBoolean(index++, (Boolean)value);
				}
			}
		}
		
		return ps;
	}

}
