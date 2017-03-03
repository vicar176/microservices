/**
 * 
 */
package com.mcmcg.ingestion.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.AccountOALDModel.MediaOald;
import com.mcmcg.ingestion.domain.Response;

/**
 * @author averm12
 *
 */

@Service
public class MediaMetadataOaldService extends BaseService<MediaOald> {

	@Value("${metadata.oald.service.url}")
	private String serviceUrl;
	
	public static final String PUT_OR_GET_OALD_MEDIAMETADATA = "/media-oalds/";
		
	/**
	 * 
	 */
	public MediaMetadataOaldService() {

	}

	@Override
	public String getName() {
		return MediaMetadataOaldService.class.getSimpleName();
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
