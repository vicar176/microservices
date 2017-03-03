package com.mcmcg.ingestion.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.Response;

/**
 * 
 * @author wporras
 *
 */

@Service
public class S3UtilityService extends BaseService<Object> {

	@Value("${mediautility.service.url}")
	private String serviceUrl;

	public static final String GET_FILE_BY_KEY = "/s3/%s/keys?key=";

	public S3UtilityService() {

	}

	@Override
	public String getName() {
		return S3UtilityService.class.getSimpleName();
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
