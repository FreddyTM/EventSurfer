package main.java.types_states;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import main.java.persistence.PersistenceManager;

/**
 * Almacena los tipos de usuarios y proporciona los métodos necesarios para
 * realizar las operaciones CRUD con la base de datos y con el resto de componentes
 * del programa
 * @author Alfred Tomey
 */
public class UserType {

	public static final String TABLE_NAME = "user_type";
	
	//Map <id, user_type> Almacena la tabla user_type de la base de datos
	private Map <Integer, String> userTypes = new LinkedHashMap<Integer, String>();
	
	public UserType () {
		
	}
	
	/**
	 * Devuelve el número de tipos de usuario almacenados en la base de datos
	 * @return Número de tipos de usuarios 
	 */
	public int getNumberOfUserTypes() {
		return userTypes.size();
	}
	
	/**
	 * Devuelve un array con el nombre de los diferentes tipos de usuarios
	 * @return Tipos de usuarios
	 */
	public String[] getUserTypesArray() {
		String[] tiposDeUsuario = new String[getNumberOfUserTypes()];
		Collection<String> tipos = userTypes.values();
		tiposDeUsuario = tipos.toArray(tiposDeUsuario);
		return tiposDeUsuario;
	}
	
	/**
	 * Retorna el id del tipo de usuario pasado por parámetro
	 * @param userType tipo de usuario del que queremos saber su id
	 * @return id del tipo de usuario (-1 si no existe)
	 */
	public int getUserTypeId (String userType) {
		for (Integer key : userTypes.keySet()) {
			if (userType.equals(userTypes.get(key))) {
				return key;
			}
		}
		//El tipo de usuario no existe
		return -1;	
	}
	
	/**
	 * Retorna el tipo de usuario que corresponde a la clave pasada por parámetro
	 * @param key
	 * @return tipo de usuario o null si la clave no existe
	 */
	public String getUserType (int key) {
		if (userTypes.containsKey(key)) {
			return userTypes.get(key);
		} else {
			return null;
		}
	}
	
	/**
	 * Conecta con la base de datos y almacena cada tipo de usuario con su clave
	 * en userTypes
	 * conn conexión con la base de datos
	 */
	public void loadData(Connection conn) {
		Statement statement = null;
		ResultSet results = null;
		String sql = "SELECT * FROM user_type;";
		try {
			statement = conn.createStatement();
			results = PersistenceManager.getResultSet(statement, sql);
			while(results.next()) {
				int id = results.getInt(1);
				String userType = results.getString(2);
				userTypes.put(id, userType);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closeStatement(statement);
		}
	}
	
	public Map <Integer, String> getUserTypes() {
		return this.userTypes;
	}
}
