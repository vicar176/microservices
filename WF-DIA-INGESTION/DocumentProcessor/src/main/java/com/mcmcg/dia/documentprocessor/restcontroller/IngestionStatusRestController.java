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
import com.mcmcg.dia.documentprocessor.service.IngestionStatusService;
import com.mcmcg.dia.iwfm.domain.Response;

/**
 * 
 * @author wporras
 *
 */
@RestController
public class IngestionStatusRestController extends BaseRestController {

	private static final Logger LOG = Logger.getLogger(IngestionStatusRestController.class);

	@Autowired
	IngestionStatusService ingestionStatusService;

	@RequestMapping(value = "/ingestion-status", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> getIngestionStatus() {

		Response<Object> response = null;

		try {
			List<Map<String, Object>> result = ingestionStatusService.getIngestionStatus();

			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Ingestion Status", result);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), null);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;

	}

}