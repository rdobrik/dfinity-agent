package com.scaleton.dfinity.types;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public final class PrincipalSerializer extends StdSerializer<Principal> {
    public PrincipalSerializer() {
        this(Principal.class);
    }

	
	public PrincipalSerializer(Class<Principal> t) {
		super(t);
	}

	@Override
	public void serialize(Principal principal, JsonGenerator gen, SerializerProvider provider) throws IOException {
		if(principal.value.isPresent())
			gen.writeBinary(principal.value.get());
		
	}

}
