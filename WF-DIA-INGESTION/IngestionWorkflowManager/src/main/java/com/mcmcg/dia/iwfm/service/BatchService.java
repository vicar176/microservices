/**
 * 
 */
package com.mcmcg.dia.iwfm.service;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.iwfm.domain.MediaDocumentStatus;
import com.mcmcg.dia.iwfm.domain.Response;
import com.mcmcg.dia.iwfm.exception.IngestionWorkflowManagerException;
import com.mcmcg.dia.iwfm.exception.MediaServiceException;
import com.mcmcg.dia.iwfm.service.media.BatchManagerService;
import com.mcmcg.dia.iwfm.service.media.DocumentProcessorService;
import com.mcmcg.dia.iwfm.service.media.IService;
import com.mcmcg.dia.iwfm.service.media.ServiceLocator;

/**
 * @author jaleman
 *
 */
@Service
public class BatchService extends BaseIngestionWorkflowService {

	private final static Logger LOG = Logger.getLogger(BatchService.class);

	@Autowired
	private ServiceLocator serviceLocator;

	private IService<Boolean> batchService;

	@Autowired
	DocumentProcessorService documentProcessorService;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {

		try {
			batchService = serviceLocator.getService(ServiceLocator.BATCH_MANAGER_SERVICE_NAME);
		} catch (MediaServiceException e) {
			LOG.warn(e);
			// fallback
		}

	}

	/**
	 * 
	 */
	public BatchService() {
	}

	/**
	 * 
	 * 
	 * 
	 * @param documentId
	 * @param batchProfileId
	 * @param processed
	 * @param exception
	 * @param failed
	 * @return
	 * @throws MediaServiceException
	 * @throws IngestionWorkflowManagerException
	 */
	public Boolean updateBatchManagerTables(String documentId, Long batchProfileJobId, MediaDocumentStatus status, 
											String user, String env) 
									throws MediaServiceException, IngestionWorkflowManagerException {

		boolean isOK = updateBatchProfileJob(documentId, batchProfileJobId, status.getDocumentStatus(), user, env);

		return isOK;
	}

	/**
	 * 
	 * PUT /scheduler-service/batch-profile-jobs?documentId={documentId}&batchProfileId={profileId}&processed={processed}&exception={exception}&failed={failed}
	 * 
	 * @param documentId
	 * @param batchProfileJobId
	 * @param processed
	 * @param exception
	 * @param failed
	 * @return
	 * @throws MediaServiceException
	 * @throws IngestionWorkflowManagerException
	 */
	private boolean updateBatchProfileJob(String documentId, Long batchProfileJobId, String status, String user, String env) 
														throws MediaServiceException, IngestionWorkflowManagerException {
		boolean isOK = false;
		String resource = String.format(BatchManagerService.PUT_BATCH_PROFILE_JOBS, documentId, batchProfileJobId, status, user);

		batchService.setEnv(env);
		Response<Boolean> response = batchService.execute(resource, "PUT");

		if (response == null || response.getData() == null) {

			throw new IngestionWorkflowManagerException(response.getError().getMessage());

		}
		
		isOK = response.getData();
		return isOK;
	}

}
