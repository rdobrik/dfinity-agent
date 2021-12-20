package com.scaleton.dfinity.test;

import java.math.BigInteger;

import com.scaleton.dfinity.candid.annotations.Field;
import com.scaleton.dfinity.candid.annotations.Name;
import com.scaleton.dfinity.candid.types.Type;

public class ComplexArrayPojo {
	@Field(Type.BOOL)
	@Name("bar")
	public Boolean[] bar;
	
	@Field(Type.INT)
	@Name("foo")
	public BigInteger[] foo;
	
	@Field(Type.RECORD)
	@Name("pojo")
	public Pojo[] pojo;
	
}
