package com.mcmcg.dia.documentprocessor.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.iwfm.domain.Response;

@Service
public class OnDemandBatchManagerService extends BaseService<Long>{

	@Value("${batchmanager.service.url}")
	private String serviceUrl;

	public static final String PUT_BATCH_PROFILE_ON_DEMAND = "/batch-profiles/on-demand";
	
	@Override
	public String getName() {
		return OnDemandBatchManagerService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<Long>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<Long>>() {
		};
	}

}
