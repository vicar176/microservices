package com.mcmcg.dia.documentprocessor.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mcmcg.dia.documentprocessor.exception.MediaServiceException;

/**
 * 
 * @author Jose Aleman
 *
 */
@Component
public class ServiceLocator {

	@Autowired
	InitialContext initialContext;

	public final static String WORKFLOW_SHUTDOWN_STATE_SERVICE = WorkflowShutdownStateService.class.getSimpleName();


	public ServiceLocator() {
	}

	/**
	 * 
	 * @param serviceName
	 * @return
	 * @throws MediaServiceException
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
