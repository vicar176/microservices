package com.mcmcg.utility.exception;

/**
 * @author jaleman
 *
 */
public class S3Exception extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "S3 Error: ";

	/**
	 * @param message
	 * @param cause
	 */
	public S3Exception(String message, Throwable cause) {
		super(DEFAULT_MESSAGE + message, cause);
	}

	/**
	 * @param message
	 */
	public S3Exception(String message) {
		super(DEFAULT_MESSAGE + message);
	}

	public S3Exception() {
		super(DEFAULT_MESSAGE);
	}

}
