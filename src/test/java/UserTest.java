package test.java;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import main.java.company.BusinessUnit;
import main.java.company.Company;
import main.java.company.User;
import main.java.persistence.PersistenceManager;
import main.java.types_states.TypesStatesContainer;
import main.java.types_states.UserType;

@TestMethodOrder(OrderAnnotation.class)
class UserTest {
	
	private static Connection conn;
	private static Company company = new Company();
	private static BusinessUnit bUnit = new BusinessUnit();

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		PersistenceManager.setUrl("jdbc:postgresql://localhost:5432/devsurferdb");
		PersistenceManager.setUser("surferadmin");
		PersistenceManager.setPassword("surferpass");
		conn = PersistenceManager.getConnection();
		company.setId(1);
		bUnit.setId(1);
		bUnit.setCompany(company);
		UserType uType = new UserType();
		uType.loadData(conn);
		TypesStatesContainer.setuType(uType);
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
		user.setNombre("MiNombre");
		user.setApellido("MiApellido");
		user.setPassword(user.passwordHash("MiPassword"));
		user.setActivo(true);
		assertTrue(new User().saveUserToDB(conn, user));
		user.setId(PersistenceManager.getLastElementIdFromDB(conn, User.TABLE_NAME));
		bUnit.getUsers().add(user);
	}

	@Test
	@Order(3)
	//@Disabled("waiting for previous test to work fine")
	void testAddNewUser() {
		User user = new User();
		user.setbUnit(bUnit);
		user.setUserType("USER");
		user.setUserAlias("FakeUser");
		user.setNombre("OtroNombre");
		user.setApellido("OtroApellido");
		user.setPassword(user.passwordHash("MiOtroPassword"));
		user.setActivo(true);
		assertNotNull(new User().addNewUser(conn, user));
		assertNotEquals(0, user.getId());
		assertEquals(3, user.getId());
		bUnit.getUsers().add(user);
	}

	@Test
	@Order(7)
	//@Disabled("waiting for previous test to work fine")
	void testUpdateUserToDB() {
		User oldUser = new User().getUserByAlias(bUnit, "FakeUser");
		User newUser = new User();
		newUser.setId(oldUser.getId());
		newUser.setbUnit(bUnit);
		newUser.setUserType("USER");
		newUser.setUserAlias("BigFakeUser");
		newUser.setNombre("UnNombre");
		newUser.setApellido("UnApellido");
		newUser.setPassword(newUser.passwordHash("NuevoPassword"));
		newUser.setActivo(true);
		assertTrue(new User().updateUserToDB(conn, newUser));
		oldUser = newUser;
	}

	@Test
	@Order(4)
	//@Disabled("waiting for previous test to work fine")
	void testGetUsersFromDB() {
		assertNotNull(new User().getUsersFromDB(conn, bUnit));
	}

	@Test
	//@Order(6)
	@Disabled("waiting for previous test to work fine")
	void testGetUserByAlias() {
		assertNotNull(new User().getUserByAlias(bUnit, "FakeManager"));
		assertEquals("Nombre", new User().getUserByAlias(bUnit, "FakeManager").getNombre());
	}

	@Test
	//@Order(5)
	@Disabled("waiting for previous test to work fine")
	void testGetUserById() {
		assertNotNull(new User().getUserById(bUnit, 3));
		assertEquals("OtroNombre", new User().getUserById(bUnit, 3).getNombre());
	}

	@Test
	@Order(1)
	void testCheckUserPassword() {
		User user = new User();
		user.setPassword(user.passwordHash("Pass123+"));
		assertTrue(new User().checkUserPassword(user, "Pass123+"));
	}

}
