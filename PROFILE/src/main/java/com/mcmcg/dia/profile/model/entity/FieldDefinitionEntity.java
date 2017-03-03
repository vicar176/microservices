package com.mcmcg.dia.profile.model.entity;

import org.springframework.data.annotation.Version;

import com.couchbase.client.java.repository.annotation.Id;
import com.mcmcg.dia.profile.model.FieldDefinitionModel;

public class FieldDefinitionEntity extends FieldDefinitionModel{

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	private String type = FieldDefinitionEntity.class.getSimpleName();
	@Version
	private Long entityVersion;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}
	
	public Long getEntityVersion() {
		return entityVersion;
	}
	
	public void setEntityVersion(Long entityVersion) {
		this.entityVersion = entityVersion;
	}
}
