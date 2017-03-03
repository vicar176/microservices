/**
 * 
 */
package com.mcmcg.media.workflow.swf.step;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.mcmcg.media.workflow.common.StepStatusCode;
import com.mcmcg.media.workflow.common.WorkflowStateCode;
import com.mcmcg.media.workflow.service.domain.BaseDomain;
import com.mcmcg.media.workflow.service.domain.Response;
import com.mcmcg.media.workflow.service.exception.MediaServiceException;

/**
 * @author jaleman
 *
 */
@Component
public class UpdateWorkflowStateStep extends BaseStep<BaseDomain> {

	/**
	 * 
	 */
	public UpdateWorkflowStateStep() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Response<BaseDomain> execute(Map<String, Object> contextMap) throws MediaServiceException{
		
		Response<BaseDomain> response = new Response<BaseDomain>();
		
		response.setError(new Response.Error());
		response.getError().setCode(200);
		response.getError().setMessage(StepStatusCode.SUCCESS.toString());
		
		return response;
	}

	/* (non-Javadoc)
	 * @see com.mcmcg.media.workflow.ingestion.service.step.BaseStep#getWorkflowStateCode()
	 */
	@Override
	public WorkflowStateCode getWorkflowStateCode() {

		return WorkflowStateCode.UPDATE_WORKFLOW_STATE;
	}

}
