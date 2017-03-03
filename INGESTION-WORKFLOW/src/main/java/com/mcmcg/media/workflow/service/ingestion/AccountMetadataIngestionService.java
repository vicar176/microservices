/**
 * 
 */
package com.mcmcg.media.workflow.service.ingestion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.mcmcg.media.workflow.service.BaseService;
import com.mcmcg.media.workflow.service.domain.AccountOALDModel;
import com.mcmcg.media.workflow.service.domain.Response;

/**
 * @author jaleman
 *
 */
@Component
public class AccountMetadataIngestionService extends BaseService<AccountOALDModel> {

	@Value("${ingestion.service.url}")
	private String serviceUrl;

	public static final String PUT_RECEIVE = "/receives/";

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<AccountOALDModel>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<AccountOALDModel>>() {
		};
	}

	@Override
	public String getName() {
		return AccountMetadataIngestionService.class.getSimpleName();
	}

}
