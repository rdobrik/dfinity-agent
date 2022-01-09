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

package com.scaleton.dfinity.candid.jackson;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scaleton.dfinity.candid.CandidError;
import com.scaleton.dfinity.candid.ObjectSerializer;
import com.scaleton.dfinity.candid.parser.IDLType;
import com.scaleton.dfinity.candid.parser.IDLValue;
import com.scaleton.dfinity.candid.types.Label;
import com.scaleton.dfinity.candid.types.Type;
import com.scaleton.dfinity.types.Principal;

public class JacksonSerializer implements ObjectSerializer {
	Optional<IDLType> idlType = Optional.empty();
	
	ObjectMapper mapper = new ObjectMapper();

	public static JacksonSerializer create(IDLType idlType) {
		JacksonSerializer deserializer = new JacksonSerializer();
		deserializer.idlType = Optional.ofNullable(idlType);
		return deserializer;

	}

	public static JacksonSerializer create() {
		JacksonSerializer deserializer = new JacksonSerializer();
		return deserializer;
	}

	@Override
	public IDLValue serialize(Object value) {
		if (value == null)
			return IDLValue.create(value);

		if (JsonNode.class.isAssignableFrom(value.getClass()))
			return this.getIDLValue(this.idlType, (JsonNode) value);
		else
		{
			try {
				JsonNode jsonNode = mapper.convertValue(value, JsonNode.class);
				return this.getIDLValue(this.idlType, jsonNode);				
			}catch (Exception e)
			{
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM,e,
						e.getLocalizedMessage());
			}
		}	
	}

	IDLValue getPrimitiveIDLValue(Type type, JsonNode value) {
		IDLValue result = IDLValue.create(null);

		if (value == null)
			return result;

		switch (type) {
		case BOOL:
			result = IDLValue.create(value.booleanValue(), type);
			break;
		case INT:
			result = IDLValue.create(value.bigIntegerValue(), type);
			break;
		case INT8:
			result = IDLValue.create(Short.valueOf(value.shortValue()).byteValue(), type);
			break;
		case INT16:
			result = IDLValue.create(value.shortValue(), type);
			break;
		case INT32:
			result = IDLValue.create(value.intValue(), type);
			break;
		case INT64:
			result = IDLValue.create(value.longValue(), type);
			break;
		case NAT:
			result = IDLValue.create(value.bigIntegerValue(), type);
			break;
		case NAT8:
			result = IDLValue.create(Short.valueOf(value.shortValue()).byteValue(), type);
			break;
		case NAT16:
			result = IDLValue.create(value.shortValue(), type);
			break;
		case NAT32:
			result = IDLValue.create(value.intValue(), type);
			break;
		case NAT64:
			result = IDLValue.create(value.longValue(), type);
			break;
		case FLOAT32:
			result = IDLValue.create(value.floatValue(), type);
			break;
		case FLOAT64:
			result = IDLValue.create(value.doubleValue(), type);
			break;
		case TEXT:
			result = IDLValue.create(value.textValue(), type);
			break;
		case PRINCIPAL:
			result = IDLValue.create(Principal.fromString(value.textValue()));
			break;
		case EMPTY:
			result = IDLValue.create(null, type);
		case NULL:
			result = IDLValue.create(null, type);
			break;
		}

		return result;
	}

	Type getType(JsonNode value) {
		if (value == null)
			return Type.NULL;

		if (value.isBoolean())
			return Type.BOOL;
		else if (value.isShort())
			return Type.INT16;
		else if (value.isInt())
			return Type.INT32;
		else if (value.isLong())
			return Type.INT64;
		else if (value.isBigInteger())
			return Type.INT;
		else if (value.isFloat())
			return Type.FLOAT32;
		else if (value.isDouble())
			return Type.FLOAT64;
		else if (value.isTextual())
			return Type.TEXT;
		else if (value.isArray() || value.isBinary())
			return Type.VEC;
		else if (value.isObject() || value.isPojo())
			return Type.RECORD;
		else if (value.isEmpty())
			return Type.EMPTY;
		else if (value.isNull())
			return Type.NULL;
		else
			return Type.NULL;
	}

	IDLValue getIDLValue(Optional<IDLType> expectedIdlType, JsonNode value) {
		// handle null values
		if (value == null)
			return IDLValue.create(value, Type.NULL);

		Type type;
		if (expectedIdlType.isPresent())
			type = expectedIdlType.get().getType();
		else
			type = this.getType(value);

		// handle primitives

		if (type.isPrimitive())
			return this.getPrimitiveIDLValue(type, value);

		// handle arrays
		if (type == Type.VEC) {
			IDLType innerIDLType = IDLType.createType(Type.NULL);

			if (expectedIdlType.isPresent())
				innerIDLType = expectedIdlType.get().getInnerType();

			if (value.isBinary()) {
				if (!expectedIdlType.isPresent())
					innerIDLType = IDLType.createType(Type.INT8);

				return IDLValue.create(value, IDLType.createType(type, innerIDLType));
			}

			if (value.isArray()) {
				ArrayNode arrayNode = (ArrayNode) value;
				Object[] arrayValue = new Object[arrayNode.size()];

				for (int i = 0; i < arrayNode.size(); i++) {
					IDLValue item = this.getIDLValue(Optional.ofNullable(innerIDLType), arrayNode.get(i));

					arrayValue[i] = item.getValue();
					if (!expectedIdlType.isPresent())
						innerIDLType = item.getIDLType();
				}

				IDLType idlType;

				if (expectedIdlType.isPresent())
					idlType = expectedIdlType.get();
				else
					idlType = IDLType.createType(Type.VEC, innerIDLType);

				return IDLValue.create(arrayValue, idlType);
			}

			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM,
					"Cannot convert class " + value.getClass().getName() + " to VEC");

		}

		// handle Objects
		if (type == Type.RECORD || type == Type.VARIANT) {
			ObjectNode objectNode = (ObjectNode) value;

			Map<String, Object> valueMap = new TreeMap<String, Object>();
			Map<Label, IDLType> typeMap = new TreeMap<Label, IDLType>();
			Map<Label, IDLType> expectedTypeMap = new TreeMap<Label, IDLType>();

			if (expectedIdlType.isPresent())
				expectedTypeMap = expectedIdlType.get().getTypeMap();

			Iterator<String> fieldNames = objectNode.fieldNames();

			while (fieldNames.hasNext()) {
				String name = fieldNames.next();

				JsonNode item = objectNode.get(name);

				IDLType expectedItemIdlType;

				if (expectedIdlType.isPresent() && expectedTypeMap != null)
					expectedItemIdlType = expectedTypeMap.get(Label.createNamedLabel(name));
				else
					expectedItemIdlType = IDLType.createType(this.getType(item));

				if (expectedItemIdlType == null)
					continue;

				IDLValue itemIdlValue = this.getIDLValue(Optional.ofNullable(expectedItemIdlType), item);

				typeMap.put(Label.createNamedLabel((String) name), itemIdlValue.getIDLType());
				valueMap.put(name, itemIdlValue.getValue());
			}

			IDLType idlType = IDLType.createType(Type.RECORD, typeMap);
			IDLValue idlValue = IDLValue.create(valueMap, idlType);

			return idlValue;
		}

		throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Cannot convert type " + type.name());

	}
}
