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

package com.scaleton.dfinity.candid.pojo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import com.scaleton.dfinity.candid.CandidError;
import com.scaleton.dfinity.candid.ObjectSerializer;
import com.scaleton.dfinity.candid.annotations.Ignore;
import com.scaleton.dfinity.candid.annotations.Name;
import com.scaleton.dfinity.candid.parser.IDLType;
import com.scaleton.dfinity.candid.parser.IDLValue;
import com.scaleton.dfinity.candid.types.Label;
import com.scaleton.dfinity.candid.types.Type;

public class PojoSerializer implements ObjectSerializer {
	
	public static PojoSerializer create() {
		PojoSerializer deserializer = new PojoSerializer();
		return deserializer; 
	}	

	@Override
	public IDLValue serialize(Object value) {
		
		// handle null values
		if(value == null)
			return IDLValue.create(value,Type.NULL);
		
		boolean isArray = value.getClass().isArray();
		boolean isOptional = Optional.class.isAssignableFrom(value.getClass());	
		
		// handle default types
		if(IDLType.isDefaultType(value.getClass()) && !isArray && !isOptional)
			return IDLValue.create(value);
		
		IDLValue idlValue;
		
		// handle arrays
		if(value.getClass().isArray())
		{
			List<Map<String, Object>> arrayValue = new ArrayList();
			
			Object[] array = (Object[]) value;
			
			IDLType idlType = IDLType.createType(Type.VEC);
			
			for(int i = 0; i < array.length; i++)
			{
				Object item = array[i];
				
				IDLValue itemIDLValue = this.getIDLValue(item);
				
				arrayValue.add(itemIDLValue.getValue());
				
				idlType = IDLType.createType(Type.VEC, itemIDLValue.getIDLType());
			}
			
			
			idlValue = IDLValue.create(arrayValue.toArray(), idlType);
		}
		else
		{			
			idlValue = this.getIDLValue(value);
		}

		return idlValue;
	}
	
	// Introspect POJO Class
	IDLValue getIDLValue(Object value)
	{
		// handle null values
		if(value == null)
			return IDLValue.create(value,Type.NULL);
			
		Class valueClass = value.getClass();
		
		if(Optional.class.isAssignableFrom(valueClass))
		{
			Optional optionalValue = (Optional)value;
			
			if(optionalValue.isPresent())
			{
				Object nestedValue = optionalValue.get();
				
				if(IDLType.isDefaultType(nestedValue.getClass()))
					return IDLValue.create(optionalValue);
				else
				{
					IDLValue nestedIdlValue = this.getIDLValue(nestedValue);
					
					IDLType nestedIdlType = nestedIdlValue.getIDLType();
					return IDLValue.create(Optional.ofNullable(nestedIdlValue.getValue()), IDLType.createType(Type.OPT, nestedIdlType));
				}
			}else return IDLValue.create(optionalValue);
		}
		
		Map<String, Object> valueMap =  new TreeMap<String, Object>();
		Map<Label,IDLType> typeMap = new TreeMap<Label,IDLType>();
		
		Field[] fields = valueClass.getDeclaredFields();
		
		for(Field field : fields)
		{
			if(field.isAnnotationPresent(Ignore.class))
				continue;
			
			String name = field.getName();
			Class typeClass = field.getType();
			
			Object item = null;
			try {
				item = field.get(value);
			} catch (IllegalArgumentException e) {
				continue;
			} catch (IllegalAccessException e) {
				continue;
			}
			
			IDLType fieldType;
			
			if(field.isAnnotationPresent(Name.class))
				name = field.getAnnotation(Name.class).value();
			
			if(item == null)
			{
				// set NULL value
				fieldType = IDLType.createType(Type.NULL);
				
				typeMap.put(Label.createNamedLabel((String)name),fieldType);	
				valueMap.put(name, item);
				continue;
			}
			
			boolean isArray = typeClass.isArray();
			boolean isOptional = Optional.class.isAssignableFrom(typeClass);
			
			if(field.isAnnotationPresent(com.scaleton.dfinity.candid.annotations.Field.class))
				fieldType = IDLType.createType(field.getAnnotation(com.scaleton.dfinity.candid.annotations.Field.class).value());
			else if(IDLType.isDefaultType(typeClass))
			{
				// if we do not specify type in annotation and type is one of default
				fieldType = IDLType.createType(item);
				typeMap.put(Label.createNamedLabel((String)name), fieldType);	
				valueMap.put(name, item);
				continue;
			}
			else
				fieldType = IDLType.createType(Type.RECORD);
			
			// do nested type introspection if type is RECORD		
			if(fieldType.getType() == Type.RECORD)
			{
				
				// handle RECORD arrays
				if(isArray)
				{
					Object[] nestedArray = (Object[])item;
					List<Object> arrayValue = new ArrayList();
					
					fieldType = this.getIDLType(typeClass.getComponentType());
					for(int i = 0; i < nestedArray.length; i++)
					{
						Object nestedValue = nestedArray[i];
						// if nested RECORD is Optional 
						if(nestedValue != null && Optional.class.isAssignableFrom(nestedValue.getClass()))
							nestedValue = ((Optional)nestedValue).orElse(null);
						
						IDLValue nestedIdlValue = this.getIDLValue(nestedValue);
						
						fieldType = nestedIdlValue.getIDLType();
						
						arrayValue.add(nestedIdlValue.getValue());
					}
					
					fieldType = IDLType.createType(Type.VEC, fieldType);
					item = arrayValue.toArray();
				}
				else
				{
					Object nestedValue = item;
					// if nested RECORD is Optional 
					if(item != null && Optional.class.isAssignableFrom(typeClass))
						nestedValue = ((Optional)item).orElse(null);
					
					IDLValue nestedIdlValue = this.getIDLValue(nestedValue);
					
					fieldType = nestedIdlValue.getIDLType();
					
					item = nestedIdlValue.getValue();
				}
			}else if(isArray)
			{
				// handle arrays , not record types
				fieldType = IDLType.createType(Type.VEC, fieldType);
			}else if(isOptional)
			{
				// handle Optional, not record types
				
				fieldType = IDLType.createType(Type.OPT, fieldType);
			}
			
			typeMap.put(Label.createNamedLabel((String)name), fieldType);	
			valueMap.put(name, item);
				
		}	
		
		IDLType idlType = IDLType.createType(Type.RECORD, typeMap);
		IDLValue idlValue = IDLValue.create(valueMap, idlType);
		
		return idlValue;
	}
	
