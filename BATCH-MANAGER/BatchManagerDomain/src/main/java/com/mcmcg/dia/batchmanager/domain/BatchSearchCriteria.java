/**
 * 
 */
package com.mcmcg.dia.batchmanager.domain;

import java.util.Date;
import java.util.List;

import com.mcmcg.dia.batchmanager.entity.Action;

/**
 * @author pshankar
 *
 */
public class BatchSearchCriteria {

	
	private List<String> documentTypes;
	private List<String> sellers;
	private List<Long> origAccNos;
	private List<Long> portfolios;
	private List<String> affinity;
	private List<String> lenders;
	private Date startuploadDate;
	private Date endUploadDate;

	private String limitResult;

	/**
	 * @return the documentTypes
	 */
	public List<String> getDocumentTypes() {
		return documentTypes;
	}

	/**
	 * @param documentTypes
	 *            the documentTypes to set
	 */
	public void setDocumentTypes(List<String> documentTypes) {
		this.documentTypes = documentTypes;
	}

	/**
	 * @return the sellers
	 */
	public List<String> getSellers() {
		return sellers;
	}

	/**
	 * @param sellers
	 *            the sellers to set
	 */
	public void setSellers(List<String> sellers) {
		this.sellers = sellers;
	}

	/**
	 * @return the origAccNos
	 */
	public List<Long> getOrigAccNos() {
		return origAccNos;
	}

	/**
	 * @param origAccNos
	 *            the origAccNos to set
	 */
	public void setOrigAccNos(List<Long> origAccNos) {
		this.origAccNos = origAccNos;
	}

	/**
	 * @return the portfolios
	 */
	public List<Long> getPortfolios() {
		return portfolios;
	}

	/**
	 * @param portfolios
	 *            the portfolios to set
	 */
	public void setPortfolios(List<Long> portfolios) {
		this.portfolios = portfolios;
	}

	/**
	 * @return the affinity
	 */
	public List<String> getAffinity() {
		return affinity;
	}

	/**
	 * @param affinity
	 *            the affinity to set
	 */
	public void setAffinity(List<String> affinity) {
		this.affinity = affinity;
	}

	/**
	 * @return the lenders
	 */
	public List<String> getLenders() {
		return lenders;
	}

	/**
	 * @param lenders
	 *            the lenders to set
	 */
	public void setLenders(List<String> lenders) {
		this.lenders = lenders;
	}

	/**
	 * @return the startuploadDate
	 */
	public Date getStartuploadDate() {
		return startuploadDate;
	}

	/**
	 * @param startuploadDate
	 *            the startuploadDate to set
	 */
	public void setStartuploadDate(Date startuploadDate) {
		this.startuploadDate = startuploadDate;
	}

	/**
	 * @return the endUploadDate
	 */
	public Date getEndUploadDate() {
		return endUploadDate;
	}

	/**
	 * @param endUploadDate
	 *            the endUploadDate to set
	 */
	public void setEndUploadDate(Date endUploadDate) {
		this.endUploadDate = endUploadDate;
	}

	/**
	 * @return the limitResult
	 */
	public String getLimitResult() {
		return limitResult;
	}

	/**
	 * @param limitResult
	 *            the limitResult to set
	 */
	public void setLimitResult(String limitResult) {
		this.limitResult = limitResult;
	}

}
