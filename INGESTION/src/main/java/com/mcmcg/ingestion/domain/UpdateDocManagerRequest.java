package com.mcmcg.ingestion.domain;

import java.io.Serializable;

public class UpdateDocManagerRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String documentId;
	private String translatedDocumentType;

	public UpdateDocManagerRequest(String documentId, String translatedDocumentType) {
		this.documentId = documentId;
		this.translatedDocumentType = translatedDocumentType;
	}

	public UpdateDocManagerRequest() {

	}

	public String getDocumentId() {
		return documentId;
	}

	public String getTranslatedDocumentType() {
		return translatedDocumentType;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public void setTranslatedDocumentType(String translatedDocumentType) {
		this.translatedDocumentType = translatedDocumentType;
	}

}
