package com.mcmcg.ingestion.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mcmcg.ingestion.domain.Account;
import com.mcmcg.ingestion.domain.DataElement;
import com.mcmcg.ingestion.domain.DocumentFieldsDefinitionModel;
import com.mcmcg.ingestion.domain.MediaDocument;
import com.mcmcg.ingestion.domain.MediaMetadataModel;
import com.mcmcg.ingestion.domain.MediaMetadataModel.Document;
import com.mcmcg.ingestion.domain.MediaMetadataModel.DocumentName;
import com.mcmcg.ingestion.domain.MediaMetadataModel.DocumentType;
import com.mcmcg.ingestion.domain.MediaMetadataModel.Extraction;
import com.mcmcg.ingestion.domain.MediaMetadataModel.Seller;
import com.mcmcg.ingestion.domain.TemplateMappingProfileModel;

/**
 * @author jaleman
 *
 */
public class MediaMetadataModelUtil {

	public static final String DATE_FORMAT_LONG = "yyyy-MM-dd";
	public static final SimpleDateFormat formater = new SimpleDateFormat(DATE_FORMAT_LONG);
	public static final String EXTRACTED = "extracted";

	protected MediaMetadataModelUtil() {
	}

	/**
	 * 
	 * @param account
	 * @param documentFieldDefinition
	 * @param templateMappingProfileList
	 * @return
	 */
	public static MediaMetadataModel buildMediaMetadataModel(MediaDocument mediaDocument, Account account,
			TemplateMappingProfileModel template, List<DataElement> dataElements, String documentStatus, 
			DocumentFieldsDefinitionModel.DocumentFieldDefinition.DocumentType documentType) {

		MediaMetadataModel mediaMetadataModel = new MediaMetadataModel();

		if (mediaDocument != null) {
			mediaMetadataModel.setDocumentId(mediaDocument.getDocumentId());
			mediaMetadataModel.setOriginalDocumentType(new DocumentType());
			mediaMetadataModel.getOriginalDocumentType().setCode(mediaDocument.getDocumentType());
			mediaMetadataModel.setDocumentStatus(documentStatus);
			mediaMetadataModel.setAccountNumber(mediaDocument.getAccountNumber());

			// Document
			mediaMetadataModel.setDocument(new Document());
			if(documentType != null){
				mediaMetadataModel.getOriginalDocumentType().setId(documentType.getId());	
				mediaMetadataModel.getDocument().setOriginalDocumentType(documentType.getCode());
			}else{
				mediaMetadataModel.getDocument().setOriginalDocumentType(mediaDocument.getDocumentType().toLowerCase());
			}
			mediaMetadataModel.getDocument().setDocumentNameString(mediaDocument.getFilename());
			mediaMetadataModel.getDocument().setBucketName(mediaDocument.getBucket());//bucket name
			// date
			mediaMetadataModel.getDocument().setDocumentDate(mediaDocument.getDocumentDate());
			mediaMetadataModel.setDocumentDate(mediaDocument.getDocumentDate());

			mediaMetadataModel.getDocument().setDocumentName(new DocumentName());
			mediaMetadataModel.getDocument().getDocumentName().setDocumentId(String.valueOf(mediaDocument.getDocumentId()));
			mediaMetadataModel.getDocument().getDocumentName().setDocumentType(mediaDocument.getDocumentType());
			mediaMetadataModel.getDocument().setDocumentType(mediaDocument.getDocumentType().toLowerCase());

			if ("stmt".equalsIgnoreCase(mediaDocument.getDocumentType())){
				mediaMetadataModel.getDocument().setTranslatedDocumentType(mediaDocument.getDocumentType().toLowerCase());
			}
			
		}

		if (template != null) {
			mediaMetadataModel.setExtraction(new Extraction());
			mediaMetadataModel.getExtraction().setExtractedDate(formatDate(new Date()));
			mediaMetadataModel.getExtraction().setTemplateMappingProfile(template);
			mediaMetadataModel.setSeller(new Seller());
			mediaMetadataModel.getSeller().setId(template.getSeller().getId());
			mediaMetadataModel.getSeller().setName(template.getSeller().getName());
		}

		if (account != null) {
			mediaMetadataModel.setPortfolioNumber(account.getPortfolioNumber());
			mediaMetadataModel.setAccountNumber(account.getAccountNumber());
			mediaMetadataModel.setSeller(new Seller());
			mediaMetadataModel.getSeller().setId(Long.parseLong(account.getSellerId()));
			mediaMetadataModel.getSeller().setName(account.getSellerName());
			mediaMetadataModel.setOriginalLenderName(account.getOriginalLender());
			mediaMetadataModel.getDocument().getDocumentName().setOriginalAccountNumber(account.getOriginalAccountNumber());
		}
		
		if (dataElements != null && !dataElements.isEmpty()) {
			mediaMetadataModel.setDataElements(new ArrayList<DataElement>());
			mediaMetadataModel.getDataElements().addAll(dataElements);
		}

		// MediaMetadataModel.setCreateDate(new Date()); Now it is a string and
		// it is auto generated in the object constructor
		// MediaMetadataModel.setUpdateDate(new Date());
		mediaMetadataModel.setUpdatedBy("workflow");

		return mediaMetadataModel;
	}

