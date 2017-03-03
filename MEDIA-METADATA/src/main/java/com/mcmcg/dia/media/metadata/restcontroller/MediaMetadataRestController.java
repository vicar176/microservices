package com.mcmcg.dia.media.metadata.restcontroller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.media.metadata.annotation.Auditable;
import com.mcmcg.dia.media.metadata.exception.PersistenceException;
import com.mcmcg.dia.media.metadata.exception.ServiceException;
import com.mcmcg.dia.media.metadata.model.BaseModel;
import com.mcmcg.dia.media.metadata.model.MediaMetadataModel.Document;
import com.mcmcg.dia.media.metadata.model.domain.MediaMetadata;
import com.mcmcg.dia.media.metadata.model.domain.PagedResponse;
import com.mcmcg.dia.media.metadata.model.domain.Response;
import com.mcmcg.dia.media.metadata.model.domain.Response.Error;
import com.mcmcg.dia.media.metadata.model.query.ExtractionExceptionDetailDataByPortfolio.TemplateFound;
import com.mcmcg.dia.media.metadata.model.query.ExtractionExceptionDetailDataByPortfolio.TemplateNotFound;
import com.mcmcg.dia.media.metadata.service.MediaMetadataService;
import com.mcmcg.dia.media.metadata.util.EventCode;

/**
 * 
 * @author Victor Arias
 *
 */

@RestController
@RequestMapping(value = "/metadatas")
public class MediaMetadataRestController extends BaseRestController {

	private static final Logger LOG = Logger.getLogger(MediaMetadataRestController.class);

	@Autowired
	MediaMetadataService mediaMetadataService;

