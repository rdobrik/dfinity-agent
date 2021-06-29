package com.scaleton.dfinity.candid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.scaleton.dfinity.candid.parser.IDLValue;

public final class IDLBuilder {
	//List<IDLValue<?>> values = new ArrayList<IDLValue<?>>();
	
	ValueSerializer valueSer = new ValueSerializer();
	TypeSerialize typeSer = new TypeSerialize();
	
	public void valueArg(IDLValue value)
	{
		this.typeSer.pushType(value.getType());
		
		value.idlSerialize(valueSer);
	}
	
	public byte[] serializeToVec()
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		try {
			this.serialize(os);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return os.toByteArray();
	}
	
	public void serialize(OutputStream os) throws IOException
	{
		os.write("DIDL".getBytes());
		
		this.typeSer.serialize();
		
		os.write(this.typeSer.getResult());
		
		os.write(this.valueSer.getResult());
	}

}
