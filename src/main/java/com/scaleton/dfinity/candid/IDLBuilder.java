/*
 * Copyright 2021 Exilor Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.scaleton.dfinity.candid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.scaleton.dfinity.candid.parser.IDLValue;

public final class IDLBuilder {
	//List<IDLValue<?>> values = new ArrayList<IDLValue<?>>();
	
	ValueSerializer valueSer = new ValueSerializer();
	TypeSerialize typeSer = new TypeSerialize();
	
	public void valueArg(IDLValue value)
	{
		this.typeSer.pushType(value.getIDLType());
		
		value.idlSerialize(valueSer);
	}
	
	public <T> void arg(T value)
	{
		IDLValue idlValue = IDLValue.create(value);
		
		this.typeSer.pushType(idlValue.getIDLType());
		
		idlValue.idlSerialize(valueSer);
	}	
	
	public byte[] serializeToVec()
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		try {
			this.serialize(os);
		} catch (IOException e) {
			throw CandidError.create(CandidError.CandidErrorCode.PARSE, e, e.getLocalizedMessage());
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
