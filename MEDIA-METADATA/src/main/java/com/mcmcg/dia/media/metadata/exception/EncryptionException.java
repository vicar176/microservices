package com.mcmcg.dia.media.metadata.exception;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author wporras
 *
 */
public class EncryptionException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "Encryption Error: ";

	/**
	 * @param message
	 * @param cause
	 */
	public EncryptionException(String message, Throwable cause) {
		super(DEFAULT_MESSAGE + StringUtils.replace(message, DEFAULT_MESSAGE, "", 1), cause);
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
