/**
 * 
 */
package com.mcmcg.dia.batchmanager.domain;

import java.util.List;

import com.mcmcg.dia.batchmanager.entity.Action;
import com.mcmcg.dia.batchmanager.entity.BatchProfile;

/**
 * @author pshankar
 *
 */
public class BatchProfileScheduled {
	
	private BatchProfile batchProfile;
	private Action action;
	private String creationMethod;
	private SearchFilter batchSearchFilter;
	private BatchSearchCriteria batchSearchCriteria;
	private List<SearchFilter> listFilteredValues;
    
	/**
	 * @return the listFilteredValues
	 */
	public List<SearchFilter> getListFilteredValues() {
		return listFilteredValues;
	}
	/**
	 * @param listFilteredValues the listFilteredValues to set
	 */
	public void setListFilteredValues(List<SearchFilter> listFilteredValues) {
		this.listFilteredValues = listFilteredValues;
	}
	/**
	 * @return the batchProfile
	 */
	public BatchProfile getBatchProfile() {
		return batchProfile;
	}
	/**
	 * @param batchProfile the batchProfile to set
	 */
	public void setBatchProfile(BatchProfile batchProfile) {
		this.batchProfile = batchProfile;
	}
	/**
	 * @return the action
	 */
	public Action getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(Action action) {
		this.action = action;
	}
	/**
	 * @return the creationMethod
	 */
	public String getCreationMethod() {
		return creationMethod;
	}
	/**
	 * @param creationMethod the creationMethod to set
	 */
	public void setCreationMethod(String creationMethod) {
		this.creationMethod = creationMethod;
	}
	/**
	 * @return the batchSearchFilter
	 */
	public SearchFilter getBatchSearchFilter() {
		return batchSearchFilter;
	}
	/**
	 * @param batchSearchFilter the batchSearchFilter to set
	 */
	public void setBatchSearchFilter(SearchFilter batchSearchFilter) {
		this.batchSearchFilter = batchSearchFilter;
	}
	/**
	 * @return the batchSearchCriteria
	 */
	public BatchSearchCriteria getBatchSearchCriteria() {
		return batchSearchCriteria;
	}
	/**
	 * @param batchSearchCriteria the batchSearchCriteria to set
	 */
	public void setBatchSearchCriteria(BatchSearchCriteria batchSearchCriteria) {
		this.batchSearchCriteria = batchSearchCriteria;
	}

	

}
