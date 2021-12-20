package com.scaleton.dfinity.test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scaleton.dfinity.agent.ByteUtils;
import com.scaleton.dfinity.candid.CandidError;
import com.scaleton.dfinity.candid.PojoDeserializer;
import com.scaleton.dfinity.candid.PojoSerializer;
import com.scaleton.dfinity.candid.parser.IDLArgs;
import com.scaleton.dfinity.candid.parser.IDLType;
import com.scaleton.dfinity.candid.parser.IDLValue;
import com.scaleton.dfinity.candid.types.Type;
import com.scaleton.dfinity.types.Principal;

public final class CandidTest {
	static final Logger LOG = LoggerFactory.getLogger(CandidTest.class);

	@Test
	public void test() {
		// testing record, move it after

		// fundamentaly wrong
		assertFail("", CandidError.class, "empty");
		assertFail("\\00\\00", CandidError.class, "no magic bytes");
		assertFail("DADL", CandidError.class, "wrong magic bytes");
		assertFail("DADL\\00\\00", CandidError.class, "wrong magic bytes");

		// Compared with Rust implementation, no exception thrown there. We need to
		// investigate correct handling
		// assertFail("DIDL\\80\\00\\00", CandidError.class, "overlong typ table length"
		// );
		// assertFail("DIDL\\00\\80\\00", CandidError.class, "overlong arg length" );

		// Null
		assertValue("DIDL\\00\\01\\7f", null);
		assertFail("DIDL\\00\\01\\7e", CandidError.class, "wrong type");
		assertFail("DIDL\\00\\01\\7f\\00", CandidError.class, "null: too long");

		// Let's see if we can match it with null value, otherwise it is nullary
		// assertFail("DIDL\\00\\00", CandidError.class, "null: extra null values" );

		// Boolean
		assertValue("DIDL\\00\\01\\7e\\00", new Boolean(false));
		assertValue("DIDL\\00\\01\\7e\\01", new Boolean(true));
		assertFail("DIDL\\00\\01\\7e", CandidError.class, "bool: missing");
		assertFail("DIDL\\00\\01\\02", CandidError.class, "bool: out of range");
		assertFail("DIDL\\00\\01\\ff", CandidError.class, "bool: out of range");

		// Unsigned Integer (Nat)
		assertValue("DIDL\\00\\01\\7d\\00", new BigInteger("0"), Type.NAT);
		assertValue("DIDL\\00\\01\\7d\\01", new BigInteger("1"), Type.NAT);
		assertValue("DIDL\\00\\01\\7d\\7f", new BigInteger("127"), Type.NAT);
		assertValue("DIDL\\00\\01\\7d\\80\\01", new BigInteger("128"), Type.NAT);
		assertValue("DIDL\\00\\01\\7d\\ff\\7f", new BigInteger("16383"), Type.NAT);
		assertFail("DIDL\\00\\01\\7d\\80", CandidError.class, "nat: leb too short");

		// TODO only for decoding, I assume
		// assertValue("DIDL\\00\\01\\7d\\80\\00",new BigInteger("0"), Type.NAT);
		// assertValue("DIDL\\00\\01\\7d\\ff\\00",new BigInteger("127"), Type.NAT );
		assertValue("DIDL\\00\\01\\7d\\80\\80\\98\\f4\\e9\\b5\\ca\\6a", new BigInteger("60000000000000000"), Type.NAT);

		// Integer
		assertValue("DIDL\\00\\01\\7c\\00", new BigInteger("0"));
		assertValue("DIDL\\00\\01\\7c\\01", new BigInteger("1"));
		assertValue("DIDL\\00\\01\\7c\\7f", new BigInteger("-1"));
		assertValue("DIDL\\00\\01\\7c\\40", new BigInteger("-64"));
		assertValue("DIDL\\00\\01\\7c\\80\\01", new BigInteger("128"));
		assertFail("DIDL\\00\\01\\7c\\80", CandidError.class, "int: leb too short");
		assertValue("DIDL\\00\\01\\7c\\80\\80\\98\\f4\\e9\\b5\\ca\\ea\\00", new BigInteger("60000000000000000"));
		assertValue("DIDL\\00\\01\\7c\\80\\80\\e8\\8b\\96\\ca\\b5\\95\\7f", new BigInteger("-60000000000000000"));

		// Byte Unsigned Int 8 (nat8)
		assertValue("DIDL\\00\\01\\7b\\00", new Byte((byte) 0), Type.NAT8);
		assertValue("DIDL\\00\\01\\7b\\01", new Byte((byte) 1), Type.NAT8);
		assertValue("DIDL\\00\\01\\7b\\ff", new Byte((byte) 255), Type.NAT8);
		assertFail("DIDL\\00\\01\\7b", CandidError.class, "nat8: leb too short");

		// Short Unsigned Int 16 (nat16)
		assertValue("DIDL\\00\\01\\7a\\00\\00", new Short((short) 0), Type.NAT16);
		assertValue("DIDL\\00\\01\\7a\\01\\00", new Short((short) 1), Type.NAT16);
		assertValue("DIDL\\00\\01\\7a\\ff\\00", new Short((short) 255), Type.NAT16);
		assertValue("DIDL\\00\\01\\7a\\00\\01", new Short((short) 256), Type.NAT16);
		assertValue("DIDL\\00\\01\\7a\\ff\\ff", new Short((short) 65535), Type.NAT16);
		assertFail("DIDL\\00\\01\\7a", CandidError.class, "nat16: leb too short");
		assertFail("DIDL\\00\\01\\7a\\00", CandidError.class, "nat16: leb too short");

		// Integer Unsigned Int 32 (nat32)
		assertValue("DIDL\\00\\01\\79\\00\\00\\00\\00", new Integer(0), Type.NAT32);
		assertValue("DIDL\\00\\01\\79\\01\\00\\00\\00", new Integer(1), Type.NAT32);
		assertValue("DIDL\\00\\01\\79\\ff\\00\\00\\00", new Integer(255), Type.NAT32);
		assertValue("DIDL\\00\\01\\79\\00\\01\\00\\00", new Integer(256), Type.NAT32);
		assertValue("DIDL\\00\\01\\79\\ff\\ff\\00\\00", new Integer(65535), Type.NAT32);
		assertValue("DIDL\\00\\01\\79\\ff\\ff\\ff\\ff", Integer.parseUnsignedInt("4294967295"), Type.NAT32);
		assertFail("DIDL\\00\\01\\79", CandidError.class, "nat32: leb too short");
		assertFail("DIDL\\00\\01\\79\\00", CandidError.class, "nat32: leb too short");
		assertFail("DIDL\\00\\01\\79\\00\\00", CandidError.class, "nat32: leb too short");
		assertFail("DIDL\\00\\01\\79\\00\\00\\00", CandidError.class, "nat32: leb too short");

		// Long Unsigned Int 64 (nat64)
		assertValue("DIDL\\00\\01\\78\\00\\00\\00\\00\\00\\00\\00\\00", new Long(0), Type.NAT64);
		assertValue("DIDL\\00\\01\\78\\01\\00\\00\\00\\00\\00\\00\\00", new Long(1), Type.NAT64);
		assertValue("DIDL\\00\\01\\78\\ff\\00\\00\\00\\00\\00\\00\\00", new Long(255), Type.NAT64);
		assertValue("DIDL\\00\\01\\78\\00\\01\\00\\00\\00\\00\\00\\00", new Long(256), Type.NAT64);
		assertValue("DIDL\\00\\01\\78\\ff\\ff\\00\\00\\00\\00\\00\\00", new Long(65535), Type.NAT64);
		assertValue("DIDL\\00\\01\\78\\ff\\ff\\ff\\ff\\00\\00\\00\\00", new Long(4294967295l), Type.NAT64);
		assertValue("DIDL\\00\\01\\78\\ff\\ff\\ff\\ff\\ff\\ff\\ff\\ff", Long.parseUnsignedLong("18446744073709551615"),
				Type.NAT64);
		assertFail("DIDL\\00\\01\\78", CandidError.class, "nat64: leb too short");
		assertFail("DIDL\\00\\01\\78\\00", CandidError.class, "nat64: leb too short");
		assertFail("DIDL\\00\\01\\78\\00\\00", CandidError.class, "nat64: leb too short");
		assertFail("DIDL\\00\\01\\78\\00\\00\\00\\00", CandidError.class, "nat64: leb too short");
		assertFail("DIDL\\00\\01\\78\\00\\00\\00\\00\\00", CandidError.class, "nat64: leb too short");
		assertFail("DIDL\\00\\01\\78\\00\\00\\00\\00\\00\\00", CandidError.class, "nat64: leb too short");
		assertFail("DIDL\\00\\01\\78\\00\\00\\00\\00\\00\\00\\00", CandidError.class, "nat64: leb too short");

		// Byte (int8)
		assertValue("DIDL\\00\\01\\77\\00", new Byte((byte) 0));
		assertValue("DIDL\\00\\01\\77\\01", new Byte((byte) 1));
		assertValue("DIDL\\00\\01\\77\\ff", new Byte((byte) -1));
		assertFail("DIDL\\00\\01\\77", CandidError.class, "int8: leb too short");

		// Short (int16)
		assertValue("DIDL\\00\\01\\76\\00\\00", new Short((short) 0));
		assertValue("DIDL\\00\\01\\76\\01\\00", new Short((short) 1));
		assertValue("DIDL\\00\\01\\76\\ff\\00", new Short((short) 255));
		assertValue("DIDL\\00\\01\\76\\00\\01", new Short((short) 256));
		assertValue("DIDL\\00\\01\\76\\ff\\ff", new Short((short) -1));
		assertFail("DIDL\\00\\01\\76", CandidError.class, "int16: leb too short");
		assertFail("DIDL\\00\\01\\76\\00", CandidError.class, "int16: leb too short");

		// Integer (int32)
		assertValue("DIDL\\00\\01\\75\\00\\00\\00\\00", new Integer(0));
		assertValue("DIDL\\00\\01\\75\\01\\00\\00\\00", new Integer(1));
		assertValue("DIDL\\00\\01\\75\\ff\\00\\00\\00", new Integer(255));
		assertValue("DIDL\\00\\01\\75\\00\\01\\00\\00", new Integer(256));
		assertValue("DIDL\\00\\01\\75\\ff\\ff\\00\\00", new Integer(65535));
		assertValue("DIDL\\00\\01\\75\\ff\\ff\\ff\\ff", new Integer(-1));
		assertFail("DIDL\\00\\01\\75", CandidError.class, "int32: leb too short");
		assertFail("DIDL\\00\\01\\75\\00", CandidError.class, "int32: leb too short");
		assertFail("DIDL\\00\\01\\75\\00\\00", CandidError.class, "int32: leb too short");
		assertFail("DIDL\\00\\01\\75\\00\\00\\00", CandidError.class, "int32: leb too short");

		// Long (int64)
		assertValue("DIDL\\00\\01\\74\\00\\00\\00\\00\\00\\00\\00\\00", new Long(0));
		assertValue("DIDL\\00\\01\\74\\01\\00\\00\\00\\00\\00\\00\\00", new Long(1));
		assertValue("DIDL\\00\\01\\74\\ff\\00\\00\\00\\00\\00\\00\\00", new Long(255));
		assertValue("DIDL\\00\\01\\74\\00\\01\\00\\00\\00\\00\\00\\00", new Long(256));
		assertValue("DIDL\\00\\01\\74\\ff\\ff\\00\\00\\00\\00\\00\\00", new Long(65535));
		assertValue("DIDL\\00\\01\\74\\ff\\ff\\ff\\ff\\00\\00\\00\\00", new Long(4294967295l));
		assertValue("DIDL\\00\\01\\74\\ff\\ff\\ff\\ff\\ff\\ff\\ff\\ff", new Long(-1));
		assertFail("DIDL\\00\\01\\74", CandidError.class, "int64: leb too short");
		assertFail("DIDL\\00\\01\\74\\00", CandidError.class, "int64: leb too short");
		assertFail("DIDL\\00\\01\\74\\00\\00", CandidError.class, "int64: leb too short");
		assertFail("DIDL\\00\\01\\74\\00\\00\\00\\00", CandidError.class, "int64: leb too short");
		assertFail("DIDL\\00\\01\\74\\00\\00\\00\\00\\00", CandidError.class, "int64: leb too short");
		assertFail("DIDL\\00\\01\\74\\00\\00\\00\\00\\00\\00", CandidError.class, "int64: leb too short");
		assertFail("DIDL\\00\\01\\74\\00\\00\\00\\00\\00\\00\\00", CandidError.class, "int64: leb too short");

		// Float (float32)
		assertValue("DIDL\\00\\01\\73\\00\\00\\00\\00", new Float(0.0));
		assertValue("DIDL\\00\\01\\73\\00\\00\\40\\40", new Float(3.0));
		assertValue("DIDL\\00\\01\\73\\00\\00\\00\\3f", new Float(0.5));
		assertValue("DIDL\\00\\01\\73\\00\\00\\00\\bf", new Float(-0.5));
		assertFail("DIDL\\00\\01\\73\\00\\00", CandidError.class, "float32: too short");

		// Double (float464)
		assertValue("DIDL\\00\\01\\72\\00\\00\\00\\00\\00\\00\\00\\00", new Double(0.0));
		assertValue("DIDL\\00\\01\\72\\00\\00\\00\\00\\00\\00\\08\\40", new Double(3.0));
		assertValue("DIDL\\00\\01\\72\\00\\00\\00\\00\\00\\00\\e0\\3f", new Double(0.5));
		assertValue("DIDL\\00\\01\\72\\00\\00\\00\\00\\00\\00\\e0\\bf", new Double(-0.5));
		assertFail("DIDL\\00\\01\\72\\00\\00\\00\\00", CandidError.class, "float64: too short");

		// String (text)
		assertValue("DIDL\\00\\01\\71\\00", new String(""));
		assertValue("DIDL\\00\\01\\71\\06", "Motoko", new String("Motoko"));
		assertValue("DIDL\\00\\01\\71\\03\\e2\\98\\83", new String("☃"));

		// Principal
		assertValue("DIDL\\00\\01\\68\\01\\00", Principal.fromString("aaaaa-aa"));
		assertValue("DIDL\\00\\01\\68\\01\\03\\ca\\ff\\ee", Principal.fromString("w7x7r-cok77-xa"));
		assertValue("DIDL\\00\\01\\68\\01\\09\\ef\\cd\\ab\\00\\00\\00\\00\\00\\01",
				Principal.fromString("2chl6-4hpzw-vqaaa-aaaaa-c"));
//		assertValue("DIDL\\00\\01\\68\\01\\02\\ca\\ff",Principal.fromString("w7x7r-cok77-xa"));	
		assertFail("DIDL\\00\\01\\68\\03\\ca\\ff\\ee", CandidError.class, "principal: no tag");
		assertFail("DIDL\\00\\01\\68\\01\\03\\ca\\ff", CandidError.class, "principal: too short");
		assertFail("DIDL\\00\\01\\68\\01\\03\\ca\\ff\\ee\\ee", CandidError.class, "principal: too long");
		assertFail("DIDL\\01\\68\\01\\00\\01\\03\\ca\\ff\\ee", CandidError.class, "principal: not construct");

		// Opt
		assertValue("DIDL\\00\\01\\7f", null);

		// TODO fix this, this is tricky, because we cannot identify type of empty inner
		// class
		// assertValue("DIDL\\01\\6e\\6f\\01\\00\\00",Optional.empty());
		assertValue("DIDL\\01\\6e\\7e\\01\\00\\01\\00", Optional.of(new Boolean(false)));
		assertValue("DIDL\\01\\6e\\7e\\01\\00\\01\\01", Optional.of(new Boolean(true)));
		assertFail("DIDL\\01\\6e\\7e\\01\\00\\01\\02", CandidError.class, "opt: parsing invalid bool at opt bool");
		// assertValue("DIDL\\01\\6e\\7e\\01\\00\\00",Optional.empty());

		// Record
		Map<String, Object> mapValue = new HashMap<String, Object>();

		assertValue("DIDL\\01\\6c\\00\\01\\00", mapValue);

		mapValue.put("bar", new Boolean(true));

		mapValue.put("foo", BigInteger.valueOf(42));

		assertValue("DIDL\\01\\6c\\02\\d3\\e3\\aa\\02\\7e\\86\\8e\\b7\\02\\7c\\01\\00\\01\\2a", mapValue);

		Map<Integer, Object> intMapValue = new HashMap<Integer, Object>();

		intMapValue.put(new Integer(1), BigInteger.valueOf(42));

		assertValue("DIDL\\01\\6c\\01\\01\\7c\\01\\00\\2a", intMapValue);
		
		// Record POJO
		
		Pojo pojoValue = new Pojo();
		
		pojoValue.bar = new Boolean(true);
		pojoValue.foo = BigInteger.valueOf(42);
		
		IDLValue idlValue = IDLValue.create(pojoValue, new PojoSerializer());
		
		List<IDLValue> args = new ArrayList<IDLValue>();
		args.add(idlValue);

		IDLArgs idlArgs = IDLArgs.create(args);
		
		byte[] buf = idlArgs.toBytes();
		
		assertBytes("DIDL\\01\\6c\\02\\d3\\e3\\aa\\02\\7e\\86\\8e\\b7\\02\\7c\\01\\00\\01\\2a", buf);
		
		IDLArgs outArgs = IDLArgs.fromBytes(buf);
		
		Pojo pojoResult = IDLArgs.fromBytes(buf).getArgs().get(0).getValue(new PojoDeserializer(), Pojo.class);
		
		Assertions.assertEquals( pojoValue, pojoResult);
		// Pojo OPT
		Optional<Pojo> optionalPojoValue = Optional.of(pojoValue);
		idlValue = IDLValue.create(optionalPojoValue, new PojoSerializer());
		
		args = new ArrayList<IDLValue>();
		args.add(idlValue);

		idlArgs = IDLArgs.create(args);
		
		buf = idlArgs.toBytes();		
		
		Pojo optionalPojoResult = IDLArgs.fromBytes(buf).getArgs().get(0).getValue(new PojoDeserializer(),Pojo.class);

		Assertions.assertEquals( pojoValue, optionalPojoResult);
		
		// Pojo Array VEC
		
		Pojo pojoValue2 = new Pojo();
		
		pojoValue2.bar = new Boolean(false);
		pojoValue2.foo = BigInteger.valueOf(43); 
		
		Pojo[] pojoArray = {pojoValue, pojoValue2};
		
		idlValue = IDLValue.create(pojoArray, new PojoSerializer());
		
		args = new ArrayList<IDLValue>();
		args.add(idlValue);

		idlArgs = IDLArgs.create(args);
		
		buf = idlArgs.toBytes();	
		
		Pojo[] pojoArrayResult = IDLArgs.fromBytes(buf).getArgs().get(0).getValue(new PojoDeserializer(), Pojo[].class);

		Assertions.assertArrayEquals(pojoArray, pojoArrayResult);
		// Complex RECORD Pojo
		
		ComplexPojo complexPojoValue = new ComplexPojo();
		complexPojoValue.bar = new Boolean(true);
		complexPojoValue.foo = BigInteger.valueOf(42);	
		
		complexPojoValue.pojo = pojoValue2;

		idlValue = IDLValue.create(complexPojoValue, new PojoSerializer());
		
		args = new ArrayList<IDLValue>();
		args.add(idlValue);

		idlArgs = IDLArgs.create(args);
		
		buf = idlArgs.toBytes();
		
		int[] unsignedBuf =ByteUtils.toUnsignedIntegerArray(buf);
		
		IDLType[] idlTypes = { idlValue.getIDLType() };
		
		outArgs = IDLArgs.fromBytes(buf, idlTypes);
		
		ComplexPojo complexPojoResult = IDLArgs.fromBytes(buf).getArgs().get(0).getValue(new PojoDeserializer(), ComplexPojo.class);
		
		Assertions.assertEquals( complexPojoValue, complexPojoResult);
		
		// Complex Array RECORD Pojo
		
		ComplexArrayPojo complexArrayPojoValue = new ComplexArrayPojo();
		
		Boolean[] barArray = { new Boolean(true),new Boolean(false)};
		complexArrayPojoValue.bar = barArray;
		
		BigInteger[] fooArray = { new BigInteger("100000000"), new BigInteger("200000000"),
				new BigInteger("300000000") };
		complexArrayPojoValue.foo = fooArray;	
		
		
		Pojo[] pojoArray2 = {pojoValue, pojoValue2};
		
		complexArrayPojoValue.pojo = pojoArray2;

		idlValue = IDLValue.create(complexArrayPojoValue, new PojoSerializer());
		
		args = new ArrayList<IDLValue>();
		args.add(idlValue);

		idlArgs = IDLArgs.create(args);
		
		buf = idlArgs.toBytes();
		
///		ComplexArrayPojo complexPojoArrayResult = IDLArgs.fromBytes(buf).getArgs().get(0).getValue(new PojoDeserializer(), ComplexArrayPojo.class);

		
//		IDLType[] complexIdlTypes = { idlValue.getIDLType() };
		
//		outArgs = IDLArgs.fromBytes(buf, complexIdlTypes);

		// Variant

		Map<Integer, Object> variantValue = new HashMap<Integer, Object>();

		variantValue.put(new Integer(3303859), "value");

		idlValue = IDLValue.create(variantValue, Type.VARIANT);

		args = new ArrayList<IDLValue>();

		args.add(idlValue);

		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();

		IDLType[] types = { idlValue.getIDLType() };

		outArgs = IDLArgs.fromBytes(buf, types);

		Assertions.assertEquals(variantValue, outArgs.getArgs().get(0).getValue());

		// test big integer argument
		args = new ArrayList<IDLValue>();

		BigInteger bigintValue = new BigInteger("1234567890");

		args.add(IDLValue.create(bigintValue));

		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();

		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(bigintValue, outArgs.getArgs().get(0).getValue());

		// test integer argument
		args = new ArrayList<IDLValue>();

		Integer intValue = new Integer(10000);

		args.add(IDLValue.create(intValue));

		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();

		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(intValue, outArgs.getArgs().get(0).getValue());

		// test Principal argument

		Principal principal = Principal.fromString("rrkah-fqaaa-aaaaa-aaaaq-cai");

		args = new ArrayList<IDLValue>();

		args.add(IDLValue.create(principal));

		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();

		outArgs = IDLArgs.fromBytes(buf);

		Principal principalResult = (Principal) outArgs.getArgs().get(0).getValue();

		LOG.info(principalResult.toString());

		Assertions.assertEquals(principal.toString(), principalResult.toString());

		// test Array argument Integer
		args = new ArrayList<IDLValue>();

		Integer[] array = { 10000, 20000, 30000 };

		args.add(IDLValue.create(array));

		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();

		outArgs = IDLArgs.fromBytes(buf);

		Assertions.assertArrayEquals(array, outArgs.getArgs().get(0).getValue());

		// test Array argument BigInteger
		args = new ArrayList<IDLValue>();

		BigInteger[] bigarray = { new BigInteger("100000000"), new BigInteger("200000000"),
				new BigInteger("300000000") };

		args.add(IDLValue.create(bigarray));

		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();

		outArgs = IDLArgs.fromBytes(buf);

		Assertions.assertArrayEquals(bigarray, outArgs.getArgs().get(0).getValue());

		// test Optional argument
		args = new ArrayList<IDLValue>();

		args.add(IDLValue.create(Optional.of(intValue)));

		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();

		outArgs = IDLArgs.fromBytes(buf);

		Optional optionalResult = (Optional) outArgs.getArgs().get(0).getValue();

		LOG.info(optionalResult.get().toString());
		Assertions.assertEquals(Optional.of(intValue), optionalResult);

		// test String argument

		args = new ArrayList<IDLValue>();

		String stringValue = "Hello";

		args.add(IDLValue.create(stringValue));

		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();

		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(stringValue, outArgs.getArgs().get(0).getValue());

		// test Boolean argument

		args = new ArrayList<IDLValue>();

		Boolean boolValue = true;

		args.add(IDLValue.create(boolValue));

		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();

		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(boolValue, outArgs.getArgs().get(0).getValue());

		// test Short argument

		args = new ArrayList<IDLValue>();

		Short shortValue = 64;

		args.add(IDLValue.create(shortValue));

		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();

		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(shortValue, outArgs.getArgs().get(0).getValue());

		// test Long argument

		args = new ArrayList<IDLValue>();

		Long longValue = 64000000000L;

		args.add(IDLValue.create(longValue));

		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();

		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(longValue, outArgs.getArgs().get(0).getValue());

		// test Byte argument

		args = new ArrayList<IDLValue>();

		Byte byteValue = (byte) 64;

		args.add(IDLValue.create(byteValue));

		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();

		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(byteValue, outArgs.getArgs().get(0).getValue());

		// test Double argument

		args = new ArrayList<IDLValue>();

		Double doubleValue = 42.42;

		args.add(IDLValue.create(doubleValue));

		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();

		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(doubleValue, outArgs.getArgs().get(0).getValue());

		// test Float argument

		args = new ArrayList<IDLValue>();

		Float floatValue = 42.42f;

		args.add(IDLValue.create(floatValue));

		idlArgs = IDLArgs.create(args);

		buf = idlArgs.toBytes();

		outArgs = IDLArgs.fromBytes(buf);

		LOG.info(outArgs.getArgs().get(0).getValue().toString());
		Assertions.assertEquals(floatValue, outArgs.getArgs().get(0).getValue());

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
		Assertions.assertEquals(intValue, outArgs.getArgs().get(0).getValue());

		LOG.info(outArgs.getArgs().get(1).getValue().toString());
		Assertions.assertEquals(stringValue, outArgs.getArgs().get(1).getValue());

		LOG.info(outArgs.getArgs().get(2).getValue().toString());
		Assertions.assertEquals(boolValue, outArgs.getArgs().get(2).getValue());

		LOG.info(outArgs.getArgs().get(3).getValue().toString());
		Assertions.assertEquals(doubleValue, outArgs.getArgs().get(3).getValue());

		LOG.info(outArgs.getArgs().get(4).getValue().toString());
		Assertions.assertEquals(floatValue, outArgs.getArgs().get(4).getValue());
	}

