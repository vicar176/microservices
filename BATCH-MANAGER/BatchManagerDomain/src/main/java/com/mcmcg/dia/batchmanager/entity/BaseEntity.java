package com.mcmcg.dia.batchmanager.entity;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mcmcg.dia.batchmanager.util.CustomDateDeserializer;
import com.mcmcg.dia.batchmanager.util.CustomDateSerializer;

public class BaseEntity {

	private Date createdOnDate;
	private Date changedOnDate;
	private String changedBy;
	private String createdBy;
	private String workstationName;
	private Integer applicationId;

	public BaseEntity() {
	}

	public BaseEntity(String changedBy) {
		super();
		this.changedBy = changedBy;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	public Date getCreatedOnDate() {
		return createdOnDate;
	}

	@JsonDeserialize(using = CustomDateDeserializer.class)
	public void setCreatedOnDate(Date createdOnDate) {
		this.createdOnDate = createdOnDate;
	}

	@JsonSerialize(using = CustomDateSerializer.class)
	public Date getChangedOnDate() {
		return changedOnDate;
	}

	@JsonDeserialize(using = CustomDateDeserializer.class)
	public void setChangedOnDate(Date changedOnDate) {
		this.changedOnDate = changedOnDate;
	}

	public String getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getWorkstationName() {
		return workstationName;
	}

	public void setWorkstationName(String workstationName) {
		this.workstationName = workstationName;
	}

	public Integer getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}
