/**
 * 
 */
package com.mcmcg.media.workflow.service.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.mcmcg.media.workflow.util.WorkflowUtil;

/**
 * @author jaleman
 *
 */
public class BaseDomain implements Serializable {

	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;


	protected Long version;
	protected String updateDate;
	protected String createDate;
	protected String updatedBy;
	
	public BaseDomain() {
		Date currentDate = new Date();
		createDate = WorkflowUtil.formatDate(currentDate);
		updateDate = createDate;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
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
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}

