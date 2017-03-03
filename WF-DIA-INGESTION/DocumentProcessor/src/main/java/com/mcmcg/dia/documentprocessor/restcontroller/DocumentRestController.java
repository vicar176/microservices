package com.mcmcg.dia.documentprocessor.restcontroller;

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
import com.mcmcg.dia.documentprocessor.service.DocumentService;
import com.mcmcg.dia.iwfm.domain.NewDocumentStatus;
import com.mcmcg.dia.iwfm.domain.Response;

/**
 * 
 * @author Victor Arias
 *
 */

@RestController
public class DocumentRestController extends BaseRestController {

	@Autowired
	private DocumentService documentService;

	@RequestMapping(value = "/documents/{documentId}/status", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> updateIngestionTracker(@PathVariable String documentId,
			@RequestBody NewDocumentStatus documentStatus) {

		Response<Object> response = null;
		Boolean success = false;
		String message = StringUtils.EMPTY;
		try {
			success = documentService.updateIngestionTracker(documentId, documentStatus);
			if(success) {
				message = "Update IngestionTracker executed properly";
			} else {
				message = "Unable to update IngestionTracker";
			}
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, success);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, success);
		} catch (Throwable t) {
			message = t.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, success);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	
}
