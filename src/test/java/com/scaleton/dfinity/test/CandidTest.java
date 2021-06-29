package com.scaleton.dfinity.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scaleton.dfinity.candid.parser.IDLArgs;
import com.scaleton.dfinity.candid.parser.IDLValue;

public final class CandidTest {
	static final Logger LOG = LoggerFactory.getLogger(CandidTest.class);

	@Test
	public void test() {
		// test integer argument
		List<IDLValue> args = new ArrayList<IDLValue>();

		Integer intValue =new Integer(10000);
		
		args.add(IDLValue.create(intValue));			
		
		IDLArgs idlArgs = IDLArgs.create(args);

		byte[] buf = idlArgs.toBytes();
		
		IDLArgs outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(intValue,outArgs.getArgs().get(0).getValue());
		
		// test String argument
		
		args = new ArrayList<IDLValue>();
		
		String stringValue = "Hello";
		
		args.add(IDLValue.create(stringValue));			
		
		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();
		
		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(stringValue,outArgs.getArgs().get(0).getValue());
		
		// test Boolean argument
		
		args = new ArrayList<IDLValue>();
		
		Boolean boolValue = true;
		
		args.add(IDLValue.create(boolValue));			
		
		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();
		
		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(boolValue,outArgs.getArgs().get(0).getValue());

		// test Short argument
		
		args = new ArrayList<IDLValue>();
		
		Short shortValue = 64;
		
		args.add(IDLValue.create(shortValue));			
		
		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();
		
		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(shortValue,outArgs.getArgs().get(0).getValue());
		
		// test Long argument
		
		args = new ArrayList<IDLValue>();
		
		Long longValue = 64000000000L;
		
		args.add(IDLValue.create(longValue));			
		
		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();
		
		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(longValue,outArgs.getArgs().get(0).getValue());		
		
		// test Byte argument
		
		args = new ArrayList<IDLValue>();
		
		Byte byteValue = (byte)64;
		
		args.add(IDLValue.create(byteValue));			
		
		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();
		
		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(byteValue,outArgs.getArgs().get(0).getValue());		
		
		// test Double argument
		
		args = new ArrayList<IDLValue>();
		
		Double doubleValue = 42.42;
		
		args.add(IDLValue.create(doubleValue));			
		
		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();
		
		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(doubleValue,outArgs.getArgs().get(0).getValue());		

		// test Float argument
		
		args = new ArrayList<IDLValue>();
		
		Float floatValue = 42.42f;
		
		args.add(IDLValue.create(floatValue));			
		
		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();
		
		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(floatValue,outArgs.getArgs().get(0).getValue());		
		
		// test multiple arguments
		
		args = new ArrayList<IDLValue>();
		
		args.add(IDLValue.create(intValue));
		args.add(IDLValue.create(stringValue));	
		args.add(IDLValue.create(boolValue));
		args.add(IDLValue.create(doubleValue));	
		args.add(IDLValue.create(floatValue));
		
		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();
		
		outArgs = IDLArgs.fromBytes(buf);
		
		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(intValue,outArgs.getArgs().get(0).getValue());

		LOG.info(outArgs.getArgs().get(1).getValue().toString());
		Assertions.assertEquals(stringValue,outArgs.getArgs().get(1).getValue());

		LOG.info(outArgs.getArgs().get(2).getValue().toString());
		Assertions.assertEquals(boolValue,outArgs.getArgs().get(2).getValue());

		LOG.info(outArgs.getArgs().get(3).getValue().toString());
		Assertions.assertEquals(doubleValue,outArgs.getArgs().get(3).getValue());		

		LOG.info(outArgs.getArgs().get(4).getValue().toString());
		Assertions.assertEquals(floatValue,outArgs.getArgs().get(4).getValue());		
	}
}
