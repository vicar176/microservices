package com.mcmcg.dia.account.metadata.util;

import java.text.ParseException;

import org.springframework.beans.BeanUtils;

import com.mcmcg.dia.account.metadata.model.BaseModel;
import com.mcmcg.dia.account.metadata.model.domain.AccountOALD;
import com.mcmcg.dia.account.metadata.model.entity.AccountOALDEntity;


/**
 * 
 * @author Victor Arias
 *
 */
public class MetadataConvertUtil {

	public static AccountOALDEntity convertAccountOaldToEntity(AccountOALD domain){
		AccountOALDEntity entity = null;
		if(domain != null){
			entity = new AccountOALDEntity();
			BeanUtils.copyProperties(domain, entity);
		}
		
		return entity;
	}
	
	public static AccountOALD convertAccountOALDEntityToDomain(AccountOALDEntity entity) throws ParseException{
		AccountOALD domain = null;
		if(entity != null){
			domain = new AccountOALD();
			BeanUtils.copyProperties(entity, domain);
		}
		
		return domain;
	}

}
