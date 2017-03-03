/**
 * 
 */
package com.mcmcg.dia.iwfm.service;

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
public class DocumentProcessor extends BaseService<Boolean> {

	@Value("${document.processor.service.url}")
	private String serviceUrl;

	public static final String PUT_DOCUMENT_STATUS = "/documents/%s/status";

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<Boolean>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<Boolean>>() {
		};
	}

	@Override
	public String getName() {
		return DocumentProcessor.class.getSimpleName();
	}

}
