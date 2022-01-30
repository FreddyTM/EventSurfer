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
import main.java.company.User;
import main.java.event.Event;
import main.java.event.EventUpdate;
import main.java.persistence.PersistenceManager;
import main.java.toolbox.ToolBox;
import main.java.types_states.EventState;
import main.java.types_states.EventType;
import main.java.types_states.TypesStatesContainer;
import main.java.types_states.UserType;

@TestMethodOrder(OrderAnnotation.class)
class EventUpdateTest {
	
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
		EventType evType = new EventType();
		evType.loadData(conn);
		TypesStatesContainer.setEvType(evType);
		EventState evState = new EventState();
		evState.loadData(conn);
		TypesStatesContainer.setEvState(evState);
		UserType uType = new UserType();
		uType.loadData(conn);
		TypesStatesContainer.setuType(uType);
		bUnit.setAreas(new Area().getAreasFromDB(conn, bUnit));
		bUnit.setEvents(new Event().getBunitEventsFromDB(conn, bUnit));
		bUnit.setUsers(new User().getUsersFromDB(conn, bUnit));
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
	void testSaveEventUpdateToDB() {
		EventUpdate eUpdate = new EventUpdate();
		Event event = bUnit.getEvents().get(0);
		eUpdate.setEvent(event);
		eUpdate.setFechaHora(ToolBox.getTimestampNow());
		eUpdate.setDescripcion("Aparecen tras el día de lluvia de ayer. "
				+ "Se colocan cubos para recoger el agua");
		eUpdate.setAutor("Juan Palomo");
		eUpdate.setUser(bUnit.getUsers().get(1));
		assertTrue(new EventUpdate().saveEventUpdateToDB(conn, eUpdate));
		eUpdate.setId(PersistenceManager.getLastElementIdFromDB(conn, EventUpdate.TABLE_NAME));
		event.getUpdates().add(eUpdate);
	}

	@Test
	@Order(2)
	void testAddNewEventUpdate() {
		EventUpdate eUpdate = new EventUpdate();
		Event event = bUnit.getEvents().get(1);
		eUpdate.setEvent(event);
		eUpdate.setFechaHora(ToolBox.getTimestampNow());
		eUpdate.setDescripcion("Retiramos la mesa al almacén");
		eUpdate.setAutor("Pepe Gotera");
		eUpdate.setUser(bUnit.getUsers().get(2));
		assertNotNull(new EventUpdate().addNewEventUpdate(conn, eUpdate));
		assertNotEquals(0, eUpdate.getId());
		assertEquals(2, eUpdate.getId());
		event.getUpdates().add(eUpdate);
	}

	@Test
	@Order(7)
	void testUpdateEventUpdateToDB() {
		Event event = bUnit.getEvents().get(1);
		EventUpdate oldEupdate = new EventUpdate().getEventUpdateById(event, 2);
		EventUpdate newEupdate = new EventUpdate();
		newEupdate.setId(oldEupdate.getId());
		newEupdate.setEvent(event);
		newEupdate.setFechaHora(ToolBox.getTimestampNow());
		newEupdate.setDescripcion("Retiramos la mesa al almacén y desmontamos la pata rota");
		newEupdate.setAutor("Pepe Gotera");
		newEupdate.setUser(oldEupdate.getUser());
		assertTrue(new EventUpdate().updateEventUpdateToDB(conn, newEupdate));
		oldEupdate = newEupdate;
	}

	@Test
	@Order(3)
	void testGetEventUpdatesFromDB() {
		Event event1 = bUnit.getEvents().get(0);
		Event event2 = bUnit.getEvents().get(1);
		assertNotNull(new EventUpdate().getEventUpdatesFromDB(conn, event1));
		assertNotNull(new EventUpdate().getEventUpdatesFromDB(conn, event2));
	}

	@Test
	@Order(4)
	void testGetEventUpdatesByUserAlias() {
		Event event1 = bUnit.getEvents().get(0);
		Event event2 = bUnit.getEvents().get(1);
		event1.setUpdates(new EventUpdate().getEventUpdatesFromDB(conn, event1));
		event2.setUpdates(new EventUpdate().getEventUpdatesFromDB(conn, event2));
		assertEquals(1, new EventUpdate().getEventUpdatesByUserAlias(event1, "FakeManager").size());
		assertEquals("Juan Palomo", new EventUpdate().getEventUpdatesByUserAlias(event1, "FakeManager").get(0).getAutor());
		assertEquals(1, new EventUpdate().getEventUpdatesByUserAlias(event2, "BigFakeUser").size());
		assertEquals("Pepe Gotera", new EventUpdate().getEventUpdatesByUserAlias(event2, "BigFakeUser").get(0).getAutor());
	}
	
	@Test
	@Order(5)
	void testGetEventUpdatesByUserId() {
		Event event1 = bUnit.getEvents().get(0);
		Event event2 = bUnit.getEvents().get(1);
		event1.setUpdates(new EventUpdate().getEventUpdatesFromDB(conn, event1));
		event2.setUpdates(new EventUpdate().getEventUpdatesFromDB(conn, event2));
		assertEquals(1, new EventUpdate().getEventUpdatesByUserId(event1, 2).size());
		assertEquals("Juan Palomo", new EventUpdate().getEventUpdatesByUserId(event1, 2).get(0).getAutor());
		assertEquals(1, new EventUpdate().getEventUpdatesByUserId(event2, 3).size());
		assertEquals("Pepe Gotera", new EventUpdate().getEventUpdatesByUserId(event2, 3).get(0).getAutor());
	}
	
	@Test
	@Order(6)
	void testGetEventUpdateById() {
		Event event1 = bUnit.getEvents().get(0);
		Event event2 = bUnit.getEvents().get(1);
		event1.setUpdates(new EventUpdate().getEventUpdatesFromDB(conn, event1));
		event2.setUpdates(new EventUpdate().getEventUpdatesFromDB(conn, event2));
		assertNotNull(new EventUpdate().getEventUpdateById(event1, 1));
		assertEquals("Juan Palomo", new EventUpdate().getEventUpdateById(event1, 1).getAutor());
		assertNotNull(new EventUpdate().getEventUpdateById(event2, 2));
		assertEquals("Pepe Gotera", new EventUpdate().getEventUpdateById(event2, 2).getAutor());
	}

	@Test
	@Order(7)
	void testDeleteEventUpdateFromDb() {
		EventUpdate update = new EventUpdate();
		update.setId(58);
		assertTrue(new EventUpdate().deleteEventUpdateFromDb(conn, update));
	}
	@Test
	@Order(8)
	void testDeleteAllEventUpdatesFromDb() {
		Event event = new Event();
		event.setId(37);
		assertTrue(new EventUpdate().deleteAllEventUpdatesFromDb(conn, event));
	}
}
