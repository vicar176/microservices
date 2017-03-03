package com.mcmcg.dia.profile.restcontroller;

import java.util.List;

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

import com.mcmcg.dia.profile.annotation.Auditable;
import com.mcmcg.dia.profile.exception.PersistenceException;
import com.mcmcg.dia.profile.exception.ServiceException;
import com.mcmcg.dia.profile.model.domain.DocumentFieldsDefinition;
import com.mcmcg.dia.profile.model.domain.Response;
import com.mcmcg.dia.profile.service.DocumentFieldDefinitionService;
import com.mcmcg.dia.profile.util.EventCode;

@RestController
@RequestMapping(value = "/document-fields/document-field-definitions")
public class DocumentFieldDefinitionRestController extends BaseRestController {

	@Autowired
	private DocumentFieldDefinitionService documentFieldDefinitionService;

	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findById(@PathVariable("id") String id)
			throws ServiceException, PersistenceException {
		DocumentFieldsDefinition domain = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;

		try {
			domain = documentFieldDefinitionService.retrieveDocumentFieldDefinitionById(id);
			message = String.format("Operation Find Document Field Definition for id = %s ", id);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, domain);
		} catch (ServiceException ex) {
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), ex.getMessage(), response);
		} catch (PersistenceException ex) {
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), ex.getMessage(), response);
		} catch (Exception ex) {
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), response);
		}
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{id}/versions/{version}", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findByversion(@PathVariable("id") String id,
			@PathVariable("version") Long version) throws ServiceException, PersistenceException {
		DocumentFieldsDefinition domain = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;

		try {
			domain = documentFieldDefinitionService.retrieveByVersion(id, version);
			message = String.format("Operation Find Document Field Definition for id = %s and version = %s", id,
					version);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, domain);
		} catch (ServiceException ex) {
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), ex.getMessage(), response);
		} catch (PersistenceException ex) {
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), ex.getMessage(), response);
		} catch (Exception ex) {
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), response);
		}
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{id}/versions", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> retrieveVersionsById(@PathVariable("id") String id) {

		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		try {
			List<Long> versionList = documentFieldDefinitionService.retrieveVersionsById(id);
			message = String.format("Operation Find Document Field Profile was executed for id = %s ", id);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, versionList);
		} catch (PersistenceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), message, null);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), message, null);
		} catch (Exception e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, null);
		}
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findByDocumentTypeCode(
			@RequestParam(value = "code", required = false) String code) throws PersistenceException, ServiceException {
		
		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		try {
			if(!StringUtils.isBlank(code)){
				DocumentFieldsDefinition domain = documentFieldDefinitionService.retrieveDocumentFieldDefinitionByDocTypeCode(code);
				message = String.format("Operation Find Document Field Definition for Document Type Code = %s ", code);
				response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, domain);
			} else {
				List<DocumentFieldsDefinition> domainsList = documentFieldDefinitionService.retrieveAllDocumentFieldsDefinition();
				message = "Operation Find all Document Field Definition";
				response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, domainsList);
			}
		} catch (ServiceException ex) {
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), ex.getMessage(), null);
		} catch (PersistenceException ex) {
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), ex.getMessage(), null);
		} catch (Exception ex) {
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	@Auditable(eventCode = EventCode.OBJECT_CREATED)
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> save(@RequestBody DocumentFieldsDefinition documentFieldsDefinition)
			throws PersistenceException, ServiceException {
		DocumentFieldsDefinition domain = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		try {
			domain = documentFieldDefinitionService.saveDocumentFieldsDefinition(documentFieldsDefinition);
			message = String.format("Operation Save Document Field Definition executed succesfully for ID %s", domain.getId());
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, domain);
		} catch (ServiceException ex) {
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), ex.getMessage(), null);
		} catch (PersistenceException ex) {
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), ex.getMessage(), null);
		} catch (Exception ex) {
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> update(@RequestBody DocumentFieldsDefinition documentFieldsDefinition, @PathVariable("id") String id)
			throws PersistenceException, ServiceException {
		DocumentFieldsDefinition domain = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		try {
			domain = documentFieldDefinitionService.saveDocumentFieldsDefinition(documentFieldsDefinition, id);
			message = String.format("Operation Update Document Field Definition executed succesfully for ID %s", domain.getId());
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, domain);
		} catch (ServiceException ex) {
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), ex.getMessage(), null);
		} catch (PersistenceException ex) {
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), ex.getMessage(), null);
		} catch (Exception ex) {
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = { "/{id}/versions/{version}", "/{id}", "/", "" }, method = { RequestMethod.DELETE, RequestMethod.POST })
	public ResponseEntity<Response<Object>> opperationsNotImplemented() {
		Response<Object> response = buildResponse(EventCode.NOT_IMPLEMENTED.getCode(),
				"Invalid attempt when not allowed", null);
		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response,
				HttpStatus.NOT_IMPLEMENTED);

		return responseEntity;
	}

}
