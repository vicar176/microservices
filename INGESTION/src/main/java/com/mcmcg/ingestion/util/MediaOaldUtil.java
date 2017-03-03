package com.mcmcg.ingestion.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.mcmcg.ingestion.domain.AccountOALDModel.MediaOald;
import com.mcmcg.ingestion.domain.MediaMetadataModel;
import com.mcmcg.ingestion.domain.OaldProfile;

public class MediaOaldUtil {

	public static final String DATE_FORMAT_LONG = "yyyy-MM-dd";
	public static final SimpleDateFormat formater = new SimpleDateFormat(DATE_FORMAT_LONG);
	/**
	 * 
	 */
	protected MediaOaldUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param account
	 * @param documentFieldDefinition
	 * @param templateMappingProfileList
	 * @return
	 */
	public static MediaOald buildMediaOald (OaldProfile oaldProfile, MediaMetadataModel mediaMetadataModel)
	{
		
		MediaOald mediaOald = new MediaOald();
		
		if (mediaMetadataModel != null){
			mediaOald.setDocumentId(mediaMetadataModel.getDocumentId());
			mediaOald.setDocumentDate(mediaMetadataModel.getDocument().getDocumentDate());
			mediaOald.setDocumentNameString(mediaMetadataModel.getDocument().getDocumentNameString());
			mediaOald.setOriginalDocumentType(mediaMetadataModel.getDocument().getOriginalDocumentType());
			mediaOald.setTranslatedDocumentType(mediaMetadataModel.getDocument().getTranslatedDocumentType());
		}

		if (oaldProfile != null){
			mediaOald.setOaldProfileId(oaldProfile.getId());
			mediaOald.setOaldProfileVersion(oaldProfile.getVersion());
		}
		mediaOald.setUpdatedBy("workflow-user");

		return mediaOald;
	}
	
	public static String formatDate(Date date) {
		return formater.format(date);
	}

}