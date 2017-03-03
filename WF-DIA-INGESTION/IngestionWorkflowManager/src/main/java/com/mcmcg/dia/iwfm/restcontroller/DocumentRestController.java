package com.mcmcg.dia.iwfm.restcontroller;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.iwfm.annotation.Diagnostics;
import com.mcmcg.dia.iwfm.domain.MediaDocumentStatus;
import com.mcmcg.dia.iwfm.domain.Response;
import com.mcmcg.dia.iwfm.exception.IngestionWorkflowManagerException;
import com.mcmcg.dia.iwfm.exception.MediaServiceException;
import com.mcmcg.dia.iwfm.service.DocumentQueueingService;
import com.mcmcg.dia.iwfm.service.DocumentService;
import com.mcmcg.dia.iwfm.util.ConversionRulesUtil;
import com.mcmcg.dia.iwfm.util.EventCode;

/**
 * 
 * @author jaleman
 *
 */

@RestController
public class DocumentRestController extends BaseRestController {

	private static final Logger LOG = Logger.getLogger(DocumentRestController.class);

	@Autowired
	private DocumentService documentService;

	@Autowired
	private DocumentQueueingService documentQueueingService;
	

	/**
	 * 
	 * 
	 * Reprocess by Document Id
	 * 
	 * @param portfolioNumber
	 * @param templateName
	 * @return
	 */
	@Diagnostics
	@RequestMapping(value = "/documents/{documentId}", method = RequestMethod.POST)
	public ResponseEntity<Response<Object>> sendDocumentToIngestionQueue(@PathVariable("documentId") Long documentId,
			@RequestParam("updatedBy") String updatedBy,
			@RequestParam(defaultValue = "", required = false) String documentName) {
		Response<Object> response = null;

		try {
			
			List<String> documentList = documentQueueingService.sendDocumentsToIngestionQueue(Arrays.asList(String.valueOf(documentId)), updatedBy);
			
			response = populateResponse(documentList, "documentId  %s", new Object[] { documentId });
			
		} catch (MediaServiceException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), (Object) null);
		} catch (IngestionWorkflowManagerException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), (Object) null);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), (Object) null);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;

	}
	
	/***
	 * 
	 * 
	 * Reprocess failed documents given a Search Filter
	 * 
	 * @param filter
	 * @param updatedBy
	 * @return
	 */
	@Diagnostics
	@RequestMapping(value = "/documents/failed", method = RequestMethod.POST)
	public ResponseEntity<Response<Object>> sendDocumentListToIngestionQueue(@RequestParam(defaultValue = StringUtils.EMPTY) String filter,
			@RequestParam("updatedBy") String updatedBy) {
		Response<Object> response = null;
		List<String> documentList = null;
		List<String> documentIdlist = null;
		try {
			documentIdlist = documentService.retrieveFailedDocumentIds(filter);
			
			documentList = documentQueueingService.sendDocumentsToIngestionQueue(documentIdlist, updatedBy);
			
			response = populateResponse(documentList, "documents to be reprocessed --->  %s", new Object[] { documentList.size() });
			
		} catch (MediaServiceException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), (Object) null);
		} catch (IngestionWorkflowManagerException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), (Object) null);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), (Object) null);
		}
		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;
	}

	/**
	 * 
	 * Update document status including Reprocess 
	 * 
	 * 
	 * @param documentId
	 * @param mediaDocumentStatus
	 * @return
	 */
	@Diagnostics
	@RequestMapping(value = "/documents/{documentId}/status", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> updateDocumentStatus(@PathVariable("documentId") Long documentId, 
																 @RequestParam("env") String env,
																 @RequestParam("batchProfileJobId") Long batchProfileJobId,
																 @RequestBody MediaDocumentStatus mediaDocumentStatus) {

		Response<Object> response = null;
		try{
			
			boolean output = documentQueueingService.updateDocumentStatus(documentId, batchProfileJobId, mediaDocumentStatus, env);
			
			response = populateResponse(output, "documentId  %s with status %s ", 
												new Object[] {documentId, mediaDocumentStatus.getMediaMetadataStatus()}); 
			
		}
		catch (MediaServiceException | IngestionWorkflowManagerException e){
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), (Object) null);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), (Object) null);
		}

		LOG.info(response.getError().getMessage());

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;

	}
	
}