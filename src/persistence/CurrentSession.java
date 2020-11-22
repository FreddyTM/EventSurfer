package persistence;

import company.BusinessUnit;
import company.Company;
import company.User;

public class CurrentSession {

	private Company company;
	private BusinessUnit bUnit;
	private User user;
	
	public CurrentSession(Company company, BusinessUnit bUnit, User user) {
		this.company = company;
		this.bUnit = bUnit;
		this.user = user;
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
	
	
}
