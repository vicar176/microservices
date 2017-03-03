package com.mcmcg.ingestion.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.AccountOALDModel.MediaOald;
import com.mcmcg.ingestion.domain.Response;

@Service
public class MediaOaldService extends BaseService<MediaOald> {

	@Value("${account.metadata.service.url}")
	private String serviceUrl;
	
	public static final String PUT_MEDIA_OALD = "/account-oalds/%d/oalds/%s";
	
	/**
	 * 
	 */
	public MediaOaldService() {

	}

	@Override
	public String getName() {
		return MediaOaldService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl ;
	}

	@Override
	public ParameterizedTypeReference<Response<MediaOald>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<MediaOald>>() {};
	}
}
