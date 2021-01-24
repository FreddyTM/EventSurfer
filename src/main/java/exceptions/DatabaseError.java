package main.java.exceptions;

import javax.swing.JOptionPane;

public class DatabaseError extends Exception {

	public static final String TITLE = "Database error";
	
	public DatabaseError() {
		// TODO Auto-generated constructor stub
	}

	public DatabaseError(String message) {
		super(message);
		JOptionPane.showMessageDialog(null, message, TITLE, JOptionPane.ERROR_MESSAGE);
	}

	public DatabaseError(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public DatabaseError(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public DatabaseError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
