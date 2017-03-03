package com.mcmcg.utility.restcontroller;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.utility.annotation.Auditable;
import com.mcmcg.utility.domain.DataElement;
import com.mcmcg.utility.domain.Response;
import com.mcmcg.utility.domain.TemplateMappingProfileModel;
import com.mcmcg.utility.exception.ServiceException;
import com.mcmcg.utility.service.ZoneDataExtractionService;
import com.mcmcg.utility.util.EventCode;

/**
 * 
 * @author wporras
 *
 */
@RestController
@RequestMapping(value = "/document-extractions")
public class DocumentDataExtractionRestController extends BaseRestController {

	@Autowired
	ZoneDataExtractionService zoneDataExtractionService;

	/**
	 * 
	 * @param location
	 * @param template
	 * @return ResponseEntity<Response<Object>>
	 */
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> extractDocumentData(@RequestParam("location") String location,
																@RequestParam(value="bucket", defaultValue=StringUtils.EMPTY) String bucket,
																@RequestBody TemplateMappingProfileModel template) {

		Response<Object> response = null;
		List<DataElement> dataElements = null;

		try {
			dataElements = zoneDataExtractionService.extractDocumentData(location, template, bucket);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Extracted data successfully", dataElements);
		} catch (ServiceException ex) {
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), ex.getMessage(), null);
		} catch (Exception ex) {
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/snippets", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> createSnippets(@RequestParam("location") String location, 
														   @RequestParam(value="bucket", defaultValue=StringUtils.EMPTY) String bucket,
														   @RequestBody TemplateMappingProfileModel template) {

		Response<Object> response = null;
		Boolean createSnippets = null;

		try {
			createSnippets = zoneDataExtractionService.createSnippets(location, template, bucket);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Created Snippets => ", createSnippets);
		} catch (ServiceException ex) {
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), ex.getMessage(), null);
		} catch (Exception ex) {
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @param location
	 * @param template
	 * @return ResponseEntity<Response<Object>>
	 */
	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE })
	public ResponseEntity<Response<Object>> notImplementedExtractDocumentData(@RequestParam("location") String location,
			@RequestBody(required = false) TemplateMappingProfileModel template) {

		return methodsNotImplemented();
	}
}
