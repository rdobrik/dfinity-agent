package com.scaleton.dfinity.candid;

import java.math.BigInteger;

public class IDLUtils {
	public static int idlHash(String value)
	{
		BigInteger hash = BigInteger.ZERO;
		
		if(value != null)
		{
			byte[] bytes = value.getBytes();
			
			for(byte b : bytes)
				hash = hash.multiply(BigInteger.valueOf(223)).add(BigInteger.valueOf(b));
		}
		
		return hash.intValue();
	}

}
