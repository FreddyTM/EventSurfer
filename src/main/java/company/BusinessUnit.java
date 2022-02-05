
package main.java.company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.java.event.Event;
import main.java.persistence.PersistenceManager;

/**
 * Almacena la información referente a los centros de trabajo y proporciona los métodos
 * necesarios para realizar las operaciones CRUD con la base de datos y con el resto de
 * componentes del programa
 * @author Alfred Tomey
 */
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
	private boolean activo;
	private List<User> users = new ArrayList<User>();
	private List<Area> areas = new ArrayList<Area>();
	private List<Event> events = new ArrayList<Event>();
	
	public BusinessUnit(int id, Company company, String nombre, String direccion,
			String provincia, String estado, String cpostal, String telefono, String mail, boolean activo) {			
		this.id = id;
		this.company = company;
		this.nombre = nombre;
		this.direccion = direccion;
		this.provincia = provincia;
		this.estado = estado;
		this.cpostal = cpostal;
		this.telefono = telefono;
		this.mail = mail;
		this.activo = activo;
	}
	
	public BusinessUnit() {
		
	}

	/**
	 * Inserta una nuevo centro de trabajo en la base de datos
	 * @param conn conexión con la base de datos
	 * @param bUnit centro de trabajo a insertar
	 * @return true si la insercion se hizo con éxito, false si no
	 */
	public boolean saveBUnitToDB(Connection conn, BusinessUnit bUnit) {
		PreparedStatement pstm = null;
		String sql = "INSERT INTO business_unit (company_id, nombre, direccion, provincia, "
				+ "estado, cpostal, telefono, mail, activo) "
				+ "VALUES (?, ?, ?, ? ,? ,? ,? ,?, ?);";
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
			pstm.setBoolean(9, bUnit.isActivo());
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
	 * Si la inserción de un nuev centro de trabajo en la base de datos tiene éxito,
	 * recupera el id asignado en el registro de la base de datos y lo almacena en el id
	 * del objeto BusinessUnit
	 * @param conn conexión con la base de datos
	 * @param bUnit centro de trabajo a insertar en la base de datos
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
	 * Actualiza los valores de un centro de trabajo que ya existe en la base de datos
	 * @param conn conexión con la base de datos
	 * @param bUnit centro de trabajo que contiene los datos que actualizan
	 * al centro de trabajo ya existente
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
				+ "mail = ?, "
				+ "activo = ? "
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
			pstm.setBoolean(9, bUnit.isActivo());
			pstm.setInt(10, bUnit.getId());
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
	 * Obtiene la lista de centros de trabajo del objeto Company pasado por parámetro
	 * @param conn conexión con la base de datos
	 * @param company objeto del que queremos recuperar sus centros de trabajo
	 * @return lista de centros de trabajo del objeto almacenados en la base de datos
	 */
	public List<BusinessUnit> getBusinessUnitsFromDB(Connection conn, Company company) {
		List<BusinessUnit> bUnitsList = new ArrayList<BusinessUnit>();
		BusinessUnit bUnit = null;
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT id, nombre, direccion, provincia, estado, "
				+ "cpostal, telefono, mail, activo "
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
				bUnit.setActivo(results.getBoolean(9));
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
	 * Refresca los datos del centro de trabajo recargándolos de la base de datos
	 * @param conn conexión con la base de datos
	 * @return true si se refrescan los datos del centro de trabajo, false si no
	 */
	public boolean refresh(Connection conn) {
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT nombre, direccion, provincia, estado, "
				+ "cpostal, telefono, mail, activo "
				+ "FROM business_unit "
				+ "WHERE id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, id);
			results = pstm.executeQuery();
			while (results.next()) {
				this.nombre = results.getString(1);
				this.direccion = results.getString(2);
				this.provincia = results.getString(3);
				this.estado = results.getString(4);
				this.cpostal = results.getString(5);
				this.telefono = results.getString(6);
				this.mail = results.getString(7);
				this.activo = results.getBoolean(8);
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
		}
	}
	
	
	/**
	 * Añade un usuario a la lista de usuarios del centro de trabajo si la inserción del usuario
	 * en la base de datos se hace con éxito
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
	 * Añade un area a la lista de areas del centro de trabajo si la inserción del area en la base de
	 * datos se hace con éxito
	 * @param conn conexión con la base de datos
	 * @param area area a añadir
	 */
	public void addArea (Connection conn, Area area) {
		Area completeArea = area.addNewAreaToBunitArea(conn, this, area);
		if (completeArea != null) {
			areas.add(completeArea);
		}
	}
	
	/**
	 * Añade una incidencia a la lista de incidencias del centro de trabajo si la inserción de la
	 * incidencia en la base de datos se hace con éxito
	 * @param conn conexión con la base de datos
	 * @param event incidencia a añadir
	 */
	public void addEvent(Connection conn, Event event) {
		Event completeEvent = event.addNewEvent(conn, event);
		if (completeEvent != null) {
			events.add(completeEvent);
		}
	}
	
	/**
	 * Devuelve el centro de trabajo al que pertenece el nombre entrado por parámetro
	 * @param company empresa de la que comprobamos el centro de trabajo
	 * @param name nombre del centro de trabajo buscado
	 * @return centro de trabajo con el nombre entrado por parámetro (null si no existe)
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
	 * Devuelve el centro de trabajo al que pertenece el id entrado por parámetro
	 * @param company empresa de la que comprobamos el centro de trabajo
	 * @param id id del usuario buscado
	 * @return centro de trabajo con el id entrado por parámetro (null si no existe)
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
	
	/**
	 * Devuelve el centro de trabajo al que pertenece el id entrado por parámetro
	 * @param conn Conexión con la base de datos
	 * @param company empresa de la que comprobamos el centro de trabajo
	 * @param id id del usuario buscado
	 * @return centro de trabajo con el id entrado por parámetro (null si no existe)
	 */
	public BusinessUnit getBusinessUnitNameByIdFromDb(Connection conn, Company company, int id) {
		BusinessUnit bUnit = null;
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT nombre, direccion, provincia, estado, "
				+ "cpostal, telefono, mail, activo "
				+ "FROM business_unit "
				+ "WHERE id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, id);
			results = pstm.executeQuery();
			while (results.next()) {
				bUnit = new BusinessUnit();
				bUnit.setId(id);
				bUnit.setCompany(company);
				bUnit.setNombre(results.getString(1));
				bUnit.setDireccion(results.getString(2));
				bUnit.setProvincia(results.getString(3));
				bUnit.setEstado(results.getString(4));
				bUnit.setCpostal(results.getString(5));
				bUnit.setTelefono(results.getString(6));
				bUnit.setMail(results.getString(7));
				bUnit.setActivo(results.getBoolean(8));
			}
			return bUnit;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
		}
	}
	
	/**
	 * Obtiene todos los objetos BusinessUnit que tengan asignada el area pasada por parámetro
	 * @param conn Conexión con la base de datos
	 * @param company empresa a la que pertenecen las unidades de negocio
	 * @param area Area de la que se quiere saber dónde está asignada
	 * @return Lista de centros de trabajo con el area asignada
	 */
	public List<BusinessUnit> getBunitsWithArea(Connection conn, Company company, Area area) {
		List<BusinessUnit> bUnitList = new ArrayList<BusinessUnit>();
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT b_unit_id "
				+ "FROM b_unit_area "
				+ "WHERE area_id = ?"
				+ "ORDER BY b_unit_id;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, area.getId());
			results = pstm.executeQuery();
			while (results.next()) {
				bUnitList.add(new BusinessUnit().getBusinessUnitNameByIdFromDb(conn,company, results.getInt(1)));
			}
			return bUnitList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
		}
	}
	
	/**
	 * Obtiene una lista con los ids de los centros de trabajo a las que está asignada el area pasada
	 * por parámetro
	 * @param conn Conexión con la base de datos
	 * @param area area a comprobar
	 * @return Lista de ids de los centros de trabajo
	 */
	public List<Integer> getBunitsWithArea(Connection conn, Area area) {
		List<Integer> bUnitIds = new ArrayList<Integer>();
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT b_unit_id "
				+ "FROM b_unit_area "
				+ "WHERE area_id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, area.getId());
			results = pstm.executeQuery();
			while (results.next()) {
				bUnitIds.add(results.getInt(1));			
			}
			return bUnitIds;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
		}
	}
	
	/**
	 * Obtiene los nombres de todas los centros de trabajo que tengan asignada el area pasada por parámetro
	 * @param conn Conexión con la base de datos
	 * @param company empresa a la que pertenecen los centros de trabajo
	 * @param area Area de la que se quiere saber dónde está asignada
	 * @return Lista de nombres de los centros de trabajo con el area asignada
	 */
	public List<String> getAllBunitNamesWithArea(Connection conn, Company company, Area area) {
		List<String> bUnitNameList = new ArrayList<String>();
		List<BusinessUnit> bUnitList = getBunitsWithArea(conn, company, area);
		for (BusinessUnit bUnit : bUnitList) {
			bUnitNameList.add(bUnit.getNombre());
		}
		return bUnitNameList;
	}
	
	/**
	 * Obtiene la lista de todas los centros de trabajo de la base de datos
	 * @param conn conexión con la base de datos
	 * @param company objeto del que queremos recuperar sus centros de trabajo
	 * @return lista de nombres de los centros de trabajo almacenadas en la base de datos
	 */
	public List<String> getAllBunitNames(Connection conn, Company company) {
		List<String> bUnitNames = new ArrayList<String>();
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT nombre "
				+ "FROM business_unit "
				+ "WHERE company_id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, company.getId());
			results = pstm.executeQuery();
			while (results.next()) {
				bUnitNames.add(results.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
		}
		return bUnitNames;
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

	public Boolean isActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
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
