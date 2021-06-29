package com.scaleton.dfinity.candid.types;

import org.apache.commons.lang3.ArrayUtils;

import com.scaleton.dfinity.candid.Bytes;

public final class Numbers {
	
	public static byte[] encodeInt(Integer value)
	{
		
		byte[] result = ArrayUtils.EMPTY_BYTE_ARRAY;
		
		boolean done;
		
		do {
			Integer bigByte = value & Integer.valueOf(255);
			byte b = bigByte.byteValue();
			
			value >>= 6;	
			
			done = (Integer.valueOf(0).equals(value) || Integer.valueOf(-1).equals(value));
			
			if(done)
				b &= 0x7f;
			else
			{
				value >>= 1;
				b |= 0x80;
			}
			
			result = ArrayUtils.add(result,b);
			
		}
		while(!done);
		
		return result;
	}
	
	public static Integer decodeInt(Bytes buf)
	{
		Integer result = Integer.valueOf(0);
		
		int shift = 0;
		
		byte b;
		
		do
		{
			b = buf.parseByte();
			
			Integer lowBits = Integer.valueOf(b & 0x7f);
			
			result |= lowBits << shift;
			
			shift += 7;
			
		}while((b & 0x80) != 0);
		
		
		if((0x40 & b) == 0x40)
			result |= Integer.valueOf(-1) << shift;
		
		return result;
	}
	
	public static byte[] encodeLong(Long value)
	{
		
		byte[] result = ArrayUtils.EMPTY_BYTE_ARRAY;
		
		boolean done;
		
		do {
			Long bigByte = value & Long.valueOf(255);
			byte b = bigByte.byteValue();
			
			value >>= 6;	
			
			done = (Long.valueOf(0).equals(value) || Long.valueOf(-1).equals(value));
			
			if(done)
				b &= 0x7f;
			else
			{
				value >>= 1;
				b |= 0x80;
			}
			
			result = ArrayUtils.add(result,b);
			
		}
		while(!done);
		
		return result;
	}
	
	public static Long decodeLong(Bytes buf)
	{
		Long result = Long.valueOf(0);
		
		int shift = 0;
		
		byte b;
		
		do
		{
			b = buf.parseByte();
			
			Long lowBits = Long.valueOf(b & 0x7f);
			
			result |= lowBits << shift;
			
			shift += 7;
			
		}while((b & 0x80) != 0);
		
		
		if((0x40 & b) == 0x40)
			result |= Integer.valueOf(-1) << shift;
		
		return result;
	}	

}
