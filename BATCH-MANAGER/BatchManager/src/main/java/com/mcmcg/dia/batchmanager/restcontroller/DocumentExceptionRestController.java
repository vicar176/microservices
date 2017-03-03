package com.mcmcg.dia.batchmanager.restcontroller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.batchmanager.domain.BatchProfileJobIdAndDocumentList;
import com.mcmcg.dia.batchmanager.domain.Response;
import com.mcmcg.dia.batchmanager.entity.DocumentException;
import com.mcmcg.dia.batchmanager.exception.PersistenceException;
import com.mcmcg.dia.batchmanager.service.DocumentExceptionService;
import com.mcmcg.dia.batchmanager.util.EventCode;

/**
 * @author pshankar
 *
 */
@RestController
public class DocumentExceptionRestController extends BaseRestController {
	
	private static final Logger LOG = Logger.getLogger(DocumentExceptionRestController.class);
	
	@Autowired
	DocumentExceptionService documentExceptionService;
	
	/**
	 * @param documentExceptions
	 * @return
	 * @author salam4
	 */
	@RequestMapping(value = "/document-exceptions", method = RequestMethod.POST)
	public ResponseEntity<Response<DocumentException>> saveDocumentException(@RequestBody DocumentException documentExceptions)  {
		Response<DocumentException> response = null;
		try {

			DocumentException documentException = documentExceptionService.saveDocException(documentExceptions);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "DocumentException : ", documentException);
			
		} catch (PersistenceException ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), "False", (DocumentException)null);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), "False", (DocumentException)null);
		}

		
		return new ResponseEntity<Response<DocumentException>>(response, HttpStatus.OK);
	}

	/**
	 * 
	 * @param batchProfileJobId
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping(value = "/document-exceptions", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> retrieveDocumentExceptionsByProfileJobId(@RequestParam Long batchProfileJobId,			
																					 @RequestParam(name = "page", defaultValue = "1") int page,
																					 @RequestParam(name = "size", defaultValue = "25") int size) {

		Response<Object> response = null;
		try {

			List<DocumentException> documentExceptions = documentExceptionService.retrieveDocumentExceptionsByProfileJobId(batchProfileJobId, page, size);
			
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "DocumentException List retrieved Size : " + documentExceptions.size(), documentExceptions);
			
		} catch (PersistenceException ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), "False", (Object)null);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), "False", (Object)null);
		}

		
		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	/**
	 * 
	 * @param documents
	 * @return
	 */
	@RequestMapping(value = "/document-exceptions-batch/", method = RequestMethod.POST)
	public ResponseEntity<Response<Object>> saveBatchOfDocumentException(@RequestBody BatchProfileJobIdAndDocumentList documents)  {
		Response<Object> response = null;
		try {

			boolean isOk = documentExceptionService.saveBatch(documents);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Save Bulk Operation : ", isOk);
			
		} catch (PersistenceException ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), "False", false);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), "False", false);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

}
