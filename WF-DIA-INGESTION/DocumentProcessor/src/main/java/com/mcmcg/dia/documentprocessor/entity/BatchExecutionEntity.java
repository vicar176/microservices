package com.mcmcg.dia.documentprocessor.entity;

import java.util.Date;

/**
 * 
 * @author wporras
 *
 */
public class BatchExecutionEntity extends BaseModel {

	private Long batchExecutionId;
	private Date executionDate;
	private String executionStatusCode;

	public BatchExecutionEntity() {
		super();
	}

	public BatchExecutionEntity(Long batchExecutionId, Date executionDate, String executionStatusCode) {
		super();
		this.batchExecutionId = batchExecutionId;
		this.executionDate = executionDate;
		this.executionStatusCode = executionStatusCode;
	}

	public Long getBatchExecutionId() {
		return batchExecutionId;
	}

	public void setBatchExecutionId(Long batchExecutionId) {
		this.batchExecutionId = batchExecutionId;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	public String getExecutionStatusCode() {
		return executionStatusCode;
	}

	public void setExecutionStatusCode(String executionStatusCode) {
		this.executionStatusCode = executionStatusCode;
	}

}