	/**
	 * 
	 * @param mediaMetadataModelParam
	 * @param template
	 * @param dataElements
	 * @param documentStatus
	 * @return
	 */
	public static MediaMetadataModel buildMediaMetadataModel(MediaMetadataModel mediaMetadataModelParam, Account account,
															 TemplateMappingProfileModel template, List<DataElement> dataElements, 
															 String documentStatus) {

		MediaMetadataModel mediaMetadataModel = new MediaMetadataModel();

		if (mediaMetadataModelParam != null) {
			mediaMetadataModel.setId(mediaMetadataModelParam.getId());
			mediaMetadataModel.setDocumentId(mediaMetadataModelParam.getDocumentId());
			mediaMetadataModel.setOriginalDocumentType(mediaMetadataModelParam.getOriginalDocumentType());
			mediaMetadataModel.setDocumentStatus(documentStatus);
			mediaMetadataModel.setAccountNumber(mediaMetadataModelParam.getAccountNumber());

			// Document
			mediaMetadataModel.setDocument(mediaMetadataModelParam.getDocument());
			// date
			mediaMetadataModel.setDocumentDate(mediaMetadataModelParam.getDocumentDate());
		}
		
		if (template != null) {
			mediaMetadataModel.setExtraction(new Extraction());
			mediaMetadataModel.getExtraction().setExtractedDate(formatDate(new Date()));
			mediaMetadataModel.getExtraction().setTemplateMappingProfile(template);
			mediaMetadataModel.setSeller(new Seller());
			mediaMetadataModel.getSeller().setId(template.getSeller().getId());
			mediaMetadataModel.getSeller().setName(template.getSeller().getName());
		}

		if (account != null) {
			mediaMetadataModel.setPortfolioNumber(account.getPortfolioNumber());
			mediaMetadataModel.setAccountNumber(account.getAccountNumber());
			mediaMetadataModel.setSeller(new Seller());
			mediaMetadataModel.getSeller().setId(Long.parseLong(account.getSellerId()));
			mediaMetadataModel.getSeller().setName(account.getSellerName());
			mediaMetadataModel.setOriginalLenderName(account.getOriginalLender());
			mediaMetadataModel.getDocument().getDocumentName().setOriginalAccountNumber(account.getOriginalAccountNumber());
		}

		if (dataElements != null && !dataElements.isEmpty()) {
			mediaMetadataModel.setDataElements(new ArrayList<DataElement>());
			mediaMetadataModel.getDataElements().addAll(dataElements);
		}

		// MediaMetadataModel.setCreateDate(new Date()); Now it is a string and
		// it is auto generated in the object constructor
		// MediaMetadataModel.setUpdateDate(new Date());
		mediaMetadataModel.setUpdatedBy("workflow");

		return mediaMetadataModel;
	}

	public static String formatDate(Date date) {
		return formater.format(date);
	}

}
