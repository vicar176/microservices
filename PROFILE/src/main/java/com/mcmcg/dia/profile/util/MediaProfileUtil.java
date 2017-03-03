package com.mcmcg.dia.profile.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mcmcg.dia.profile.model.BaseModel;
import com.mcmcg.dia.profile.model.DocumentFieldsDefinitionModel;
import com.mcmcg.dia.profile.model.FieldDefinitionModel;
import com.mcmcg.dia.profile.model.OaldProfileModel;
import com.mcmcg.dia.profile.model.TemplateMappingProfileModel;
import com.mcmcg.dia.profile.model.domain.BaseDomain;
import com.mcmcg.dia.profile.model.domain.DocumentFieldsDefinition;
import com.mcmcg.dia.profile.model.domain.FieldDefinition;
import com.mcmcg.dia.profile.model.domain.OaldProfile;
import com.mcmcg.dia.profile.model.domain.TemplateMappingProfile;
import com.mcmcg.dia.profile.model.entity.BaseEntity;
import com.mcmcg.dia.profile.model.entity.DocumentFieldsDefinitionEntity;
import com.mcmcg.dia.profile.model.entity.FieldDefinitionEntity;
import com.mcmcg.dia.profile.model.entity.OaldProfileEntity;
import com.mcmcg.dia.profile.model.entity.TemplateMappingEntity;

/**
 * 
 * @author Victor Arias
 *
 */
public class MediaProfileUtil {

	public static final String DATE_FORMAT_LONG = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String DATE_FORMAT_SHORT = "MM/dd/yyyy";

	public static OaldProfile buildOaldDomainFromOaldEntity(OaldProfileModel entity)
			throws ParseException, JsonParseException, JsonMappingException, IOException {
		OaldProfile domain = null;
		if (entity != null) {
			domain = new OaldProfile();
			BeanUtils.copyProperties(entity, domain);
		}

		return domain;
	}

	public static OaldProfileEntity buildOaldEntityFromOaldDomain(OaldProfile domain)
			throws JsonProcessingException, ParseException {
		OaldProfileEntity entity = null;
		if (domain != null) {
			entity = new OaldProfileEntity();
			BeanUtils.copyProperties(domain, entity);
		}

		return entity;
	}

	/**
	 * Map the attributes of a TemplateMappingProfile into a
	 * TemplateMappingEntity
	 * 
	 * @param domain
	 * @return TemplateMappingEntity
	 * @throws JsonProcessingException
	 * @throws ParseException 
	 */
	public static TemplateMappingEntity buildTemplateMappingEntityFromTemplateMappingDomain(
			TemplateMappingProfile domain) throws JsonProcessingException, ParseException {

		TemplateMappingEntity entity = null; 
		if (domain != null) {
			entity = new TemplateMappingEntity();
			BeanUtils.copyProperties(domain, entity);
		}

		return entity;
	}

	/**
	 * Map the attributes of a TemplateMappingEntity into a
	 * TemplateMappingProfile
	 * 
	 * @param entity
	 * @return TemplateMappingProfile
	 * @throws ParseException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public static TemplateMappingProfile buildTemplateMappingDomainFromTemplateMappingEntity(
			TemplateMappingProfileModel entity) throws ParseException, JsonParseException, JsonMappingException, IOException {

		TemplateMappingProfile domain = null;
		if(entity != null) {
			domain = new TemplateMappingProfile();
			BeanUtils.copyProperties(entity, domain);
		}
		return domain;
	}
	
	public static DocumentFieldsDefinition buildDocumentFieldDefinitionFromEntity(DocumentFieldsDefinitionModel entity) throws ParseException{
		DocumentFieldsDefinition domain = null;
		
		if(entity != null){
			domain = new DocumentFieldsDefinition();
			BeanUtils.copyProperties(entity, domain);
		}
		
		return domain;
	}
	
	public static DocumentFieldsDefinitionEntity buildDocumentFieldDefinitionEntityFromDomain(DocumentFieldsDefinitionModel domain) throws ParseException{
		DocumentFieldsDefinitionEntity entity = null;
		
		if(domain != null){
			entity = new DocumentFieldsDefinitionEntity();
			BeanUtils.copyProperties(domain, entity);
		}
		
		return entity;
	}
	
	public static FieldDefinition buildFieldDefinitionFromEntity(FieldDefinitionModel entity) throws ParseException{
		FieldDefinition domain = null;
		
		if(entity != null){
			domain = new FieldDefinition();
			BeanUtils.copyProperties(entity, domain);
		}
		
		return domain;
	}
	
	public static FieldDefinitionEntity buildFieldDefinitionEntityFromDomain(FieldDefinitionModel domain) throws ParseException{
		FieldDefinitionEntity entity = null;
		
		if(domain != null){
			entity = new FieldDefinitionEntity();
			BeanUtils.copyProperties(domain, entity);
		}
		
		return entity;
	}

	/**
	 * Map the general attributes of a domain into an entity object
	 * 
	 * @param entity
	 * @param domain
	 */
	public static void setGeneralEntityAttributes(BaseEntity entity, BaseDomain domain) {
		entity.setCreateDate(formatDate(domain.getCreateDate()));
		entity.setUpdateDate(formatDate(domain.getUpdateDate()));
		entity.setUpdatedBy(domain.getUpdatedBy());
	}

	/**
	 * Map the general attributes of an entity into a domain
	 * 
	 * @param entity
	 * @param domain
	 * @throws ParseException
	 */
	public static void setGeneralAttributes(BaseModel domain, BaseModel entity) throws ParseException {
		domain.setCreateDate(entity.getCreateDate());
		domain.setUpdateDate(entity.getUpdateDate());
		domain.setUpdatedBy(entity.getUpdatedBy());
		domain.setVersion(entity.getVersion());
	}

	public static String formatDate(Date date) {
		return getFormater(false).format(date);
	}

	public static String formatDateShort(Date date) {
		return getFormater(true).format(date);
	}

	public static Date parseDate(String date) throws ParseException {
		return getFormater(false).parse(date);
	}

	private static SimpleDateFormat getFormater(boolean isShort) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		SimpleDateFormat formater;
		if (isShort) {
			String dateLong = DATE_FORMAT_SHORT;
			formater = new SimpleDateFormat(dateLong);
		} else {
			formater = new SimpleDateFormat(DATE_FORMAT_LONG);
		}
		formater.setTimeZone(tz);
		return formater;
	}
	
	/**
	 * Convert a Array to a String and truncate it in case it exceed the max length
	 * 
	 * @param arrayList
	 * @param maxLength
	 * @return paramsAsString
	 */
	public static String arrayToTruncateString(Set<String> arrayList, int maxLength) {
		
		String paramsAsString = null;
		
		if(arrayList != null){
		    paramsAsString = arrayList.toString();
		    if(paramsAsString.length() > maxLength){
		        paramsAsString = paramsAsString.substring(0, 100) + " ... ]";
		    }
		}
		
		return paramsAsString;
	}
}
