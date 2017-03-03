/**
 * 
 */
package com.mcmcg.dia.media.metadata.exception;

/**
 * @author Jose Aleman
 *
 */
@SuppressWarnings("unused")
public abstract class BaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public BaseException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public BaseException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	private BaseException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public BaseException() {
		// TODO Auto-generated constructor stub
	}

	
	
}
