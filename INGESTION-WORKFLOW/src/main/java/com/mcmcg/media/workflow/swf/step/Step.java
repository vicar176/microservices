/**
 * 
 */
package com.mcmcg.media.workflow.swf.step;

import java.util.Map;

import com.mcmcg.media.workflow.common.WorkflowStateCode;
import com.mcmcg.media.workflow.service.domain.Response;
import com.mcmcg.media.workflow.service.exception.MediaServiceException;

/**
 * @author jaleman
 *
 */
public interface Step<T> {

	public Response<T> execute(Map<String, Object> contextMap) throws MediaServiceException;
	
	public String getName();
	
	public WorkflowStateCode getWorkflowStateCode();
	
}
