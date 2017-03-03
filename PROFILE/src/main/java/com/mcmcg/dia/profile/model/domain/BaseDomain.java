package com.mcmcg.dia.profile.model.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author Jose Aleman
 *
 */
public class BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Date updateDate;
	protected Date createDate;
	protected String updatedBy;
	protected Long version;

	public BaseDomain() {
		this.createDate = new Date();
		this.updateDate = this.createDate;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public Date getUpdateDate() {
		return new Date(updateDate.getTime());
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = new Date(updateDate.getTime());
	}

	public Date getCreateDate() {
		return new Date(createDate.getTime());
	}

	public void setCreateDate(Date createDate) {
		this.createDate = new Date(createDate.getTime());
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
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
