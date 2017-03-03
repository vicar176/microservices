/**
 * 
 */
package com.mcmcg.dia.media.metadata.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlParams;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.consistency.ScanConsistency;
import com.mcmcg.dia.media.metadata.annotation.Diagnostics;
import com.mcmcg.dia.media.metadata.exception.PersistenceException;
import com.mcmcg.dia.media.metadata.model.query.CustomQuery;

/**
 * @author Jose Aleman
 *
 */
@Repository
public class N1qlQueryDAO {

	private static final String IS = "IS";

	private static final String LIST = "List";

	private static final Logger LOG = Logger.getLogger(N1qlQueryDAO.class);

	@Resource(name = "queriesMap")
	private Map<String, String> queriesMap;

	@Autowired
	private Bucket couchbaseBucket;

	public static final String GET_ALL_METADATA = "GET_ALL_METADATA";
	public static final String GET_PORTFOLIO_SUMMARY = "GET_PORTFOLIO_SUMMARY";
	public static final String GET_METADATA_WITH_TEMPLATE_NOT_EXTRACTED = "GET_METADATA_WITH_TEMPLATE_NOT_EXTRACTED";
	public static final String GET_METADATA_WITHOUT_TEMPLATE_NOT_EXTRACTED = "GET_METADATA_WITHOUT_TEMPLATE_NOT_EXTRACTED";
	public static final String GET_FAILED_DOCUMENTS_BY_TEMPLATE_NAME = "GET_FAILED_DOCUMENTS_BY_TEMPLATE_NAME";
	public static final String GET_COUNT = "GET_COUNT";
	public static final String GET_COUNT_TEMPLATES_FOUND = "GET_COUNT_TEMPLATES_FOUND";
	public static final String GET_COUNT_TEMPLATES_NOT_FOUND = "GET_COUNT_TEMPLATES_NOT_FOUND";
	public static final String GET_PORTFOLIO_IDS = "GET_PORTFOLIO_IDS";
	public static final String GET_COUNT_PORTFOLIOS_FOUND = "GET_COUNT_PORTFOLIOS_FOUND";

	//
	/**
	 * 
	 */
	public N1qlQueryDAO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param query
	 * @param whereClauseMap
	 * @return
	 */
	public List<JsonObject> query(CustomQuery customQuery) throws PersistenceException {

		List<JsonObject> results = null;

		try {

			StringBuilder queryBuilder = buildQueryWithFilters(customQuery);

			results = executeQuery(queryBuilder);
		} catch (Throwable e) {
			throw new PersistenceException(e.getMessage(), e);
		}
		return results;
	}

	public List<JsonObject> simpleQuery(CustomQuery customQuery, String... parameters) throws PersistenceException {

		List<JsonObject> results = null;

		try {
			StringBuilder queryBuilder = buildSimpleQuery(customQuery, parameters);
			results = executeQuery(queryBuilder);
		} catch (Throwable e) {
			throw new PersistenceException(e.getMessage(), e);
		}

		return results;
	}

	/**
	 * @param results
	 * @param queryBuilder
	 */
	@Diagnostics(area = "Couchbase-NQL1-Reads")
	public List<JsonObject> executeQuery(StringBuilder queryBuilder) {
		N1qlParams ryow = N1qlParams.build().consistency(ScanConsistency.STATEMENT_PLUS);
		N1qlQuery n1qlQuery = N1qlQuery.simple(queryBuilder.toString(), ryow);
		N1qlQueryResult result = couchbaseBucket.query(n1qlQuery);
		List<JsonObject> results = null;
		if (result != null && result.finalSuccess()) {
			results = new ArrayList<JsonObject>();
			for (N1qlQueryRow row : result) {
				results.add(row.value());
				LOG.debug(row.value());
			}

		}
		return results;
	}

