package com.mcmcg.dia.media.metadata.model.entity;


import org.springframework.data.annotation.Version;

import com.mcmcg.dia.media.metadata.model.MediaOaldModel;

/**
 * 
 * @author Victor Arias
 *
 */

public class MediaOALDEntity extends MediaOaldModel {
	
	private static final long serialVersionUID = 1L;
	
	@Version
	private Long versionEntity;

	public Long getVersionEntity() {
		return versionEntity;
	}

	public void setVersionEntity(Long versionEntity) {
		this.versionEntity = versionEntity;
	}

	private String type = MediaOALDEntity.class.getSimpleName();
	
	public String getType() {
		return type;
	}

}
