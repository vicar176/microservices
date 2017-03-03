package com.mcmcg.dia.batchmanager.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mcmcg.dia.batchmanager.dao.BatchProfileJobDAO;
import com.mcmcg.dia.batchmanager.dao.DocumentExceptionDAO;
import com.mcmcg.dia.batchmanager.domain.DocumentStatus;
import com.mcmcg.dia.batchmanager.entity.BatchProfileJob;
import com.mcmcg.dia.batchmanager.entity.DocumentException;
import com.mcmcg.dia.batchmanager.exception.PersistenceException;
import com.mcmcg.dia.batchmanager.exception.ServiceException;

@Service
public class BatchProfileJobService {

	@Autowired
	@Qualifier("batchProfileJobDAO")
	private BatchProfileJobDAO batchProfileJobDAO;

	@Autowired
	private DocumentExceptionDAO documentExceptionDAO;

	private static final Logger LOG = Logger.getLogger(BatchProfileJobService.class);

	/**
	 * 
	 * @param entity
	 * @return
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	public BatchProfileJob save(BatchProfileJob entity) throws ServiceException, PersistenceException {

		try {
			if (entity != null) {
				long key = batchProfileJobDAO.save(entity);
				entity.setBatchProfileJobId(key);
			} else {
				String errorMsj = "Unable to save a null value";
				ServiceException se = new ServiceException(errorMsj);
				LOG.error(errorMsj, se);
				throw se;
			}
		} catch (ServiceException se) {
			LOG.error(se.getMessage(), se);
			throw se;
		} catch (Throwable t) {
			String errorMsj = (!StringUtils.isBlank(t.getMessage()) ? t.getMessage()
					: "An error occured on save method");
			LOG.error(errorMsj, t);
			throw new ServiceException(errorMsj, t);
		}

		return entity;
	}

	/**
	 * 
	 * @return
	 * @throws PersistenceException
	 */
	public List<BatchProfileJob> retrieveAllBatchProfileJobs() throws PersistenceException {

		List<BatchProfileJob> batchProfileList = null;
		batchProfileList = batchProfileJobDAO.retrieveAllBatchProfileJobs();

		return batchProfileList;
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws PersistenceException
	 */
	public BatchProfileJob retrieveBatchProfileJobById(Long id) throws PersistenceException {

		BatchProfileJob batchProfileJob = batchProfileJobDAO.findOne(id);

		return batchProfileJob;
	}

	/**
	 * 
	 * @param batchProfileJobId
	 * @param status
	 * @return
	 */
	@Transactional
	public boolean updateCountersById(String documentId, Long batchProfileJobId, String status, String updatedBy)
			throws PersistenceException, ServiceException {

		boolean isOk = false;

		DocumentException documentException = getDocumentStatus(documentId, batchProfileJobId, status, updatedBy);
		
		//No DocumentException Found
		if (documentException.getExceptionId() == null ){
			
			//Evaluate if need to save doc exception
			if (updateJobCounters(batchProfileJobId, status, updatedBy)){
				documentExceptionDAO.saveDocException(documentException);
				isOk=true;
			}
			
			//Evaluates if status is different from current one
		}else if (documentException.getStatus() != DocumentStatus.getMap().get(status).getId()){
			
			String documentExceptionStatus = DocumentStatus.getIntegerMap().get(documentException.getStatus()).getDescription();
			
			updateJobCounters(documentId, batchProfileJobId, status,  documentExceptionStatus, updatedBy);

			isOk=true;
			
		}

		return isOk;
	}


	/***************************************************
	 * 
	 * 
	 * 
	 * PRIVATE METHODS
	 * 
	 * 
	 * 
	 * 
	 ****************************************************/

	/**
	 * @param status
	 * @throws PersistenceException 
	 */
	private DocumentException getDocumentStatus(String documentId, Long id, String status, String user) throws PersistenceException {
		DocumentStatus documentStatus = DocumentStatus.New;
		try {
			documentStatus = DocumentStatus.getMap().get(status.trim());
		} catch (Throwable e) {
			// nothing
		}
		
		DocumentException documentException = documentExceptionDAO.findByDocumentId(documentId, id);
		
		if (documentException.getStatus() == 0){
			documentException = populateDocumentException(documentId, id, documentStatus.getId(), user, status);
		}
		
		return documentException;
	}

	/**
	 * 
	 * @param id
	 * @param status
	 * @param updatedBy
	 * @return
	 * @throws PersistenceException 
	 * @throws ServiceException 
	 */
	private boolean updateJobCounters(Long id, String status, String updatedBy) throws PersistenceException, ServiceException {
		boolean isOk = false;
		BatchProfileJob batchProfileJob = batchProfileJobDAO.findOne(id);

		if (batchProfileJob != null && batchProfileJob.getBatchProfileJobId() == id) {
			batchProfileJob.setChangedBy(updatedBy);
			isOk = incrementCounters(status, batchProfileJob);
			batchProfileJobDAO.updateCounters(batchProfileJob);
		}

		return isOk;
	}
	
	/**
	 * 
	 * @param id
	 * @param status
	 * @param updatedBy
	 * @return
	 * @throws PersistenceException 
	 * @throws ServiceException 
	 */
	private boolean updateJobCounters(String documentId, Long id, String status, String documentExceptionStatus, String updatedBy) throws PersistenceException, ServiceException {
		boolean isOk = false;
		BatchProfileJob batchProfileJob = batchProfileJobDAO.findOne(id);

		if (batchProfileJob != null && batchProfileJob.getBatchProfileJobId() == id) {
			batchProfileJob.setChangedBy(updatedBy);
			
			incrementCounters(status, batchProfileJob);
			decrementCounters(documentExceptionStatus, batchProfileJob);
			
			if (StringUtils.containsIgnoreCase(DocumentStatus.Success.getDescription(), status)){
				documentExceptionDAO.removeByDocumentId(documentId, id);
			}else{
				documentExceptionDAO.updateByDocumentId(documentId, id, DocumentStatus.getMap().get(status).getId(), status);
			}
			
			batchProfileJobDAO.updateCounters(batchProfileJob);

			isOk = true;
		}

		return isOk;
	}

	/**
	 * @param status
	 * @param isOk
	 * @param batchProfileJob
	 * @return
	 * @throws ServiceException
	 */
	private boolean incrementCounters(String status, BatchProfileJob batchProfileJob)
			throws ServiceException {

		boolean isOk = false;
		switch (status.trim()) {

		case "Exception":
			isOk = true;
			batchProfileJob.setDocumentsInException(batchProfileJob.getDocumentsInException() + 1);
			break;

		case "Failed":
			isOk = true;
			batchProfileJob.setDocumentsFailed(batchProfileJob.getDocumentsFailed() + 1);
			break;

		case "Success":
			batchProfileJob.setDocumentsProcessed(batchProfileJob.getDocumentsProcessed() + 1);

			break;

		default:
			throw new ServiceException("No valid status " + status);
		}
		return isOk;
	}

	/**
	 * @param status
	 * @param isOk
	 * @param batchProfileJob
	 * @return
	 * @throws ServiceException
	 */
	private boolean decrementCounters(String status, BatchProfileJob batchProfileJob)
			throws ServiceException {

		boolean isOk = false;
		switch (status.trim()) {

		case "Exception":
			isOk = true;
			batchProfileJob.setDocumentsInException(batchProfileJob.getDocumentsInException() - 1);
			break;

		case "Failed":
			isOk = true;
			batchProfileJob.setDocumentsFailed(batchProfileJob.getDocumentsFailed() - 1);
			break;

		default:
			throw new ServiceException("No valid status " + status);
		}
		return isOk;
	}
	
	/**
	 * 
	 * @param documentId
	 * @param batchProfileJobId
	 * @param documentStatusId
	 * @param user
	 * @return
	 * @throws PersistenceException
	 */
	private DocumentException populateDocumentException(String documentId, Long batchProfileJobId, int documentStatusId,
			String user, String description) throws PersistenceException {
		DocumentException documentException = documentExceptionDAO.findByDocumentId(documentId, batchProfileJobId);
		if (documentException.getBatchProfileJobId() == null) {
			documentException = buildDocumentException(documentId, batchProfileJobId, documentStatusId, user, description);
		}

		return documentException;
	}

	/**
	 * 
	 * @param documentId
	 * @param batchProfileJobId
	 * @param documentStatusId
	 * @param user
	 * @return
	 */
	private DocumentException buildDocumentException(String documentId, Long batchProfileJobId, int documentStatusId,
			String user, String description) {

		DocumentException documentException = new DocumentException();

		documentException.setDocumentId(documentId);
		documentException.setStatus(documentStatusId);
		documentException.setBatchProfileJobId(batchProfileJobId);
		documentException.setChangedBy(user);
		documentException.setApplicationId(1);
		documentException.setWorkstationName(" ");
		documentException.setChangedOnDate(new Date());
		documentException.setCreatedOnDate(new Date());
		documentException.setCreatedBy(user);
		documentException.setErrorDescription(description);

		return documentException;

	}
}