	/**
	 * @param query
	 * @param whereClauseMap
	 * @return
	 */
	private StringBuilder buildQueryWithFilters(CustomQuery customQuery) {

		StringBuilder queryBuilder = new StringBuilder();

		if (queriesMap.get(customQuery.getBaseQuery()) != null) {
			queryBuilder.append(queriesMap.get(customQuery.getBaseQuery())).append(" ");
		}

		buildWhereClause(customQuery, queryBuilder);

		buildGroupByClause(customQuery, queryBuilder);

		buildOrderByClause(customQuery, queryBuilder);

		buildLimitAndOffset(customQuery, queryBuilder);

		LOG.debug(queryBuilder.toString());

		return queryBuilder;

	}

	private StringBuilder buildSimpleQuery(CustomQuery customQuery, String... parameters) throws PersistenceException {

		StringBuilder queryBuilder = new StringBuilder();
		String baseQuery = StringUtils.EMPTY;

		if (queriesMap.get(customQuery.getBaseQuery()) != null) {
			baseQuery = queriesMap.get(customQuery.getBaseQuery());
		}

		if (baseQuery.contains("?")) {
			int index = baseQuery.indexOf("?");
			queryBuilder.append(baseQuery.substring(0, index));
			String subStr = baseQuery.substring(index);
			String[] tokens = subStr.split("\\?");
			if (tokens.length == parameters.length + 1) {
				for (int i = 0; i < parameters.length; i++) {
					queryBuilder.append(parameters[i]);
					queryBuilder.append(tokens[i + 1]);
				}
			} else {
				String errorMsj = "Difference between the number of parameters in the query";
				PersistenceException pe = new PersistenceException(errorMsj);
				LOG.error(errorMsj, pe);
				throw pe;
			}
		}

		LOG.debug(queryBuilder.toString());

		return queryBuilder;
	}

	/**
	 * @param customQuery
	 * @param queryBuilder
	 */
	private void buildLimitAndOffset(CustomQuery customQuery, StringBuilder queryBuilder) {
		if (customQuery.getLimit() != null && customQuery.getOffset() != null && customQuery.getLimit() > 0
				&& customQuery.getOffset() >= 0) {
			queryBuilder.append(" ").append("LIMIT ").append(customQuery.getLimit()).append(" ").append("OFFSET ")
					.append(customQuery.getOffset());
		}
	}

	/**
	 * @param customQuery
	 * @param queryBuilder
	 */
	private void buildOrderByClause(CustomQuery customQuery, StringBuilder queryBuilder) {
		if (StringUtils.isNotBlank(customQuery.getSortBy())) {
			String tokens [] = customQuery.getSortBy().split(",");
			queryBuilder.append(" ").append(" ORDER BY ");
			int length = tokens.length;
			for (String token : tokens){
				
				if (token.startsWith("-")){
					queryBuilder.append(token.substring(1)).
								 append(" DESC");
				}else{
					queryBuilder.append(token).
					 			 append(" ASC");
				}
				
				if (--length > 0) {
					queryBuilder.append(", ");
				}
			}

		}
	}

	/**
	 * @param customQuery
	 * @param queryBuilder
	 */
	private void buildGroupByClause(CustomQuery customQuery, StringBuilder queryBuilder) {
		if (StringUtils.isNotBlank(customQuery.getGroupBy())) {

			queryBuilder.append(" ").append(" GROUP BY ").append(customQuery.getGroupBy());

		}
	}

	/**
	 * @param customQuery
	 * @param queryBuilder
	 */
	private void buildWhereClause(CustomQuery customQuery, StringBuilder queryBuilder) {
		if (customQuery.getWhereClauseMap() != null && customQuery.getWhereClauseMap().size() > 0) {
			int count = customQuery.getWhereClauseMap().size();
			queryBuilder.append(" AND ");
			for (String key : customQuery.getWhereClauseMap().keySet()) {

				String value = customQuery.getWhereClauseMap().get(key).toString();
				if (StringUtils.endsWith(key, LIST)) {

					count = buildINClause(customQuery, queryBuilder, key, count);

				} else if (StringUtils.startsWith(value, IS)) {

					count = buildISClause(customQuery, queryBuilder, key, count);

				} else if (StringUtils.endsWith(key, "Map")) {

					count = buildANYClause(queryBuilder, key, value, count);

				} else if (StringUtils.endsWith(key, "Contains")) {

					count = buildContainsClause(queryBuilder, key, value, count);

				} else if (StringUtils.contains(value, "@")) {

					count = buildLIKEClause(queryBuilder, key, value, count);

				} else {

					count = buildPropertyInWhereClause(customQuery, queryBuilder, count, key);

				}

			}

		}
	}

