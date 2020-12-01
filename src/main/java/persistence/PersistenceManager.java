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
import java.util.List;

import main.java.company.Area;
import main.java.company.BusinessUnit;
import main.java.company.Company;
import main.java.company.User;
import main.java.event.Event;
import main.java.event.EventUpdate;
import main.java.types_states.EventState;
import main.java.types_states.EventType;
import main.java.types_states.TypesStatesContainer;
import main.java.types_states.UserType;

public class PersistenceManager {
	
	private static String url;
	private static String user;
	private static String password;

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
	 * Devuelve una conexión a una base de datos
	 * Los parámetros de la conexión son los atributos de la propia clase PersistenceManager
	 * @return conexión con la base de datos
	 */
	public static Connection getConnection () {		
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
			results = pstm.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return results;
	}
	
	/**
	 * Devuelve el id del último tipo de evento de la tabla event_type
	 * @param conn conexión con la base de datos
	 * @return id del último tipo de evento
	 */
	public static int getLastElementIdFromDB (Connection conn, String tableName) {
		int id = 0;
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT id FROM ? "
				+ "ORDER BY id DESC LIMIT 1;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, tableName);
			results = PersistenceManager.getResultSet(pstm, sql);
			while (results.next()) {
				id = results.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return id;
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
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
	 * Carga los datos de la base de datos al abrir la aplicación
	 * @param conn conexión con la base de datos
	 */
	public static void loadData (Connection conn) {
		
		//Lista de tipos de usuario
		UserType userTypeList = new UserType();
		userTypeList.loadData(conn);
		
		//Lista de tipos de eventos
		EventType eventTypeList = new EventType();
		eventTypeList.loadData(conn);
		
		//Lista de estados de eventos
		EventState eventStateList = new EventState();
		eventStateList.loadData(conn);
		
		//Mandamos las listas a un objeto contenedor
		TypesStatesContainer.setuType(userTypeList);
		TypesStatesContainer.setEvType(eventTypeList);
		TypesStatesContainer.setEvState(eventStateList);
		
		//Cargamos datos de la compañía
		Company company = new Company().getCompanyFromDB(conn);
		//Cargamos las unidades de negocio de la compañía
		List<BusinessUnit> bUnitList = new BusinessUnit().getBusinessUnitsFromDB(conn, company);
		company.setBusinessUnits(bUnitList);
		//Para cada unidad de negocio, cargamos sus usuarios, areas y eventos
		for (BusinessUnit bUnit: company.getBusinessUnits()) {
			List<User> userList = new User().getUsersFromDB(conn, bUnit);
			bUnit.setUsers(userList);
			List<Area> areaList = new Area().getAreasFromDB(conn, bUnit);
			bUnit.setAreas(areaList);
			List<Event> eventList = new Event().getEventsFromDB(conn, bUnit);
			bUnit.setEvents(eventList);
			for (Event event: bUnit.getEvents()) {
				List<EventUpdate> eUpdate = new EventUpdate().getEventUpdatesFromDB(conn, event);
				event.setUpdates(eUpdate);
			}
		}
	}
	
//	//Se espera una entrada de 20 caracteres como máximo
//	//Devuelve un hash de 40 caracteres
//	public static String passHash(String input) {
//		String hashedInput = "";
//		int size = input.length();
//		if (size == 40) {
//			return input;
//		} else {
//			hashedInput = getHash(input);
//		}
//		return passHash(hashedInput + getHash(hashedInput));
//	}
//	
//	public static String getHash(String input) {
//		String hashedInput = "";
//		int size = input.length();
//		String charList = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz"
//				+ "0123456789"
//				+ "*!$%&@#^";
//		//Debug
////		System.out.println("-----------------------------");
////		System.out.println("charList.lenght(): " + charList.length());
////		System.out.println("input size: " + size);
//		int factor = 0;
//		if (size <=10) {
//			factor = 7;
//		} else if (size > 10 && size <= 15) {
//			factor = 12;
//		} else if (size > 15 && size <= 20) { 
//			factor = 25;
//		} else if (size > 20 && size <= 30) {
//			factor = 37;
//		} else {
//			factor = 2;
//		}
//		//Debug
////		System.out.println("factor: " + factor);
//		for (int i = 0; i < input.length(); i++) {
//			char inChar = input.charAt(i);
//			//Debug
////			System.out.println ("Char: " + inChar);
//			int charIndex = charList.indexOf(inChar);
//			//Debug
////			System.out.println("charIndex prefactor: " + charIndex);
//			charIndex = charIndex + factor;
//			//Debug
////			System.out.println("charIndex postfactor: " + charIndex);
//			if (charIndex == charList.length()) {
//				charIndex = 0;
//			} else if (charIndex > charList.length()) {
//				charIndex = charIndex - charList.length() - 1;
//			}
//			hashedInput = hashedInput + charList.charAt(charIndex);
//		}
//		if (hashedInput.length() > 20) {
//			hashedInput = hashedInput.substring(0, 20);
//		}
//		return hashedInput;
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
