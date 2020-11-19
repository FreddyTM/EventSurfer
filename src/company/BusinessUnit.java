
package company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import event.Event;
import persistence.PersistenceManager;

public class BusinessUnit {
	
	//private Connection connection;
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
	private List<Event> events;
	
	public BusinessUnit(int id, Company company, String nombre, String direccion,
			String provincia, String estado, String cpostal, String telefono, String mail) {			
		//this.connection = connection;
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
	
	public BusinessUnit() {
		
	}

	/**
	 * Inserta una nueva unidad de negocio en la base de datos
	 * @param conn conexión con la base de datos
	 * @param bUnit BusinessUnit a insertar
	 * @return true si la insercion se hizo con éxito, false si no
	 */
	public boolean saveBUnitToDB(Connection conn, BusinessUnit bUnit) {
		String sql = "INSERT INTO business_unit (company_id, nombre, direccion, provincia, "
				+ "estado, cpostal, telefono, mail) "
				+ "VALUES (?, ?, ?, ? ,? ,? ,? ,?);";
		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			pstm.setInt(1, bUnit.getCompany().getId());
			pstm.setString(2, bUnit.getNombre());
			pstm.setString(3, bUnit.getDireccion());
			pstm.setString(4, bUnit.getProvincia());
			pstm.setString(5, bUnit.getEstado());
			pstm.setString(6, bUnit.getCpostal());
			pstm.setString(7, bUnit.getTelefono());
			pstm.setString(8, bUnit.getMail());
			pstm.executeUpdate();
			PersistenceManager.closePrepStatement(pstm);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Actualiza los valores de una unidad de negocio que ya existe en la base de datos
	 * @param conn conexión con la base de datos
	 * @param bUnit unidad de negocio que contiene los datos que actualizan
	 * a la unidad de negocio ya existente
	 * @return true si la actualización se hizo con éxito, false si no
	 */
	public boolean updateBusinessUnitToDB(Connection conn, BusinessUnit bUnit) {
		String sql = "";
		try {
			PreparedStatement pstm = conn.prepareStatement(sql);
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	/**
	 * Añade un usuario a la lista de usuarios si la inserción del usuario en la base de
	 * datos se hace con éxito
	 * @param conn conexión con la base de datos
	 * @param user usuario a añadir
	 */
	public void addUser (Connection conn, User user) {
		if (user.saveUserToDB(conn, user)) {
			users.add(user);
		}
	}
	
	/**
	 * Añade un area a la lista de areas si la inserción del area en la base de
	 * datos se hace con éxito
	 * @param conn conexión con la base de datos
	 * @param area area a añadir
	 */
	public void addArea (Connection conn, Area area) {
		if (area.saveAreaToDB(conn, this, area)) {
			areas.add(area);
		}
	}
	
	/**
	 * Añade un evento a la lista de eventos si la inserción del evento en la base de
	 * datos se hace con éxito
	 * @param conn conexión con la base de datos
	 * @param event evento a añadir
	 */
	public void addEvent(Connection conn, Event event) {
		
	}

	
	//METODO DE PRUEBA. SE PUEDE BORRAR
	public void pruebaEnviaObjeto () {
		User user = new User();
		user.prueba(this);
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

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
		
}
