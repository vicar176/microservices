package com.mcmcg.dia.batchmanager.domain;

/**
 * @author Victor Arias
 *
 */
public enum JobStatus {
	
	processCompleted (1,"Process Completed"), 
	currentlyProcessing (2, "Currently processing"),
	processFailed (3, "Process failed"),
	waitingToBeProcessed (4, "Waiting to be processed"),
	noDocumentsOrPlacementsFound (5, "No documents or placements found");
	
	private final int id;
	private final String description;
	
	JobStatus(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
	
}
