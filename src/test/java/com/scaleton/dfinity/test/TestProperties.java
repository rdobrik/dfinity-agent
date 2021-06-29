package com.scaleton.dfinity.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class TestProperties extends Properties{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected static final Logger LOG = LoggerFactory.getLogger(TestProperties.class);
	
	static String PROPERTIES_FILE_NAME = "test.properties";
	
	static String MOCK_PORT_PROPERTY = "mockPort";
	static String CANISTER_ID_PROPERTY = "canisterId";
	static String FORWARD_PROPERTY = "forward";
	static String FORWARD_HOST_PROPERTY = "forwardHost";
	static String FORWARD_PORT_PROPERTY = "forwardPort";
	static String STORE_PROPERTY = "store";
	static String STORE_PATH_PROPERTY = "storePath";


	protected static Integer MOCK_PORT = 8777;
	
	protected static Boolean FORWARD = false;
	
	protected static String FORWARD_HOST = "localhost";
	protected static Integer FORWARD_PORT = 8000;
	
	protected static Boolean STORE = false;
	protected static String STORE_PATH = "/tmp";	
	
	protected static String CANISTER_ID = "rrkah-fqaaa-aaaaa-aaaaq-cai";
	
	protected static String CBOR_STATUS_RESPONSE_FILE = "cbor.status.response";
	
	protected static String CBOR_ECHOBOOL_QUERY_RESPONSE_FILE = "cbor.echoBool.query.response";
	protected static String CBOR_ECHOINT_QUERY_RESPONSE_FILE = "cbor.echoInt.query.response";
	protected static String CBOR_ECHOFLOAT_QUERY_RESPONSE_FILE = "cbor.echoFloat.query.response";

	protected static String CBOR_PEEK_QUERY_RESPONSE_FILE = "cbor.peek.query.response";
	protected static String CBOR_HELLO_QUERY_RESPONSE_FILE = "cbor.hello.query.response";	
	
	protected static String CBOR_UPDATE_GREET_RESPONSE_FILE = "cbor.update.greet.response";
	

	
	protected static String ED25519_IDENTITY_FILE = "Ed25519_identity.pem";	
	protected static String SECP256K1_IDENTITY_FILE = "Secp256k1_identity.pem";	
	
	static
	{	 
		InputStream propInputStream = TestProperties.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);

		try {
			Properties props = new Properties();
			props.load(propInputStream);
			
			MOCK_PORT = Integer.valueOf(props.getProperty(MOCK_PORT_PROPERTY, "8777"));
			FORWARD = Boolean.valueOf(props.getProperty(FORWARD_PROPERTY, "false"));
			FORWARD_HOST = props.getProperty(FORWARD_HOST_PROPERTY, "localhost");
			FORWARD_PORT = Integer.valueOf(props.getProperty(FORWARD_PORT_PROPERTY, "8000"));
			STORE = Boolean.valueOf(props.getProperty(STORE_PROPERTY, "false"));
			STORE_PATH = props.getProperty(STORE_PATH_PROPERTY, "localhost");
			
		} catch (IOException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
	}
}