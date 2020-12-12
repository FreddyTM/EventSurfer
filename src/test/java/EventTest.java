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

import main.java.company.Area;
import main.java.company.BusinessUnit;
import main.java.company.Company;
import main.java.event.Event;
import main.java.persistence.PersistenceManager;
import main.java.types_states.EventState;
import main.java.types_states.EventType;
import main.java.types_states.TypesStatesContainer;

@TestMethodOrder(OrderAnnotation.class)
class EventTest {
	
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
		bUnit.setAreas(new Area().getAreasFromDB(conn, bUnit));
		EventType evType = new EventType();
		evType.loadData(conn);
		TypesStatesContainer.setEvType(evType);
		EventState evState = new EventState();
		evState.loadData(conn);
		TypesStatesContainer.setEvState(evState);
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
	void testSaveEventToDB() {
		Event event = new Event();
		event.setbUnit(bUnit);
		event.setArea(bUnit.getAreas().get(0));
		event.setEventType("GOTERAS");
		event.setTitulo("Goteras frente al mostrador");
		event.setDescripcion("Aparecen dos goteras frente al mostrador de recepción "
				+ "tras el pasado día de lluvias. Se colocan cubos para recoger el agua");
		event.setEventState("ABIERTA");
		assertTrue(new Event().saveEventToDB(conn, event));
		event.setId(PersistenceManager.getLastElementIdFromDB(conn, Event.TABLE_NAME));
		bUnit.getEvents().add(event);
	}

	@Test
	@Order(2)
	void testAddNewEvent() {
		Event event = new Event();
		event.setbUnit(bUnit);
		event.setArea(bUnit.getAreas().get(1));
		event.setEventType("ROTURA DE MATERIAL");
		event.setTitulo("Silla rota");
		event.setDescripcion("El respaldo de una silla se ha roto");
		event.setEventState("ABIERTA");
		assertNotNull(new Event().addNewEvent(conn, event));
		assertNotEquals(0, event.getId());
		assertEquals(2, event.getId());
		bUnit.getEvents().add(event);
	}

	@Test
	@Order(5)
	void testUpdateEventToDB() {
		Event oldEvent = new Event().getEventById(bUnit, 2);
		Event newEvent = new Event();
		newEvent.setId(oldEvent.getId());
		newEvent.setbUnit(bUnit);
		newEvent.setArea(bUnit.getAreas().get(1));
		newEvent.setEventType("ROTURA DE MATERIAL");
		newEvent.setTitulo("Mesa rota");
		newEvent.setDescripcion("La pata de una mesa se ha roto");
		newEvent.setEventState("ABIERTA");
		assertTrue(new Event().updateEventToDB(conn, newEvent));
		oldEvent = newEvent;
	}

	@Test
	@Order(3)
	void testGetEventsFromDB() {
		assertNotNull(new Event().getEventsFromDB(conn, bUnit));
	}

	@Test
	@Order(4)
	void testGetEventById() {
		assertNotNull(new Event().getEventById(bUnit, 2));
		assertEquals("Silla rota", new Event().getEventById(bUnit, 2).getTitulo());
	}

}
