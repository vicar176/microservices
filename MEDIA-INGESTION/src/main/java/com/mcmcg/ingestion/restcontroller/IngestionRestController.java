package com.mcmcg.ingestion.restcontroller;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.ingestion.annotation.Diagnostics;
import com.mcmcg.ingestion.domain.AccountOALDModel;
import com.mcmcg.ingestion.domain.BaseDomain;
import com.mcmcg.ingestion.domain.MediaDocument;
import com.mcmcg.ingestion.domain.MediaMetadataModel;
import com.mcmcg.ingestion.domain.Response;
import com.mcmcg.ingestion.exception.IngestionServiceException;
import com.mcmcg.ingestion.exception.MediaServiceException;
import com.mcmcg.ingestion.service.AutoValidationService;
import com.mcmcg.ingestion.service.ExtractionService;
import com.mcmcg.ingestion.service.ReceiveService;
import com.mcmcg.ingestion.service.StatementTranslationService;
import com.mcmcg.ingestion.service.TagPdfService;
import com.mcmcg.ingestion.util.EventCode;

/**
 * 
 * @author jaleman
 *
 */

@RestController
public class IngestionRestController extends BaseRestController {

	public static final String REPROCESS = "REPROCESS";

	private static final Logger LOG = Logger.getLogger(IngestionRestController.class);

	@Autowired
	ExtractionService extractionService;

	@Autowired
	ReceiveService receiveService;

	@Autowired
	AutoValidationService autoValidationService;

	@Autowired
	TagPdfService tagPdfService;

	@Autowired
	StatementTranslationService translationService;

