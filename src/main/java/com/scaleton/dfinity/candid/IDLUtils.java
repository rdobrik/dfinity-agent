package com.scaleton.dfinity.candid;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class IDLUtils {
	public static int idlHash(String value)
	{
		BigInteger hash = BigInteger.ZERO;
		
		if(value != null)
		{
			byte[] bytes = value.getBytes();
			
			for(byte b : bytes)
				hash = hash.multiply(BigInteger.valueOf(223)).add(BigInteger.valueOf(b));
		}
		
		return hash.intValue();
	}
	
	public static <T> T[] toArray(Class<T> clazz, Object[] sourceArray) {
		if(sourceArray.length == 0)
			return null;
		
		List<T> list = new ArrayList<T>();
	    
	   for(int i = 0 ; i < sourceArray.length; i++)
		   list.add((T) sourceArray[i]);	   
	   
		Class arrayClazz = list.get(0).getClass();
	    T[] array = (T[]) java.lang.reflect.Array.newInstance(arrayClazz, list.size());
	    
	   return list.toArray(array);

	}	

}
