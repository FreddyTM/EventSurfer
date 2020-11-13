package company;

import java.sql.Connection;

public class Area {

	private Connection connection;
	private int id;
	private String area;
	private String descripcion;
	
	
	public Area(Connection connection, int id, String area, String descripcion) {
		this.connection = connection;
		this.id = id;
		this.area = area;
		this.descripcion = descripcion;
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
}
