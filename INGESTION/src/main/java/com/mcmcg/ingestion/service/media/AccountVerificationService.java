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
public class AccountVerificationService extends BaseService<String> {

	@Value("${mediautility.service.url}")
	private String serviceUrl;
	
	public static final String GET_ACCOUNT_VERIFICATION = "/account-verification/";
	
	/**
	 * 
	 */
	public AccountVerificationService() {

	}

	@Override
	public String getName() {
		return AccountVerificationService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl ;
	}

	@Override
	public ParameterizedTypeReference<Response<String>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<String>>() {};
	}
	
}
