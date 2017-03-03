package com.mcmcg.dia.documentprocessor.web.filter;

import java.io.IOException;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

public class PerformanceFilter extends OncePerRequestFilter {

	private final static Logger LOG = Logger.getLogger(PerformanceFilter.class);

	public PerformanceFilter() {
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String message = String.format("Request [%s] Parameters %s", request.getRequestURI(),
				buildStringFromRequestParameters(request));

		long start = System.currentTimeMillis();

		filterChain.doFilter(request, response);

		long end = System.currentTimeMillis();

		LOG.info(String.format("Call %s \t Spent time %d", message, end - start));

	}

	/**
	 * 
	 * @param response
	 * @return
	 */
	private String buildStringFromRequestParameters(HttpServletRequest request) {

		StringBuilder builder = new StringBuilder();
		Set<String> keySet = request.getParameterMap().keySet();

		for (String key : keySet) {
			builder.append("[" + key + "=" + request.getParameter(key) + "]");
			builder.append(", ");
		}

		return builder.toString();

	}

}
