package com.mcmcg.dia.profile.model.entity;

import org.springframework.data.annotation.Version;

import com.mcmcg.dia.profile.model.DocumentFieldsDefinitionModel;

public class DocumentFieldsDefinitionEntity extends DocumentFieldsDefinitionModel {

	private static final long serialVersionUID = 1L;

	private String type = DocumentFieldsDefinitionEntity.class.getSimpleName();
	@Version
	private Long entityVersion;
	
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
