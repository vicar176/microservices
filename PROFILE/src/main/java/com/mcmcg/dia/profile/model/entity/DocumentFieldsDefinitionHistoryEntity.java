package com.mcmcg.dia.profile.model.entity;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Version;

import com.mcmcg.dia.profile.model.DocumentFieldsDefinitionModel;

public class DocumentFieldsDefinitionHistoryEntity extends DocumentFieldsDefinitionModel {

	private static final long serialVersionUID = 1L;

	private String type = DocumentFieldsDefinitionHistoryEntity.class.getSimpleName();
	@Version
	private Long entityVersion;
	
	public DocumentFieldsDefinitionHistoryEntity() {
		
	}
	
	public DocumentFieldsDefinitionHistoryEntity(DocumentFieldsDefinitionModel entity){
		BeanUtils.copyProperties(entity, this);
		this.setId(entity.getId() + "_" + entity.getVersion());
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
