package com.mcmcg.dia.documentprocessor.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.iwfm.domain.Response;

/**
 * 
 * @author wporras
 *
 */

@Service
public class S3UtilityService extends BaseService<Boolean> {

	@Value("${mediautility.service.url}")
	private String serviceUrl;

	public static final String POST_FILE_BY_KEY = "/s3/%s/objects?key=%s";

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
	public ParameterizedTypeReference<Response<Boolean>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<Boolean>>() {};
	}

}
