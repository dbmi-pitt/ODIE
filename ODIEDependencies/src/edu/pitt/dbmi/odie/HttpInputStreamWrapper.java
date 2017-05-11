package edu.pitt.dbmi.odie;

import java.io.InputStream;

public class HttpInputStreamWrapper {
	int statusCode;
	InputStream inputStream;
	public HttpInputStreamWrapper(int statusCode, InputStream is) {
		super();
		this.statusCode = statusCode;
		this.inputStream = is;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	
}