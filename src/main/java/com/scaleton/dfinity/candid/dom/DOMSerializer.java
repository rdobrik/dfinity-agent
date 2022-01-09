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

package com.scaleton.dfinity.candid.dom;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.scaleton.dfinity.candid.CandidError;
import com.scaleton.dfinity.candid.ObjectSerializer;
import com.scaleton.dfinity.candid.parser.IDLType;
import com.scaleton.dfinity.candid.parser.IDLValue;
import com.scaleton.dfinity.candid.types.Label;
import com.scaleton.dfinity.candid.types.Type;
import com.scaleton.dfinity.types.Principal;

public class DOMSerializer extends DOMSerDeserBase implements ObjectSerializer {

	public static DOMSerializer create(IDLType idlType) {
		DOMSerializer serializer = new DOMSerializer();
		serializer.idlType = Optional.ofNullable(idlType);
		return serializer;
	}

	public static DOMSerializer create() {
		DOMSerializer serializer = new DOMSerializer();
		return serializer;
	}

	public DOMSerializer arrayItem(String arrayItem) {
		this.arrayItem = arrayItem;

		return this;
	}

	@Override
	public IDLValue serialize(Object value) {
		if (value == null)
			return IDLValue.create(value);

		if (!Element.class.isAssignableFrom(value.getClass()))
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM,
					value.getClass().getName() + " is not assignable from " + Element.class.getName());
		
		this.namespace = Optional.ofNullable(((Element)value).getNamespaceURI());

		if(this.namespace.isPresent())
			this.isQualified = true;
		
