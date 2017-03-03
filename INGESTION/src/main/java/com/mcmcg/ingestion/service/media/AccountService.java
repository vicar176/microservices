package com.mcmcg.ingestion.service.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.Account;
import com.mcmcg.ingestion.domain.Response;

/**
 * @author jaleman
 *
 */

@Service
public class AccountService extends BaseService<Account> {

	@Value("${account.service.url}")
	private String serviceUrl;

	public static final String GET_ACCOUNT_BY_NUMBER = "/accounts/";
	public static final String GET_ACCOUNT_BY_NUMBER_WITH_STATEMENT = "/accounts/accountNumber/statement-translation";

	public AccountService() {

	}

	@Override
	public String getName() {
		return AccountService.class.getSimpleName();
	}

	@Override
	public String getEndpoint() {
		return serviceUrl;
	}

	@Override
	public ParameterizedTypeReference<Response<Account>> buildParameterizedTypeReference() {
		return new ParameterizedTypeReference<Response<Account>>() {};
	}
}
