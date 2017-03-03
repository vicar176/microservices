package com.mcmcg.dia.ingestionState.restcontroller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.ingestionState.annotation.Auditable;
import com.mcmcg.dia.ingestionState.exception.ServiceException;
import com.mcmcg.dia.ingestionState.model.domain.Response;
import com.mcmcg.dia.ingestionState.model.domain.WorkflowShutdownState;
import com.mcmcg.dia.ingestionState.service.WorkflowShutdownStateService;
import com.mcmcg.dia.ingestionState.util.EventCode;

@RestController
@RequestMapping(value = "/workflow-shutdown")
public class WorkflowShutdownStateRestController extends BaseRestController {
	
	@Autowired
	private WorkflowShutdownStateService workflowShutdownStateService;
	
	@Auditable
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> save(@RequestBody WorkflowShutdownState workflowShutdownState) {
		Response<Object> response = null;
		WorkflowShutdownState domain = null;
		String message = StringUtils.EMPTY;
		try {
			domain = workflowShutdownStateService.save(workflowShutdownState);
			message = "workflowShutdownState saved success";
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, domain);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, domain);
		} catch (Throwable t) {
			message = t.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, domain);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	
	@Auditable
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> find() {
		Response<Object> response = null;
		WorkflowShutdownState domain = null;
		String message = StringUtils.EMPTY;
		try {
			domain = workflowShutdownStateService.find();
			message = "workflowShutdownState find success";
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, domain);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, domain);
		} catch (Throwable t) {
			message = t.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, domain);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

}
