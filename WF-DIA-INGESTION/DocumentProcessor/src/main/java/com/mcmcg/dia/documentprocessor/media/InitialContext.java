package com.mcmcg.dia.documentprocessor.media;

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
	WorkflowShutdownStateService workflowShutdownStateService;
	
	@Autowired
	S3UtilityService s3UtilityService;
	
	@Autowired
	OnDemandBatchManagerService onDemandBatchManagerService;
	
	@Autowired
	DocumentExceptionBatchManagerService documentExceptionBatchManagerService;
	

	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap<String, IService> serviceMap;

	@SuppressWarnings("rawtypes")
	@PostConstruct
	public void init() {
		serviceMap = new ConcurrentHashMap<String, IService>();
		
		serviceMap.put(WorkflowShutdownStateService.class.getSimpleName(), workflowShutdownStateService);
		serviceMap.put(S3UtilityService.class.getSimpleName(), s3UtilityService);
		serviceMap.put(OnDemandBatchManagerService.class.getSimpleName(), onDemandBatchManagerService);
		serviceMap.put(DocumentExceptionBatchManagerService.class.getSimpleName(), documentExceptionBatchManagerService);

	}

	public InitialContext() {
	}

	public Object lookup(String serviceName) {
		return serviceMap.get(serviceName);
	}
}
