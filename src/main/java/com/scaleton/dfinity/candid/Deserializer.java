package com.scaleton.dfinity.candid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Optional;

import com.scaleton.dfinity.candid.parser.IDLValue;
import com.scaleton.dfinity.candid.types.Numbers;
import com.scaleton.dfinity.candid.types.Opcode;

public final class Deserializer{
	Bytes input;
	TypeTable table;

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
			// TODO implement identifier deserializer
		}

		Opcode type = this.table.peekType();

		if (type != Opcode.RECORD)
			this.recordNestingDepth = 0;

		switch (type) {
		case BOOL:
			return this.deserializeBool();	
		case INT:			
			return this.deserializeInt();
		case INT8:			
			return this.deserializeByte();
		case INT16:			
			return this.deserializeShort();
		case INT32:			
			return this.deserializeInteger();
		case INT64:			
			return this.deserializeLong();			
		case FLOAT32:
			return this.deserializeFloat();			
		case FLOAT64:
			return this.deserializeDouble();			
		case TEXT:
			return this.deserializeString();
		default:
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, String.format("Unrecogized type %d", type.value));
		}

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

	public IDLValue deserializeString() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.TEXT);
		int len = this.input.leb128Read();

		String value = this.input.parseString(len);

		return IDLValue.create(value);

	}
	
	public IDLValue deserializeInt() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.INT);

		Integer value =  Numbers.decodeInt(input);

		return IDLValue.create(value);

	}
	
	public IDLValue deserializeDouble() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.FLOAT64);

		Double value =  ByteBuffer.wrap(input.parseBytes(Double.BYTES)).order(ByteOrder.LITTLE_ENDIAN).getDouble();

		return IDLValue.create(value);

	}	
	
	public IDLValue deserializeFloat() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.FLOAT32);

		Float value =  ByteBuffer.wrap(input.parseBytes(Float.BYTES)).order(ByteOrder.LITTLE_ENDIAN).getFloat();

		return IDLValue.create(value);
	}
	
	public IDLValue deserializeByte() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.INT8);

		Byte value = input.parseByte();

		return IDLValue.create(value);
	}
	
	public IDLValue deserializeShort() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.INT16);

		Short value =  ByteBuffer.wrap(input.parseBytes(Short.BYTES)).order(ByteOrder.LITTLE_ENDIAN).getShort();

		return IDLValue.create(value);
	}

	public IDLValue deserializeInteger() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.INT32);

		Integer value =  ByteBuffer.wrap(input.parseBytes(Integer.BYTES)).order(ByteOrder.LITTLE_ENDIAN).getInt();

		return IDLValue.create(value);
	}
	
	public IDLValue deserializeLong() {
		this.recordNestingDepth = 0;
		this.table.checkType(Opcode.INT64);

		Long value =  ByteBuffer.wrap(input.parseBytes(Long.BYTES)).order(ByteOrder.LITTLE_ENDIAN).getLong();

		return IDLValue.create(value);
	}	

}
