/**
 * 
 */
package com.mcmcg.dia.batchmanager.domain;

import java.util.Map;
import java.util.Set;

/**
 * @author pshankar
 *
 */
public class BatchProfileSearchFilterDetail {

	private Long batchProfileId;

	// Search Filter Value
	private Map<SearchFilter, Set<Object>> searchFilterMap;

	/**
	 * @return the batchProfileId
	 */
	public Long getBatchProfileId() {
		return batchProfileId;
	}

	/**
	 * @param batchProfileId
	 *            the batchProfileId to set
	 */
	public void setBatchProfileId(Long batchProfileId) {
		this.batchProfileId = batchProfileId;
	}

	/**
	 * @return the searchFilterMap
	 */
	public Map<SearchFilter, Set<Object>> getSearchFilterMap() {
		return searchFilterMap;
	}

	/**
	 * @param searchFilterMap
	 *            the searchFilterMap to set
	 */
	public void setSearchFilterMap(Map<SearchFilter, Set<Object>> searchFilterMap) {
		this.searchFilterMap = searchFilterMap;
	}

}
