package com.mcmcg.dia.profile.model.entity;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.mapping.Document;

import com.mcmcg.dia.profile.model.OaldProfileModel;

/**
 * 
 * @author Victor Arias
 *
 */

@Document(expiry = 0)
public class OaldProfileHistoryEntity extends OaldProfileModel {

	private static final long serialVersionUID = 1L;
	
	private String type = OaldProfileHistoryEntity.class.getSimpleName();
	
	@Version
	private Long entityVersion;

	public OaldProfileHistoryEntity(){
		
	}
	
	public OaldProfileHistoryEntity(OaldProfileModel entity){
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
