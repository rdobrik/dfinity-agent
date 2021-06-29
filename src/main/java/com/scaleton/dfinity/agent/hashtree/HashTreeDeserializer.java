package com.scaleton.dfinity.agent.hashtree;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public final class HashTreeDeserializer extends JsonDeserializer<HashTree> {
	protected static final Logger LOG = LoggerFactory.getLogger( HashTreeDeserializer.class);

	@Override
	public HashTree deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
		ObjectCodec oc = parser.getCodec();
		JsonNode node = oc.readTree(parser);
		
		// TODO change later to debug
		LOG.info(node.toPrettyString());
		
		
		return new HashTree(HashTreeNode.deserialize(node));
	}

}
