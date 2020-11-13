
package company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import persistence.PersistenceManager;
import types_states.EventsStatesContainer;


public class User {

	private Connection connection;
	private int id;
	private BusinessUnit bUnit;
	private String userType;
	private String userAlias;
	private String nombre;
	private String apellido;
	private String password;
	private boolean activo;
	
	
	public User(Connection connection, int id, BusinessUnit bUnit, String userType,
			String userAlias, String nombre, String apellido, String password, boolean activo) {
		this.connection = connection;
		this.id = id;
		this.bUnit = bUnit;
		this.userType = userType;
		this.userAlias = userAlias;
		this.nombre = nombre;
		this.apellido = apellido;
		this.password = password;
		this.activo = activo;
	}
	
	public User () {
		
	}

	
	/**
	 * Inserta un nuevo usuario en la base de datos
	 * @param user usuario a insertar
	 */
	public void saveToDB (User user) {
		String sql = "INSERT INTO \"user\" (b_unit_id, user_type_id, user_alias, nombre, "
				+ "apellido, user_password, activo) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?);";
		try {
			PreparedStatement pstm = connection.prepareStatement(sql);
			pstm.setInt(1, bUnit.getId());
			pstm.setInt(2, EventsStatesContainer.getuType().getUserTypeId(this.getUserType()));
			pstm.setString(3, getUserAlias());
			pstm.setString(4, getNombre());
			pstm.setString(5, getApellido());
			pstm.setString(6, getPassword());
			pstm.setBoolean(7, isActivo());
			pstm.executeUpdate();
			PersistenceManager.closePrepStatement(pstm);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateToDB (User user) {
		String sql = "UPDATE \"user\" SET ";
	}
	
	public List<User> getFromDB(Connection conn, BusinessUnit bUnit) {
		List<User> userList = new ArrayList<User>();
		User user = null;
		PreparedStatement pstm = null;
		ResultSet results = null;
		// Cambiar sql para filtrar por id del bUnit
		String sql = "SELECT * FROM \"user\";";
		try {
			pstm = connection.prepareStatement(sql);
			results = pstm.executeQuery();
			while (results.next()) {
				user = new User();
				user.setConnection(conn);
				user.setId(results.getInt(1));
				//Recuperar código bUnit y relacionarlo con un objeto BusinessUnit
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return userList;
	}
	
	public void prueba(BusinessUnit bUnit) {
		
	}

	public Connection getConnection() {
		return connection;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
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

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserAlias() {
		return userAlias;
	}

	public void setUserAlias(String userAlias) {
		this.userAlias = userAlias;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	
	
}
