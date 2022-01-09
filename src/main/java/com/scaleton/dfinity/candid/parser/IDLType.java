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
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import com.scaleton.dfinity.candid.CandidError;
import com.scaleton.dfinity.candid.types.Label;
import com.scaleton.dfinity.candid.types.Type;
import com.scaleton.dfinity.types.Principal;

public final class IDLType {
	Type type;
	IDLType innerType;
	
	Map<Label,IDLType> typeMap = new TreeMap<Label,IDLType>();


	void addInnerTypes(Object value) {
		if (value == null)
			return;
		if (value instanceof Optional)
		{
			if( ((Optional) value).isPresent())
				this.innerType = IDLType.createType(((Optional) value).get());
		}
		else if (value.getClass().isArray()) {
			//Class clazz = ((Object[]) value).getClass().getComponentType();
			
			Object[] arrayValue = (Object[]) value;
			
			if(arrayValue.length > 0)
				this.innerType = IDLType.createType(arrayValue[0]);
			else
			{	
				Class clazz = value.getClass().getComponentType();
				this.innerType = IDLType.createType(clazz);
			}
		}
		else if (value instanceof Map)
		{
			
			this.typeMap = new TreeMap<Label,IDLType>();
			
			for(Object key : ((Map) value).keySet())
			{
				Label label;
				if(key instanceof String)
					label = Label.createNamedLabel((String)key);
				else if(key instanceof Integer)
					label = Label.createIdLabel((Integer)key);
				else
					throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Invalid Map Key");
				
				IDLType idlType = IDLType.createType(((Map) value).get(key));
				
				this.typeMap.put(label, idlType);	
			}
		}		

	}

	void addInnerType(Class clazz) {
		if (clazz == null)
			return;
		else if (clazz.isArray()) {
			clazz = clazz.getComponentType();
			this.innerType = IDLType.createType(clazz);
		}

	}

	public static IDLType createType(Type type) {
		IDLType idlType = new IDLType();

		idlType.type = type;

		return idlType;
	}

	public static IDLType createType(Type type, IDLType innerType) {
		IDLType idlType = new IDLType();

		idlType.type = type;
		
		if(type == Type.OPT || type == Type.VEC)
			if(innerType != null)
				idlType.innerType = innerType;

		return idlType;
	}
	
	public static IDLType createType(Type type, Map<Label,IDLType> typeMap) {
		IDLType idlType = new IDLType();

		idlType.type = type;
		
		
		if(type == Type.RECORD || type == Type.VARIANT)
			if(typeMap != null)
				idlType.typeMap = typeMap;

		return idlType;
	}
	
	public static IDLType createType(Object value, Type type) {
		IDLType idlType = new IDLType();

		idlType.type = type;
		
		idlType.addInnerTypes(value);

		return idlType;
	}

	public static IDLType createType(Object value) {
		IDLType idlType = new IDLType();

		idlType.type = Type.NULL;

		if (value == null)
			return idlType;

		if (value instanceof Boolean)
			idlType.type = Type.BOOL;
		else if (value instanceof BigInteger)
			idlType.type = Type.INT;
		else if (value instanceof Byte)
			idlType.type = Type.INT8;
		else if (value instanceof Short)
			idlType.type = Type.INT16;
		else if (value instanceof Integer)
			idlType.type = Type.INT32;
		else if (value instanceof Long)
			idlType.type = Type.INT64;
		else if (value instanceof Float)
			idlType.type = Type.FLOAT32;
		else if (value instanceof Double)
			idlType.type = Type.FLOAT64;
		else if (value instanceof String)
			idlType.type = Type.TEXT;
		else if (value instanceof Optional)
			idlType.type = Type.OPT;
		else if (value.getClass().isArray())
			idlType.type = Type.VEC;
		else if (value instanceof Map)
			idlType.type = Type.RECORD;		
		else if (value instanceof Principal)
			idlType.type = Type.PRINCIPAL;

		idlType.addInnerTypes(value);

		return idlType;

	}

	static IDLType createType(Class clazz) {
		IDLType idlType = new IDLType();

		idlType.type = Type.NULL;

		if (clazz == Boolean.class)
			idlType.type = Type.BOOL;
		else if (clazz == BigInteger.class)
			idlType.type = Type.INT;
		else if (clazz == Byte.class)
			idlType.type = Type.INT8;
		else if (clazz == Short.class)
			idlType.type = Type.INT16;
		else if (clazz == Integer.class)
			idlType.type = Type.INT32;
		else if (clazz == Long.class)
			idlType.type = Type.INT64;
		else if (clazz == Float.class)
			idlType.type = Type.FLOAT32;
		else if (clazz == Double.class)
			idlType.type = Type.FLOAT64;
		else if (clazz == String.class)
			idlType.type = Type.TEXT;
		else if (clazz == Optional.class)
			idlType.type = Type.OPT;
		else if (clazz.isArray())
			idlType.type = Type.VEC;
		else if (Map.class.isAssignableFrom(clazz))
			idlType.type = Type.RECORD;			
		else if (clazz == Optional.class)
			idlType.type = Type.PRINCIPAL;

		idlType.addInnerType(clazz);

		return idlType;

	}
	
	public static boolean isDefaultType(Class clazz)
	{
		if( Number.class.isAssignableFrom(clazz) ||  Boolean.class.isAssignableFrom(clazz) || String.class.isAssignableFrom(clazz) || Optional.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz) || Principal.class.isAssignableFrom(clazz) )
			return true;
		else
			return false;
	}
	
	public static boolean isPrimitiveType(Class clazz)
	{
		if( Number.class.isAssignableFrom(clazz) ||  Boolean.class.isAssignableFrom(clazz) || String.class.isAssignableFrom(clazz) || Principal.class.isAssignableFrom(clazz) )
			return true;
		else
			return false;
	}	

	public Type getType() {
		return this.type;
	}

	public IDLType getInnerType() {
		return this.innerType;
	}
	
	public Map<Label,IDLType> getTypeMap()
	{
		return this.typeMap;
	}
	


}
