package com.mcmcg.dia.ingestionState.restcontroller;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.ingestionState.annotation.Auditable;
import com.mcmcg.dia.ingestionState.exception.ServiceException;
import com.mcmcg.dia.ingestionState.model.domain.IngestionStep;
import com.mcmcg.dia.ingestionState.model.domain.PagedResponse;
import com.mcmcg.dia.ingestionState.model.domain.Response;
import com.mcmcg.dia.ingestionState.service.IngestionStepService;
import com.mcmcg.dia.ingestionState.util.EventCode;

/**
 * @author Victor Arias
 *
 */
@RestController
@RequestMapping(value = "/ingestion-steps")
public class IngestionStepRestController extends BaseRestController {

	@Autowired
	private IngestionStepService ingestionStepService;

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "", method = { RequestMethod.DELETE, RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<Response<Object>> saveNotSupported(
			@RequestBody(required = false) IngestionStep ingestionStep) {
		return methodsNotImplemented();
	}

	@Auditable
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> save(@RequestBody IngestionStep ingestionStep) {
		Response<Object> response = null;
		IngestionStep domain = null;
		String message = StringUtils.EMPTY;
		try {
			domain = ingestionStepService.save(ingestionStep);
			message = "IngestionStep saved success";
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, domain);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, domain);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "/{id}/version", method = { RequestMethod.DELETE, RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT})
	public ResponseEntity<Response<Object>> updateVersionNotSupported(@PathVariable("id") String id) {
		return methodsNotImplemented();
	}

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "/{id}/status", method = { RequestMethod.DELETE, RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT})
	public ResponseEntity<Response<Object>> updateStatusNotSupported(@PathVariable("id") Long id,
			@RequestBody(required = false) IngestionStep ingestionStep) {
		return methodsNotImplemented();
	}

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "/{documentId}", method = { RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT })
	public ResponseEntity<Response<Object>> findNotSupported(@PathVariable Long documentId,
			@RequestParam("ingestionStateCode") String ingestionStateCode) {
		return methodsNotImplemented();
	}

	@Auditable
	@RequestMapping(value = "/{documentId}", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> find(@PathVariable Long documentId,
			@RequestParam("ingestionStateCode") String ingestionStateCode) {
		Response<Object> response = null;
		IngestionStep domain = null;
		String message = StringUtils.EMPTY;

		try {
			domain = ingestionStepService.findLastByDocIdStCode(documentId, ingestionStateCode);
			message = "Find by documentId " + documentId + " and ingestionStateCode '" + ingestionStateCode + "' executed properly";
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, domain);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, domain);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	
	@Auditable
	@RequestMapping(value = "/documentid/{documentId}", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findByDocumentId(@PathVariable String documentId) {
		Response<Object> response = null;
		List<IngestionStep> domainList = null;
		String message = StringUtils.EMPTY;

		try {
			domainList = ingestionStepService.findByDocumentId(documentId);
			if(domainList.isEmpty()) {
				message = String.format("No Steps found for documentId %s", documentId);
			} else {
				message = "Find by documentId executed properly";
			}
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, domainList);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, domainList);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	
	@Auditable
	@RequestMapping(value = "/workflowid/{workflowId}", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findByWorkflowId(@PathVariable Long workflowId) {
		Response<Object> response = null;
		List<IngestionStep> domainList = null;
		String message = StringUtils.EMPTY;

		try {
			domainList = ingestionStepService.findByWorkflowId(workflowId);
			if(domainList.isEmpty()) {
				message = String.format("No Steps found for workflowId %s", workflowId);
			} else {
				message = "Find by workflowId executed properly";
			}
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, domainList);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, domainList);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ingestion-states", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findAllIngestionStates() {
		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		List<String> ingestionStates = null;
		
		try {
			ingestionStates = ingestionStepService.findAllIngestionStates();
			message = "Find all IngestionStates executed properly";
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, ingestionStates);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, null);
		}
		
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/failed", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findFailedSteps(
			@RequestParam(name = "filter", defaultValue = StringUtils.EMPTY) String filter,
			@RequestParam(name = "sort", defaultValue = "documentId") String sort,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "2147483647") int size) {
			
		Response<Object> response = new Response<Object>();
		try {
			PagedResponse<Map<String, Object>> pagedResponse = ingestionStepService.retrieveStepsFailed(filter, sort, page, size);
			response = populateBaseDomainResponse(pagedResponse, "Page [%s] Size [%s] Filter [%s]", new Object[] { page, size, filter});
		} catch(Exception e) {
			
		}
		
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/failed/documentIds", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findFailedDocumentIds(@RequestParam(name = "filter", defaultValue = StringUtils.EMPTY) String filter) {
		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		List<String> failedDocumentIds = null;
		
		try {
			failedDocumentIds = ingestionStepService.retrieveDocumentIdsFailed(filter);
			message = "Retrieve failed documentIds executed";
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, failedDocumentIds);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, null);
		}
		
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
}
