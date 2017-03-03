package com.mcmcg.ingestion.service.media;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mcmcg.ingestion.BaseApplicationTest;
import com.mcmcg.ingestion.domain.Account;
import com.mcmcg.ingestion.domain.Response;
import com.mcmcg.ingestion.exception.IngestionServiceException;
import com.mcmcg.ingestion.exception.MediaServiceException;

public class AccountServiceTest extends BaseApplicationTest{

	@Autowired
	ServiceLocator serviceLocator;
	
	public AccountServiceTest() {
		
	}
	
	@Ignore
	@Test
	public void testAccountService() throws MediaServiceException, IngestionServiceException{
		
		IService<Account> service = serviceLocator.getService(ServiceLocator.ACCOUNT_SERVICE_NAME);
		
		Response<Account> response = service.execute(AccountService.GET_ACCOUNT_BY_NUMBER + "1",  IService.GET);
		
		System.out.println(response);
		
	}
}
