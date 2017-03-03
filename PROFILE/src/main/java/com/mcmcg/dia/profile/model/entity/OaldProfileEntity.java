package com.mcmcg.dia.profile.model.entity;

import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.mapping.Document;

import com.mcmcg.dia.profile.model.OaldProfileModel;


/**
 * 
 * @author Victor Arias
 *
 */

@Document(expiry = 0)
public class OaldProfileEntity extends OaldProfileModel {

	private static final long serialVersionUID = 1L;

	private String type = OaldProfileEntity.class.getSimpleName();
	
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
