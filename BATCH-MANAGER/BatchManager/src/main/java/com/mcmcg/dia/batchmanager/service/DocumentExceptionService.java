package com.mcmcg.dia.batchmanager.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mcmcg.dia.batchmanager.dao.DocumentExceptionDAO;
import com.mcmcg.dia.batchmanager.domain.BatchProfileJobIdAndDocumentList;
import com.mcmcg.dia.batchmanager.entity.DocumentException;
import com.mcmcg.dia.batchmanager.exception.PersistenceException;
import com.mcmcg.dia.batchmanager.exception.ServiceException;

@Service
public class DocumentExceptionService extends BaseService{

	private static final int NOT_FOUND_STATUS_CODE = 8;
	@Autowired
	private DocumentExceptionDAO documentExceptionDAO;
	//private static final Logger LOG = Logger.getLogger(DocumentExceptionService.class);

	public DocumentExceptionService(){
		
	}
	/**
	 * 
	 * @param documentException
	 * @throws PersistenceException
	 */
	public DocumentException saveDocException(DocumentException documentException) throws ServiceException, PersistenceException {

		if (documentException == null || StringUtils.isBlank(documentException.getDocumentId())){
			throw new ServiceException("Document ID might be empty");
		}
			
		long key = documentExceptionDAO.saveDocException(documentException);
		
		if (key < 0){
			throw new ServiceException("Key is not valid");
		}
		
		documentException.setExceptionId(key);
		
		return documentException;

	}
	
	/**
	 * 
	 * @param documents
	 * @return
	 * @throws ServiceException
	 * @throws PersistenceException
	 */
	public boolean saveBatch(BatchProfileJobIdAndDocumentList documents) throws ServiceException, PersistenceException{
		
		boolean  isOk = false;
		
		if (CollectionUtils.isEmpty(documents.getDocuments()))
			throw new ServiceException(" No documents were found ");
		
		List<DocumentException> documentExceptions = new ArrayList<DocumentException>();
		
		for (String document : documents.getDocuments()){
			DocumentException documentException = populateDocumentException(documents.getBatchProfileJobId(), document, documents.getUser(), 
																			NOT_FOUND_STATUS_CODE, "Document was not found in DocumentImages table");
			
			documentExceptions.add(documentException);
		}
		
		if (documentExceptions.size() > 0){
			
			isOk = documentExceptionDAO.saveBatchOfDocumentExceptions(documentExceptions);
		}
		
		return isOk;
	}
	
	/**
	 * 
	 * @param batchProfileJobId
	 * @return
	 * @throws PersistenceException
	 */
	public List<DocumentException> retrieveDocumentExceptionsByProfileJobId(Long batchProfileJobId, int page, int size) throws  PersistenceException{
		
		return documentExceptionDAO.findByProfileJobId(batchProfileJobId, page, size);
	}

}
