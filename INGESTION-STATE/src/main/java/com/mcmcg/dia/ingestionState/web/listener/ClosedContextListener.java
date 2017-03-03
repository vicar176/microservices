package com.mcmcg.dia.ingestionState.web.listener;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import com.mcmcg.dia.ingestionState.annotation.Auditable;
import com.mcmcg.dia.ingestionState.util.EventCode;

/**
 * @author jaleman
 *
 */
@Component
public class ClosedContextListener implements ApplicationListener<ContextClosedEvent> {


	private static final Logger LOG = Logger.getLogger(ClosedContextListener.class);

	public ClosedContextListener() {

	}

	@Auditable(eventCode = EventCode.STOP)
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		LOG.info("Context stopped ");
	}

}
