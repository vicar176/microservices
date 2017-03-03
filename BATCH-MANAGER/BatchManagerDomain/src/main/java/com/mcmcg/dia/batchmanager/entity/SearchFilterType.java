package com.mcmcg.dia.batchmanager.entity;

/**
 * @author Victor Arias
 *
 */
public class SearchFilterType extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String code;
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
