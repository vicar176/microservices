package com.mcmcg.dia.documentprocessor.exception;

/**
 * @author Jose Aleman
 *
 */
public abstract class BaseException extends Exception {

	private static final long serialVersionUID = 1L;

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException() {
	}

}
