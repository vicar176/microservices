package com.mcmcg.dia.account.metadata.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.mcmcg.dia.account.metadata.model.entity.AccountOALDEntity;


@Repository("accountOALDDAO")
public interface AccountOALDDAO extends PagingAndSortingRepository<AccountOALDEntity, String> {

	AccountOALDEntity findByAccountNumber(Long accountNumber);
	
}
