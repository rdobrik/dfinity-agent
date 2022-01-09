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

import java.util.Optional;

import org.w3c.dom.Document;

import com.scaleton.dfinity.candid.parser.IDLType;

public class DOMSerDeserBase {
	public static final String CANDID_NS = "http://scaleton.com/dfinity/candid";
	public static final String XML_XSD_NS = "http://www.w3.org/2001/XMLSchema";
	public static final String XML_XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
	public static final String XSD_PREFIX = "xsd";
	public static final String XSI_PREFIX = "xsi";
	public static final String CANDID_PREFIX = "candid";
	public static final String CANDID_TYPE_ATTR_NAME = "type";
	public static final String CANDID_NAME_ATTR_NAME = "name";
	public static final String XML_TYPE_ATTR_NAME = "type";
	public static final String ARRAY_ITEM_NAME = "item";
	
	Optional<IDLType> idlType = Optional.empty();
	Optional<String> namespace = Optional.empty();
	Optional<Document> document = Optional.empty();
	
	String arrayItem = ARRAY_ITEM_NAME;

	boolean isQualified = true;
}
