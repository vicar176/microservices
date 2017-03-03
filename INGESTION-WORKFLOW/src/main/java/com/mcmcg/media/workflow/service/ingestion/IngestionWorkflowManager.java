/**
 * 
 */
package com.mcmcg.media.workflow.service.ingestion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.mcmcg.media.workflow.service.BaseService;
import com.mcmcg.media.workflow.service.domain.Response;

/**
 * @author jaleman
 *
 */
@Component
public class IngestionWorkflowManager extends BaseService<Object> {

	@Value("${iwm.service.url}")
	private String serviceUrl;

	public static final String POST_DOCUMENT_ID = "/documents/%s/status?batchProfileJobId=%d&env=%s";  ///documents/{documentId}/status
	public static final String GET_PARAM_THREADCOUNT = "/app/parameters/threadcount";  
	public static final String GET_PARAM_WFEXECUTION = "/app/parameters/wfexecution";
	public static final String PUT_PARAM_WFEXECUTION = "/app/parameters/wfexecution";
	public static final String GET_PARAM_CREATE_SNIPPETS = "/app/parameters/createsnippets";
	public static final String GET_PARAM_PDF_TAGGING = "/app/parameters/pdfTagging";
	public static final String GET_PARAM_REPROCESS_ATTEMPTS = "/app/parameters/reprocessAttempts";
	
	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<Object>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<Object>>() {
		};
	}

	@Override
	public String getName() {
		return IngestionWorkflowManager.class.getSimpleName();
	}

}
