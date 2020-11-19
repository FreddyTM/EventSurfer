
package event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import persistence.PersistenceManager;

public class EventUpdate {

	private int id = 0;
	private Event event;
	private Timestamp fechaHora;
	private String descripcion;
	private String autor;
	
	public EventUpdate(int id, Event event, Timestamp fechaHora, String descripcion,
			String autor) {
		this.id = id;
		this.event = event;
		this.fechaHora = fechaHora;
		this.descripcion = descripcion;
		this.autor = autor;
	}
	
	public EventUpdate() {
		
	}
	
	
	/**
	 * Inserta un nuevo EventUpdate en la base de datos
	 * @param conn conexión con la base de datos
	 * @param eUpdate EventUpdate a insertar
	 * @return true si la insercion se hizo con éxito, false si no 
	 */
	public boolean saveEventUpdateToDB (Connection conn, EventUpdate eUpdate ) {
		String sql = "INSERT INTO event_update (event_id, fecha_hora, descripcion, autor) "
				+ "VALUES (?, ?, ?, ?);";
		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			pstm.setInt(1, 	eUpdate.getId());
			Calendar calendar = Calendar.getInstance();
			Date dNow = calendar.getTime();
			Timestamp tNow = new Timestamp(dNow.getTime());
			pstm.setTimestamp(2, tNow);
			pstm.setString(3, eUpdate.getDescripcion());
			pstm.setString(4, eUpdate.getAutor());
			pstm.executeUpdate();
			PersistenceManager.closePrepStatement(pstm);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Actualiza los valores de un EventUpdate que ya existe en la base de datos
	 * @param conn
	 * @param eUpdate
	 * @return true si la actualización se hizo con éxito, false si no
	 */
	public boolean updateEventUpdateToDB (Connection conn, EventUpdate eUpdate) {
		String sql = "UPDATE event_update "
				+ "SET "
				+ "event_id = ?, "
				+ "fecha_hora = ?, "
				+ "descripcion = ?, "
				+ "autor = ? "
				+ "WHERE id = ?;";
		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			pstm.setInt(1, eUpdate.getEvent().getId());
			pstm.setTimestamp(2, eUpdate.getFechaHora());
			pstm.setString(3, eUpdate.getDescripcion());
			pstm.setString(4, eUpdate.getAutor());
			pstm.setInt(5, eUpdate.getId());
			pstm.executeUpdate();
			PersistenceManager.closePrepStatement(pstm);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
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
	

	
	
}
