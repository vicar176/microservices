package com.mcmcg.utility.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.utility.annotation.Auditable;
import com.mcmcg.utility.domain.Response;
import com.mcmcg.utility.domain.StatementTranslation;
import com.mcmcg.utility.domain.StatementTranslation.TranslateDocument;
import com.mcmcg.utility.domain.StatementType;
import com.mcmcg.utility.service.StatementTranslationService;
import com.mcmcg.utility.util.EventCode;

/**
 * 
 * @author Aditi SIngh
 *
 */

@RestController
@RequestMapping(value = "/statement-translations")
public class StatementTranslationRestcontroller extends BaseRestController {

	@Autowired
	StatementTranslationService statementTranslationService;

	/**
	 * 
	 * @param documentId
	 * @param mediaMetadata
	 * @return
	 */
	@Auditable(eventCode = EventCode.REQUEST_SUCCESS)
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> statementTranslation(
			@RequestBody StatementTranslation statementTranslation) {

		Response<Object> response = null;

		StatementTranslation statementTranslated = new StatementTranslation();

		try {
			statementTranslated = statementTranslationService.statementTranslation(statementTranslation);

			if (statementTranslated == null || statementTranslated.getDocumentList() == null
					|| statementTranslated.getDocumentList().isEmpty()) {

				statementTranslated = new StatementTranslation();
				statementTranslated.setDocumentList(statementTranslation.getDocumentList());
				for (TranslateDocument translateDocument : statementTranslation.getDocumentList()) {
					translateDocument.setTranslatedType(StatementType.STATEMENT.getType());
				}
			}
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Document successfully translated",
					statementTranslated);

		} catch (Exception ex) {
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), ex.getMessage(), null);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

	}

}
