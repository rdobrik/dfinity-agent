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

import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.scaleton.dfinity.candid.CandidError;
import com.scaleton.dfinity.candid.ObjectDeserializer;
import com.scaleton.dfinity.candid.parser.IDLType;
import com.scaleton.dfinity.candid.parser.IDLValue;
import com.scaleton.dfinity.candid.types.Label;
import com.scaleton.dfinity.candid.types.Type;

public class DOMDeserializer extends DOMSerDeserBase implements ObjectDeserializer {
	String localName;

	boolean setAttributes = false;

	public static DOMDeserializer create(IDLType idlType) {
		DOMDeserializer deserializer = new DOMDeserializer();
		deserializer.idlType = Optional.ofNullable(idlType);
		return deserializer;

	}

	public static DOMDeserializer create() {
		DOMDeserializer deserializer = new DOMDeserializer();
		return deserializer;
	}

	public DOMDeserializer rootElement(String namespace, String localName) {
		this.isQualified = true;
		this.namespace = Optional.ofNullable(namespace);
		this.localName = localName;

		return this;
	}

	public DOMDeserializer rootElement(String localName) {
		this.isQualified = false;
		this.localName = localName;

		return this;
	}

	public DOMDeserializer arrayItem(String arrayItem) {
		this.arrayItem = arrayItem;

		return this;
	}

	public DOMDeserializer document(Document document) {
		this.document = Optional.ofNullable(document);

		return this;
	}

	/**
	 * @return the isQualified
	 */
	public boolean isQualified() {
		return isQualified;
	}

	/**
	 * @param isQualified the isQualified to set
	 */
	public DOMDeserializer setQualified(boolean isQualified) {
		this.isQualified = isQualified;

		return this;
	}

	/**
	 * @param setAttributes the setAttributes to set
	 */
	public DOMDeserializer setAttributes(boolean setAttributes) {
		this.setAttributes = setAttributes;
		return this;
	}

