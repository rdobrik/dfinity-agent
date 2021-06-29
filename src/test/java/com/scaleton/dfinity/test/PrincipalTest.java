package com.scaleton.dfinity.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.scaleton.dfinity.types.Principal;
import com.scaleton.dfinity.types.PrincipalError;

public class PrincipalTest {
	static final Logger LOG = LoggerFactory.getLogger(PrincipalTest.class);



	@Test
	public void test() {

		Principal principal;

		try {
			principal = Principal.fromString("aaaaa-aa");

			Assertions.assertEquals(principal.toString(), "aaaaa-aa");

			principal = Principal.fromString(TestProperties.CANISTER_ID);

			Assertions.assertEquals(principal.toString(), TestProperties.CANISTER_ID);

			ObjectMapper objectMapper = new ObjectMapper(new CBORFactory());
			objectMapper.registerModule(new Jdk8Module());

		} catch (PrincipalError e) {
			LOG.error(e.getLocalizedMessage(), e);
			Assertions.fail(e.getMessage());
		}

		try {
			principal = Principal.fromString("RRkah-fqaaa-aaaaa-aaaaq-cai");

		} catch (PrincipalError e) {
			Assertions.assertEquals(e.getCode(), PrincipalError.PrincipalErrorCode.ABNORMAL_TEXTUAL_FORMAT);
		}

		try {
			principal = Principal.fromString("rr");

		} catch (PrincipalError e) {
			Assertions.assertEquals(e.getCode(), PrincipalError.PrincipalErrorCode.TEXT_TOO_SMALL);
		}

	}

}
