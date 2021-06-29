package com.scaleton.dfinity.candid;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.scaleton.dfinity.candid.types.Opcode;
import com.scaleton.dfinity.candid.types.Type;

public final class TypeSerialize {
	List<byte[]> typeTable;
	List<Type> args;
	
	byte[] result;
	
	TypeSerialize()
	{
		this.typeTable = new ArrayList<byte[]>();
		this.args = new ArrayList<Type>();
		this.result = ArrayUtils.EMPTY_BYTE_ARRAY;
	}
	
	void pushType(Type type) {
		this.args.add(type);
		
	}	
	
	byte[] encode(Type type)
	{
		switch(type)
		{
		case NULL: return Leb128.writeSigned(Opcode.NULL.value);
		case BOOL: return Leb128.writeSigned(Opcode.BOOL.value);
		case NAT: return Leb128.writeSigned(Opcode.NAT.value);		
		case INT: return Leb128.writeSigned(Opcode.INT.value);
		case NAT8: return Leb128.writeSigned(Opcode.NAT8.value);
		case NAT16: return Leb128.writeSigned(Opcode.NAT16.value);
		case NAT32: return Leb128.writeSigned(Opcode.NAT32.value);
		case NAT64: return Leb128.writeSigned(Opcode.NAT64.value);			
		case INT8: return Leb128.writeSigned(Opcode.INT8.value);
		case INT16: return Leb128.writeSigned(Opcode.INT16.value);
		case INT32: return Leb128.writeSigned(Opcode.INT32.value);
		case INT64: return Leb128.writeSigned(Opcode.INT64.value);	
		case FLOAT32: return Leb128.writeSigned(Opcode.FLOAT32.value);
		case FLOAT64: return Leb128.writeSigned(Opcode.FLOAT64.value);		
		case TEXT: return Leb128.writeSigned(Opcode.TEXT.value);
		default:
			return ArrayUtils.EMPTY_BYTE_ARRAY;	
		}
	}
	
	void serialize()
	{		
		this.result = ArrayUtils.addAll(this.result, Leb128.writeUnsigned(this.typeTable.size()));
		
		// TODO serialize content of type table
		
		this.result = ArrayUtils.addAll(this.result, Leb128.writeUnsigned(this.args.size()));
		
		byte[] tyEncode = ArrayUtils.EMPTY_BYTE_ARRAY;
		
		for(Type type : args)
		{
			tyEncode = ArrayUtils.addAll(tyEncode,this.encode(type));
		}
		
		this.result = ArrayUtils.addAll(this.result,tyEncode);
	}
	
	byte[] getResult()
	{
		return this.result;
	}


	
}
