package com.mcmcg.dia.documentprocessor.restcontroller;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.documentprocessor.common.EventCode;
import com.mcmcg.dia.documentprocessor.entity.BatchExecutionEntity;
import com.mcmcg.dia.documentprocessor.service.BatchExecutionService;
import com.mcmcg.dia.iwfm.domain.Response;

/**
 * 
 * @author wporras
 *
 */
@RestController
@RequestMapping(value = "/batch-executions")
public class BatchExecutionRestController extends BaseRestController {

	private static final Logger LOG = Logger.getLogger(BatchExecutionRestController.class);

	@Autowired
	BatchExecutionService batchExecutionService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findAll() {

		Response<Object> response = null;

		try {
			List<BatchExecutionEntity> result = batchExecutionService.findAll();

			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Batch Execution FindAll", result);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), null);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;

	}

	@RequestMapping(value = "/summary", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> getBatchExecutionSummary() {

		Response<Object> response = null;

		try {
			List<Map<String, Object>> result = batchExecutionService.getBatchExecutionsSummary();

			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Batch Executions Summary", result);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), null);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;

	}

}