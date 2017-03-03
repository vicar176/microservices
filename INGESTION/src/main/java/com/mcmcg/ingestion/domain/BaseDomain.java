package com.mcmcg.ingestion.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.mcmcg.ingestion.util.IngestionUtils;

/**
 * @author Jose Aleman
 *
 */
public class BaseDomain implements Serializable {

	
	protected static final long serialVersionUID = 1L;


	protected Long version;
	protected String updateDate;
	protected String createDate;
	protected String updatedBy;
	
	public BaseDomain() {
//		this.createDate = new Date();
//		this.updateDate = this.createDate;
		Date currentDate = new Date();
		createDate = IngestionUtils.formatDate(currentDate);
		updateDate = createDate;
	}
	
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
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
