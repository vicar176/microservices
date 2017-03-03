/**
 * 
 */
package com.mcmcg.dia.iwfm.exception;

/**
 * @author jaleman
 *
 */
public class MediaServiceException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE  = "Media Service Error: ";

	/**
	 * @param message
	 * @param cause
	 */
	public MediaServiceException(String message, Throwable cause) {
		super(DEFAULT_MESSAGE + message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public MediaServiceException(String message) {
		super(DEFAULT_MESSAGE + message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public MediaServiceException() {
		// TODO Auto-generated constructor stub
		super(DEFAULT_MESSAGE);
	}

}
