/**
 * 
 */
package com.mcmcg.dia.iwfm.domain;

/**
 * @author jaleman
 *
 */
public class NewDocumentStatus {

	private String status;
	private String updatedBy;
	
	/**
	 * 
	 */
	public NewDocumentStatus() {
		// TODO Auto-generated constructor stub
	}

	
	public NewDocumentStatus(String status, String updatedBy) {
		this.status = status;
		this.updatedBy = updatedBy;
	}


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	
}
