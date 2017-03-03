/**
 * 
 */
package com.mcmcg.ingestion.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.OaldProfile;
import com.mcmcg.ingestion.domain.Response;

/**
 * @author averm12
 *
 */

@Service
public class OaldProfileService extends BaseService<OaldProfile> {

	@Value("${oaldprofile.service.url}")
	private String serviceUrl;
	
	public static final String GET_PRODUCTGROUP_PORTFOLIOID_ORIGINALLENDER= "/oald-profiles?";
	
	/**
	 * 
	 */
	public OaldProfileService() {
		
	}

	/**
	 * 
	 */
	@Override
	public String getName() {
		return OaldProfileService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<OaldProfile>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<OaldProfile>>() {};
	}
}
