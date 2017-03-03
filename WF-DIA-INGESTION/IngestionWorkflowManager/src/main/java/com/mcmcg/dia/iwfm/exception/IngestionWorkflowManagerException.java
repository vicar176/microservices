/**
 * 
 */
package com.mcmcg.dia.iwfm.exception;

/**
 * @author jaleman
 *
 */
public class IngestionWorkflowManagerException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE  = "Ingestion Workflow Manager Error: ";

	/**
	 * @param message
	 * @param cause
	 */
	public IngestionWorkflowManagerException(String message, Throwable cause) {
		super(DEFAULT_MESSAGE + message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public IngestionWorkflowManagerException(String message) {
		super(DEFAULT_MESSAGE + message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public IngestionWorkflowManagerException() {
		// TODO Auto-generated constructor stub
		super(DEFAULT_MESSAGE);
	}

}
