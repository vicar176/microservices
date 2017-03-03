package com.mcmcg.dia.media.metadata.aop;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mcmcg.dia.media.metadata.component.EncryptComponent;
import com.mcmcg.dia.media.metadata.exception.EncryptionException;
import com.mcmcg.dia.media.metadata.model.BaseModel;

/**
 * 
 * @author wporras
 *
 */
@Aspect
@Component
public class EncryptAspect {
	private final static Logger LOG = Logger.getLogger(EncryptAspect.class);

	@Autowired
	EncryptComponent encryptComponent;

	/**
	 * Advice for all save calls to encrypt the request
	 * 
	 * @param pjp
	 * @throws Throwable
	 * @throws EncryptionException
	 */
	@Around("execution(public * org.springframework.data.repository.Repository+.save(..))")
	public void encryptAdvice(ProceedingJoinPoint pjp) throws Throwable, EncryptionException {

		LOG.debug("encryptAdvice => .save(..)");

		Object[] args = pjp.getArgs();
		Object object = encryptComponent.encrypt((BaseModel) args[0]);

		pjp.proceed(new Object[] { object });

	}

	/**
	 * Advice for all find calls to decrypt the response
	 * 
	 * @param pjp
	 * @return object
	 * @throws EncryptionException
	 * @throws Throwable
	 */
	@Around("execution(public * org.springframework.data.repository.Repository+.find*(..))")
	public Object decryptAdvice(ProceedingJoinPoint pjp) throws EncryptionException, Throwable {

		LOG.debug("decryptAdvice => find*(..)");

		Object object = pjp.proceed();
		encryptComponent.decrypt(object);

		return object;

	}
}
