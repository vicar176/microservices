package com.mcmcg.dia.iwfm.service.media;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.iwfm.domain.PagedResponse;
import com.mcmcg.dia.iwfm.domain.Response;

/**
 * @author jaleman
 *
 */

@Service
public class MediaMetadataDocumentService extends BaseService<PagedResponse<Map<String, Object>>> {

	@Value("${metadata.service.url}")
	private String serviceUrl;

	public static final String GET_MEDIAMETADATA_DOCUMENTS = "/metadatas/portfolios/%d/documents?filter=documentStatus=%s|extraction.templateMappingProfile.name=%s";
	public static final String GET_MEDIAMETADATA_DOCUMENT_TYPES = "/metadatas/portfolios/%d/templatesNotFound?filter=documentStatusList=%s|originalDocumentType.code=%s";

	public MediaMetadataDocumentService() {

	}

	@Override
	public String getName() {
		return MediaMetadataDocumentService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<PagedResponse<Map<String, Object>>>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<PagedResponse<Map<String, Object>>>>() {
		};
	}
}
