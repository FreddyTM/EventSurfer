package test.java;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.company.Company;
import main.java.persistence.PersistenceManager;

class PersistenceManagerTest {

	private static Connection connection;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		String devUrl = "jdbc:postgresql://localhost:5432/devsurferdb";
		String devUser = "surferadmin";
		String devPassword = "surferpass";
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(devUrl, devUser, devPassword);
		} catch (ClassNotFoundException ex) {
			System.out.println("No se encuentra el controlador JDBC ("
			+ ex.getMessage() +")");
		} catch (SQLException e) {
			System.out.println("Error: " + e.getMessage());
			System.out.println("Estado: " + e.getSQLState());
			System.out.println("Código: " + e.getErrorCode());
		}
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}


	@Test
	void testConnectToDatabase() {
		assertNotNull(PersistenceManager.connectToDatabase("LOCAL_DB"));
		assertNotNull(PersistenceManager.connectToDatabase("LOCAL_TEST_DB"));
		assertNull(PersistenceManager.connectToDatabase(""));
		assertNull(PersistenceManager.connectToDatabase("REMOTE_DB"));
	}
	
	@Test
	void testGetConnection() {
		PersistenceManager.setUrl("jdbc:postgresql://localhost:5432/devsurferdb");
		PersistenceManager.setUser("surferadmin");
		PersistenceManager.setPassword("surferpass");
		assertNotNull(PersistenceManager.getConnection());
	}

	@Test
	void testGetResultSetStatementString() {
//		Connection connection = null;
//		String devUrl = "jdbc:postgresql://localhost:5432/devsurferdb";
//		String devUser = "surferadmin";
//		String devPassword = "surferpass";
//		try {
//			Class.forName("org.postgresql.Driver");
//			connection = DriverManager.getConnection(devUrl, devUser, devPassword);
//		} catch (ClassNotFoundException ex) {
//			System.out.println("No se encuentra el controlador JDBC ("
//			+ ex.getMessage() +")");
//		} catch (SQLException e) {
//			System.out.println("Error: " + e.getMessage());
//			System.out.println("Estado: " + e.getSQLState());
//			System.out.println("Código: " + e.getErrorCode());
//		}
		Statement statement = null;
		//ResultSet results = null;
		String sql = "SELECT * FROM event_state;";
		try {
			statement = connection.createStatement();
			assertNotNull(PersistenceManager.getResultSet(statement, sql));
			//results = PersistenceManager.getResultSet(statement, sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//PersistenceManager.closeResultSet(results);
			PersistenceManager.closeStatement(statement);
		}
	}

	@Test
	void testGetResultSetPreparedStatementString() {
//		Connection connection = null;
//		String devUrl = "jdbc:postgresql://localhost:5432/devsurferdb";
//		String devUser = "surferadmin";
//		String devPassword = "surferpass";
//		try {
//			Class.forName("org.postgresql.Driver");
//			connection = DriverManager.getConnection(devUrl, devUser, devPassword);
//		} catch (ClassNotFoundException ex) {
//			System.out.println("No se encuentra el controlador JDBC ("
//			+ ex.getMessage() +")");
//		} catch (SQLException e) {
//			System.out.println("Error: " + e.getMessage());
//			System.out.println("Estado: " + e.getSQLState());
//			System.out.println("Código: " + e.getErrorCode());
//		}
		PreparedStatement pstm = null;
		String sql = "SELECT id FROM \"?\" "
				+ "ORDER BY id DESC LIMIT 1;";
		try {
			pstm = connection.prepareStatement(sql);
			pstm.setString(1, "user");
			assertNotNull(PersistenceManager.getResultSet(pstm, sql));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.closePrepStatement(pstm);
		}
	}

	@Test
	void testGetLastElementIdFromDB() {
//		Connection connection = null;
//		String devUrl = "jdbc:postgresql://localhost:5432/devsurferdb";
//		String devUser = "surferadmin";
//		String devPassword = "surferpass";
//		try {
//			Class.forName("org.postgresql.Driver");
//			connection = DriverManager.getConnection(devUrl, devUser, devPassword);
//		} catch (ClassNotFoundException ex) {
//			System.out.println("No se encuentra el controlador JDBC ("
//			+ ex.getMessage() +")");
//		} catch (SQLException e) {
//			System.out.println("Error: " + e.getMessage());
//			System.out.println("Estado: " + e.getSQLState());
//			System.out.println("Código: " + e.getErrorCode());
//		}
		assertEquals(1, PersistenceManager.getLastElementIdFromDB(connection, "business_unit"));
	}
	
	@Test
	void testUpdateTimeStampToDB() {
		Timestamp tNow = PersistenceManager.getTimestampNow();
		Timestamp readTimestamp = null;
		assertTrue(PersistenceManager.updateTimeStampToDB(connection, Company.TABLE_NAME, tNow));
		Statement stm = null;
		ResultSet results = null;
		String sql = "SELECT fecha_hora "
				+ "FROM last_modification "
				+ "WHERE table_name = 'company';";
		try {
			stm = connection.createStatement();
			results = stm.executeQuery(sql);
			while(results.next()) {
				readTimestamp = results.getTimestamp(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assertEquals(tNow, readTimestamp);
	}
	
//	@Test
//	void testCheckDefaultAdminPassword() {
//		assertEquals(0, PersistenceManager.checkDefaultAdminPassword(connection));
//	}
	
//	@Test
//	void testGetBunitIdFromUser() {
//		assertEquals(1, PersistenceManager.getBunitIdFromUser(connection, "FakeManager",
//				"oLrDVVZRUGSoVgyy5uxj2x8p@&7avEEIADyB&EZhhldg4KgN#q"));
//		assertEquals(0, PersistenceManager.getBunitIdFromUser(connection, "FakeManager",
//				"sdophjis0ih0sthjs"));
//	}
	
//	@Test
//	void testGetDefaultAdminUser() {
//		assertNotNull(PersistenceManager.getDefaultAdminUser(connection));
//	}

}
