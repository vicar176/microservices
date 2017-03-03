/**
 * 
 */
package com.mcmcg.ingestion.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.MediaMetadataModel;
import com.mcmcg.ingestion.domain.Response;

/**
 * @author jaleman
 *
 */

@Service
public class PdfTaggingUtilityService extends BaseService<MediaMetadataModel> {

	@Value("${mediautility.service.url}")
	private String serviceUrl;
	
	public static final String PUT_TAG_METADATA = "/tag-pdfs/%s?bucket=%s";
	
	/**
	 * 
	 */
	public PdfTaggingUtilityService() {

	}

	@Override
	public String getName() {
		return PdfTaggingUtilityService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl ;
	}

	@Override
	public ParameterizedTypeReference<Response<MediaMetadataModel>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<MediaMetadataModel>>() {};
	}
	
}
