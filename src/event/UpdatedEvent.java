
package event;

import java.sql.Timestamp;

public class UpdatedEvent {

	private int id = 0;
	private Event event;
	private Timestamp fechaHora;
	private String descripcion;
	private String autor;
	
	public UpdatedEvent(int id, Event event, Timestamp fechaHora, String descripcion,
			String autor) {
		this.id = id;
		this.event = event;
		this.fechaHora = fechaHora;
		this.descripcion = descripcion;
		this.autor = autor;
	}
	
	public UpdatedEvent() {
		
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
