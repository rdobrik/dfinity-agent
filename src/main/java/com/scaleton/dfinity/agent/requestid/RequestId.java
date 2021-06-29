package com.scaleton.dfinity.agent.requestid;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.scaleton.dfinity.agent.Serialize;

public final class RequestId {
	byte[] value;
	
	
	RequestId(byte[] value)
	{
		this.value = value;
	}
	
	/*
	Derive the request ID from a serializable data structure.
	
	See https://hydra.dfinity.systems//build/268411/download/1/dfinity/spec/public/index.html#api-request-id
	
	# Warnings

	The argument type simply needs to be serializable; the function
	does NOT sift between fields to include them or not and assumes
	the passed value only includes fields that are not part of the
	envelope and should be included in the calculation of the request
	id.
	*/
	
	public static <T extends Serialize> RequestId toRequestId(T value) throws RequestIdError
	{
		RequestIdSerializer serializer = new RequestIdSerializer();
		value.serialize(serializer);
		
		serializer.hashFields();
		return serializer.finish();
	}
	
	public static RequestId fromHexString(String hexValue) 
	{

		try {
			return new RequestId((Hex.decodeHex(hexValue)));
		} catch (DecoderException e) {
			throw RequestIdError.create(RequestIdError.RequestIdErrorCode.CUSTOM_SERIALIZER_ERROR, e, e.getLocalizedMessage());
		}
	}	
	
	public byte[] get()
	{
		return this.value;
	}
	
	public String toString()
	{
		return this.value.toString();
	}
	
	public String toHexString()
	{
		return Hex.encodeHexString(this.value);
	}	

}
