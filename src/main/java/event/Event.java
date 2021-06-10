
package main.java.event;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.java.company.Area;
import main.java.company.BusinessUnit;
import main.java.company.Company;
import main.java.company.User;
import main.java.persistence.PersistenceManager;
import main.java.types_states.TypesStatesContainer;

public class Event {

	public static final String TABLE_NAME = "event";
	private int id;
	private BusinessUnit bUnit;
	private Area area;
	private String eventType;
	private String titulo;
	private String descripcion;
	private String eventState;
	private List<EventUpdate> updates = new ArrayList<EventUpdate>();
	
	
	public Event(int id, BusinessUnit bUnit, Area area, String eventType, String titulo,
			String descripcion, String eventState) {
		this.id = id;
		this.bUnit = bUnit;
		this.area = area;
		this.eventType = eventType;
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.eventState = eventState;
		this.updates = new ArrayList<EventUpdate>();
	}
	
	public Event() {
		
	}
	
	
	/**
	 * Inserta un nuevo evento en la base de datos
	 * @param conn conexión con la base de datos
	 * @param event usuario a insertar
	 * @return true si la insercion se hizo con éxito, false si no 
	 */
	public boolean saveEventToDB(Connection conn, Event event) {
		PreparedStatement pstm = null;
		String sql = "INSERT INTO \"event\" (b_unit_id, area_id, event_type_id, titulo, "
				+ "descripcion, event_state_id) "
				+ "VALUES (?, ?, ?, ?, ?, ?);";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, event.getbUnit().getId());
			pstm.setInt(2, event.getArea().getId());
			pstm.setInt(3, TypesStatesContainer.getEvType().getEventTypeId(event.getEventType()));
			pstm.setString(4, event.getTitulo());
			pstm.setString(5, event.getDescripcion());
			pstm.setInt(6, TypesStatesContainer.getEvState().getEventStateId(event.getEventState()));
			pstm.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			PersistenceManager.closePrepStatement(pstm);
		}
	}

	/**
	 * Si la inserción de un nuevo evento en la base de datos tiene éxito,
	 * recupera el id asignado en el registro de la base de datos y lo almacena
	 * en el id del objeto Event
	 * @param conn conexión con la base de datos
	 * @param event objeto a insertar en la base de datos
	 * @returnobjeto Event con el id asignado
	 */
	public Event addNewEvent(Connection conn, Event event) {
		if (saveEventToDB(conn, event)) {
			int id = PersistenceManager.getLastElementIdFromDB(conn, TABLE_NAME);
			event.setId(id);
			return event;
		}
		return null;
	}
	
	/**
	 * Actualiza los valores de un evento que ya existe en la base de datos
	 * @param conn conexión con la base de datos
	 * @param event evento que contiene los datos que actualizan al evento ya existente
	 * @return true si la actualización se hizo con éxito, false si no
	 */
	public boolean updateEventToDB(Connection conn, Event event) {
		PreparedStatement pstm = null;
		String sql = "UPDATE \"event\" "
				+ "SET "
				+ "b_unit_id = ?, "
				+ "area_id = ?, "
				+ "event_type_id = ?, "
				+ "titulo = ?, "
				+ "descripcion = ?, "
				+ "event_state_id = ? "
				+ "WHERE id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, event.getbUnit().getId());
			pstm.setInt(2, event.getArea().getId());
			pstm.setInt(3, TypesStatesContainer.getEvType().getEventTypeId(event.getEventType()));
			pstm.setString(4, event.getTitulo());
			pstm.setString(5, event.getDescripcion());
			pstm.setInt(6, TypesStatesContainer.getEvState().getEventStateId(event.getEventState()));
			pstm.setInt(7, event.getId());
			pstm.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			PersistenceManager.closePrepStatement(pstm);
		}
	}
	
	/**
	 * Obtiene la lista de eventos del objeto BusinessUnit pasado por parámetro
	 * @param conn conexión con la base de datos
	 * @param bUnit unidad de negocio de la que queremos recuperar sus eventos
	 * @return lista de eventos de la unidad de negocio almacenados en la base de datos
	 */
	public List<Event> getBunitEventsFromDB(Connection conn, BusinessUnit bUnit) {
		List<Event> eventList = new ArrayList<Event>();
		Event event = null;
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT id, area_id, event_type_id, titulo, descripcion, event_state_id "
				+ "FROM \"event\" "
				+ "WHERE b_unit_id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, bUnit.getId());
			results = pstm.executeQuery();
			while (results.next()) {
				event = new Event();
				event.setId(results.getInt(1));
				event.setbUnit(bUnit);
				Area area = new Area().getAreaById(bUnit, results.getInt(2));
				event.setArea(area);
				event.setEventType(TypesStatesContainer.getEvType().getEventType(results.getInt(3)));
				event.setTitulo(results.getString(4));
				event.setDescripcion(results.getString(5));
				event.setEventState(TypesStatesContainer.getEvState().getEventState(results.getInt(6)));
				eventList.add(event);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
		}
		return eventList;
	}
	
	/**
	 * Obtiene la lista de eventos sucedidos en el area pasada por parámetro
	 * @param conn conexión con la base de datos
	 * @param area area del que queremos recuperar sus eventos
	 * @param company empresa a la que pertenecen las unidades de negocio en la que suceden los eventos
	 * @return lista de eventos sucedidos en el area almacenados en la base de datos
	 */
	public List<Event> getAreaEventsFromDB(Connection conn, Area area, Company company) {
		List<Event> eventList = new ArrayList<Event>();
		Event event = null;
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT id, b_unit_id, event_type_id, titulo, descripcion, event_state_id "
				+ "FROM \"event\" "
				+ "WHERE area_id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, area.getId());
			results = pstm.executeQuery();
			while (results.next()) {
				event = new Event();
				event.setId(results.getInt(1));
				BusinessUnit bUnit = new BusinessUnit().getBusinessUnitById(company, results.getInt(2));
				event.setbUnit(bUnit);
				event.setArea(area);
				event.setEventType(TypesStatesContainer.getEvType().getEventType(results.getInt(3)));
				event.setTitulo(results.getString(4));
				event.setDescripcion(results.getString(5));
				event.setEventState(TypesStatesContainer.getEvState().getEventState(results.getInt(6)));
				eventList.add(event);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
		}
		return eventList;
	}
	
	/**
	 * Devuelve el usuario al que pertenece el id entrado por parámetro
	 * @param bUnit BusinessUnit del que comprobamos la lista de usuarios
	 * @param id id del usuario buscado
	 * @return usuario con el id entrado por parámetro (null si no existe)
	 */
	public Event getEventById (BusinessUnit bUnit, int id) {
		List<Event> eventList = bUnit.getEvents();
		for (Event event: eventList) {
			if (event.getId() == id) {
				return event;
			}
		}	
		return null;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public BusinessUnit getbUnit() {
		return bUnit;
	}
	public void setbUnit(BusinessUnit bUnit) {
		this.bUnit = bUnit;
	}
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getEventState() {
		return eventState;
	}
	public void setEventState(String eventState) {
		this.eventState = eventState;
	}
	public List<EventUpdate> getUpdates() {
		return updates;
	}
	public void setUpdates(List<EventUpdate> updates) {
		this.updates = updates;
	}
	

	
}
