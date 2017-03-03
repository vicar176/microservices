/**
 * 
 */
package com.mcmcg.dia.iwfm.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.mcmcg.dia.iwfm.domain.FoundAndNotFoundDocumentList;
import com.mcmcg.dia.iwfm.domain.Response;
import com.mcmcg.dia.iwfm.service.media.BaseService;

/**
 * @author jaleman
 *
 */
@Component
public class DocumentIdListDocumentProcessorService extends BaseService<FoundAndNotFoundDocumentList> {

	@Value("${document.processor.service.url}")
	private String serviceUrl;

	public static final String POST_DOCUMENT_IMAGES_COLLECTION = "/document-images/collection";

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<FoundAndNotFoundDocumentList>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<FoundAndNotFoundDocumentList>>() {
		};
	}

	@Override
	public String getName() {
		return DocumentIdListDocumentProcessorService.class.getSimpleName();
	}

}
