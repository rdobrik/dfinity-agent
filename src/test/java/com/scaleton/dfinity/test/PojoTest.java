package com.scaleton.dfinity.test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.transform.TransformerException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.scaleton.dfinity.agent.ByteUtils;
import com.scaleton.dfinity.candid.dom.DOMDeserializer;
import com.scaleton.dfinity.candid.dom.DOMSerializer;
import com.scaleton.dfinity.candid.dom.DOMUtils;
import com.scaleton.dfinity.candid.jackson.JacksonDeserializer;
import com.scaleton.dfinity.candid.jackson.JacksonSerializer;
import com.scaleton.dfinity.candid.parser.IDLArgs;
import com.scaleton.dfinity.candid.parser.IDLType;
import com.scaleton.dfinity.candid.parser.IDLValue;
import com.scaleton.dfinity.candid.pojo.PojoDeserializer;
import com.scaleton.dfinity.candid.pojo.PojoSerializer;

public final class PojoTest extends CandidAssert{
	
	static
	{
		LOG = LoggerFactory.getLogger(CandidTest.class);
	}
	
	@Test
	public void test() {
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
		
		ArrayNode arrayNode = IDLArgs.fromBytes(buf).getArgs().get(0).getValue(JacksonDeserializer.create(idlValue.getIDLType()), ArrayNode.class);
		
		JsonNode jsonNode = IDLArgs.fromBytes(buf).getArgs().get(0).getValue(JacksonDeserializer.create(idlValue.getIDLType()), JsonNode.class);
		
		idlValue = IDLValue.create(jsonNode,JacksonSerializer.create(idlValue.getIDLType()));
		args = new ArrayList<IDLValue>();
		args.add(idlValue);

		idlArgs = IDLArgs.create(args);
		
		buf = idlArgs.toBytes();
		
		JsonNode jsonNodeResult = IDLArgs.fromBytes(buf).getArgs().get(0).getValue(JacksonDeserializer.create(idlValue.getIDLType()), JsonNode.class);
			
		Assertions.assertEquals(jsonNode, jsonNodeResult);
		
		DOMDeserializer domDeserializer = DOMDeserializer.create(idlValue.getIDLType()).rootElement("http://scaleton.com/dfinity/candid","root");
		//domDeserializer = domDeserializer.setAttributes(true);
		
		Node domNode = IDLArgs.fromBytes(buf).getArgs().get(0).getValue(domDeserializer, Node.class);
		
		try {
			String domString = DOMUtils.getStringFromDocument(domNode.getOwnerDocument());
		} catch (TransformerException e) {

		}
		
		idlValue = IDLValue.create(domNode,DOMSerializer.create());
		
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
		
		ComplexPojo complexPojoResult = IDLArgs.fromBytes(buf).getArgs().get(0).getValue(PojoDeserializer.create(), ComplexPojo.class);
		
		Assertions.assertEquals( complexPojoValue, complexPojoResult);
		
		jsonNode = IDLArgs.fromBytes(buf).getArgs().get(0).getValue(JacksonDeserializer.create(idlValue.getIDLType()), JsonNode.class);
		
		idlValue = IDLValue.create(jsonNode,JacksonSerializer.create(idlValue.getIDLType()));
		
		domDeserializer = DOMDeserializer.create(idlValue.getIDLType()).rootElement("http://scaleton.com/dfinity/candid","data");
		//domDeserializer = domDeserializer.setAttributes(true);
		
		domNode = IDLArgs.fromBytes(buf).getArgs().get(0).getValue(domDeserializer, Node.class);
		
		try {
			String domString = DOMUtils.getStringFromDocument(domNode.getOwnerDocument());
		} catch (TransformerException e) {

		}
		
		idlValue = IDLValue.create(domNode,DOMSerializer.create());
		
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
		
		ComplexArrayPojo complexPojoArrayResult = IDLArgs.fromBytes(buf).getArgs().get(0).getValue(new PojoDeserializer(), ComplexArrayPojo.class);
		
		Assertions.assertEquals( complexArrayPojoValue, complexPojoArrayResult);
	}
}
