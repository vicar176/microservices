/**
 * 
 */
package com.mcmcg.dia.profile.web;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.mcmcg.dia.profile.configuration.ProfileServiceConfiguration;
import com.mcmcg.dia.profile.web.filter.CorsFilter;

/**
 * @author Jose Aleman
 *
 */
public class ProfileServiceInitializer extends AbstractAnnotationConfigDispatcherServletInitializer  {



	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { ProfileServiceConfiguration.class };
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
		// TODO Auto-generated method stub
		super.onStartup(servletContext);
	}
}
