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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.scaleton.dfinity.candid.CandidError;
import com.scaleton.dfinity.candid.IDLUtils;
import com.scaleton.dfinity.candid.ObjectDeserializer;
import com.scaleton.dfinity.candid.annotations.Name;
import com.scaleton.dfinity.candid.parser.IDLType;
import com.scaleton.dfinity.candid.parser.IDLValue;
import com.scaleton.dfinity.candid.types.Type;

public class PojoDeserializer implements ObjectDeserializer {

	public static PojoDeserializer create() {
		PojoDeserializer deserializer = new PojoDeserializer();
		return deserializer;
	}

	@Override
	public <T> T deserialize(IDLValue idlValue, Class<T> clazz) {

		if (idlValue == null)
			return null;

		if (idlValue.getType().isPrimitive()) {
			return idlValue.getValue();
		}

		// handle OPT
		if (idlValue.getType() == Type.OPT) {
			Optional optionalValue = (Optional) idlValue.getValue();
			// if innerType is primitive
			if (idlValue.getIDLType().getInnerType().getType().isPrimitive())
				return (T) optionalValue.orElse(null);
			else {
				T result = null;

				if (optionalValue.isPresent())
					result = this.getValue(optionalValue.get(), clazz);

				return result;
			}

		}

		// handle arrays
		if (idlValue.getType() == Type.VEC) {
			// if innerType is primitive
			if (IDLType.isPrimitiveType(clazz))
				return idlValue.getValue();
			else if (idlValue.getValue().getClass().isArray()) {
				Object[] array = idlValue.getValue();

				Object[] result = (Object[]) Array.newInstance(clazz.getComponentType(), array.length);

				for (int i = 0; i < array.length; i++) {
					result[i] = this.getValue(array[i], clazz.getComponentType());
				}

				return (T) result;
			}
		}

		// handle RECORD and VARIANT
		if (idlValue.getType() == Type.RECORD || idlValue.getType() == Type.VARIANT) {
			return (T) this.getValue(idlValue.getValue(), clazz);
		}

		return idlValue.getValue();
	}

	<T> T getValue(Object value, Class<T> clazz) {
		if (value == null)
			return null;

		if (IDLType.isPrimitiveType(value.getClass()))
			return (T) value;

		// handle arrays
		if (value.getClass().isArray()) {
			List<T> arrayValue = new ArrayList();

			Object[] array = (Object[]) value;

			for (Object item : array) {
				arrayValue.add((T) this.getValue(item, clazz.getComponentType()));
			}

			return (T) arrayValue.toArray();
		}

		// handle Optional
		if (Optional.class.isAssignableFrom(value.getClass())) {
			Optional optionalValue = (Optional) value;

			if (optionalValue.isPresent()) {
				value = this.getValue(optionalValue.get(), clazz);
				return (T) Optional.ofNullable(value);
			} else
				return (T) optionalValue;
		}

		// handle Map, match with clazz type
		if (Map.class.isAssignableFrom(value.getClass())) {
			T pojoValue;
			try {
				pojoValue = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, e, e.getLocalizedMessage());
			}

			Map<Integer, Object> valueMap = (Map<Integer, Object>) value;

			Field[] fields = clazz.getDeclaredFields();

			for (Field field : fields) {
				String name = field.getName();
				Class typeClass = field.getType();

				if (field.isAnnotationPresent(Name.class))
					name = field.getAnnotation(Name.class).value();

				Object item = valueMap.get(IDLUtils.idlHash(name));

				if (!IDLType.isDefaultType(typeClass))
					item = this.getValue(item, typeClass);

				try {
					// convert to proper type
					if(item != null && item.getClass().isArray())
						item = IDLUtils.toArray(typeClass, (Object[]) item);
					
					field.set(pojoValue, item);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					continue;
				}
			}

			return (T) pojoValue;

		}

		throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Undefined type " + clazz.getName());
	}


}
