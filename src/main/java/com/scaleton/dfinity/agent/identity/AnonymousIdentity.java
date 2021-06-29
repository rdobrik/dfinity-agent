package com.scaleton.dfinity.agent.identity;

import com.scaleton.dfinity.types.Principal;

public final class AnonymousIdentity implements Identity {

	
	@Override
	public Principal sender() {
		return Principal.anonymous();
	}

	@Override
	public Signature sign(byte[] msg) {
		return new Signature();
	}

}
