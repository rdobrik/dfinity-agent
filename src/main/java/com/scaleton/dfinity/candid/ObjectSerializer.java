package com.scaleton.dfinity.candid;

import com.scaleton.dfinity.candid.parser.IDLValue;

public interface ObjectSerializer {
	
	public IDLValue serialize(Object value);
}
