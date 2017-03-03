package com.mcmcg.dia.documentprocessor.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.documentprocessor.dao.DocumentImagesDAO;
import com.mcmcg.dia.documentprocessor.exception.PersistenceException;
import com.mcmcg.dia.documentprocessor.exception.ServiceException;
import com.mcmcg.dia.iwfm.domain.NewDocumentStatus;


@Service
public class DocumentImagesService extends BaseService {

	private static final Logger LOG = Logger.getLogger(DocumentImagesService.class);
	@Autowired
	private DocumentImagesDAO documentImagesDAO;
	
	public List<Map<String,Object>>  findDocumentImageStatus() throws ServiceException {
		List<Map<String,Object>>  ingestionStates = null;

		try {
			ingestionStates = documentImagesDAO.findDocumentImageStatus();
		} catch (PersistenceException pe) {
			ServiceException se = new ServiceException("An error occurred while trying to find documents status");
			LOG.error(se.getMessage(), se);
			throw se;
		}

		return ingestionStates;
	}
	
	/**
	 * 
	 * @param documentId
	 * @param documentStatus
	 * @return
	 * @throws ServiceException
	 */
	public boolean updateDocumentImages(String documentId, NewDocumentStatus documentStatus) throws ServiceException {
		boolean success = false;

		try {
			success = documentImagesDAO.update(documentId, documentStatus);
		} catch (Throwable e) {
			try {
				
				waitTime();
				success = documentImagesDAO.update(documentId, documentStatus);
				
			} catch (Throwable t) {
				String message = "Unable to update DocumentImages " + t.getMessage();
				LOG.error(message, t);
				throw new ServiceException(message);
			}
		}
		
		LOG.info(documentId + " - " + documentStatus.getStatus());
		
		return success;
	}
	
	
	
}
