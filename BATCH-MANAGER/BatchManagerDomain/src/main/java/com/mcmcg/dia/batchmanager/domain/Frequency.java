/**
 * 
 */
package com.mcmcg.dia.batchmanager.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pshankar
 *
 */
public enum Frequency {
	
	Daily (1,"Daily"), 
	Monthly (2, "Monthly"),
	Weekly (3, "Weekly"),
	Once(0,"once");
	
	Frequency(int id, String description) {
		this.id = id;
		this.description = description;
	}
	private final int id;
	private final String description;
	
	private final static Map<String, Frequency> FrequencyMap = new HashMap<>();
	private final static Map<Integer, Frequency> FrequencyIdMap = new HashMap<>();
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Map<String, Frequency> getMap(){
		
		if (FrequencyMap.size() == 0){
			for (Frequency value : Frequency.values()){
				FrequencyMap.put(value.getDescription(), value);
			}
		}
		
		return FrequencyMap;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Map<Integer, Frequency> getIntegerMap(){
		
		if (FrequencyIdMap.size() == 0){
			for (Frequency value : Frequency.values()){
				FrequencyIdMap.put(value.getId(), value);
			}
		}
		
		return FrequencyIdMap;
	}

}
