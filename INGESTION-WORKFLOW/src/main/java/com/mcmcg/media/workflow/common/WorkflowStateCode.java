/**
 * 
 */
package com.mcmcg.media.workflow.common;

/**
 * @author jaleman
 *
 */
public enum WorkflowStateCode {

	NEW("new"),	
	EXTRACTION("extraction"),
	UPDATE_WORKFLOW_STATE("update-workflowstate"),
	STATEMENT_TRANSLATION("statement-translation"),
	UPDATE_RERUN_STATUS("update-rerun-status"),
	PDF_TAGGING("pdf-tagging"),
	RECEIVE("receive"),
	AUTO_VALIDATION("auto-validation");
	
	private String value;
	
	private WorkflowStateCode(String value){
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
