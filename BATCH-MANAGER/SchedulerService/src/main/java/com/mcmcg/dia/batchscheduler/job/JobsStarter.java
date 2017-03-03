/**
 * 
 */
package com.mcmcg.dia.batchscheduler.job;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.mcmcg.dia.batchmanager.domain.BatchProfileWithAction;
import com.mcmcg.dia.batchmanager.domain.Response;
import com.mcmcg.dia.batchmanager.entity.Action;
import com.mcmcg.dia.batchmanager.entity.BatchProfile;
import com.mcmcg.dia.batchscheduler.exception.ServiceException;
import com.mcmcg.dia.batchscheduler.service.batchmanager.BatchProfileService;
import com.mcmcg.dia.batchscheduler.service.batchmanager.IService;
import com.mcmcg.dia.batchscheduler.service.batchmanager.ServiceLocator;

/**
 * @author jaleman
 *
 */
@Component
public class JobsStarter {

	private static final Logger LOG = Logger.getLogger(JobsStarter.class);

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	
	
	@Autowired
	private ServiceLocator serviceLocator;
	
	@Autowired
	private BatchProfileService batchProfileService;

	/**
	 * 
	 */
	public JobsStarter() {
		// TODO Auto-generated constructor stub
	}

	public void startJobs() {

		LOG.info("Starting Jobs");
		
		List<BatchProfileWithAction> batchProfileWithActions;
		try {
			batchProfileWithActions = retrieveBatchProfileWithAction();
			for (BatchProfileWithAction batchProfileWithAction : batchProfileWithActions) {
				
				taskExecutor.execute(new ScheduleJob(batchProfileWithAction));
			}
		} catch (ServiceException e) {
			LOG.error("Error while processing " + e.getMessage());
			e.printStackTrace();
		}
		
		
	}

	/**
	 * TODO
	 * 
	 * private List<BatchProfile> getActiveJobs();
	 * 
	 * 
	 */

	
	private BatchProfileWithAction mockBatchProfileWithAction(){
		
		BatchProfile batchProfile = new BatchProfile();
		batchProfile.setActionId(1L);
		batchProfile.setBatchProfileId(1L);
		batchProfile.setCreationMethod("");
		batchProfile.setScheduleDate(new Date());
		batchProfile.setScheduleTime(new Date());
		batchProfile.setFrecuency(1L); //need to be updated with the right value
		
		Action action = new Action();
		action.setId(1L);
		action.setDescription("Ingest");
		
		BatchProfileWithAction batchProfileWithAction = new BatchProfileWithAction();
		batchProfileWithAction.setBatchProfile(batchProfile);
		batchProfileWithAction.setAction(action);
		
		return batchProfileWithAction;
	}
	
	private List<BatchProfileWithAction>  retrieveBatchProfileWithAction() throws ServiceException{

		String resource = BatchProfileService.GET_BATCHPROFILE_WITH_ACTION;
			
		Response<List<BatchProfileWithAction>> response = batchProfileService.execute( resource, IService.GET);
		
		return response.getData();
	}
}

/**
 * 
 * @author jaleman
 *
 */
class ScheduleJob implements Runnable {

	private static final Logger LOG = Logger.getLogger(ScheduleJob.class);

	private BatchProfileWithAction batchProfile;

	public ScheduleJob(BatchProfileWithAction batchProfile) {
		this.batchProfile = batchProfile;
	}

	@Override
	public void run() {

		try {
			
			JobScheduler.schedule(batchProfile);
			LOG.info("Job Started " + batchProfile.getBatchProfile().getBatchProfileId() + " -> Action:  " + batchProfile.getAction().getDescription());
			
		} catch (JobExecutionException e) {
			LOG.warn(e.getMessage(), e); 
		}

	}
	
}
