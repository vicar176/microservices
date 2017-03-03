/**
 * 
 */
package com.mcmcg.dia.profile.exception;

/**
 * @author jaleman
 *
 */
public class PersistenceException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE  = "Database Error: ";

	/**
	 * @param message
	 * @param cause
	 */
	public PersistenceException(String message, Throwable cause) {
		super(DEFAULT_MESSAGE + message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public PersistenceException(String message) {
		super(DEFAULT_MESSAGE + message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public PersistenceException() {
		// TODO Auto-generated constructor stub
		super(DEFAULT_MESSAGE);
	}

}

