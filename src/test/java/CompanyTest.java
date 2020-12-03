package test.java;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import main.java.company.Company;
import main.java.persistence.PersistenceManager;

class CompanyTest {
	
	private static Connection conn;
	
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
	void testUpdateCompanyToDB() {
//		fail("Not yet implemented");
		Company oldCompany = new Company().getCompanyFromDB(conn);
		Company updatedCompany = new Company();
		updatedCompany.setId(oldCompany.getId());
		updatedCompany.setNombre("Empresa actualizada");
		updatedCompany.setDireccion("Dirección actualizada");
		updatedCompany.setProvincia("Provincia actualizada");
		updatedCompany.setEstado("Estado actualizado");
		updatedCompany.setCpostal("12345678");
		updatedCompany.setTelefono("987654321");
		updatedCompany.setMail("empactual@empactual.com");
		updatedCompany.setWeb("www.empactual.com");
		assertTrue(new Company().updateCompanyToDB(conn, updatedCompany));
		Company otherCompany = new Company().getCompanyFromDB(conn);
		assertAll(
				() -> assertEquals("Empresa actualizada", otherCompany.getNombre()),
				() -> assertEquals("Dirección actualizada", otherCompany.getDireccion()),
				() -> assertEquals("Provincia actualizada", otherCompany.getProvincia()),
				() -> assertEquals("Estado actualizado", otherCompany.getEstado()),
				() -> assertEquals("12345678", otherCompany.getCpostal()),
				() -> assertEquals("987654321", otherCompany.getTelefono()),
				() -> assertEquals("empactual@empactual.com", otherCompany.getMail()),
				() -> assertEquals("www.empactual.com", otherCompany.getWeb())
				);
	}

	@Test
	@Disabled("Don't really need this")
	void testUpdateCompany() {
		fail("Not yet implemented");
	}

	@Test
	void testGetCompanyFromDB() {
//		fail("Not yet implemented");
		assertNotNull(new Company().getCompanyFromDB(CompanyTest.conn));
	}

	@Test
	@Disabled("BussinesUnit class not yet tested")
	void testAddBusinessUnit() {
		fail("Not yet implemented");
	}

}
