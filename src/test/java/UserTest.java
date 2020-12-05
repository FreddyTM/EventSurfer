package test.java;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import main.java.company.User;
import main.java.persistence.PersistenceManager;

@TestMethodOrder(OrderAnnotation.class)
class UserTest {
	
	private static Connection conn;
	//private static User user = new User();

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		PersistenceManager.setUrl("jdbc:postgresql://localhost:5432/devsurferdb");
		PersistenceManager.setUser("surferadmin");
		PersistenceManager.setPassword("surferpass");
		conn = PersistenceManager.getConnection();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		PersistenceManager.closeDatabase(conn);
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testSaveUserToDB() {
//		fail("Not yet implemented");
		User user = new User();
		
	}

	@Test
	void testAddNewUser() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateUserToDB() {
		fail("Not yet implemented");
	}

	@Test
	void testGetUsersFromDB() {
		fail("Not yet implemented");
	}

	@Test
	void testGetUserByAlias() {
		fail("Not yet implemented");
	}

	@Test
	void testGetUserById() {
		fail("Not yet implemented");
	}

	@Test
	void testCheckUserPassword() {
		fail("Not yet implemented");
	}

}
