
package com.mcmcg.dia.ingestionState.web;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.mcmcg.dia.ingestionState.configuration.ServiceConfiguration;
import com.mcmcg.dia.ingestionState.web.filter.CorsFilter;

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
			      new CorsFilter()
	    };
	}
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
	}
}