	static byte[] getBytes(String input) throws DecoderException {
		if (input == null)
			throw new Error("Invalid input value");

		if (input.isEmpty())
			return input.getBytes();

		int i = input.indexOf('\\');

		if (i < 0)
			return input.getBytes();

		String prefix = input.substring(0, i);

		String data = input.substring(input.indexOf('\\')).replace("\\", "");

		return ArrayUtils.addAll(prefix.getBytes(), Hex.decodeHex(data));
	}

	static byte[] getBytes(String input, String value) throws DecoderException {
		if (value == null)
			return getBytes(input);
		else
			return ArrayUtils.addAll(getBytes(input), value.getBytes());
	}
	
	static void assertBytes(String input, byte[] value) {
		try {
			byte[] bytes = getBytes(input);
			
			Assertions.assertArrayEquals(bytes,value);

		} catch (DecoderException e) {
			LOG.error(e.getLocalizedMessage(), e);
			Assertions.fail(e.getMessage());
		}
	}	

	static void assertValue(String input, Object value) {
		IDLValue idlValue = IDLValue.create(value);

		try {
			byte[] bytes = getBytes(input);
			assertValue(bytes, input, value, idlValue);

		} catch (DecoderException e) {
			LOG.error(e.getLocalizedMessage(), e);
			Assertions.fail(e.getMessage());
		}
	}

