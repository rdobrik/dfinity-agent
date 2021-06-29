package com.scaleton.dfinity.agent.replicaapi;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public final class Envelope<T> {
	@JsonProperty("content")
	public T content;
	
	@JsonProperty("sender_pubkey")
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	public Optional<byte[]> senderPubkey;
	
	@JsonProperty("sender_sig")
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	public Optional<byte[]> senderSig;	

}
