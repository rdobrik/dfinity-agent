package com.scaleton.dfinity.candid;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public final class CandidError extends Error {
	private static final long serialVersionUID = 1L;
	
	final static String RESOURCE_BUNDLE_FILE = "dfinity_candid";
	static ResourceBundle properties;
	
	CandidErrorCode code;

	static {
		properties = ResourceBundle.getBundle(RESOURCE_BUNDLE_FILE);
	}
	
	public static CandidError create(CandidErrorCode code, Object... args) {

		String message = properties.getString(code.label);
		// set arguments
		message = MessageFormat.format(message, args);

		return new CandidError(code, message);
	}
	
	public static CandidError create(CandidErrorCode code,Throwable t, Object... args) {

		String message = properties.getString(code.label);
		// set arguments
		message = MessageFormat.format(message, args);

		return new CandidError(code,t, message);
	}	

	private CandidError(CandidErrorCode code, String message) {	
		super(message);
		this.code = code;
	}
	
	private CandidError(CandidErrorCode code, Throwable t, String message) {
		super(message, t);
		this.code = code;
	}
	
	public CandidErrorCode getCode() {
		return code;
	}
	
	public enum CandidErrorCode {
		PARSE("Parse"),
		BINREAD("Binread"),
		CUSTOM("Custom");
		
		public String label;

		CandidErrorCode(String label) {
			this.label = label;
		}
			
	}		
}
