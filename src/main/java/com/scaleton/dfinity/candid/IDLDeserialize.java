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

import java.util.Optional;

import com.scaleton.dfinity.candid.parser.IDLType;

public final class IDLDeserialize {
	Deserializer de;
	
	public IDLDeserialize(Deserializer de) {
		this.de = de;
	}

	public static IDLDeserialize create(byte[] bytes)
	{
		Deserializer de = Deserializer.fromBytes(bytes);
		
		return new IDLDeserialize(de);
		
	}
	
	public <T extends Deserialize> T getValue(Class<T> clazz)
	{
		T value;

			try {
				value = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, String.format("Cannot initialize class %s"),clazz.getCanonicalName());
			}

		
		Integer ty = this.de.table.types.poll();
		
		if(ty == null)
			CandidError.create(CandidError.CandidErrorCode.CUSTOM, "No more values to deserialize");
		
		this.de.table.currentType.add(ty);
		
		value.deserialize(this.de);
		
		if(this.de.table.currentType.isEmpty() && !this.de.fieldName.isPresent())
			return value;
		else
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Trailing type after deserializing a value");
		
	}
	
	public void setExpectedType(IDLType expectedType)
	{
		this.de.expectedType = Optional.of(expectedType);
	}
	
	public boolean isDone()
	{
		return this.de.table.types.isEmpty();
	}
	
	public void done()
	{
		if(this.de.input.data.hasRemaining())
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Trailing type after deserializing a value");
	}

}
