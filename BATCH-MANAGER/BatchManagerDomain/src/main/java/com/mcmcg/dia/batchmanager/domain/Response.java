package com.mcmcg.dia.batchmanager.domain;

/**
 * 
 * @author Victor Arias
 *
 */
public class Response<T> {

	private Error error;
	private T data;

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public static class Error {

		private int code;
		private String message;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}
