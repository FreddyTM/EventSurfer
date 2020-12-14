package main.java.persistence;

import java.sql.Connection;
import java.sql.Timestamp;

import main.java.company.BusinessUnit;
import main.java.company.Company;
import main.java.company.User;

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
