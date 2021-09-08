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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.scaleton.dfinity.candid.parser.IDLType;
import com.scaleton.dfinity.candid.types.Opcode;
import com.scaleton.dfinity.candid.types.Type;

public final class TypeSerialize {
	Map<Type, Integer> typeMap;
	List<byte[]> typeTable;
	List<IDLType> args;

	byte[] result;

	TypeSerialize() {
		this.typeMap = new HashMap<Type, Integer>();
		this.typeTable = new ArrayList<byte[]>();
		this.args = new ArrayList<IDLType>();
		this.result = ArrayUtils.EMPTY_BYTE_ARRAY;
	}

	void pushType(IDLType type) {
		this.args.add(type);
		this.buildType(type);
	}

	void buildType(IDLType type) {
		if (typeMap.containsKey(type))
			return;

		IDLType actualType = type;

		if (actualType.getType().isPrimitive())
			return;

		Integer idx = this.typeTable.size();

		this.typeMap.put(type.getType(), idx);

		byte[] buf = ArrayUtils.EMPTY_BYTE_ARRAY;

		this.typeTable.add(ArrayUtils.EMPTY_BYTE_ARRAY);

		switch (actualType.getType()) {
		case OPT:
			for (IDLType innerType : actualType.getInnerTypes())
				this.buildType(innerType);
			buf = ArrayUtils.addAll(buf, Leb128.writeSigned(Opcode.OPT.value));
			for (IDLType innerType : actualType.getInnerTypes())
				buf = ArrayUtils.addAll(buf, this.encode(innerType.getType()));
			break;
		case VEC:
			for (IDLType innerType : actualType.getInnerTypes())
				this.buildType(innerType);
			buf = ArrayUtils.addAll(buf, Leb128.writeSigned(Opcode.VEC.value));
			for (IDLType innerType : actualType.getInnerTypes())
				buf = ArrayUtils.addAll(buf, this.encode(innerType.getType()));
			break;

		}

		this.typeTable.set(idx, buf);
	}

	byte[] encode(Type type) {
		switch (type) {
		case NULL:
			return Leb128.writeSigned(Opcode.NULL.value);
		case BOOL:
			return Leb128.writeSigned(Opcode.BOOL.value);
		case NAT:
			return Leb128.writeSigned(Opcode.NAT.value);
		case INT:
			return Leb128.writeSigned(Opcode.INT.value);
		case NAT8:
			return Leb128.writeSigned(Opcode.NAT8.value);
		case NAT16:
			return Leb128.writeSigned(Opcode.NAT16.value);
		case NAT32:
			return Leb128.writeSigned(Opcode.NAT32.value);
		case NAT64:
			return Leb128.writeSigned(Opcode.NAT64.value);
		case INT8:
			return Leb128.writeSigned(Opcode.INT8.value);
		case INT16:
			return Leb128.writeSigned(Opcode.INT16.value);
		case INT32:
			return Leb128.writeSigned(Opcode.INT32.value);
		case INT64:
			return Leb128.writeSigned(Opcode.INT64.value);
		case FLOAT32:
			return Leb128.writeSigned(Opcode.FLOAT32.value);
		case FLOAT64:
			return Leb128.writeSigned(Opcode.FLOAT64.value);
		case RESERVED:
			return Leb128.writeSigned(Opcode.RESERVED.value);
		case TEXT:
			return Leb128.writeSigned(Opcode.TEXT.value);
		case EMPTY:
			return Leb128.writeSigned(Opcode.EMPTY.value);
		case PRINCIPAL:
			return Leb128.writeSigned(Opcode.PRINCIPAL.value);
		default:
			Integer idx = this.typeMap.getOrDefault(type, -1);
			if (idx != -1)
				return Leb128.writeSigned(idx);
			else
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, String.format("Type %s not found", type));
		}
	}

	void serialize() {
		this.result = ArrayUtils.addAll(this.result, Leb128.writeUnsigned(this.typeTable.size()));

		// TODO serialize content of type table
		for (byte[] type : this.typeTable)
			this.result = ArrayUtils.addAll(this.result, type);

		this.result = ArrayUtils.addAll(this.result, Leb128.writeUnsigned(this.args.size()));

		byte[] tyEncode = ArrayUtils.EMPTY_BYTE_ARRAY;

		for (IDLType idlType : args) {
			tyEncode = ArrayUtils.addAll(tyEncode, this.encode(idlType.getType()));
		}

		this.result = ArrayUtils.addAll(this.result, tyEncode);
	}

	byte[] getResult() {
		return this.result;
	}

}
