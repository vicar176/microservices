package com.mcmcg.utility.restcontroller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.utility.annotation.Auditable;
import com.mcmcg.utility.domain.Response;
import com.mcmcg.utility.exception.ServiceException;
import com.mcmcg.utility.service.ZoneDataExtractionService;
import com.mcmcg.utility.util.EventCode;

/**
 * 
 * @author wporras
 *
 */
@RestController
@RequestMapping(value = "/zone-data-extraction")
public class ZoneDataExtractionRestController extends BaseRestController{

	@Autowired
	ZoneDataExtractionService zoneDataExtractionService;

	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/extract-zone-text", method = RequestMethod.GET)
	public  ResponseEntity<Response<Map<String, String>>> extractZoneTextByType(@RequestParam("x") float x, @RequestParam("y") float y,
			@RequestParam("w") float width, @RequestParam("h") float height,
			@RequestParam("page-number") int pageNumber, @RequestParam("source") String source,
			@RequestParam("type") String type, @RequestParam("validation") String validation,
			@RequestParam(value = "dateFormat", required = false) String dateFormat) {

		Response<Map<String, String>> response = null;

		try {
			String result = zoneDataExtractionService.extractZoneTextByType(x, y, width, height, pageNumber, source,
					type, validation, dateFormat);

			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Extracted text successfully", result);
		} 
		catch (ServiceException ex) {
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), ex.getMessage(), null);
		}
		catch (Exception ex) {
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), ex.getMessage(), null);
		}

		return new ResponseEntity<Response<Map<String, String>>>(response, HttpStatus.OK);
	}

	/**
	 * Build a envelope object with a object for Error managing and another for
	 * Data managing
	 * 
	 * @param code
	 * @param message
	 * @param result
	 * @return Response<String>
	 */
	private Response<Map<String, String>> buildResponse(int code, String message, String result) {

		Response<Map<String, String>> response = new Response<Map<String, String>>();

		Response.Error error = new Response.Error();
		error.setCode(code);
		error.setMessage(message);
		response.setError(error);

		Map<String, String> map = null;
		if (result != null) {
			map = new HashMap<String, String>();
			map.put("extractedText", result);
		}
		response.setData(map);

		return response;
	}
}
