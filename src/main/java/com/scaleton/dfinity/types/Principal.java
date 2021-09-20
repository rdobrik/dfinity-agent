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

package com.scaleton.dfinity.types;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;
import java.util.zip.CRC32;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.digest.DigestUtils;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_224;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonSerialize(using = PrincipalSerializer.class)
@JsonDeserialize(using = PrincipalDeserializer.class)
public final class Principal implements Cloneable {
	static final byte ID_ANONYMOUS_BYTES = PrincipalClass.ANONYMOUS.value.byteValue();
	
	static Base32 codec = new Base32();
	
	PrincipalInner principalInner;
	Optional<byte[]> value;
	
	Principal()
	{
	}

	Principal(PrincipalInner principalInner) {
		this.principalInner = principalInner;
		this.value = Optional.of(new byte[0]);
	}

	Principal(PrincipalInner principalInner, byte[] value) {
		this.principalInner = principalInner;
		this.value = Optional.of(value);
	}

	public static Principal managementCanister() {
		byte[] value = {};
		return new Principal(PrincipalInner.MANAGEMENT_CANISTER, value);
	}

    // An anonymous Principal.
	public static Principal anonymous() {
		byte[] value = {Principal.ID_ANONYMOUS_BYTES};
		return new Principal(PrincipalInner.ANONYMOUS,value);
	}

    // Right now we are enforcing a Twisted Edwards Curve 25519 point
    // as the public key.
	public static Principal selfAuthenticating(byte[] publicKey) {
		DigestUtils digestUtils = new DigestUtils(SHA_224);
		byte[] value = digestUtils.digest(publicKey);
		
		// Now add a suffix denoting the identifier as representing a
        // self-authenticating principal.
		value = ArrayUtils.add(value,PrincipalClass.SELF_AUTHENTICATING.value.byteValue());
		
		return new Principal(PrincipalInner.SELF_AUTHENTICATING,value);
	}	

    // Parse the text format for canister IDs (e.g., `jkies-sibbb-ap6`).
    // The text format follows the public spec (see Textual IDs section).	
	public static Principal fromString(String text) throws PrincipalError {

		String value = makeAsciiLowerCase(text);
		value = value.replace("-", "");

		Optional<byte[]> bytes = Optional.ofNullable(codec.decode(value));

		if (bytes.isPresent()) {
			if (bytes.get().length < 4) {
				throw PrincipalError.create(PrincipalError.PrincipalErrorCode.TEXT_TOO_SMALL);
			}

			Principal result = from(Arrays.copyOfRange(bytes.get(), 4, bytes.get().length));

			String expected = result.toString();

			if (text.equals(expected))
				return result;
			else
				throw PrincipalError.create(PrincipalError.PrincipalErrorCode.ABNORMAL_TEXTUAL_FORMAT, expected);
		} else
			throw PrincipalError.create(PrincipalError.PrincipalErrorCode.INVALID_TEXTUAL_FORMAT_NOT_BASE32);
		// Principal principal = new
		// Principal(PrincipalInner.ANONYMOUS,value.getBytes());

	}
	
	public byte[] getValue()
	{
		if (value.isPresent())
			return value.get();
		else 
			throw PrincipalError.create(PrincipalError.PrincipalErrorCode.EXTERNAL_ERROR,"Value is empty");			
	}

	public String toString() {
		if (value.isPresent()) {
			CRC32 hasher = new CRC32();

			hasher.update(value.get());

			// initializing byte array
			byte[] checksum = new byte[] { 0, 0, 0, 0 };

			if (hasher.getValue() > 0)
				checksum = BigInteger.valueOf(Long.valueOf(hasher.getValue()).intValue()).toByteArray();

			byte[] bytes = concatByteArrays(checksum, value.get());

			String output = codec.encodeAsString(bytes);

			output = makeAsciiLowerCase(output);

			// remove padding
			output = StringUtils.stripEnd(output, "=");
			output = output.replaceAll("(.{5})", "$1-");
			return output;
		} else
			return new String();
	}
	
	public Principal clone()
	{
		Principal clone = new Principal();
		
		clone.principalInner = this.principalInner;
		clone.value = this.value;
		
		return clone;
		
	}
	
	public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		
		if(!(obj instanceof Principal))
			return false;
		
		Principal value = (Principal)obj;
		
		if(this.toString() == null && value.toString() == null)
			return true;
		
		return this.toString().equals(value.toString());
	}

	static byte[] concatByteArrays(byte[] first, byte[] second) {
		byte[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	static String makeAsciiLowerCase(String input) {
		String output = new String();

		int i, n;
		char c;

		for (i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (c >= 65 && c <= 90) // If ASCII values represent LowerCase, changing to Upper Case
			{
				n = c + 32;
				c = (char) n;
			}
			output = output + c;
		}

		return output;
	}

	public static Principal from(byte[] bytes) throws PrincipalError {
		if (Optional.ofNullable(bytes).isPresent() && bytes.length > 0) {
			Byte lastByte = bytes[bytes.length - 1];

			switch (PrincipalClass.from(lastByte)) {
			case OPAQUE_ID:
				return new Principal(PrincipalInner.OPAQUE_ID, bytes);
			case SELF_AUTHENTICATING:
				return new Principal(PrincipalInner.SELF_AUTHENTICATING, bytes);
			case DERIVED_ID:
				return new Principal(PrincipalInner.DERIVED_ID, bytes);
			case ANONYMOUS:
				if (bytes.length == 1)
					return new Principal(PrincipalInner.ANONYMOUS);
				else
					throw PrincipalError.create(PrincipalError.PrincipalErrorCode.BUFFER_TOO_LONG);

			case UNASSIGNED:
				return new Principal(PrincipalInner.UNASSIGNED, bytes);
			default:
				throw PrincipalError.create(PrincipalError.PrincipalErrorCode.ABNORMAL_TEXTUAL_FORMAT);
			}
		} else
			return new Principal(PrincipalInner.MANAGEMENT_CANISTER);
	}

	enum PrincipalInner {
		MANAGEMENT_CANISTER, OPAQUE_ID, SELF_AUTHENTICATING, DERIVED_ID, ANONYMOUS, UNASSIGNED;
	}

	enum PrincipalClass {
		UNASSIGNED(0), OPAQUE_ID(1), SELF_AUTHENTICATING(2), DERIVED_ID(3), ANONYMOUS(4);

		final Integer value;

		PrincipalClass(Integer value) {
			this.value = value;
		}

		static PrincipalClass from(Byte value) {
			switch (value) {
			case 1:
				return OPAQUE_ID;
			case 2:
				return SELF_AUTHENTICATING;
			case 3:
				return DERIVED_ID;
			case 4:
				return ANONYMOUS;
			default:
				return UNASSIGNED;
			}
		}

	}

}
