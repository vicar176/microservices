package com.mcmcg.dia.batchmanager.domain;

import com.mcmcg.dia.batchmanager.entity.Action;

/**
 * @author Victor Arias
 *
 */
public class BatchCreationOnDemand {

	private Action action;
	private String creationMethod;
	private String csvFileName;
	private String name;
	private String description;
	private Long totalOfDocuments;
	
	private String user;

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public String getCreationMethod() {
		return creationMethod;
	}

	public void setCreationMethod(String creationMethod) {
		this.creationMethod = creationMethod;
	}

	public String getCsvFileName() {
		return csvFileName;
	}

	public void setCsvFileName(String csvFileName) {
		this.csvFileName = csvFileName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getTotalOfDocuments() {
		return totalOfDocuments;
	}

	public void setTotalOfDocuments(Long totalOfDocuments) {
		this.totalOfDocuments = totalOfDocuments;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
