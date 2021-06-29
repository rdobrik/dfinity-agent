package com.scaleton.dfinity.agent.identity;

import java.util.Optional;

public final class Signature {
	public Optional<byte[]> publicKey;
	public Optional<byte[]> signature;
	
	public Signature() {
		
	}
	
	public Signature(byte[] publicKey, byte[] signature)
	{
		this.publicKey = Optional.of(publicKey);
		this.signature = Optional.of(signature);
	}

}
