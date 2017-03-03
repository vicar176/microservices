package com.mcmcg.dia.batchscheduler.service.batchmanager;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mcmcg.dia.batchscheduler.exception.ServiceException;

/**
 * 
 * @author Jose Aleman
 *
 */
@Component
public class ServiceLocator {

	@Autowired
	InitialContext initialContext;

	public final static String BATCHPROFILE_SERVICE_NAME = BatchProfileService.class.getSimpleName();
	public final static String BATCHPROFILEJOB_SERVICE_NAME = BatchProfileJobService.class.getSimpleName();
	
	public ServiceLocator() {
		
	}

	/**
	 * 
	 * @param serviceName
	 * @return
	 * @throws ServiceException
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public IService getService(String serviceName) throws ServiceException {

		IService service = (IService) initialContext.lookup(serviceName);

		if (service == null) {
			throw new ServiceException("Service " + serviceName + " was not found in context");
		}

		return service;
	}
}
