/**
 * 
 */
package com.mcmcg.dia.iwfm.service.media;

import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.mcmcg.dia.iwfm.domain.Response;
import com.mcmcg.dia.iwfm.exception.MediaServiceException;

/**
 * @author Jose Aleman
 *
 */
public abstract class BaseService<T> implements IService<T> {

	private static final Logger LOG = Logger.getLogger(BaseService.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	private String env;
	
	public abstract String getName();
	
	@Override
	public void setEnv(String env){
		
		if (StringUtils.isBlank(this.env)){
			
			if (StringUtils.contains(env, "pr")){
				this.env = StringUtils.EMPTY;
			}else{
				this.env = "-" + env;
			}
			
		}
		
	}
	
	protected String getEnv(){
		return env;
	}

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
			String message = "Command : " + getHostname() + getEndpoint() + command + "-->" + e.getMessage();
			LOG.error(message, e);
			throw new MediaServiceException(message, e);
		}

		return returnValue;
	}

	/**
	 * @param command
	 * @param params
	 * @return
	 * @throws RestClientException
	 * @throws URISyntaxException 
	 */
	private Response<T> getPutOrPostResponse(String command, HttpMethod method, Object... params) throws RestClientException, URISyntaxException {
		Response<T> returnValue;
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
	 * @throws URISyntaxException 
	 */
	private Response<T> getGetResponse(String command) throws RestClientException, URISyntaxException {
		Response<T> returnValue;
		ResponseEntity<Response<T>> getResponse = restTemplate.exchange(getHostname() + getEndpoint() + command, HttpMethod.GET,
				HttpEntity.EMPTY, buildParameterizedTypeReference());
		returnValue = getResponse.getBody();
		return returnValue;
	}

	@Override
	public abstract String getEndpoint();

	public abstract ParameterizedTypeReference<Response<T>> buildParameterizedTypeReference();
	
	private String getHostname(){

		String hostname = null;
		if (getEndpoint().contains("http")){
			return StringUtils.EMPTY;
		}else{
			hostname = "http://localhost:8080";
		}
		
/*		if (this.hostname == null){
			HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		    //String scheme = req.getScheme();             // http or https
		    String scheme = "https";             // http or https
		    String serverName = req.getServerName();     // everest-[x].mcmcg.com
		    int serverPort = req.getServerPort();        // 80, 8080 8443 443
		    
		    StringBuilder url = new StringBuilder();
		    url.append(scheme).append("://").append(serverName);
		    if (serverPort != 80 && serverPort != 443) {
		        url.append(":").append(serverPort);
		    }
		    this.hostname = url.toString();
		}
		*/

	    return hostname;
	}

}
