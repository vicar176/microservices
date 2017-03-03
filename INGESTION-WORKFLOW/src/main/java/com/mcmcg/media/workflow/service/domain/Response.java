/**
 * 
 */
package com.mcmcg.media.workflow.service.domain;

/**
 * @author jaleman
 *
 */
public class Response<T> {

	private Error error;
	private T data;

	/**
	 * 
	 * @return The status
	 */
	public Error getError() {
		return error;
	}

	/**
	 * 
	 * @param status
	 *            The status
	 */
	public void setError(Error error) {
		this.error = error;
	}

	/**
	 * 
	 * @return The data
	 */
	public T getData() {
		return data;
	}

	/**
	 * 
	 * @param data
	 *            The data
	 */
	public void setData(T data) {
		this.data = data;
	}

	public static class Error {

		private int code;
		private String message;

		/**
		 * 
		 * @return The code
		 */
		public int getCode() {
			return code;
		}

		/**
		 * 
		 * @param code
		 *            The code
		 */
		public void setCode(int code) {
			this.code = code;
		}

		/**
		 * 
		 * @return The message
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * 
		 * @param message
		 *            The message
		 */
		public void setMessage(String message) {
			this.message = message;
		}
	}
}
