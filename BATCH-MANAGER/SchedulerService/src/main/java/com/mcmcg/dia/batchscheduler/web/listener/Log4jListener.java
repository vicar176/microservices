
package com.mcmcg.dia.batchscheduler.web.listener;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.PropertyConfigurator;

/**
 * @author jaleman
 *
 */
@WebListener
public class Log4jListener implements ServletContextListener {

	public Log4jListener() {
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// initialize log4j
		ServletContext context = event.getServletContext();
		String fullPath = context.getRealPath("") + File.separator + "WEB-INF/classes/log4j.properties";
		PropertyConfigurator.configure(fullPath);

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
