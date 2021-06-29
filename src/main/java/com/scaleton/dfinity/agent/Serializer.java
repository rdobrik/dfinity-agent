package com.scaleton.dfinity.agent;

public interface Serializer {
	
	public <T> void serializeField(String key, T value);

}
