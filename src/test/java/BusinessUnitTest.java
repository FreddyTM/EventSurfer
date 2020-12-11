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
class BusinessUnitTest {

	private static Connection conn;
	private static Company company = new Company();
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		PersistenceManager.setUrl("jdbc:postgresql://localhost:5432/devsurferdb");
		PersistenceManager.setUser("surferadmin");
		PersistenceManager.setPassword("surferpass");
		conn = PersistenceManager.getConnection();
		company.setId(1);
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
	void testSaveBUnitToDB() {
		BusinessUnit bUnit = new BusinessUnit();
		bUnit.setCompany(company);
		bUnit.setNombre("TIENDA 1");
		bUnit.setDireccion("Calle Inventada, 12 Barcelona");
		bUnit.setProvincia("Barcelona");
		bUnit.setEstado("Catalunya");
		bUnit.setCpostal("08999");
		bUnit.setTelefono("999887766");
		bUnit.setMail("tienda1@empactual.com");
		assertTrue(new BusinessUnit().saveBUnitToDB(conn, bUnit));
		bUnit.setId(PersistenceManager.getLastElementIdFromDB(conn, BusinessUnit.TABLE_NAME));
		company.getBusinessUnits().add(bUnit);
	}

	@Test
	void testAddNewBusinessUnit() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateBusinessUnitToDB() {
		fail("Not yet implemented");
	}

	@Test
	void testGetBusinessUnitsFromDB() {
		fail("Not yet implemented");
	}

	@Test
	void testGetBusinessUnitByName() {
		fail("Not yet implemented");
	}

	@Test
	void testGetBusinessUnitById() {
		fail("Not yet implemented");
	}

}
