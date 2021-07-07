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

package com.scaleton.dfinity.types;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public final class PrincipalError extends Error {
	private static final long serialVersionUID = 1L;
	
	final static String RESOURCE_BUNDLE_FILE = "dfinity_types";
	static ResourceBundle properties;
	
	PrincipalErrorCode code;

	static {
		properties = ResourceBundle.getBundle(RESOURCE_BUNDLE_FILE);
	}
	
	public static PrincipalError create(PrincipalErrorCode code, Object... args) {

		String message = properties.getString(code.label);
		// set arguments
		message = MessageFormat.format(message, args);

		return new PrincipalError(code, message);
	}
	
	public static PrincipalError create(PrincipalErrorCode code,Throwable t, Object... args) {

		String message = properties.getString(code.label);
		// set arguments
		message = MessageFormat.format(message, args);

		return new PrincipalError(code,t, message);
	}	

	private PrincipalError(PrincipalErrorCode code, String message) {	
		super(message);
		this.code = code;
	}
	
	private PrincipalError(PrincipalErrorCode code, Throwable t, String message) {
		super(message, t);
		this.code = code;
	}
	
	public PrincipalErrorCode getCode() {
		return code;
	}
	
	public enum PrincipalErrorCode {
		BUFFER_TOO_LONG("BufferTooLong"),
		ABNORMAL_TEXTUAL_FORMAT("AbnormalTextualFormat"),
		INVALID_TEXTUAL_FORMAT_NOT_BASE32("InvalidTextualFormatNotBase32"),
		TEXT_TOO_SMALL("TextTooSmall"),	
		EXTERNAL_ERROR("ExternalError");
		
		public String label;

		PrincipalErrorCode(String label) {
			this.label = label;
		}
			
	}		
}
