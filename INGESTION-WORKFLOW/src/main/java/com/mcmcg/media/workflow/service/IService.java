package com.mcmcg.media.workflow.service;

import com.mcmcg.media.workflow.service.domain.Response;
import com.mcmcg.media.workflow.service.exception.MediaServiceException;

public interface IService<T> {

	public String getName();
	
	public Response<T> execute(String command, String httpMethod, Object... params ) throws MediaServiceException;
	
	public String getEndpoint();
}
