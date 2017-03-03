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
public class MediaMetadataModelService extends BaseService<MediaMetadataModel> {

	@Value("${metadata.service.url}")
	private String serviceUrl;

	public static final String PUT_OR_GET_MEDIAMETADATA = "/metadatas/";
	public static final String PUT_DATA_ELEMENTS_VALIDATES = "/metadatas/documentId/data-elements/validates";
	public static final String PUT_AUTO_VALIDATION = "/metadatas/documentId/auto-validations";
	public static final String PUT_STATEMENT_TRANSLATION = "/metadatas/documentId/statement-translation";
	public static final String GET_METADATA = "/metadatas/documentId";
	public static final String GET_BY_ACCOUNTNUMBER_DOCUMENTTYPE_DOCUMENTDATE = "/metadatas?accountNumber=%s&originalDocumentType=%s&documentDate=%s";

	public MediaMetadataModelService() {

	}

	@Override
	public String getName() {
		return MediaMetadataModelService.class.getSimpleName();
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
