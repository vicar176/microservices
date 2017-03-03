package com.mcmcg.dia.media.metadata.model.entity;

import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.mapping.Document;

import com.mcmcg.dia.media.metadata.annotation.Encrypt;
import com.mcmcg.dia.media.metadata.model.MediaMetadataModel;

/**
 * 
 * @author Victor Arias
 *
 */
@Encrypt(fields = { "document.documentName.originalAccountNumber" }, 
		dataElements = { "name", "account", "creditCard", "routing", "SSN", "social", "security" })
@Document(expiry = 0)
public class MediaMetadataEntity extends MediaMetadataModel {

	private static final long serialVersionUID = 1L;

	private String type = MediaMetadataEntity.class.getSimpleName();
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
