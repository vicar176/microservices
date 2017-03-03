package com.mcmcg.dia.documentprocessor.common;

import java.util.Date;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mcmcg.dia.iwfm.domain.BaseDomain;
import com.mcmcg.dia.iwfm.domain.History;
import com.mcmcg.dia.iwfm.domain.Response;

/**
 * 
 * @author Jose Aleman
 *
 */
@Aspect
@Component
public class AuditableAspect {
	private final static Logger LOG = Logger.getLogger(AuditableAspect.class);

	private int started = 0;
	private int closed = 0;

	@Autowired
	RestTemplate restTemplate;

	@Value("${media.history.service.url}")
	private String HISTORY_ENDPOINT;

	public final String SERVICE_NAME = "SVC-DIA-DOCUMENT-PROCESSOR";

	public AuditableAspect() {

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
			History history = new History(SERVICE_NAME, auditable.eventCode().getCode(), message,
					MediaUtil.formatDate(new Date()));
			Response response = restTemplate.postForObject(HISTORY_ENDPOINT, history, Response.class);
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
			ResponseEntity<Response<BaseDomain>> responseEntity = (ResponseEntity<Response<BaseDomain>>) returnedValue;
			int code = getCode(responseEntity, auditable);
			String message = responseEntity.getBody().getError().getMessage();
			History history = new History(SERVICE_NAME, code, message, MediaUtil.formatDate(new Date()));
			Response response = restTemplate.postForObject(HISTORY_ENDPOINT, history, Response.class);
			LOG.info("Auditing OBJ: " + response.getData() + " Response Data : " + responseEntity.getBody().getData());
		} catch (Throwable t) {
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
	private int getCode(ResponseEntity<Response<BaseDomain>> responseEntity, Auditable auditable) {

		return (responseEntity.getBody().getError().getCode() == 0) ? auditable.eventCode().getCode()
				: responseEntity.getBody().getError().getCode();
	}
}
