package com.scaleton.dfinity.test;

import java.math.BigInteger;

public class JacksonPojo {
	public Boolean bar;

	public BigInteger foo;
	
	// Just for testing purposes, JUnit uses equals
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JacksonPojo other = (JacksonPojo) obj;
		if (bar == null) {
			if (other.bar != null)
				return false;
		} else if (!bar.equals(other.bar))
			return false;
		if (foo == null) {
			if (other.foo != null)
				return false;
		} else if (!foo.equals(other.foo))
			return false;
		return true;
	}	

}
