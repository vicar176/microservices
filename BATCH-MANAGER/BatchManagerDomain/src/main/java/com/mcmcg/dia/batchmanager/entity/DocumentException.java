package com.mcmcg.dia.batchmanager.entity;

/**
 * @author Victor Arias
 *
 */
public class DocumentException extends BaseEntity {

	private Long exceptionId;
	private Long batchProfileJobId;
	private String documentId;
	private int status;
	private String errorDescription;

	public Long getBatchProfileJobId() {
		return batchProfileJobId;
	}

	public void setBatchProfileJobId(Long batchProfileJobId) {
		this.batchProfileJobId = batchProfileJobId;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public Long getExceptionId() {
		return exceptionId;
	}

	public void setExceptionId(Long exceptionId) {
		this.exceptionId = exceptionId;
	}

}
