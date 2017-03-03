package com.mcmcg.ingestion.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.ProductGroup;
import com.mcmcg.ingestion.domain.Response;

@Service
public class PortfolioService extends BaseService<ProductGroup>{

	@Value("${portfolio.service.url}")
	private String serviceUrl;

	public static final String GET_PRODUCTGROUP_BY_TYPE = "/product-group/";

	public PortfolioService() {

	}

	@Override
	public String getName() {
		return PortfolioService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<ProductGroup>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<ProductGroup>>() {};
	}	
	
}