	/**
	 * @param customQuery
	 * @param queryBuilder
	 * @param key
	 */
	private int buildContainsClause(StringBuilder queryBuilder, String key, String value, int count) {

		queryBuilder.append("ARRAY_CONTAINS( ").append(StringUtils.remove(key, "Contains")).append(", ").append(value)
				.append(")");

		if (--count > 0) {
			queryBuilder.append(" AND ");
		}

		return count;
	}

	/**
	 * @param customQuery
	 * @param queryBuilder
	 * @param key
	 */
	private int buildANYClause(StringBuilder queryBuilder, String key, String value, int count) {
		String tokens[] = key.split("[.]");
		if (tokens.length > 1) {
			tokens[1] = StringUtils.remove(tokens[1], "Map");
			String newValue = StringUtils.replace(value.trim(), "@", "%");
			queryBuilder.append("ANY child in ").append(tokens[0]).append(" SATISFIES ").append("UPPER(child.")
					.append(tokens[1].trim()).append(")").append(" like").append(newValue.toUpperCase())
					.append(" END ");
		} else {
			String newKey = StringUtils.remove(key, "Map");
			String newValue = StringUtils.replace(value.trim(), "@", "%");
			queryBuilder.append("ANY child in ").append(newKey).append(" SATISFIES ").append("UPPER(child)")
					.append(" like").append(newValue.toUpperCase()).append(" END ");
		}

		if (--count > 0) {
			queryBuilder.append(" AND ");
		}

		return count;
	}

	/**
	 * @param customQuery
	 * @param queryBuilder
	 * @param key
	 */
	private int buildLIKEClause(StringBuilder queryBuilder, String key, String value, int count) {
		queryBuilder.append("UPPER(").append(key).append(") LIKE ").
		 			 append(StringUtils.replace(value.trim().toUpperCase(), "@", "%"));

		if (--count > 0){
			queryBuilder.append(" AND ");
		}
		
		return count;

	}

	/**
	 * @param customQuery
	 * @param queryBuilder
	 * @param key
	 */
	private int buildISClause(CustomQuery customQuery, StringBuilder queryBuilder, String key, int count) {
		queryBuilder.append("(").
					 append(key).append(" ").append(customQuery.getWhereClauseMap().get(key)).
					 append(" OR ").
					 append("TYPE(").append(key).append(") = \"missing\"").
					 append(")");

		if (--count > 0) {
			queryBuilder.append(" AND ");
		}

		return count;
	}

	/**
	 * @param customQuery
	 * @param queryBuilder
	 * @param count
	 * @param key
	 * @return
	 */
	private int buildPropertyInWhereClause(CustomQuery customQuery, StringBuilder queryBuilder, int count, String key) {
		queryBuilder.append(key).append("=");

		Object value = customQuery.getWhereClauseMap().get(key);
		queryBuilder.append(value);

		if (--count > 0) {
			queryBuilder.append(" AND ");
		}
		return count;
	}

	/**
	 * @param customQuery
	 * @param queryBuilder
	 * @param key
	 */
	private int buildINClause(CustomQuery customQuery, StringBuilder queryBuilder, String key, int count) {
		String newKey = StringUtils.remove(key, LIST);
		String values[] = customQuery.getWhereClauseMap().get(key).toString().split(",");
		queryBuilder.append("(");
		int size = values.length;
		for (String value : values) {
			queryBuilder.append(newKey).append("=").append(value.trim());

			if (--size > 0) {
				queryBuilder.append(" OR ");
			}
		}
		queryBuilder.append(")");

		if (--count > 0) {
			queryBuilder.append(" AND ");
		}
		return count;
	}

}
