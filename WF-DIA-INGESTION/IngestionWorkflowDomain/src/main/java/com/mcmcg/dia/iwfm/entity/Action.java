package com.mcmcg.dia.iwfm.entity;

/**
 * @author Victor Arias
 *
 */
public class Action extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String description;
	
	public Action(Long id, String description) {
		super();
		this.id = id;
		this.description = description;
	}

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

}
