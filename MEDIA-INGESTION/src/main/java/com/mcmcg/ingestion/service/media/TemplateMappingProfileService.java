/**
 * 
 */
package com.mcmcg.ingestion.service.media;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.Response;
import com.mcmcg.ingestion.domain.TemplateMappingProfileModel;

/**
 * @author jaleman
 *
 */

@Service
public class TemplateMappingProfileService extends BaseService<List<TemplateMappingProfileModel>> {

	@Value("${templatemapping.service.url}")
	private String serviceUrl;
	
	public static final String GET_DOCUMENTTYPE_SELLER_ORIGINALLENDER= "/template-mapping-profiles?";
	
	/**
	 * 
	 */
	public TemplateMappingProfileService() {
		
	}

	/**
	 * 
	 */
	@Override
	public String getName() {
		return TemplateMappingProfileService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<List<TemplateMappingProfileModel>>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<List<TemplateMappingProfileModel>>>() {};
	}
}
