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

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import org.apache.commons.codec.binary.Base32;

final public class Bytes {
	static final byte[] MAGIC_NUMBER = "DIDL".getBytes();

	ByteBuffer data;

	Bytes(byte[] input) {
		this.data = ByteBuffer.wrap(input);
	}

	public static Bytes from(byte[] input) {
		return new Bytes(input);
	}

	public Integer leb128Read() {
		try
		{
			return Leb128.readUnsigned(this.data);
		}catch(BufferUnderflowException e)
		{
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Unexpected end of message");
		}
	}

	public Integer leb128ReadSigned() {
		try
		{
			return Leb128.readSigned(this.data);
		}catch(BufferUnderflowException e)
		{
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Unexpected end of message");
		}
	}

	public byte parseByte() {
		if (this.data.remaining() < 1)
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Unexpected end of message");

		return this.data.get();
	}

	public byte[] parseBytes(int len) {
		if (this.data.remaining() < len)
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Unexpected end of message");

		byte[] buf = new byte[len];

		this.data.get(buf, 0, len);

		return buf;
	}

	public String parseString(int len) {
		byte[] buf = this.parseBytes(len);

		return new String(buf, StandardCharsets.UTF_8);
	}

	public void parseMagic() {
		byte[] buf = new byte[4];


		try
		{
			this.data.get(buf, 0, buf.length);
		}
		catch(BufferUnderflowException e)
		{
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Wrong magic number size");
		}

		if (!Arrays.equals(buf, MAGIC_NUMBER))
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Wrong magic number " + new String(buf));
	}
}
