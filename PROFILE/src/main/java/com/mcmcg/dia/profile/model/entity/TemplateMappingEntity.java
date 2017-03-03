package com.mcmcg.dia.profile.model.entity;

import org.springframework.data.annotation.Version;

import com.mcmcg.dia.profile.model.TemplateMappingProfileModel;


public class TemplateMappingEntity extends TemplateMappingProfileModel{
	
	private static final long serialVersionUID = 1L;

	private String type = TemplateMappingEntity.class.getSimpleName();
	
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
