package com.mcmcg.dia.documentprocessor.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.mcmcg.dia.iwfm.domain.Response;
import com.mcmcg.dia.iwfm.domain.WorkflowShutdownStateModel;

/**
 * @author jaleman
 *
 */
@Component
public class WorkflowShutdownStateService extends BaseService<WorkflowShutdownStateModel> {

	@Value("${ingestionstate.service.url}")
	private String serviceUrl;

	public static final String GET_WORKFLOW_SHUTDOWN_STATE = "/workflow-shutdown";

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<WorkflowShutdownStateModel>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<WorkflowShutdownStateModel>>() {
		};
	}

	@Override
	public String getName() {
		return WorkflowShutdownStateService.class.getSimpleName();
	}

}
