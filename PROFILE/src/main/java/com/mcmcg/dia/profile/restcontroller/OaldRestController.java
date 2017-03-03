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
import com.mcmcg.dia.profile.model.domain.OaldProfile;
import com.mcmcg.dia.profile.model.domain.PagedResponse;
import com.mcmcg.dia.profile.model.domain.Response;
import com.mcmcg.dia.profile.service.OaldService;
import com.mcmcg.dia.profile.util.EventCode;

/**
 * 
 * @author Victor Arias
 *
 */
@RestController
@RequestMapping(value = "/oald-profiles")
public class OaldRestController extends BaseRestController {

	@Autowired
	OaldService oaldService;

	@Auditable(eventCode = EventCode.OBJECT_CREATED)
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<Response<Object>> saveOaldProfile(@RequestBody OaldProfile oaldProfile) {

		OaldProfile oald = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;

		try {
			oald = oaldService.saveOaldProfile(oaldProfile);
			message = String.format("Operation Save Oald Profile was executed succesfully for id %s",
					oaldProfile.getId());
			response = buildResponse(EventCode.OBJECT_CREATED.getCode(), message, oald);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), message, oald);
		} catch (PersistenceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), message, oald);
		} catch (Exception e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, oald);
		}
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findById(@PathVariable("id") String id)
			throws ServiceException, PersistenceException {
		OaldProfile domain = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;

		try {
			domain = oaldService.retrieveById(id);
			message = String.format("Operation Find Oald Profile for id = %s ", id);
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
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> updateOaldProfile(@PathVariable("id") String id,
			@RequestBody OaldProfile oaldProfile) {
		OaldProfile oald = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		try {
			oald = oaldService.updateOaldProfile(id, oaldProfile);
			message = String.format("Operation Update Oald Profile was executed for ID %s", id);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, oald);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), message, oald);
		} catch (PersistenceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), message, oald);
		} catch (Exception e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, oald);
		}
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Response<Object>> deleteOaldProfile(@PathVariable("id") String id) {
		return methodsNotImplemented();
	}

	/**
	 * 
	 * @param productGroupCode
	 * @param portfolioId
	 * @param originalLenderName
	 * @param filter
	 * @param sort
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findByPGCAndPIdAndOLN(
			@RequestParam(name = "productGroup.code", defaultValue = "") String productGroupCode,
			@RequestParam(name =  "portfolio.id", required = false) Long portfolioId,
			@RequestParam(name =  "originalLender.name", required = false) String originalLenderName,
			@RequestParam(name = "filter", defaultValue = "") String filter,
			@RequestParam(name = "sort", defaultValue = "") String sort,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "15") int size) {
		
		ResponseEntity<Response<Object>> responseEntity = null;
		
		if (StringUtils.isNotBlank(productGroupCode)){
			
			responseEntity =retrieveByPGCAndPIdAndOLN(productGroupCode, portfolioId, originalLenderName);
					
		}else{
			
			responseEntity = retrieveOalds(filter, sort, page, size);
			
		}
		
		return responseEntity;
	}
	
	@RequestMapping(value = "/{id}/versions/{version}", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> retreveByVersion(@PathVariable("id") String id, @PathVariable("version") Long version) {
		OaldProfile oald = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		
		try {
			oald = oaldService.findByVersion(id, version);
			message = String.format("Operation Find Oald Profile was executed for version = %s ", version);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, oald);
		} catch (PersistenceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), message, oald);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), message, oald);
		} catch (Exception e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, oald);
		}
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{id}/versions", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> retrieveVersionsById(@PathVariable("id") String id) {

		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		try {
			List<Long> versionList = oaldService.retrieveVersionsById(id);
			message = String.format("Operation Find OALD Profile was executed for id = %s ", id);
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
	 * @param productGroupCode
	 * @param portfolioId
	 * @param originalLenderName
	 * @return
	 */
	private ResponseEntity<Response<Object>> retrieveByPGCAndPIdAndOLN(String productGroupCode, Long portfolioId,
			String originalLenderName) {
		OaldProfile oald = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		try {
			oald = oaldService.findByProductGroupPortfolioIdOriginalLender(productGroupCode, portfolioId,
					originalLenderName);
			message = String.format(
					"Operation Find Oald Profile was executed for parameters productGroupCode = %s, portfolioId = %s, originalLenderName = %s",
					productGroupCode, portfolioId, originalLenderName);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, oald);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), message, oald);
		} catch (PersistenceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), message, oald);
		} catch (Exception e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, oald);
		}
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	/**
	 * 
	 * @param filter
	 * @param sort
	 * @param page
	 * @param size
	 * @return
	 */
	private ResponseEntity<Response<Object>> retrieveOalds(String filter,String sort,int page,int size) {

		Response<Object> response = null;

		try {

			PagedResponse<Object> pagedResponse = oaldService.retrieveOalds(filter, sort, page, size);
			response = populateResponse(pagedResponse, "Filter [%s] Sort [%s] Page [%d] Size[%d] ", new Object[]{filter, sort, page, size});
			
		} catch (ServiceException e) {
			
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), null);
			
		} catch (PersistenceException e) {
			
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), e.getMessage(), null);
			
		} catch (Exception e) {
			
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), null);
			
		}
		
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
}
