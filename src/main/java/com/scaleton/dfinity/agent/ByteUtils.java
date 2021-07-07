/*
 * Copyright 2021 Exilor Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
