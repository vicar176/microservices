package com.mcmcg.dia.profile.model.entity;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Version;

import com.mcmcg.dia.profile.model.TemplateMappingProfileModel;

/**
 * 
 * @author Victor Arias
 *
 */

public class TemplateMappingHistoryEntity extends TemplateMappingProfileModel {
	
	private static final long serialVersionUID = 1L;
	
	private String type = TemplateMappingHistoryEntity.class.getSimpleName();
	
    @Version
	private Long entityVersion;

	public TemplateMappingHistoryEntity(){
		
	}
	
	public TemplateMappingHistoryEntity(TemplateMappingProfileModel entity){
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
