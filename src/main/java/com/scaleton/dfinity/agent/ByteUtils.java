package com.scaleton.dfinity.agent;

public final class ByteUtils {
	
	/*
	 * Helper function to convert byte array to unsigned int array. 
	 * Makes easier to compare with data in Rust implementation
	 */

	public static int[] toUnsignedIntegerArray(byte[] input)
	{
		int[] output = new int[input.length];
		
		for(int i = 0; i < input.length; i++)
			output[i] = Byte.toUnsignedInt(input[i]);
			
		return output;
	}	

}
