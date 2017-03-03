package com.mcmcg.dia.batchscheduler.web.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * @author jaleman
 *
 */
@Component
public class ClosedContextListener implements ApplicationListener<ContextClosedEvent> {

	private static final Logger LOG = Logger.getLogger(ClosedContextListener.class);

	public ClosedContextListener() {

	}


	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		LOG.info("Context stopped ");
	}

}
