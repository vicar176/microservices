/**
 * 
 */
package com.mcmcg.dia.iwfm.service;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mcmcg.dia.iwfm.domain.MediaDocumentStatus;
import com.mcmcg.dia.iwfm.domain.MediaMetadataModel;
import com.mcmcg.dia.iwfm.domain.NewDocumentStatus;
import com.mcmcg.dia.iwfm.domain.Response;
import com.mcmcg.dia.iwfm.exception.IngestionWorkflowManagerException;
import com.mcmcg.dia.iwfm.exception.MediaServiceException;
import com.mcmcg.dia.iwfm.service.media.DocumentProcessorService;
import com.mcmcg.dia.iwfm.service.media.IService;
import com.mcmcg.dia.iwfm.service.media.PagedResponseMediaMetadataService;
import com.mcmcg.dia.iwfm.service.media.ServiceLocator;
import com.mcmcg.dia.iwfm.util.ConversionRulesUtil;
import com.mcmcg.dia.iwfm.util.DocumentImagesStatusCode;
import com.mcmcg.dia.iwfm.util.DocumentStatusCode;

/**
 * @author jaleman
 *
 */
@Service
public class DocumentQueueingService extends BaseIngestionWorkflowService {

	private final static Logger LOG = Logger.getLogger(DocumentQueueingService.class);

	@Autowired
	private ServiceLocator serviceLocator;

	private IService<MediaMetadataModel> mediaMetadataService;

	@Autowired
	DocumentProcessorService documentProcessorService;
	
	@Autowired
	private BatchService batchService;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {

		try {
			mediaMetadataService = serviceLocator.getService(ServiceLocator.RESPONSE_METADATA_SERVICE_NAME);
		} catch (MediaServiceException e) {
			LOG.warn(e);
			// fallback
		}

	}

	/**
	 * 
	 */
	public DocumentQueueingService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param documents
	 * @return
	 * @throws IngestionWorkflowManagerException
	 * @throws MediaServiceException
	 */
	public List<String> sendDocumentsToIngestionQueue(List<String> documents, String updatedBy)
			throws IngestionWorkflowManagerException, MediaServiceException {

		List<String> documentList = new LinkedList<String>();
		if (documents != null && !documents.isEmpty()) {

			for (String documentId : documents) {
				//prepareAndSendMessageToQueue(documentId, DocumentStatusCode.REPROCESS, updatedBy);
				updateDocumentImagesTable(documentId, DocumentImagesStatusCode.REPROCESS.toString(), updatedBy);
				documentList.add(documentId);
			}
		}

		return documentList;
	}

	/**
	 * 
	 * @param metadataList
	 * @return
	 * @throws IngestionWorkflowManagerException
	 */
	public List<String> prepareAndSendDocumentsToIngestionQueue(List<MediaMetadataModel> metadataList,
			final DocumentStatusCode documentStatusCode, final String updatedBy)
			throws IngestionWorkflowManagerException {

		List<String> documentList = new LinkedList<String>();

		if (metadataList != null && !metadataList.isEmpty()) {

			for (final MediaMetadataModel metadata : metadataList) {
				final String document = String.valueOf(metadata.getDocumentId());

				taskExecutor.execute(new Runnable() {

					@Override
					public void run() {
						try {
							
							Response<MediaMetadataModel> response = saveMetadataByStatus("reprocess", updatedBy, metadata);

							if (response.getData() == null) {
								LOG.error(response.getError().getMessage());
							}

							//prepareAndSendMessageToQueue(String.valueOf(metadata.getDocumentId()), documentStatusCode,	updatedBy);
							updateDocumentImagesTable(String.valueOf(metadata.getDocumentId()),	DocumentImagesStatusCode.REPROCESS.toString(), updatedBy);

						} catch (MediaServiceException e) {
							LOG.warn(e.getMessage(), e);
						} catch (Exception e) {
							LOG.warn(e.getMessage(), e);
						}
					}
				});
				documentList.add(document);
			}
		}

		return documentList;
	}

