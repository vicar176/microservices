/**
 * 
 */
package com.mcmcg.dia.batchmanager.domain;

/**
 * @author pshankar
 *
 */
public class BatchSearchFilter {

	private Long docmentId;
	private String DocType;
	private String accountNumber;
	private Long portfolio;
	private String seller;
	private String lender;
	private String affinity;
	private String TemplateName;

	/**
	 * @return the docmentId
	 */
	public Long getDocmentId() {
		return docmentId;
	}

	/**
	 * @param docmentId
	 *            the docmentId to set
	 */
	public void setDocmentId(Long docmentId) {
		this.docmentId = docmentId;
	}

	/**
	 * @return the docType
	 */
	public String getDocType() {
		return DocType;
	}

	/**
	 * @param docType
	 *            the docType to set
	 */
	public void setDocType(String docType) {
		DocType = docType;
	}

	/**
	 * @return the accountNumber
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @param accountNumber
	 *            the accountNumber to set
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * @return the portfolio
	 */
	public Long getPortfolio() {
		return portfolio;
	}

	/**
	 * @param portfolio
	 *            the portfolio to set
	 */
	public void setPortfolio(Long portfolio) {
		this.portfolio = portfolio;
	}

	/**
	 * @return the seller
	 */
	public String getSeller() {
		return seller;
	}

	/**
	 * @param seller
	 *            the seller to set
	 */
	public void setSeller(String seller) {
		this.seller = seller;
	}

	/**
	 * @return the lender
	 */
	public String getLender() {
		return lender;
	}

	/**
	 * @param lender
	 *            the lender to set
	 */
	public void setLender(String lender) {
		this.lender = lender;
	}

	/**
	 * @return the affinity
	 */
	public String getAffinity() {
		return affinity;
	}

	/**
	 * @param affinity
	 *            the affinity to set
	 */
	public void setAffinity(String affinity) {
		this.affinity = affinity;
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return TemplateName;
	}

	/**
	 * @param templateName
	 *            the templateName to set
	 */
	public void setTemplateName(String templateName) {
		TemplateName = templateName;
	}

}
