package com.mcmcg.dia.iwfm.automation;

import com.mcmcg.dia.iwfm.exception.BaseException;

/**
 * @author Victor Arias
 *
 */
public class ServiceException extends BaseException {

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "Service Error: ";

	/**
	 * @param message
	 * @param cause
	 */
	public ServiceException(String message, Throwable cause) {
		super(DEFAULT_MESSAGE + message, cause);
	}

	/**
	 * @param message
	 */
	public ServiceException(String message) {
		super(DEFAULT_MESSAGE + message);
	}

	public ServiceException() {
		super(DEFAULT_MESSAGE);
	}
}
