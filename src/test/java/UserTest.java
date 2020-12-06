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
	@Order(2)
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
		user.setId(2);
		bUnit.getUsers().add(user);
	}

	@Test
	@Order(3)
	void testAddNewUser() {
		User user = new User();
		user.setbUnit(bUnit);
		user.setUserType("USER");
		user.setUserAlias("FakeUser");
		user.setNombre("OtroNombre");
		user.setApellido("OtroApellido");
		user.setPassword(user.passwordHash("MiOtroPassword"));
		user.setActivo(true);
		
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
		
	}

	@Test
	@Order(1)
	void testCheckUserPassword() {
		User user = new User();
		user.setPassword(user.passwordHash("Pass123+"));
		assertTrue(new User().checkUserPassword(user, "Pass123+"));
	}

}
