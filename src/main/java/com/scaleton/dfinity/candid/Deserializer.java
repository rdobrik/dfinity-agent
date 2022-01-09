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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import com.scaleton.dfinity.candid.parser.IDLType;
import com.scaleton.dfinity.candid.parser.IDLValue;
import com.scaleton.dfinity.candid.types.Label;
import com.scaleton.dfinity.candid.types.Numbers;
import com.scaleton.dfinity.candid.types.Opcode;
import com.scaleton.dfinity.candid.types.Type;
import com.scaleton.dfinity.types.Principal;

public final class Deserializer {
	Bytes input;
	TypeTable table;

	Optional<IDLType> expectedType = Optional.empty();

	Optional<String> fieldName = Optional.empty();

	int recordNestingDepth;

	Deserializer(Bytes input, TypeTable table, String fieldName, int recordNestingDepth) {
		this.input = input;

		this.table = table;

		this.recordNestingDepth = recordNestingDepth;

		this.fieldName = Optional.ofNullable(fieldName);
	}

	public static Deserializer fromBytes(byte[] input) {
		TypeTableResponse response = TypeTable.fromBytes(input);

		return new Deserializer(Bytes.from(response.data), response.typeTable, null, 0);
	}

	public IDLValue deserializeAny() {
		if (this.fieldName.isPresent()) {
			return this.deserializeIdentifier();
		}

		Opcode type = this.table.peekType();

		if (type != Opcode.RECORD)
			this.recordNestingDepth = 0;

		switch (type) {
		case NULL:
			return this.deserializeNull();
		case BOOL:
			return this.deserializeBool();
		case NAT:
			return this.deserializeNat();
		case NAT8:
			return this.deserializeNat8();
		case NAT16:
			return this.deserializeNat16();
		case NAT32:
			return this.deserializeNat32();
		case NAT64:
			return this.deserializeNat64();
		case INT:
			return this.deserializeInt();
		case INT8:
			return this.deserializeInt8();
		case INT16:
			return this.deserializeInt16();
		case INT32:
			return this.deserializeInt32();
		case INT64:
			return this.deserializeInt64();
		case FLOAT32:
			return this.deserializeFloat32();
		case FLOAT64:
			return this.deserializeFloat64();
		case TEXT:
			return this.deserializeText();
		case OPT:
			return this.deserializeOpt();
		case VEC:
			return this.deserializeVec();
		case RESERVED:
			return this.deserializeReserved();
		case RECORD:
			return this.deserializeRecord();
		case VARIANT:
			return this.deserializeVariant();
		case PRINCIPAL:
			return this.deserializePrincipal();
		default:
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM,
					String.format("Unrecogized type %d", type.value));
		}

	}

	public IDLValue deserializeNull() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.NULL);

		return IDLValue.create(null);
	}

	public IDLValue deserializeBool() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.BOOL);

		byte b = this.input.parseByte();

		if (b > 1)
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Not a boolean value");

		boolean value = b == 1;

		return IDLValue.create(Boolean.valueOf(value));

	}

	public IDLValue deserializeText() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.TEXT);
		int len = this.input.leb128Read();

		String value = this.input.parseString(len);

		return IDLValue.create(value);
	}

	public IDLValue deserializeNat() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.NAT);

		BigInteger value = Numbers.decodeBigNat(this.input);

		return IDLValue.create(value);
	}

	public IDLValue deserializeInt() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.INT);

		BigInteger value = Numbers.decodeBigInt(this.input);

		return IDLValue.create(value);
	}

	public IDLValue deserializeFloat64() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.FLOAT64);

		Double value = ByteBuffer.wrap(this.input.parseBytes(Double.BYTES)).order(ByteOrder.LITTLE_ENDIAN).getDouble();

		return IDLValue.create(value);
	}

	public IDLValue deserializeFloat32() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.FLOAT32);

		Float value = ByteBuffer.wrap(this.input.parseBytes(Float.BYTES)).order(ByteOrder.LITTLE_ENDIAN).getFloat();

		return IDLValue.create(value);
	}

	public IDLValue deserializeNat8() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.NAT8);

		Byte value = this.input.parseByte();

		return IDLValue.create(value);
	}

	public IDLValue deserializeNat16() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.NAT16);

		Short value = ByteBuffer.wrap(this.input.parseBytes(Short.BYTES)).order(ByteOrder.LITTLE_ENDIAN).getShort();

		return IDLValue.create(value);
	}

	public IDLValue deserializeNat32() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.NAT32);

		Integer value = ByteBuffer.wrap(this.input.parseBytes(Integer.BYTES)).order(ByteOrder.LITTLE_ENDIAN).getInt();

		return IDLValue.create(value);
	}

	public IDLValue deserializeNat64() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.NAT64);

		Long value = ByteBuffer.wrap(this.input.parseBytes(Long.BYTES)).order(ByteOrder.LITTLE_ENDIAN).getLong();

		return IDLValue.create(value);
	}

	public IDLValue deserializeInt8() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.INT8);

		Byte value = this.input.parseByte();

		return IDLValue.create(value);
	}

	public IDLValue deserializeInt16() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.INT16);

		Short value = ByteBuffer.wrap(this.input.parseBytes(Short.BYTES)).order(ByteOrder.LITTLE_ENDIAN).getShort();

		return IDLValue.create(value);
	}

	public IDLValue deserializeInt32() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.INT32);

		Integer value = ByteBuffer.wrap(this.input.parseBytes(Integer.BYTES)).order(ByteOrder.LITTLE_ENDIAN).getInt();

		return IDLValue.create(value);
	}

	public IDLValue deserializeInt64() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.INT64);

		Long value = ByteBuffer.wrap(this.input.parseBytes(Long.BYTES)).order(ByteOrder.LITTLE_ENDIAN).getLong();

		return IDLValue.create(value);
	}

	public IDLValue deserializeOpt() {
		this.recordNestingDepth = 0;

		Optional value;
		
		Optional<IDLType> expectedType =this.expectedType;

		switch (this.table.peekType()) {
		case OPT:
			if(expectedType.isPresent())
					if(expectedType.get().getType() != Type.OPT)
						throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, String.format("Not an expected type"));
					else
						this.expectedType = Optional.ofNullable(expectedType.get().getInnerType());
			
			this.table.parseType();
			switch (this.input.parseByte()) {
			case 0:
				value = Optional.empty();
				break;
			case 1:
				value = Optional.of(this.deserializeAny().getValue());
				break;
			default:
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, String.format("Not an option tag"));
			}
			break;
		case NULL:
		case RESERVED:
			this.table.parseType();
			value = Optional.empty();
			break;
		default:
			value = Optional.of(this.deserializeAny().getValue());
		}

		this.expectedType = expectedType;
		
		return IDLValue.create(value);
	}

	public IDLValue deserializeVec() {
		this.recordNestingDepth = 0;
		
		Object value;
		
		Optional<IDLType> expectedType =this.expectedType;

		switch (this.table.parseType()) {
		case VEC: {
			if(expectedType.isPresent())
				if(expectedType.get().getType() != Type.VEC)
					throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, String.format("Not an expected type"));
				else
					this.expectedType = Optional.ofNullable(expectedType.get().getInnerType());
			
			int len = this.input.leb128Read();

			List values = new ArrayList<>(len);

			for (int i = 0; i < len; i++) {
				Integer ty = this.table.peekCurrentType();

				IDLValue idlValue = this.deserializeAny();

				values.add(idlValue.getValue());
				
				this.table.currentType.addFirst(ty);
			}

			Object[] array = this.toArray(values);

			this.table.popCurrentType();
			
			value = array;
			break;
		}
		case RECORD:
			// TODO Implement vector, when supported
		default:
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM,
					String.format("Seq only takes vector or tuple"));
		}
		
		this.expectedType = expectedType;
		
		return IDLValue.create(value);
	}

	public IDLValue deserializeRecord() {
		int oldNesting = this.recordNestingDepth;
		
		this.table.checkType(Opcode.RECORD);
		
		Optional<IDLType> expectedType = this.expectedType;
		Optional<Map<Label,IDLType>> typeMap = Optional.empty();
		

		this.recordNestingDepth++;

		if (this.recordNestingDepth > this.table.table.size())
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM,
					"There is an infinite loop in the record definition, the type is isomorphic to an empty type");
		
		if(expectedType.isPresent())
			if(expectedType.get().getType() != Type.RECORD)
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, String.format("Not an expected type"));
			else
				typeMap = Optional.ofNullable(expectedType.get().getTypeMap());

		int len = this.table.popCurrentType();

		Map<Integer, Optional> fs = new TreeMap<Integer, Optional>();

		Integer[] currentType = (Integer[]) this.table.currentType.toArray(new Integer[this.table.currentType.size()]);

		for (int i = 0; i < len; i++) {
			Integer hash = currentType[2 * i];
			if (fs.put(hash, Optional.empty()) != null)
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, String.format("hash collision %d", hash));
		}

		Map<Object, Object> map = new TreeMap<Object, Object>();
		
		Map<Integer, Label> labels = new TreeMap<Integer, Label>();
		
		if(typeMap.isPresent())
			for(Label label : typeMap.get().keySet())
				labels.put(label.getId(), label);

		for (int i = 0; i < len; i++) {
			Object key;

			Integer hash = this.table.popCurrentType();

			Optional field = fs.get(hash);

			if (field.isPresent())
				key = field.get();
			else
				key = hash;
			
			// assign named Label, if exists
			if(typeMap.isPresent())
				if(labels.containsKey(key))
				{
					Label label = labels.get(key);
					this.expectedType = Optional.ofNullable(typeMap.get().get(label));
					
					key = label.getValue();
				}
				else this.expectedType = Optional.empty();

			IDLValue idlValue = this.deserializeAny();
			
			this.expectedType = expectedType;

			map.put(key, idlValue.getValue());
		}

		this.recordNestingDepth = oldNesting;
		
		this.expectedType = expectedType;

		return IDLValue.create(map);
	}
	
	public IDLValue deserializeVariant()
	{
		this.recordNestingDepth = 0;
		
		this.table.checkType(Opcode.VARIANT);
		
		Optional<IDLType> expectedType = this.expectedType;
		Optional<Map<Label,IDLType>> typeMap = Optional.empty();
		
		if(expectedType.isPresent())
			if(expectedType.get().getType() != Type.VARIANT)
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, String.format("Not an expected type"));
			else
				typeMap = Optional.ofNullable(expectedType.get().getTypeMap());

		int len = this.table.popCurrentType();

		Map<Integer, Optional> fs = new TreeMap<Integer, Optional>();

		Integer[] currentType = (Integer[]) this.table.currentType.toArray(new Integer[this.table.currentType.size()]);

		for (int i = 0; i < len; i++) {
			Integer hash = currentType[2 * i];
			if (fs.put(hash, Optional.empty()) != null)
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, String.format("hash collision %d", hash));
		}
		
		Map<Object, Object> map = new TreeMap<Object, Object>();
		
		int idx = this.input.leb128Read();

		for (int i = 0; i < len; i++) {			
			Object key;

			Integer hash = this.table.popCurrentType();
			
			if(i == idx)
			{	
				Optional field = fs.get(hash);
	
				if (field.isPresent())
					key = field.get();
				else
					key = hash;
				
				// assign named Label, if exists
				if(typeMap.isPresent() && typeMap.get().keySet().iterator().hasNext())
				{
					Label label = typeMap.get().keySet().iterator().next();
					this.expectedType = Optional.ofNullable(typeMap.get().get(label));
							
					key = label.getValue();
				}
				else 
					this.expectedType = Optional.empty();
	
				IDLValue idlValue = this.deserializeAny();
				
				this.expectedType = expectedType;
	
				map.put(key, idlValue.getValue());
			}
		}
		
		this.expectedType = expectedType;

		if(this.expectedType.isPresent())
			return IDLValue.create(map, this.expectedType.get());
		else
			return IDLValue.create(map, Type.VARIANT);
	}

	public IDLValue deserializePrincipal() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.PRINCIPAL);

		byte bit = this.input.parseByte();

		if (bit != (byte) 1)
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM,
					String.format("Opaque reference not supported"));

		int len = this.input.leb128Read();

		byte[] bytes = this.input.parseBytes(len);

		Principal value = Principal.from(bytes);

		return IDLValue.create(value);

	}

	public IDLValue deserializeReserved() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.RESERVED);

		// TODO need to figure out deserializing reserve value, for now we create emply
		// Object
		return IDLValue.create(new Object(), Type.RESERVED);
	}

	public IDLValue deserializeIdentifier() {
		return null;
	}

	Object[] toArray(List value) {
		Object[] array;

		Integer ty = this.table.popCurrentType();

		int size = value.size();

		if(ty >= 0)
		{
			array = value.toArray(new Object[size]);
			
			this.table.currentType.addFirst(ty);
			
			return array;
		}
		
		Opcode type = Opcode.from(ty);

		switch (type) {
		case BOOL:
			array = value.toArray(new Boolean[size]);
			break;
		case NAT:
			array = value.toArray(new BigInteger[size]);
			break;
		case NAT8:
			array = value.toArray(new Byte[size]);
			break;
		case NAT16:
			array = value.toArray(new Short[size]);
			;
			break;
		case NAT32:
			array = value.toArray(new Integer[size]);
			break;
		case NAT64:
			array = value.toArray(new Long[size]);
			;
			break;
		case INT:
			array = value.toArray(new BigInteger[size]);
			break;
		case INT8:
			array = value.toArray(new Byte[size]);
			break;
		case INT16:
			array = value.toArray(new Short[size]);
			;
			break;
		case INT32:
			array = value.toArray(new Integer[size]);
			break;
		case INT64:
			array = value.toArray(new Long[size]);
			;
			break;
		case FLOAT32:
			array = value.toArray(new Float[size]);
			break;
		case FLOAT64:
			array = value.toArray(new Double[size]);
			break;
		case TEXT:
			array = value.toArray(new String[size]);
			break;
		case OPT:
			array = value.toArray(new Optional[size]);
			break;
		case VEC:
			for (Object element : value) {
				// TODO implement casting of nested arrays
				// for now we cast to Object
			}
			array = value.toArray(new Object[size]);
			break;
		case PRINCIPAL:
			array = value.toArray(new Principal[size]);
			break;
		default:
			array = value.toArray(new Object[size]);
		}

		this.table.currentType.addFirst(ty);

		return array;
	}

}
