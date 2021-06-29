package com.scaleton.dfinity.agent.hashtree;


public final class LookupResult {
	public LookupResultStatus status;
	
	public byte[] value;
	
	LookupResult(LookupResultStatus status)
	{		
		this.status = status;
	}
	
	LookupResult(LookupResultStatus status, byte[] value)
	{		
		this.status = status;
		this.value = value;
	}	
	
	public enum LookupResultStatus{
		// The value is guaranteed to be absent in the original state tree.
		ABSENT,
		// This partial view does not include information about this path, and the original
	    // tree may or may note include this value.
		UNKNOWN,
		// The value was found at the referenced node.
		FOUND,
		// The path does not make sense for this certificate.
		ERROR
		;
		
	}

}
