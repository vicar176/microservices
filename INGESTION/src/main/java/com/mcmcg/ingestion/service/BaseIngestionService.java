package com.mcmcg.ingestion.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mcmcg.ingestion.domain.Account;
import com.mcmcg.ingestion.domain.AccountOALDModel.MediaOald;
import com.mcmcg.ingestion.domain.DataElement;
import com.mcmcg.ingestion.domain.DocumentFieldsDefinitionModel;
import com.mcmcg.ingestion.domain.MediaDocument;
import com.mcmcg.ingestion.domain.MediaMetadataModel;
import com.mcmcg.ingestion.domain.OaldProfile;
import com.mcmcg.ingestion.domain.OaldProfile.DocumentType;
import com.mcmcg.ingestion.domain.ProductGroup;
import com.mcmcg.ingestion.domain.Response;
import com.mcmcg.ingestion.domain.StatementTranslation;
import com.mcmcg.ingestion.domain.TemplateMappingProfileModel;
import com.mcmcg.ingestion.exception.IngestionServiceException;
import com.mcmcg.ingestion.exception.MediaServiceException;
import com.mcmcg.ingestion.service.media.AccountService;
import com.mcmcg.ingestion.service.media.DocumentFieldProfileService;
import com.mcmcg.ingestion.service.media.DocumentTypePortfolioService;
import com.mcmcg.ingestion.service.media.IService;
import com.mcmcg.ingestion.service.media.MediaMetadataModelService;
import com.mcmcg.ingestion.service.media.OaldProfileService;
import com.mcmcg.ingestion.service.media.PortfolioService;
import com.mcmcg.ingestion.service.media.ServiceLocator;
import com.mcmcg.ingestion.service.media.TemplateMappingProfileService;
import com.mcmcg.ingestion.util.IngestionUtils;
import com.mcmcg.ingestion.util.MediaMetadataModelUtil;

/**
 * @author jaleman
 *
 */
public abstract class BaseIngestionService {

	private static final Logger LOG = Logger.getLogger(BaseIngestionService.class);

	@Autowired
	ServiceLocator serviceLocator;

	@Resource
	Map<String, Integer> documentStatusMap;

	protected IService<MediaMetadataModel> mediaMetadataService;
	protected IService<Account> accountService;
	protected IService<StatementTranslation> statementTranslationUtilityService;
	protected IService<Boolean> statementTranslationAccountService;
	protected IService<MediaMetadataModel> pdfTaggingUtilityService;
	protected IService<DocumentFieldsDefinitionModel> documentFieldService;
	protected IService<List<TemplateMappingProfileModel>> templateMappingService;
	protected IService<List<DataElement>> utilityService;
	protected IService<Object> s3UtilityService;
	protected IService<MediaOald> mediaOaldService;

	protected IService<Boolean> createSnippetsService;

	protected static Set<String> stmtDocTypeSet;

	static{
		stmtDocTypeSet = new HashSet<String>();
		stmtDocTypeSet.add("chrgoff");
		stmtDocTypeSet.add("lastactivity");
		stmtDocTypeSet.add("lastbillstmt");
		stmtDocTypeSet.add("lastpmt");
		
		stmtDocTypeSet = Collections.unmodifiableSet(stmtDocTypeSet);
	}
	
	/**
	 * 
	 * @param mediaDocument
	 * @return
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	public <T> T execute(MediaDocument mediaDocument, Object... params)
			throws IngestionServiceException, MediaServiceException {

		mediaDocument = markDocumentStatus(mediaDocument);

		return executeService(mediaDocument, params);
	}

	/*********************************************************************************************************************
	 * 
	 * 
	 * PROTECTED METHODS
	 * 
	 * 
	 *******************************************************************************************************************/

	@PostConstruct
	protected void init() {
		initServices();
	}

