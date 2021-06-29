package com.scaleton.dfinity.agent.replicaapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class CallReply {
	@JsonProperty("arg")
	public byte[] arg;
	
	public CallReply()
	{
		
	}
	
	public CallReply(byte[] arg)
	{
		this.arg = arg;
	}
}
