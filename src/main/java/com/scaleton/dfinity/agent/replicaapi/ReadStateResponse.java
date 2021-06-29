package com.scaleton.dfinity.agent.replicaapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class ReadStateResponse {
	@JsonProperty("certificate")
	public byte[] certificate;
}