	IDLType getIDLType(Class valueClass)
	{
		// handle null values
		if(valueClass == null)
			return IDLType.createType(Type.NULL);
		
		if(IDLType.isDefaultType(valueClass))
			return IDLType.createType(valueClass);
			
		
		if(Optional.class.isAssignableFrom(valueClass))
			return IDLType.createType(Type.OPT);

		Map<Label,IDLType> typeMap = new TreeMap<Label,IDLType>();

		Field[] fields = valueClass.getFields();		
		
		for(Field field : fields)
		{
			if(field.isAnnotationPresent(Ignore.class))
				continue;
			
			String name = field.getName();
			Class typeClass = field.getType();	
			
			IDLType fieldType = this.getIDLType(typeClass);
			
			if(field.isAnnotationPresent(Name.class))
				name = field.getAnnotation(Name.class).value();
						
			boolean isArray = typeClass.isArray();
			boolean isOptional = Optional.class.isAssignableFrom(typeClass);
			
			if(field.isAnnotationPresent(com.scaleton.dfinity.candid.annotations.Field.class))
				fieldType = IDLType.createType(field.getAnnotation(com.scaleton.dfinity.candid.annotations.Field.class).value());
			else if(IDLType.isDefaultType(typeClass))
			{
				// if we do not specify type in annotation and type is one of default
				typeMap.put(Label.createNamedLabel((String)name), fieldType);	
				continue;
			}
			else
				fieldType = IDLType.createType(Type.RECORD);
			
			// do nested type introspection if type is RECORD		
			if(fieldType.getType() == Type.RECORD)
			{
				
				// handle RECORD arrays
				if(isArray)
				{
					IDLType nestedIdlType = 
					fieldType = IDLType.createType(Type.VEC, fieldType);
				}

			}else if(isArray)
			{
				// handle arrays , not record types
				fieldType = IDLType.createType(Type.VEC, fieldType);
			}else if(isOptional)
			{
				// handle Optional, not record types
				
				fieldType = IDLType.createType(Type.OPT, fieldType);
			}
			
			typeMap.put(Label.createNamedLabel((String)name), fieldType);	

		}	
		
		IDLType idlType = IDLType.createType(Type.RECORD, typeMap);
		
		return idlType;		
	}
	

}
