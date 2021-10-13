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

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

import com.scaleton.dfinity.types.Principal;

public interface Serializer {
	public void serializeNull();
	
	public void serializeBool(Boolean value);
	
	public void serializeText(String value);
	
	public void serializeNat(BigInteger value);
	
	public void serializeNat8(Byte value);
	
	public void serializeNat16(Short value);
	
	public void serializeNat32(Integer value);
	
	public void serializeNat64(Long value);		
	
	public void serializeInt(BigInteger value);

	public void serializeFloat64(Double value);
	
	public void serializeFloat32(Float value);
	
	public void serializeInt8(Byte value);
	
	public void serializeInt16(Short value);
	
	public void serializeInt32(Integer value);
	
	public void serializeInt64(Long value);	
	
	public void serializeOpt(Optional<?> value);
	
	public <T> void serializeVec(T[] value);	
	
	public void serializeRecord(Object value);
	
	public void serializeVariant(Object value);
	
	public void serializePrincipal(Principal value);

}
