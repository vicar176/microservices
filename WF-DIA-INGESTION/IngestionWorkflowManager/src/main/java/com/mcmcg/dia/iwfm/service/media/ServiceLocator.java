package com.mcmcg.dia.iwfm.service.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mcmcg.dia.iwfm.exception.IngestionWorkflowManagerException;
import com.mcmcg.dia.iwfm.exception.MediaServiceException;

/**
 * 
 * @author Jose Aleman
 *
 */
@Component
public class ServiceLocator {

	@Autowired
	InitialContext initialContext;

	public final static String DOCUMENT_SERVICE_NAME = MediaMetadataDocumentService.class.getSimpleName();
	public final static String PAGED_METADATA_SERVICE_NAME = PagedResponseMediaMetadataService.class.getSimpleName();
	public final static String RESPONSE_METADATA_SERVICE_NAME = ResponseMediaMetadataService.class.getSimpleName();
	
	public final static String DOCUMENT_PROCESSOR_SERVICE_NAME = DocumentProcessorService.class.getSimpleName();

	public final static String INGESTION_STEP_SERVICE_NAME = IngestionStepService.class.getSimpleName();
	
	public final static String BATCH_MANAGER_SERVICE_NAME = BatchManagerService.class.getSimpleName();
	
	public ServiceLocator() {
		
	}

	/**
	 * 
	 * @param serviceName
	 * @return
	 * @throws IngestionWorkflowManagerException
	 */
	@SuppressWarnings("rawtypes")
	public IService getService(String serviceName) throws MediaServiceException {

		IService service = (IService) initialContext.lookup(serviceName);

		if (service == null) {
			throw new MediaServiceException("Service " + serviceName + " was not found in context");
		}

		return service;
	}
}
