package test.java;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import main.java.company.BusinessUnit;
import main.java.company.Company;
import main.java.company.User;
import main.java.persistence.PersistenceManager;

@TestMethodOrder(OrderAnnotation.class)
class UserTest {
	
	private static Connection conn;
	private static Company company = new Company();
	private static BusinessUnit bUnit = new BusinessUnit();
	//private static User user = new User();

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		PersistenceManager.setUrl("jdbc:postgresql://localhost:5432/devsurferdb");
		PersistenceManager.setUser("surferadmin");
		PersistenceManager.setPassword("surferpass");
		conn = PersistenceManager.getConnection();
		company.setId(1);
		bUnit.setId(1);
		bUnit.setCompany(company);
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
	@Order(1)
	void testSaveUserToDB() {
		User user = new User();
		user.setbUnit(bUnit);
		user.setUserType("MANAGER");
		user.setUserAlias("FakeManager");
		user.setNombre("Nombre");
		user.setApellido("Apellido");
		user.setPassword(user.passwordHash("MiPassword"));
		user.setActivo(true);
		assertTrue(new User().saveUserToDB(conn, user));
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
