package com.mcmcg.dia.documentprocessor.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.documentprocessor.dao.IngestionTrackerDAO;
import com.mcmcg.dia.documentprocessor.exception.ServiceException;
import com.mcmcg.dia.iwfm.domain.NewDocumentStatus;

/**
 * 
 * @author Victor Arias
 *
 */
@Service
public class DocumentService extends BaseService{

	private static final Logger LOG = Logger.getLogger(DocumentService.class);

	@Autowired
	private IngestionTrackerDAO ingestionTrackerDAO;

	public boolean updateIngestionTracker(String documentId, NewDocumentStatus documentStatus) throws ServiceException {
		boolean success = false;

		try {
			success = ingestionTrackerDAO.update(documentId, documentStatus);
		} catch (Throwable e) {
			try {

			    waitTime();
			    success = ingestionTrackerDAO.update(documentId, documentStatus);
			    
			} catch (Throwable t) {
				String message = "Unable to update IngestionTracker " + t.getMessage();
				LOG.error(message, t);
				throw new ServiceException(message);
			}		
		}
		
		LOG.info(documentId + " - " + documentStatus.getStatus());

		return success;
	}

}