	/// metadatas/

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "", method = { RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT })
	public ResponseEntity<Response<Object>> notSupported() {
		return methodsNotImplemented();
	}


	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> findByOriginalAcctNumDocTypeDocDate(
			@RequestParam(name= "accountNumber", defaultValue = StringUtils.EMPTY) Long accountNumber,
			@RequestParam(name = "originalDocumentType", defaultValue = StringUtils.EMPTY) String originalDocumentType,
			@RequestParam(name = "documentDate", defaultValue = StringUtils.EMPTY) String documentDate,
			@RequestParam(name = "documentStatus", defaultValue = StringUtils.EMPTY) String documentStatus,
			@RequestParam(name = "filter", defaultValue = StringUtils.EMPTY) String filter,
			@RequestParam(name = "sort", defaultValue = "documentId") String sort,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "2147483647") int size) {

		Object returnValue = null;
		Response<Object> response = new Response<Object>();
		Error status = new Error();

		try {
			
			if (StringUtils.isNotBlank(originalDocumentType) &&  StringUtils.isNotBlank(documentDate)){
				returnValue = mediaMetadataService.findByOriAcctNumDocTypeAndDocDate(accountNumber, originalDocumentType, documentDate);
				status.setCode(EventCode.REQUEST_SUCCESS.getCode());
				status.setMessage("Find by originalAccountNumber, originalDocumentType and documentDate executed");
			}else if (StringUtils.isNotBlank(filter)){
				returnValue = mediaMetadataService.retrieveMediaMetadata(filter, sort, page, size);

				status.setCode(EventCode.REQUEST_SUCCESS.getCode());
				String message = String.format("Filter [%s] Sort [%s] Page [%s] Size [%s] was executed ", filter, sort, page, size );
				status.setMessage(message);
				
			}else{
				String newFilter = "documentStatus=\"" + documentStatus + "\"";
				returnValue = mediaMetadataService.retrieveMediaMetadata(newFilter);
				status.setCode(EventCode.REQUEST_SUCCESS.getCode());
				String message = String.format("Filter [%s] Sort [%s] Page [%s] Size [%s] was executed ", filter, sort, page, size );
				status.setMessage(message);
			}
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			status.setCode(EventCode.SERVICE_ERROR.getCode());
			status.setMessage(e.getMessage());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			status.setCode(EventCode.SERVER_ERROR.getCode());
			status.setMessage(e.getMessage());
		}

		response.setData(returnValue);
		response.setError(status);

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	
	/// metadatas/{documentId}

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "/{documentId}", method = RequestMethod.POST)
	public ResponseEntity<Response<Object>> notSupportedWithDocId(@PathVariable("documentId") Long documentId) {
		return methodsNotImplemented();
	}

	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{documentId}", method = RequestMethod.GET)
	public ResponseEntity<Response<MediaMetadata>> findByDocumentId(@PathVariable("documentId") Long documentId) {
		MediaMetadata metadata = null;
		Response<MediaMetadata> response = new Response<MediaMetadata>();
		Error status = new Error();

		try {
			metadata = mediaMetadataService.findByDocumentId(documentId);
			status.setCode(EventCode.REQUEST_SUCCESS.getCode());
			status.setMessage("Find by documentId executed");
		} catch (ServiceException e) {
			status.setCode(EventCode.SERVICE_ERROR.getCode());
			status.setMessage(e.getMessage());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			status.setCode(EventCode.SERVER_ERROR.getCode());
			status.setMessage(e.getMessage());
		}


		response.setData(metadata);
		response.setError(status);

		return new ResponseEntity<Response<MediaMetadata>>(response, HttpStatus.OK);
	}

	@Auditable
	@RequestMapping(value = "/{documentId}", method = RequestMethod.PUT)
	public ResponseEntity<Response<MediaMetadata>> save(@PathVariable("documentId") Long documentId,
			@RequestBody MediaMetadata mediaMetadata) {

		MediaMetadata metadata = null;
		Response<MediaMetadata> response = new Response<MediaMetadata>();
		Error status = new Error();
		HttpStatus httpStatus = null;

		try {
			if (mediaMetadata.getId() != null){
				metadata = mediaMetadataService.save(mediaMetadata, mediaMetadata.getId());
			}else{
				metadata = mediaMetadataService.save(mediaMetadata);
			}
			if (metadata.getVersion() == null) {
				status.setCode(EventCode.OBJECT_CREATED.getCode());
				httpStatus = HttpStatus.CREATED;
			} else {
				status.setCode(EventCode.REQUEST_SUCCESS.getCode());
				httpStatus = HttpStatus.OK;
			}
			status.setMessage("Media Metadata object saved");
		} catch (ServiceException e) {
			status.setCode(EventCode.SERVICE_ERROR.getCode());
			status.setMessage(e.getMessage());
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			status.setCode(EventCode.SERVER_ERROR.getCode());
			status.setMessage(e.getMessage());
		}

		response.setData(metadata);
		response.setError(status);

		return new ResponseEntity<Response<MediaMetadata>>(response, httpStatus);
	}
	
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/{documentId}", method = RequestMethod.DELETE)
	public ResponseEntity<Response<Boolean>> deleteByDocumentId(@PathVariable("documentId") Long documentId) {
		Boolean deleted = false;
		Response<Boolean> response = new Response<Boolean>();
		Error status = new Error();

		try {
			deleted = mediaMetadataService.deleteMetadata(documentId);
			status.setCode(EventCode.REQUEST_SUCCESS.getCode());
			status.setMessage("Delete by documentId executed");
		} catch (ServiceException e) {
			status.setCode(EventCode.SERVICE_ERROR.getCode());
			status.setMessage(e.getMessage());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			status.setCode(EventCode.SERVER_ERROR.getCode());
			status.setMessage(e.getMessage());
		}

		response.setData(deleted);
		response.setError(status);

		return new ResponseEntity<Response<Boolean>>(response, HttpStatus.OK);
	}

	//// metadatas/{documentID}/auto-validations

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = {"/{documentId}/auto-validations", "/{documentId}/manual-validation"}, method = { RequestMethod.DELETE, RequestMethod.POST,
			RequestMethod.GET })
	public ResponseEntity<Response<Object>> notSupportedWithAutoValid(@PathVariable("documentId") String documentId) {
		return methodsNotImplemented();
	}

	@Auditable(eventCode = EventCode.OBJECT_CREATED)
	@RequestMapping(value = "/{documentId}/auto-validations", method = RequestMethod.PUT)
	public ResponseEntity<Response<MediaMetadata>> updateAutoValidation(@PathVariable("documentId") Long documentId,
			@RequestBody MediaMetadata mediaMetadata) {
		MediaMetadata metadata = null;
		Response<MediaMetadata> response = new Response<MediaMetadata>();
		Error status = new Error();

		try {
			metadata = mediaMetadataService.updateAutoValidation(documentId, mediaMetadata);
			status.setCode(EventCode.REQUEST_SUCCESS.getCode());
			status.setMessage("Autovalidation executed");
		} catch (ServiceException e) {
			status.setCode(EventCode.SERVICE_ERROR.getCode());
			status.setMessage(e.getMessage());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			status.setCode(EventCode.SERVER_ERROR.getCode());
			status.setMessage(e.getMessage());
		}

		response.setData(metadata);
		response.setError(status);
		return new ResponseEntity<Response<MediaMetadata>>(response, HttpStatus.OK);
	}
	
	@Auditable(eventCode = EventCode.OBJECT_CREATED)
	@RequestMapping(value = "/{documentId}/manual-validation", method = RequestMethod.PUT)
	public ResponseEntity<Response<MediaMetadata>> updateManualValidation(@PathVariable("documentId") Long documentId,
			@RequestBody MediaMetadata mediaMetadata) {
		MediaMetadata metadata = null;
		Response<MediaMetadata> response = new Response<MediaMetadata>();
		Error status = new Error();

		try {
			metadata = mediaMetadataService.updateManualValidation(documentId, mediaMetadata);
			status.setCode(EventCode.REQUEST_SUCCESS.getCode());
			status.setMessage("Autovalidation executed");
		} catch (ServiceException e) {
			status.setCode(EventCode.SERVICE_ERROR.getCode());
			status.setMessage(e.getMessage());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			status.setCode(EventCode.SERVER_ERROR.getCode());
			status.setMessage(e.getMessage());
		}

		response.setData(metadata);
		response.setError(status);
		return new ResponseEntity<Response<MediaMetadata>>(response, HttpStatus.OK);
	}

	//// metadatas/{documentID}/data-elements/validates

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "/{documentId}/data-elements/validates", method = { RequestMethod.DELETE,
			RequestMethod.POST, RequestMethod.GET })
	public ResponseEntity<Response<Object>> notSupportedWithDataElem(@PathVariable("documentId") Long documentId) {
		return methodsNotImplemented();
	}

	@Auditable
	@RequestMapping(value = "/{documentId}/data-elements/validates", method = RequestMethod.PUT)
	public ResponseEntity<Response<MediaMetadata>> validateDataElements(@PathVariable("documentId") Long documentId) {
		MediaMetadata metadata = null;
		Response<MediaMetadata> response = new Response<MediaMetadata>();
		Error status = new Error();
		try {
			metadata = mediaMetadataService.validateDataElements(documentId);
			status.setCode(EventCode.REQUEST_SUCCESS.getCode());
			status.setMessage("Validate data elements executed");
		} catch (ServiceException e) {
			status.setCode(EventCode.SERVICE_ERROR.getCode());
			status.setMessage(e.getMessage());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			status.setCode(EventCode.SERVER_ERROR.getCode());
			status.setMessage(e.getMessage());
		}

		response.setData(metadata);
		response.setError(status);
		return new ResponseEntity<Response<MediaMetadata>>(response, HttpStatus.OK);
	}

	@Auditable(eventCode = EventCode.NOT_IMPLEMENTED)
	@RequestMapping(value = "/{documentId}/statement-translation", method = { RequestMethod.DELETE, RequestMethod.POST,
			RequestMethod.GET })
	public ResponseEntity<Response<Object>> notSupportedWithStmtTranslation(
			@PathVariable("documentId") String documentId, @RequestBody(required = false) Document document) {

		return methodsNotImplemented();
	}

	@Auditable(eventCode = EventCode.OBJECT_CREATED)
	@RequestMapping(value = "/{documentId}/statement-translation", method = RequestMethod.PUT)
	public ResponseEntity<Response<MediaMetadata>> updateStatementTranslation(
			@PathVariable("documentId") Long documentId, @RequestBody MediaMetadata mediaMetadata) {

		MediaMetadata metadata = null;
		Response<MediaMetadata> response = new Response<MediaMetadata>();
		Error status = new Error();

		try {
			metadata = mediaMetadataService.updateStatementTranslation(documentId, mediaMetadata);
			status.setCode(EventCode.REQUEST_SUCCESS.getCode());
			status.setMessage("Statement Translation executed");
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			status.setCode(EventCode.SERVICE_ERROR.getCode());
			status.setMessage(e.getMessage());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			status.setCode(EventCode.SERVER_ERROR.getCode());
			status.setMessage(e.getMessage());
		}
		response.setData(metadata);
		response.setError(status);
		return new ResponseEntity<Response<MediaMetadata>>(response, HttpStatus.OK);
	}

	// Extraction Management Screen
	/**
	 * 
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 */
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/portfolios", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> summarizeByPortfolio(
			@RequestParam(name = "filter", defaultValue = "") String filter,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "sort", defaultValue = "") String sort,
			@RequestParam(name = "showSummary", defaultValue = "false") boolean showSummary,
			@RequestParam(name = "size", defaultValue = "2147483647") int size) {
		Response<Object> response = new Response<Object>();

		try {

			PagedResponse<Object> pagedResponse = mediaMetadataService.retrievePortfolioByCriteria(filter, sort, showSummary, page, size);
			
			response = populateResponse(pagedResponse, "Page [%s] Size [%s] Filter [%s]", new Object[] { page, size, filter});

		} catch (DataAccessException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), e.getMessage(), (BaseModel) null);
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), (BaseModel) null);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), "Server Error: " + e.getMessage(), (BaseModel) null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @param portfolioNumber
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 */
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = {"/portfolios/{portfolioNumber}/templatesFound", "/portfolios/{portfolioNumber}/noTextLayer"}, method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> summarizeByTemplateFound(
			@PathVariable("portfolioNumber") Long portfolioNumber,
			@RequestParam(name = "filter", defaultValue = "") String filter,
			@RequestParam(name = "sort", defaultValue = "") String sort,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "2147483647") int size) {
		
		Response<Object>  response = null;

		try {

			PagedResponse<TemplateFound>  data = mediaMetadataService.retrieveTemplateFoundForExtractedException(portfolioNumber, filter, sort, page, size);
			
			response = populateResponse(data, "Page [%s] Size [%s] PortfolioNumber [%d] ", new Object[] { page, size, portfolioNumber });

		} catch (PersistenceException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), e.getMessage(), (BaseModel) null);
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), (BaseModel) null);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), "Server Error: " + e.getMessage(), (BaseModel) null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

	/**
	 * 
	 * @param portfolioNumber
	 * @param filter
	 * @param sort
	 * @param page
	 * @param size
	 * @return
	 */
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/portfolios/{portfolioNumber}/templatesNotFound", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> summarizeByTemplateNotFound(
			@PathVariable("portfolioNumber") Long portfolioNumber,
			@RequestParam(name = "filter", defaultValue = "") String filter,
			@RequestParam(name = "sort", defaultValue = "") String sort,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "2147483647") int size) {
		Response<Object>  response = null;

		try {

			PagedResponse<TemplateNotFound>  data = mediaMetadataService.retrieveTemplateNotFoundForExtractedException(portfolioNumber, filter, sort, page, size);
			
			response = populateResponse(data, "Page [%s] Size [%s] PortfolioNumber [%d] ", new Object[] { page, size, portfolioNumber });

		} catch (PersistenceException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), e.getMessage(), (BaseModel) null);
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), (BaseModel) null);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), "Server Error: " + e.getMessage(), (BaseModel) null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	/**
	 * 
	 * @param portfolioNumber
	 * @param filter
	 * @param page
	 * @param size
	 * @return
	 */
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "/portfolios/{portfolioNumber}/documents", method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> retrieveFailedDocumentsByPortfolio(
			@PathVariable("portfolioNumber") Long portfolioNumber,
			@RequestParam(name = "filter", defaultValue = "") String filter,
			@RequestParam(name = "sort", defaultValue = "") String sort,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "2147483647") int size) {
		Response<Object> response = new Response<Object>();

		try {

			PagedResponse<Object> pagedResponse = mediaMetadataService.retrieveFailedDocumentsByPortfolio(portfolioNumber, filter, sort, page, size);
			
			response = populateResponse(pagedResponse, "Page [%s] Size [%s] Filter [%s] Sort [%s]", new Object[] { page, size, filter, sort });

		} catch (PersistenceException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.DATABASE_ERROR.getCode(), e.getMessage(), (BaseModel) null);
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), e.getMessage(), (BaseModel) null);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), "Server Error: " + e.getMessage(), (BaseModel) null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}

}
