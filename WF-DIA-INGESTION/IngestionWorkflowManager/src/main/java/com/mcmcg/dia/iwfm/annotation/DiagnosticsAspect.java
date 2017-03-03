/**
 * 
 */
package com.mcmcg.dia.iwfm.annotation;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author jaleman
 *
 */
@Aspect
@Component
public class DiagnosticsAspect {

	private final static Logger LOG = Logger.getLogger(DiagnosticsAspect.class);
	
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
		
		LOG.debug(method + " : duration Time ==>  " + (end - start)/1000 + " sec");

		return o;
	}
}
