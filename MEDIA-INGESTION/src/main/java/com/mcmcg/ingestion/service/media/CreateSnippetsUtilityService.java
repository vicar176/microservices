/**
 * 
 */
package com.mcmcg.ingestion.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.Response;

/**
 * @author jaleman
 *
 */

@Service
public class CreateSnippetsUtilityService extends BaseService<Boolean> {

	@Value("${mediautility.service.url}")
	private String serviceUrl;
	
	public static final String PUT_CREATE_SNIPPETS = "/document-extractions/snippets?location=%s&bucket=%s";
	
	/**
	 * 
	 */
	public CreateSnippetsUtilityService() {

	}

	@Override
	public String getName() {
		return CreateSnippetsUtilityService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl ;
	}

	@Override
	public ParameterizedTypeReference<Response<Boolean>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<Boolean>>() {};
	}
	
}
