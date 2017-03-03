package com.mcmcg.dia.media.metadata.model;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;

public class MediaOaldModel extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	@Field
	private Long documentId;
	@Field
	private String receivedDate;
	@Field
	private String oaldProfileId;
	@Field
	private String documentNameString;
	@Field
	private String originalDocumentType;
	@Field
	private String translatedDocumentType;
	@Field
	private String documentDate;
	@Field
	private long oaldProfileVersion;
	@Field
	private boolean oaldValidated;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getOaldProfileId() {
		return oaldProfileId;
	}

	public void setOaldProfileId(String oaldProfileId) {
		this.oaldProfileId = oaldProfileId;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public String getDocumentNameString() {
		return documentNameString;
	}

	public void setDocumentNameString(String documentNameString) {
		this.documentNameString = documentNameString;
	}

	public String getOriginalDocumentType() {
		return originalDocumentType;
	}

	public void setOriginalDocumentType(String originalDocumentType) {
		this.originalDocumentType = originalDocumentType;
	}

	public String getTranslatedDocumentType() {
		return translatedDocumentType;
	}

	public void setTranslatedDocumentType(String translatedDocumentType) {
		this.translatedDocumentType = translatedDocumentType;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}

	public long getOaldProfileVersion() {
		return oaldProfileVersion;
	}

	public void setOaldProfileVersion(long oaldProfileVersion) {
		this.oaldProfileVersion = oaldProfileVersion;
	}

	public boolean isOaldValidated() {
		return oaldValidated;
	}

	public void setOaldValidated(boolean oaldValidated) {
		this.oaldValidated = oaldValidated;
	}

}
