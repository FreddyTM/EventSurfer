package main.java.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

public class CurrentSession {

	//Instancia única de la sesión en curso para toda la aplicación
	private static CurrentSession session;
	//Usuario que inicia sesión en la aplicación
	private User user;
	//Unidad de negocio al que pertenece el usuario que inicia sesión en la aplicación
	private BusinessUnit bUnit;
	//Empresa a la que pertenece la unidad de negocio
	private Company company;
	//Registra la fecha y hora en el momento de cargar los datos de la sesión
	//CurrentSession. Se actualiza con la fecha y hora en que se produzcan
	//cambios en la base de datos que afecten a company, a bUnit, o a cualquiera
	//de los objetos que contiene bUnit
	private volatile Timestamp dateTimeReference;
	private Connection connection;
	private Timer timer;
	
//	private CurrentSession(Company company, BusinessUnit bUnit, User user) {
//		this.company = company;
//		this.bUnit = bUnit;
//		this.user = user;
//		
//	}
	
	private CurrentSession() {
		timer = new Timer();
		TimerTask task = new TimerJob();
		timer.scheduleAtFixedRate(task, 60000, 60000);
	}
	
	public static CurrentSession getInstance() {
		if (session == null) {
			session = new CurrentSession();
		}
		return session;
	}
	
	/**
	 * Carga todos los datos de la base de datos
	 * @param conn conexión con la base de datos
	 */
	public void loadAllData (Connection conn) {
		
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
		company = new Company().getCompanyFromDB(conn);
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
		//Registramos fecha y hora de la carga de datos de la sesión
		dateTimeReference = PersistenceManager.getTimestampNow();
	}
	
	/**
	 * Carga los datos de la unidad de negocio a la que pertenece el usuario
	 * que abre la sesión. La unidad de negocio y el usuario se almacenan en
	 * los atributos de la clase
	 * @param conn conexión con la base de datos
	 * @param bUnitId id de la unidad de negocio
	 * @param userId id del usuario que abre la sesión
	 */
	public void loadCurrentSessionData(Connection conn, int bUnitId, int userId) {
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
		company = new Company().getCompanyFromDB(conn);
		//Cargamos la unidad de negocio que tiene el id bUnitId
		List<BusinessUnit> bUnitList = new BusinessUnit().getBusinessUnitsFromDB(conn, company);
		for (BusinessUnit bUnit: bUnitList) {
			if (bUnit.getId() == bUnitId) {
				company.getBusinessUnits().add(bUnit);
				//Asignamos la unidad de negocio a bUnit
				this.bUnit = bUnit;
			}
		}
		//Cargamos sus usuarios, areas y eventos de la unidad de negocio
		List<User> userList = new User().getUsersFromDB(conn, bUnit);
		bUnit.setUsers(userList);
		//Asignamos el usuario que abre sesión a user
		user = new User().getUserById(bUnit, userId);
		List<Area> areaList = new Area().getAreasFromDB(conn, bUnit);
		bUnit.setAreas(areaList);
		List<Event> eventList = new Event().getEventsFromDB(conn, bUnit);
		bUnit.setEvents(eventList);
		for (Event event: bUnit.getEvents()) {
			List<EventUpdate> eUpdate = new EventUpdate().getEventUpdatesFromDB(conn, event);
			event.setUpdates(eUpdate);
		}
		//Registramos fecha y hora de la carga de datos de la sesión
		dateTimeReference = PersistenceManager.getTimestampNow();
	}

