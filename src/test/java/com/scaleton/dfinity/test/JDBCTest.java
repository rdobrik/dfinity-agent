package com.scaleton.dfinity.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import com.scaleton.dfinity.candid.jackson.JacksonSerializer;
import com.scaleton.dfinity.candid.jdbc.JDBCSerializer;
import com.scaleton.dfinity.candid.parser.IDLArgs;
import com.scaleton.dfinity.candid.parser.IDLValue;

public final class JDBCTest extends CandidAssert {
	static String urlConnection = "jdbc:derby:dfinity;create=true";
	static Connection connection;

	static {
		LOG = LoggerFactory.getLogger(JDBCTest.class);
	}

	@BeforeAll
	static void setup() {
		try {
			connection = DriverManager.getConnection(urlConnection);
		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
		try {
			Statement statement = connection.createStatement();
			String sql = "DROP TABLE data";
			statement.execute(sql);
		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e);
		}

		try {
			Statement statement = connection.createStatement();
			String sql = "CREATE TABLE data (foo INT PRIMARY KEY,bar BOOLEAN,dummy VARCHAR(255))";
			statement.execute(sql);
			sql = "INSERT INTO data VALUES (42, TRUE,'dummy')";
			statement.execute(sql);
			sql = "INSERT INTO data VALUES (43, FALSE,'dummy')";
			statement.execute(sql);
		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
	}

	@Test
	public void test() {
		try {
			Statement statement = connection.createStatement();
			String sql = "SELECT bar, foo FROM data";

			ResultSet result = statement.executeQuery(sql);
			
			IDLValue idlValue = IDLValue.create(result, JDBCSerializer.create());
			List<IDLValue> args = new ArrayList<IDLValue>();
			args.add(idlValue);

			IDLArgs idlArgs = IDLArgs.create(args);

			byte[] buf = idlArgs.toBytes();		
			
			//while (result.next()) {
			//	result.getInt(2); // We can print or use ResultSet here
			//}

		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
	}
}
