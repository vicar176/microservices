
package com.mcmcg.ingestion.web;

import javax.servlet.Filter;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.mcmcg.ingestion.configuration.IngestionServiceConfiguration;
import com.mcmcg.ingestion.web.filter.CorsFilter;
import com.mcmcg.ingestion.web.filter.PerformanceFilter;

/**
 * @author Jose Aleman
 *
 */
public class IngestionServiceInitializer extends AbstractAnnotationConfigDispatcherServletInitializer  {



	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { IngestionServiceConfiguration.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return null;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}
	
	@Override
	protected Filter[] getServletFilters() {
		
		return new Filter[] {
			      new CorsFilter(), new PerformanceFilter()
	    };
	}

}
