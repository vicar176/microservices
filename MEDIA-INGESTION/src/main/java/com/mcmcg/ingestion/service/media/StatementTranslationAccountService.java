package com.mcmcg.ingestion.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.Response;

/**
 * @author jaleman
 *
 */

@Service
public class StatementTranslationAccountService extends BaseService<Boolean> {

	@Value("${account.service.url}")
	private String serviceUrl;

	public static final String PUT_TRANSLARTED_DOC_TYPE_BY_ACCOUNTNUMBER = "/accounts/%s/documents/%s/translation";
	

	public StatementTranslationAccountService() {

	}

	@Override
	public String getName() {
		return StatementTranslationAccountService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<Boolean>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<Boolean>>() {};
	}
}
