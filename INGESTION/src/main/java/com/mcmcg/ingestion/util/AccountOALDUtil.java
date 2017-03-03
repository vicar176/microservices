package com.mcmcg.ingestion.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.mcmcg.ingestion.domain.Account;
import com.mcmcg.ingestion.domain.AccountOALDModel;
import com.mcmcg.ingestion.domain.AccountOALDModel.MediaOald;
import com.mcmcg.ingestion.domain.MediaMetadataModel;
import com.mcmcg.ingestion.domain.OaldProfile;

public class AccountOALDUtil {

	public static final String DATE_FORMAT_LONG = "yyyy-MM-dd";
	public static final SimpleDateFormat formater = new SimpleDateFormat(DATE_FORMAT_LONG);

	/**
	 * 
	 */
	protected AccountOALDUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param account
	 * @param documentFieldDefinition
	 * @param templateMappingProfileList
	 * @return
	 */
	public static AccountOALDModel buildAccountMetadata(MediaMetadataModel mediaMetadataModel, Account account,
			OaldProfile oaldProfile, MediaOald mediaOald) {

		AccountOALDModel accountMetadata = new AccountOALDModel();

		if (account != null) {
			accountMetadata.setAccountNumber(account.getAccountNumber());
			accountMetadata.setOriginalAccountNumber(account.getOriginalAccountNumber());
			accountMetadata.setPortfolioNumber(account.getPortfolioNumber());
		}

		if (mediaMetadataModel != null) {

			MediaOald mediaOadl = new MediaOald();
			mediaOadl.setId(mediaOald.getId());
			mediaOadl.setDocumentId(mediaMetadataModel.getDocumentId());
			mediaOadl.setDocumentNameString(mediaMetadataModel.getDocument().getDocumentNameString());
			mediaOadl.setDocumentDate(mediaMetadataModel.getDocument().getDocumentDate());

			if ( oaldProfile != null){
				mediaOadl.setOaldProfileId(oaldProfile.getId());
				mediaOadl.setOaldProfileVersion(oaldProfile.getVersion());

			}

			mediaOadl.setOriginalDocumentType(mediaMetadataModel.getDocument().getDocumentType());
			mediaOadl.setTranslatedDocumentType(mediaMetadataModel.getDocument().getTranslatedDocumentType());

			List<MediaOald> oaldList = new ArrayList<MediaOald>();
			oaldList.add(mediaOadl);
			accountMetadata.setOalds(oaldList);

		}

		if ( oaldProfile != null){
			
		}
		accountMetadata.setUpdatedBy("workflow-user");

		return accountMetadata;
	}

	public static AccountOALDModel buildAccountMetadata(MediaMetadataModel mediaMetadataModel, AccountOALDModel account,
														OaldProfile oaldProfile) {

		AccountOALDModel accountMetadata = new AccountOALDModel();

		if (account != null) {
			accountMetadata.setAccountNumber(account.getAccountNumber());
			accountMetadata.setOriginalAccountNumber(account.getOriginalAccountNumber());
			accountMetadata.setPortfolioNumber(account.getPortfolioNumber());
		}

		if (mediaMetadataModel != null && oaldProfile != null) {
			// String id = UUID.randomUUID().toString();
			// accountMetadata.setId(id);

			MediaOald mediaOadl = new MediaOald();

			mediaOadl.setDocumentId(mediaMetadataModel.getDocumentId());
			mediaOadl.setDocumentNameString(mediaMetadataModel.getDocument().getDocumentNameString());
			mediaOadl.setDocumentDate(mediaMetadataModel.getDocument().getDocumentDate());
			// mediaOadl.setReceivedDate(mediaDocument.getDocumentDate());
			mediaOadl.setOaldProfileId(oaldProfile.getId());
			mediaOadl.setOaldProfileVersion(oaldProfile.getVersion());

			mediaOadl.setOriginalDocumentType(mediaMetadataModel.getDocument().getDocumentType());
			mediaOadl.setTranslatedDocumentType(mediaMetadataModel.getDocument().getTranslatedDocumentType());

			List<MediaOald> oaldList = new ArrayList<MediaOald>();
			oaldList.add(mediaOadl);
			accountMetadata.setOalds(oaldList);

		}

		accountMetadata.setUpdatedBy("workflow-user");

		return accountMetadata;
	}

}