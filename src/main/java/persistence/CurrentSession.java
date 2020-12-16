package main.java.persistence;

import java.sql.Connection;
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

	private static CurrentSession session;
	//Usuario que inicia sesión en la aplicación
	private User user;
	//Unidad de negocio al que pertenece el usuario que inicia sesión en la aplicación
	private BusinessUnit bUnit;
	//Empresa a la que pertenece la unidad de negocio
	private Company company;
	//Registra la fecha y hora actuales en el momento de instanciar el objeto
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
		//dateTimeReference = PersistenceManager.getTimestampNow();
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
	}
	
	public void loadCurrentSessionData(Connection conn, int bUnitId, int userId) {
		
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

	public Connection getConn() {
		return connection;
	}

	public void setConn(Connection conn) {
		this.connection = conn;
	}
	
	
}
