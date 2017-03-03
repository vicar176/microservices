package com.mcmcg.dia.media.metadata.aop;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mcmcg.dia.media.metadata.annotation.Auditable;
import com.mcmcg.dia.media.metadata.model.BaseModel;
import com.mcmcg.dia.media.metadata.model.domain.History;
import com.mcmcg.dia.media.metadata.model.domain.Response;
import com.mcmcg.dia.media.metadata.util.EventCode;
import com.mcmcg.dia.media.metadata.util.MediaMetadataUtil;

/**
 * 
 * @author Jose Aleman
 *
 */
@Aspect
@Component
@PropertySource(value = "classpath:/application.properties")
public class AuditableAspect {
	private final static Logger LOG = Logger.getLogger(AuditableAspect.class);

	private int started = 0;
	private int closed = 0;
	@Autowired
	Environment env;

	@Autowired
	RestTemplate restTemplate;

	private final String MEDIA_HISTORY_ENDPOINT = "media.history.endpoint";
	public final String SVC_DIA_METADATA = "SVC-DIA-METADATA";

	private String endpoint;
	private String hostname;

	/**
	 * 
	 */
	public AuditableAspect() {

	}

	@PostConstruct
	public void init() {
		endpoint = env.getProperty(MEDIA_HISTORY_ENDPOINT);
	}

	@Around("execution(public * *(..)) && @annotation(auditable)")
	public Object aroundEvent(ProceedingJoinPoint pjp, Auditable auditable) throws Throwable {

		Object o = pjp.proceed();

		audit(o, auditable);

		return o;
	}

	// ***************************************************************************************
	//
	// PRIVATE METHODS
	//
	// ***************************************************************************************

	/***
	 * 
	 * @param returnedValue
	 * @param auditable
	 */
	private void audit(Object returnedValue, Auditable auditable) {

		if (returnedValue != null) {
			postEventToHistory(returnedValue, auditable);
		} else {
			postStartStopEventToHistory(auditable);
		}
	}

	/**
	 * @param auditable
	 */
	@SuppressWarnings("rawtypes")
	private void postStartStopEventToHistory(Auditable auditable) {
		String message = auditable.eventCode() == EventCode.START ? "service-start successful"
				: "service-stop successful";
		started = auditable.eventCode() == EventCode.START ? started + 1 : 0;
		closed = auditable.eventCode() == EventCode.STOP ? closed + 1 : 0;

		if (started <= 1 && closed <= 1) {
			History history = new History(SVC_DIA_METADATA, auditable.eventCode().getCode(), message,
					MediaMetadataUtil.formatDate(new Date()));
			Response response = restTemplate.postForObject(getHostname() +  endpoint, history, Response.class);
			LOG.info("Auditing OBJ: " + response.getData());
		}
	}

	/**
	 * @param returnedValue
	 * @param auditable
	 */
	@SuppressWarnings(value = { "unchecked", "rawtypes" })
	private void postEventToHistory(Object returnedValue, Auditable auditable) {
		try{
			ResponseEntity<Response<BaseModel>> responseEntity = (ResponseEntity<Response<BaseModel>>) returnedValue;
			int code = getCode(responseEntity, auditable);
			String message = responseEntity.getBody().getError().getMessage();
			History history = new History(SVC_DIA_METADATA, code, message, MediaMetadataUtil.formatDate(new Date()));
			Response response = restTemplate.postForObject(getHostname() + endpoint, history, Response.class);
			LOG.info("Auditing OBJ: " + response.getData() + " Response Data : " + responseEntity.getBody().getData()) ;
		}
		catch (Throwable t){
			String message = "Unable to save event to History : " + t.getMessage();
			LOG.warn(message, t);
		}

	}

	/**
	 * 
	 * @param responseEntity
	 * @param auditable
	 * @return
	 */
	private int getCode(ResponseEntity<Response<BaseModel>> responseEntity, Auditable auditable) {

		return (responseEntity.getBody().getError().getCode() == 0) ? auditable.eventCode().getCode()
				: responseEntity.getBody().getError().getCode();
	}
	
	/**
	 * 
	 * @return
	 */
	private String getHostname(){

		if (endpoint.contains("http")){
			return null;
		}
		
		if (this.hostname == null){
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

	    return this.hostname;
	}
}
