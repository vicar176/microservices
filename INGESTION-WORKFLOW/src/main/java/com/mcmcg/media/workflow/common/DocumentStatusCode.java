/**
 * 
 */
package com.mcmcg.media.workflow.common;

/**
 * @author jaleman
 *
 */
public enum DocumentStatusCode {

	NEW("new",1),	
	SUCCESS("success",2),
	FAILED("failed",3),
	REPROCESS("reprocess",4);
	
	private String value;
	private int code;
	
	private DocumentStatusCode(String value, int code){
		this.value = value;
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
