package com.mcmcg.dia.batchmanager.domain;

import java.util.HashMap;
import java.util.Map;

public enum DocumentStatus {

	New (1,"New"), 
	Success (2, "Success"),
	Failed (3, "Failed"),
	Reprocess (4, "Reprocess"),
	NotFound (4, "NotFound"),
	Exception (5, "Exception");
	
	
	private final int id;
	private final String description;
	
	private final static Map<String, DocumentStatus> DocumentStatusMap = new HashMap<>();
	private final static Map<Integer, DocumentStatus> DocumentStatusIntegerMap = new HashMap<>();
	
	DocumentStatus(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Map<String, DocumentStatus> getMap(){
		
		if (DocumentStatusMap.size() == 0){
			for (DocumentStatus value : DocumentStatus.values()){
				DocumentStatusMap.put(value.getDescription(), value);
			}
		}
		
		return DocumentStatusMap;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Map<Integer, DocumentStatus> getIntegerMap(){
		
		if (DocumentStatusIntegerMap.size() == 0){
			for (DocumentStatus value : DocumentStatus.values()){
				DocumentStatusIntegerMap.put(value.getId(), value);
			}
		}
		
		return DocumentStatusIntegerMap;
	}
}
