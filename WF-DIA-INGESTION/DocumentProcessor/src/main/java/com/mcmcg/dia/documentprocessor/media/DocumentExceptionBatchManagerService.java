package com.mcmcg.dia.documentprocessor.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.iwfm.domain.Response;

@Service
public class DocumentExceptionBatchManagerService extends BaseService<Boolean>{

	@Value("${batchmanager.service.url}")
	private String serviceUrl;

	public static final String POST_DOCUMENT_EXCEPTIONS = "/document-exceptions-batch/";
	
	@Override
	public String getName() {
		return DocumentExceptionBatchManagerService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<Boolean>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<Boolean>>() {
		};
	}

}
