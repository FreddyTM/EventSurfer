
package main.java.company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.java.event.Event;
import main.java.persistence.PersistenceManager;

public class BusinessUnit {
	
	public static final String TABLE_NAME = "business_unit";
	private int id;
	private Company company;
	private String nombre;
	private String direccion;
	private String provincia;
	private String estado;
	private String cpostal;
	private String telefono;
	private String mail;
	private List<User> users = new ArrayList<User>();
	private List<Area> areas = new ArrayList<Area>();
	private List<Event> events = new ArrayList<Event>();
	
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
		PreparedStatement pstm = null;
		String sql = "INSERT INTO business_unit (company_id, nombre, direccion, provincia, "
				+ "estado, cpostal, telefono, mail) "
				+ "VALUES (?, ?, ?, ? ,? ,? ,? ,?);";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, bUnit.getCompany().getId());
			pstm.setString(2, bUnit.getNombre());
			pstm.setString(3, bUnit.getDireccion());
			pstm.setString(4, bUnit.getProvincia());
			pstm.setString(5, bUnit.getEstado());
			pstm.setString(6, bUnit.getCpostal());
			pstm.setString(7, bUnit.getTelefono());
			pstm.setString(8, bUnit.getMail());
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
	 * Si la inserción de una nueva unidad de negocio en la base de datos tiene éxito,
	 * recupera el id asignado en el registro de la base de datos y lo almacena en el id
	 * del objeto BusinessUnit
	 * @param conn conexión con la base de datos
	 * @param bUnit objeto a insertar en la base de datos
	 * @return objeto BusinessUnit con el id asignado
	 */
	public BusinessUnit addNewBusinessUnit (Connection conn, BusinessUnit bUnit) {
		if (saveBUnitToDB(conn, bUnit)) {
			int id = PersistenceManager.getLastElementIdFromDB(conn, TABLE_NAME);
			bUnit.setId(id);
			return bUnit;
		}
		return null;
	}
	
	/**
	 * Actualiza los valores de una unidad de negocio que ya existe en la base de datos
	 * @param conn conexión con la base de datos
	 * @param bUnit unidad de negocio que contiene los datos que actualizan
	 * a la unidad de negocio ya existente
	 * @return true si la actualización se hizo con éxito, false si no
	 */
	public boolean updateBusinessUnitToDB(Connection conn, BusinessUnit bUnit) {
		PreparedStatement pstm = null;
		String sql = "UPDATE business_unit "
				+ "SET "
				+ "company_id = ?, "
				+ "nombre = ?, "
				+ "direccion = ?, "
				+ "provincia = ?, "
				+ "estado = ?, "
				+ "cpostal = ?, "
				+ "telefono = ?, "
				+ "mail = ? "
				+ "WHERE id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, bUnit.getCompany().getId());
			pstm.setString(2, bUnit.getNombre());
			pstm.setString(3, bUnit.getDireccion());
			pstm.setString(4, bUnit.getProvincia());
			pstm.setString(5, bUnit.getEstado());
			pstm.setString(6, bUnit.getCpostal());
			pstm.setString(7, bUnit.getTelefono());
			pstm.setString(8, bUnit.getMail());
			pstm.setInt(9, bUnit.getId());
			pstm.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			PersistenceManager.closePrepStatement(pstm);
		}	
	}
	
