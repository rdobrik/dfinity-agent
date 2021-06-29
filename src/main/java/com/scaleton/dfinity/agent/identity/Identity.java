package com.scaleton.dfinity.agent.identity;

import com.scaleton.dfinity.types.Principal;

public interface Identity {
	public  Principal sender();
	public  Signature sign(byte[] blob);

}
