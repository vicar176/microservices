package com.mcmcg.ingestion.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mcmcg.ingestion.domain.Account;
import com.mcmcg.ingestion.domain.AccountOALDModel;
import com.mcmcg.ingestion.domain.AccountOALDModel.MediaOald;
import com.mcmcg.ingestion.domain.MediaDocument;
import com.mcmcg.ingestion.domain.MediaMetadataModel;
import com.mcmcg.ingestion.domain.OaldProfile;
import com.mcmcg.ingestion.domain.Response;
import com.mcmcg.ingestion.exception.IngestionServiceException;
import com.mcmcg.ingestion.exception.MediaServiceException;
import com.mcmcg.ingestion.service.media.AccountMetadataService;
import com.mcmcg.ingestion.service.media.IService;
import com.mcmcg.ingestion.service.media.MediaMetadataOaldService;
import com.mcmcg.ingestion.service.media.MediaOaldService;
import com.mcmcg.ingestion.service.media.ServiceLocator;
import com.mcmcg.ingestion.util.AccountOALDUtil;
import com.mcmcg.ingestion.util.IngestionUtils;
import com.mcmcg.ingestion.util.MediaOaldUtil;

/**
 * 
 * @author Alok Verma
 *
 */
@Service
public class ReceiveService extends BaseIngestionService{
	
	private static final Logger LOG = Logger.getLogger(ReceiveService.class);
	

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected AccountOALDModel executeService(MediaDocument mediaDocument, Object ...params) throws IngestionServiceException, MediaServiceException {
		return receive(mediaDocument);
	}
	
	@Override
	protected String getDocumentStatus() {
		
		return IngestionUtils.RECEIVED;
	}
	/**
	 * 
	 * @param mediaDocument
	 * @return
	 */
	private AccountOALDModel receive(MediaDocument mediaDocument) throws IngestionServiceException, MediaServiceException{

		LOG.debug("Receive document: " + mediaDocument);
		//MediaMetadata mediaMetadata = null;
		AccountOALDModel accountOald = null;
		if (mediaDocument != null){

			//1. Check if document type is stmt
			Account account = retrieveAccountInfo(mediaDocument);
			MediaMetadataModel mediaMetadataModel = getMediaMetadataByDocumentId(mediaDocument);
			OaldProfile oaldProfile = retrieveOaldProfileByQuery(mediaDocument, account, mediaMetadataModel);

			if (oaldProfile != null){

				MediaOald mediaOald = saveReceivedFlag(mediaDocument,MediaOaldUtil.buildMediaOald(oaldProfile, mediaMetadataModel));
				
				AccountOALDModel accountOALDModel = retrieveAccountOald(account.getAccountNumber());
				
				if(accountOALDModel != null){
					MediaOald foundMediaOald =  searchForCurrentMediaOald(mediaDocument, accountOALDModel);
					if (foundMediaOald != null){
						foundMediaOald.setOaldProfileVersion(oaldProfile.getVersion());
					}else{
						foundMediaOald = mediaOald;
					}
					accountOALDModel.getOalds().add(foundMediaOald);
					accountOald = updateAccountOald(mediaDocument,accountOALDModel,oaldProfile, mediaOald);
				}else{
					accountOALDModel = AccountOALDUtil.buildAccountMetadata(mediaMetadataModel, account, oaldProfile, mediaOald);
					accountOald = saveAccountOald(mediaDocument, oaldProfile, account, accountOALDModel);
				}
				
				LOG.debug("Document Received:- " + mediaDocument);
			}else{
				LOG.debug("Document Not Marked as Received:- " + mediaDocument);
			}
		}
		return accountOald;
	}

	/**
	 * @param mediaDocument
	 * @param account
	 * @param mediaMetadataModel
	 * @return
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	private OaldProfile retrieveOaldProfileByQuery(MediaDocument mediaDocument, Account account,
			MediaMetadataModel mediaMetadataModel) throws IngestionServiceException, MediaServiceException {
		OaldProfile oaldProfile;
		if (StringUtils.equalsIgnoreCase(mediaDocument.getDocumentType(), "stmt")){
			
			String documentType = StringUtils.isNotBlank(mediaMetadataModel.getDocument().getTranslatedDocumentType()) ? 
									mediaMetadataModel.getDocument().getTranslatedDocumentType() : mediaDocument.getDocumentType();
									
			oaldProfile = checkRequiredOALDDocuments(account, documentType);
			
		}else{
			oaldProfile = getOaldProfile(account);
		}
		return oaldProfile;
	}


	/**
	 * @param mediaDocument
	 * @param oaldProfile
	 * @throws IngestionServiceException
	 */
	@SuppressWarnings("unchecked")
	private MediaOald saveReceivedFlag(MediaDocument mediaDocument,MediaOald mediaOald) throws IngestionServiceException, MediaServiceException {
				
		IService<MediaOald> mediaMetadataOaldService = serviceLocator.getService(ServiceLocator.MEDIA_METADATA_OALD_SERVICE_NAME);
		String resource = MediaMetadataOaldService.PUT_OR_GET_OALD_MEDIAMETADATA + mediaDocument.getDocumentId();
		
		Response<MediaOald> response = mediaMetadataOaldService.execute( resource, IService.PUT, mediaOald);
		
		if (response.getData() == null){
			String message = "Flag could not be saved: " + mediaDocument;
			LOG.error(message);
			throw new IngestionServiceException(message);
		}
		
		return response.getData();
		
	}
	
