package com.scaleton.dfinity.agent;

import java.util.Optional;

public class Value <T> {
	private Optional<T> value;
	
	public void set(T value)
	{
		this.value = Optional.of(value);
	}
	
	public T get()
	{
		return value.get();
	}

}
