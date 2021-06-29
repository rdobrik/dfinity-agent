package com.scaleton.dfinity.agent.replicaapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.scaleton.dfinity.agent.Serialize;
import com.scaleton.dfinity.agent.Serializer;
import com.scaleton.dfinity.types.Principal;

public final class ReadStateContent implements Serialize{

	@JsonUnwrapped
	public ReadStateRequest readStateRequest = new ReadStateRequest();
	
	@JsonAppend(
		    prepend = true,
		    attrs = {
		            @JsonAppend.Attr( value = "request_type")
		    })
	public final class ReadStateRequest
	{
		@JsonProperty("ingress_expiry")
		public Long ingressExpiry;		
		@JsonProperty("sender")
		public Principal sender;		
		@JsonProperty("paths")
		public List<List<byte[]>> paths;
	}

	@Override
	public void serialize(Serializer serializer) {
		serializer.serializeField("request_type", "read_state");
		serializer.serializeField("ingress_expiry", readStateRequest.ingressExpiry);
		serializer.serializeField("sender",readStateRequest.sender);
		if(readStateRequest.paths != null && !readStateRequest.paths.isEmpty())
			serializer.serializeField("paths",readStateRequest.paths);		
	}
}
