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
import com.mcmcg.dia.profile.model.domain.FieldDefinition;
import com.mcmcg.dia.profile.model.domain.PagedResponse;
import com.mcmcg.dia.profile.model.domain.Response;
import com.mcmcg.dia.profile.service.FieldDefinitionService;
import com.mcmcg.dia.profile.util.EventCode;

@RestController
@RequestMapping(value = "/document-fields/field-definitions")
public class FieldDefinitionRestController extends BaseRestController {

	@Autowired
	private FieldDefinitionService fieldDefinitionService;

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = { "/{id}", "/", "" }, method = { RequestMethod.DELETE, RequestMethod.POST })
	public ResponseEntity<Response<Object>> getOperationNotImplemented() {
		Response<Object> response = buildResponse(EventCode.NOT_IMPLEMENTED.getCode(),
				"Invalid attempt when not allowed", null);
		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response,
				HttpStatus.NOT_IMPLEMENTED);

		return responseEntity;
	}

	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findById(@PathVariable("id") String id)
			throws ServiceException, PersistenceException {
		FieldDefinition domain = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;

		try {
			domain = fieldDefinitionService.retrieveFieldDefinitionById(id);
			message = String.format("Operation Find Field Definition for id = %s ", id);
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
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findAll(
			@RequestParam(name = "filter", defaultValue = "", required = false) String filter,
			@RequestParam(name = "sort", defaultValue = "", required = false) String sort,
			@RequestParam(name = "page", defaultValue = "1", required = false) int page,
			@RequestParam(name = "size", defaultValue = "15", required = false) int size) 
					throws ServiceException, PersistenceException {
		List<FieldDefinition> domainList = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;

		try {
			if (StringUtils.isBlank(filter) && StringUtils.isBlank(sort)) {
				domainList = fieldDefinitionService.retrieveAllFieldDefinition();
				message = "Operation Find All Field Definition";
				response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, domainList);
			} else {
				response = internalRetrieveFieldDefinitions(filter, sort, page, size);
			}
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
		FieldDefinition domain = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;

		try {
			domain = fieldDefinitionService.retrieveByVersion(id, version);
			message = String.format("Operation Find Field Definition for id = %s and version = %s", id, version);
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

	@Auditable(eventCode = EventCode.OBJECT_CREATED)
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> save(@RequestBody FieldDefinition fieldDefinition)
			throws PersistenceException, ServiceException {
		FieldDefinition domain = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		try {
			domain = fieldDefinitionService.saveDocumentFieldsDefinition(fieldDefinition);
			message = String.format("Operation Save Field Definition executed succesfully for ID %s", domain.getId());
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
	public ResponseEntity<Response<Object>> update(@RequestBody FieldDefinition fieldDefinition,
			@PathVariable("id") String id) throws PersistenceException, ServiceException {
		FieldDefinition domain = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		try {
			domain = fieldDefinitionService.saveDocumentFieldsDefinition(fieldDefinition, id);
			message = String.format("Operation Update Field Definition executed succesfully for ID %s", domain.getId());
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
	@RequestMapping(value = "/{id}/versions", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> retrieveVersionsById(@PathVariable("id") String id) {

		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		try {
			List<Long> versionList = fieldDefinitionService.retrieveVersionsById(id);
			message = String.format("Operation Find Field Definition was executed for id = %s ", id);
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
	private Response<Object> internalRetrieveFieldDefinitions(
			@RequestParam(name = "filter", defaultValue = "") String filter,
			@RequestParam(name = "sort", defaultValue = "") String sort,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "15") int size) {
		Response<Object> response = null;
		try {
			PagedResponse<Object> pagedResponse = fieldDefinitionService.retrieveFieldDefinitions(filter, sort, page,
					size);
			response = populateResponse(pagedResponse, "Filter [%s] Sort [%s] Page [%d] Size[%d] ",
					new Object[] { filter, sort, page, size });
		} catch (ServiceException e) {
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), null);
		} catch (PersistenceException e) {
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), e.getMessage(), null);
		} catch (Exception e) {
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), null);
		}

		return response;
	}
}
