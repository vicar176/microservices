/**
 * 
 */
package com.mcmcg.media.workflow.service.ingestionstate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.mcmcg.media.workflow.service.BaseService;
import com.mcmcg.media.workflow.service.domain.Response;
import com.mcmcg.media.workflow.service.domain.WorkflowState;

/**
 * @author jaleman
 *
 */
@Component
public class WorkflowStateService extends BaseService<WorkflowState> {

	@Value("${ingestionstate.service.url}")
	private String serviceUrl;
	
	public static final String PUT_WORKFLOW_STATES = "/workflow-states";
	public static final String GET_WORKFLOW_STATES = "/workflow-states/";
	public static final String PUT_FORCE_RERUN = "/workflow-states/documentId/force-rerun";
	public static final String PUT_WORKFLOW_STATE_BY_DOCUMENT_ID = "/workflow-states/documentId/workflow-state";
	
	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<WorkflowState>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<WorkflowState>>() {};
	}

	@Override
	public String getName() {
		return WorkflowStateService.class.getSimpleName();
	}

}