//	/**
//	 * Actualiza los datos de una unidad de negocio que ya esté en la lista de unidades de negocio
//	 * de la empresa, si la actualización de dichos datos en la base de datos se realiza con éxito
//	 * @param conn conexión con la base de datos
//	 * @param bUnit unidad de negocio que contiene los datos que se actualizan
//	 */
//	public void updateBusinessUnit(Connection conn, BusinessUnit bUnit) {
//		if (updateBusinessUnitToDB(conn, bUnit)) {
//			this.company = bUnit.getCompany();
//			this.nombre = bUnit.getNombre();
//			this.direccion = bUnit.getDireccion();
//			this.provincia = bUnit.getProvincia();
//			this.estado = bUnit.getEstado();
//			this.cpostal = bUnit.getCpostal();
//			this.telefono = bUnit.getTelefono();
//			this.mail = bUnit.getMail();
//		}
//	}
	
	/**
	 * Obtiene la lista de BusinessUnits del objeto Company pasado por parámetro
	 * @param conn conexión con la base de datos
	 * @param company objeto del que queremos recuperar sus BusinessUnits
	 * @return lista de BusinessUnits del objeto almacenados en la base de datos
	 */
	public List<BusinessUnit> getBusinessUnitsFromDB(Connection conn, Company company) {
		List<BusinessUnit> bUnitsList = new ArrayList<BusinessUnit>();
		BusinessUnit bUnit = null;
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT id, nombre, direccion, provincia, estado, "
				+ "cpostal, telefono, mail "
				+ "FROM business_unit "
				+ "WHERE company_id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, company.getId());
			results = pstm.executeQuery();
			while (results.next()) {
				bUnit = new BusinessUnit();
				bUnit.setId(results.getInt(1));
				bUnit.setCompany(company);
				bUnit.setNombre(results.getString(2));
				bUnit.setDireccion(results.getString(3));
				bUnit.setProvincia(results.getString(4));
				bUnit.setEstado(results.getString(5));
				bUnit.setCpostal(results.getString(6));
				bUnit.setTelefono(results.getString(7));
				bUnit.setMail(results.getString(8));
				bUnitsList.add(bUnit);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
		}
		return bUnitsList;
	}
	
	
	/**
	 * Añade un usuario a la lista de usuarios si la inserción del usuario en la base de
	 * datos se hace con éxito
	 * @param conn conexión con la base de datos
	 * @param user usuario a añadir
	 */
	public void addUser (Connection conn, User user) {
		User completeUser = user.addNewUser(conn, user);
		if (completeUser != null) {
			users.add(completeUser);
		}
	}
	
	/**
	 * Añade un area a la lista de areas si la inserción del area en la base de
	 * datos se hace con éxito
	 * @param conn conexión con la base de datos
	 * @param area area a añadir
	 */
	public void addArea (Connection conn, Area area) {
		Area completeArea = area.addNewArea(conn, this, area);
		if (completeArea != null) {
			areas.add(completeArea);
		}
	}
	
	/**
	 * Añade un evento a la lista de eventos si la inserción del evento en la base de
	 * datos se hace con éxito
	 * @param conn conexión con la base de datos
	 * @param event evento a añadir
	 */
	public void addEvent(Connection conn, Event event) {
		Event completeEvent = event.addNewEvent(conn, event);
		if (completeEvent != null) {
			events.add(completeEvent);
		}
	}
	
	/**
	 * Devuelve la unidad de negocio al que pertenece el nombre entrado por parámetro
	 * @param company empresa de la que comprobamos la lista de unidades de negocio
	 * @param name nombre de la unidad de negocio buscada
	 * @return unidad de negocio con el nombre entrado por parámetro (null si no existe)
	 */
	public BusinessUnit getBusinessUnitByName (Company company, String name) {
		List<BusinessUnit> bUnitList = company.getBusinessUnits();
		for (BusinessUnit bUnit: bUnitList) {
			if (bUnit.getNombre().equals(name)) {
				return bUnit;
			}
		}	
		return null;
	}
	
	/**
	 * Devuelve la unidad de negocio al que pertenece el id entrado por parámetro
	 * @param bUnit BusinessUnit del que comprobamos la lista de usuarios
	 * @param id id del usuario buscado
	 * @return usuario con el id entrado por parámetro (null si no existe)
	 */
	public BusinessUnit getBusinessUnitById (Company company, int id) {
		List<BusinessUnit> bUnitList = company.getBusinessUnits();
		for (BusinessUnit bUnit: bUnitList) {
			if (bUnit.getId() == id) {
				return bUnit;
			}
		}	
		return null;
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

	public void setCompany(Company company) {
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
