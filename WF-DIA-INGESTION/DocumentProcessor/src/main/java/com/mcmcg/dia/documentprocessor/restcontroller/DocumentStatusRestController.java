package com.mcmcg.dia.documentprocessor.restcontroller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.documentprocessor.common.EventCode;
import com.mcmcg.dia.documentprocessor.dao.DocumentStatusDAO;
import com.mcmcg.dia.documentprocessor.entity.DocumentStatus;
import com.mcmcg.dia.iwfm.domain.Response;


@RestController
@RequestMapping(value = "/document-statuses")
public class DocumentStatusRestController extends BaseRestController {

	private static final Logger LOG = Logger.getLogger(DocumentStatusRestController.class);

	@Autowired
	private DocumentStatusDAO documentStatusDAO;
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Response<Object>> retrieveAll() {

		Response<Object> response = null;

		try {
			List<DocumentStatus> list = documentStatusDAO.findAll();

			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(),"FindAll () ",	list);

		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			response = buildResponse(EventCode.SERVER_ERROR.getCode(), e.getMessage(), (Object) null);
		}

		ResponseEntity<Response<Object>> responseEntity = new ResponseEntity<Response<Object>>(response, HttpStatus.OK);

		return responseEntity;

	}


}