package com.mcmcg.dia.batchscheduler.service.batchmanager;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.batchmanager.domain.BatchProfileWithAction;
import com.mcmcg.dia.batchmanager.domain.Response;


@Service
public class BatchProfileService extends BaseService<List<BatchProfileWithAction>>{

	@Value("${batchmanager.service.url}")
	private String serviceUrl;
	
	public static final String GET_BATCHPROFILE_WITH_ACTION = "/batch-profiles";
	
	/**
	 * 
	 */
	public BatchProfileService() {

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
	public ParameterizedTypeReference<Response<List<BatchProfileWithAction>>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<List<BatchProfileWithAction>>>() {};
	}
}
