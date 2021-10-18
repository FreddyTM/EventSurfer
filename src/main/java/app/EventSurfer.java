
package main.java.app;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.sql.Connection;
import main.java.company.User;
import main.java.gui.AppWindow;
import main.java.gui.DefaultAdmin;
import main.java.gui.Login;
import main.java.persistence.PersistenceManager;
import main.java.session.CurrentSession;

//VERSION 0.1.5

public class EventSurfer {

	private Connection connection;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EventSurfer eventSurfer = new EventSurfer();
					eventSurfer.go(args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public EventSurfer() {

	}

	/**
	 * Conecta con la base de datos e inicia la sesión y la ventana de la aplicación
	 * @param args base de datos a la que conectar. Si no existe parámetro, se conecta
	 * a la base de datos designada por defecto
	 */
	public void go(String[] args) {
		
		if (args.length == 1) {
			connection = PersistenceManager.connectToDatabase(args[0]);

		} else {
			connection = PersistenceManager.connectToDatabase("LOCAL_TEST_DB");			
		}
		
		while(connection == null) {
			//Error screen with reconnect button
		}
		CurrentSession session = CurrentSession.getInstance();
		session.setConnection(connection);
		AppWindow frame = new AppWindow("EVENTSURFER", connection, session);
		frame.setVisible(true);
		User user = new User().getDefaultAdminUser(connection);

		if ((user.getPassword().equals("surferpass"))) {
			//admin password sin cambiar
			System.out.println("Password sin cambiar");
			//DefaultAdmin screen			
			DefaultAdmin adminPanel = new DefaultAdmin(connection, user, session, frame);
			//Añadimos el panel de actualización de datos del usuario administrador
			//por defecto a la ventana
			frame.getBasePanel().add(adminPanel, BorderLayout.CENTER);

		} else {
			//admin password cambiado
			System.out.println("Password cambiado");
			//Login screen
			Login loginPanel = new Login(connection, session, frame);
			//Añadimos el panel de login a la ventana
			frame.getBasePanel().add(loginPanel, BorderLayout.CENTER);
		}
	}
}
