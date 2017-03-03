package com.mcmcg.dia.iwfm.service.media;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.iwfm.domain.Response;

@Service
public class IngestionStepService extends BaseService<Object>{

	@Value("${ingestionstep.service.url}")
	private String serviceUrl;

	public static final String GET_FAILED_DOCUMENTS = "/ingestion-steps/failed/documentIds";

	
	@Override
	public String getName() {
		return IngestionStepService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<Object>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<Object>>() {
		};
	}

}