	/**
	 * 
	 * @param mediaDocument
	 * @return
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	protected abstract <T> T executeService(MediaDocument mediaDocument, Object... params)
			throws IngestionServiceException, MediaServiceException;

	/**
	 * 
	 * @return
	 */
	protected abstract String getDocumentStatus();

	/**
	 * @param mediaDocument
	 * @param templateMappingProfile
	 * @throws IngestionServiceException
	 */
	@SuppressWarnings("unchecked")
	protected MediaMetadataModel postMetadata(MediaDocument mediaDocument, MediaMetadataModel mediaMetadataModel)
			throws IngestionServiceException, MediaServiceException {

		IService<MediaMetadataModel> mediaMetadataModelService = serviceLocator
				.getService(ServiceLocator.MEDIA_METADATA_SERVICE_NAME);
		if (mediaDocument.getMediaMetadataModelId() != null) {
			LOG.debug("Document Id : " + mediaDocument.getDocumentId() + " " + "MediaMetadataModel Id : "
					+ mediaMetadataModel.getId());
			mediaMetadataModel.setId(mediaDocument.getMediaMetadataModelId());
		}
		String resource = MediaMetadataModelService.PUT_OR_GET_MEDIAMETADATA + mediaDocument.getDocumentId();
		LOG.info("Mediametadata --> " + IngestionUtils.getJsonObject(mediaMetadataModel));
		Response<MediaMetadataModel> response = mediaMetadataModelService.execute(resource, IService.PUT,
				mediaMetadataModel);
		LOG.debug(IngestionUtils.getJsonObject(mediaMetadataModel));

		if (response.getData() == null
				&& !StringUtils.contains(response.getError().getMessage(), "MediaMetadata already exists with")) {
			String message = "MediaMetadataModel could not be saved: " + mediaDocument.getDocumentId() + " due to --> "
					+ response.getError().getMessage();
			LOG.error(message);
			throw new IngestionServiceException(message);
		}

		mediaDocument.setMediaMetadataModelId(
				response.getData() != null ? response.getData().getId() : mediaDocument.getMediaMetadataModelId());
		return response.getData();
	}

	/**
	 * @param mediaDocument
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	protected OaldProfile checkRequiredOALDDocuments(Account account, String documentType)
			throws IngestionServiceException, MediaServiceException {
		OaldProfile oaldProfile = getOaldProfile(account);

		if (StringUtils.isNotBlank(documentType) && oaldProfile != null) {
			if (!checkIfIsRequiredOALDDocument(oaldProfile, documentType)) {
				String lender = (oaldProfile.getOriginalLender() != null) ? oaldProfile.getOriginalLender().getName()
						: StringUtils.EMPTY;
				String portfolio = (oaldProfile.getOriginalLender() != null) ? oaldProfile.getOriginalLender().getName()
						: StringUtils.EMPTY;
				String message = String.format(
						"Document Type %s is not a required document for OALDProfile [%s, %s, %s]", documentType,
						lender, portfolio, oaldProfile.getProductGroup().getCode());
				LOG.warn(message);
				//throw new IngestionServiceException(message);
			}
		}

		return oaldProfile;
	}

	/**
	 * 
	 * @param mediaDocument
	 * @return
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	protected OaldProfile getOaldProfile(Account account) throws IngestionServiceException, MediaServiceException {

		ProductGroup productGroup = retrieveProductGroup(account.getProductType());
		OaldProfile oaldProfile = retrieveOaldProfile(account, productGroup);

		return oaldProfile;
	}

	/**
	 * 
	 * @param profile
	 * @param documentType
	 * @return
	 */
	protected boolean checkIfIsRequiredOALDDocument(OaldProfile profile, String documentType) {

		for (DocumentType type : profile.getDocumentTypes()) {

			if (StringUtils.equalsIgnoreCase(type.getCode().trim(), documentType.trim())) {
				return true;
			}
		}

		return false;
	}

