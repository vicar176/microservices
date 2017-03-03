package com.mcmcg.dia.iwfm.domain;

/**
 * 
 * @author wporras
 *
 */
public class ExecutionStatus {

	private Long executionStatusId;
	private String executionStatusCode;
	private String executionStatusDescription;
	private String createdBy;
	private String changedBy;

	public ExecutionStatus(Long executionStatusId, String executionStatusCode, String executionStatusDescription,
			String createdBy, String changedBy) {
		super();
		this.executionStatusId = executionStatusId;
		this.executionStatusCode = executionStatusCode;
		this.executionStatusDescription = executionStatusDescription;
		this.createdBy = createdBy;
		this.changedBy = changedBy;
	}

	public ExecutionStatus(String executionStatusCode, String changedBy) {
		super();
		this.executionStatusCode = executionStatusCode;
		this.changedBy = changedBy;
	}

	public Long getExecutionStatusId() {
		return executionStatusId;
	}

	public void setExecutionStatusId(Long executionStatusId) {
		this.executionStatusId = executionStatusId;
	}

	public String getExecutionStatusCode() {
		return executionStatusCode;
	}

	public void setExecutionStatusCode(String executionStatusCode) {
		this.executionStatusCode = executionStatusCode;
	}

	public String getExecutionStatusDescription() {
		return executionStatusDescription;
	}

	public void setExecutionStatusDescription(String executionStatusDescription) {
		this.executionStatusDescription = executionStatusDescription;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

}
