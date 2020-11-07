package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PersistenceManager {

	
//	public PersistenceManager(String url, String user, String password) {
//		this.url = url;
//		this.user = user;
//		this.password = password;
//		this.dbName = url.substring(url.lastIndexOf("/"));
//	}
	
	//Abre la base de datos
	public static Connection openDatabase (String url, String user, String password) {
		
		Connection connection = null;
		String dbName = url.substring(url.lastIndexOf("/"));;
		
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(url, user, password);
			System.out.println("Connexión a " + dbName + " establecida con éxito");
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

	//Cierra la base de datos
	public static void closeDatabase (Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println("Error: " + e.getMessage());
			System.out.println("Estado: " + e.getSQLState());
			System.out.println("Código: " + e.getErrorCode());
		}
	}
	

	
	
}
