package com.scaleton.dfinity.candid.parser;

import java.util.Optional;

import com.scaleton.dfinity.candid.Deserialize;
import com.scaleton.dfinity.candid.Deserializer;
import com.scaleton.dfinity.candid.Serializer;
import com.scaleton.dfinity.candid.types.Type;

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
		
		if(value == null)
		{	
			
			idlValue.type = Type.NULL;
		}	
		else
		{	
			if(value instanceof Boolean)
				idlValue.type = Type.BOOL;		
			else if(value instanceof Integer)				
				idlValue.type =  Type.INT;
			else if(value instanceof Byte)
				idlValue.type =  Type.INT8;			
			else if(value instanceof Short)
				idlValue.type =  Type.INT16;			
			else if(value instanceof Long)
				idlValue.type =  Type.INT64;
			else if(value instanceof Float)
				idlValue.type =  Type.FLOAT32;			
			else if(value instanceof Double)
				idlValue.type =  Type.FLOAT64;				
			else if(value instanceof String)
				idlValue.type = Type.TEXT;
			else
				idlValue.type =  Type.NULL;	
		}	
		
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
