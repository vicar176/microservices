package com.mcmcg.dia.batchscheduler.service.batchmanager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.batchmanager.domain.BatchProfileSearchFilterDetail;
import com.mcmcg.dia.batchmanager.domain.BatchProfileWithAction;
import com.mcmcg.dia.batchmanager.domain.Response;


@Service
public class BatchProfileSearchFilterService extends BaseService<BatchProfileSearchFilterDetail>{

	@Value("${batchmanager.service.url}")
	private String serviceUrl;
	
	public static final String GET_BATCHPROFILE_WITH_SEARCH_FILTER = "/batch-profiles/{batchProfileId}/search-filters";
	
	/**
	 * 
	 */
	public BatchProfileSearchFilterService() {

	}

	@Override
	public String getName() {
		return BatchProfileService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl ;
	}

	@Override
	public ParameterizedTypeReference<Response<BatchProfileSearchFilterDetail>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<BatchProfileSearchFilterDetail>>() {};
	}
}
