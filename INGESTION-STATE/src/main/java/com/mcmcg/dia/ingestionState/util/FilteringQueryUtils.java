package com.mcmcg.dia.ingestionState.util;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.mcmcg.dia.ingestionState.exception.PersistenceException;

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
			String[] tokens = filter.split("\\|");
			for (String token : tokens) {
				String[] parameters = token.split("=");
				if (filterValues.containsKey(parameters[0])) {
					clauseBuilder.append(" AND ");
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
		return String.format(" LIMIT %s, %s", page - 1, pageSize);
	}

}
