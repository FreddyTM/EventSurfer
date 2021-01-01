
package main.java.app;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.java.company.Company;
import main.java.company.User;
import main.java.gui.DefaultAdmin;
import main.java.persistence.CurrentSession;
import main.java.persistence.PersistenceManager;
import main.java.types_states.EventState;
import main.java.types_states.EventType;
import main.java.types_states.TypesStatesContainer;
import main.java.types_states.UserType;
import java.awt.Font;


//VERSION 0.0.22


public class EventSurfer {

	Connection connection;
	CurrentSession session;
	private JFrame frame = new JFrame("EVENTSURFER");
	private JPanel upPanel;
	private JPanel downPanel;
	private JPanel centerPanel;
	private JPanel leftPanel;
	private JPanel rightPanel;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EventSurfer eventSurfer = new EventSurfer();
					eventSurfer.frame.setVisible(true);
					eventSurfer.go(args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public EventSurfer() {
		frame.getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 15));
		initialize();
	}
	
	private void initialize() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		frame.setExtendedState(frame.getExtendedState() | frame.MAXIMIZED_BOTH);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setBounds(300, 300, 1000, 700);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent e) {
				  session.getTimer().cancel();
				  try {
					connection.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				  System.exit(0);
			  }
			});
	}
	
	public void go(String[] args) {
		
		//connectToDatabase();
		//System.out.println(args.length);
		if (args.length == 1) {
			connection = connectToDatabase(args[0]);

		} else {
			connection = connectToDatabase("");			
		}
		
		while(connection == null) {
			//Error screen with reconnect button
		}
		session = CurrentSession.getInstance();
		User user = PersistenceManager.getDefaultAdminUser(connection);
		//user.setId(1);
		if ((user.getPassword().equals("surferpass"))) {
			//admin password sin cambiar
			System.out.println("Password sin cambiar");
			//update admin data screen			
			DefaultAdmin adminPanel = new DefaultAdmin(connection, user, session);
			adminPanel.getAliasField().setText(user.getUserAlias());
			adminPanel.getNameField().setText(user.getNombre());
			adminPanel.getLastNameField().setText(user.getApellido());
			centerPanel = adminPanel;
			frame.add(centerPanel, BorderLayout.CENTER);		
			//User id será 1, el administrador por defecto
			//BUnit id será 1, la unidad de negocio por defecto
			//session.loadCurrentSessionData(connection, 1, 1);
		} else {
			//admin password cambiado
			System.out.println("Password cambiado");
			//Login screen
			
			//Add PersistenceManager method to find user and its business unit
			//Instantiate a CurrentSession with company, business unit, user
			//and last db update of business unit data.
			
			//En botón login
			//int bUnitId = PersistenceManager.getBunitIdFromUser(connection, alias, password);
			//int userId = PersistenceManager.getUserId()
			//session.loadCurrentSessionData(connection, int bUnitId, int userId)
		}
		//Until code is completed
		System.out.println("So far so good");
		//PersistenceManager.closeDatabase(connection);
	}
	
	public void checkConnectToDatabase () {
		Connection connection = null;
		String url = "jdbc:postgresql://localhost:5432/surferdb";
		String user = "surferadmin";
		String password = "surferpass";
		
		Connection devConnection = null;
		String devUrl = "jdbc:postgresql://localhost:5432/devsurferdb";
		String devUser = "surferadmin";
		String devPassword = "surferpass";
		
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException ex) {
				System.out.println("No se encuentra el controlador JDBC ("
				+ ex.getMessage() +")");
		}
		
		try{
			//Obtenim una connexió des de DriverManager
			connection = DriverManager.getConnection(url, user, password);
			System.out.println("Connexió a surferdb realitzada usant"
			+ " DriverManager");
			connection.close();
			 
		} catch (SQLException ex) {
			System.out.println("Error " + ex.getMessage());
		}
		
		try{
			//Obtenim una connexió des de DriverManager
			devConnection = DriverManager.getConnection(devUrl, devUser, devPassword);
			System.out.println("Connexió a devsurferdb realitzada usant"
			+ " DriverManager");
			devConnection.close();
			 
		} catch (SQLException ex) {
			System.out.println("Error " + ex.getMessage());
		}
	}
	
	//LOCAL_DB - Local database
	//LOCAL_TEST_DB - Local test database
	//REMOTE_DB - Remote database (Heroku)
	public Connection connectToDatabase(String database) {
		String url = null;
		String user = null;
		String password = null;
		Connection connection = null;
		
		switch(database) {
		case "LOCAL_DB":
			url = "jdbc:postgresql://localhost:5432/surferdb";
			user = "surferadmin";
			password = "surferpass";
			break;
		case "LOCAL_TEST_DB":
			url = "jdbc:postgresql://localhost:5432/devsurferdb";
			user = "surferadmin";
			password = "surferpass";
			break;
		case "REMOTE_DB":
			url = null;
			user = null;
			password = null;
		default:
			url = "jdbc:postgresql://localhost:5432/devsurferdb";
			user = "surferadmin";
			password = "surferpass";
		}
		
		PersistenceManager.setUrl(url);
		PersistenceManager.setUser(user);
		PersistenceManager.setPassword(password);
		connection = PersistenceManager.getConnection();
		return connection;
		
		//PersistenceManager.loadData(connection);	
		//PersistenceManager.closeDatabase(connection);
		
	}

}
