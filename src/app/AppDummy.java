
package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//VERSION 0.0.2
//Se añade conexión a la base de datos

public class AppDummy {

	public static void main(String[] args) {
		AppDummy appDummy = new AppDummy();
		appDummy.go();
	}
	
	public void go() {
		connectToDatabase();
	}
	
	public void connectToDatabase () {
		Connection connection = null;
		String url = "jdbc:postgresql://localhost:5432/surferdb";
		String user = "surferadmin";
		String password = "surferpass";
		
		Connection devConnection = null;
		String devUrl = "jdbc:postgresql://localhost:5432/devsurferdb";
		String devUser = "surferadmin";
		String devPassword = "surferpass";
		
		try {
			Class.forName("org.postgresql.Driver");
			} catch (ClassNotFoundException ex) {
				System.out.println("No se encuentra el controlador JDBC ("
				+ ex.getMessage() +")");
			}
		
		try{
			//Obtenim una connexió des de DriverManager
			connection = DriverManager.getConnection(url, user, password);
			System.out.println("Connexió a surferdb realitzada usant"
			+ " DriverManager");
			connection.close();
			 
			} catch (SQLException ex) {
			System.out.println("Error " + ex.getMessage());
			}
		
		try{
			//Obtenim una connexió des de DriverManager
			devConnection = DriverManager.getConnection(devUrl, devUser, devPassword);
			System.out.println("Connexió a devsurferdb realitzada usant"
			+ " DriverManager");
			devConnection.close();
			 
			} catch (SQLException ex) {
			System.out.println("Error " + ex.getMessage());
			}
		}

}
