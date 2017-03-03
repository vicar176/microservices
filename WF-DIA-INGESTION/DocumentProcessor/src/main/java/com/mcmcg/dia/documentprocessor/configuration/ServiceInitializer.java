
package com.mcmcg.dia.documentprocessor.configuration;

import javax.servlet.Filter;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.mcmcg.dia.documentprocessor.web.filter.CorsFilter;
import com.mcmcg.dia.documentprocessor.web.filter.PerformanceFilter;

/**
 * @author Jose Aleman
 *
 */
public class ServiceInitializer extends AbstractAnnotationConfigDispatcherServletInitializer  {



	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { ServiceConfiguration.class };
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
