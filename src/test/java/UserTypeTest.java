package test.java;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import main.java.persistence.PersistenceManager;
import main.java.types_states.UserType;

class UserTypeTest {
	
	private static Connection conn;
	private static UserType uType = new UserType();

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
	@Order(2)
	void testGetUserTypesArray() {
		String[] usuarios = {"ADMIN", "MANAGER", "USER"};
		String[] dbUsuarios = uType.getUserTypesArray();
		for (int i = 0; i < usuarios.length; i++) {
			assertEquals(usuarios[i], dbUsuarios[i]);
		}
	}

	@Test
	void testGetUserTypeId() {
		assertEquals(1, uType.getUserTypeId("ADMIN"));
		assertEquals(-1, uType.getUserTypeId("EMPLOYEE"));
	}

	@Test
	void testGetUserType() {
		assertEquals("MANAGER", uType.getUserType(2));
		assertNotNull(uType.getUserType(3));
		assertNull(uType.getUserType(4));
	}

	@Test
	@Order(1)
	void testLoadData() {
		uType.loadData(conn);
		assertTrue(uType.getUserTypes().size() > 0);
	}

}