	protected String buildOaldRequest(String productGroup, Long portfolioNumber, String originalLender) {
		/**
		 * @RequestParam("productGroup.code") String productGroupCode,
		 * 
		 * @RequestParam(value = "portfolio.id", required=false) Long
		 *                     portfolioId,
		 * @RequestParam(value = "originalLender.name", required=false) String
		 *                     originalLenderName)
		 */
		StringBuilder builder = new StringBuilder();
		builder.append("productGroup.code=").append(productGroup);
		if (portfolioNumber != null && originalLender != null) {
			builder.append("&");
			builder.append("portfolio.id=").append(portfolioNumber).append("&");
			builder.append("originalLender.name=").append(originalLender);
		}

		return builder.toString();
	}

	/**
	 * 
	 * @param productType
	 * @return
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	@SuppressWarnings("unchecked")
	protected ProductGroup retrieveProductGroup(String productType)
			throws IngestionServiceException, MediaServiceException {
		IService<ProductGroup> portfolioService = serviceLocator.getService(ServiceLocator.PORTFOLIO_SERVICE_NAME);
		String resource = PortfolioService.GET_PRODUCTGROUP_BY_TYPE + productType;
		Response<ProductGroup> response = portfolioService.execute(resource, IService.GET);

		if (response.getData() == null) {
			String message = "Product Group was not found for this Product type " + productType;
			LOG.error(message);
			throw new IngestionServiceException(message);
		}

		LOG.debug(String.format("Product Group Info: %s for Product type %s", response.getData(), productType));
		return response.getData();
	}

	/**
	 * 
	 * @param code
	 * @return
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> retrieveDocTypeByCode(String code)
			throws IngestionServiceException, MediaServiceException {
		IService<Map<String, Object>> portfolioService = serviceLocator.getService(ServiceLocator.DOCUMENT_TYPE_PORTFOLIO_SERVICE_NAME);
		String resource = String.format(DocumentTypePortfolioService.GET_DOCUMENTTYPE_BY_CODE, code);
		Response<Map<String, Object>> response = portfolioService.execute(resource, IService.GET);

		return response.getData();
	}
	/**
	 * 
	 * @param account
	 * @param productGroup
	 * @return
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	@SuppressWarnings("unchecked")
	protected OaldProfile retrieveOaldProfile(Account account, ProductGroup productGroup)
			throws IngestionServiceException, MediaServiceException {

		IService<OaldProfile> oaldProfileService = serviceLocator.getService(ServiceLocator.OALD_PROFILE_SERVICE_NAME);
		String queryString = buildOaldRequest(productGroup.getCode(), account.getPortfolioNumber(),
				account.getOriginalLender());
		String resource = OaldProfileService.GET_PRODUCTGROUP_PORTFOLIOID_ORIGINALLENDER + queryString;
		Response<OaldProfile> response = oaldProfileService.execute(resource, IService.GET);

		if (response.getData() == null) {
			queryString = buildOaldRequest(productGroup.getCode(), null, null);
			resource = OaldProfileService.GET_PRODUCTGROUP_PORTFOLIOID_ORIGINALLENDER + queryString;
			response = oaldProfileService.execute(resource, IService.GET);

			if (response.getData() == null) {
				String message = "Oald was not found for account " + account;
				LOG.warn(message);
				//throw new IngestionServiceException(message);
			}
		}

		return response.getData();
	}

	/**
	 * 
	 * @param mediaDocument
	 * @return
	 * @throws IngestionServiceException
	 */
	@SuppressWarnings("unchecked")
	protected Account retrieveAccountInfo(MediaDocument mediaDocument)
			throws IngestionServiceException, MediaServiceException {

		IService<Account> accountService = serviceLocator.getService(ServiceLocator.ACCOUNT_SERVICE_NAME);
		String resource = AccountService.GET_ACCOUNT_BY_NUMBER + mediaDocument.getAccountNumber();
		Response<Account> response = accountService.execute(resource, IService.GET);

		if (response.getData() == null) {
			String message = "Account was not found for mediaDocument " + mediaDocument;
			LOG.error(message);
			throw new IngestionServiceException(message);
		}

		LOG.debug(String.format("Account Info: %s for MediaDocument %s", response.getData(), mediaDocument));

		// Setting Original Account Number
		mediaDocument.setOriginalAccountNumber(response.getData().getOriginalAccountNumber());

		return response.getData();
	}

