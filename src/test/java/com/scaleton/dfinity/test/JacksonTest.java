package com.scaleton.dfinity.test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaleton.dfinity.candid.jackson.JacksonDeserializer;
import com.scaleton.dfinity.candid.jackson.JacksonSerializer;
import com.scaleton.dfinity.candid.parser.IDLArgs;
import com.scaleton.dfinity.candid.parser.IDLType;
import com.scaleton.dfinity.candid.parser.IDLValue;
import com.scaleton.dfinity.candid.types.Label;
import com.scaleton.dfinity.candid.types.Type;

public final class JacksonTest extends CandidAssert {
	static final String SIMPLE_NODE_FILE = "SimpleNode.json";
	static final String SIMPLE_ARRAY_NODE_FILE = "SimpleArrayNode.json";	
	
	ObjectMapper mapper = new ObjectMapper();

	static {
		LOG = LoggerFactory.getLogger(JacksonTest.class);
	}

	@Test
	public void test() {

		Map<Label, IDLType> typeMap = new TreeMap<Label, IDLType>();

		typeMap.put(Label.createNamedLabel("bar"), IDLType.createType(Type.BOOL));
		typeMap.put(Label.createNamedLabel("foo"), IDLType.createType(Type.INT));

		this.testJson(SIMPLE_NODE_FILE, IDLType.createType(Type.RECORD, typeMap));

		IDLType idlType = IDLType.createType(Type.VEC, IDLType.createType(Type.RECORD, typeMap));

		this.testJson(SIMPLE_ARRAY_NODE_FILE, idlType);
		
		JacksonPojo pojo = new JacksonPojo();
		
		pojo.bar = true;
		pojo.foo =BigInteger.valueOf(42);

		IDLValue idlValue = IDLValue.create(pojo, JacksonSerializer.create());
		List<IDLValue> args = new ArrayList<IDLValue>();
		args.add(idlValue);

		IDLArgs idlArgs = IDLArgs.create(args);

		byte[] buf = idlArgs.toBytes();

		JacksonPojo pojoResult = IDLArgs.fromBytes(buf).getArgs().get(0)
				.getValue(JacksonDeserializer.create(idlValue.getIDLType()), JacksonPojo.class);		
		
		Assertions.assertEquals(pojo, pojoResult);
	}

	void testJson(String fileName, IDLType idlType) {
		try {
			JsonNode jsonValue = readNode(fileName);

			IDLValue idlValue = IDLValue.create(jsonValue, JacksonSerializer.create(idlType));
			List<IDLValue> args = new ArrayList<IDLValue>();
			args.add(idlValue);

			IDLArgs idlArgs = IDLArgs.create(args);

			byte[] buf = idlArgs.toBytes();

			JsonNode jsonResult = IDLArgs.fromBytes(buf).getArgs().get(0)
					.getValue(JacksonDeserializer.create(idlValue.getIDLType()), JsonNode.class);

			JSONAssert.assertEquals(jsonValue.asText(), jsonResult.asText(), JSONCompareMode.LENIENT);

		} catch (IOException e) {
			LOG.error(e.getLocalizedMessage(), e);
			Assertions.fail(e.getMessage());
		} catch (JSONException e) {
			LOG.error(e.getLocalizedMessage(), e);
			Assertions.fail(e.getMessage());
		}
	}

	JsonNode readNode(String fileName) throws JsonProcessingException, IOException {
		byte[] input = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(fileName).getPath()));

		JsonNode rootNode = (JsonNode) mapper.readTree(input);

		return rootNode;
	}
}
