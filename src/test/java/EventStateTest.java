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
import main.java.types_states.EventState;

class EventStateTest {

	private static Connection conn;
	private static EventState eState = new EventState();
	
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
	void testGetEventStatesArray() {
		String[] estados = {"ABIERTA", "EN CURSO", "CERRADA"};
		String[] dbEstados = eState.getEventStatesArray();
		for (int i = 0; i < estados.length; i++) {
			assertEquals(estados[i], dbEstados[i]);
		}
	}

	@Test
	void testGetEventStateId() {
		assertEquals(1, eState.getEventStateId("ABIERTA"));
		assertEquals(-1, eState.getEventStateId("EN REPARACION"));
	}

	@Test
	void testGetEventState() {
		assertEquals("EN CURSO", eState.getEventState(2));
		assertNotNull(eState.getEventState(3));
		assertNull(eState.getEventState(4));
	}

	@Test
	@Order(1)
	void testLoadData() {
		eState.loadData(conn);
		assertTrue(eState.getEventStates().size() > 0);
	}

}
