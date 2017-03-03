package com.mcmcg.dia.documentprocessor.media;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.mcmcg.dia.documentprocessor.exception.MediaServiceException;
import com.mcmcg.dia.documentprocessor.exception.ServiceException;
import com.mcmcg.dia.iwfm.domain.Response;

/**
 * @author Jose Aleman
 *
 */
public abstract class BaseService<T> implements IService<T> {

	private static final Logger LOG = Logger.getLogger(BaseService.class);

	@Autowired
	private RestTemplate restTemplate;

	private volatile String hostname;

	public abstract String getName();

	@Override
	public final Response<T> execute(String command, String httpMethod, Object... params) throws MediaServiceException {

		Response<T> returnValue = null;
		try {
			switch (httpMethod) {

			case IService.GET:
				returnValue = getGetResponse(command);
				break;

			case IService.POST:
				returnValue = getPutOrPostResponse(command, HttpMethod.POST, params);
				break;
				
			case IService.POST_FILE:
				returnValue = postOrPutMultipart(command, HttpMethod.POST, params);
				break;

			case IService.PUT:
				returnValue = getPutOrPostResponse(command, HttpMethod.PUT, params);

			}
		} catch (Throwable e) {
			LOG.error(e);
			throw new MediaServiceException(e.getMessage(), e);
		}

		return returnValue;
	}

	/**
	 * 
	 * @param command
	 * @param method
	 * @param params
	 * @return
	 * @throws RestClientException
	 */
	private Response<T> getPutOrPostResponse(String command, HttpMethod method, Object... params)
			throws RestClientException {

		Response<T> returnValue;
		HttpEntity<Object> entity = (params != null && params.length > 0 && params[0] != null)
				? new HttpEntity<Object>(params[0]) : null;

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
		ResponseEntity<Response<T>> getResponse = restTemplate.exchange(getHostname() + getEndpoint() + command,
				HttpMethod.GET, HttpEntity.EMPTY, buildParameterizedTypeReference());
		returnValue = getResponse.getBody();
		return returnValue;
	}
	
	private Response<T> postOrPutMultipart(String command, HttpMethod method, Object... params)
			throws RestClientException, ServiceException {

		Response<T> returnValue;
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		String tempFileName;
		FileOutputStream fo;
		
		// set the multipart file as a parameter		
		try {
			MultipartFile multipartFile = (MultipartFile) params[0];
			tempFileName = "/tmp/" + multipartFile.getOriginalFilename();
			fo = new FileOutputStream(tempFileName);
			fo.write(multipartFile.getBytes());
			fo.close();
		} catch (IOException e) {
			String message = "An Error occurred loading the file -- " + e.getMessage();
			LOG.error(message, e);
			throw new ServiceException(message, e);
		}		

		map.add("multipartFile", new FileSystemResource(tempFileName));

		// Set http Headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		
		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
				map, headers);

        ResponseEntity<Response<T>> response = restTemplate.exchange(getHostname() + getEndpoint() + command, method, 
        		requestEntity, buildParameterizedTypeReference());
		
        returnValue = response.getBody();
        return returnValue;
	}

	@Override
	public abstract String getEndpoint();

	public abstract ParameterizedTypeReference<Response<T>> buildParameterizedTypeReference();

	private String getHostname() {

		if (getEndpoint().contains("http")) {
			return StringUtils.EMPTY;
		}

		if (this.hostname == null) {
			HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();
			String scheme = "https"; // http or https
			String serverName = req.getServerName(); // everest-[x].mcmcg.com
			int serverPort = req.getServerPort(); // 80, 8080 8443 443

			StringBuilder url = new StringBuilder();
			url.append(scheme).append("://").append(serverName);
			if (serverPort != 80 && serverPort != 443) {
				url.append(":").append(serverPort);
			}
			this.hostname = url.toString();
		}

		LOG.info("Hostname -----------> " + hostname);
		
		return this.hostname;
	}

}
