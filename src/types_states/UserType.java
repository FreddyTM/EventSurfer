package types_states;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import persistence.PersistenceManager;

public class UserType {

	private Connection connection;
	//Map <id, user_type> Almacena la tabla user_type de la base de datos
	private Map <Integer, String> userTypes = new LinkedHashMap<Integer, String>();
	
	public UserType (Connection connection) {
		this.connection = connection;
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
	public String[] getUserTypes() {
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
	 */
	public void loadData() {
		Statement statement = null;
		ResultSet results = null;
		String sql = "SELECT * FROM user_type;";
		try {
			statement = connection.createStatement();
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
		
//		Statement statement = null;
//		ResultSet results = null;
//		try {
//			statement = connection.createStatement();
//			results = statement.executeQuery(
//					"SELECT * FROM user_type;");
//			while(results.next()) {
//				int id = results.getInt(1);
//				String userType = results.getString(2);
//				userTypes.put(id, userType);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			PersistenceManager.closeResultSet(results);
//			PersistenceManager.closeStatement(statement);
//		}
		
		
		//Debug
		System.out.println("Tipos de usuario cargados correctamente\n");
		System.out.print(userTypes.entrySet());
		System.out.println();
//		System.out.println("TIPOS DE USUARIO: ");
//		for (int i = 0; i < getNumberOfUserTypes(); i++) {
//			System.out.print(getUserTypes()[i] + ", ");
//		}
	}
}
