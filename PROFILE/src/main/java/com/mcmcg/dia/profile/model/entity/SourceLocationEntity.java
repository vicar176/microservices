package com.mcmcg.dia.profile.model.entity;


/**
 * 
 * @author Victor Arias
 *
 */

public class SourceLocationEntity{

	private String id;
	private String name;
	private String location;
	private Long version;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	
}
