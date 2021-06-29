package com.scaleton.dfinity.agent.hashtree;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Label implements Comparable<Label>{
	static final Logger LOG = LoggerFactory.getLogger(Label.class);	
	
	byte[] value;
	
	public Label(byte[] value)
	{
		this.value = value;
		
		// TODO remove in the future
		LOG.debug(this.toString());
	}
	
	public Label(String value)
	{
		if(value != null)
			this.value = value.getBytes();
		
		// TODO remove in the future
		LOG.debug(this.toString());
	}	
	
	public byte[] get()
	{
		return this.value;
	}
	
	public boolean equals(Label label)
	{
		return Arrays.equals(this.value, label.get());
	}
	
	public String toString()
	{
		if(this.value != null)
			return new String(this.value);
		else
			return null;
	}

	@Override
	public int compareTo(Label label) {
    	int result = 0;
    	
    	// TODO remove in the future
    	LOG.debug(this.toString() + " compare to " + label.toString());
    	
    	ByteBuffer bytes1 = ByteBuffer.wrap(this.value);
    	ByteBuffer bytes2 = ByteBuffer.wrap(label.value);
    	
    	while(bytes1.position() < this.value.length && bytes2.position() < label.value.length)
    	{
    		result = Long.compare(Byte.toUnsignedLong(bytes1.get()), Byte.toUnsignedLong(bytes2.get()));
    		
    		if(result != 0)
    			break;
    	}
    	
    	if(result == 0 && this.value.length != label.value.length)
    		return label.value.length - this.value.length;
    		  
    	return result;
	}

}
