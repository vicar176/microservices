/**
 * 
 */
package com.mcmcg.dia.profile.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.couchbase.client.java.document.json.JsonObject;
import com.mcmcg.dia.profile.dao.N1qlQueryDAO;
import com.mcmcg.dia.profile.exception.PersistenceException;
import com.mcmcg.dia.profile.exception.ServiceException;
import com.mcmcg.dia.profile.model.query.CustomQueryBuilder;

/**
 * @author jaleman
 *
 */
public abstract class BaseService {

	@Autowired
	N1qlQueryDAO n1qlQueryDAO;

	/**
	 * 
	 */
	public BaseService() {
		// TODO Auto-generated constructor stub
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
	protected int getTotalCount(String baseQuery, String filter, String groupBy) throws PersistenceException, ServiceException {
		int total = 0;
		List<JsonObject> totalItems = filter(baseQuery, filter, groupBy,  null, 0, 0);
		
		if (totalItems != null && !totalItems.isEmpty()){
			
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
	 * @param parameters
	 * @return List<JsonObject>
	 * @throws PersistenceException
	 * @throws ServiceException
	 */
	protected List<JsonObject> filter(String baseQuery, String filter, String groupBy, String sort, int page, int size,
			String... parameters) throws PersistenceException, ServiceException {

		CustomQueryBuilder builder = new CustomQueryBuilder();
		builder.setBaseQuery(baseQuery).
					setFilter(filter).
					setGroupBy(groupBy).
					setPageNumber(page).
					setPageSize(size).
					setSort(sort);

		return n1qlQueryDAO.query(builder.build(), parameters);
	}
	
	protected List<JsonObject> simpleFilter(String baseQuery, String ... parameters) throws PersistenceException {
		
		CustomQueryBuilder builder = new CustomQueryBuilder();
		builder.setBaseQuery(baseQuery);
		
		return n1qlQueryDAO.simpleQuery(builder.build(), parameters);
	}
	
	/**
	 * @param results
	 * @return
	 */
	protected List<Object> parseJsonObjectToObject(List<JsonObject> results) {
		List<Object> parseResult = null;
		if (results != null && !results.isEmpty()){
			parseResult = new ArrayList<Object>();
			
			for (JsonObject jsonObject: results){
				
				parseResult.add( jsonObject.toMap());
			}
		}
		return parseResult == null || parseResult.isEmpty() ? null : parseResult;
	}
	
	/**
	 * @param versionList
	 * @param version
	 * @return
	 */
	protected List<Long> fillVersionsIntoList(List<Long> versionList, Long version) {
		if (version != null){
			versionList = new LinkedList<Long>();
			//Need to think a better way later
			for (long i = version ; i > 0L; i-- ){
				versionList.add(i);
			}
		}
		return versionList;
	}
}
