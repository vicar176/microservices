package com.mcmcg.dia.account.metadata.restcontroller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.account.metadata.annotation.Auditable;
import com.mcmcg.dia.account.metadata.exception.ServiceException;
import com.mcmcg.dia.account.metadata.model.AccountOALDModel.MediaOald;
import com.mcmcg.dia.account.metadata.model.domain.AccountOALD;
import com.mcmcg.dia.account.metadata.model.domain.Response;
import com.mcmcg.dia.account.metadata.service.AccountOALDService;
import com.mcmcg.dia.account.metadata.util.EventCode;

@RestController
@RequestMapping(value = "/account-oalds")
public class AccountMetadataRestController extends BaseRestController {
	
	@Autowired
	AccountOALDService accountOALDService;
	
	@Auditable
	@RequestMapping(value = "/{accountNumber}", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> save(@PathVariable("accountNumber") Long accountNumber,
			@RequestBody AccountOALD accountOALD) {
		
		AccountOALD accountOald = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;

		try {
			accountOald = accountOALDService.save(accountNumber,accountOALD);
			message = "Account metadata object saved";
			response = buildResponse(EventCode.OBJECT_CREATED.getCode(), message, accountOald);
		} 
		catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), message, accountOald);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	
	
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{accountNumber}", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findByAccountNumber(@PathVariable("accountNumber") Long accountNumber) {
		
		AccountOALD accountOald = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;

		try {
			accountOald = accountOALDService.findByAccountNumber(accountNumber);
			message = "Find by accountNumber executed";
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, accountOald);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), message, accountOald);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	
	@RequestMapping(value = "/{accountNumber}/oalds/{oaldId}", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> upddateAccountOald(@PathVariable("accountNumber") Long accountNumber,
			@PathVariable("oaldId") String oaldId, @RequestBody MediaOald mediaOald) {
		AccountOALD accountOald = null;
		Response<Object> response = null;
		String message = StringUtils.EMPTY;
		
		try {
			accountOald = accountOALDService.save(accountNumber,oaldId, mediaOald, null, true);
			message = "AccountOald updated";
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), message, accountOald);
		} catch (ServiceException e) {
			message = e.getMessage();
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), message, accountOald);
		}
		
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/{accountNumber}/oalds/{oaldId}", method = RequestMethod.DELETE)
	public ResponseEntity<Response<Object>> deleteAccountOald(@PathVariable("accountNumber") Long accountNumber,
			@PathVariable("oaldId") String oaldId) {
		/*AccountOALD accountOald = null;
		Response<AccountOALD> response = new Response<AccountOALD>();
		Error status = new Error();
		
		try {
			accountOald = accountOALDService.deleteAccOald(accountNumber,oaldId);
			status.setCode(EventCode.REQUEST_SUCCESS.getCode());
			status.setMessage("AccountOald Deleted");
		} catch (ServiceException e) {
			status.setCode(EventCode.SERVICE_ERROR.getCode());
			status.setMessage(e.getMessage());
		}
		response.setData(accountOald);
		response.setError(status);
		return new ResponseEntity<Response<AccountOALD>>(response, HttpStatus.OK);*/
		return methodsNotImplemented();
	}

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "", method = {RequestMethod.GET,RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
	public ResponseEntity<Response<Object>> notSupported() {
		return methodsNotImplemented();
	}
	
	
	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "/{accountNumber}", method = {RequestMethod.DELETE, RequestMethod.POST})
	public ResponseEntity<Response<Object>> notSupportedWithAccountNumber(@PathVariable("accountNumber") Long accountNumber) {
		return methodsNotImplemented();
	}
	
	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "/{accountNumber}/oalds/{oaldId}", method = {RequestMethod.POST, RequestMethod.GET})
	public ResponseEntity<Response<Object>> notSupportedWithAccountNumberandOaldId(@PathVariable("accountNumber") Long accountNumber,
			@PathVariable("oaldId") String oaldId) {
		return methodsNotImplemented();
	}
	
	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "/{accountNumber}/oalds/", method = {RequestMethod.PUT,RequestMethod.DELETE,RequestMethod.POST, RequestMethod.GET})
	public ResponseEntity<Response<Object>> notSupportedWithAccountNumberandOald(@PathVariable("accountNumber") Long accountNumber) {
		return methodsNotImplemented();
	}

}
