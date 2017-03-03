package com.mcmcg.dia.iwfm.service;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.iwfm.domain.MediaMetadataModel;
import com.mcmcg.dia.iwfm.domain.PagedResponse;
import com.mcmcg.dia.iwfm.domain.Response;
import com.mcmcg.dia.iwfm.exception.IngestionWorkflowManagerException;
import com.mcmcg.dia.iwfm.exception.MediaServiceException;
import com.mcmcg.dia.iwfm.service.media.DocumentIdListDocumentProcessorService;
import com.mcmcg.dia.iwfm.service.media.IService;
import com.mcmcg.dia.iwfm.service.media.IngestionStepService;
import com.mcmcg.dia.iwfm.service.media.MediaMetadataDocumentService;
import com.mcmcg.dia.iwfm.service.media.PagedResponseMediaMetadataService;
import com.mcmcg.dia.iwfm.service.media.ServiceLocator;

/**
 * 
 * @author Jose Aleman
 *
 */
@Service
public class DocumentService {

	private static final Logger LOG = Logger.getLogger(DocumentService.class);

	@Autowired
	private ServiceLocator serviceLocator;

	private IService<PagedResponse<Map<String, Object>>> mediaMetadataDocumentService;
	private IService<PagedResponse<MediaMetadataModel>> mediaMetadataService;
	private IService<Response<Object>> ingestionStepService;
	
	@Autowired
	private DocumentIdListDocumentProcessorService documentIdListDocumentProcessorService;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {

		try {
			mediaMetadataDocumentService = serviceLocator.getService(ServiceLocator.DOCUMENT_SERVICE_NAME);
			mediaMetadataService = serviceLocator.getService(ServiceLocator.PAGED_METADATA_SERVICE_NAME);
			ingestionStepService = serviceLocator.getService(ServiceLocator.INGESTION_STEP_SERVICE_NAME);
		} catch (MediaServiceException e) {
			LOG.warn(e);
			// fallback
			mediaMetadataDocumentService = new MediaMetadataDocumentService();
		}

	}

	/**
	 * 
	 * @param portfolioNumber
	 * @param templateName
	 * @return
	 * @throws MediaServiceException
	 * @throws IngestionWorkflowManagerException
	 */
	// "/metadatas/portfolios/%d/documents?filter=documentStatus=%s|extraction.templateMappingProfile.name=%s";
	public PagedResponse<Map<String, Object>> retrieveDocuments(Long portfolioNumber, String templateName,
			String documentStatus) throws MediaServiceException, IngestionWorkflowManagerException {

		String resource = String.format(MediaMetadataDocumentService.GET_MEDIAMETADATA_DOCUMENTS, portfolioNumber,
				"\"" + documentStatus + "\"", "\"" + templateName + "\"");

		Response<PagedResponse<Map<String, Object>>> response = mediaMetadataDocumentService.execute(resource, "GET");

		if (response == null || response.getData() == null) {
			String message = response != null && response.getError() != null ? response.getError().getMessage()
					: "No documents were found";
			throw new IngestionWorkflowManagerException(message);
		}

		return response.getData();
	}

	/**
	 * 
	 * @param portfolioNumber
	 * @param templateName
	 * @param documentStatus
	 * @return
	 * @throws MediaServiceException
	 * @throws IngestionWorkflowManagerException
	 */
	// "/metadatas/portfolios/%d/documents?filter=documentStatus=%s|extraction.templateMappingProfile.name=%s";
	public PagedResponse<MediaMetadataModel> retrieveMetadatas(Long portfolioNumber, String templateName,
			String documentStatus) throws MediaServiceException, IngestionWorkflowManagerException {

		String resource = PagedResponseMediaMetadataService.GET_MEDIAMETADATA_DOCUMENTS + "portfolioNumber="
				+ portfolioNumber + "|documentStatus=\"" + documentStatus + "\""
				+ "|extraction.templateMappingProfile.name=\"" + templateName + "\"";

		Response<PagedResponse<MediaMetadataModel>> response = mediaMetadataService.execute(resource, "GET");

		if (response == null || response.getData() == null) {
			String message = response != null && response.getError() != null ? response.getError().getMessage()
					: "No documents were found";
			throw new IngestionWorkflowManagerException(message);
		}

		return response.getData();
	}

	// "/metadatas/portfolios/%d/templatesNotFound?filter=documentStatusList=%s,reprocess|originalDocumentType.code=%s"
	public PagedResponse<MediaMetadataModel> retrieveDocumentTypes(Long portfolioNumber, String documentTypeCode,
			String originalLenderName) throws MediaServiceException, IngestionWorkflowManagerException {

		String resource = PagedResponseMediaMetadataService.GET_MEDIAMETADATA_DOCUMENTS + "portfolioNumber="
				+ portfolioNumber + "|originalDocumentType.code=\"" + documentTypeCode + "\""
				+ "|originalLenderName=\"" + originalLenderName + "\"" + "|TYPE(extraction.templateMappingProfile) = \"missing\"";

		Response<PagedResponse<MediaMetadataModel>> response = mediaMetadataService.execute(resource, "GET");

		if (response == null || response.getData() == null) {
			String message = response != null && response.getError() != null ? response.getError().getMessage()
					: "No documents were found";
			throw new IngestionWorkflowManagerException(message);
		}

		return response.getData();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> retrieveFailedDocumentIds(String filter) throws MediaServiceException, IngestionWorkflowManagerException {
		
		String resource = IngestionStepService.GET_FAILED_DOCUMENTS;
		List<String> result = null;
		if(StringUtils.isNotBlank(filter)) {
			resource += "?filter=" + filter;
		}

		Response<Response<Object>> response = ingestionStepService.execute(resource, "GET");

		if (response == null || response.getData() == null) {
			String message = response != null && response.getError() != null ? response.getError().getMessage()
					: "No documents were found";
			throw new IngestionWorkflowManagerException(message);
		}
		result = (List<String>) response.getData();
			
		return result;
	}
	
}
