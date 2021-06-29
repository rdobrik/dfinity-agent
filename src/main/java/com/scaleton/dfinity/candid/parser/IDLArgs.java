package com.scaleton.dfinity.candid.parser;

import java.util.ArrayList;
import java.util.List;

import com.scaleton.dfinity.candid.IDLBuilder;
import com.scaleton.dfinity.candid.IDLDeserialize;

public final class IDLArgs {
	List<IDLValue> args;

	IDLArgs(List<IDLValue> args) {
		this.args = args;
	}

	public static IDLArgs create(List<IDLValue> args) {
		return new IDLArgs(args);
	}

	public byte[] toBytes() {
		IDLBuilder idl = new IDLBuilder();

		for (IDLValue arg : args) {
			idl.valueArg(arg);

		}

		return idl.serializeToVec();

	}
	
	public static IDLArgs fromBytes(byte[] bytes)
	{
		IDLDeserialize de = IDLDeserialize.create(bytes);
		
		List<IDLValue> args = new ArrayList<IDLValue>();
		
		while(!de.isDone())
		{
			IDLValue value = de.getValue(IDLValue.class);
			args.add(value);
		}
		
		
		return new IDLArgs(args);			
	}
	
	public List<IDLValue> getArgs()
	{
		return this.args;
	}
}
