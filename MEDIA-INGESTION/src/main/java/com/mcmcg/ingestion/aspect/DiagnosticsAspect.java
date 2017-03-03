/**
 * 
 */
package com.mcmcg.ingestion.aspect;

import java.util.Arrays;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.mcmcg.ingestion.annotation.Diagnostics;

/**
 * @author jaleman
 *
 */
@Aspect
@Component
public class DiagnosticsAspect {

	private final static Logger LOG = Logger.getLogger(DiagnosticsAspect.class);
	private Object lock = new Object();
	
	/**
	 * 
	 */
	public DiagnosticsAspect() {

	}

	@Around("execution(public * *(..)) && @annotation(diagnostics)")
	public Object aroundEvent(ProceedingJoinPoint pjp, Diagnostics diagnostics) throws Throwable {
		long start = System.currentTimeMillis();
		Object o = pjp.proceed();
		long end = System.currentTimeMillis();

		String method = String.format("%s (%s) ", pjp.getSignature().getName(), Arrays.deepToString(pjp.getArgs()));
		
		synchronized (lock) {
			LOG.debug(method);
			((AppenderSkeleton)Logger.getRootLogger().getAppender("MetricsRollingAppender")).setThreshold(Level.toLevel("DEBUG"));
			LOG.debug(String.format("%s\tTime\t%d", pjp.getSignature().getName(), end-start));
			((AppenderSkeleton)Logger.getRootLogger().getAppender("MetricsRollingAppender")).setThreshold(Level.toLevel("OFF"));
		}

		return o;
	}
}
