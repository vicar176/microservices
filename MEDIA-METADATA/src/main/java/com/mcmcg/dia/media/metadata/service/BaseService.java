package com.mcmcg.dia.media.metadata.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.couchbase.client.java.document.json.JsonObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcmcg.dia.media.metadata.component.EncryptComponent;
import com.mcmcg.dia.media.metadata.dao.N1qlQueryDAO;
import com.mcmcg.dia.media.metadata.exception.PersistenceException;
import com.mcmcg.dia.media.metadata.exception.ServiceException;
import com.mcmcg.dia.media.metadata.model.query.CustomQueryBuilder;

/**
 * @author jaleman
 *
 */
public class BaseService {

	@Autowired
	protected N1qlQueryDAO n1qlQueryDAO;

	@Autowired
	protected EncryptComponent encryptComponent;

	public BaseService() {
	}

	/**
	 * 
	 * @param filter
	 * @param groupBy
	 * @return
	 * @throws PersistenceException
	 * @throws ServiceException
	 */
	protected int getTotalCount(String filter, String groupBy) throws PersistenceException, ServiceException {

		return getTotalCount(N1qlQueryDAO.GET_COUNT, filter, groupBy);

	}

	/**
	 * 
	 * @param baseQuery
	 * @param filter
	 * @param groupBy
	 * @return
	 * @throws PersistenceException
	 * @throws ServiceException
	 */
	protected int getTotalCount(String baseQuery, String filter, String groupBy)
			throws PersistenceException, ServiceException {
		int total = 0;
		List<JsonObject> totalItems = filter(baseQuery, filter, groupBy, null, 0, 0);

		if (totalItems != null && !totalItems.isEmpty()) {

			total = totalItems.size() == 1 ? totalItems.get(0).getInt("total") : totalItems.size();
		}

		return total;
	}

	/**
	 * 
	 * @param baseQuery
	 * @param filter
	 * @param groupBy
	 * @param sort
	 * @param page
	 * @param size
	 * @return
	 * @throws PersistenceException
	 * @throws ServiceException
	 */
	protected List<JsonObject> filter(String baseQuery, String filter, String groupBy, String sort, int page, int size)
			throws PersistenceException, ServiceException {

		CustomQueryBuilder builder = new CustomQueryBuilder();
		builder.setBaseQuery(baseQuery).setFilter(filter).setGroupBy(groupBy).setPageNumber(page).setPageSize(size)
				.setSort(sort);

		return n1qlQueryDAO.query(builder.build());
	}

	protected List<JsonObject> simpleFilter(String baseQuery, String... parameters) throws PersistenceException {

		CustomQueryBuilder builder = new CustomQueryBuilder();
		builder.setBaseQuery(baseQuery);

		return n1qlQueryDAO.simpleQuery(builder.build(), parameters);
	}

	/**
	 * @param results
	 * @return
	 */
	protected List<Object> parseJsonObjectToObject(String key, List<JsonObject> results) {
		List<Object> parseResult = null;
		if (results != null && !results.isEmpty()) {
			parseResult = new ArrayList<Object>();

			for (JsonObject jsonObject : results) {
				if (StringUtils.isNotBlank(key)) {
					Map<String, Object> map = ((JsonObject) jsonObject.get(key)).toMap();
					Object id = jsonObject.get("id");
					if (id != null) {
						map.put("id", id);
					}
					parseResult.add(map);
				} else {
					parseResult.add(jsonObject.toMap());
				}
			}
		}
		return parseResult == null || parseResult.isEmpty() ? null : parseResult;
	}

	/**
	 * 
	 * @param results
	 * @return
	 */
	protected List<Object> parseJsonObjectToObject(List<JsonObject> results) {

		return parseJsonObjectToObject(null, results);
	}

	/**
	 * Parses a Json Object to Entity Objects and decrypts the content
	 * 
	 * @param results
	 * @param entityType
	 * @return parseResult
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	protected <T> List<Object> parseJsonObjectToEntity(List<JsonObject> results, Class<T> entityType)
			throws JsonParseException, JsonMappingException, IOException {

		List<Object> parseResult = null;

		if (results != null && !results.isEmpty()) {
			parseResult = new ArrayList<Object>();
			ObjectMapper mapper = new ObjectMapper();

			for (JsonObject jsonObject : results) {
				Object entityObject = mapper.readValue(jsonObject.toString(), entityType);
				encryptComponent.decrypt(entityObject);
				parseResult.add(entityObject);
			}
		}
		return parseResult == null || parseResult.isEmpty() ? null : parseResult;
	}
}
