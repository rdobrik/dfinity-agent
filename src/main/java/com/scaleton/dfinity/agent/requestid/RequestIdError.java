package com.scaleton.dfinity.agent.requestid;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public final class RequestIdError extends Error {
	private static final long serialVersionUID = 1L;
	
	final static String RESOURCE_BUNDLE_FILE = "dfinity_requestid";
	static ResourceBundle properties;
	
	RequestIdErrorCode code;

	static {
		properties = ResourceBundle.getBundle(RESOURCE_BUNDLE_FILE);
	}
	
	public static RequestIdError create(RequestIdErrorCode code, Object... args) {

		String message = properties.getString(code.label);
		// set arguments
		message = MessageFormat.format(message, args);

		return new RequestIdError(code, message);
	}
	
	public static RequestIdError create(RequestIdErrorCode code,Throwable t, Object... args) {

		String message = properties.getString(code.label);
		// set arguments
		message = MessageFormat.format(message, args);

		return new RequestIdError(code,t, message);
	}	

	private RequestIdError(RequestIdErrorCode code, String message) {	
		super(message);
		this.code = code;
	}
	
	private RequestIdError(RequestIdErrorCode code, Throwable t, String message) {
		super(message, t);
		this.code = code;
	}
	
	public RequestIdErrorCode getCode() {
		return code;
	}
	
	public enum RequestIdErrorCode {
		CUSTOM_SERIALIZER_ERROR("CustomSerializerError"),
		EMPTY_SERIALIZER("EmptySerializer"),
		INVALID_STATE("InvalidState");
		
		public String label;

		RequestIdErrorCode(String label) {
			this.label = label;
		}
			
	}		
}
