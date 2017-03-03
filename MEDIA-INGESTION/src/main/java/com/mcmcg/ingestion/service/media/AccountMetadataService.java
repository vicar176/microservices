package com.mcmcg.ingestion.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.AccountOALDModel;
import com.mcmcg.ingestion.domain.Response;

@Service
public class AccountMetadataService extends BaseService<AccountOALDModel> {

	@Value("${account.metadata.service.url}")
	private String serviceUrl;
	
	public static final String PUT_OR_GET_ACCOUNTMETADATA = "/account-oalds/";
	public static final String PUT_OR_UPDATE_ACCOUNTOALD = "/account-oalds/accountNumber/oalds/mediaOaldId";
	
		
	/**
	 * 
	 */
	public AccountMetadataService() {

	}

	@Override
	public String getName() {
		return AccountMetadataService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl ;
	}

	@Override
	public ParameterizedTypeReference<Response<AccountOALDModel>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<AccountOALDModel>>() {};
	}
}
