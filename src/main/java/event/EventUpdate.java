
package main.java.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import main.java.company.BusinessUnit;
import main.java.company.User;
import main.java.persistence.PersistenceManager;
import main.java.toolbox.ToolBox;

public class EventUpdate {

	public static final String TABLE_NAME = "event_update";
	private int id = 0;
	private Event event;
	private Timestamp fechaHora;
	private String descripcion;
	private String autor;
	private User user;
	
	public EventUpdate(int id, Event event, Timestamp fechaHora, String descripcion,
			String autor, User user) {
		this.id = id;
		this.event = event;
		this.fechaHora = fechaHora;
		this.descripcion = descripcion;
		this.autor = autor;
		this.user = user;
	}
	
	public EventUpdate() {
		
	}
	
	
	/**
	 * Inserta un nuevo EventUpdate en la base de datos
	 * @param conn conexión con la base de datos
	 * @param eUpdate EventUpdate a insertar
	 * @return true si la insercion se hizo con éxito, false si no 
	 */
	public boolean saveEventUpdateToDB (Connection conn, EventUpdate eUpdate) {
		PreparedStatement pstm = null;
		String sql = "INSERT INTO event_update (event_id, fecha_hora, descripcion, autor, user_id) "
				+ "VALUES (?, ?, ?, ?, ?);";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, 	eUpdate.getEvent().getId());
			pstm.setTimestamp(2, ToolBox.getTimestampNow());
			pstm.setString(3, eUpdate.getDescripcion());
			pstm.setString(4, eUpdate.getAutor());
			pstm.setInt(5, eUpdate.getUser().getId());
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
	 * Si la inserción de un nuevo update en la base de datos tiene éxito,
	 * recupera el id asignado en el registro de la base de datos y lo almacena
	 * en el id del objeto EventUpdate
	 * @param conn conexión con la base de datos
	 * @param eUpdate objeto a insertar en la base de datos
	 * @return objeto EventUpdate con el id asignado
	 */
	public EventUpdate addNewEventUpdate(Connection conn, EventUpdate eUpdate) {
		if (saveEventUpdateToDB (conn, eUpdate)) {
			int id = PersistenceManager.getLastElementIdFromDB(conn, TABLE_NAME);
			eUpdate.setId(id);
			return eUpdate;
		}
		return null;
	}
	
	/**
	 * Actualiza los valores de un EventUpdate que ya existe en la base de datos
	 * @param conn
	 * @param eUpdate
	 * @return true si la actualización se hizo con éxito, false si no
	 */
	public boolean updateEventUpdateToDB (Connection conn, EventUpdate eUpdate) {
		PreparedStatement pstm = null;
		String sql = "UPDATE event_update "
				+ "SET "
				+ "event_id = ?, "
				+ "fecha_hora = ?, "
				+ "descripcion = ?, "
				+ "autor = ?, "
				+ "user_id = ? "
				+ "WHERE id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, eUpdate.getEvent().getId());
			pstm.setTimestamp(2, eUpdate.getFechaHora());
			pstm.setString(3, eUpdate.getDescripcion());
			pstm.setString(4, eUpdate.getAutor());
			pstm.setInt(5, eUpdate.getUser().getId());
			pstm.setInt(6, eUpdate.getId());
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
	 * Obtiene la lista de EventUpdates del objeto Event pasado por parámetro
	 * @param conn conexión con la base de datos
	 * @param event objeto del que queremos recuperar sus updates
	 * @return lista de updates del objeto almacenados en la base de datos
	 */
	public List<EventUpdate> getEventUpdatesFromDB (Connection conn, Event event) {
		List<EventUpdate> updatesList = new ArrayList<EventUpdate>();
		EventUpdate eUpdate = null;
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT id, fecha_hora, descripcion, autor, user_id "
				+ "FROM event_update "
				+ "WHERE event_id = ?";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, event.getId());
			results = pstm.executeQuery();
			while (results.next()) {
				eUpdate = new EventUpdate();
				eUpdate.setId(results.getInt(1));
				eUpdate.setEvent(event);
				eUpdate.setFechaHora(results.getTimestamp(2));
				eUpdate.setDescripcion(results.getString(3));
				eUpdate.setAutor(results.getString(4));
				BusinessUnit bUnit = event.getbUnit();
				User user = new User().getUserById(bUnit, results.getInt(5));
				eUpdate.setUser(user);
				updatesList.add(eUpdate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
		}
		return updatesList;
	}
	
	/**
	 * Devuelve una lista de actualizaciones de evento que hayan sido realizadas por el 
	 * usuario cuyo id se pasa por parámetro
	 * @param event evento del que se buscan las actualizaciones
	 * @param userId id del usuario que ha hecho las actualizaciones del evento
	 * @return Lista de actualizaciones de evento realizadas por el usuario
	 */
	public List<EventUpdate> getEventUpdatesByUserId(Event event, int userId) {
		List<EventUpdate> updatesList = event.getUpdates();
		List<EventUpdate> returnList = new ArrayList<EventUpdate>();
		for (EventUpdate eUpdate: updatesList) {
			if (eUpdate.getUser().getId() == userId) {
				returnList.add(eUpdate);
			}
		}
		return returnList;
	}
	
	/**
	 * Devuelve una lista de actualizaciones de evento que hayan sido realizadas por el 
	 * usuario cuyo alias se pasa por parámetro
	 * @param event evento del que se buscan las actualizaciones
	 * @param userAlias alias del usuario que ha hecho las actualizaciones del evento
	 * @return Lista de actualizaciones de evento realizadas por el usuario
	 */
	public List<EventUpdate> getEventUpdatesByUserAlias(Event event, String userAlias) {
		List<EventUpdate> updatesList = event.getUpdates();
		List<EventUpdate> returnList = new ArrayList<EventUpdate>();
		for (EventUpdate eUpdate: updatesList) {
			if (eUpdate.getUser().getUserAlias().equals(userAlias)) {
				returnList.add(eUpdate);
			}
		}
		return returnList;
	}
	
	/**
	 * Devuelve el EventUpdate al que pertenece el id pasado por parámetro
	 * @param event Evento del que se busca la actualización
	 * @param id Id del EventUpdate
	 * @return EventUpdate con el id entrado por parámetro (null si no existe)
	 */
	public EventUpdate getEventUpdateById(Event event, int id) {
		List<EventUpdate> updatesList = event.getUpdates();
		for (EventUpdate eUpdate: updatesList) {
			if (eUpdate.getId() == id) {
				return eUpdate;
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

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Timestamp getFechaHora() {
		return fechaHora;
	}

	public void setFechaHora(Timestamp fechaHora) {
		this.fechaHora = fechaHora;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
		
}
