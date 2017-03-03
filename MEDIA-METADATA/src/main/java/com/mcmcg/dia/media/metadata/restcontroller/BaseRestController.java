/**
 * 
 */
package com.mcmcg.dia.media.metadata.restcontroller;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mcmcg.dia.media.metadata.model.BaseModel;
import com.mcmcg.dia.media.metadata.model.domain.PagedResponse;
import com.mcmcg.dia.media.metadata.model.domain.Response;
import com.mcmcg.dia.media.metadata.util.EventCode;

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
	 * @param id
	 * @param response
	 * @param portfolio
	 * @return
	 */
	protected Response<Object> populateResponse(Object object, String message, Object[] params) {
		Response<Object> response = null; 
		String newMessage = "Request Success: " + message;
		if (object != null) {
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), String.format(newMessage + " was found", params), object);
		}else{
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), String.format(newMessage + " was not found", params), object);
		}
		return response;
	}
	
	/**
	 * @param id
	 * @param response
	 * @param portfolio
	 * @return
	 */
	protected Response<Object> populateBaseDomainResponse(BaseModel domain, String message, Object[] params) {
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
		Response<Object> response = buildResponse(EventCode.NOT_IMPLEMENTED.getCode(), "Invalid attempt when not allowed", (BaseModel) null);
		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response,
				HttpStatus.NOT_IMPLEMENTED);
		return responseEntity;
	}

}
