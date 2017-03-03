package com.mcmcg.dia.batchmanager.restcontroller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.batchmanager.domain.BatchCreationOnDemand;
import com.mcmcg.dia.batchmanager.domain.BatchProfileSearchFilterDetail;
import com.mcmcg.dia.batchmanager.domain.BatchProfileWithAction;
import com.mcmcg.dia.batchmanager.domain.Response;
import com.mcmcg.dia.batchmanager.domain.SearchFilter;
import com.mcmcg.dia.batchmanager.entity.BatchProfile;
import com.mcmcg.dia.batchmanager.exception.PersistenceException;
import com.mcmcg.dia.batchmanager.exception.ServiceException;
import com.mcmcg.dia.batchmanager.service.BatchProfileService;
import com.mcmcg.dia.batchmanager.service.OnDemandService;
import com.mcmcg.dia.batchmanager.util.EventCode;



/**
 * @author Victor Arias
 *
 */
@RestController
@RequestMapping(value = "/batch-profiles")
public class BatchProfileRestController extends BaseRestController {
	
	private static final Logger LOG = Logger.getLogger(BatchProfileRestController.class);
	
	@Autowired
	private OnDemandService onDemandService;
	
	@Autowired
	private BatchProfileService batchProfileService;
	
	@RequestMapping(value = "/on-demand", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> saveOnDemand(@RequestBody BatchCreationOnDemand batchCreationDomain) {
		Response<Object> response = null;
		Long key = null;
		String message = StringUtils.EMPTY;
		try {
			key = onDemandService.saveOnDemand(batchCreationDomain);
			message = "On Demand Request Success";
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message , key);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), message, -1);
		}catch (Throwable e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, -1);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	/**
	 * 
	 * @param batchProfile
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<Response<BatchProfile>> saveOnDemand(@RequestBody BatchProfile batchProfile) {
		Response<BatchProfile> response = null;
		BatchProfile key = null;
		String message = StringUtils.EMPTY;
		try {
			key = batchProfileService.save(batchProfile);
			message = "IngestionStep saved success";
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, key);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), message, (BatchProfile)null);
		}catch (Throwable e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), message, (BatchProfile)null);
		}

		return new ResponseEntity<Response<BatchProfile>>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{batchProfileId}/search-filters", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> getSearchFiltersByBatchProfileId(@PathVariable("batchProfileId") Long batchProfileId){
		
		Response<Object> response = null;

		BatchProfileSearchFilterDetail batchProfileSearchFilterDetail = new BatchProfileSearchFilterDetail();

		try {
			batchProfileSearchFilterDetail = batchProfileService.getSearchFiltersByBatchProfileId(batchProfileId);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "GetSearchFilters retreived successfully",
					batchProfileSearchFilterDetail);

		} catch (Exception ex) {

			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), true);
		}

		if (response == null) {
			response = buildResponse(0, "SearchFilters not found", batchProfileSearchFilterDetail);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
		return responseEntity;

		
	}
	
	/**
	 * @return
	 * @throws PersistenceException
	 * @author salam4
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> getBatchProfileWithAction() throws PersistenceException  {

		Response<Object> response = null;

		List<BatchProfileWithAction> batchProfileList = null;

		try {
			batchProfileList = batchProfileService.retriveBatchProfileWithAction();
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "batchProfileJob retreived successfully",
					batchProfileList);

		} catch (Exception ex) {

			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), true);
		}

		if (response == null) {
			response = buildResponse(0, "batchProfileJob not found", batchProfileList);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
		return responseEntity;
	}
	
	
	/**
	 * 
	 * @param batchProfileJob
	 * @return
	 */
	@RequestMapping(value = "/batch-profile", method = RequestMethod.POST)
	public ResponseEntity<Response<BatchProfile>> saveBatchProfile(@RequestBody BatchProfile batchProfile, @RequestBody Map<SearchFilter, Set<Object>> searchFilterMap) {
		
		Response<BatchProfile> response = null;
		try {
			
			BatchProfile result = batchProfileService.save(batchProfile);
			
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Updated counters : ", result);

		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), (BatchProfile)null);
		}

		ResponseEntity<Response<BatchProfile>> responseEntity = new ResponseEntity<Response<BatchProfile>>(response, HttpStatus.OK);
		
		return responseEntity;

	}
	
	
	
}
