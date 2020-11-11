package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PersistenceManager {

	/**
	 * Abre una conexión con la base de datos
	 * @param url URL de la base de datos
	 * @param user usuario que accede a la base de datos
	 * @param password contraseña del usuario
	 * @return conexión con la base de datos
	 */
	public static Connection openDatabase (String url, String user, String password) {
		
		Connection connection = null;
		String dbName = url.substring(url.lastIndexOf("/"));;
		
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(url, user, password);
			System.out.println("Connexión a " + dbName + " establecida con éxito\n");
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
			pstm.close();
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
	 * @param statement Statement sobre el que se ejecuta la consulta
	 * @param sql consulta sql
	 * @return ResultSet obtenido a partir de la consulta sql
	 */
	public static ResultSet getResultSet (Statement statement, String sql) {
		
		ResultSet results = null;	
		try {
			results = statement.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return results;
	}
}
