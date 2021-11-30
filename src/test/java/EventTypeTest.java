package test.java;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import main.java.persistence.PersistenceManager;
import main.java.types_states.EventType;

@TestMethodOrder(OrderAnnotation.class)
class EventTypeTest {
	
	private static Connection conn;
	private static EventType eType = new EventType();

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
	@Order(4)
	void testGetEventTypesArray() {
		eType.loadData(conn);
		String[] tipos = {"AVERÍA ELÉCTRICA", "CERRADURA INUTILIZADA", "GOTERAS", "ROTURA DE MATERIAL"};
		String[] dbTipos = eType.getEventTypesArray();
		for (int i = 0; i < tipos.length; i++) {
			assertEquals(tipos[i], dbTipos[i]);
		}
		
	}

	@Test
	@Order(5)
	void testGetEventTypeId() {
		assertEquals(1, eType.getEventTypeId("AVERÍA ELÉCTRICA"));
		assertEquals(-1, eType.getEventTypeId("CLIMATIZACIÓN"));
	}

	@Test
	@Order(6)
	void testGetEventType() {
		assertEquals("GOTERAS", eType.getEventTypeDescription(2));
		assertNotNull(eType.getEventTypeDescription(3));
		assertNull(eType.getEventTypeDescription(4));
	}

	@Test
	@Order(3)
	void testLoadData() {
		eType.loadData(conn);
		assertTrue(eType.getEventTypes().size() > 0);
	}

	@Test
	@Order(1)
	void testSaveEventTypeToDB() {
		assertTrue(eType.saveEventTypeToDB(conn, "AVERÍA ELÉCTRICA"));
		eType.getEventTypes().put(1, "AVERÍA ELÉCTRICA");
	}

	@Test
	@Order(2)
	void testAddNewEventType() {
		assertTrue(eType.addNewEventType(conn, "GOTERAS"));
		assertTrue(eType.addNewEventType(conn, "ROTURA DE MATERIAL"));
	}

	@Test
	@Order(7)
	void testUpdateEventTypeToDB() {
		assertTrue(eType.updateEventTypeToDB(conn, 4, "CERRADURA AVERIADA"));
	}
	
	@Test
	@Order(8)
	void testIsEventTypeUsed() {
		assertTrue(eType.isEventTypeUsed(conn, 2));
		assertFalse(eType.isEventTypeUsed(conn, 4));
	}
	
	@Test
	@Order(9)
	void testDeleteEventTypeFromDB() {
		assertTrue(eType.deleteEventTypeFromDB(conn, "CERRADURA AVERIADA"));
	}
}