	/**
	 * 
	 * @param mediaDocument
	 * @return
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	protected MediaMetadataModel getMediaMetadataByDocumentId(MediaDocument mediaDocument)
			throws IngestionServiceException, MediaServiceException {

		String resource = MediaMetadataModelService.PUT_OR_GET_MEDIAMETADATA + mediaDocument.getDocumentId();
		Response<MediaMetadataModel> response = mediaMetadataService.execute(resource, IService.GET);

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			LOG.debug(String.format("MediaMetadataModel: %s Step: %s ", mapper.writeValueAsString(response.getData()),
					this.getDocumentStatus()));
		} catch (JsonProcessingException e) {
			// nothing
		}
		return response.getData();
	}

	protected DocumentFieldsDefinitionModel retrieveDocumentFieldsDefinitionModel(MediaDocument mediaDocument,
			Account account, MediaMetadataModel mediaMetadataModelSaved) throws IngestionServiceException, MediaServiceException {

		String docType = stmtDocTypeSet.contains(mediaDocument.getDocumentType()) ? "stmt" : mediaDocument.getDocumentType();
		
		String resource = DocumentFieldProfileService.GET_DOCUMENT_FIELD_DEFINITION_BY_CODE	+ docType;
		Response<DocumentFieldsDefinitionModel> response = documentFieldService.execute(resource, IService.GET);

		if (response.getData() == null) {
			String message = IngestionUtils.DISCARDED
					+ " --> DocumentFieldsDefinitionModel was not found for mediaDocument " + mediaDocument;
			// Discards document types which don't have profiles
			mediaMetadataModelSaved.setDocumentStatus(IngestionUtils.DISCARDED);
			postMetadata(mediaDocument, mediaMetadataModelSaved);
			LOG.error(message);
			throw new IngestionServiceException(message);
		}

		DocumentFieldsDefinitionModel documentFieldsDefinitionModel = response.getData();
		LOG.debug(String.format("DocumentFieldsDefinitionModel Info: %s for MediaDocument %s", response.getData(),
				mediaDocument));

		if (!documentFieldsDefinitionModel.isActive()) {
			String message = String.format("Document Field Definition Profile %s is not active %s",
					documentFieldsDefinitionModel.getId(), documentFieldsDefinitionModel.isActive());
			mediaMetadataModelSaved.setDocumentStatus(getDocumentStatus());
			postMetadata(mediaDocument, mediaMetadataModelSaved);
			LOG.error(message);
			throw new IngestionServiceException(message);
		}

		return response.getData();
	}

	/**
	 * @param mediaDocument
	 * @param account
	 * @param affinities
	 * @throws IngestionServiceException
	 */
	protected TemplateMappingProfileModel retrieveTemplateMappingProfile(MediaDocument mediaDocument,
			Account account, DocumentFieldsDefinitionModel.DocumentFieldDefinition.DocumentType documentType, MediaMetadataModel mediaMetadataModelSaved)
			throws IngestionServiceException, MediaServiceException {

		boolean affinities = StringUtils.isNotBlank(account.getAffinity());
		Response<List<TemplateMappingProfileModel>> response = retrieveTemplateMappingProfile(mediaDocument, account,
				documentType, affinities);

		TemplateMappingProfileModel template = null;
		
		//Check if affinities are part of the resultset
		if (affinities){
			List<TemplateMappingProfileModel> list = response.getData();
			
			if (list != null){
				for (TemplateMappingProfileModel model : list){
					if (model.getAffinities().contains(account.getAffinity())){
						template = model;
					}
				}
			}
		}
		
		if (template == null){
			response = retrieveTemplateMappingProfile(mediaDocument, account, documentType, false);
			
			List<TemplateMappingProfileModel> list = response.getData();
			
			if (list != null && !list.isEmpty()){
				template = list.get(0);
			}
		}
		
		if (template == null) {
			String message = IngestionUtils.TEMPLATE_NOT_FOUND + " --> Template was not found for mediaDocument "
					+ mediaDocument;
			mediaMetadataModelSaved.setDocumentStatus(IngestionUtils.TEMPLATE_NOT_FOUND);
			postMetadata(mediaDocument, mediaMetadataModelSaved);
			LOG.error(message);
			throw new IngestionServiceException(message);
		}

		return template;
	}

