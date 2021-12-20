package com.scaleton.dfinity.candid;

import com.scaleton.dfinity.candid.parser.IDLValue;

public interface ObjectDeserializer {
	
	public <T> T deserialize(IDLValue value, Class<T> clazz);

}
