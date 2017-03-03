package com.mcmcg.utility.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;

public class FileVO {

	private String fileName;
	private String path;
	private ByteArrayOutputStream byteArrayOutputStream;

	public FileVO() {
	}

	public FileVO(InputStream inputStream, String fileName) throws IOException {
		this.fileName = fileName;

		byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = inputStream.read(buffer)) > -1) {
			byteArrayOutputStream.write(buffer, 0, len);
		}
		byteArrayOutputStream.flush();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public InputStream getInputStream() {
		return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
	}

	public void setInputStream(InputStream inputStream) throws IOException {
		byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = inputStream.read(buffer)) > -1) {
			byteArrayOutputStream.write(buffer, 0, len);
		}
		byteArrayOutputStream.flush();
	}

	@Override
	public String toString() {
		if (StringUtils.isBlank(fileName)) {
			return super.toString();
		} else {
			return fileName;
		}
	}

}
