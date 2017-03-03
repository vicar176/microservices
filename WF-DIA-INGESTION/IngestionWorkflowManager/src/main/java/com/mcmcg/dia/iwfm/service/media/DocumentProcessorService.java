/**
 * 
 */
package com.mcmcg.dia.iwfm.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.mcmcg.dia.iwfm.domain.Response;
import com.mcmcg.dia.iwfm.service.media.BaseService;

/**
 * @author jaleman
 *
 */
@Component
public class DocumentProcessorService extends BaseService<Object> {

	@Value("${document.processor.service.url}")
	private String serviceUrl;

	public static final String PUT_DOCUMENT_STATUS = "/documents/%s/status";
	public static final String PUT_DOCUMENT_IMAGES_STATUS = "/document-images/documents/%s/status";
	public static final String POST_DOCUMENT_IMAGES_COLLECTION = "/document-images/collection";

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<Object>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<Object>>() {
		};
	}

	@Override
	public String getName() {
		return DocumentProcessorService.class.getSimpleName();
	}

}
