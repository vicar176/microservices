package com.mcmcg.dia.media.metadata.util;

import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.couchbase.client.deps.com.fasterxml.jackson.databind.DeserializationFeature;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.SerializationFeature;
import com.couchbase.client.java.document.json.JsonObject;
import com.mcmcg.dia.media.metadata.model.BaseModel;
import com.mcmcg.dia.media.metadata.model.MediaMetadataModel.DocumentType;
import com.mcmcg.dia.media.metadata.model.MediaMetadataModel.Seller;
import com.mcmcg.dia.media.metadata.model.domain.MediaMetadata;
import com.mcmcg.dia.media.metadata.model.entity.MediaMetadataEntity;
import com.mcmcg.dia.media.metadata.model.query.ExtractionExceptionDetailDataByPortfolio;
import com.mcmcg.dia.media.metadata.model.query.ExtractionExceptionMasterDataByPortfolio;

/**
 * 
 * @author Victor Arias
 *
 */
public final class MetadataConvertUtil {

	private static final ObjectMapper objectMapper;

	static {
		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
		objectMapper.configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, true);
	}

	/**
	 * 
	 * @param domain
	 * @return
	 */
	public static MediaMetadataEntity convertMediaMetadataToEntity(MediaMetadata domain) {
		MediaMetadataEntity entity = null;
		if (domain != null) {
			entity = new MediaMetadataEntity();
			BeanUtils.copyProperties(domain, entity);
			setGeneralAttributes(entity, domain);
		}

		return entity;
	}

	/**
	 * 
	 * @param entity
	 * @return
	 * @throws ParseException
	 */
	public static MediaMetadata convertMediaMetadataEntityToDomain(MediaMetadataEntity entity) {
		MediaMetadata domain = null;
		if (entity != null) {
			domain = new MediaMetadata();
			BeanUtils.copyProperties(entity, domain);
			setGeneralAttributes(domain, entity);
		}

		return domain;
	}

	/**
	 * 
	 * @param to
	 * @param from
	 */
	public static void setGeneralAttributes(BaseModel to, BaseModel from) {
		to.setCreateDate(from.getCreateDate());
		to.setUpdateDate(from.getUpdateDate());
		to.setVersion(from.getVersion());
		to.setUpdatedBy(from.getUpdatedBy());
	}

	/**
	 * 
	 * @param jsonObject
	 * @return
	 */
	public static ExtractionExceptionMasterDataByPortfolio convertJsonObjectToMediaMetadataGroupByPortfolio(
			JsonObject jsonObject) {

		Long portfolioNumber = jsonObject.getLong("portfolioNumber");
		String originalSeller = jsonObject.getString("originalSeller");
		Integer documentsFailed = jsonObject.getInt("documentsFailed");
		Integer templateFoundCount = jsonObject.getInt("templateFoundCount");
		Integer noTextLayer = jsonObject.getInt("noTextLayer");

		return new ExtractionExceptionMasterDataByPortfolio(portfolioNumber, originalSeller, documentsFailed,
				templateFoundCount, noTextLayer);
	}

	/**
	 * 
	 * @param jsonObject
	 * @return
	 */
	public static ExtractionExceptionDetailDataByPortfolio.TemplateFound convertJsonObjectToTemplateFound(
			JsonObject jsonObject) {

		String templateId = jsonObject.getString("templateId");
		String templateName = jsonObject.getString("templateName");
		String updatedBy = jsonObject.getString("updatedBy");
		String updateDate = jsonObject.getString("updateDate");
		Long version = jsonObject.getLong("version");
		String lastRun = jsonObject.getString("lastRun");
		Integer documentsFailed = jsonObject.getInt("documentsFailed");
		boolean reprocess = StringUtils.equals(jsonObject.getString("documentStatus"), "reprocess");

		return new ExtractionExceptionDetailDataByPortfolio.TemplateFound(templateId, templateName, version, updatedBy,
				updateDate, lastRun, documentsFailed, reprocess);
	}

	/**
	 * 
	 * @param jsonObject
	 * @return
	 */
	public static ExtractionExceptionDetailDataByPortfolio.TemplateNotFound convertJsonObjectToTemplateNotFound(
			JsonObject jsonObject) {

		if (jsonObject == null || jsonObject.isEmpty()) {
			return null;
		}

		String originalLenderName = jsonObject.getString("originalLender");
		Integer documentsFailed = jsonObject.getInt("documentsFailed");

		JsonObject jSeller = jsonObject.getObject("seller");
		Seller seller = new Seller(jSeller.getLong("id"), jSeller.getString("name"));

		JsonObject jDocumentType = jsonObject.getObject("originalDocumentType");
		DocumentType documentType = new DocumentType(jDocumentType.getLong("id"), jDocumentType.getString("code"));
		
		boolean reprocess = StringUtils.equals(jsonObject.getString("documentStatus"), "reprocess");

		return new ExtractionExceptionDetailDataByPortfolio.TemplateNotFound(originalLenderName, documentsFailed,
				seller, documentType, reprocess);
	}

	/**
	 * 
	 * @param jsonObject
	 * @return
	 */
	public static MediaMetadataEntity convertJsonObjectToMediaMetadataEntity(JsonObject jsonObject) {

		if (jsonObject == null || jsonObject.isEmpty()) {
			return null;
		}

		MediaMetadataEntity entity = null;
		try {
			entity = objectMapper.readValue(jsonObject.toString(), MediaMetadataEntity.class);
		} catch (Throwable e) {
			// nothing
			e.printStackTrace();
		}

		return entity;
	}

}