	/**
	 * @param mediaDocument
	 * @param account
	 * @param documentType
	 * @param affinities
	 * @return
	 * @throws MediaServiceException
	 * @throws IngestionServiceException
	 */
	private Response<List<TemplateMappingProfileModel>> retrieveTemplateMappingProfile(MediaDocument mediaDocument,
			Account account, DocumentFieldsDefinitionModel.DocumentFieldDefinition.DocumentType documentType,
			boolean affinities) throws MediaServiceException, IngestionServiceException {
		String queryString = buildTemplateMappingRequest(documentType.getCode(), account.getSellerId(),
				account.getOriginalLender(), affinities);
		String resource = TemplateMappingProfileService.GET_DOCUMENTTYPE_SELLER_ORIGINALLENDER + queryString;

		LOG.debug("retrieveTemplateMappingProfile " + resource);
		Response<List<TemplateMappingProfileModel>> response = templateMappingService.execute(resource, IService.GET);

		return response;
	}

	/**
	 * 
	 * @param documentType
	 * @param seller
	 * @param originalLender
	 * @param affinities
	 * @return
	 */
	protected String buildTemplateMappingRequest(String documentType, String seller, String originalLender,
			boolean affinities) {
		/**
		 * @RequestParam(value = "documentType.code") String documentTypeCode,
		 * @RequestParam(value = "seller.id") Long sellerId,
		 * @RequestParam(value = "originalLender.name") String
		 *                     originalLenderName,
		 * @RequestParam(value = "affinities") boolean hasAffinities)
		 */
		StringBuilder builder = new StringBuilder();
		builder.append("documentType.code=").append(documentType).append("&");
		builder.append("seller.id=").append(seller).append("&");
		builder.append("originalLender.name=").append(originalLender).append("&");
		builder.append("affinities=").append(affinities);

		return builder.toString();
	}

	/**
	 * 
	 * @param templateMappingProfile
	 * @param account
	 * @return Template
	 * @throws IngestionServiceException
	 */
	protected TemplateMappingProfileModel retrieveTemplateMappingProfile(MediaDocument mediaDocument,
			List<TemplateMappingProfileModel> templateMappingProfileList, Account account,
			DocumentFieldsDefinitionModel.DocumentFieldDefinition.DocumentType documentType)
			throws IngestionServiceException, MediaServiceException {

		String affinity = account.getAffinity();
		TemplateMappingProfileModel returnTemplate = null;

		for (TemplateMappingProfileModel template : templateMappingProfileList) {
			if ((template.getAffinities() != null && template.getAffinities().contains(affinity))
					|| (template.getAffinities() == null && affinity == null)) {
				returnTemplate = template;
				break;
			}
		}

		return returnTemplate;
	}

	/*********************************************************************************************************************
	 * 
	 * 
	 * PRIVATE METHODS
	 * 
	 * 
	 *******************************************************************************************************************/

