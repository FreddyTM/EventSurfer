package main.java.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;


import main.java.company.User;


public class PersistenceManager {
	
	private static String url; //URL de la base de datos
	private static String user; //usuario de la base de datos
	private static String password; //contraseña del usuario de la base de datos

	//LOCAL_DB - Local database
	//LOCAL_TEST_DB - Local test database
	//REMOTE_DB - Remote database (Heroku)
	public static Connection connectToDatabase(String database) {
		String url = null;
		String user = null;
		String password = null;
		Connection connection = null;
		
		switch(database) {
		case "LOCAL_DB":
			url = "jdbc:postgresql://localhost:5432/surferdb";
			user = "surferadmin";
			password = "surferpass";
			break;
		case "LOCAL_TEST_DB":
			url = "jdbc:postgresql://localhost:5432/devsurferdb";
			user = "surferadmin";
			password = "surferpass";
			break;
		case "REMOTE_DB":
			url = "postgres://jqjqasnlbihnyy:0bb8e23db3107a14e61bbdaaae12c176c5b977663abbb1db3a1dfeadc2091bd4@ec2-18-203-7-163.eu-west-1.compute.amazonaws.com:5432/de2ttdn4inqheo\r\n";
			user = "jqjqasnlbihnyy";
			password = "0bb8e23db3107a14e61bbdaaae12c176c5b977663abbb1db3a1dfeadc2091bd4";
		default:
			url = null;
			user = null;
			password = null;
		}
		
		setUrl(url);
		setUser(user);
		setPassword(password);
		connection = getConnection();
		return connection;
	}

