package com.scaleton.dfinity.agent;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public final class AgentError extends Error {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final static String RESOURCE_BUNDLE_FILE = "dfinity_agent";
	static ResourceBundle properties;
	
	AgentErrorCode code;


	static {
		properties = ResourceBundle.getBundle(RESOURCE_BUNDLE_FILE);
	}

	public static AgentError create(AgentErrorCode code, Object... args) {

		String message = properties.getString(code.label);
		// set arguments
		message = MessageFormat.format(message, args);

		return new AgentError(code, message);
	}
	
	public static AgentError create(AgentErrorCode code,Throwable t, Object... args) {

		String message = properties.getString(code.label);
		// set arguments
		message = MessageFormat.format(message, args);

		return new AgentError(code,t, message);
	}	

	private AgentError(AgentErrorCode code, String message) {
		super(message);
	}
	
	private AgentError(AgentErrorCode code, Throwable t, String message) {
		super(message, t);
	}
	
	public AgentErrorCode getCode() {
		return code;
	}
	
	public enum AgentErrorCode {
		INVALID_REPLICA_URL("InvalidReplicaUrl"),
		TIMEOUT_WAITING_FOR_RESPONSE("TimeoutWaitingForResponse"),
		URL_SYNTAX_ERROR("UrlSyntaxError"),	
		URL_PARSE_ERROR("UrlParseError"),
		PRINCIPAL_ERROR("PrincipalError"),
		REPLICA_ERROR("ReplicaError"),
		INVALID_CBOR_DATA("InvalidCborData"),
		HTTP_ERROR("HttpError"),
		CANNOT_USE_AUTHENTICATION_ON_NONSECURE_URL("CannotUseAuthenticationOnNonSecureUrl"),
		AUTHENTICATION_ERROR("AuthenticationError"),
		INVALID_REPLICA_STATUS("InvalidReplicaStatus"),
		REQUEST_STATUS_DONE_NO_REPLY("RequestStatusDoneNoReply"),
		MESSAGE_ERROR("MessageError"),
		CUSTOM_ERROR("CustomError"),
		LEB128_READ_ERROR("Leb128ReadError"),
		UTF8_READ_ERROR("Utf8ReadError"),
		LOOKUP_PATH_ABSENT("LookupPathAbsent"),
		LOOKUP_PATH_UNKNOWN("LookupPathUnknown"),
		LOOKUP_PATH_ERROR("LookupPathError"),
		INVALID_REQUEST_STATUS("InvalidRequestStatus"),
		CERTIFICATE_VERIFICATION_FAILED("CertificateVerificationFailed"),
		NO_ROOT_KEY_IN_STATUS("NoRootKeyInStatus"),
		COULD_NOT_READ_ROOT_KEY("CouldNotReadRootKey"),
		TRANSPORT_ERROR("TransportError");
		
		public String label;

		AgentErrorCode(String label) {
			this.label = label;
		}
			
	}	

}
