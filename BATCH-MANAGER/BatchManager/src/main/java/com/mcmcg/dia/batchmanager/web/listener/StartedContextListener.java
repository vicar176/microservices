
package com.mcmcg.dia.batchmanager.web.listener;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired
	private DataSource dataSource;

	public StartedContextListener() {

	}

	//@Auditable(eventCode = EventCode.START)
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOG.info("Context started ");
		
		try {
			if(dataSource.getConnection().isClosed()) {
				
			}
		} catch (SQLException e) {
			LOG.error(e.getMessage(), e);
		}
	}

}
