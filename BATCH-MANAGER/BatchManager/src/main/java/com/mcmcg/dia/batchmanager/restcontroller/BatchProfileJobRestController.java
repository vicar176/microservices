package com.mcmcg.dia.batchmanager.restcontroller;

import java.util.List;

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

import com.mcmcg.dia.batchmanager.domain.Response;
import com.mcmcg.dia.batchmanager.entity.BatchProfileJob;
import com.mcmcg.dia.batchmanager.exception.PersistenceException;
import com.mcmcg.dia.batchmanager.service.BatchProfileJobService;
import com.mcmcg.dia.batchmanager.util.EventCode;

/**
 * @author pshankar
 *
 */
@RestController
public class BatchProfileJobRestController extends BaseRestController {
	
	private static final Logger LOG = Logger.getLogger(BatchProfileJobRestController.class);
	
	@Autowired
	BatchProfileJobService batchProfileJobService;

	/***
	 * 
	 * @param batchProfileJobId
	 * @param status
	 * @param updatedBy
	 * @return
	 */
	@RequestMapping(value = "/batch-profile-jobs", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> updateBatchProfileJobforDoc(@RequestParam("batchProfileJobId") Long batchProfileJobId, 
											   @RequestParam("documentId") String documentId,	
											   @RequestParam("status") String status,
											   @RequestParam("updatedBy") String updatedBy) {
		
		Response<Object> response = null;
		try {
			
			boolean result = batchProfileJobService.updateCountersById(documentId, batchProfileJobId, status, updatedBy);
			
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Updated counters : ", result);

		} catch (PersistenceException ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), ex.getMessage(),false);

		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), false);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
		return responseEntity;

	}

	/**
	 * 
	 * @param batchProfileJob
	 * @return
	 */
	@RequestMapping(value = "/batch-profile-jobs", method = RequestMethod.POST)
	public ResponseEntity<Response<BatchProfileJob>> saveBatchProfileJob(@RequestBody BatchProfileJob batchProfileJob) {
		
		Response<BatchProfileJob> response = null;
		try {
			
			BatchProfileJob result = batchProfileJobService.save(batchProfileJob);
			
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Updated counters : ", result);

		} catch (PersistenceException ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), ex.getMessage(), (BatchProfileJob)null);

		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), (BatchProfileJob)null);
		}

		ResponseEntity<Response<BatchProfileJob>> responseEntity = new ResponseEntity<Response<BatchProfileJob>>(response, HttpStatus.OK);
		
		return responseEntity;

	}
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/batch-profile-jobs", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> getBatchProfileJob()  {

		Response<Object> response = null;

		List<BatchProfileJob> batchProfileJobList = null;

		try {
			batchProfileJobList = batchProfileJobService.retrieveAllBatchProfileJobs();
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "batchProfileJob retreived successfully",
					batchProfileJobList);

		} catch (PersistenceException ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), ex.getMessage(),true);

		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), true);
		}

		
		
		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
		return responseEntity;
	}
	
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/batch-profile-jobs/{id}", method = RequestMethod.GET)
	public ResponseEntity<Response<BatchProfileJob>> getBatchProfileJob(@PathVariable("id") Long id)  {

		Response<BatchProfileJob> response = null;

		BatchProfileJob batchProfileJob = null;

		try {
			batchProfileJob = batchProfileJobService.retrieveBatchProfileJobById(id);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "batchProfileJob retreived successfully",	batchProfileJob);

		} catch (PersistenceException ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), ex.getMessage(),(BatchProfileJob)null);

		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), (BatchProfileJob)null);
		}

		ResponseEntity<Response<BatchProfileJob>> responseEntity = new ResponseEntity<Response<BatchProfileJob>>(response, HttpStatus.OK);
		
		return responseEntity;
	}

}
