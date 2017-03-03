package com.mcmcg.dia.iwfm.domain;

import java.util.List;

/**
 * 
 * @author jaleman
 *
 */
public class FoundAndNotFoundDocumentList  {

	private List<MediaDocument> foundDocumentIds;
	
	private List<String> notFoundDocumentIds;
	
	public FoundAndNotFoundDocumentList() {
		
	}

	public List<MediaDocument> getFoundDocumentIds() {
		return foundDocumentIds;
	}

	public void setFoundDocumentIds(List<MediaDocument> foundDocumentIds) {
		this.foundDocumentIds = foundDocumentIds;
	}

	public List<String> getNotFoundDocumentIds() {
		return notFoundDocumentIds;
	}

	public void setNotFoundDocumentIds(List<String> notFoundDocumentIds) {
		this.notFoundDocumentIds = notFoundDocumentIds;
	}

	
}
