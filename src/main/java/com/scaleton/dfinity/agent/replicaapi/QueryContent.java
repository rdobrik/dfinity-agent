package com.scaleton.dfinity.agent.replicaapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.scaleton.dfinity.agent.Serialize;
import com.scaleton.dfinity.agent.Serializer;
import com.scaleton.dfinity.types.Principal;

public final class QueryContent implements Serialize{

	@JsonUnwrapped
	public QueryRequest queryRequest = new QueryRequest();
	
	@JsonAppend(
		    prepend = true,
		    attrs = {
		            @JsonAppend.Attr( value = "request_type")
		    })
	public final class QueryRequest
	{
		@JsonProperty("ingress_expiry")
		public Long ingressExpiry;		
		@JsonProperty("sender")
		public Principal sender;		
		@JsonProperty("canister_id")
		public Principal canisterId;		
		@JsonProperty("method_name")
		public String methodName;
		@JsonProperty("arg")
		public byte[] arg;
	}

	@Override
	public void serialize(Serializer serializer) {
		serializer.serializeField("request_type", "query");
		serializer.serializeField("ingress_expiry", queryRequest.ingressExpiry);
		serializer.serializeField("sender",queryRequest.sender);
		serializer.serializeField("canister_id",queryRequest.canisterId);
		serializer.serializeField("method_name",queryRequest.methodName);
		serializer.serializeField("arg",queryRequest.arg);		
	}
}
