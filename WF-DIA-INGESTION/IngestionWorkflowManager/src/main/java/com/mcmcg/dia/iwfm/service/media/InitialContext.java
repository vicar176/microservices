package com.mcmcg.dia.iwfm.service.media;

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
	MediaMetadataDocumentService mediaMetadataDocumentService;
	
	@Autowired
	PagedResponseMediaMetadataService pagedResponseMediaMetadataService;
	
	@Autowired
	ResponseMediaMetadataService responseMediaMetadataService;
	
	@Autowired
	DocumentProcessorService documentProcessorService;
	
	@Autowired
	IngestionStepService ingestionStepService;
	
	@Autowired
	BatchManagerService batchManagerService;
	
	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap<String, IService> serviceMap;

	@SuppressWarnings("rawtypes")
	@PostConstruct
	public void init() {
		serviceMap = new ConcurrentHashMap<String, IService>();
		
		serviceMap.put(ServiceLocator.DOCUMENT_SERVICE_NAME, mediaMetadataDocumentService);
		serviceMap.put(ServiceLocator.PAGED_METADATA_SERVICE_NAME, pagedResponseMediaMetadataService);
		serviceMap.put(ServiceLocator.RESPONSE_METADATA_SERVICE_NAME, responseMediaMetadataService);
		
		serviceMap.put(ServiceLocator.DOCUMENT_PROCESSOR_SERVICE_NAME, documentProcessorService);
		serviceMap.put(ServiceLocator.INGESTION_STEP_SERVICE_NAME, ingestionStepService);
		serviceMap.put(ServiceLocator.BATCH_MANAGER_SERVICE_NAME, batchManagerService);
		
	}

	public InitialContext() {
	}

	public Object lookup(String serviceName) {
		return serviceMap.get(serviceName);
	}
}
