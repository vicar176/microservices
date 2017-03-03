
package com.mcmcg.dia.batchscheduler.exception;

/**
 * @author jaleman
 *
 */
public class JobSchedulerException extends BaseException {

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "Job Scheduler Error: ";

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public JobSchedulerException(String message, Throwable cause) {
		super(DEFAULT_MESSAGE + message, cause);
	}

	/**
	 * 
	 * @param message
	 */
	public JobSchedulerException(String message) {
		super(DEFAULT_MESSAGE + message);
	}

	public JobSchedulerException() {
		super(DEFAULT_MESSAGE);
	}

}
