package com.scaleton.dfinity.types;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public final class PrincipalDeserializer extends StdDeserializer<Principal> {

	protected PrincipalDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Principal deserialize(JsonParser parser, DeserializationContext context)
			throws IOException, JsonProcessingException {
		JsonNode node = parser.getCodec().readTree(parser);
		if(node.isBinary())
		{	
			byte[] bytes = node.binaryValue();
			return Principal.from(bytes);
		}
		else if(node.isTextual())
		{
			return Principal.fromString(node.asText());
		}
		else
		 throw PrincipalError.create(PrincipalError.PrincipalErrorCode.EXTERNAL_ERROR);
	}

}
