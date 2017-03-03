/**
 * 
 */
package com.mcmcg.media.workflow.common;

/**
 * @author jaleman
 *
 */
public enum StepStatusCode {
	
	SUCCESS("Success"),
	FAILED("Failed"),
	SKIP("Skip");
	
	private String value;
	
	private StepStatusCode(String value){
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
}
