/**
 * 
 */
package com.mcmcg.ingestion.service.media;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mcmcg.ingestion.domain.Response;
import com.mcmcg.ingestion.exception.MediaServiceException;
import com.mcmcg.ingestion.restcontroller.IngestionRestController;
import com.mcmcg.ingestion.util.IngestionUtils;

/**
 * @author Jose Aleman
 *
 */
public abstract class BaseService<T> implements IService<T> {

	private static final Logger LOG = Logger.getLogger(BaseService.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	private String hostname;
	
	public static final String ERROR_504_GATEWAY_TIMEOUT = "504 GATEWAY_TIMEOUT";
	public static final String ERROR_503_SERVICE_UNAVAILABLE = "503 Service Unavailable";
	public static final String SLOWDOWN = "SLOWDOWN";
	public static final String ERROR_500_SERVER_ERROR = "500 Internal Server Error";

	public abstract String getName();

	/**
	 * 
	 */
	@Override
	public final Response<T> execute(String command, String httpMethod, Object... params) throws MediaServiceException{

		Response<T> returnValue = null;
		try{
			switch (httpMethod) {

			case IService.GET:
				returnValue = getGetResponse(command);
				break;

			case IService.POST:
				returnValue = getPutOrPostResponse(command, HttpMethod.POST, params);
				break;
				
			case IService.PUT:
				returnValue = getPutOrPostResponse(command, HttpMethod.PUT, params);

			}
		}
		catch (Throwable e){

			String message = getHostname() + command + "--->" + e.getMessage();
		
			if (StringUtils.contains(e.getMessage().toUpperCase(), ERROR_504_GATEWAY_TIMEOUT.toUpperCase()) || 
					StringUtils.contains(e.getMessage().toUpperCase(), ERROR_500_SERVER_ERROR.toUpperCase()) ||	
					StringUtils.contains(e.getMessage().toUpperCase(), SLOWDOWN.toUpperCase()) ||	
					StringUtils.contains(e.getMessage().toUpperCase(), ERROR_503_SERVICE_UNAVAILABLE.toUpperCase())){
				
				message = IngestionRestController.REPROCESS + " " + message;
				
			}
			
			throw new MediaServiceException(message, e);
		}

		return returnValue;
	}

	/**
	 * @param command
	 * @param params
	 * @return
	 * @throws RestClientException
	 */
	private Response<T> getPutOrPostResponse(String command, HttpMethod method, Object... params) throws RestClientException {
		Response<T> returnValue;
		if (params.length > 0){
			LOG.debug(IngestionUtils.getJsonObject(params[0]));
		}
		HttpEntity<Object> entity = (params != null && params.length > 0 && params[0] != null) ? new HttpEntity<Object>(params[0]) : null;
		
		ResponseEntity<Response<T>> response = restTemplate.exchange(getHostname() + getEndpoint() + command, method,
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
		Response<T> returnValue;
		
		LOG.debug("URL -- > " + getHostname() + getEndpoint() + command);
		
		ResponseEntity<Response<T>> getResponse = restTemplate.exchange(getHostname() + getEndpoint() + command, HttpMethod.GET,
				HttpEntity.EMPTY, buildParameterizedTypeReference());
		returnValue = getResponse.getBody();
		return returnValue;
	}

	@Override
	public abstract String getEndpoint();

	public abstract ParameterizedTypeReference<Response<T>> buildParameterizedTypeReference();

	/**
	 * 
	 * @return
	 */
	private String getHostname(){

		if (getEndpoint().contains("http")){
			return StringUtils.EMPTY;
		}
		
		if (this.hostname == null){
			HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		    //String scheme = req.getScheme();	    	 // http or https
		    String scheme = "https";	    	 // http or https
		    String serverName = req.getServerName();     // everest-[x].mcmcg.com
		    int serverPort = req.getServerPort();        // 80, 8080 8443 443
		    
		    StringBuilder url = new StringBuilder();
		    url.append(scheme).append("://").append(serverName);
		    if (serverPort != 80 && serverPort != 443) {
		        url.append(":").append(serverPort);
		    }
		    this.hostname = url.toString();
		}

	    return this.hostname;
	}
}
