package com.mcmcg.ingestion.service.media;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jaleman
 *
 */
@Component
public class InitialContext {

	@Autowired
	AccountService accountService;

	@Autowired
	OaldProfileService oaldProfileService;

	@Autowired
	DocumentFieldProfileService documentFieldProfileService;

	@Autowired
	TemplateMappingProfileService templateMappingProfileService;

	@Autowired
	MediaMetadataModelService mediaMetadataModelService;

	@Autowired
	MediaMetadataOaldService mediaMetadataOaldService;

	@Autowired
	ExtractionUtilityService extractionUtilityService;

	@Autowired
	PdfTaggingUtilityService pdfTaggingUtilityService;

	@Autowired
	StatementTranslationUtilityService statementTranslationUtilityService;

	@Autowired
	AccountMetadataService accountMetadataService;

	@Autowired
	PortfolioService portfolioService;

	@Autowired
	StatementTranslationAccountService statementTranslationAccountService;

	@Autowired
	S3UtilityService s3UtilityService;

	@Autowired
	MediaOaldService mediaOaldService;

	@Autowired
	private CreateSnippetsUtilityService createSnippetsUtilityService;
	
	@Autowired
	private DocumentTypePortfolioService documentTypePortfolioService;

	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap<String, IService> serviceMap;

	@SuppressWarnings("rawtypes")
	@PostConstruct
	public void init() {
		serviceMap = new ConcurrentHashMap<String, IService>();

		serviceMap.put(AccountService.class.getSimpleName(), accountService);
		serviceMap.put(DocumentFieldProfileService.class.getSimpleName(), documentFieldProfileService);
		serviceMap.put(TemplateMappingProfileService.class.getSimpleName(), templateMappingProfileService);
		serviceMap.put(MediaMetadataModelService.class.getSimpleName(), mediaMetadataModelService);
		serviceMap.put(ExtractionUtilityService.class.getSimpleName(), extractionUtilityService);
		serviceMap.put(PdfTaggingUtilityService.class.getSimpleName(), pdfTaggingUtilityService);
		serviceMap.put(OaldProfileService.class.getSimpleName(), oaldProfileService);
		serviceMap.put(MediaMetadataOaldService.class.getSimpleName(), mediaMetadataOaldService);
		serviceMap.put(StatementTranslationUtilityService.class.getSimpleName(), statementTranslationUtilityService);
		serviceMap.put(AccountMetadataService.class.getSimpleName(), accountMetadataService);
		serviceMap.put(PortfolioService.class.getSimpleName(), portfolioService);
		serviceMap.put(StatementTranslationAccountService.class.getSimpleName(), statementTranslationAccountService);
		serviceMap.put(S3UtilityService.class.getSimpleName(), s3UtilityService);
		serviceMap.put(MediaOaldService.class.getSimpleName(), mediaOaldService);
		serviceMap.put(CreateSnippetsUtilityService.class.getSimpleName(), createSnippetsUtilityService);
		serviceMap.put(DocumentTypePortfolioService.class.getSimpleName(), documentTypePortfolioService);
	}

	public InitialContext() {
	}

	public Object lookup(String serviceName) {
		return serviceMap.get(serviceName);
	}
}
