package com.mcmcg.dia.batchmanager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.batchmanager.dao.BatchProfileDAO;
import com.mcmcg.dia.batchmanager.domain.BatchProfileSearchFilterDetail;
import com.mcmcg.dia.batchmanager.domain.BatchProfileWithAction;
import com.mcmcg.dia.batchmanager.domain.SearchFilter;
import com.mcmcg.dia.batchmanager.entity.BatchProfile;
import com.mcmcg.dia.batchmanager.entity.BatchProfileJob;
import com.mcmcg.dia.batchmanager.entity.SearchFiltersBatchProfile;
import com.mcmcg.dia.batchmanager.exception.ServiceException;


@Service
public class BatchProfileService extends BaseService{

	@Autowired
	@Qualifier("batchProfileDAO")
	private BatchProfileDAO batchProfileDAO;

	private static final Logger LOG = Logger.getLogger(BatchProfileService.class);

	/**
	 * @param entity
	 * @return
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	public BatchProfile save(BatchProfile entity) throws ServiceException, PersistenceException {
		try {
			if (entity != null) {
				long key =	batchProfileDAO.save(entity);
				entity.setBatchProfileId(key);
				
			} else {
				String errorMsj = "Unable to save a null value";
				ServiceException se = new ServiceException(errorMsj);
				LOG.error(errorMsj, se);
				throw se;
			}
		} catch (PersistenceException pe) {
			LOG.error(pe.getMessage(), pe);
			throw pe;
		} catch (ServiceException se) { 
			LOG.error(se.getMessage(), se);
			throw se;
		} catch (Throwable t) {
			String errorMsj = (!StringUtils.isBlank(t.getMessage()) ? t.getMessage() : "An error occured on save method");
			LOG.error(errorMsj, t);
			throw new ServiceException(errorMsj, t);
		}

		return entity;
	}

	
	
	/**
	 * @param batchProfileId
	 * @return
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	public BatchProfileSearchFilterDetail getSearchFiltersByBatchProfileId(Long batchProfileId) throws ServiceException, PersistenceException {
		List<SearchFiltersBatchProfile> searchFiltersBatchProfileList = new ArrayList<SearchFiltersBatchProfile>();
		// This is the DOMAIN Object Which We need to Return
		BatchProfileSearchFilterDetail batchProfileSearchFilterDetail = new BatchProfileSearchFilterDetail();
		try{
			if(batchProfileId>0){
				//Fetching the Selected FilterEntity List for BatchProfileId
				searchFiltersBatchProfileList=batchProfileDAO.getSearchFiltersByBatchProfileId(batchProfileId);
				
				
				//Populating the Search FilterBatchPrfile begin
				batchProfileSearchFilterDetail.setBatchProfileId(batchProfileId);
				
				//Initializing the Selected SearchFilters
				Map<SearchFilter,Set<Object>> searchFilterSelectedMap = new HashMap<>();
				
				//Fetch all the SearchFilters  based on Id this is to populate the Searchfilter object based on Id
				Map<Long, SearchFilter> searchFilterMap = retriveSearchFilters();
				
				//For Each Batch Profile We will Have Lister Of Filter Id and Values for it
				for(SearchFiltersBatchProfile searchFiltersBatchProfile:searchFiltersBatchProfileList){
					SearchFilter searchFilter = new SearchFilter();
					// Getting SearchFilter Object 
					searchFilter= searchFilterMap.get(searchFiltersBatchProfile.getSearchFilterId());
					//Fetch The Search Filter Objects
					Set<Object> searchFilterValueSet = new HashSet<Object>();
					if(searchFilterSelectedMap.containsKey(searchFilter)){
						//Getting the existing valueSet for Search filter
						 searchFilterValueSet = searchFilterSelectedMap.get(searchFilter);
						 //Adding the New Value
						searchFilterValueSet.add(searchFiltersBatchProfile.getValue());
						//Putting it in Map not req
						searchFilterSelectedMap.put(searchFilter, searchFilterValueSet);
						
						
					}else{
						//Adding the Value
						searchFilterValueSet.add(searchFiltersBatchProfile.getValue());
						//Returning the Vaue
						searchFilterSelectedMap.put(searchFilter, searchFilterValueSet);
					}
					
					
					
				}
				
				batchProfileSearchFilterDetail.setSearchFilterMap(searchFilterSelectedMap);
				
				
				
				
			}else{
				
				String errorMsj = "Unable to fetch record for this BatchProfile";
				ServiceException se = new ServiceException(errorMsj);
				LOG.error(errorMsj, se);
				throw se;	
			}
			
			
			
		}catch (PersistenceException pe) {
			LOG.error(pe.getMessage(), pe);
			throw pe;
		} catch (ServiceException se) { 
			LOG.error(se.getMessage(), se);
			throw se;
		} catch (Throwable t) {
			String errorMsj = (!StringUtils.isBlank(t.getMessage()) ? t.getMessage() : "An error occured on save method");
			LOG.error(errorMsj, t);
			throw new ServiceException(errorMsj, t);
		}

		
		return batchProfileSearchFilterDetail;
		
	}

	/**
	 * @return
	 * @throws PersistenceException
	 */
	public List<BatchProfileWithAction> retriveBatchProfileWithAction()throws PersistenceException
	{
		
		List<BatchProfileWithAction> batchProfileList = null;
		batchProfileList =batchProfileDAO.retriveBatchProfileWithAction();
		
		return batchProfileList;
	}
	
	/**
	 * @return
	 */
	private Map<Long, SearchFilter> retriveSearchFilters(){
		
		Map<Long, SearchFilter> results = new HashMap<Long, SearchFilter>();
		return results=batchProfileDAO.retriveSearchFilters();
	}
	
	

	
}
