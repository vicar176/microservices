/**
 * 
 */
package com.mcmcg.dia.batchscheduler.restcontroller;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mcmcg.dia.batchmanager.domain.BatchProfileWithAction;
import com.mcmcg.dia.batchmanager.domain.Response;
import com.mcmcg.dia.batchmanager.util.EventCode;
import com.mcmcg.dia.batchscheduler.job.JobScheduler;



/**
 * @author jaleman
 *
 */
@RestController
@RequestMapping(value = "/scheduled-batches")
public class JobSchedulerRestController extends BaseRestController{

	private static final Logger LOG = Logger.getLogger(JobSchedulerRestController.class);
	
	
	/**
	 * 
	 */
	public JobSchedulerRestController() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param batchProfile
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.PUT)
	public ResponseEntity<Response<Object>> updateScheduledBatch(@RequestBody BatchProfileWithAction batchProfile)  {
		Response<Object> response = null;
		try {

			JobScheduler.reschedule(batchProfile);
			response = buildResponse(EventCode.REQUEST_SUCCESS.getCode(), "Success", true);
			
		} catch (JobExecutionException e) {
			LOG.error(e.toString(), e);
			response = buildResponse(EventCode.SERVICE_ERROR.getCode(), "Failed", true);
		}

		return new ResponseEntity<Response<Object>>(response, HttpStatus.OK);
	}
	


}
