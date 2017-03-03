package com.mcmcg.utility.aop;

import java.util.Date;

import javax.annotation.PostConstruct;

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

import com.mcmcg.utility.annotation.Auditable;
import com.mcmcg.utility.domain.History;
import com.mcmcg.utility.domain.Response;
import com.mcmcg.utility.util.EventCode;
import com.mcmcg.utility.util.MetaDataUtil;

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
	public final String SVC_DIA_UTILITY = "SVC-DIA-UTILITY";

	private String endpoint;

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

		//audit(o, auditable);

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
			History history = new History(SVC_DIA_UTILITY, auditable.eventCode().getCode(), message,
					MetaDataUtil.formatDate(new Date()));
			Response response = restTemplate.postForObject(endpoint, history, Response.class);
			LOG.info("Auditing OBJ: " + response.getData());
		}
	}

	/**
	 * @param returnedValue
	 * @param auditable
	 */
	@SuppressWarnings(value = { "unchecked", "rawtypes" })
	private void postEventToHistory(Object returnedValue, Auditable auditable) {
		try {
			ResponseEntity<Response<Object>> response = (ResponseEntity<Response<Object>>) returnedValue;
			int code = getCode(response.getBody(), auditable);
			String message = response.getBody().getError().getMessage();
			History history = new History(SVC_DIA_UTILITY, code, message, MetaDataUtil.formatDate(new Date()));
			Response responseHistory = restTemplate.postForObject(endpoint, history, Response.class);
			LOG.info("Auditing OBJ: " + responseHistory.getData() + " Response Data : " + response.getBody().getData());
		} catch (Throwable t) {
			String message = "Unable to save event to History : " + t.getMessage();
			LOG.warn(message, t);
		}

	}

	/**
	 * 
	 * @param response
	 * @param auditable
	 * @return
	 */
	private int getCode(Response<Object> response, Auditable auditable) {

		return (response.getError().getCode() == 0) ? auditable.eventCode().getCode()
				: response.getError().getCode();
	}
}
