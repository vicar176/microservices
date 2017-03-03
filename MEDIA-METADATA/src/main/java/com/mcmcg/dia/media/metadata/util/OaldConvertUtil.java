package com.mcmcg.dia.media.metadata.util;

import java.text.ParseException;

import org.springframework.beans.BeanUtils;

import com.mcmcg.dia.media.metadata.model.BaseModel;
import com.mcmcg.dia.media.metadata.model.domain.MediaOald;
import com.mcmcg.dia.media.metadata.model.entity.MediaOALDEntity;


/**
 * 
 * @author Victor Arias
 *
 */
public class OaldConvertUtil {

	public static MediaOALDEntity convertMediaOaldToEntity(MediaOald domain){
		MediaOALDEntity entity = null;
		if(domain != null){
			entity = new MediaOALDEntity();
			BeanUtils.copyProperties(domain, entity);
		}
		
		return entity;
	}
	
	public static MediaOald convertMediaOaldEntityToDomain(MediaOALDEntity entity) throws ParseException{
		MediaOald domain = null;
		if(entity != null){
			domain = new MediaOald();
			BeanUtils.copyProperties(entity, domain);
		}
		
		return domain;
	}
	
	public static void setGeneralAttributes(BaseModel to, BaseModel from) {
		to.setCreateDate(from.getCreateDate());
		to.setUpdateDate(from.getUpdateDate());
		to.setVersion(from.getVersion());
		to.setUpdatedBy(from.getUpdatedBy());
	}

}
