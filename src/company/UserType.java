package company;

public enum UserType {

	SYSADMIN, ADMIN, USER;
	
	public String toString() {
		switch(this) {
			case SYSADMIN: return "SYSADMIN";
			case ADMIN: return "ADMIN";
			case USER: return "USER";
			default : return "UNDEFINED";
		}
	}
}
