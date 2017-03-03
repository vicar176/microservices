package com.mcmcg.utility.restcontroller;


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

import com.mcmcg.utility.annotation.Auditable;
import com.mcmcg.utility.domain.MediaMetadataModel;
import com.mcmcg.utility.domain.Response;
import com.mcmcg.utility.exception.ServiceException;
import com.mcmcg.utility.service.TagPdfService;
import com.mcmcg.utility.util.EventCode;



@RestController
@RequestMapping(value = "/tag-pdfs")
public class TagPdfRestController extends BaseRestController {

	@Autowired
	TagPdfService tagPdfService;


	/**
	 * 
	 * @param mediaMetadataModel
	 * @return
	 */
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{documentId}", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> tagPdfWithMetadata( @PathVariable String documentId,
																@RequestParam(value="bucket", defaultValue=StringUtils.EMPTY) String bucket,
																@RequestBody MediaMetadataModel  mediaMetadataModel ){


		//		MediaMetadataModel  mediaMetadata = null;
		Response<Object> response = null;
		MediaMetadataModel  mediaMetadataTagged = null;
		try {
			if (documentId == null || !StringUtils.equalsIgnoreCase(documentId, String.valueOf(mediaMetadataModel.getDocumentId()))
					|| !StringUtils.equalsIgnoreCase(documentId, String.valueOf(mediaMetadataModel.getDocument().getDocumentName().getDocumentId()))){
				response = buildResponse(EventCode.SERVICE_ERROR.getCode(), "DocumentId is not the same as provided in mediaMetadata.getDocumentId()", null);
			}
			else{

				mediaMetadataTagged = tagPdfService.tagPdfwithMetadata(mediaMetadataModel, bucket);

				response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Document tagged successfully", mediaMetadataTagged);

			}
		} 
		catch (ServiceException ex) {
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), ex.getMessage(),response);
		}

		catch (Exception ex) 
		{
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), ex.getMessage(), null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}



	@RequestMapping(value = { "/{documentId}","/"}, method = { RequestMethod.DELETE,RequestMethod.GET , RequestMethod.POST})
	public ResponseEntity<Response<Object>> getOperationNotImplemented() {
		Response<Object> response = buildResponse(EventCode.NOT_IMPLEMENTED.getCode(),
				"Invalid attempt when not allowed",  null);
		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response,
				HttpStatus.NOT_IMPLEMENTED);

		return responseEntity;
	}

}
