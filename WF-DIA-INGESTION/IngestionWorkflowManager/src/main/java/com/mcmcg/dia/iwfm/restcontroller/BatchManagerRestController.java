package com.mcmcg.dia.iwfm.restcontroller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.iwfm.annotation.Diagnostics;
import com.mcmcg.dia.iwfm.domain.MediaDocumentStatus;
import com.mcmcg.dia.iwfm.domain.Response;
import com.mcmcg.dia.iwfm.exception.IngestionWorkflowManagerException;
import com.mcmcg.dia.iwfm.exception.MediaServiceException;
import com.mcmcg.dia.iwfm.service.BatchService;
import com.mcmcg.dia.iwfm.util.EventCode;

/**
 * 
 * @author jaleman
 *
 */

@RestController
public class BatchManagerRestController extends BaseRestController {

	private static final Logger LOG = Logger.getLogger(BatchManagerRestController.class);

	@Autowired
	BatchService batchService;
	/**
	 * 
	 * PUT /scheduler-service/batch-profile-jobs?documentId={documentId}&batchProfileId={profileId}&processed={processed}&exception={exception}&failed={failed}
	 * 
	 * @param portfolioNumber
	 * @param templateName
	 * @return
	 */
	@Diagnostics
	@RequestMapping(value = "/batch-profile-jobs", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> updateBatchProfileJobs(@RequestParam String documentId,
																   @RequestParam Long batchProfileJobId,
																   @RequestParam(defaultValue = "dev", required = false) String env,
																   @RequestParam(defaultValue = "Success", required = false) String status,
																   @RequestParam(required = true) String user) {
		
		Response<Object> response = null;

		try {

			MediaDocumentStatus documentStatus = new MediaDocumentStatus();
			documentStatus.setDocumentStatus(status);
			
			boolean result = batchService.updateBatchManagerTables(documentId, batchProfileJobId, documentStatus, user, env);
			response = populateResponse(result,	"DocumentId %s batchProfileJobId %d Status ", 
												 new Object[] { documentId, batchProfileJobId, status });
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
	
	

}