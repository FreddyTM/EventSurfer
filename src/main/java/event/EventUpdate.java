
package main.java.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import main.java.company.BusinessUnit;
import main.java.company.Company;
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
	 * Inserta una nueva actualización de incidencia en la base de datos
	 * @param conn conexión con la base de datos
	 * @param eUpdate actualización de incidencia a insertar
	 * @return true si la insercion se hizo con éxito, false si no 
	 */
	public boolean saveEventUpdateToDB (Connection conn, EventUpdate eUpdate) {
		PreparedStatement pstm = null;
		String sql = "INSERT INTO event_update (event_id, fecha_hora, descripcion, autor, user_id) "
				+ "VALUES (?, ?, ?, ?, ?);";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, 	eUpdate.getEvent().getId());
			pstm.setTimestamp(2, eUpdate.getFechaHora());
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
	 * Si la inserción de una nueva actualización de incidencia en la base de datos tiene éxito,
	 * recupera el id asignado en el registro de la base de datos y lo almacena en la actualización
	 * de incidencia
	 * @param conn conexión con la base de datos
	 * @param eUpdate actualización de incidencia a insertar en la base de datos
	 * @return actualización de incidencia con el id asignado
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
	 * Actualiza los valores de una actualización de incidencia que ya existe en la base de datos
	 * @param conn conexión con la base de datos
	 * @param eUpdate actualización de incidencia a actualizar
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
	 * Obtiene la lista de actualizaciones de la incidencia pasada por parámetro
	 * @param conn conexión con la base de datos
	 * @param event incidencia del que queremos recuperar sus actualizaciones
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
				Company company = bUnit.getCompany();
				User user = new User().getUserById(company.getAllCompanyUsers(), results.getInt(5));
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
	 * Devuelve una lista de actualizaciones de una incidencia que hayan sido realizadas por el 
	 * usuario cuyo id se pasa por parámetro
	 * @param event incidencia de la que se buscan las actualizaciones
	 * @param userId id del usuario que ha hecho las actualizaciones de la incidencia
	 * @return Lista de actualizaciones de la incidencia realizadas por el usuario
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
	 * Devuelve una lista de actualizaciones de incidencia que hayan sido realizadas por el 
	 * usuario cuyo alias se pasa por parámetro
	 * @param event incidencia de la que se buscan las actualizaciones
	 * @param userAlias alias del usuario que ha hecho las actualizaciones de la incidencia
	 * @return Lista de actualizaciones de incidencia realizadas por el usuario
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
	 * Devuelve la actualización de incidencia a la que pertenece el id pasado por parámetro
	 * @param event incidencia del que se busca la actualización
	 * @param id Id de la actualización
	 * @return actualización de incidencia con el id entrado por parámetro (null si no existe)
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
	
	/**
	 * Borra una actualización de incidencia de la base de datos 
	 * @param conn conexión a la base de datos
	 * @param eUpdate actualización de incidencia a borrar de la base de datos
	 * @return true si el borrado se hizo con éxito, false si no
	 */
	public boolean deleteEventUpdateFromDb(Connection conn, EventUpdate eUpdate) {
		PreparedStatement pstm = null;
		String sql = "DELETE FROM event_update "
				+ "WHERE id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, eUpdate.getId());
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
	 * Borra de la lista de actualizaciones de la incidencia pasada por parámetro
	 * la actualización pasada por parámetro, si es que está en la lista
	 * @param event incidencia de la que borramos la actualización
	 * @param event incidencia a borrar
	 * @return true si se ha borrado alguna incidencia, false si no
	 */
	public boolean deleteEventUpdate(Event event, EventUpdate eUpdate) {
		boolean updateDeleted = false;
		//Using Iterator for safe remove
		Iterator<EventUpdate> iterator = event.getUpdates().iterator();
		while(iterator.hasNext()) {
			EventUpdate eu = (EventUpdate) iterator.next();
			if (eu.getId() == eUpdate.getId()) {
				iterator.remove();
				updateDeleted = true;
				break;
			}
		}
		return updateDeleted;
	}
	
	/**
	 * Borra de la base de datos todos las actualizaciones de una incidencia pasado por parámetro 
	 * @param conn conexión a la base de datos
	 * @param event incidencia  de la que se borrarán todas las actualizaciones
	 * @return true si el borrado se hizo con éxito, false si no
	 */
	public boolean deleteAllEventUpdatesFromDb(Connection conn, Event event) {
		PreparedStatement pstm = null;
		String sql = "DELETE FROM event_update "
				+ "WHERE event_id = ?;";
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
            return true;
		if((obj == null) || (obj.getClass() != this.getClass()))
			return false;
        EventUpdate update = (EventUpdate) obj;
        return id == update.getId() && (event.getId() == update.getEvent().getId())
        		&& user.getUserAlias().equals(update.getUser().getUserAlias());
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
	    hash = 31 * hash + id;
	    hash = 31 * hash + event.getId();
	    hash = 31 * hash + user.getUserAlias().hashCode();
	    return hash;
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
