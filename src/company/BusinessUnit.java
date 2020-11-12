
package company;

import java.util.List;

public class BusinessUnit {
	
	private int id;
	private Company company;
	private String nombre;
	private String direccion;
	private String provincia;
	private String estado;
	private String cpostal;
	private String telefono;
	private String mail;
	private List<User> users;
	private List<Area> areas;
	
	
	public BusinessUnit(int id, Company company, String nombre, String direccion, String provincia, String estado,
			String cpostal, String telefono, String mail) {
		this.id = id;
		this.company = company;
		this.nombre = nombre;
		this.direccion = direccion;
		this.provincia = provincia;
		this.estado = estado;
		this.cpostal = cpostal;
		this.telefono = telefono;
		this.mail = mail;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompanyd(Company company) {
		this.company = company;
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


	public List<User> getUsers() {
		return users;
	}


	public void setUsers(List<User> users) {
		this.users = users;
	}


	public List<Area> getAreas() {
		return areas;
	}


	public void setAreas(List<Area> areas) {
		this.areas = areas;
	}
		
}
