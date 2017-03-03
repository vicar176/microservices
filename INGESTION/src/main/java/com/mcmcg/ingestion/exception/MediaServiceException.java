/**
 * 
 */
package com.mcmcg.ingestion.exception;

/**
 * @author jaleman
 *
 */
public class MediaServiceException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @param message
	 * @param cause
	 */
	private static final String DEFAULT_MESSAGE  = "Media Service Error: ";

	
	/**
	 * @param message
	 * @param cause
	 */
	public MediaServiceException(String message, Throwable cause) {

		super(DEFAULT_MESSAGE + message, cause);
		
	}

	/**
	 * @param message
	 */
	public MediaServiceException(String message) {
		
		super(DEFAULT_MESSAGE + message);
		
	}

	/**
	 * 
	 */
	public MediaServiceException() {
		
		super(DEFAULT_MESSAGE);
		
	}

}
