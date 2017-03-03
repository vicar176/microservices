package com.mcmcg.dia.batchscheduler.service.batchmanager;

import com.mcmcg.dia.batchmanager.domain.Response;
import com.mcmcg.dia.batchscheduler.exception.ServiceException;

/**
 * @author jaleman
 *
 */
public interface IService<T> {

	public final String GET  = "GET";
	public final String POST = "POST";
	public final String PUT = "PUT";
	
	public String getName();
	
	public Response<T> execute(String command, String httpMethod, Object... params ) throws ServiceException;
	
	public String getEndpoint();
	
}
