package com.mcmcg.dia.iwfm.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.iwfm.domain.Response;

@Service
public class BatchManagerService extends BaseService<Boolean>{

	@Value("${batchmanager.service.url}")
	private String serviceUrl;

	public static final String PUT_BATCH_PROFILE_JOBS = "/batch-profile-jobs?documentId=%s&batchProfileJobId=%d&status=%s&updatedBy=%s";
	public static final String POST_DOCUMENT_EXCEPTIONS = "/document-exceptions";
	
	@Override
	public String getName() {
		return BatchManagerService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return String.format("https://everest%s.mcmcg.com" + serviceUrl, getEnv());
	}

	@Override
	public ParameterizedTypeReference<Response<Boolean>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<Boolean>>() {
		};
	}
	
}
