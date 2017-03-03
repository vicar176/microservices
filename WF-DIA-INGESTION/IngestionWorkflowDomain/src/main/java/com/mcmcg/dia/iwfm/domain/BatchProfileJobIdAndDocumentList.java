package com.mcmcg.dia.iwfm.domain;

import java.util.Set;

public class BatchProfileJobIdAndDocumentList {

	private int batchProfileJobId;
	
	private Set<String> documents;
	
	private String user;
	
	public BatchProfileJobIdAndDocumentList() {
	}

	public int getBatchProfileJobId() {
		return batchProfileJobId;
	}

	public void setBatchProfileJobId(int batchProfileJobId) {
		this.batchProfileJobId = batchProfileJobId;
	}

	public Set<String> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<String> documents) {
		this.documents = documents;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public String getUser() {
		return user;
	}
	
}