package com.mcmcg.dia.ingestionState.restcontroller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.ingestionState.annotation.Auditable;
import com.mcmcg.dia.ingestionState.exception.ServiceException;
import com.mcmcg.dia.ingestionState.model.domain.Response;
import com.mcmcg.dia.ingestionState.model.domain.WorkflowState;
import com.mcmcg.dia.ingestionState.service.WorkflowStateService;
import com.mcmcg.dia.ingestionState.util.EventCode;

/**
 * 
 * @author Victor Arias
 *
 */

@RestController
@RequestMapping(value = "/workflow-states")
public class WorkflowStateRestController extends BaseRestController {

	@Autowired
	private WorkflowStateService workflowStateService;

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "", method = { RequestMethod.DELETE, RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<Response<Object>> saveNotSupported(
			@RequestBody(required = false) WorkflowState workflowState) {
		return methodsNotImplemented();
	}

	@Auditable
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> save(@RequestBody WorkflowState workflowState) {
		Response<Object> response = null;
		WorkflowState domain = null;
		String message = StringUtils.EMPTY;
		try {
			domain = workflowStateService.save(workflowState);
			message = "workflowState saved success";
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

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "/{documentId}", method = { RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT })
	public ResponseEntity<Response<Object>> findNotSupported(@PathVariable String documentId) {
		return methodsNotImplemented();
	}

	@Auditable
	@RequestMapping(value = "/{documentId}", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> find(@PathVariable String documentId) {
		Response<Object> response = null;
		WorkflowState domain = null;
		String message = StringUtils.EMPTY;

		try {
			domain = workflowStateService.findByDocumentId(documentId);
			message = "Find by documentId executed properly";
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

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "/{documentId}/workflow-state", method = { RequestMethod.DELETE, RequestMethod.POST,
			RequestMethod.GET })
	public ResponseEntity<Response<Object>> updateWorkflowStateNotSupported(@PathVariable("documentId") String documentId,
			@RequestBody(required = false) WorkflowState workflowState) {
		return methodsNotImplemented();
	}

	@Auditable
	@RequestMapping(value = "/{documentId}/workflow-state", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> updateWorkflowState(@PathVariable("documentId") String documentId,
			@RequestBody WorkflowState workflowState) {
		return update(documentId, workflowState, "state");
	}

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "/{documentId}/force-rerun", method = { RequestMethod.DELETE, RequestMethod.POST,
			RequestMethod.GET })
	public ResponseEntity<Response<Object>> updateForceRerunNotSupported(@PathVariable("documentId") String documentId,
			@RequestBody(required = false) WorkflowState workflowState) {
		return methodsNotImplemented();
	}

	@Auditable
	@RequestMapping(value = "/{documentId}/force-rerun", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> updateForceRerun(@PathVariable("documentId") String documentId,
			@RequestBody WorkflowState workflowState) {
		return update(documentId, workflowState, "rerun");
	}

	private ResponseEntity<Response<Object>> update(@PathVariable("documentId") String documentId,
			@RequestBody WorkflowState workflowState, String action) {
		Response<Object> response = null;
		WorkflowState domain = null;
		String message = StringUtils.EMPTY;

		try {
			domain = workflowStateService.update(documentId, workflowState, action);
			message = "IngestionStep updated success";
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
