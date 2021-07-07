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

package com.scaleton.dfinity.candid;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

final public class Bytes{
	static final byte[] MAGIC_NUMBER = "DIDL".getBytes();
	
	ByteBuffer data;
	
	Bytes(byte[] input)
	{
		this.data = ByteBuffer.wrap(input);
	}
	
	public static Bytes from(byte[] input)
	{
		return new Bytes(input);
	}
	
	public Integer leb128Read()
	{
		return Leb128.readUnsigned(data);
	}
	
	public Integer leb128ReadSigned()
	{
		return Leb128.readSigned(data);
	}	
	
	public byte parseByte()
	{
		return data.get();
	}	
	
	public byte[] parseBytes(int len)
	{
		if(data.remaining() < len)
			CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Unexpected end of message" );
		
		byte[] buf = new byte[len];
		
		data.get(buf, 0, len);
		
		return buf;
	}
	
	public String parseString(int len)
	{
		byte[] buf = this.parseBytes(len);
		
		return new String(buf, StandardCharsets.UTF_8);
	}
	
	public void parseMagic()
	{
		byte[] buf = new byte[4];
		
		data.get(buf, 0, buf.length);
		
		if(!Arrays.equals(buf, MAGIC_NUMBER))
		{
			CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Wrong magic number " + buf.toString());
		}
	}
}
