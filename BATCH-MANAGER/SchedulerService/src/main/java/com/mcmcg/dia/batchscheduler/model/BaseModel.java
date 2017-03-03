package com.mcmcg.dia.batchscheduler.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class BaseModel implements Serializable{

	private static final long serialVersionUID = 1L;

	protected Date createDate;
	protected Date updateDate;
	protected String updatedBy;

	public BaseModel() {
		Date currentDate = new Date();
		createDate = currentDate;
		updateDate = currentDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
