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

import com.scaleton.dfinity.types.Principal;

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
	
	public void serializeOpt(Optional<?> value);
	
	public <T> void serializeVec(T[] value);	
	
	public void serializePrincipal(Principal value);

}
