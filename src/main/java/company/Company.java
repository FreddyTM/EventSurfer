
package main.java.company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import main.java.persistence.PersistenceManager;

public class Company {
	
	public static final String TABLE_NAME = "company";
	private int id;
	private String nombre;
	private String direccion;
	private String provincia;
	private String estado;
	private String cpostal;
	private String telefono;
	private String mail;
	private String web;
	private List<BusinessUnit> businessUnits = new ArrayList<BusinessUnit>();
	private List<User> allCompanyUsers;
	
	public Company(int id, String nombre, String direccion, String provincia,
			String estado, String cpostal, String telefono, String mail, String web) {
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
	
	public Company() {
		
	}
	
	/**
	 * Actualiza los datos de una empresa que ya existe en la base de datos
	 * @param conn conexión con la base de datos
	 * @param company empresa que contiene los datos que se actualizan
	 * @return true si la actualización se hizo con éxito, false si no
	 */
	public boolean updateCompanyToDB(Connection conn, Company company) {
		PreparedStatement pstm = null;
		String sql = "UPDATE company "
				+ "SET "
				+ "nombre = ?, "
				+ "direccion = ?, "
				+ "provincia = ?, "
				+ "estado = ?, "
				+ "cpostal = ?, "
				+ "telefono = ?, "
				+ "mail = ?, "
				+ "web = ? "
				+ "WHERE id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, company.getNombre());
			pstm.setString(2, company.getDireccion());
			pstm.setString(3, company.getProvincia());
			pstm.setString(4, company.getEstado());
			pstm.setString(5, company.getCpostal());
			pstm.setString(6, company.getTelefono());
			pstm.setString(7, company.getMail());
			pstm.setString(8, company.getWeb());
			pstm.setInt(9, company.getId());
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
	 * Actualiza los datos de la empresa que ya existe en la base de datos si la actualización
	 * de dichos datos en la base de datos se realiza con éxito
	 * @param conn conexión con la base de datos
	 * @param company empresa que contiene los datos que se actualizan
	 */
	public boolean updateCompany (Connection conn, Company company) {
		if (updateCompanyToDB(conn, company)) {
			this.nombre = company.getNombre();
			this.direccion = company.getDireccion();
			this.provincia = company.getProvincia();
			this.estado = company.getEstado();
			this.cpostal = company.getCpostal();
			this.telefono = company.getTelefono();
			this.mail = company.getMail();
			this.web = company.getWeb();
			return true;
		}
		return false;
	}
	
	/**
	 * Lee los datos de la empresa almacenada en la base de datos
	 * @param conn conexión con la base de datos
	 * @return objeto Company con los datos de la empresa
	 */
	public Company getCompanyFromDB(Connection conn) {
		Statement stm = null;
		ResultSet results = null;
		String sql = "SELECT * FROM company;";
		try {
			stm = conn.createStatement();
			results = stm.executeQuery(sql);
			Company company = new Company();
			while (results.next()) {
				company.setId(results.getInt(1));
				company.setNombre(results.getString(2));
				company.setDireccion(results.getString(3));
				company.setProvincia(results.getString(4));
				company.setEstado(results.getString(5));
				company.setCpostal(results.getString(6));
				company.setTelefono(results.getString(7));
				company.setMail(results.getString(8));
				company.setWeb(results.getString(9));
			}
			return company;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			PersistenceManager.closeStatement(stm);
		}
	}
	
	/**
	 * Refresca los datos de la compañía recargándolos de la
	 * base de datos
	 * @param conn conexión con la base de datos
	 * @return true si se refrescan los datos, false si no
	 */
	public boolean refresh(Connection conn) {
		Statement stm = null;
		ResultSet results = null;
		String sql = "SELECT nombre, direccion, provincia, estado, "
				+ "cpostal, telefono, mail, web "
				+ "FROM company;";
		try {
			stm = conn.createStatement();
			results = stm.executeQuery(sql);
			while (results.next()) {
				this.nombre = results.getString(1);
				this.direccion = results.getString(2);
				this.provincia = results.getString(3);
				this.estado = results.getString(4);
				this.cpostal = results.getString(5);
				this.telefono = results.getString(6);
				this.mail = results.getString(7);
				this.web = results.getString(8);
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			PersistenceManager.closeStatement(stm);
		}
	}
	
	/**
	 * Añade una unidad de negocio a la lista de unidades de negocio si la inserción de
	 * la unidad de negocio en la base de datos se hace con éxito
	 * @param conn conexión con la base de datos
	 * @param bUnit unidad de negocio a añadir
	 */
	public void addBusinessUnit (Connection conn, BusinessUnit bUnit) {
		BusinessUnit completeBUnit = new BusinessUnit().addNewBusinessUnit(conn, bUnit);
		if (completeBUnit != null) {
			businessUnits.add(completeBUnit);
		}
	}
	
	public List<User> getAllCompanyUsers() {
		return allCompanyUsers;
	}
	
	public void setAllCompanyUsers(List<User> allCompanyUsers) {
		this.allCompanyUsers = allCompanyUsers;
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
