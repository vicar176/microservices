package com.mcmcg.ingestion.service.media;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.Response;

@Service
public class DocumentTypePortfolioService extends BaseService<Map<String, Object>>{

	@Value("${portfolio.service.url}")
	private String serviceUrl;

	public static final String GET_DOCUMENTTYPE_BY_CODE = "/document-types/%s";

	public DocumentTypePortfolioService() {

	}

	@Override
	public String getName() {
		return DocumentTypePortfolioService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<Map<String, Object>>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<Map<String, Object>>>() {};
	}	
	
}
