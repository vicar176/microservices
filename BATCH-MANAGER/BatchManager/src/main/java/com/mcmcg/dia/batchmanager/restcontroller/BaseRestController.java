package com.mcmcg.dia.batchmanager.restcontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mcmcg.dia.batchmanager.domain.Response;
import com.mcmcg.dia.batchmanager.entity.BaseEntity;
import com.mcmcg.dia.batchmanager.entity.BatchProfile;
import com.mcmcg.dia.batchmanager.entity.BatchProfileJob;
import com.mcmcg.dia.batchmanager.entity.DocumentException;
import com.mcmcg.dia.batchmanager.util.EventCode;

/**
 * @author jaleman
 *
 */
public abstract class BaseRestController {


	/**
	 * 
	 * @param code
	 * @param message
	 * @param domain
	 * @return
	 */
	protected Response<Object> buildResponse(int code, String message, Object object) {

		Response<Object> response = new Response<Object>();

		Response.Error error = new Response.Error();
		error.setCode(code);
		error.setMessage(message);

		response.setError(error);
		response.setData(object);

		return response;
	}

	/**
	 * 
	 * @param code
	 * @param message
	 * @param domain
	 * @return
	 */
	protected Response<BatchProfileJob> buildResponse(int code, String message, BatchProfileJob object) {

		Response<BatchProfileJob> response = new Response<BatchProfileJob>();

		Response.Error error = new Response.Error();
		error.setCode(code);
		error.setMessage(message);

		response.setError(error);
		response.setData(object);

		return response;
	}
	
	
	/**
	 * 
	 * @param code
	 * @param message
	 * @param domain
	 * @return
	 */
	protected Response<BatchProfile> buildResponse(int code, String message, BatchProfile object) {

		Response<BatchProfile> response = new Response<BatchProfile>();

		Response.Error error = new Response.Error();
		error.setCode(code);
		error.setMessage(message);

		response.setError(error);
		response.setData(object);

		return response;
	}
	/**
	 * 
	 * @param code
	 * @param message
	 * @param domain
	 * @return
	 */
	protected Response<DocumentException> buildResponse(int code, String message, DocumentException object) {

		Response<DocumentException> response = new Response<DocumentException>();

		Response.Error error = new Response.Error();
		error.setCode(code);
		error.setMessage(message);

		response.setError(error);
		response.setData(object);

		return response;
	}
	/**
	 * @param id
	 * @param response
	 * @param portfolio
	 * @return
	 */
	protected Response<Object> populateBaseDomainResponse(Object domain, String message, Object[] params) {
		Response<Object> response = null; 
		String newMessage = "Request Success: " + message;
		if (domain != null) {
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), String.format(newMessage + " were found", params), domain);
		}else{
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), String.format(newMessage + " were not found", params), domain);
		}
		return response;
	}

	/**
	 * 
	 * @return
	 */
	protected ResponseEntity<Response<Object>> methodsNotImplemented() {
		Response<Object> response = buildResponse(EventCode.NOT_IMPLEMENTED.getCode(), "Invalid attempt when not allowed", (BaseEntity) null);
		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response,
				HttpStatus.NOT_IMPLEMENTED);
		return responseEntity;
	}

}
