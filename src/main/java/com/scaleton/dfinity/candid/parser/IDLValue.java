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

import java.util.Optional;

import com.scaleton.dfinity.candid.Deserialize;
import com.scaleton.dfinity.candid.Deserializer;
import com.scaleton.dfinity.candid.Serializer;
import com.scaleton.dfinity.candid.types.Type;
import com.scaleton.dfinity.types.Principal;

public final class IDLValue implements Deserialize{
	Optional<?> value;
	Type type;
	
	public static IDLValue create(Object value, Type type)
	{
		IDLValue idlValue = new IDLValue();
		
		idlValue.type = type;
			
		idlValue.value = Optional.ofNullable(value);
		
		return idlValue;
	}	
    
	public static IDLValue create(Object value)
	{
		IDLValue idlValue = new IDLValue();
		
		idlValue.value = Optional.ofNullable(value);
		
		idlValue.type = Type.createType(value);	
		
		return idlValue;
	}
	
	public void idlSerialize(Serializer serializer)
	{
		switch(this.type)
		{
		case NULL:
			serializer.serializeNull();
			break;		
		case BOOL:
			serializer.serializeBool((Boolean) value.get());
			break;
		case INT:
			serializer.serializeInt((Integer) value.get());
			break;
		case INT8:
			serializer.serializeByte((Byte) value.get());
			break;	
		case INT16:
			serializer.serializeShort((Short) value.get());
			break;
		case INT32:
			serializer.serializeInteger((Integer) value.get());
			break;
		case INT64:
			serializer.serializeLong((Long) value.get());
			break;			
		case FLOAT32:
			serializer.serializeFloat((Float) value.get());	
			break;
		case FLOAT64:
			serializer.serializeDouble((Double) value.get());
			break;			
		case TEXT:
			serializer.serializeString((String) value.get());
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
	
	public Type getType()
	{
		return this.type;	
	}

	public <T> T getValue()
	{
		T value = (T) this.value.get();
		return value;
	}
	
	public void deserialize(Deserializer deserializer) {
		IDLValue idlValue = deserializer.deserializeAny();	
		
		this.type = idlValue.type;
		this.value = idlValue.value;
	}	

}
