package main.java.exceptions;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class DatabaseError extends Exception {

	public static final String TITLE = "Database error";
	
	public DatabaseError() {
		
	}

	public DatabaseError(String message) {
		super(message);
		JOptionPane.showMessageDialog(new JFrame(), message, TITLE, JOptionPane.ERROR_MESSAGE);
	}
	
	public DatabaseError(String message, JFrame frame) {
		super(message);
		JOptionPane.showMessageDialog(frame, message, TITLE, JOptionPane.ERROR_MESSAGE);
	}

	public DatabaseError(Throwable cause) {
		super(cause);
		
	}

	public DatabaseError(String message, Throwable cause) {
		super(message, cause);
		
	}

	public DatabaseError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
