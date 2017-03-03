/**
 * 
 */
package com.mcmcg.dia.profile.model.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author Jose Aleman
 *
 */
public abstract class BaseEntity {

	protected Long version;
	protected String updateDate;
	protected String createDate;
	protected String updatedBy;
	/**
	 * 
	 */

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	
	public String getUpdateDate() {
		return updateDate;
	}


	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}


	public String getCreateDate() {
		return createDate;
	}


	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}


	public String getUpdatedBy() {
		return updatedBy;
	}


	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
}
