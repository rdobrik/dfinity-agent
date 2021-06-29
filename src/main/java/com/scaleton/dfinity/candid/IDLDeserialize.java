package com.scaleton.dfinity.candid;

public final class IDLDeserialize {
	Deserializer de;
	
	public IDLDeserialize(Deserializer de) {
		this.de = de;
	}

	public static IDLDeserialize create(byte[] bytes)
	{
		Deserializer de = Deserializer.fromBytes(bytes);
		
		return new IDLDeserialize(de);
		
	}
	
	public <T extends Deserialize> T getValue(Class<T> clazz)
	{
		T value;

			try {
				value = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, String.format("Cannot initialize class %s"),clazz.getCanonicalName());
			}

		
		Integer ty = this.de.table.types.poll();
		
		if(ty == null)
			CandidError.create(CandidError.CandidErrorCode.CUSTOM, "No more values to deserialize");
		
		this.de.table.currentType.add(ty);
		
		value.deserialize(de);
		
		if(this.de.table.currentType.isEmpty() && !this.de.fieldName.isPresent())
			return value;
		else
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Trailing type after deserializing a value");
		
	}
	
	public boolean isDone()
	{
		return this.de.table.types.isEmpty();
	}

}
