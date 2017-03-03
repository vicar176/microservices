/**
 * 
 */
package com.mcmcg.media.workflow.swf.step;

import org.springframework.stereotype.Component;

import com.mcmcg.media.workflow.common.WorkflowStateCode;

/**
 * @author jaleman
 *
 */
@Component
public class UpdateRerunStatusStep extends UpdateWorkflowStateStep {

	/**
	 * 
	 */
	public UpdateRerunStatusStep() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.mcmcg.media.workflow.ingestion.service.step.BaseStep#getWorkflowStateCode()
	 */
	@Override
	public WorkflowStateCode getWorkflowStateCode() {

		return WorkflowStateCode.UPDATE_RERUN_STATUS;
	}

}
