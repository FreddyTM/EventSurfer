
package company;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class Company {
	
	private Connection connection;
	private int id;
	private String nombre;
	private String direccion;
	private String provincia;
	private String estado;
	private String cpostal;
	private String telefono;
	private String mail;
	private String web;
	private List<BusinessUnit> businessUnits;
	
	
	public Company(Connection connection, int id, String nombre, String direccion, String provincia,
			String estado, String cpostal, String telefono, String mail, String web) {
		this.connection = connection;
		this.id = id;
		this.nombre = nombre;
		this.direccion = direccion;
		this.provincia = provincia;
		this.estado = estado;
		this.cpostal = cpostal;
		this.telefono = telefono;
		this.mail = mail;
		this.web = web;
	}
	
	
	public void loadBusinessUnits() {
		Statement statement = null;
		ResultSet results = null;
		String sql = "SELECT * FROM business_unit;";
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getDireccion() {
		return direccion;
	}
	
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
	public String getProvincia() {
		return provincia;
	}
	
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	
	public String getEstado() {
		return estado;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public String getCpostal() {
		return cpostal;
	}
	
	public void setCpostal(String cpostal) {
		this.cpostal = cpostal;
	}
	
	public String getTelefono() {
		return telefono;
	}
	
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
	public String getMail() {
		return mail;
	}
	
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	public String getWeb() {
		return web;
	}
	
	public void setWeb(String web) {
		this.web = web;
	}
	
	public List<BusinessUnit> getBusinessUnits() {
		return businessUnits;
	}
	
	public void setBusinessUnits(List<BusinessUnit> businessUnits) {
		this.businessUnits = businessUnits;
	}
	
}
