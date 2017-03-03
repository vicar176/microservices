package com.mcmcg.dia.profile.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.mcmcg.dia.profile.util.MediaProfileUtil;

public class BaseModel implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String createDate;
	protected String updateDate;
	protected String updatedBy;
	protected Long version;

	public BaseModel() {
		Date currentDate = new Date();
		createDate = MediaProfileUtil.formatDate(currentDate);
		updateDate = createDate;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
