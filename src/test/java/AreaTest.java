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
import main.java.persistence.PersistenceManager;

@TestMethodOrder(OrderAnnotation.class)
class AreaTest {

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
	void testSaveAreaToDB() {
		Area area = new Area();
		area.setArea("VESTÍBULO POSTERIOR");
		area.setDescripcion("Vestíbulo de la entrada posterior del centro de trabajo");
		assertTrue(new Area().saveAreaToDB(conn, area));
//		area.setId(PersistenceManager.getLastElementIdFromDB(conn, Area.TABLE_NAME));
//		bUnit.getAreas().add(area);
	}

	@Test
	@Order(4)
	void testSaveBUnitAreaToDB() {
		assertTrue(new Area().saveBUnitAreaToDB(conn, bUnit, bUnit.getAreas().get(0)));
	}

	@Test
	@Order(5)
	void testAddNewAreaToBunitArea() {
		Area area = new Area();
		area.setArea("VESTÍBULO POSTERIOR");
		area.setDescripcion("Vestíbulo de la entrada posterior del centro de trabajo");
		assertNotNull(new Area().addNewAreaToBunitArea(conn, bUnit, area));
		assertNotEquals(0, area.getId());
//		assertEquals(2, area.getId());
		bUnit.getAreas().add(area);
	}
	
	@Test
	@Order(6)
	void testAddNewArea() {
		Area area = new Area();
		area.setArea("VESTÍBULO PRINCIPAL");
		area.setDescripcion("Vestíbulo principal del centro de trabajo");
		assertNotNull(new Area().addNewArea(conn, area));
		assertNotEquals(0, area.getId());
//		assertEquals(2, area.getId());
//		bUnit.getAreas().add(area);
	}

	@Test
	@Order(7)
	void testUpdateAreaToDB() {
		Area oldArea = new Area().getAreaByName(bUnit, "VESTÍBULO PRINCIPAL");
		Area newArea = new Area();
		newArea.setId(oldArea.getId());
		newArea.setArea("COMEDOR");
		newArea.setDescripcion("Comedor del centro de trabajo");
		assertTrue(new Area().updateAreaToDB(conn, newArea));
		oldArea = newArea;
	}

	@Test
	@Order(8)
	void testGetAreasFromDB() {
		assertNotNull(new Area().getAreasFromDB(conn, bUnit));
	}

	@Test
	@Order(3)
	void testGetAreaByName() {
		assertEquals("Recepción del centro de trabajo", new Area().getAreaByName(bUnit, "RECEPCIÓN").getDescripcion());
	}

	@Test
	@Order(2)
	void testGetAreaById() {
		int id = bUnit.getAreas().get(0).getId();
		assertNotNull(new Area().getAreaById(bUnit, id));
	}
	
	@Test
	@Order(9)
	void testDeleteBUnitAreaFromDB() {
		BusinessUnit bUnit = new BusinessUnit();
		bUnit.setId(2);
		Area area = new Area();
		area.setId(2);
		assertTrue(new Area().deleteBUnitAreaFromDB(conn, bUnit, area));
	}
	
	@Test
	@Order(10)
	void testCheckAllocatedAreasFromDB() {
		Area area = new Area();
		area.setId(1);
		assertTrue(area.isAllocatedArea(conn, area));
		area.setId(3);
		assertFalse(area.isAllocatedArea(conn, area));
	}

	@Test
	@Order(11)
	void testGetAllAreasFromDB() {
		assertTrue(new Area().getAllAreasFromDB(conn).size() > 0);
	}
}
