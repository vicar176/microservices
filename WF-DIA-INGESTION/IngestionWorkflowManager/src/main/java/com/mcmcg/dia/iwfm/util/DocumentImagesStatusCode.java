package com.mcmcg.dia.iwfm.util;
/**
 * 
 * @author jaleman
 *
 */
public enum DocumentImagesStatusCode {

	BYPASS("Bypass"),	
	NULL("null"),
	SKIPPED("Skipped"),
	SUCCESS("Success"),
	IGNORE("Ignore"),
	FAILURE("Failure"),
	REPROCESS("Reprocess");
	
	private String value;
	
	private DocumentImagesStatusCode(String value){
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}