	@Override
	public <T> T deserialize(IDLValue value, Class<T> clazz) {

		if (!this.document.isPresent()) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				if (this.isQualified)
					factory.setNamespaceAware(true);

				builder = factory.newDocumentBuilder();
				this.document = Optional.ofNullable(builder.newDocument());
			} catch (ParserConfigurationException e) {
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, e, e.getLocalizedMessage());
			}
		}

		if (clazz != null)
			if (!Node.class.isAssignableFrom(clazz))
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM,
						clazz.getName() + " is not assignable from " + Node.class.getName());

		Element rootElement = this.document.get().getDocumentElement();

		if (rootElement == null) {
			if (this.localName == null)
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Root Element local name is not defined");

			if (this.isQualified)
				rootElement = this.document.get().createElementNS(this.namespace.get(), this.localName);
			else
				rootElement = this.document.get().createElement(this.localName);

			this.document.get().appendChild(rootElement);
		}

		if (this.isQualified) {
			rootElement.setAttribute("xmlns:" + XSD_PREFIX, XML_XSD_NS);
			rootElement.setAttribute("xmlns:" + XSI_PREFIX, XML_XSI_NS);
			if (this.namespace.isPresent())
				rootElement.setAttribute("xmlns", this.namespace.get());

			if (this.setAttributes)
				rootElement.setAttribute("xmlns:candid", CANDID_NS);
		}
		
		rootElement = this.getValue(rootElement, value.getIDLType(), this.idlType, value.getValue());

		return (T) rootElement;
	}

	Element getPrimitiveValue(Element parentElement, Type type, Object value) {
		if (value != null) {
			Node textNode = this.document.get().createTextNode(value.toString());

			parentElement.appendChild(textNode);

			if (this.isQualified) {
				switch (type) {
				case BOOL:
					parentElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, XSD_PREFIX + ":boolean");
					break;
				case INT:
					parentElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, XSD_PREFIX + ":integer");
					break;
				case INT8:
					parentElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, XSD_PREFIX + ":byte");
					break;
				case INT16:
					parentElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, XSD_PREFIX + ":short");
					break;
				case INT32:
					parentElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, XSD_PREFIX + ":int");
					break;
				case INT64:
					parentElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, XSD_PREFIX + ":long");
					break;
				case NAT:
					parentElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, XSD_PREFIX + ":positiveInteger");
					break;
				case NAT8:
					parentElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, XSD_PREFIX + ":unsignedByte");
					break;
				case NAT16:
					parentElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, XSD_PREFIX + ":unsignedShort");
					break;
				case NAT32:
					parentElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, XSD_PREFIX + ":unsignedInt");
					break;
				case NAT64:
					parentElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, XSD_PREFIX + ":unsignedLong");
					break;
				case FLOAT32:
					parentElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, XSD_PREFIX + ":float");
					break;
				case FLOAT64:
					parentElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, XSD_PREFIX + ":double");
					break;
				case TEXT:
					parentElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, XSD_PREFIX + ":string");
					break;
				case PRINCIPAL:
					parentElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, XSD_PREFIX + ":ID");
					break;

				}
			}

			if (this.setAttributes) {
				if (this.isQualified)
					parentElement.setAttributeNS(CANDID_NS, CANDID_PREFIX + ":" + CANDID_TYPE_ATTR_NAME,
							type.toString());
				else
					parentElement.setAttribute(CANDID_TYPE_ATTR_NAME, type.toString());
			}
		}

		return parentElement;
	}

	Element getValue(Element parentElement, IDLType idlType, Optional<IDLType> expectedIdlType, Object value) {
		Type type = Type.NULL;

		if (expectedIdlType.isPresent()) {
			type = expectedIdlType.get().getType();
			if (idlType != null)
				idlType = expectedIdlType.get();
		}

		// handle primitives
		if (type.isPrimitive())
			parentElement = this.getPrimitiveValue(parentElement, type, value);

		// handle VEC
		if (type == Type.VEC) {
			IDLType expectedInnerIdlType = null;
			IDLType innerIdlType = idlType.getInnerType();

			if (expectedIdlType.isPresent()) {
				expectedInnerIdlType = expectedIdlType.get().getInnerType();
				innerIdlType = expectedInnerIdlType;
			}

			// handle byte array
			if (innerIdlType.getType() == Type.INT8 || innerIdlType.getType() == Type.NAT8)
				parentElement = this.getByteArrayValue(parentElement, innerIdlType, value);
			else 
				parentElement = this.getArrayValue(parentElement, this.arrayItem, innerIdlType, expectedIdlType, value);
		}
		// handle OPT
		if (type == Type.OPT) {
			Optional optionalValue = (Optional) value;

			if (optionalValue.isPresent()) {
				IDLType expectedInnerIdlType = null;

				if (expectedIdlType.isPresent())
					expectedInnerIdlType = expectedIdlType.get().getInnerType();

				parentElement = this.getValue(parentElement, idlType.getInnerType(),
						Optional.ofNullable(expectedInnerIdlType), optionalValue.get());
			}
		}

		// handle object
		if (type == Type.RECORD || type == Type.VARIANT) {
			Map<Integer, Object> valueMap = (Map<Integer, Object>) value;

			Map<Label, IDLType> typeMap = idlType.getTypeMap();

			Map<Label, IDLType> expectedTypeMap = new TreeMap<Label, IDLType>();

			if (expectedIdlType.isPresent() && expectedIdlType.get().getTypeMap() != null)
				expectedTypeMap = expectedIdlType.get().getTypeMap();

			Set<Integer> hashes = valueMap.keySet();

			Map<Integer, Label> expectedLabels = new TreeMap<Integer, Label>();

			for (Label entry : expectedTypeMap.keySet())
				expectedLabels.put(entry.getId(), entry);

			for (Integer hash : hashes) {
				String fieldName;

				Label hashLabel = Label.createIdLabel(hash);

				IDLType itemIdlType = typeMap.get(hashLabel);

				IDLType expectedItemIdlType = null;

				if (expectedTypeMap.containsKey(Label.createIdLabel(hash))) {
					expectedItemIdlType = expectedTypeMap.get(hashLabel);

					Label expectedLabel = expectedLabels.get(hash);

					fieldName = expectedLabel.toString();
				} else
					fieldName = hashLabel.toString();

				Element itemElement;
				if (itemIdlType.getType() == Type.VEC)
					itemElement = this.getArrayValue(parentElement, fieldName, itemIdlType,
							Optional.ofNullable(expectedItemIdlType), valueMap.get(hash));
				else {
					if (this.isQualified)
						itemElement = this.document.get().createElementNS(this.namespace.get(), fieldName);
					else
						itemElement = this.document.get().createElement(fieldName);

					itemElement = this.getValue(itemElement, itemIdlType, Optional.ofNullable(expectedItemIdlType),
							valueMap.get(hash));
				}

				parentElement.appendChild(itemElement);

			}

		}

		return parentElement;
	}

	Element getArrayValue(Element parentElement, String localName, IDLType idlType, Optional<IDLType> expectedIdlType,
			Object value) {
		IDLType expectedInnerIDLType = null;
		IDLType innerIdlType = idlType.getInnerType();

		if (expectedIdlType.isPresent()) {
			expectedInnerIDLType = expectedIdlType.get().getInnerType();
			innerIdlType = expectedInnerIDLType;
		}

		// handle byte array
		if (innerIdlType.getType() == Type.INT8 || innerIdlType.getType() == Type.NAT8) {
			Element arrayElement;

			if (this.isQualified)
				arrayElement = this.document.get().createElementNS(this.namespace.get(), localName);
			else
				arrayElement = this.document.get().createElement(localName);

			arrayElement = this.getByteArrayValue(arrayElement, innerIdlType, value);

			parentElement.appendChild(arrayElement);

			return parentElement;
		} else {
			Object[] arrayValue = (Object[]) value;

			for (Object item : arrayValue) {
				Element arrayElement;

				if (this.isQualified)
					arrayElement = this.document.get().createElementNS(this.namespace.get(), localName);
				else
					arrayElement = this.document.get().createElement(localName);

				arrayElement = this.getValue(arrayElement, innerIdlType, Optional.ofNullable(expectedInnerIDLType), item);

				parentElement.appendChild(arrayElement);
			}

			return parentElement;
		}
	}

	Element getByteArrayValue(Element arrayElement, IDLType innerIdlType, Object value) {
		if (this.isQualified)
			arrayElement.setAttributeNS(XML_XSI_NS,XSI_PREFIX + ":" + XML_TYPE_ATTR_NAME, "xsd:base64Binary");

		if (this.setAttributes) {
			if (this.isQualified)
				arrayElement.setAttributeNS(CANDID_NS, CANDID_PREFIX + ":" + CANDID_TYPE_ATTR_NAME,
						innerIdlType.getType().toString());
			else
				arrayElement.setAttribute(CANDID_TYPE_ATTR_NAME, innerIdlType.getType().toString());
		}

		if (value != null) {
			Node textNode = this.document.get().createTextNode(Base64.getEncoder().encodeToString((byte[]) value));

			arrayElement.appendChild(textNode);

		}

		return arrayElement;
	}

}
