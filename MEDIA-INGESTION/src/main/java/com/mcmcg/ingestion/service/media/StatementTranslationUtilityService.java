package com.mcmcg.ingestion.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.Response;
import com.mcmcg.ingestion.domain.StatementTranslation;

/**
 * 
 * @author wporras
 *
 */

@Service
public class StatementTranslationUtilityService extends BaseService<StatementTranslation> {

	@Value("${mediautility.service.url}")
	private String serviceUrl;

	public static final String PUT_STATEMENT_TRANSLATION = "/statement-translations";

	public StatementTranslationUtilityService() {

	}

	@Override
	public String getName() {
		return StatementTranslationUtilityService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<StatementTranslation>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<StatementTranslation>>() {
		};
	}

}
