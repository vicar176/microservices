package com.mcmcg.media.workflow.util;

import java.util.Arrays;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class DiagnosticsLogger {

	private final static Logger LOG = Logger.getLogger(DiagnosticsLogger.class);
	private Object lock = new Object();
	
	/**
	 * 
	 */
	public DiagnosticsLogger() {

	}

	/**
	 * @param pjp
	 * @param diagnostics
	 * @param start
	 * @param end
	 */
	public void log(String pjpMethod, Object[] args, String area, long start, long end) {
		String method = String.format("%s (%s) ", pjpMethod, Arrays.deepToString(args));
		
		synchronized (lock) {
			((AppenderSkeleton)Logger.getRootLogger().getAppender("MetricsRollingAppender")).setThreshold(Level.toLevel("DEBUG"));
			LOG.info(String.format("\t%s\t%s\tTime\t%d", area, method, end-start));
			((AppenderSkeleton)Logger.getRootLogger().getAppender("MetricsRollingAppender")).setThreshold(Level.toLevel("OFF"));
		}
	}


}
