
package com.mcmcg.dia.batchmanager.exception;

/**
 * @author Jose Aleman
 *
 */
@SuppressWarnings("unused")
public abstract class BaseException extends Exception {

	private static final long serialVersionUID = 1L;

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseException(String message) {
		super(message);
	}

	private BaseException(Throwable cause) {
		super(cause);
	}

	public BaseException() {
		// TODO Auto-generated constructor stub
	}

}
