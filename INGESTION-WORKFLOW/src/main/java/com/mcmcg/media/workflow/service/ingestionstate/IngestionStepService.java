/**
 * 
 */
package com.mcmcg.media.workflow.service.ingestionstate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.mcmcg.media.workflow.service.BaseService;
import com.mcmcg.media.workflow.service.domain.IngestionStep;
import com.mcmcg.media.workflow.service.domain.Response;

/**
 * @author jaleman
 *
 */
@Component
public class IngestionStepService extends BaseService<IngestionStep> {

	@Value("${ingestionstate.service.url}")
	private String serviceUrl;
	
	public static final String GET_LATEST_STEP = "/ingestion-steps/";
	public static final String PUT_STEP = "/ingestion-steps";
	public static final String PUT_STEP_VERSION_BY_ID = "/ingestion-steps/";
	
	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<IngestionStep>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<IngestionStep>>() {};
	}

	@Override
	public String getName() {
		return IngestionStepService.class.getSimpleName();
	}

}
