
package com.mcmcg.dia.batchscheduler.web.listener;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.mcmcg.dia.batchscheduler.job.JobsStarter;

/**
 * @author Jose Aleman
 *
 */
@Component
public class StartedContextListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOG = Logger.getLogger(StartedContextListener.class);
	
	private boolean isInitialized = false;
	
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	
	@Autowired
	private JobsStarter jobsStarter;
	
	public StartedContextListener() {

	}

	//@Auditable(eventCode = EventCode.START)
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOG.info("Context started ");
		
		if (!isInitialized){
			taskExecutor.execute(new Runnable() {
				
				@Override
				public void run() {
					jobsStarter.startJobs();
				}
			}, 60000);
			isInitialized = true;
		}
		
	}

}

