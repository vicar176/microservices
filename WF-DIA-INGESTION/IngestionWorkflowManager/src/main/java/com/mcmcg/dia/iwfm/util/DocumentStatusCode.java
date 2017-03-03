package com.mcmcg.dia.iwfm.util;
/**
 * 
 * @author jaleman
 *
 */
public enum DocumentStatusCode {

	NEW("New"),	
	SUCCESS("Success"),
	FAILED("Failed"),
	REPROCESS("Reprocess");
	
	private String value;
	
	private DocumentStatusCode(String value){
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}

}