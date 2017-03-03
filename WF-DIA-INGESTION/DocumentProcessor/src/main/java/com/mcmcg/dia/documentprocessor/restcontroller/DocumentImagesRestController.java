package com.mcmcg.dia.documentprocessor.restcontroller;

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
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.documentprocessor.common.EventCode;
import com.mcmcg.dia.documentprocessor.exception.ServiceException;
import com.mcmcg.dia.documentprocessor.service.DocumentImagesService;
import com.mcmcg.dia.iwfm.domain.DocumentList;
import com.mcmcg.dia.iwfm.domain.FoundAndNotFoundDocumentList;
import com.mcmcg.dia.iwfm.domain.NewDocumentStatus;
import com.mcmcg.dia.iwfm.domain.Response;


@RestController
@RequestMapping(value = "/document-images")
public class DocumentImagesRestController extends BaseRestController {

	@Autowired
	private DocumentImagesService documentImagesService;
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/ingestion-status", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findIngestionStatus() {
		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		List<Map<String,Object>>  ingestionStates = null;
		
		try {
			ingestionStates = documentImagesService.findDocumentImageStatus();
			message = "Find all IngestionStates executed properly";
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, ingestionStates);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, null);
		}
		
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @param documentId
	 * @param documentStatus
	 * @return
	 */
	@RequestMapping(value = "/documents/{documentId}/status", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> updateDocumentImages(@PathVariable String documentId,
			@RequestBody NewDocumentStatus documentStatus) {

		Response<Object> response = null;
		Boolean success = false;
		String message = StringUtils.EMPTY;
		try {
			success = documentImagesService.updateDocumentImages(documentId, documentStatus);
			if(success) {
				message = "Update DocumentImages executed properly";
			} else {
				message = "Unable to update DocumentImages";
			}
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, success);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), null);
		} catch (Throwable t) {
			message = t.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, success);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	
}
