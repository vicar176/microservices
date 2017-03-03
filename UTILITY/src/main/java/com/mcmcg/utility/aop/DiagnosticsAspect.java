package com.mcmcg.utility.aop;

import java.util.Arrays;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.mcmcg.utility.annotation.Diagnostics;

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

	@Around("@annotation(diagnostics)")
	public Object aroundEvent(ProceedingJoinPoint pjp, Diagnostics diagnostics) throws Throwable {
		long start = System.currentTimeMillis();
		Object o = pjp.proceed();
		long end = System.currentTimeMillis();

		log(pjp.getSignature().getName(), pjp.getArgs(), diagnostics.area(), start, end);

		return o;
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
