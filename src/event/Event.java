
package event;


import java.util.ArrayList;
import java.util.List;

import company.BusinessUnit;

public class Event {

	private int id;
	private BusinessUnit bUnit;
	private String eventType;
	private String titulo;
	private String descripcion;
	private String eventState;
	private List<UpdatedEvent> updates;
	
	
	public Event(int id, BusinessUnit bUnit, String eventType, String titulo,
			String descripcion, String eventState) {
		this.id = id;
		this.bUnit = bUnit;
		this.eventType = eventType;
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.eventState = eventState;
		this.updates = new ArrayList<UpdatedEvent>();
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
	public List<UpdatedEvent> getUpdates() {
		return updates;
	}
	public void setUpdates(List<UpdatedEvent> updates) {
		this.updates = updates;
	}
	

	
}
