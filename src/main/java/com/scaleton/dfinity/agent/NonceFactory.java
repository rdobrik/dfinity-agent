package com.scaleton.dfinity.agent;

import java.security.SecureRandom;

public final class NonceFactory {
	
	public byte[] generate()
	{
		byte[] nonce = new byte[16];
		
		new SecureRandom().nextBytes(nonce);
		
		return nonce;
	}

}