	/**
	 * @param accountNumber
	 * @throws IngestionServiceException
	 */
	@SuppressWarnings("unchecked")
	private AccountOALDModel retrieveAccountOald(Long accountNumber) throws IngestionServiceException, MediaServiceException {
		
		IService<AccountOALDModel> accountMetadataService = serviceLocator.getService(ServiceLocator.ACCOUNT_METADATA_OALD_SERVICE_NAME);
		
		String resource = AccountMetadataService.PUT_OR_GET_ACCOUNTMETADATA + accountNumber ;
		Response<AccountOALDModel> response = accountMetadataService.execute( resource, IService.GET);
		
		if (response.getData() == null){
			String message = "Account Metadata Oald not found for account " + accountNumber;
			LOG.warn(message);
			//throw new IngestionServiceException(message);
		}

		return  response.getData();
	}
	
	
	/**
	 * @param account
	 * @param accountMetaDataOald
	 * @throws IngestionServiceException
	 */
	@SuppressWarnings("unchecked")
	private AccountOALDModel saveAccountOald(MediaDocument mediaDocument, OaldProfile oaldProfile, Account account,AccountOALDModel accountMetaDataOald) 
																		throws IngestionServiceException, MediaServiceException {
				
		IService<AccountOALDModel> accountMetadataService = serviceLocator.getService(ServiceLocator.ACCOUNT_METADATA_OALD_SERVICE_NAME);
		String resource = AccountMetadataService.PUT_OR_GET_ACCOUNTMETADATA + account.getAccountNumber() ;
		
		Response<AccountOALDModel> response = accountMetadataService.execute( resource, IService.PUT, accountMetaDataOald);
		
		if (response.getData() == null){
			if (StringUtils.contains(response.getError().getMessage(), "An AccountOald already exists")){
				AccountOALDModel accountOALDModel = retrieveAccountOald(account.getAccountNumber());
				updateAccountOald(mediaDocument,accountOALDModel,oaldProfile, accountMetaDataOald.getOalds().get(0));
			}else{
				String message = "Account Metadata Oald could not be saved for : " + account.getAccountNumber();
				if (response.getError() != null){
					message += " --> " + response.getError().getMessage();
				}
				LOG.error(message);
				throw new IngestionServiceException(message);
			}
		}
		
		return response.getData();
		
	}
	
	/**
	 * @param account
	 * @param accountMetaDataOald
	 * @throws IngestionServiceException
	 */
	private AccountOALDModel updateAccountOald(MediaDocument mediaDocument,AccountOALDModel accountMetaDataOald,OaldProfile oaldProfile, MediaOald mediaOaldParam) 
															 throws IngestionServiceException, MediaServiceException {

		String resource = String.format(MediaOaldService.PUT_MEDIA_OALD, accountMetaDataOald.getAccountNumber(), mediaOaldParam.getId())  ;
		mediaOaldParam.setOaldProfileVersion(oaldProfile.getVersion());
		@SuppressWarnings("unchecked")
		IService<AccountOALDModel> accountMetadataService = serviceLocator.getService(ServiceLocator.ACCOUNT_METADATA_OALD_SERVICE_NAME);
		Response<AccountOALDModel> response = accountMetadataService.execute( resource, IService.PUT, mediaOaldParam);
		
		if (response.getData() == null){
			String message = "Media Oald could not be updated for : " + accountMetaDataOald.getAccountNumber();
			if (response.getError() != null){
				message += " --> " + response.getError().getMessage();
			}
			LOG.error(message);
			throw new IngestionServiceException(message);
		}
			
						
		return accountMetaDataOald;
	}

	/**
	 * @param mediaDocument
	 * @param accountMetaDataOald
	 * @param foundMediaOald
	 * @return
	 */
	private MediaOald searchForCurrentMediaOald(MediaDocument mediaDocument, AccountOALDModel accountMetaDataOald) {
		
		MediaOald foundMediaOald = null;
		int index = 0;
		for (MediaOald mediaOald : accountMetaDataOald.getOalds()){
			if (StringUtils.equalsIgnoreCase(String.valueOf(mediaOald.getDocumentId()), 
											 String.valueOf(mediaDocument.getDocumentId()))){
				foundMediaOald = accountMetaDataOald.getOalds().remove(index);
				break;
			}
			index++;
		}
		return foundMediaOald;
	}
	
	
}
