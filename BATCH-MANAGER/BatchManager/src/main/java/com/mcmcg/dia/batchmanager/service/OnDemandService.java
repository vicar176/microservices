package com.mcmcg.dia.batchmanager.service;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mcmcg.dia.batchmanager.dao.BatchProfileDAO;
import com.mcmcg.dia.batchmanager.dao.BatchProfileJobDAO;
import com.mcmcg.dia.batchmanager.domain.BatchCreationOnDemand;
import com.mcmcg.dia.batchmanager.entity.BatchProfile;
import com.mcmcg.dia.batchmanager.entity.BatchProfileJob;
import com.mcmcg.dia.batchmanager.exception.ServiceException;

@Service
public class OnDemandService extends BaseService{

	@Autowired
	@Qualifier("batchProfileDAO")
	private BatchProfileDAO batchProfileDAO;

	@Autowired
	@Qualifier("batchProfileJobDAO")
	private BatchProfileJobDAO batchProfileJobDAO;

	private static final Logger LOG = Logger.getLogger(OnDemandService.class);

	@Transactional(rollbackFor={ServiceException.class,PersistenceException.class,Throwable.class})
	public Long saveOnDemand(BatchCreationOnDemand batchCreationDomain) throws ServiceException, PersistenceException {
		BatchProfile entity = buildBatchProfileEntity(batchCreationDomain);
		long key = -1; 
		try {
			if (entity != null) {
				long batchProfileKey = batchProfileDAO.save(entity);
				
				BatchProfileJob batchProfileJob = populateBatchProfileJob(batchProfileKey, 
																		  batchCreationDomain.getTotalOfDocuments(), 
																		  batchCreationDomain.getUser());
				
				key = batchProfileJobDAO.save(batchProfileJob);
				
			} else {
				String errorMsj = "Unable to save a null value";
				ServiceException se = new ServiceException(errorMsj);
				LOG.error(errorMsj, se);
				throw se;
			}
		} catch (PersistenceException pe) {
			LOG.error(pe.getMessage(), pe);
			throw pe;
		} catch (ServiceException se) { 
			LOG.error(se.getMessage(), se);
			throw se;
		} catch (Throwable t) {
			String errorMsj = (!StringUtils.isBlank(t.getMessage()) ? t.getMessage() : "An error occured on save method");
			LOG.error(errorMsj, t);
			throw new ServiceException(errorMsj, t);
		}

		return key;
	}
	
}
