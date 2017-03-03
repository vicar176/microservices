package com.mcmcg.media.workflow.service.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class IngestionStep {

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
	
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * @return the updateDate
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
	}
}
