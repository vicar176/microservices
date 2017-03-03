package com.mcmcg.dia.batchscheduler.service.batchmanager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.batchmanager.domain.Response;
import com.mcmcg.dia.batchmanager.entity.BatchProfileJob;

@Service
public class BatchProfileJobService extends BaseService<BatchProfileJob> {
	
	@Value("${batchmanager.service.url}")
	private String serviceUrl;
	
	public static final String POST_BACTH_PROFILE_JOB = "/batch-profile-jobs";
	
	/**
	 * 
	 */
	public BatchProfileJobService() {

	}

	@Override
	public String getName() {
		return BatchProfileJobService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl ;
	}

	@Override
	public ParameterizedTypeReference<Response<BatchProfileJob>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<BatchProfileJob>>() {};
	}
}
