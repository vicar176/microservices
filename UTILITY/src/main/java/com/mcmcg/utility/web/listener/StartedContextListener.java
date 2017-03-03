package com.mcmcg.utility.web.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author Jose Aleman
 *
 */
@Component
public class StartedContextListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOG = Logger.getLogger(StartedContextListener.class);

	/**
	 * 
	 */
	public StartedContextListener() {

	}

	// @Auditable(eventCode=EventCode.START)
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOG.info("Context started ");
	}

}
