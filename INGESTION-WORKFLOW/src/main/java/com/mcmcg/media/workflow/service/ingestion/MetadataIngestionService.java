/**
 * 
 */
package com.mcmcg.media.workflow.service.ingestion;

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
public class MetadataIngestionService extends BaseService<MediaMetadataModel> {

	@Value("${ingestion.service.url}")
	private String serviceUrl;

	public static final String PUT_EXTRACTIONS = "/extractions/";
	public static final String PUT_AUTO_VALIDATIONS = "/auto-validations/";
	public static final String PUT_PDF_TAGGING = "/tag-pdfs/";
	public static final String PUT_STATEMENT_TRANSLATION = "/statement-translations/";

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<MediaMetadataModel>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<MediaMetadataModel>>() {
		};
	}

	@Override
	public String getName() {
		return MetadataIngestionService.class.getSimpleName();
	}

}
