/**
 * 
 */
package com.mcmcg.utility.web.listener;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.mcmcg.utility.annotation.Auditable;
import com.mcmcg.utility.util.EventCode;

/**
 * @author jaleman
 *
 */
@Component
public class ClosedContextListener implements ApplicationListener<ContextClosedEvent> {


	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	private static final Logger LOG = Logger.getLogger(ClosedContextListener.class);

	/**
	 * 
	 */
	public ClosedContextListener() {

	}

	@Auditable(eventCode = EventCode.STOP)
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		LOG.info("Context stopped ");
		if(taskExecutor != null){
			taskExecutor.shutdown();
			taskExecutor = null;
		}
	}

}
