
package main.java.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import main.java.company.Company;
import main.java.persistence.CurrentSession;
import main.java.persistence.PersistenceManager;
import main.java.types_states.EventState;
import main.java.types_states.EventType;
import main.java.types_states.TypesStatesContainer;
import main.java.types_states.UserType;


//VERSION 0.0.22


public class EventSurfer {

	Connection connection;
	
	public static void main(String[] args) {
		EventSurfer eventSurfer = new EventSurfer();
		eventSurfer.go(args);
	}
	
	public void go(String[] args) {
		
		//connectToDatabase();
		//System.out.println(args.length);
		if (args.length == 1) {
			connection = connectToDatabase(args[0]);

		} else {
			connection = connectToDatabase("");			
		}
		
		while(connection == null) {
			//Error screen with reconnect button
		}
		CurrentSession session = CurrentSession.getInstance();
		if (PersistenceManager.checkDefaultAdminPassword(connection) == 0) {
			//admin password sin cambiar
			System.out.println("Password sin cambiar");
			//update admin data screen
			
			//User id será 1, el administrador por defecto
			//BUnit id será 1, la unidad de negocio por defecto
			session.loadCurrentSessionData(connection, 1, 1);
		} else if (PersistenceManager.checkDefaultAdminPassword(connection) == 1) {
			//admin password cambiado
			System.out.println("Password cambiado");
			//Login screen
			
			//Add PersistenceManager method to find user and its business unit
			//Instantiate a CurrentSession with company, business unit, user
			//and last db update of business unit data.
			
			//En botón login
			//int bUnitId = PersistenceManager.getBunitIdFromUser(connection, alias, password);
			//int userId = PersistenceManager.getUserId()
			//session.loadCurrentSessionData(connection, int bUnitId, int userId)
		}
		//Until code is completed
		System.out.println("So far so good");
		PersistenceManager.closeDatabase(connection);
	}
	
	public void checkConnectToDatabase () {
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
	
	//LOCAL_DB - Local database
	//LOCAL_TEST_DB - Local test database
	//REMOTE_DB - Remote database (Heroku)
	public Connection connectToDatabase(String database) {
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
			url = null;
			user = null;
			password = null;
		default:
			url = "jdbc:postgresql://localhost:5432/devsurferdb";
			user = "surferadmin";
			password = "surferpass";
		}
		
		PersistenceManager.setUrl(url);
		PersistenceManager.setUser(user);
		PersistenceManager.setPassword(password);
		connection = PersistenceManager.getConnection();
		return connection;
		
		//PersistenceManager.loadData(connection);	
		//PersistenceManager.closeDatabase(connection);
		
	}

}
