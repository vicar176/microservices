package com.mcmcg.dia.iwfm.automation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.iwfm.domain.Response;
import com.mcmcg.dia.iwfm.restcontroller.BaseRestController;
import com.mcmcg.dia.iwfm.util.EventCode;

/**
 * 
 * @author wporras
 *
 */
@RestController
@RequestMapping(value = "automations")
public class AutomationRestController extends BaseRestController {

	private static final Logger LOG = Logger.getLogger(AutomationRestController.class);

	@Autowired
	AutomationService automationService;

	@RequestMapping(value = "/prepareIngestionTest", method = RequestMethod.POST)
	public ResponseEntity<Response<Object>> prepareIngestionTest(@RequestBody IngestionTest ingestionTest) {

		Response<Object> response = null;

		try {
			IngestionTest result = automationService.prepareIngestionTest(ingestionTest);

			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Batch Execution FindAll", result);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), null);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;

	}

}