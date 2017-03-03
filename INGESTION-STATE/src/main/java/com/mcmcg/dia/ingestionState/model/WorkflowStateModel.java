package com.mcmcg.dia.ingestionState.model;


public class WorkflowStateModel {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String documentId;
	
	private Boolean forceRerun;
	
	private int rerunNumber;
	
	private String ingestionStateCode;
	
	private String updatedBy;
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public Boolean getForceRerun() {
		return forceRerun;
	}

	public void setForceRerun(Boolean forceRerun) {
		this.forceRerun = forceRerun;
	}

	public int getRerunNumber() {
		return rerunNumber;
	}

	public void setRerunNumber(int rerunNumber) {
		this.rerunNumber = rerunNumber;
	}

	public String getIngestionStateCode() {
		return ingestionStateCode;
	}

	public void setIngestionStateCode(String ingestionStateCode) {
		this.ingestionStateCode = ingestionStateCode;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
}