	/**
	 * @param mediaDocument
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	private MediaDocument markDocumentStatus(MediaDocument mediaDocument)
			throws IngestionServiceException, MediaServiceException {
		MediaMetadataModel mediaMetadataModel = getMediaMetadataByDocumentId(mediaDocument);


		if (mediaMetadataModel == null) {
			DocumentFieldsDefinitionModel.DocumentFieldDefinition.DocumentType documentType = retrieveDocTypeInfo(mediaDocument);

			mediaMetadataModel = MediaMetadataModelUtil.buildMediaMetadataModel(mediaDocument,
					retrieveAccountInfo(mediaDocument), null, null, getDocumentStatus(), documentType);
			mediaMetadataModel.setBatchProfileJobId(new Long(mediaDocument.getBatchProfileJobId()));
			mediaMetadataModel = postMetadata(mediaDocument, mediaMetadataModel);
		} else if (documentStatusMap != null && documentStatusMap.containsKey(mediaMetadataModel.getDocumentStatus())) {
			int rest = documentStatusMap.get(mediaMetadataModel.getDocumentStatus())
					- documentStatusMap.get(getDocumentStatus());
			if (rest < 0) {
				mediaMetadataModel.setDocumentStatus(getDocumentStatus());
				mediaMetadataModel.setBatchProfileJobId(new Long(mediaDocument.getBatchProfileJobId()));
				mediaMetadataModel = postMetadata(mediaDocument, mediaMetadataModel);
			}
		}

		mediaDocument.setMediaMetadataModelId(mediaMetadataModel.getId());
		return mediaDocument;
	}

	/**
	 * @param mediaDocument
	 * @return
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 * @throws NumberFormatException
	 */
	protected DocumentFieldsDefinitionModel.DocumentFieldDefinition.DocumentType retrieveDocTypeInfo(
			MediaDocument mediaDocument)
			throws IngestionServiceException, MediaServiceException, NumberFormatException {
		Map<String, Object> documentTypeMap = retrieveDocTypeByCode(mediaDocument.getDocumentType().toLowerCase());
		DocumentFieldsDefinitionModel.DocumentFieldDefinition.DocumentType documentType = new DocumentFieldsDefinitionModel.DocumentFieldDefinition.DocumentType();
		documentType.setCode(mediaDocument.getDocumentType().toLowerCase());
		if (documentTypeMap != null && documentTypeMap.size() > 0){
			documentType.setId(Long.parseLong(documentTypeMap.get("id").toString()));
		}
		return documentType;
	}

	/**
	 * @throws IngestionServiceException
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void initServices() {
		try {

			mediaMetadataService = serviceLocator.getService(ServiceLocator.MEDIA_METADATA_SERVICE_NAME);
			accountService = serviceLocator.getService(ServiceLocator.ACCOUNT_SERVICE_NAME);
			statementTranslationUtilityService = serviceLocator
					.getService(ServiceLocator.STATEMENT_TRANSLATION_UTILITY_SERVICE_NAME);
			pdfTaggingUtilityService = serviceLocator.getService(ServiceLocator.PDFTAGGING_UTILITY_SERVICE_NAME);
			documentFieldService = serviceLocator.getService(ServiceLocator.FIELD_DEFINITION_SERVICE_NAME);
			templateMappingService = serviceLocator.getService(ServiceLocator.TEMPLATE_MAPPING_SERVICE_NAME);
			utilityService = serviceLocator.getService(ServiceLocator.EXTRACTION_UTILITY_SERVICE_NAME);
			statementTranslationAccountService = serviceLocator
					.getService(ServiceLocator.STATEMENT_TRANSLATION_ACCOUNT_SERVICE_NAME);
			s3UtilityService = serviceLocator.getService(ServiceLocator.S3_UTILITY_SERVICE_NAME);
			mediaOaldService = serviceLocator.getService(ServiceLocator.MEDIA_OALD_SERVICE_NAME);
			createSnippetsService = serviceLocator.getService(ServiceLocator.CREATE_SNIPPETS_UTILITY_SERVICE_NAME);

		} catch (IngestionServiceException e) {
			LOG.warn(e);
			// fallback
			mediaMetadataService = new MediaMetadataModelService();
			// utilityService = new UtilityService();
		}
	}

}