	/**
	 * Actualiza la tabla last_modification, registrando la fecha y la hora
	 * en la que se produce la actualización de alguna de las otras tablas
	 * de la base de datos
	 * @param conn conexión con la base de datos
	 * @param tableName nombre de la tabla que se ha actualizado
	 * @param timestamp fecha y hora de la actualización
	 * @return true si la actualización se realiza con éxito, false si no
	 */
	public boolean updateLastModification(Connection conn, String tableName, Timestamp timestamp) {
		PreparedStatement pstm = null;
		String sql = "UPDATE last_modification "
				+ "SET datetime = ? "
				+ "WHERE table_name = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setTimestamp(1, timestamp);
			pstm.setString(2, tableName);
			pstm.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			PersistenceManager.closePrepStatement(pstm);
		}
	}
	
	//Clase que realiza la comprobación de las tablas que se han actualizado
	//con posterioridad a la última carga de datos de la sesión en curso
	class TimerJob extends TimerTask {
		
		//CurrentSession session = CurrentSession.getInstance();
		
		@Override
		public void run() {
			Connection conn = session.getConnection();
			if (conn == null) {
				conn = PersistenceManager.getConnection();
			}
			Timestamp tempDateTime = session.getDateTimeReference();
			Statement stm = null;
			ResultSet results = null;
			String sql = "SELECT * "
					+ "FROM last_modification";
			try {
				stm = conn.createStatement();
				results = PersistenceManager.getResultSet(stm, sql);
				while (results.next()) {
					String tableName = results.getString(1);
					Timestamp dateTimeDb = results.getTimestamp(2);
					if (session.getDateTimeReference().before(dateTimeDb) ) {
						//Actualizar objetos correspondientes a table_name
						//Llamadas a métodos de recarga de datos de las clases
						switch(tableName) {
							case "user_type":
								UserType userTypeList = new UserType();
								userTypeList.loadData(conn);
								TypesStatesContainer.setuType(userTypeList);
								break;
							case "event_type":
								EventType eventTypeList = new EventType();
								eventTypeList.loadData(conn);
								TypesStatesContainer.setEvType(eventTypeList);
								break;
							case "event_state":
								EventState eventStateList = new EventState();
								eventStateList.loadData(conn);
								TypesStatesContainer.setEvState(eventStateList);
								break;
							case "company":
								session.getCompany().refresh(conn);
								break;
							case "business_unit":
								session.getbUnit().refresh(conn);
								break;
							case "user":
								List<User> userList = new User().getUsersFromDB(conn, session.getbUnit());
								session.getbUnit().setUsers(userList);
								for (User user: session.getbUnit().getUsers()) {
									if (session.getUser().getId() == user.getId()) {
										session.setUser(user);
									}
								}
								break;
							case "area":
								List<Area> areaList = new Area().getAreasFromDB(conn, session.getbUnit());
								session.getbUnit().setAreas(areaList);
								break;
							case "event":
								List<Event> eventList = new Event().getEventsFromDB(conn, session.getbUnit());
								session.getbUnit().setEvents(eventList);
								for (Event event: session.getbUnit().getEvents()) {
									List<EventUpdate> eUpdate = new EventUpdate().getEventUpdatesFromDB(conn, event);
									event.setUpdates(eUpdate);
								}
								break;
							case "event_update":
								for (Event event: session.getbUnit().getEvents()) {
									List<EventUpdate> eUpdate = new EventUpdate().getEventUpdatesFromDB(conn, event);
									event.setUpdates(eUpdate);
								}
								break;
							default:
								//Throw exception unknown table
						}				
						//Actualizamos el timestamp temporal para que acabe registrando
						//el mayor valor que se encuentre en el Resultset
						if(tempDateTime.before(dateTimeDb)) {
							tempDateTime = dateTimeDb;
						}
					}
				}
				//Actualizamos dateTimeReference de la sesión
				session.setDateTimeReference(tempDateTime);;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public BusinessUnit getbUnit() {
		return bUnit;
	}

	public void setbUnit(BusinessUnit bUnit) {
		this.bUnit = bUnit;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Timestamp getDateTimeReference() {
		return dateTimeReference;
	}

	public void setDateTimeReference(Timestamp dateTimeReference) {
		this.dateTimeReference = dateTimeReference;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Timer getTimer() {
		return timer;
	}
		
}
