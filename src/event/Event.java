
package event;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import company.Area;
import company.BusinessUnit;
import persistence.PersistenceManager;
import types_states.TypesStatesContainer;

public class Event {

	public static final String TABLE_NAME = "event";
	private int id;
	private BusinessUnit bUnit;
	private Area area;
	private String eventType;
	private String titulo;
	private String descripcion;
	private String eventState;
	private List<EventUpdate> updates;
	
	
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
	
	public boolean saveEventToDB(Connection conn, Event event) {
		PreparedStatement pstm = null;
		String sql = "INSERT INTO event (b_unit_id, area_id, event_type_id, titulo, "
				+ "descripcion, event_state_id) "
				+ "VALUES (?, ?, ?, ?, ?, ?);";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, event.getbUnit().getId());
			pstm.setInt(2, area.getId());
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
	
	public Event() {
		
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
