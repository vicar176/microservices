/**
 * 
 */
package com.mcmcg.media.workflow.service.exception;

/**
 * @author jaleman
 *
 */
public class MediaServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public MediaServiceException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public MediaServiceException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public MediaServiceException() {
		// TODO Auto-generated constructor stub
	}
}