	static void assertValue(String input, Object value, Type type) {
		IDLValue idlValue = IDLValue.create(value, type);

		try {

			byte[] bytes = getBytes(input);

			assertValue(bytes, input, value, idlValue);

		} catch (DecoderException e) {
			LOG.error(e.getLocalizedMessage(), e);
			Assertions.fail(e.getMessage());
		}

	}

	static void assertValue(String input, String stringValue, Object value) {
		IDLValue idlValue = IDLValue.create(value);

		try {
			byte[] bytes = getBytes(input, stringValue);
			assertValue(bytes, input, value, idlValue);

		} catch (DecoderException e) {
			LOG.error(e.getLocalizedMessage(), e);
			Assertions.fail(e.getMessage());
		}
	}

	static void assertValue(String input, String stringValue, Object value, Type type) {
		IDLValue idlValue = IDLValue.create(value, type);

		try {

			byte[] bytes = getBytes(input, stringValue);

			assertValue(bytes, input, value, idlValue);

		} catch (DecoderException e) {
			LOG.error(e.getLocalizedMessage(), e);
			Assertions.fail(e.getMessage());
		}

	}

	static void assertValue(byte[] bytes, String input, Object value, IDLValue idlValue) {
		List<IDLValue> args = new ArrayList<IDLValue>();

		args.add(idlValue);

		IDLArgs idlArgs = IDLArgs.create(args);

		byte[] buf = idlArgs.toBytes();

		if (value != null)
			LOG.info(value.toString() + ":" + input);
		else
			LOG.info("null" + ":" + input);

		Assertions.assertArrayEquals(buf, bytes);

		IDLArgs outArgs;

		if (idlValue.getType() == Type.RECORD || idlValue.getType() == Type.VARIANT) {
			IDLType[] idlTypes = { idlValue.getIDLType() };

			outArgs = IDLArgs.fromBytes(bytes, idlTypes);
		} else
			outArgs = IDLArgs.fromBytes(bytes);

		if (value != null)
			LOG.info(input + ":" + value.toString());
		else
			LOG.info(input + ":" + "null");

		Assertions.assertEquals(value, outArgs.getArgs().get(0).getValue());

	}

