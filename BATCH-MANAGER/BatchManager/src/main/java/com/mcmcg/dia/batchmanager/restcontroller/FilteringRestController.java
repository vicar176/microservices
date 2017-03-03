package com.mcmcg.dia.batchmanager.restcontroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcmcg.dia.batchmanager.domain.PagedResponse;
import com.mcmcg.dia.batchmanager.domain.Response;
import com.mcmcg.dia.batchmanager.entity.BatchProfileJobDetails;
import com.mcmcg.dia.batchmanager.entity.BatchProfileJobDocumentDetail;
import com.mcmcg.dia.batchmanager.service.FilteringService;
import com.mcmcg.dia.batchmanager.util.EventCode;

@RestController
@RequestMapping(value = "/filter")
public class FilteringRestController extends BaseRestController {

	private static final Logger LOG = Logger.getLogger(FilteringRestController.class);

	@Autowired
	private FilteringService filteringService;

	@RequestMapping(value = "/batchStatus", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> filterBatchStatus(
			@RequestParam(name = "filter", defaultValue = StringUtils.EMPTY) String filter,
			@RequestParam(name = "sort", defaultValue = "executionStartDateTime") String sort,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "25") int size) {

		Response<Object> response = new Response<Object>();
		try {
			PagedResponse<Map<String, Object>> pagedResponse = filteringService.filteringBatchStatus(filter, sort, page,
					size);
			response = populateBaseDomainResponse(pagedResponse, "Page [%s] Size [%s] Filter [%s]",
					new Object[] { page, size, filter });
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), "False", (Response<Object>) null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/scheduledbatches", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> filterScheduledBatches(
			@RequestParam(name = "filter", defaultValue = StringUtils.EMPTY) String filter,
			@RequestParam(name = "sort", defaultValue = "batchProfileId") String sort,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "25") int size) {

		Response<Object> response = new Response<Object>();
		try {
			PagedResponse<Map<String, Object>> pagedResponse = filteringService.filteringScheduledBatches(filter, sort,
					page, size);
			response = populateBaseDomainResponse(pagedResponse, "Page [%s] Size [%s] Filter [%s]",
					new Object[] { page, size, filter });
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), "False", (Response<Object>) null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/batchStatus/{batchProfileJobId}", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> filterBatchStatusDetails(
			@RequestParam(name = "filter", defaultValue = StringUtils.EMPTY) String filter,
			@RequestParam(name = "sort", defaultValue = "executionStartDateTime") String sort,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "25") int size,
			@PathVariable("batchProfileJobId") String batchProfileJobId) {

		Response<Object> response = new Response<Object>();
		String message = StringUtils.EMPTY;
		try {
			PagedResponse<BatchProfileJobDetails> pagedResponse = filteringService
					.filteringBatchStatusDetails(batchProfileJobId);
			response = populateBaseDomainResponse(pagedResponse, "Page [%s] Size [%s] Filter [%s]",
					new Object[] {  page, size, filter });
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), "False", (Response<Object>) null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/batchStatus/{batchProfileJobId}/document-detail/{status}", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> filterBatchJobStatusDocumentDetail(
			@RequestParam(name = "filter", defaultValue = StringUtils.EMPTY) String filter,
			@RequestParam(name = "sort", defaultValue = "executionStartDateTime") String sort,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "25") int size,
			@PathVariable("batchProfileJobId") String batchProfileJobId, @PathVariable("status") String status) {

		Response<Object> response = new Response<Object>();
		String message = StringUtils.EMPTY;
		try {
			PagedResponse<BatchProfileJobDocumentDetail> pagedResponse = filteringService
					.filteringBatchJobStatusDocumentDetail(filter, sort, page, size, batchProfileJobId, status);
			response = populateBaseDomainResponse(pagedResponse, "Page [%s] Size [%s] Filter [%s]",
					new Object[] { page, size, filter });

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), "False", (Response<Object>) null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
}
