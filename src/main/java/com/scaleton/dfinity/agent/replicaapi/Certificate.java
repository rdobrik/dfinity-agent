package com.scaleton.dfinity.agent.replicaapi;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scaleton.dfinity.agent.hashtree.HashTree;

public final class Certificate {
	
	@JsonProperty("tree")
	public HashTree tree;
	
	@JsonProperty("signature")
	public byte[] signature;
	
	@JsonProperty("delegation")
	public Optional delegation;
	
}
