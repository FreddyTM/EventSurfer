
package main.java.company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.persistence.PersistenceManager;
import main.java.types_states.TypesStatesContainer;


public class User {

	public static final String TABLE_NAME = "user";
	private int id;
	private BusinessUnit bUnit;
	private String userType;
	private String userAlias;
	private String nombre;
	private String apellido;
	private String password;
	private boolean activo;
	
	
	public User(int id, BusinessUnit bUnit, String userType, String userAlias, 
			String nombre, String apellido, String password, boolean activo) {
		//this.connection = connection;
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
	 * @param conn conexión con la base de datos
	 * @param user usuario a insertar
	 * @return true si la insercion se hizo con éxito, false si no 
	 */
	public boolean saveUserToDB (Connection conn, User user) {
		PreparedStatement pstm = null;
		String sql = "INSERT INTO \"user\" (b_unit_id, user_type_id, user_alias, nombre, "
				+ "apellido, user_password, activo) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?);";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, user.getbUnit().getId());
			pstm.setInt(2, TypesStatesContainer.getuType().getUserTypeId(user.getUserType()));
			pstm.setString(3, user.getUserAlias());
			pstm.setString(4, user.getNombre());
			pstm.setString(5, user.getApellido());
			pstm.setString(6, user.getPassword());
			pstm.setBoolean(7, user.isActivo());
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
	 * Si la inserción de un nuevo usuario en la base de datos tiene éxito,
	 * recupera el id asignado en el registro de la base de datos y lo almacena
	 * en el id del objeto User
	 * @param conn conexión con la base de datos
	 * @param user objeto a insertar en la base de datos
	 * @return objeto User con el id asignado
	 */
	public User addNewUser (Connection conn, User user) {
		if (saveUserToDB(conn, user)) {
			int id = PersistenceManager.getLastElementIdFromDB(conn, TABLE_NAME);
			user.setId(id);
			return user;
		}
		return null;
	}
	
	/**
	 * Actualiza los valores de un usuario que ya existe en la base de datos
	 * @param conn conexión con la base de datos
	 * @param user usuario que contiene los datos que actualizan al usuario ya existente
	 * @return true si la actualización se hizo con éxito, false si no 
	 */
	public boolean updateUserToDB (Connection conn, User user) {
		PreparedStatement pstm = null;
		String sql = "UPDATE \"user\" "
				+ "SET "
				+ "b_unit_id = ?, "
				+ "user_type_id = ?, "
				+ "user_alias = ?, "
				+ "nombre = ?, "
				+ "apellido = ?, "
				+ "user_password = ?,"
				+ "activo = ? "
				+ "WHERE id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, user.getbUnit().getId());
			pstm.setInt(2, TypesStatesContainer.getuType().getUserTypeId(user.getUserType()));
			pstm.setString(3, user.getUserAlias());
			pstm.setString(4, user.getNombre());
			pstm.setString(5, user.getApellido());
			pstm.setString(6, user.getPassword());
			pstm.setBoolean(7, user.isActivo());
			pstm.setInt(8, user.getId());
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
	 * Actualiza la contraseña, y opcionalmente el alias, el nombre y el apellido
	 * del usuario administrador por defecto si la contraseña por defecto de dicho
	 * usuario no ha sido cambiada
	 * @param conn conexión con la base de datos
	 * @param user usuario administrador por defecto
	 * @return true si la actualización se hizo con éxito, false si no 
	 */
	public boolean updateDefaultAdminUserToDb (Connection conn, User user) {
		PreparedStatement pstm = null;
		String sql = "UPDATE \"user\" "
				+ "SET "
				+ "user_alias = ?, "
				+ "nombre = ?, "
				+ "apellido = ?, "
				+ "user_password = ? "
				+ "WHERE id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, user.getUserAlias());
			pstm.setString(2, user.getNombre());
			pstm.setString(3, user.getApellido());
			pstm.setString(4, user.getPassword());
			pstm.setInt(5, user.getId());
			pstm.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Obtiene la lista de usuarios del objeto BusinessUnit pasado por parámetro
	 * @param conn conexión con la base de datos
	 * @param bUnit objeto del que queremos recuperar sus usuarios
	 * @return lista de usuarios del objeto almacenados en la base de datos
	 */
	public List<User> getUsersFromDB(Connection conn, BusinessUnit bUnit) {
		List<User> userList = new ArrayList<User>();
		User user = null;
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT id, user_type_id, user_alias, nombre, "
				+ "apellido, user_password, activo "
				+ "FROM \"user\" "
				+ "WHERE b_unit_id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, bUnit.getId());
			results = pstm.executeQuery();
			while (results.next()) {
				user = new User();
				user.setId(results.getInt(1));
				user.setbUnit(bUnit);
				user.setUserType(TypesStatesContainer.getuType().getUserType(results.getInt(2)));
				user.setUserAlias(results.getString(3));
				user.setNombre(results.getString(4));
				user.setApellido(results.getString(5));
				user.setPassword(results.getString(6));
				user.setActivo(results.getBoolean(7));
				userList.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
		}
		return userList;
	}
	
	/**
	 * Devuelve el usuario al que pertenece el alias entrado por parámetro
	 * @param bUnit BusinessUnit del que comprobamos la lista de usuarios
	 * @param alias alias del usuario buscado
	 * @return usuario con el alias entrado por parámetro (null si no existe)
	 */
	public User getUserByAlias (BusinessUnit bUnit, String alias) {
		List<User> userList = bUnit.getUsers();
		for (User user: userList) {
			if (user.getUserAlias().equals(alias)) {
				return user;
			}
		}	
		return null;
	}
	
	/**
	 * Devuelve el usuario al que pertenece el id entrado por parámetro
	 * @param bUnit BusinessUnit del que comprobamos la lista de usuarios
	 * @param id id del usuario buscado
	 * @return usuario con el id entrado por parámetro (null si no existe)
	 */
	public User getUserById (BusinessUnit bUnit, int id) {
		List<User> userList = bUnit.getUsers();
		for (User user: userList) {
			if (user.getId() == id) {
				return user;
			}
		}	
		return null;
	}
	
	/**
	 * Comprueba que el password introducido por parámetro es igual al del usuario user
	 * @param user Usuario
	 * @param input password a comprobar
	 * @return True si es igual, false si no lo es
	 */
	public boolean checkUserPassword(User user, String input) {
		if (user.getPassword().equals(passwordHash(input))) {
			return true;
		}
		return false;
	}
	
	/**
	 * Comprueba que la contraseña introducida cumple las restricciones para ser correcta
	 * Caracteres [a-z], [A-Z], [0-9], [*!$%&@#^]
	 * @param password contraseña a validar
	 * @return true si la contraseña es correcta, false si no lo es
	 */
	public boolean isAValidPassword(String password) {
		String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*!$%&@#^])(?=\\S+$).{8,25}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}
	
	/**
	 * Genera un hash de 50 caracteres a partir de un String
	 * @param input String de entrada
	 * @return hash de 50 caracteres
	 */
	public String passwordHash(String input) {
		String hashedInput = "";
		int size = input.length();
		if (size == 50) {
			return input;
		} else {
			hashedInput = getHash(input);
		}
		return passwordHash(hashedInput + getHash(hashedInput));
	}
	
	/**
	 * Genera un hash de igual longitud al String pasado por parámetro
	 * @param input String de entrada
	 * @return hash del String de entrada
	 */
	public String getHash(String input) {
		String hashedInput = "";
		int size = input.length();
		String charList = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz"
				+ "0123456789"
				+ "*!$%&@#^";
		int factor = 0;
		if (size <=10) {
			factor = 7;
		} else if (size > 10 && size <= 15) {
			factor = 12;
		} else if (size > 15 && size <= 20) { 
			factor = 25;
		} else if (size > 20 && size <= 30) {
			factor = 37;
		} else if (size > 30 && size <= 40) {
			factor = 44;
		} else {
			factor = 3;
		}

		for (int i = 0; i < input.length(); i++) {
			char inChar = input.charAt(i);
			int charIndex = charList.indexOf(inChar);
			charIndex = charIndex + factor;
			if (charIndex == charList.length()) {
				charIndex = 0;
			} else if (charIndex > charList.length()) {
				charIndex = charIndex - charList.length() - 1;
			}
			hashedInput = hashedInput + charList.charAt(charIndex);
		}
		if (hashedInput.length() > 25) {
			hashedInput = hashedInput.substring(0, 25);
		}
		return hashedInput;
	}
	

	
	//METODO DE PRUEBA. SE PUEDE BORRAR
	public void prueba(BusinessUnit bUnit) {
		
	}

//	public Connection getConnection() {
//		return connection;
//	}
//	
//	public void setConnection(Connection connection) {
//		this.connection = connection;
//	}
	
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
