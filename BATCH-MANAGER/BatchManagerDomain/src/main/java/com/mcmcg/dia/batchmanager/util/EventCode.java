
package com.mcmcg.dia.batchmanager.util;

/**
 * @author Jose Aleman
 *
 */
public enum EventCode {

	START(100), 
	ASYNCH_START(102),
	REQUEST_SUCCESS(200),
	OBJECT_CREATED(201),
	BAD_REQUEST(400),
	AUTH_ERROR (401),
	NOT_IMPLEMENTED (501),
	SERVER_ERROR(500),
	DATABASE_ERROR(550),
	SERVICE_ERROR(551),
	STOP(100);

	private final Integer code;

	/**
	 * 
	 * @param code
	 */
	private EventCode(final Integer code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return code.toString();
	}
	
	public Integer getCode() {
		return code;
	}
}