	static void testEncode(String input, Object value) {
		try {
			testEncode(getBytes(input), value);
		} catch (DecoderException e) {
			LOG.error(e.getLocalizedMessage(), e);
			Assertions.fail(e.getMessage());
		}
	}

	static void testEncode(byte[] bytes, Object value) {
		IDLValue idlValue = IDLValue.create(value);

		testEncode(bytes, idlValue);
	}

	static void testEncode(byte[] bytes, Object value, Type type) {
		IDLValue idlValue = IDLValue.create(value, type);

		testEncode(bytes, idlValue);

	}

	static void testEncode(byte[] bytes, IDLValue idlValue) {
		List<IDLValue> args = new ArrayList<IDLValue>();

		args.add(idlValue);

		IDLArgs idlArgs = IDLArgs.create(args);

		byte[] buf = idlArgs.toBytes();

		Assertions.assertArrayEquals(buf, bytes);
	}

	static void testDecode(Object value, byte[] bytes) {
		IDLArgs outArgs = IDLArgs.fromBytes(bytes);

		Assertions.assertEquals(value, outArgs.getArgs().get(0).getValue());

	}

	static void assertFail(String input, Class exClass, String message) {
		try {
			byte[] bytes = getBytes(input);
			IDLArgs outArgs = IDLArgs.fromBytes(bytes);
			Assertions.fail(message);
		} catch (DecoderException e) {
			LOG.error(e.getLocalizedMessage(), e);
			Assertions.fail(e.getMessage());
		} catch (AssertionFailedError e) {
			throw e;
		} catch (Throwable t) {
			LOG.info(t.getLocalizedMessage());
			LOG.info(message);
			Assertions.assertTrue(t.getClass() == exClass);
		}
	}
}