	/**
	 * Cierra la conexión con la base de datos
	 * @param connection conexión con la base de datos que se desea cerrar
	 */
	public static void closeDatabase (Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println("Error: " + e.getMessage());
			System.out.println("Estado: " + e.getSQLState());
			System.out.println("Código: " + e.getErrorCode());
		}
	}
	
	/**
	 * Devuelve una conexión a una base de datos
	 * Los parámetros de la conexión son los atributos de la propia clase PersistenceManager
	 * @return conexión con la base de datos
	 */
	public static Connection getConnection () {		
		Connection connection = null;
		String dbName = null;
		if (url != null) {
			dbName = url.substring(url.lastIndexOf("/"));
			;
		}
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(url, user, password);
			if (url != null) {
				System.out.println("Connexión a " + dbName + " establecida con éxito\n");
			} else {
				System.out.println("Error de onnexión a " + dbName + "\n");
			}
		} catch (ClassNotFoundException ex) {
			System.out.println("No se encuentra el controlador JDBC ("
			+ ex.getMessage() +")");
		} catch (SQLException e) {
			System.out.println("Error: " + e.getMessage());
			System.out.println("Estado: " + e.getSQLState());
			System.out.println("Código: " + e.getErrorCode());
		}		
		return connection;
	}
	
	/**
	 * Cierra un Statement
	 * @param stm statement a cerrar
	 */
	public static void closeStatement (Statement stm) {
		try {
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Cierra un PreparedStatement
	 * @param pstm statement a cerrar
	 */
	public static void closePrepStatement (PreparedStatement pstm) {
		try {
			if (pstm != null) {
				pstm.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Cierra un ResultSet
	 * @param rset resultset a cerrar
	 */
	public static void closeResultSet (ResultSet rset) {
		try {
			rset.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Devuelve un ResultSet a partir de una consulta sql
	 * @param stm Statement sobre el que se ejecuta la consulta
	 * @param sql consulta sql
	 * @return ResultSet obtenido a partir de la consulta sql
	 */
	public static ResultSet getResultSet (Statement stm, String sql) {
		
		ResultSet results = null;	
		try {
			results = stm.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return results;
	}
	
	/**
	 * Devuelve un ResultSet a partir de una consulta sql
	 * @param pstm PreparedStatement sobre el que se ejecuta la consulta
	 * @param sql consulta sql
	 * @return ResultSet obtenido a partir de la consulta sql
	 */
	public static ResultSet getResultSet (PreparedStatement pstm, String sql) {
		
		ResultSet results = null;	
		try {
			results = pstm.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return results;
	}
	
	/**
	 * Devuelve el id del último tipo de evento de la tabla indicada
	 * @param tableName nombre de la tabla en la base de datos
	 * @param conn conexión con la base de datos
	 * @return id del último tipo de evento
	 */
	public static int getLastElementIdFromDB (Connection conn, String tableName) {
		int id = 0;
		Statement stm = null;
		ResultSet results = null;
		String sql = "SELECT * FROM \"" + tableName
				+ "\" ORDER BY id DESC LIMIT 1";
		try {
			stm = conn.createStatement();
			results = stm.executeQuery(sql);
			while (results.next()) {
				id = results.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Estado: " + e.getSQLState());
			System.out.println("Código: " + e.getErrorCode());
			return id;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closeStatement(stm);
		}
		return id;
	}
	
	/**
	 * Devuelve la fecha y la hora actuales
	 * @return Timestamp con la fecha y la hora actuales
	 */
	public static Timestamp getTimestampNow() {
		Calendar calendar = Calendar.getInstance();
		Date dNow = calendar.getTime();
		Timestamp tNow = new Timestamp(dNow.getTime());
		return tNow;
	}
	
	/**
	 * Devuelve la fecha y la hora de la última actualización de datos en alguna
	 * de las tablas de la base de datos
	 * @param conn conexión con la base de datos
	 * @return timestamp con la fecha y la hora, null si falla la consulta
	 */
	public static Timestamp getLatestTimestampFromDb(Connection conn) {
		Timestamp lastUpdate = null;
		Statement stm = null;
		ResultSet results = null;
		String sql = "SELECT fecha_hora "
				+ "FROM last_modification";
		try {
			stm = conn.createStatement();
			results = getResultSet(stm, sql);
			while (results.next()) {
				Timestamp temp = results.getTimestamp(1);
				if (lastUpdate == null) {
					lastUpdate = temp;
				}
				if (temp.after(lastUpdate)) {
					lastUpdate = temp;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closeStatement(stm);
		}
		return lastUpdate;
	}
	
	/**
	 * Actualiza la tabla last_modification de la base de datos con la fecha y la hora
	 * en la que se ha modificado o insertado algún dato en la tabla pasada por parámetro.
	 * @param conn conexión a la base de datos
	 * @param tableName tabla en la que ha habido algún tipo de modificación o inserción
	 * @param tNow fecha y hora de la modificación o inserción
	 * @return true si la actualización de la tabla last_modification se completa con éxito,
	 * false si no se completa
	 */
	public static boolean updateTimeStampToDB(Connection conn, String tableName, Timestamp tNow) {
		PreparedStatement pstm = null;
		String sql = "UPDATE last_modification "
				+ "SET fecha_hora = ? "
				+ "WHERE table_name = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setTimestamp(1, tNow);
			pstm.setString(2, tableName);
			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			PersistenceManager.closePrepStatement(pstm);
		}
		return true;
	}
	
//	/**
//	 * Comprueba si el usuario administrador por defecto mantiene el password por defecto
//	 * o lo ha cambiado.
//	 * @param conn conexión con la base de datos
//	 * @return 0. password sin cambiar, 1. password cambiado, -1 error de comprobación
//	 */
//	public static int checkDefaultAdminPassword(Connection conn) {
//		Statement stm = null;
//		ResultSet results = null;
//		String sql = "SELECT user_password "
//				+ "FROM \"user\" "
//				+ "WHERE id = 1;";
//		try {
//			stm = conn.createStatement();
//			results = getResultSet(stm, sql);
//			while(results.next()) {
//				String password = results.getString(1);
//				if (password.equals("surferpass")) {
//					return 0;
//				} else {
//					return 1;
//				}
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			closeResultSet(results);
//			closeStatement(stm);
//		}
//		return -1;
//	}
	
//	/**
//	 * Obtiene el alias, nombre, apellido y password del usuario administrador por defecto
//	 * para comprobar si su password ha sido cambiado. En la primera ejecución del programa
//	 * Es obligatorio cambiar este password, y opcional cambiar el alias, nombre y apellido. 
//	 * @param conn conexión con la base de datos
//	 * @return usuario administrador por defecto incluyendo solo alias, nombre, apellido y
//	 * password. El resto de datos no son necesarios porque ya son conocidos.
//	 */
//	public static User getDefaultAdminUser (Connection conn) {
//		Statement stm = null;
//		ResultSet results = null;
//		User user = new User();
//		String sql = "SELECT user_alias, nombre, apellido, user_password "
//				+ "FROM \"user\" "
//				+ "WHERE id = 1;";
//		try {
//			stm = conn.createStatement();
//			results = getResultSet(stm, sql);
//			while (results.next()) {
//				user.setUserAlias(results.getString(1));
//				user.setNombre(results.getString(2));
//				user.setApellido(results.getString(3));
//				user.setPassword(results.getString(4));
//			}
//			return user;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
//	/**
//	 * Obtiene el id de la unidad de negocio a la que pertenece el usuario, si el
//	 * usuario existe
//	 * @param conn conexión con la base de datos
//	 * @param alias alias del usuario
//	 * @param password password del usuario
//	 * @return id de la unidad de negocio, o 0 si el usuario no existe
//	 */
//	public static int getBunitIdFromUser (Connection conn, String alias, String password) {
//		PreparedStatement pstm = null;
//		ResultSet results = null;
//		String sql = "SELECT b_unit_id "
//				+ "	FROM \"user\" "
//				+ "WHERE user_alias = ? "
//				+ "AND user_password = ?;";
//		try {
//			pstm = conn.prepareStatement(sql);
//			pstm.setString(1,  alias);
//			pstm.setString(2, new User().passwordHash(password));
//			results = pstm.executeQuery();
//			if (results.next()) {
//				return results.getInt(1);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			PersistenceManager.closeResultSet(results);
//			PersistenceManager.closePrepStatement(pstm);
//		}
//		return 0;
//	}
//	
//	/**
//	 * Obtiene el id del usuario si el usuario existe
//	 * @param conn conexión con la base de datos
//	 * @param alias alias del usuario
//	 * @param password password del usuario
//	 * @return id del usuario, 0 si el usuario no existe
//	 */
//	public static int getUserId (Connection conn, String alias, String password) {
//		PreparedStatement pstm = null;
//		ResultSet results = null;
//		String sql = "SELECT id "
//				+ "	FROM \"user\" "
//				+ "WHERE user_alias = ? "
//				+ "AND user_password = ?;";
//		try {
//			pstm = conn.prepareStatement(sql);
//			pstm.setString(1,  alias);
//			pstm.setString(2, new User().passwordHash(password));
//			results = pstm.executeQuery();
//			if (results.next()) {
//				return results.getInt(1);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			PersistenceManager.closeResultSet(results);
//			PersistenceManager.closePrepStatement(pstm);
//		}
//		return 0;
//	}

	public static String getUrl() {
		return url;
	}

	public static void setUrl(String url) {
		PersistenceManager.url = url;
	}

	public static String getUser() {
		return user;
	}

	public static void setUser(String user) {
		PersistenceManager.user = user;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		PersistenceManager.password = password;
	}
}

