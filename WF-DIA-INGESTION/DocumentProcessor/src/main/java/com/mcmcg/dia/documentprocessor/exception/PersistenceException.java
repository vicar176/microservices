package com.mcmcg.dia.documentprocessor.exception;

/**
 * @author jaleman
 *
 */
public class PersistenceException extends BaseException {

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "Database Error: ";

	/**
	 * @param message
	 * @param cause
	 */
	public PersistenceException(String message, Throwable cause) {
		super(DEFAULT_MESSAGE + message, cause);
	}

	/**
	 * @param message
	 */
	public PersistenceException(String message) {
		super(DEFAULT_MESSAGE + message);
	}

	public PersistenceException() {
		super(DEFAULT_MESSAGE);
	}

}
