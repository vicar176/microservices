package com.mcmcg.dia.iwfm.domain;

import java.util.Set;

/**
 * 
 * @author jaleman
 *
 */
public class DocumentList {

	private Set<String> documentIds;
	
	public DocumentList() {
		
	}

	public void setDocumentIds(Set<String> documentIds) {
		this.documentIds = documentIds;
	}
	
	public Set<String> getDocumentIds() {
		return documentIds;
	}
}
