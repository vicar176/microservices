package com.mcmcg.dia.documentprocessor.restcontroller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mcmcg.dia.documentprocessor.common.Auditable;
import com.mcmcg.dia.documentprocessor.common.EventCode;
import com.mcmcg.dia.documentprocessor.exception.ServiceException;
import com.mcmcg.dia.documentprocessor.service.DocumentProcessorService;
import com.mcmcg.dia.iwfm.domain.BatchCreationOnDemand;
import com.mcmcg.dia.iwfm.domain.ReprocessWrapper;
import com.mcmcg.dia.iwfm.domain.Response;
import com.mcmcg.dia.iwfm.entity.Action;

/**
 * 
 * @author wporras
 *
 */
@RestController
@RequestMapping(value = "/batches")
public class DocumentProcessorRestController extends BaseRestController {

	private static final Logger LOG = Logger.getLogger(DocumentProcessorRestController.class);

	@Autowired
	private DocumentProcessorService documentProcessorService;

	@RequestMapping(value = "/on-demand", method = RequestMethod.POST)
	public ResponseEntity<Response<Object>> processOnDemandBatch(
			@RequestParam("multipartFile") MultipartFile multipartFile,
			@RequestParam("user") String user,
			@RequestParam("name") String name,
			@RequestParam("description") String description){

		Response<Object> response = null;
		BatchCreationOnDemand batchCreationDomain =null;

		try {
			
			batchCreationDomain = new BatchCreationOnDemand(new Action(1L, "Ingest"), multipartFile.getOriginalFilename(), name,
					description, "OnDemand", user);
			documentProcessorService.processOnDemandBatch(multipartFile, batchCreationDomain);

			response = buildResponse(EventCode.OBJECT_CREATED.getCode(),
					"A new on-demand batch have been created succesfully", Boolean.TRUE);
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), Boolean.FALSE);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), Boolean.FALSE);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;

	}
	
	@RequestMapping(value = "/on-demand/reprocess",  method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> reprocessOnDemandBatch(
			@RequestParam("user") String user,
			@RequestBody ReprocessWrapper wrapper){

		Response<Object> response = null;

		try {
			documentProcessorService.reprocessOnDemandBatch(wrapper.getReprocessList(), user);

			response = buildResponse(EventCode.OBJECT_CREATED.getCode(),
					"A new reprocess on-demand batch have been created succesfully", Boolean.TRUE);
			
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), Boolean.FALSE);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), Boolean.FALSE);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;

	}

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(method = { RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.GET })
	public ResponseEntity<Response<Object>> notImplementedProcessOnDemandBatch(
			@RequestParam(value = "user", required = false) String user) {

		Response<Object> response = buildResponse(EventCode.NOT_IMPLEMENTED.getCode(),
				"Invalid attempt when not allowed", null);

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response,
				HttpStatus.NOT_IMPLEMENTED);

		return responseEntity;
	}
}