package com.mcmcg.dia.batchmanager.entity;

/**
 * @author Victor Arias
 *
 */
public class SearchFilters extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String description;
	private Long searchFilterTypeId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getSearchFilterTypeId() {
		return searchFilterTypeId;
	}

	public void setSearchFilterTypeId(Long searchFilterTypeId) {
		this.searchFilterTypeId = searchFilterTypeId;
	}

}
