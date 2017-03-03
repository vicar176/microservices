package com.mcmcg.ingestion.service.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mcmcg.ingestion.exception.IngestionServiceException;

/**
 * 
 * @author Jose Aleman
 *
 */
@Component
public class ServiceLocator {

	@Autowired
	InitialContext initialContext;

	public final static String ACCOUNT_SERVICE_NAME = AccountService.class.getSimpleName();
	public final static String FIELD_DEFINITION_SERVICE_NAME = DocumentFieldProfileService.class.getSimpleName();
	public final static String TEMPLATE_MAPPING_SERVICE_NAME = TemplateMappingProfileService.class.getSimpleName();
	public final static String MEDIA_METADATA_SERVICE_NAME = MediaMetadataModelService.class.getSimpleName();
	public final static String EXTRACTION_UTILITY_SERVICE_NAME = ExtractionUtilityService.class.getSimpleName();
	public final static String PDFTAGGING_UTILITY_SERVICE_NAME = PdfTaggingUtilityService.class.getSimpleName();
	public final static String OALD_PROFILE_SERVICE_NAME = OaldProfileService.class.getSimpleName();
	public final static String MEDIA_METADATA_OALD_SERVICE_NAME = MediaMetadataOaldService.class.getSimpleName();
	public final static String STATEMENT_TRANSLATION_UTILITY_SERVICE_NAME = StatementTranslationUtilityService.class.getSimpleName();
	public final static String ACCOUNT_METADATA_OALD_SERVICE_NAME = AccountMetadataService.class.getSimpleName();
	public final static String MEDIA_OALD_SERVICE_NAME = MediaOaldService.class.getSimpleName();
	public final static String PORTFOLIO_SERVICE_NAME = PortfolioService.class.getSimpleName();
	public final static String STATEMENT_TRANSLATION_ACCOUNT_SERVICE_NAME = StatementTranslationAccountService.class.getSimpleName();
	public final static String S3_UTILITY_SERVICE_NAME = S3UtilityService.class.getSimpleName();
	public final static String CREATE_SNIPPETS_UTILITY_SERVICE_NAME = CreateSnippetsUtilityService.class.getSimpleName();
	public final static String DOCUMENT_TYPE_PORTFOLIO_SERVICE_NAME = DocumentTypePortfolioService.class.getSimpleName();


	public ServiceLocator() {
	}

	/**
	 * 
	 * @param serviceName
	 * @return
	 * @throws IngestionServiceException
	 */
	@SuppressWarnings("rawtypes")
	public IService getService(String serviceName) throws IngestionServiceException {

		IService service = (IService) initialContext.lookup(serviceName);

		if (service == null) {
			throw new IngestionServiceException("Service " + serviceName + " was not found in context");
		}

		return service;
	}
}
