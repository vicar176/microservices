package com.mcmcg.dia.profile.model.entity;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Id;
import com.mcmcg.dia.profile.model.FieldDefinitionModel;
import com.mcmcg.dia.profile.model.domain.FieldDefinition;

/**
 * 
 * @author Victor Arias
 *
 */

@Document(expiry = 0)
public class FieldDefinitionHistoryEntity extends FieldDefinitionModel{

	private static final long serialVersionUID = 1L;

	private String type = FieldDefinitionHistoryEntity.class.getSimpleName();
	
	@Version
	private Long entityVersion;
	@Id
	private String id;
	
	public FieldDefinitionHistoryEntity (){
		
	}
	
	public FieldDefinitionHistoryEntity(FieldDefinition entity){
		BeanUtils.copyProperties(entity, this);
		this.setId(entity.getId() + "_" + entity.getVersion());
	}
	

	
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
