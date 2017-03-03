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
import com.mcmcg.dia.documentprocessor.service.IngestionTrackerService;
import com.mcmcg.dia.iwfm.domain.Response;

/**
 * 
 * @author wporras
 *
 */
@RestController
public class IngestionTrackerRestController extends BaseRestController {

	private static final Logger LOG = Logger.getLogger(IngestionTrackerRestController.class);

	@Autowired
	IngestionTrackerService IngestionTrackerService;

	@RequestMapping(value = "/ingestion-trackers/summary", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> getIngestionTrackerSummary() {

		Response<Object> response = null;

		try {
			List<Map<String, Object>> result = IngestionTrackerService.getIngestionTrackerSummary();

			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Ingestion Tracker Summary",
					result);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

	}

}