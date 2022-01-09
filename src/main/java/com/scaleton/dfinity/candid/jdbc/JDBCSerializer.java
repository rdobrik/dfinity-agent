package com.scaleton.dfinity.candid.jdbc;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import com.scaleton.dfinity.candid.CandidError;
import com.scaleton.dfinity.candid.ObjectSerializer;
import com.scaleton.dfinity.candid.parser.IDLType;
import com.scaleton.dfinity.candid.parser.IDLValue;
import com.scaleton.dfinity.candid.types.Label;
import com.scaleton.dfinity.candid.types.Type;
import com.scaleton.dfinity.types.Principal;

public class JDBCSerializer implements ObjectSerializer {
	Optional<IDLType> idlType = Optional.empty();
	boolean isArray = true;

	public static JDBCSerializer create(IDLType idlType) {
		JDBCSerializer serializer = new JDBCSerializer();
		serializer.idlType = Optional.ofNullable(idlType);
		return serializer;
	}

	public static JDBCSerializer create() {
		JDBCSerializer serializer = new JDBCSerializer();
		return serializer;
	}

	public JDBCSerializer array(boolean isArray) {
		this.isArray = isArray;
		return this;
	}

	@Override
	public IDLValue serialize(Object value) {
		if (value == null)
			return IDLValue.create(value);

		if (!ResultSet.class.isAssignableFrom(value.getClass()))
			throw CandidError.create(CandidError.CandidErrorCode.CUSTOM,
					value.getClass().getName() + " is not assignable from " + ResultSet.class.getName());

		// handle JDBC result set as VEC
		if (ResultSet.class.isAssignableFrom(value.getClass())) {
			ResultSet resultSet = (ResultSet) value;

			try {
				if (idlType.isPresent())
					if (idlType.get().getType() == Type.VEC)
						return this.getArrayIDLValue(Optional.ofNullable(this.idlType.get()), resultSet);
					else if (resultSet.next())
						return this.getIDLValue(Optional.ofNullable(this.idlType.get()), resultSet);
					else
						return IDLValue.create(null);

				else if (this.isArray)
					return this.getArrayIDLValue(Optional.empty(), resultSet);
				else if (resultSet.next())
					return this.getIDLValue(Optional.empty(), resultSet);
				else
					return IDLValue.create(null);
			} catch (SQLException e) {
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, e, e.getLocalizedMessage());
			}

		}

		throw CandidError.create(CandidError.CandidErrorCode.CUSTOM,
				"Cannot convert class " + value.getClass().getName());
	}

	IDLValue getArrayIDLValue(Optional<IDLType> expectedIdlType, ResultSet value) throws SQLException {
		IDLType innerIdlType = IDLType.createType(Type.RECORD);
		
		if (expectedIdlType.isPresent())
			innerIdlType = expectedIdlType.get().getInnerType();

		List<Map> arrayValue = new ArrayList<Map>();

		IDLType expectedInnerIdlType = null;
		if(expectedIdlType.isPresent())
			expectedInnerIdlType = expectedIdlType.get().getInnerType();
			
		while (value.next()) {	
			IDLValue rowValue = this.getIDLValue(Optional.ofNullable(expectedInnerIdlType), value);

			arrayValue.add(rowValue.getValue());
			
			innerIdlType = rowValue.getIDLType();
		}

		IDLType idlType;

		if (expectedIdlType.isPresent() && expectedIdlType.get().getInnerType() != null)
			idlType = expectedIdlType.get();
		else
			idlType = IDLType.createType(Type.VEC, innerIdlType);

		return IDLValue.create(arrayValue.toArray(), idlType);

	}

	IDLValue getIDLValue(Optional<IDLType> expectedIdlType, ResultSet value) throws SQLException {
		Map<String, Object> valueMap = new TreeMap<String, Object>();
		Map<Label, IDLType> typeMap = new TreeMap<Label, IDLType>();
		Map<Label, IDLType> expectedTypeMap = new TreeMap<Label, IDLType>();

		if (expectedIdlType.isPresent())
			expectedTypeMap = expectedIdlType.get().getTypeMap();

		ResultSetMetaData metaData = value.getMetaData();

		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			String name = metaData.getColumnName(i).toLowerCase();

			IDLValue itemIdlValue;
			if (expectedIdlType.isPresent() && expectedTypeMap != null) {
				IDLType expectedItemIdlType = expectedTypeMap.get(Label.createNamedLabel(name));

				if (expectedItemIdlType == null)
					continue;

				Object itemValue = value.getObject(i);

				itemIdlValue = this.getPrimitiveIDLValue(Optional.ofNullable(expectedItemIdlType), itemValue, metaData.getColumnType(i));
			} else {
				Object itemValue = value.getObject(i);

				itemIdlValue = this.getPrimitiveIDLValue(Optional.empty(), itemValue, metaData.getColumnType(i));
			}

			typeMap.put(Label.createNamedLabel(name), itemIdlValue.getIDLType());
			valueMap.put(name, itemIdlValue.getValue());
		}

		IDLType idlType = IDLType.createType(Type.RECORD, typeMap);
		IDLValue idlValue = IDLValue.create(valueMap, idlType);

		return idlValue;
	}

	IDLValue getPrimitiveIDLValue(Optional<IDLType> expectedIdlType, Object value,int columnType) {
		Type type;
		if(expectedIdlType.isPresent())
			type = expectedIdlType.get().getType();
		else
			type = this.getPrimitiveType(columnType);
		
		// parse principal
		if(type == Type.PRINCIPAL)
			return IDLValue.create(Principal.fromString((String) value), type);
		
		// parse byte array
		if(type == Type.VEC)
		{
			if(expectedIdlType.isPresent())
				return IDLValue.create(value, expectedIdlType.get());
			else
				return IDLValue.create(value, IDLType.createType(type, IDLType.createType(Type.INT8)));
		}
		
		if(value instanceof Timestamp)
		{
			Timestamp timestamp = (Timestamp)value;
			
			return IDLValue.create(timestamp.getTime(), type);
		}
			
		// get Date
		if(value instanceof Date)
		{
			Date date = (Date)value;
			
			if(type == Type.INT64 || type == Type.NAT64)
				return IDLValue.create(date.getTime(), type);
			else if(type == Type.TEXT)
				return IDLValue.create(date.toString(), type);
			else
				throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Cannot convert JDBC Date type to " + type);
		}
		
		return IDLValue.create(value, type);
	}

	Type getPrimitiveType(int columnType) {
		switch (columnType) {
		case Types.NULL:
			return Type.NULL;
		case Types.BOOLEAN:
			return Type.BOOL;			
		case Types.BIT:
			return Type.BOOL;
		case Types.DECIMAL:
			return Type.INT;			
		case Types.TINYINT:
			return Type.INT8;	
		case Types.SMALLINT:
			return Type.INT16;	
		case Types.INTEGER:
			return Type.INT32;	
		case Types.BIGINT:
			return Type.INT64;	
		case Types.FLOAT:
			return Type.FLOAT32;	
		case Types.DOUBLE:
			return Type.FLOAT64;
		case Types.BLOB:
			return Type.VEC;
		case Types.VARCHAR:
			return Type.TEXT;
		case Types.CLOB:
			return Type.TEXT;	
		case Types.TIMESTAMP:
			return Type.NAT64;	
		case Types.DATE:
			return Type.TEXT;			
		}

		throw CandidError.create(CandidError.CandidErrorCode.CUSTOM, "Cannot convert JDBC type " + columnType);
	}

}
