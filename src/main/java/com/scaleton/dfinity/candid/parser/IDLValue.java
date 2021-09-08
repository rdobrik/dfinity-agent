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

package com.scaleton.dfinity.candid.parser;

import java.math.BigInteger;
import java.util.Optional;

import com.scaleton.dfinity.candid.Deserialize;
import com.scaleton.dfinity.candid.Deserializer;
import com.scaleton.dfinity.candid.Serializer;
import com.scaleton.dfinity.candid.types.Type;
import com.scaleton.dfinity.types.Principal;

public final class IDLValue implements Deserialize{
	Optional<?> value;
	IDLType idlType;
	
	public static IDLValue create(Object value, Type type)
	{
		IDLValue idlValue = new IDLValue();
		
		idlValue.idlType = IDLType.createType(type);
			
		idlValue.value = Optional.ofNullable(value);
		
		return idlValue;
	}	
    
	public static IDLValue create(Object value)
	{
		IDLValue idlValue = new IDLValue();
		
		idlValue.value = Optional.ofNullable(value);
		
		idlValue.idlType = IDLType.createType(value);	
		
		return idlValue;
	}
	
	public void idlSerialize(Serializer serializer)
	{
		switch(this.idlType.type)
		{
		case NULL:
			serializer.serializeNull();
			break;		
		case BOOL:
			serializer.serializeBool((Boolean) value.get());
			break;
		case NAT:
			serializer.serializeNat((BigInteger) value.get());
			break;
		case NAT8:
			serializer.serializeNat8((Byte) value.get());
			break;
		case NAT16:
			serializer.serializeNat16((Short) value.get());
			break;
		case NAT32:
			serializer.serializeNat32((Integer) value.get());
			break;
		case NAT64:
			serializer.serializeNat64((Long) value.get());
			break;			
		case INT:
			serializer.serializeInt((BigInteger) value.get());			
			break;
		case INT8:
			serializer.serializeInt8((Byte) value.get());
			break;	
		case INT16:
			serializer.serializeInt16((Short) value.get());
			break;
		case INT32:
			serializer.serializeInt32((Integer) value.get());
			break;
		case INT64:
			serializer.serializeInt64((Long) value.get());
			break;			
		case FLOAT32:
			serializer.serializeFloat32((Float) value.get());	
			break;
		case FLOAT64:
			serializer.serializeFloat64((Double) value.get());
			break;			
		case TEXT:
			serializer.serializeText((String) value.get());
			break;	
		case OPT:
			serializer.serializeOpt((Optional) value.get());
			break;
		case VEC:
			serializer.serializeVec((Object[])value.get());
			break;
		case PRINCIPAL:
			serializer.serializePrincipal((Principal) value.get());
			break;				
		}

	}
	
	public IDLType getIDLType()
	{
		return this.idlType;	
	}	
	
	public Type getType()
	{
		return this.idlType.type;	
	}

	public <T> T getValue()
	{
		if(this.value.isPresent())
		{
			T value = (T) this.value.get();
			return value;
		}
		else
			return null;
	}
	
	public void deserialize(Deserializer deserializer) {
		IDLValue idlValue = deserializer.deserializeAny();	
		
		this.idlType = idlValue.idlType;
		this.value = idlValue.value;
	}	

}
