package com.mcmcg.dia.ingestionState.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mcmcg.dia.ingestionState.util.CustomDateSerializer;

@JsonAutoDetect
public class IngestionStepModel {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private Long workflowStateId;
	
	private int rerunNumber;
	
	private String description;
	
	private String statusCode;
	
	private String ingestionStateCode;
	
	private String updatedBy;
	
	private Date createDate;
	
	private Date updateDate;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWorkflowStateId() {
		return workflowStateId;
	}

	public void setWorkflowStateId(Long workflowStateId) {
		this.workflowStateId = workflowStateId;
	}

	public int getRerunNumber() {
		return rerunNumber;
	}

	public void setRerunNumber(int rerunNumber) {
		this.rerunNumber = rerunNumber;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getIngestionStateCode() {
		return ingestionStateCode;
	}

	public void setIngestionStateCode(String ingestionStateCode) {
		this.ingestionStateCode = ingestionStateCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	@JsonSerialize(using = CustomDateSerializer.class)
	public Date getCreateDate() {
		return createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
}
