package com.mcmcg.dia.media.metadata.restcontroller;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.media.metadata.annotation.Auditable;
import com.mcmcg.dia.media.metadata.exception.ServiceException;
import com.mcmcg.dia.media.metadata.model.domain.MediaOald;
import com.mcmcg.dia.media.metadata.model.domain.Response;
import com.mcmcg.dia.media.metadata.model.domain.Response.Error;
import com.mcmcg.dia.media.metadata.service.MediaOALDService;
import com.mcmcg.dia.media.metadata.util.EventCode;
	
/**
 * 
 * @author Victor Arias
 *
 */
@RestController
@RequestMapping(value = "/media-oalds")
public class MediaOALDRestController extends BaseRestController {

	private static final Logger LOG = Logger.getLogger(MediaOALDRestController.class);
	@Autowired
	MediaOALDService mediaOALDService;
	
	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "", method = {RequestMethod.PUT})
	public ResponseEntity<Response<Object>> notSupportedforPut() {
		return methodsNotImplemented();
	}
	
	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = { "", "/{documentId}"}, method = {RequestMethod.DELETE, RequestMethod.POST})
	public ResponseEntity<Response<Object>> notSupported() {
		return methodsNotImplemented();
	}
	
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{documentId}", method = RequestMethod.GET)
	public ResponseEntity<Response<MediaOald>> findByDocumentId(@PathVariable("documentId") Long documentId) {
		LOG.info("findByDocumentId MediaOALDRestController start");
		MediaOald metadata = null;
		Response<MediaOald> response = new Response<MediaOald>();
		Error status = new Error();

		try {
			metadata = mediaOALDService.findMediaOaldByDocumentId(documentId);
			status.setCode(EventCode.REQUEST_SUCCESS.getCode());
			status.setMessage("Find by documentId executed");
		} catch (ServiceException e) {
			status.setCode(EventCode.SERVICE_ERROR.getCode());
			status.setMessage(e.getMessage());
		}

		response.setData(metadata);
		response.setError(status);

		return new ResponseEntity<Response<MediaOald>>(response, HttpStatus.OK);
	}

		
	@Auditable
	@RequestMapping(value = "/{documentId}", method = RequestMethod.PUT)
	public ResponseEntity<Response<MediaOald>> save(@PathVariable("documentId") Long documentId,
			@RequestBody MediaOald mediaOald) {
		LOG.info("Save MediaOALDRestController start");
		MediaOald mediaOaldResponse = null;
		Response<MediaOald> response = new Response<MediaOald>();
		Error status = new Error();
		HttpStatus httpStatus = null;

		try {
			mediaOaldResponse = mediaOALDService.save(mediaOald, documentId);
			if (mediaOaldResponse != null) {
				status.setCode(EventCode.OBJECT_CREATED.getCode());
				httpStatus = HttpStatus.CREATED;
			} /*else {
				status.setCode(EventCode.REQUEST_SUCCESS.getCode());
				httpStatus = HttpStatus.OK;
			}*/
			status.setMessage("Media OALD object saved");
		} catch (ServiceException e) {
			status.setCode(EventCode.SERVICE_ERROR.getCode());
			status.setMessage(e.getMessage());
			httpStatus = HttpStatus.OK;
		}

		response.setData(mediaOaldResponse);
		response.setError(status);

		return new ResponseEntity<Response<MediaOald>>(response, httpStatus);
	}

	
}
