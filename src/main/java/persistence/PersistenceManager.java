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
	
	private static String url; //URL de la base de datos
	private static String user; //usuario de la base de datos
	private static String password; //contraseña del usuario de la base de datos

//	/**
//	 * Abre una conexión con la base de datos
//	 * @param url URL de la base de datos
//	 * @param user usuario de la base de datos
//	 * @param password contraseña del usuario de la base de datos
//	 * @return conexión con la base de datos
//	 */
//	public static Connection openDatabase (String url, String user, String password) {
//		
//		Connection connection = null;
//		String dbName = url.substring(url.lastIndexOf("/"));;
//		
//		try {
//			Class.forName("org.postgresql.Driver");
//			connection = DriverManager.getConnection(url, user, password);
//			System.out.println("Connexión a " + dbName + " establecida con éxito\n");
//		} catch (ClassNotFoundException ex) {
//			System.out.println("No se encuentra el controlador JDBC ("
//			+ ex.getMessage() +")");
//		} catch (SQLException e) {
//			System.out.println("Error: " + e.getMessage());
//			System.out.println("Estado: " + e.getSQLState());
//			System.out.println("Código: " + e.getErrorCode());
//		}
//		
//		return connection;
//	}

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
			results = pstm.executeQuery();
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
	 * Carga todos los datos de la base de datos
	 * @param conn conexión con la base de datos
	 */
	public static void loadAllData (Connection conn) {
		
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
	
	/**
	 * Comprueba si el usuario administrador por defecto mantiene el password por defecto
	 * o lo ha cambiado.
	 * @param conn conexión con la base de datos
	 * @return 0. password sin cambiar, 1. password cambiado, -1 error de comprobación
	 */
	public static int checkDefaultAdminPassword(Connection conn) {
		Statement stm = null;
		ResultSet results = null;
		String sql = "SELECT user_password "
				+ "FROM \"user\" "
				+ "WHERE id = 1;";
		try {
			stm = conn.createStatement();
			results = getResultSet(stm, sql);
			while(results.next()) {
				String password = results.getString(1);
				if (password.equals("surferpass")) {
					return 0;
				} else {
					return 1;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeResultSet(results);
			closeStatement(stm);
		}
		return -1;
	}
	
	/**
	 * Obtiene el id de la unidad de negocio a la que pertenece el usuario, si el
	 * usuario existe
	 * @param conn conexión con la base de datos
	 * @param alias alias del usuario
	 * @param password password del usuario
	 * @return id de la unidad de negocio, o 0 si el usuario no existe
	 */
	public static int getBunitIdFromUser (Connection conn, String alias, String password) {
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT b_unit_id "
				+ "	FROM \"user\" "
				+ "WHERE user_alias = ? "
				+ "AND user_password = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setString(1,  alias);
			pstm.setString(2, new User().passwordHash(password));
			results = pstm.executeQuery();
			if (results.next()) {
				return results.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
		}
		return 0;
	}
	
	public static int getUserId (Connection conn, String alias, String password) {
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT id "
				+ "	FROM \"user\" "
				+ "WHERE user_alias = ? "
				+ "AND user_password = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setString(1,  alias);
			pstm.setString(2, new User().passwordHash(password));
			results = pstm.executeQuery();
			if (results.next()) {
				return results.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closePrepStatement(pstm);
		}
		return 0;
	}

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

