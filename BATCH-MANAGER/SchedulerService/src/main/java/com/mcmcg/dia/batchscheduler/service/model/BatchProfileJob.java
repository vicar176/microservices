/**
 * 
 */
package com.mcmcg.dia.batchscheduler.service.model;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.mcmcg.dia.batchmanager.domain.BatchProfileSearchFilterDetail;
import com.mcmcg.dia.batchmanager.domain.Response;
import com.mcmcg.dia.batchscheduler.exception.ServiceException;
import com.mcmcg.dia.batchscheduler.service.batchmanager.BatchProfileJobService;
import com.mcmcg.dia.batchscheduler.service.batchmanager.IService;
import com.mcmcg.dia.batchscheduler.service.batchmanager.ServiceLocator;

/**
 * @author jaleman
 *
 */
@PersistJobDataAfterExecution
public class BatchProfileJob implements Job{

	private static final Logger LOG = Logger.getLogger(BatchProfileJob.class);
	
	@Autowired
	private ServiceLocator serviceLocator;

	
	/**
	 * 
	 */
	public BatchProfileJob() {
		
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		Long batchProfileId = dataMap.getLong("batchProfileId");
		String action = dataMap.getString("action");
		Integer version = dataMap.getInt("version");
		
		//Step 1 Insert Into BatchprofileJob with Start Time
		// Entity Object for Batch Profile to insert
		com.mcmcg.dia.batchmanager.entity.BatchProfileJob batchProfileJob = new com.mcmcg.dia.batchmanager.entity.BatchProfileJob();
		
		batchProfileJob.setBatchProfileId(batchProfileId);
		batchProfileJob.setVersion(version);
		
		//Step2 Fetch Filter values based on the batch profile Id---Done by ME
		
		
	    //Step 3 Get the list of document Id  based on the filters -- Aditi
		
		
		
		
		
		//Step 4 Enter the values in a temp table based on the Name of batch-- Needs to be done
		
		
		//Step 5 Call the end point based on the action-- Needs to be Done
		
		
		
	
		
		
		LOG.info(String.format("Executing Batch Profile Id %s --> Action %s ", batchProfileId, action));
	}
	
	private BatchProfileSearchFilterDetail retrieveBatchProfileFilters(Long batchProfileId) throws ServiceException{
		IService<BatchProfileSearchFilterDetail> batchProfileSearchFilterService = serviceLocator.getService(ServiceLocator.BATCHPROFILE_SERVICE_NAME);
		String resource = BatchProfileJobService.POST_BACTH_PROFILE_JOB;
			
		Response<BatchProfileSearchFilterDetail> response = batchProfileSearchFilterService.execute( resource, IService.GET);
		
		return response.getData();
	}
	
	
	/**
	 * 
	 * @param batchProfileJob
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	private BatchProfileJob saveBatchProfileJob(com.mcmcg.dia.batchmanager.entity.BatchProfileJob batchProfileJob) throws ServiceException {
		IService<BatchProfileJob> batchProfileJobService = serviceLocator.getService(ServiceLocator.BATCHPROFILEJOB_SERVICE_NAME);
		String resource = BatchProfileJobService.POST_BACTH_PROFILE_JOB;
			
		Response<BatchProfileJob> response = batchProfileJobService.execute( resource, IService.POST, batchProfileJob);
		
		return response.getData();
	}
	

}
