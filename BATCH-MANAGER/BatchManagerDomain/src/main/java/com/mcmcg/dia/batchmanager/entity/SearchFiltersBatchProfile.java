package com.mcmcg.dia.batchmanager.entity;

/**
 * @author Victor Arias
 *
 */
public class SearchFiltersBatchProfile extends BaseEntity {

	private Long batchProfileId;
	private Long searchFilterId;
	private String value;
	private Long version;

	public Long getBatchProfileId() {
		return batchProfileId;
	}

	public void setBatchProfileId(Long batchProfileId) {
		this.batchProfileId = batchProfileId;
	}

	public Long getSearchFilterId() {
		return searchFilterId;
	}

	public void setSearchFilterId(Long searchFilterId) {
		this.searchFilterId = searchFilterId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	
}
