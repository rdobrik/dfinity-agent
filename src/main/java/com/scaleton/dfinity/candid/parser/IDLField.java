package com.scaleton.dfinity.candid.parser;

import com.scaleton.dfinity.candid.types.Label;

public class IDLField {
	Label id;
	IDLValue value;
	
	public static IDLField createIDLField(Label id, IDLValue value)
	{
		IDLField idlField = new IDLField();
		
		idlField.id = id;
		idlField.value = value;
		
		return idlField;
	}
	
	public Label getId() {
		return this.id;
	}
	public IDLValue getValue() {
		return this.value;
	}
}
