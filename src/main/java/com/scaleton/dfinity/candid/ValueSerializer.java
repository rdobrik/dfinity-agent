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

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scaleton.dfinity.candid.parser.IDLValue;
import com.scaleton.dfinity.candid.types.Numbers;
import com.scaleton.dfinity.types.Principal;

public final class ValueSerializer implements Serializer{
	static final Logger LOG = LoggerFactory.getLogger(ValueSerializer.class);
	
	byte[] value;
	
	ValueSerializer()
	{
		this.value = ArrayUtils.EMPTY_BYTE_ARRAY;
	}
	
	public void serializeNull() {		
	}	
	
	public final void serializeBool(Boolean value)
    {   
		this.value = ArrayUtils.add(this.value, (byte)(value?1:0));
    }	
	
	public final void serializeText(String value)
    {
    	byte[] leb128 = Leb128.writeUnsigned(value.length());
    	
    	this.value = ArrayUtils.addAll(this.value,leb128);
    	
    	this.value = ArrayUtils.addAll(this.value,value.getBytes()); 	
    }
	
	public final void serializeNat(BigInteger value)
    { 
		if(value.compareTo(BigInteger.ZERO) < 0)
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, String.format("Invalid unsigned value %d", value));
		
		this.value = ArrayUtils.addAll(this.value, Numbers.encodeBigNat(value));
    }	
	
	public final void serializeNat8(Byte value)
    {   		
		ByteBuffer output = ByteBuffer.allocate(Byte.BYTES);
		output.order(ByteOrder.LITTLE_ENDIAN);
	    output.put(value);
	    
		this.value = ArrayUtils.addAll(this.value, output.array());
    }
	
	public final void serializeNat16(Short value)
    { 
		ByteBuffer output = ByteBuffer.allocate(Short.BYTES);
		output.order(ByteOrder.LITTLE_ENDIAN);
	    output.putShort(value);
	    
		this.value = ArrayUtils.addAll(this.value, output.array());
    }
	
	
	public final void serializeNat32(Integer value)
    { 		
		ByteBuffer output = ByteBuffer.allocate(Integer.BYTES);
		output.order(ByteOrder.LITTLE_ENDIAN);
	    output.putInt(value);
	    
		this.value = ArrayUtils.addAll(this.value, output.array());
    }
	
	public final void serializeNat64(Long value)
    { 		
		ByteBuffer output = ByteBuffer.allocate(Long.BYTES);
		output.order(ByteOrder.LITTLE_ENDIAN);
	    output.putLong(value);
	    
		this.value = ArrayUtils.addAll(this.value, output.array());
    }	
	
	public final void serializeInt(BigInteger value)
    {   		
		this.value = ArrayUtils.addAll(this.value, Numbers.encodeBigInt(value));
    }
	
	public final void serializeFloat64(Double value)
    {   
		ByteBuffer output = ByteBuffer.allocate(Double.BYTES);
		output.order(ByteOrder.LITTLE_ENDIAN);
	    output.putDouble(value);
	    
		this.value = ArrayUtils.addAll(this.value, output.array());
    }		
	
	public final void serializeFloat32(Float value)
    {   
		ByteBuffer output = ByteBuffer.allocate(Float.BYTES);
		output.order(ByteOrder.LITTLE_ENDIAN);
	    output.putFloat(value);
	    
		this.value = ArrayUtils.addAll(this.value, output.array());
    }
	
	public final void serializeInt8(Byte value)
    {   
		ByteBuffer output = ByteBuffer.allocate(Byte.BYTES);
		output.order(ByteOrder.LITTLE_ENDIAN);
	    output.put(value);
	    
		this.value = ArrayUtils.addAll(this.value, output.array());
    }
	
	public final void serializeInt16(Short value)
    {   
		ByteBuffer output = ByteBuffer.allocate(Short.BYTES);
		output.order(ByteOrder.LITTLE_ENDIAN);
	    output.putShort(value);
	    
		this.value = ArrayUtils.addAll(this.value, output.array());
    }
	
	
	public final void serializeInt32(Integer value)
    {   
		ByteBuffer output = ByteBuffer.allocate(Integer.BYTES);
		output.order(ByteOrder.LITTLE_ENDIAN);
	    output.putInt(value);
	    
		this.value = ArrayUtils.addAll(this.value, output.array());
    }
	
	public final void serializeInt64(Long value)
    {   
		ByteBuffer output = ByteBuffer.allocate(Long.BYTES);
		output.order(ByteOrder.LITTLE_ENDIAN);
	    output.putLong(value);
	    
		this.value = ArrayUtils.addAll(this.value, output.array());
    }	
	
	public final void serializeOpt(Optional<?> value) {
		if(value.isPresent())
		{
			byte[] leb128 = Leb128.writeUnsigned(1);
	    	
	    	this.value = ArrayUtils.addAll(this.value,leb128);		
	    	
	    	Object obj = value.get();
	    	
	    	IDLValue idlValue = IDLValue.create(obj);
	    	
	    	idlValue.idlSerialize(this);
		}
		else
		{
			byte[] leb128 = Leb128.writeUnsigned(0);
	    	
	    	this.value = ArrayUtils.addAll(this.value,leb128);
		}
		
	}

	public final <T> void serializeVec(T[] value) {
		byte[] leb128 = Leb128.writeUnsigned(value.length);
    	
    	this.value = ArrayUtils.addAll(this.value,leb128);
    	
    	for(Object element : value)
    	{
    		IDLValue idlValue = IDLValue.create(element);
    		idlValue.idlSerialize(this);
    	}
		
	}

	public final void serializePrincipal(Principal value) {
		this.value = ArrayUtils.addAll(this.value,(byte)1);
		
		byte[] leb128 = Leb128.writeUnsigned(value.getValue().length);
    	
    	this.value = ArrayUtils.addAll(this.value,leb128);
    	
    	this.value = ArrayUtils.addAll(this.value,value.getValue());
		
	}
	
	void serializeElement(Object value) {
		
	}
	
	byte[] getResult()
	{
		return this.value;
	}	

}
