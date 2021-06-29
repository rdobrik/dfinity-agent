package com.scaleton.dfinity.candid;

public interface Serializer {
	public void serializeNull();
	
	public void serializeBool(Boolean value);
	
	public void serializeString(String value);
	
	public void serializeInt(Integer value);

	public void serializeDouble(Double value);
	
	public void serializeFloat(Float value);
	
	public void serializeByte(Byte value);
	
	public void serializeShort(Short value);
	
	public void serializeInteger(Integer value);
	
	public void serializeLong(Long value);	

}
