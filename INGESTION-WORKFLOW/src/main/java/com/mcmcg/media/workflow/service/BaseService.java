/**
 * 
 */
package com.mcmcg.media.workflow.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.mcmcg.media.workflow.configuration.sqs.SQSCommon;
import com.mcmcg.media.workflow.service.domain.Response;
import com.mcmcg.media.workflow.service.exception.MediaServiceException;

/**
 * @author jaleman
 *
 */
public abstract class BaseService<T> implements IService<T> {

	public enum Methods {
		GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

		private String value;

		private Methods(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}
	}

	private static final Logger LOG = Logger.getLogger(BaseService.class);

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 
	 */
	@Override
	public final Response<T> execute(String command, String httpMethod, Object... params) throws MediaServiceException{

		Response<T> returnValue = null;
		
		Methods method = Methods.valueOf(httpMethod);
		
		if (method == null){
			method = Methods.GET;
		}
		
		try{
			switch (method) {

			case GET:
				returnValue = getGetResponse(command);
				break;

			case POST:
				returnValue = getPutOrPostResponse(command, HttpMethod.POST, params);
				break;
				
			case DELETE:
				delete(command, params);
				break;
				
			case PUT:
				returnValue = getPutOrPostResponse(command, HttpMethod.PUT, params);

			}
		}
		catch (Throwable e){
			//LOG.error(e);
			throw new MediaServiceException(getEndpoint() + command + "--->" + e.getMessage(), e);
		}

		return returnValue;
	}

	/**
	 * @param command
	 * @param params
	 * @return
	 * @throws RestClientException
	 */
	private void delete(String command, Object... params)
			throws RestClientException {

		if (params.length > 0){
			LOG.debug(SQSCommon.getJsonObject(params[0]));
		}

		restTemplate.delete(getEndpoint() + command, params);
		
	}
	
	/**
	 * @param command
	 * @param params
	 * @return
	 * @throws RestClientException
	 */
	private Response<T> getPutOrPostResponse(String command, HttpMethod method, Object... params)
			throws RestClientException {
		Response<T> returnValue = null;
		HttpEntity<Object> entity = (params != null && params.length > 0 && params[0] != null) ? new HttpEntity<Object>(params[0]) : null;
		if (params.length > 0){
			LOG.debug(SQSCommon.getJsonObject(params[0]));
		}
		ResponseEntity<Response<T>> response = restTemplate.exchange(getEndpoint() + command, method,
				entity, buildParameterizedTypeReference());
		returnValue = response.getBody();
		return returnValue;
	}

	/**
	 * @param command
	 * @return
	 * @throws RestClientException
	 */
	private Response<T> getGetResponse(String command) throws RestClientException {
		Response<T> returnValue = null;
		ResponseEntity<Response<T>> getResponse = restTemplate.exchange(getEndpoint() + command, HttpMethod.GET,
				HttpEntity.EMPTY, buildParameterizedTypeReference());
		returnValue = getResponse.getBody();
		return returnValue;
	}

	public abstract String getEndpoint();

	public abstract ParameterizedTypeReference<Response<T>> buildParameterizedTypeReference();

	public abstract String getName();

}
