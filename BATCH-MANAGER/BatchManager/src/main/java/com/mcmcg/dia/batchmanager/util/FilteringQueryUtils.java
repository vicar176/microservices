package com.mcmcg.dia.batchmanager.util;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.mcmcg.dia.batchmanager.exception.PersistenceException;

/**
 * @author Victor Arias
 *
 */

public class FilteringQueryUtils {
	
	private static final Logger LOG = Logger.getLogger(FilteringQueryUtils.class);

	public static String buildSortingClause(String sort, Map<String, String> filterValues) {
		StringBuilder sortingClause = new StringBuilder();
		String field = sort.replace("-", "");
		if (filterValues.containsKey(field)) {
			String direction = " ASC ";
			if (StringUtils.contains(sort, "-")) {
				direction = " DESC ";
			}
			sortingClause.append(" ORDER BY ").append(filterValues.get(field)).append(direction);
		}
		return sortingClause.toString();
	}

	public static String buildFilterClause(String filter, Map<String, String> filterValues) throws PersistenceException {
		StringBuilder clauseBuilder = new StringBuilder();
		if (StringUtils.isNotBlank(filter)) {
			filter = filter.replaceAll("\"", StringUtils.EMPTY);
			String[] tokens = filter.split("\\|");
			for (String token : tokens) {
				String[] parameters = token.split("=");
				if (filterValues.containsKey(parameters[0])) {
					if (!StringUtils.contains(clauseBuilder,"WHERE")) {
						clauseBuilder.append(" WHERE ");
					} else {
						clauseBuilder.append(" AND ");
					}
					clauseBuilder.append(
							String.format(" %s LIKE \"%%%s%%\" ", filterValues.get(parameters[0]), parameters[1]));
				} else {
					String message = "The column to filter does not exists!";
					PersistenceException pe = new PersistenceException(message);
					LOG.error(message, pe);
					throw pe;
				}
			}
		}
		
		return clauseBuilder.toString();
	}
	
	public static String buildLimitClause(int page, int pageSize) {
		
			if (page > 0 || pageSize > 0){
				if(page>0)
				return String.format(" LIMIT %s, %s", (page * pageSize)-pageSize, pageSize);
				else
				return String.format(" LIMIT %s, %s", 0, pageSize);	
						//return (page * size) - size;
					}
		
			return "";
	}

}