	/**
	 * 
	 * @return
	 */
	@Diagnostics
	@RequestMapping(value = "/receives/{documentId}", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> receive(@PathVariable Long documentId,
													@RequestBody MediaDocument mediaDocument) {
		Response<Object> response = null;
		try {
			response = receiveDocument(documentId, mediaDocument);

		} catch (IngestionServiceException ex) {

			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), "Failed: " + ex.getMessage(),
					(BaseDomain) null);

		} catch (MediaServiceException ex) {

			response = handleMediaException(ex);

		} catch (Exception ex) {

			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), "Failed: " + ex.getMessage(), (BaseDomain) null);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;

	}

	@Diagnostics
	@RequestMapping(value = "/tag-pdfs/{documentId}", method = RequestMethod.PUT)
	public Response<Object> pdfTagging(@PathVariable Long documentId, @RequestBody MediaDocument mediaDocument)
			throws IngestionServiceException, MediaServiceException {

		Response<Object> response = null;

		try {

			response = tagPdf(mediaDocument);

		} catch (IngestionServiceException ex) {

			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), "Failed: " + ex.getMessage(),
					(BaseDomain) null);

		} catch (MediaServiceException ex) {

			response = handleMediaException(ex);

		} catch (Exception ex) {

			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), "Failed: " + ex.getMessage(), (BaseDomain) null);
		}

		return response;
	}

	/**
	 * 
	 * @param documentId
	 * @param mediaDocument
	 * @return
	 */
	@Diagnostics
	@RequestMapping(value = "/extractions/{documentId}", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> extract(@PathVariable Long documentId, 
													@RequestBody MediaDocument mediaDocument) {

		Response<Object> response = null;
		try {

			response = extractDocument(documentId, mediaDocument);

		} catch (IngestionServiceException ex) {

			LOG.error(ex.getMessage(), ex);
			
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), "Failed: " + ex.getMessage(),
					(BaseDomain) null);

		} catch (MediaServiceException ex) {

			response = handleMediaException(ex);

		} catch (Exception ex) {

			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), "Failed: " + ex.getMessage(), (BaseDomain) null);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;

	}

	/**
	 * 
	 * @param documentId
	 * @param mediaDocument
	 * @return
	 */
	@Diagnostics
	@RequestMapping(value = "/auto-validations/{documentId}", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> autoValidation( @PathVariable Long documentId,
															@RequestBody MediaDocument mediaDocument) {

		Response<Object> response = null;
		try {

			response = autoValidateDocument(documentId, mediaDocument);

		} catch (IngestionServiceException ex) {

			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), "Failed: " + ex.getMessage(),
					(BaseDomain) null);

		} catch (MediaServiceException ex) {

			response = handleMediaException(ex);

		} catch (Exception ex) {

			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), "Failed: " + ex.getMessage(), (BaseDomain) null);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;

	}

	/**
	 * Run existing document type translation logic to rename/reclassify the
	 * statement documents to Chrgoff, Lastpmt, Lastbillstmt and Lastactivity
	 * 
	 * @param documentId
	 * @param mediaDocument
	 * @return ResponseEntity<Response<Object>>
	 * 
	 */
	@Diagnostics
	@RequestMapping(value = "/statement-translations/{documentId}", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> translateStatement(@PathVariable Long documentId,
			@RequestBody MediaDocument mediaDocument) {

		Response<Object> response = null;
		try {
			response = translateStmt(documentId, mediaDocument);

		} catch (IngestionServiceException ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), "Failed: " + ex.getMessage(),
					(BaseDomain) null);
		} catch (MediaServiceException ex) {
			response = handleMediaException(ex);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), "Failed: " + ex.getMessage(), (BaseDomain) null);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
		return responseEntity;
	}

	@RequestMapping(value = { "/statement-translations/{documentId}", "/statement-translations" }, method = {
			RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE })
	public ResponseEntity<Response<Object>> notImplementedTranslateStatement(@PathVariable Map<String, Long> documentId,
			@RequestBody(required = false) MediaDocument mediaDocument) {

		return methodsNotImplemented();
	}

	/**
	 * 
	 * PRIVATE METHODS
	 * 
	 */

	/**
	 * @param documentId
	 * @param mediaDocument
	 * @return
	 * @throws IngestionServiceException
	 */
	private Response<Object> extractDocument(Long documentId, MediaDocument mediaDocument)
			throws IngestionServiceException, MediaServiceException {
		Response<Object> response;
		if (mediaDocument.getDocumentId() == null) {
			mediaDocument.setDocumentId(documentId);
		}

		MediaMetadataModel mediaMetadata = extractionService.execute(mediaDocument);

		if (mediaMetadata == null) {

			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Skip", (BaseDomain) null);
		} else {
			mediaMetadata.setDataElements(null);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Success", mediaMetadata);
		}

		return response;
	}

	/**
	 * @param documentId
	 * @param mediaDocument
	 * @return
	 * @throws IngestionServiceException
	 */
	private Response<Object> receiveDocument(Long documentId, MediaDocument mediaDocument)
			throws IngestionServiceException, MediaServiceException {
		Response<Object> response;
		if (mediaDocument.getDocumentId() == null) {
			mediaDocument.setDocumentId(documentId);
		}

		AccountOALDModel accountOald = receiveService.execute(mediaDocument);

		if (accountOald == null) {
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Skip", (BaseDomain) null);
		} else {
			accountOald.setOalds(null);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Success", accountOald);
		}

		return response;
	}

	/**
	 * @param documentId
	 * @param mediaDocument
	 * @return
	 * @throws IngestionServiceException
	 */
	private Response<Object> autoValidateDocument(Long documentId, MediaDocument mediaDocument)
			throws IngestionServiceException, MediaServiceException {
		Response<Object> response;
		
		MediaMetadataModel mediaMetadata = autoValidationService.execute(mediaDocument);

		if (mediaMetadata == null) {

			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Skip", (BaseDomain) null);
		} else {
			mediaMetadata.setDataElements(null);
			mediaMetadata.getExtraction().setTemplateMappingProfile(null);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Success", mediaMetadata);
		}

		return response;
	}

	/**
	 * 
	 * @param mediaDocument
	 * @return
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	private Response<Object> tagPdf(MediaDocument mediaDocument)
			throws IngestionServiceException, MediaServiceException {

		Response<Object> response = null;

		MediaMetadataModel mediaMetadata = tagPdfService.execute(mediaDocument);

		if (mediaMetadata == null) {

			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Skip", (BaseDomain) null);
		} else {
			mediaMetadata.setDataElements(null);
			mediaMetadata.getExtraction().setTemplateMappingProfile(null);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Success", mediaMetadata);
		}

		return response;
	}

	/**
	 * 
	 * @param documentId
	 * @param mediaDocument
	 * @return Response<Object>
	 * @throws IngestionServiceException
	 * @throws MediaServiceException
	 */
	private Response<Object> translateStmt(Long documentId, MediaDocument mediaDocument)
			throws IngestionServiceException, MediaServiceException {

		Response<Object> response;
		if (mediaDocument.getDocumentId() == null) {
			mediaDocument.setDocumentId(documentId);
		}

		MediaMetadataModel mediaMetadata = translationService.execute(mediaDocument);

		if (mediaMetadata == null) {
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Skip", (BaseDomain) null);
		} else {
			mediaMetadata.setDataElements(null);
			mediaMetadata.getExtraction().setTemplateMappingProfile(null);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Success", mediaMetadata);
		}

		return response;
	}

	/**
	 * @param ex
	 * @return
	 */
	private Response<Object> handleMediaException(MediaServiceException ex) {
		Response<Object> response;
		LOG.error(ex.getMessage(), ex);
		
		int code = EventCode.SERVICE_ERROR.getCode();

		if (ex.getMessage().toUpperCase().contains(REPROCESS)){
			code = EventCode.SERVER_ERROR.getCode();
		}
		
		response = buildResponse(code, "Failed: " + ex.getMessage(), (BaseDomain) null);
		return response;
	}
}
