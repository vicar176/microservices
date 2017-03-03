package com.mcmcg.dia.account.metadata.exception;

/**
 * 
 * @author wporras
 *
 */
public class EncryptionException extends RuntimeException  {

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "Encryption Error: ";

	/**
	 * @param message
	 * @param cause
	 */
	public EncryptionException(String message, Throwable cause) {
		super(DEFAULT_MESSAGE + message, cause);
	}

	/**
	 * @param message
	 */
	public EncryptionException(String message) {
		super(DEFAULT_MESSAGE + message);
	}

	public EncryptionException() {
		super(DEFAULT_MESSAGE);
	}
}
