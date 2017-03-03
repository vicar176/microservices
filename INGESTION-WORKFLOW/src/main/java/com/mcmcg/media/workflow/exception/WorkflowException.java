/**
 * 
 */
package com.mcmcg.media.workflow.exception;

/**
 * @author jaleman
 *
 */
public class WorkflowException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 * @param cause
	 */
	public WorkflowException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public WorkflowException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public WorkflowException() {
		// TODO Auto-generated constructor stub
	}
}
