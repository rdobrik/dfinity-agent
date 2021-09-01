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

package com.scaleton.dfinity.candid.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;

import com.scaleton.dfinity.types.Principal;

public enum Type {
	NULL, BOOL, NAT, INT, NAT8, NAT16, NAT32, NAT64, INT8, INT16, INT32, INT64, FLOAT32, FLOAT64, TEXT, RESERVED, EMPTY,
	OPT, VEC, RECORD, VARIANT, FUNC, SERVICE, PRINCIPAL;

	List<Type> innerTypes = new ArrayList<Type>();

	Type() {
	}
	
	void addInnerType(Object value) {
		if(value == null)
			return;
		else if (value instanceof Optional)
			this.innerTypes.add(Type.createType(((Optional) value).get()));
		else if (value.getClass().isArray())
		{
			Class clazz = ((Object[])value).getClass().getComponentType();
			this.innerTypes.add(Type.createType(clazz));
		}
					
	}
	
	void addInnerType(Class clazz) {
		if(clazz == null)
			return;
		else if (clazz.isArray())
		{
			clazz = clazz.getComponentType();
			this.innerTypes.add(Type.createType(clazz));
		}
					
	}	

	public static Type createType(Object value) {
		Type type = NULL;

		if (value instanceof Boolean)
			type = Type.BOOL;
		else if (value instanceof Integer)
			type = Type.INT;
		else if (value instanceof Byte)
			type = Type.INT8;
		else if (value instanceof Short)
			type = Type.INT16;
		else if (value instanceof Long)
			type = Type.INT64;
		else if (value instanceof Float)
			type = Type.FLOAT32;
		else if (value instanceof Double)
			type = Type.FLOAT64;
		else if (value instanceof String)
			type = Type.TEXT;
		else if (value instanceof Optional)
			type = Type.OPT;
		else if (value.getClass().isArray())
			type = Type.VEC;
		else if (value instanceof Principal)
			type = Type.PRINCIPAL;

		type.addInnerType(value);

		return type;

	}
	
	static Type createType(Class clazz) {
		Type type = NULL;

		if (clazz == Boolean.class)
			type = Type.BOOL;
		else if (clazz == Integer.class)
			type = Type.INT;
		else if(clazz == Byte.class)
			type = Type.INT8;
		else if (clazz == Short.class)
			type = Type.INT16;
		else if (clazz == Long.class)
			type = Type.INT64;
		else if (clazz == Float.class)
			type = Type.FLOAT32;
		else if (clazz == Double.class)
			type = Type.FLOAT64;
		else if (clazz == String.class)
			type = Type.TEXT;
		else if (clazz == Optional.class)
			type = Type.OPT;
		else if (clazz.isArray())
			type = Type.VEC;
		else if (clazz == Principal.class)
			type = Type.PRINCIPAL;

		type.addInnerType(clazz);

		return type;

	}	
	

	public List<Type> getInnerTypes() {
		return this.innerTypes;
	}
	
	public boolean isPrimitive()
	{
		Type[] primitives = {NULL,BOOL,NAT,INT,NAT8,NAT16,NAT32,NAT64,INT8,INT16,INT32,INT64,FLOAT32,FLOAT64,TEXT,RESERVED,EMPTY,PRINCIPAL};
		
		return ArrayUtils.contains(primitives, this);
	}	

}
