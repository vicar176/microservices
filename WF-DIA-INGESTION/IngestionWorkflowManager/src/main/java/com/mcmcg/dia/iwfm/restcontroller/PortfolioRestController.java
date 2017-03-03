package com.mcmcg.dia.iwfm.restcontroller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.iwfm.annotation.Diagnostics;
import com.mcmcg.dia.iwfm.domain.MediaMetadataModel;
import com.mcmcg.dia.iwfm.domain.PagedResponse;
import com.mcmcg.dia.iwfm.domain.Response;
import com.mcmcg.dia.iwfm.exception.IngestionWorkflowManagerException;
import com.mcmcg.dia.iwfm.exception.MediaServiceException;
import com.mcmcg.dia.iwfm.service.DocumentQueueingService;
import com.mcmcg.dia.iwfm.service.DocumentService;
import com.mcmcg.dia.iwfm.util.DocumentStatusCode;
import com.mcmcg.dia.iwfm.util.EventCode;

/**
 * 
 * @author jaleman
 *
 */

@RestController
public class PortfolioRestController extends BaseRestController {

	private static final Logger LOG = Logger.getLogger(PortfolioRestController.class);

	@Autowired
	private DocumentService documentService;

	@Autowired
	private DocumentQueueingService documentQueueingService;

	/**
	 * 
	 * @param portfolioNumber
	 * @param templateName
	 * @return
	 */
	@Diagnostics
	@RequestMapping(value = "/portfolios/{portfolioNumber}/documents", method = RequestMethod.POST)
	public ResponseEntity<Response<Object>> sendDocumentsToIngestionQueue(
			@PathVariable("portfolioNumber") Long portfolioNumber,
			@RequestParam("documentStatus") String documentStatus, @RequestParam("updatedBy") String updatedBy,
			@RequestParam("templateName") String templateName) {

		Response<Object> response = null;

		try {
			PagedResponse<MediaMetadataModel> pagedDocuments = documentService.retrieveMetadatas(portfolioNumber,
					templateName, documentStatus);

			response = populateResponse(
					documentQueueingService.prepareAndSendDocumentsToIngestionQueue(pagedDocuments.getItems(),
							DocumentStatusCode.REPROCESS, updatedBy),
					"Portfolio %d Template Name %s Document Status [%s]",
					new Object[] { portfolioNumber, templateName, documentStatus });
		} catch (MediaServiceException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), (Object) null);
		} catch (IngestionWorkflowManagerException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), (Object) null);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), (Object) null);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;

	}

	/**
	 * 
	 * @param portfolioNumber
	 * @param documentTypeCode
	 * @param originalLenderName
	 * @return
	 */
	@Diagnostics
	@RequestMapping(value = "/portfolios/{portfolioNumber}/documentTypes", method = RequestMethod.POST)
	public ResponseEntity<Response<Object>> sendDocumentTypesToIngestionQueue(
			@PathVariable("portfolioNumber") Long portfolioNumber,
			@RequestParam("documentTypeCode") String documentTypeCode, @RequestParam("updatedBy") String updatedBy,
			@RequestParam("originalLenderName") String originalLenderName) {

		Response<Object> response = null;

		try {
			PagedResponse<MediaMetadataModel> pagedDocuments = documentService.retrieveDocumentTypes(portfolioNumber,
					documentTypeCode, originalLenderName);

			response = populateResponse(
					documentQueueingService.prepareAndSendDocumentsToIngestionQueue(pagedDocuments.getItems(),
							DocumentStatusCode.REPROCESS, updatedBy),
					"Portfolio %d Document type code %s Original Lender Name [%s]",
					new Object[] { portfolioNumber, documentTypeCode, originalLenderName });
		} catch (MediaServiceException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), (Object) null);
		} catch (IngestionWorkflowManagerException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), (Object) null);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), (Object) null);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;

	}

	/**
	 * Enhancements R1 
	 * 
	 * @param templateName
	 * @param portfolioNumber
	 * @param status
	 * @param updatedBy
	 * @return
	 */
	@Diagnostics
	@RequestMapping(value = "portfolios/{portfolioNumber}/templates/statuses/{documentStatus}", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> updateDocumentStatus(@RequestParam("templateName") String templateName,
			 													 @PathVariable("portfolioNumber") Long portfolioNumber,
			 													 @PathVariable("documentStatus") String documentStatus,
																 @RequestParam("newDocumentstatus") String newDocumentstatus, 
																 @RequestParam("updatedBy") String updatedBy) {

		Response<Object> response = null;
		try{
			
			PagedResponse<MediaMetadataModel>  pagedResponse = documentService.retrieveMetadatas(portfolioNumber, templateName, documentStatus);
			List<String> documentIdList =  new ArrayList<String>();
			if (pagedResponse != null && pagedResponse.getTotalItems() > 0){
				documentIdList.addAll(documentQueueingService.updateDocImgAndMetadata(pagedResponse.getItems(), newDocumentstatus, updatedBy));
			}
			response = populateResponse(documentIdList, "documents to be reprocessed --->  %s", new Object[] { pagedResponse.getItems().size() });
		}
		catch (MediaServiceException e){
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), (Object) null);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), (Object) null);
		}

		LOG.info(response.getError().getMessage());

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;

	}
}