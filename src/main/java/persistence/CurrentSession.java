package main.java.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
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
	
//	private CurrentSession(Company company, BusinessUnit bUnit, User user) {
//		this.company = company;
//		this.bUnit = bUnit;
//		this.user = user;
//		
//	}
	
	private CurrentSession() {

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
				this.bUnit = bUnit;
			}
		}
		//Cargamos sus usuarios, areas y eventos de la unidad de negocio
		List<User> userList = new User().getUsersFromDB(conn, bUnit);
		bUnit.setUsers(userList);
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

	public Company getCompany() {
		return company;
	}
	
	public boolean updateLastModification(Connection conn, int tableId, Timestamp timestamp) {
		PreparedStatement pstm = null;
		String sql = "UPDATE last_modification "
				+ "SET datetime = ? "
				+ "WHERE table_id = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setTimestamp(1, timestamp);
			pstm.setInt(2, tableId);
			pstm.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			PersistenceManager.closePrepStatement(pstm);
		}
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

	public Connection getConn() {
		return connection;
	}

	public void setConn(Connection conn) {
		this.connection = conn;
	}
	
	
}
