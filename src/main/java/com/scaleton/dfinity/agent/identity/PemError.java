package com.scaleton.dfinity.agent.identity;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public final class PemError extends Error {
	private static final long serialVersionUID = 1L;
	
	final static String RESOURCE_BUNDLE_FILE = "dfinity_pem";
	static ResourceBundle properties;
	
	PemErrorCode code;

	static {
		properties = ResourceBundle.getBundle(RESOURCE_BUNDLE_FILE);
	}
	
	public static PemError create(PemErrorCode code, Object... args) {

		String message = properties.getString(code.label);
		// set arguments
		message = MessageFormat.format(message, args);

		return new PemError(code, message);
	}
	
	public static PemError create(PemErrorCode code,Throwable t, Object... args) {

		String message = properties.getString(code.label);
		// set arguments
		message = MessageFormat.format(message, args);

		return new PemError(code,t, message);
	}	

	private PemError(PemErrorCode code, String message) {	
		super(message);
		this.code = code;
	}
	
	private PemError(PemErrorCode code, Throwable t, String message) {
		super(message, t);
		this.code = code;
	}
	
	public PemErrorCode getCode() {
		return code;
	}
	
	public enum PemErrorCode {
		PEM_ERROR("PemError"),
		KEY_REJECTED("KeyRejected"),
		ERROR_STACK("ErrorStack");
		
		public String label;

		PemErrorCode(String label) {
			this.label = label;
		}
			
	}		
}
