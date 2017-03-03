/**
 * 
 */
package com.mcmcg.media.workflow.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.mcmcg.media.workflow.service.BaseService;
import com.mcmcg.media.workflow.service.domain.MediaMetadataModel;
import com.mcmcg.media.workflow.service.domain.Response;

/**
 * @author jaleman
 *
 */
@Component
public class MediaMetadataService extends BaseService<MediaMetadataModel> {

	@Value("${metadata.service.url}")
	private String serviceUrl;
	
	public static final String GET_MEDIAMETADATA = "/metadatas/";
	
	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<MediaMetadataModel>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<MediaMetadataModel>>() {};
	}

	@Override
	public String getName() {
		return MediaMetadataService.class.getSimpleName();
	}

}
