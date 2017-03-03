package com.mcmcg.dia.batchmanager.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mcmcg.dia.batchmanager.dao.FilteringDAO;
import com.mcmcg.dia.batchmanager.domain.PagedResponse;
import com.mcmcg.dia.batchmanager.entity.BatchProfileJobDetails;
import com.mcmcg.dia.batchmanager.entity.BatchProfileJobDocumentDetail;
import com.mcmcg.dia.batchmanager.exception.PersistenceException;
import com.mcmcg.dia.batchmanager.exception.ServiceException;

@Service
public class FilteringService {

	private static final Logger LOG = Logger.getLogger(FilteringService.class);

	@Autowired
	private FilteringDAO filteringDAO;
	
	
	@Value("${s3BucketBatchFiles}")
	String filePath;
	
	public PagedResponse<Map<String, Object>> filteringBatchStatus(String filter, String sort, int page, int size)
			throws ServiceException {
		PagedResponse<Map<String, Object>> response = null;
		try {
			int total = filteringDAO.filteringCountBatchProfile(filter);
			List<Map<String, Object>> results = filteringDAO.filteringBatchProfile(filter, sort, page, size);
			/**
			 * If s3BucketBatchFiles not defined in context.xml then put hard-core entry for now : to be removed later.
			 */
			if(filePath==null ||filePath.isEmpty()){
				filePath ="mcm-s3-media-batch-files-dev";
			}
			for(Map<String,Object> map :results){
				map.put("fileURL", filePath+"/objects?key=Ingest/"+map.get("fileName"));
			}
			response = new PagedResponse<Map<String, Object>>(total, page, size, results);
		} catch (PersistenceException e) {
			ServiceException se = new ServiceException(e.getMessage());
			LOG.error(se);
			throw se;
		}
		return response;
	}

	public PagedResponse<Map<String, Object>> filteringScheduledBatches(String filter, String sort, int page, int size)
			throws ServiceException {
		PagedResponse<Map<String, Object>> response = null;
		try {
			int total = filteringDAO.filteringCountScheduledBatches(filter);
			List<Map<String, Object>> results = filteringDAO.filteringScheduledBatches(filter, sort, page, size);
			response = new PagedResponse<Map<String, Object>>(total, page, size, results);
		} catch (PersistenceException e) {
			ServiceException se = new ServiceException(e.getMessage());
			LOG.error(se);
			throw se;
		}
		return response;
	}

	public PagedResponse<BatchProfileJobDocumentDetail> filteringBatchJobStatusDocumentDetail(String filter,
			String sort, int page, int size, String batchProfileJobId, String status) throws ServiceException {
		PagedResponse<BatchProfileJobDocumentDetail> response = null;
		try {
			int total = filteringDAO.filteringCountBatchJobStatusDocumentDetail(filter, batchProfileJobId, status);
			List<BatchProfileJobDocumentDetail> results = filteringDAO.filteringBatchJobStatusDocumentDetail(filter,
					sort, page, size, batchProfileJobId, status);
			response = new PagedResponse<BatchProfileJobDocumentDetail>(total, page, size, results);
		} catch (PersistenceException e) {
			ServiceException se = new ServiceException(e.getMessage());
			LOG.error(se);
			throw se;
		}
		return response;
	}

	public PagedResponse<BatchProfileJobDetails> filteringBatchStatusDetails(String batchProfileJobId)
			throws ServiceException {
		PagedResponse<BatchProfileJobDetails> response = null;
		try {
			List<BatchProfileJobDetails> results = filteringDAO.filteringBatchStatusDetails(batchProfileJobId);
			response = new PagedResponse<BatchProfileJobDetails>(1, 1, 25, results);
		} catch (PersistenceException e) {
			ServiceException se = new ServiceException(e.getMessage());
			LOG.error(se);
			throw se;
		}
		return response;
	}
 
}
