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
import main.java.exceptions.DatabaseError;
import main.java.persistence.PersistenceManager;
import main.java.toolbox.ToolBox;

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
	void testConnectToLocalDatabase() {
		try {
			assertNotNull(PersistenceManager.connectToDatabase("LOCAL_DB"));
		} catch (DatabaseError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			assertNotNull(PersistenceManager.connectToDatabase("LOCAL_TEST_DB"));
		} catch (DatabaseError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			assertNull(PersistenceManager.connectToDatabase(""));
		} catch (DatabaseError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	void testConnectToRemoteDatabase() {
		String devUrl = "jdbc:postgresql://jqjqasnlbihnyy:0bb8e23db3107a14e61bbdaaae12c176c5b977663abbb1db3a1dfeadc2091bd4@ec2-18-203-7-163.eu-west-1.compute.amazonaws.com:5432/de2ttdn4inqheo\r\n";
		String devUser = "jqjqasnlbihnyy";
		String devPassword = "0bb8e23db3107a14e61bbdaaae12c176c5b977663abbb1db3a1dfeadc2091bd4";
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
		try {
			assertNotNull(PersistenceManager.connectToDatabase("REMOTE_DB"));
		} catch (DatabaseError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	void testGetConnection() {
		PersistenceManager.setUrl("jdbc:postgresql://localhost:5432/devsurferdb");
		PersistenceManager.setUser("surferadmin");
		PersistenceManager.setPassword("surferpass");
		try {
			assertNotNull(PersistenceManager.getConnection());
		} catch (DatabaseError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void testGetResultSetStatementString() {
		Statement statement = null;
		String sql = "SELECT * FROM event_state;";
		try {
			statement = connection.createStatement();
			assertNotNull(PersistenceManager.getResultSet(statement, sql));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.closeStatement(statement);
		}
	}

	@Test
	void testGetResultSetPreparedStatementString() {
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
		assertEquals(3, PersistenceManager.getLastElementIdFromDB(connection, "user_type"));
	}
	
	@Test
	void testUpdateTimeStampToDB() {
		Timestamp tNow = ToolBox.getTimestampNow();
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
	
	@Test
	void testGetLatestTimestampFromDb() {
		assertNotNull(PersistenceManager.getLatestTimestampFromDb(connection));
		Timestamp tNow = ToolBox.getTimestampNow();
		assertTrue(tNow.after(PersistenceManager.getLatestTimestampFromDb(connection)));
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
