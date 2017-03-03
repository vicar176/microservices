/**
 * 
 */
package com.mcmcg.ingestion.exception;

/**
 * @author jaleman
 *
 */
public class IngestionServiceException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE  = "Ingestion Service Error: ";

	/**
	 * @param message
	 * @param cause
	 */
	public IngestionServiceException(String message, Throwable cause) {
		super(DEFAULT_MESSAGE + message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public IngestionServiceException(String message) {
		super(DEFAULT_MESSAGE + message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public IngestionServiceException() {
		// TODO Auto-generated constructor stub
		super(DEFAULT_MESSAGE);
	}

}
