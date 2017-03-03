package com.mcmcg.dia.iwfm.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.iwfm.domain.MediaMetadataModel;
import com.mcmcg.dia.iwfm.domain.Response;

/**
 * @author jaleman
 *
 */

@Service
public class ResponseMediaMetadataService extends BaseService<MediaMetadataModel> {

	@Value("${metadata.service.url}")
	private String serviceUrl;

	public static final String GET_MEDIAMETADATA_DOCUMENTS = "/metadatas?filter=";
	public static final String PUT_MEDIAMETADATA = "/metadatas/";

	public ResponseMediaMetadataService() {

	}

	@Override
	public String getName() {
		return ResponseMediaMetadataService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<MediaMetadataModel>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<MediaMetadataModel>>() {
		};
	}
}
