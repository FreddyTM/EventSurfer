
package main.java.event;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
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
	 * Inserta una nueva incidencia en la base de datos
	 * @param conn conexión con la base de datos
	 * @param event incidencia a insertar
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
	 * Si la inserción de una nueva incidencia en la base de datos tiene éxito,
	 * recupera el id asignado en el registro de la base de datos y lo almacena
	 * en el id del objeto Event
	 * @param conn conexión con la base de datos
	 * @param event incidencia a insertar en la base de datos
	 * @returnobjeto incidencia con el id asignado
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
	 * Actualiza los valores de una incidencia que ya existe en la base de datos
	 * @param conn conexión con la base de datos
	 * @param event incidencia que contiene los datos que actualizan a la incidencia ya existente
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
	 * Actualiza el estado de una incidencia que ya existe en la base de datos
	 * @param conn conexión con la base de datos
	 * @param event incidencia que contiene el estado actualizado
	 * @return true si la actualización se hizo con éxito, false si no
	 */
	public boolean updateEventStateOfEventToDB(Connection conn, Event event) {
		PreparedStatement pstm = null;
		String sql = "UPDATE \"event\" "
				+ "SET "
				+ "event_state_id = ? "
				+ "WHERE id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, TypesStatesContainer.getEvState().getEventStateId(event.getEventState()));
			pstm.setInt(2, event.getId());
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
	 * Obtiene la lista de incidencias del centro de trabajo pasado por parámetro
	 * @param conn conexión con la base de datos
	 * @param bUnit centro de trabajo del que queremos recuperar sus incidencias
	 * @return lista de incidencias del centro de trabajo almacenados en la base de datos
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
				event.setEventType(TypesStatesContainer.getEvType().getEventTypeDescription(results.getInt(3)));
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
	 * Obtiene el número de veces que un tipo de incidencia está registrado en alguna incidencia en la base de datos
	 * @param conn conexión con la base de datos
	 * @param id id del tipo de incidencia
	 * @return número de veces que el tipo de incidencia está registrado en una incidencia (-1 si la consulta falla)
	 */
	public int getEventTypesOnEventFromDB(Connection conn, int id) {
		int eventTypes = 0;
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT COUNT (event_type_id) "
				+ "FROM \"event\" "
				+ "WHERE event_type_id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, id);
			results = pstm.executeQuery();
			while (results.next()) {
				eventTypes = results.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
		}
		return eventTypes;
	}
	
	/**
	 * Obtiene la lista de incidencias sucedidas en el area pasada por parámetro
	 * @param conn conexión con la base de datos
	 * @param area area del que queremos recuperar sus incidencias
	 * @param company empresa a la que pertenecen los centros de trabajo en los que suceden las incidencias
	 * @return lista de incidencias sucedidos en el area almacenados en la base de datos
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
				event.setEventType(TypesStatesContainer.getEvType().getEventTypeDescription(results.getInt(3)));
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
	 * Devuelve la incidencia al que pertenece el id entrado por parámetro
	 * @param bUnit centro de trabajo del que comprobamos la lista de incidencias
	 * @param id id de la incidencia buscada
	 * @return incidencia con el id entrado por parámetro (null si no existe)
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
	
	/**
	 * Obtiene una lista con los ids de los centros de trabajo en las que hay incidencias
	 * registrados con el tipo de incidencia del id pasado por parámetro
	 * @param conn Conexión con la base de datos
	 * @param id id del tipo de incidencia a comprobar
	 * @return Lista de ids de los centros de trabajo
	 */
	public List<Integer> getBunitsIdsWithEventTypes(Connection conn, int id) {
		List<Integer> bUnitIds = new ArrayList<Integer>();
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT DISTINCT b_unit_id "
				+ "FROM event "
				+ "WHERE event_type_id = ? "
				+ "ORDER BY b_unit_id;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, id);
			results = pstm.executeQuery();
			while (results.next()) {
				bUnitIds.add(results.getInt(1));			
			}
			return bUnitIds;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
		}
	}
	
	/**
	 * Borra una incidencia de la base de datos 
	 * @param conn conexión a la base de datos
	 * @param event incidencia a borrar de la base de datos
	 * @return true si el borrado se hizo con éxito, false si no
	 */
	public boolean deleteEventFromDb(Connection conn, Event event) {
		PreparedStatement pstm = null;
		String sql = "DELETE FROM \"event\" "
				+ "WHERE id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, event.getId());
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
	 * Borra de la lista de incidencias del centro de trabajo pasado por parámetro
	 * la incidencia pasada por parámetro, si es que está en la lista
	 * @param bUnit centro de trabajo del que borramos la incidencia
	 * @param event incidencia a borrar
	 * @return true si se ha borrado alguna incidencia, false si no
	 */
	public boolean deleteEvent(BusinessUnit bUnit, Event event) {
		boolean eventDeleted = false;
		//Using Iterator for safe remove
		Iterator<Event> iterator = bUnit.getEvents().iterator();
		while(iterator.hasNext()) {
			Event ev = (Event) iterator.next();
			if (ev.getId() == event.getId()) {
				iterator.remove();
				eventDeleted = true;
				break;
			}
		}
		return eventDeleted;
	}
	
	/**
	 * Busca la actualización incial de la incidencia
	 * @return actualización inicial de la incidencia
	 */
	public EventUpdate findFirstUpdate() {
		//Si solo hay una actualización, la actualización inicial es esa
		if (updates.size() == 1) {
			return updates.get(0);
		} 
		EventUpdate firstUpdate = updates.get(0);
		int i;
		for (i = 1; i < updates.size(); i++) {
			if (firstUpdate.getFechaHora().after(updates.get(i).getFechaHora())) {
				firstUpdate = updates.get(i);
			}
		}
		return firstUpdate;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
            return true;
		if((obj == null) || (obj.getClass() != this.getClass()))
			return false;
        Event event = (Event) obj;
        return id == event.getId() && (bUnit.getNombre().equals(event.getbUnit().getNombre()));
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
	    hash = 31 * hash + id;
	    hash = 31 * hash + (bUnit.getNombre() == null ? 0 : bUnit.getNombre().hashCode());
	    return hash;
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
