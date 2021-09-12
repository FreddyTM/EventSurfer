package test.java;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import main.java.company.Area;
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
		bUnit.setActivo(true);
		assertTrue(new BusinessUnit().saveBUnitToDB(conn, bUnit));
		bUnit.setId(PersistenceManager.getLastElementIdFromDB(conn, BusinessUnit.TABLE_NAME));
		company.getBusinessUnits().add(bUnit);
	}

	@Test
	@Order(2)
	void testAddNewBusinessUnit() {
		BusinessUnit bUnit = new BusinessUnit();
		bUnit.setCompany(company);
		bUnit.setNombre("TIENDA 2");
		bUnit.setDireccion("Calle Inexistente, 24 Girona");
		bUnit.setProvincia("Girona");
		bUnit.setEstado("Catalunya");
		bUnit.setCpostal("17999");
		bUnit.setTelefono("888776655");
		bUnit.setMail("tienda2@empactual.com");
		bUnit.setActivo(true);
		assertNotNull(new BusinessUnit().addNewBusinessUnit(conn, bUnit));
		assertNotEquals(0, bUnit.getId());
		assertEquals(3, bUnit.getId());
		company.getBusinessUnits().add(bUnit);
	}

	@Test
	@Order(6)
	void testUpdateBusinessUnitToDB() {
		BusinessUnit oldBunit = new BusinessUnit().getBusinessUnitByName(company, "TIENDA 1");
		BusinessUnit newBunit = new BusinessUnit();
		newBunit.setId(oldBunit.getId());
		newBunit.setCompany(company);
		newBunit.setNombre("NUEVA TIENDA 1");
		newBunit.setDireccion("Calle Falsa, 36 Tarragona");
		newBunit.setProvincia("Tarragona");
		newBunit.setEstado("Catalunya");
		newBunit.setCpostal("43999");
		newBunit.setTelefono("777665544");
		newBunit.setMail("nuevatienda1@empactual.com");
		newBunit.setActivo(true);
		assertTrue(new BusinessUnit().updateBusinessUnitToDB(conn, newBunit));
		oldBunit = newBunit;
	}

	@Test
	@Order(3)
	void testGetBusinessUnitsFromDB() {
		assertNotNull(new BusinessUnit().getBusinessUnitsFromDB(conn, company));
	}

	@Test
	@Order(4)
	void testGetBusinessUnitByName() {
		assertNotNull(new BusinessUnit().getBusinessUnitByName(company, "TIENDA 1"));
		assertEquals("Girona", new BusinessUnit().getBusinessUnitByName(company, "TIENDA 2").getProvincia());
	}

	@Test
	@Order(5)
	void testGetBusinessUnitById() {
		assertNotNull(new BusinessUnit().getBusinessUnitById(company, 3));
		assertEquals("08999", new BusinessUnit().getBusinessUnitById(company, 2).getCpostal());
	}
	
	@Test
	@Order(6)
	void testGetBunitsWithArea() {
		Area area = new Area();
		area.setId(1);
		assertNotNull(new BusinessUnit().getBunitsWithArea(conn, company, area));
		assertTrue(new BusinessUnit().getBunitsWithArea(conn, company, area).size() > 0);
	}
	
	@Test
	@Order(7)
	void testGetBunitsWithAreaIds() {
		Area area = new Area();
		area.setId(1);
		assertNotNull(new BusinessUnit().getBunitsWithArea(conn, area));
		assertTrue(new BusinessUnit().getBunitsWithArea(conn, area).size() > 0);
	}
	
	@Test
	@Order(8)
	void testGetAllBunitNamesWithArea() {
		Area area = new Area();
		area.setId(1);
		List<BusinessUnit> allBunits = new BusinessUnit().getBusinessUnitsFromDB(conn, company);
		company.setBusinessUnits(allBunits);
		assertNotNull(new BusinessUnit().getAllBunitNamesWithArea(conn,company, area));
		assertTrue(new BusinessUnit().getAllBunitNamesWithArea(conn,company, area).size() > 0);
	}
	
	@Test
	@Order(9)
	void testGetAllBunitNames() {
		Area area = new Area();
		area.setId(1);
		assertNotNull(new BusinessUnit().getAllBunitNames(conn,company));
		assertTrue(new BusinessUnit().getAllBunitNames(conn,company).size() > 0);
	}

}
