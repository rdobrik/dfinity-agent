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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.scaleton.dfinity.candid.types.Type;
import com.scaleton.dfinity.types.Principal;

public final class IDLType {
	Type type;
	List<IDLType> innerTypes;

	IDLType() {
		innerTypes = new ArrayList<IDLType>();
	}

	void addInnerType(Object value) {
		if (value == null)
			return;
		if (value instanceof Optional)
		{
			if( ((Optional) value).isPresent())
				this.innerTypes.add(IDLType.createType(((Optional) value).get()));
		}
		else if (value.getClass().isArray()) {
			Class clazz = ((Object[]) value).getClass().getComponentType();
			this.innerTypes.add(IDLType.createType(clazz));
		}

	}

	void addInnerType(Class clazz) {
		if (clazz == null)
			return;
		else if (clazz.isArray()) {
			clazz = clazz.getComponentType();
			this.innerTypes.add(IDLType.createType(clazz));
		}

	}

	public static IDLType createType(Type type) {
		IDLType idlType = new IDLType();

		idlType.type = type;

		return idlType;
	}

	public static IDLType createType(Type type, List<IDLType> innerTypes) {
		IDLType idlType = new IDLType();

		idlType.type = type;
		idlType.innerTypes = innerTypes;

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
		else if (value instanceof Principal)
			idlType.type = Type.PRINCIPAL;

		idlType.addInnerType(value);

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
		else if (clazz == Principal.class)
			idlType.type = Type.PRINCIPAL;

		idlType.addInnerType(clazz);

		return idlType;

	}

	public Type getType() {
		return this.type;
	}

	public List<IDLType> getInnerTypes() {
		return this.innerTypes;
	}

}