		return this.getIDLValue(this.idlType, (Element) value);
	}

	IDLValue getPrimitiveIDLValue(Type type, Element value) {
		IDLValue result = IDLValue.create(null);

		if (value == null)
			return result;

		Text textNode = this.getTextNode(value);

		String textValue = textNode.getTextContent();

		switch (type) {
		case BOOL:
			result = IDLValue.create(Boolean.valueOf(textValue), type);
			break;
		case INT:
			result = IDLValue.create(new BigInteger(textValue), type);
			break;
		case INT8:
			result = IDLValue.create(Byte.valueOf(textValue), type);
			break;
		case INT16:
			result = IDLValue.create(Short.valueOf(textValue), type);
			break;
		case INT32:
			result = IDLValue.create(Integer.valueOf(textValue), type);
			break;
		case INT64:
			result = IDLValue.create(Long.valueOf(textValue), type);
			break;
		case NAT:
			result = IDLValue.create(new BigInteger(textValue), type);
			break;
		case NAT8:
			result = IDLValue.create(Byte.valueOf(textValue), type);
			break;
		case NAT16:
			result = IDLValue.create(Short.valueOf(textValue), type);
			break;
		case NAT32:
			result = IDLValue.create(Integer.valueOf(textValue), type);
			break;
		case NAT64:
			result = IDLValue.create(Long.valueOf(textValue), type);
			break;
		case FLOAT32:
			result = IDLValue.create(Float.valueOf(textValue), type);
			break;
		case FLOAT64:
			result = IDLValue.create(Double.valueOf(textValue), type);
			break;
		case TEXT:
			result = IDLValue.create(textValue, type);
			break;
		case PRINCIPAL:
			result = IDLValue.create(Principal.fromString(textValue));
			break;
		case EMPTY:
			result = IDLValue.create(null, type);
		case NULL:
			result = IDLValue.create(null, type);
			break;
		}

		return result;
	}

	Type getPrimitiveType(Element value) {
		if (value == null)
			return Type.NULL;

		String type = null;
		if(this.isQualified)
		{
			if (value.hasAttributeNS(CANDID_NS, CANDID_TYPE_ATTR_NAME)) 
				type = value.getAttributeNS(CANDID_NS, CANDID_TYPE_ATTR_NAME);
		}
		else
		{
			if (value.hasAttribute(CANDID_TYPE_ATTR_NAME)) 
				type = value.getAttribute(CANDID_TYPE_ATTR_NAME);
		}
		
		if(type != null)
		{
			switch (type) {
			case "BOOL":
				return Type.BOOL;
			case "INT":
				return Type.INT;
			case "INT8":
				return Type.INT8;
			case "INT16":
				return Type.INT16;
			case "INT32":
				return Type.INT32;
			case "INT64":
				return Type.INT64;
			case "NAT":
				return Type.NAT;
			case "NAT8":
				return Type.NAT8;
			case "NAT16":
				return Type.NAT16;
			case "NAT32":
				return Type.NAT32;
			case "NAT64":
				return Type.NAT64;
			case "FLOAT32":
				return Type.FLOAT32;
			case "FLOAT64":
				return Type.FLOAT64;
			case "TEXT":
				return Type.TEXT;
			case "PRINCIPAL":
				return Type.PRINCIPAL;
			default:
				return Type.TEXT;

			}
		}		
		
		if (value.hasAttributeNS(XML_XSI_NS, XML_TYPE_ATTR_NAME)) {
			type = value.getAttributeNS(XML_XSI_NS, XML_TYPE_ATTR_NAME);

			switch (type) {
			case XSD_PREFIX + ":boolean":
				return Type.BOOL;
			case XSD_PREFIX + ":integer":
				return Type.INT;
			case XSD_PREFIX + ":byte":
				return Type.INT8;
			case XSD_PREFIX + ":short":
				return Type.INT16;
			case XSD_PREFIX + ":int":
				return Type.INT32;
			case XSD_PREFIX + ":long":
				return Type.INT64;
			case XSD_PREFIX + ":positiveInteger":
				return Type.NAT;
			case XSD_PREFIX + ":unsignedByte":
				return Type.NAT8;
			case XSD_PREFIX + ":unsignedShort":
				return Type.NAT16;
			case XSD_PREFIX + ":unsignedInt":
				return Type.NAT32;
			case XSD_PREFIX + ":unsignedLong":
				return Type.NAT64;
			case XSD_PREFIX + ":float":
				return Type.FLOAT32;
			case XSD_PREFIX + ":double":
				return Type.FLOAT64;
			case XSD_PREFIX + ":string":
				return Type.TEXT;
			case XSD_PREFIX + ":ID":
				return Type.PRINCIPAL;
			default:
				return Type.TEXT;

			}
		}

		return Type.TEXT;
	}
	
	IDLValue getArrayIDLValue(Optional<IDLType> expectedIdlType, Element value, String localName)
	{
		IDLType innerIdlType = IDLType.createType(Type.NULL);

		if (expectedIdlType.isPresent())
			innerIdlType = expectedIdlType.get().getInnerType();

		if (this.hasTextNode(value)) {
			if (innerIdlType == null)
				innerIdlType = IDLType.createType(Type.INT8);

			byte[] byteArray = Base64.getDecoder().decode(this.getTextNode(value).getTextContent());
			return IDLValue.create(byteArray, IDLType.createType(Type.VEC, innerIdlType));
		}

		if (value.getNodeType() == Node.ELEMENT_NODE) {
			List<Element> arrayElements = this.getArrayElements(value, localName);
			Object[] arrayValue = new Object[arrayElements.size()];

			int i = 0;
			for (Element arrayElement : arrayElements) {
				IDLValue item = this.getIDLValue(Optional.ofNullable(innerIdlType), arrayElement);

				arrayValue[i] = item.getValue();
				if (innerIdlType == null)
					innerIdlType = item.getIDLType();

				i++;
			}

			IDLType idlType;

			if (expectedIdlType.isPresent() && expectedIdlType.get().getInnerType() != null)
				idlType = expectedIdlType.get();
			else
				idlType = IDLType.createType(Type.VEC, innerIdlType);

			return IDLValue.create(arrayValue, idlType);
		}

		throw CandidError.create(CandidError.CandidErrorCode.CUSTOM,
				"Cannot convert class " + value.getClass().getName() + " to VEC");
		
	}

	IDLValue getIDLValue(Optional<IDLType> expectedIdlType, Element value) {
		// handle null values
		if (value == null)
			return IDLValue.create(value, Type.NULL);

		Type type;
		if (expectedIdlType.isPresent())
			type = expectedIdlType.get().getType();
		else if (this.hasTextNode(value))
			type = this.getPrimitiveType(value);
		else
		{
			MultiMap<String, Element> elementMap = this.getFlatElements(value);
			
			// check if it's array
			Collection<Element> items = elementMap.get(this.arrayItem);
			
			if(items == null || items.isEmpty())
				type = Type.RECORD;
			else
				type = Type.VEC;
		}


		// handle primitives

		if (type.isPrimitive())
			return this.getPrimitiveIDLValue(type, value);

		// handle arrays
		if (type == Type.VEC) 
		{
			if (!expectedIdlType.isPresent())
				expectedIdlType = Optional.ofNullable(IDLType.createType(Type.VEC));
				
			return this.getArrayIDLValue(expectedIdlType, value, this.arrayItem);			
		}	

		// handle Objects
		if (type == Type.RECORD || type == Type.VARIANT) {
			MultiMap<String, Element> elementMap = this.getFlatElements(value);

			Map<String, Object> valueMap = new TreeMap<String, Object>();
			Map<Label, IDLType> typeMap = new TreeMap<Label, IDLType>();
			Map<Label, IDLType> expectedTypeMap = new TreeMap<Label, IDLType>();

			if (expectedIdlType.isPresent())
				expectedTypeMap = expectedIdlType.get().getTypeMap();

			Iterator<String> fieldNames = elementMap.keySet().iterator();

			while (fieldNames.hasNext()) {
				String name = fieldNames.next();

				Collection<Element> items = elementMap.get(name);

				IDLType expectedItemIdlType = null;

				IDLValue itemIdlValue;
				
				if (expectedIdlType.isPresent() && expectedTypeMap != null)
				{	
					expectedItemIdlType = expectedTypeMap.get(Label.createNamedLabel(name));
					
					if(expectedItemIdlType == null)
						continue;
					
					if(expectedItemIdlType.getType() == Type.VEC)
						itemIdlValue = this.getArrayIDLValue(Optional.ofNullable(expectedItemIdlType), value, name);
					else if(items.size() == 1)	
						itemIdlValue = this.getIDLValue(Optional.ofNullable(expectedItemIdlType), items.iterator().next());
					else
						throw CandidError.create(CandidError.CandidErrorCode.CUSTOM,
								"Invalid number of " + name + " elements");
				}
				else
				{
					if(items.size() == 1)
						itemIdlValue = this.getIDLValue(Optional.ofNullable(expectedItemIdlType), items.iterator().next());
					else
						itemIdlValue = this.getArrayIDLValue(Optional.ofNullable(IDLType.createType(Type.VEC)), value, name);
				}

				typeMap.put(Label.createNamedLabel((String) name), itemIdlValue.getIDLType());
				valueMap.put(name, itemIdlValue.getValue());
			}

			IDLType idlType = IDLType.createType(Type.RECORD, typeMap);
			IDLValue idlValue = IDLValue.create(valueMap, idlType);

			return idlValue;
		}

		throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Cannot convert type " + type.name());

	}

	MultiMap<String, Element> getFlatElements(Element element) {
		
		MultiMap<String, Element> elementMap = new MultiMap<String, Element>();

		//Map<String, Element> elementMap = new TreeMap<String, Element>();
		Node childNode = element.getFirstChild();
		if (childNode != null) {
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;

				if(!this.isQualified || this.namespace.get() == childElement.getNamespaceURI())		
					elementMap.put(childElement.getLocalName(), childElement);
			}
			while (childNode.getNextSibling() != null) {
				childNode = childNode.getNextSibling();
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = (Element) childNode;

					if(!this.isQualified || this.namespace.get() == childElement.getNamespaceURI())	
						elementMap.put(childElement.getLocalName(), childElement);
				}
			}
		}
		return elementMap;

	}

	List<Element> getArrayElements(Element element, String localName) {

		List<Element> elementSet = new ArrayList<Element>();
		Node childNode = element.getFirstChild();
		if (childNode != null) {
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;

				if(!this.isQualified || this.namespace.get() == childElement.getNamespaceURI())	
					if (childElement.getLocalName() == localName)
						elementSet.add(childElement);
			}
			while (childNode.getNextSibling() != null) {
				childNode = childNode.getNextSibling();
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = (Element) childNode;

					if(!this.isQualified || this.namespace.get() == childElement.getNamespaceURI())	
						if (childElement.getLocalName() == localName)
							elementSet.add(childElement);
				}
			}
		}
		return elementSet;
	}

	Text getTextNode(Element element) {
		Node childNode = element.getFirstChild();
		if (childNode != null) {
			if (childNode.getNodeType() == Node.TEXT_NODE) {
				return (Text) childNode;
			}
			while (childNode.getNextSibling() != null) {
				childNode = childNode.getNextSibling();
				if (childNode.getNodeType() == Node.TEXT_NODE) {
					return (Text) childNode;
				}
			}
		}
		return null;
	}

	boolean hasTextNode(Element element) {
		
		if(this.getTextNode(element) != null)
			return true;
		else
			return false;
	}
	
	class MultiMap<K, V>
	{
	    private Map<K, Collection<V>> map = new TreeMap<>();
	 
	    public void put(K key, V value)
	    {
	        if (map.get(key) == null) {
	            map.put(key, new ArrayList<V>());
	        }
	 
	        map.get(key).add(value);
	    }
	 
	 
	    public Collection<V> get(Object key) {
	        return map.get(key);
	    }
	 
	    public Set<K> keySet() {
	        return map.keySet();
	    }
	 
	    public Set<Map.Entry<K, Collection<V>>> entrySet() {
	        return map.entrySet();
	    }
	 
	    public Collection<Collection<V>> values() {
	        return map.values();
	    }
	 
	    public boolean containsKey(Object key) {
	        return map.containsKey(key);
	    }
	 
	    public int size()
	    {
	        int size = 0;
	        for (Collection<V> value: map.values()) {
	            size += value.size();
	        }
	        return size;
	    }
	 
	    public boolean isEmpty() {
	        return map.isEmpty();
	    }
	 
	}
}
