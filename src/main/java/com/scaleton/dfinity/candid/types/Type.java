package com.scaleton.dfinity.candid.types;

public enum Type {
	NULL,
	BOOL,
	NAT,
	INT,
	NAT8,
	NAT16,
	NAT32,
	NAT64,
	INT8,
	INT16,
	INT32,
	INT64,
	FLOAT32,
	FLOAT64,
	TEXT,
	RESERVED,
	EMPTY,
	OPT,
	VEC,
	RECORD,
	VARIANT
	;	
	
	Object value;
	
	Type(Object value) {
		this.value = value;
	}

	Type() {
	}	

}
