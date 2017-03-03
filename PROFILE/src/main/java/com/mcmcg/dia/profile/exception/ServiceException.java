package com.mcmcg.dia.profile.exception;

/**
 * @author Victor Arias
 *
 */
public class ServiceException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE  = "Service Error: ";
	
	/**
	 * @param message
	 * @param cause
	 */
	public ServiceException(String message, Throwable cause) {
		super(DEFAULT_MESSAGE +message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ServiceException(String message) {
		super(DEFAULT_MESSAGE + message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public ServiceException() {
		// TODO Auto-generated constructor stub
		super(DEFAULT_MESSAGE);
	}
}