	/**
	 * 
	 * @param metadataList
	 * @param documentStatusCode
	 * @param updatedBy
	 * @return
	 * @throws IngestionWorkflowManagerException
	 */
	public List<String> updateDocImgAndMetadata(List<MediaMetadataModel> metadataList, final String documentStatus, final String updatedBy)
			throws IngestionWorkflowManagerException {

		List<String> documentList = new LinkedList<String>();

		if (metadataList != null && !metadataList.isEmpty()) {

			for (final MediaMetadataModel metadata : metadataList) {
				final String document = String.valueOf(metadata.getDocumentId());

				taskExecutor.execute(new Runnable() {

					@Override
					public void run() {
						try {
							Response<MediaMetadataModel> response = saveMetadataByStatus(documentStatus, updatedBy,	metadata);

							if (response.getData() == null) {
								LOG.error(response.getError().getMessage());
								
							}else{
								updateDocumentImagesTable(String.valueOf(metadata.getDocumentId()),	StringUtils.capitalize(documentStatus), updatedBy);
							}
							
						} catch (MediaServiceException e) {
							LOG.warn(e.getMessage(), e);
						} catch (Exception e) {
							LOG.warn(e.getMessage(), e);
						}
					}

				});
				documentList.add(document);
			}
		}

		return documentList;
	}

	/**
	 * 
	 * @param documentId
	 * @param batchProfileJobId
	 * @param mediaDocumentStatus
	 * @return
	 * @throws MediaServiceException
	 * @throws IngestionWorkflowManagerException
	 */
	public boolean updateDocumentStatus(Long documentId, Long batchProfileJobId,MediaDocumentStatus mediaDocumentStatus, String env) 
			throws MediaServiceException, IngestionWorkflowManagerException{

		String documentImagesTableStatusCode = ConversionRulesUtil.getHighLevelStatusFrom(mediaDocumentStatus.getMediaMetadataStatus());
		
		String user = "workflow";
		boolean output = batchService.updateBatchManagerTables(String.valueOf(documentId), batchProfileJobId, mediaDocumentStatus, user, env) && 
						 updateDocumentImagesTable(String.valueOf(documentId), documentImagesTableStatusCode, user);
		
		return output;
	}
	
	
	/****************************************************************************************************************
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 					PRIVATE METHODS
	 * 
	 * 
	 * 
	 * 
	 * 
	 ****************************************************************************************************************/
	
	/**
	 * 
	 * @param documentId
	 * @param code
	 * @param updatedBy
	 * @return
	 * @throws MediaServiceException
	 */
	private boolean updateDocumentImagesTable(String documentId, String code, String updatedBy)
			throws MediaServiceException {

		String resource = String.format(DocumentProcessorService.PUT_DOCUMENT_IMAGES_STATUS, documentId);

		NewDocumentStatus status = new NewDocumentStatus();
		status.setStatus(code);
		status.setUpdatedBy(updatedBy);
		Response<Object> response = documentProcessorService.execute(resource, "PUT", status);

		if (response.getData() == null) {
			LOG.error(response.getError().getMessage());
			return false;
		}

		LOG.info("Sent to update DI table " + documentId + " - " + code);

		return true;
	}
	
	
	
	
	/**
	 * @param documentStatus
	 * @param updatedBy
	 * @param metadata
	 * @return
	 * @throws MediaServiceException
	 */
	private Response<MediaMetadataModel> saveMetadataByStatus(final String documentStatus,
			final String updatedBy, final MediaMetadataModel metadata) throws MediaServiceException {
		String resource = PagedResponseMediaMetadataService.PUT_MEDIAMETADATA
				+ metadata.getDocumentId();
		metadata.setDocumentStatus(documentStatus);
		metadata.setUpdatedBy(updatedBy);

		Response<MediaMetadataModel> response = mediaMetadataService.execute(resource, "PUT",
				metadata);
		return response;
	}
}
