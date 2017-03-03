package com.mcmcg.dia.profile.restcontroller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.mcmcg.dia.profile.model.domain.OriginalLenderList;
import com.mcmcg.dia.profile.model.domain.PagedResponse;
import com.mcmcg.dia.profile.model.domain.Response;
import com.mcmcg.dia.profile.model.domain.TemplateMappingProfile;
import com.mcmcg.dia.profile.service.TemplateMappingService;
import com.mcmcg.dia.profile.util.EventCode;

/**
 * 
 * @author wporras
 *
 */
@RestController
@RequestMapping(value = "/template-mapping-profiles")
public class TemplateMappingRestController extends BaseRestController{

	@Autowired
	TemplateMappingService templateMappingService;

	/**
	 * Save Template Mapping Profile
	 * 
	 * @param templateMapping
	 * @return ResponseEntity<Response<TemplateMappingProfile>>
	 */
	@Auditable(eventCode = EventCode.OBJECT_CREATED)
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Response<Object>> saveTemplateMappingProfile(
			@RequestBody TemplateMappingProfile templateMapping) {

		TemplateMappingProfile domain = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		
		try{
			domain = templateMappingService.saveTemplateMappingProfile(templateMapping);
			message = "Operation save Template Mapping Profile executed succesfully for ID " + templateMapping.getId();
			response = buildResponse(EventCode.OBJECT_CREATED.getCode(), message, domain);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), message, domain);
		} catch (PersistenceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), message, domain);
		} catch (Exception e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, domain);
		}
		return new ResponseEntity<Response<Object>>(response, HttpStatus.CREATED);
	}

	/**
	 * Update Template Mapping Profile
	 * 
	 * @param id
	 * @param templateMapping
	 * @return Response<TemplateMappingProfile>
	 */
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> updateTemplateMappingProfile(@PathVariable("id") String id,
			@RequestBody TemplateMappingProfile templateMapping) {

		TemplateMappingProfile domain = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		
		try{
			domain = templateMappingService.updateTemplateMappingProfile(id, templateMapping);
			message = "Operation update Template Mapping Profile executed for ID " + id;
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, domain);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), message, domain);
		} catch (PersistenceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), message, domain);
		} catch (Exception e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, domain);
		}
		
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	/**
	 * Find Template Mapping Profiles by DocumentType, Seller and Original Lender.
	 * Filter by affinity level. Used by Ingestion Module
	 * 
	 * @param documentTypeCode
	 * @param sellerId
	 * @param originalLenderName
	 * @param affinities
	 * @return Response<TemplateMappingProfileList>
	 */
	@RequestMapping(method = RequestMethod.GET)
	public Response<Object> findByDocumentTypeAndSellerAndOriginalLender(
			@RequestParam(name = "documentType.code", defaultValue = "" ) String documentTypeCode,
			@RequestParam(name = "seller.id", defaultValue = "0") Long sellerId,
			@RequestParam(name = "originalLender.name", defaultValue = "") String originalLenderName,
			@RequestParam(name = "affinities", defaultValue = "false") boolean hasAffinities,
			@RequestParam(name = "filter", defaultValue = "") String filter,
			@RequestParam(name = "sort", defaultValue = "") String sort,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "15") int size) {

		Response<Object> response = null;
		
		if (StringUtils.isNotBlank(documentTypeCode)) {
			response = retrieveByDocumentTypeAndSellerAndOriginalLender(documentTypeCode, sellerId, originalLenderName,
					hasAffinities);
		} else {
			response = retrieveTemplateMappings(filter, sort, page, size);
		}				
		
		return response;
	}
	
	/**
	 * Find Template Mapping Profiles by DocumentType, Seller and Original Lenders.
	 * Filter by affinity level.
	 * 
	 * @param documentTypeCode
	 * @param sellerId
	 * @param originalLenders
	 * @param affinities
	 * @return Response<TemplateMappingProfileList>
	 */
	@RequestMapping(value="search", method = RequestMethod.POST)
	public Response<Object> findByDocumentTypeAndSellerAndOriginalLenderList(
			@RequestParam(name = "documentType.code", defaultValue = "" ) String documentTypeCode,
			@RequestParam(name = "seller.id", defaultValue = "0") Long sellerId,
			@RequestParam(name = "affinities", defaultValue = "false") boolean hasAffinities,
			@RequestParam(name = "filter", defaultValue = "") String filter,
			@RequestParam(name = "sort", defaultValue = "") String sort,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "15") int size,
			@RequestBody OriginalLenderList originalLenders) {

		Response<Object> response = null;
		
		if (StringUtils.isNotBlank(documentTypeCode)) {
			response = retrieveByDocumentTypeAndSellerAndOriginalLenderList(documentTypeCode, sellerId,
					originalLenders.getOriginalLenders(), hasAffinities);
		} else {
			response = retrieveTemplateMappings(filter, sort, page, size);
		}	
		
		return response;
	}

	/**
	 * Delete Template Mapping Profile by Id
	 * 
	 * @param id
	 * @return
	 */
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Response<Object>> notImplementedDeleteTemplateMappingProfile(@PathVariable("id") String id) {
		return methodsNotImplemented();
	}
	
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{id}/versions/{version}", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> retreveByVersion(@PathVariable("id") String id, @PathVariable("version") Long version) {

		TemplateMappingProfile templateMappingProfile = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		try {
			templateMappingProfile = templateMappingService.retrieveByVersion(id, version);
			message = String.format("Operation Find Template Mapping Profile was executed for version = %s ", version);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, templateMappingProfile);
		} catch (PersistenceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), message, templateMappingProfile);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), message, templateMappingProfile);
		} catch (Exception e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, templateMappingProfile);
		}
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{id}/versions", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> retrieveVersionsById(@PathVariable("id") String id) {

		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		try {
			List<Long> versionList = templateMappingService.retrieveVersionsById(id);
			message = String.format("Operation Find Template Mapping Profile was executed for id = %s ", id);
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

	
	/**
	 * Used by Ingestion module
	 * 
	 * @param documentTypeCode
	 * @param sellerId
	 * @param originalLenderName
	 * @param hasAffinities
	 * @return
	 */
	private Response<Object> retrieveByDocumentTypeAndSellerAndOriginalLender(String documentTypeCode, Long sellerId,
			String originalLenderName, boolean hasAffinities) {
		
		List<TemplateMappingProfile> templatesList = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;

		try {
			message = "Operation find Template Mapping Profile executed";
			
			Set<String> lenders = new HashSet<String>(Arrays.asList(originalLenderName));
			templatesList = templateMappingService.findByDocumentTypeAndSellerAndOriginalLenderList(documentTypeCode,
					sellerId, lenders, hasAffinities);
			
			if(templatesList != null){
				for (TemplateMappingProfile template : templatesList) {
					template.setOriginalLenders(null);
				}
			}			
			
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, templatesList);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), message, templatesList);
		} catch (PersistenceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), message, templatesList);
		} catch (Exception e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, templatesList);
		}
		
		return response;
	}
	
	private Response<Object> retrieveByDocumentTypeAndSellerAndOriginalLenderList(String documentTypeCode, Long sellerId,
			Set<String> originalLenders, boolean hasAffinities) {
		
		List<TemplateMappingProfile> templatesList = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;

		try{
			message = "Operation find Template Mapping Profile executed";
			templatesList = templateMappingService.findByDocumentTypeAndSellerAndOriginalLenderList(documentTypeCode,
					sellerId, originalLenders, hasAffinities);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, templatesList);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), message, templatesList);
		} catch (PersistenceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), message, templatesList);
		} catch (Exception e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, templatesList);
		}
		return response;
	}
	
	/**
	 * 
	 * @param filter
	 * @param sort
	 * @param page
	 * @param size
	 * @return
	 */
	private Response<Object> retrieveTemplateMappings(			
						@RequestParam(name = "filter", defaultValue = "") String filter,
						@RequestParam(name = "sort", defaultValue = "") String sort,
						@RequestParam(name = "page", defaultValue = "1") int page,
						@RequestParam(name = "size", defaultValue = "15") int size) {

		Response<Object> response = null;

		try {

			PagedResponse<Object> pagedResponse = templateMappingService.retrieveTemplateMappings(filter, sort, page, size);
			response = populateResponse(pagedResponse, "Filter [%s] Sort [%s] Page [%d] Size[%d] ", new Object[]{filter, sort, page, size});
			
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
