package main.java.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import main.java.persistence.PersistenceManager;
import main.java.session.CurrentSession;

public class AppWindow extends JFrame {
	
	private JPanel upPanel;
	private JPanel downPanel;
	private JPanel centerPanel;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel basePanel;
	private JLabel infoLabel;
	private String versionNumber = "0.1.9";
	//private TabbedDesk desk;
	private CurrentSession session;
	private Connection conn;

	public AppWindow(String title, Connection conn, CurrentSession session) {
		super(title);
		this.conn = conn;
		this.session = session;
		initialize();
	}
	
	public AppWindow() throws HeadlessException {
		// TODO Auto-generated constructor stub
	}

//	public AppWindow(GraphicsConfiguration gc) {
//		super(gc);
//		// TODO Auto-generated constructor stub
//	}
//
//	public AppWindow(String title) throws HeadlessException {
//		super(title);
//		// TODO Auto-generated constructor stub
//	}
//
//	public AppWindow(String title, GraphicsConfiguration gc) {
//		super(title, gc);
//		// TODO Auto-generated constructor stub
//	}
	
	/**
	 * Crea la ventana de la aplicación y la centra en la pantalla. Envía a la sesión la información de las pantallas del sistema
	 * y de la pantalla en la que se está ejecutando la aplicación. Actualiza dicha información de manera continuada para que,
	 * en un entorno multipantalla, la ventana de login y los mensajes emergentes puedan aparecer siempre centrados en la pantalla 
	 * en la que se ejecuta la aplicación
	 * 
	 */
	public void initialize() {

		GraphicsDevice [] displays = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		GraphicsDevice currentDisplay = this.getGraphicsConfiguration().getDevice();
		session.setDisplays(displays);
		session.setCurrentDisplay(currentDisplay);
		
		//Debug
		System.out.println("Monitor actual: " + currentDisplay.getIDstring());
		
	    int appWidth = 1000;
	    int appHeight = 700;
	    int coordinateX = currentDisplay.getDefaultConfiguration().getBounds().x;
	    int coordinateY = currentDisplay.getDefaultConfiguration().getBounds().y;
	    int currentWidth = 0;
	    int currentHeight = 0;
		//Centrado de pantalla multimonitor
		for (int i = 0; i < displays.length; i++) {
		    if (currentDisplay.getIDstring().equals(displays[i].getIDstring())) {
				currentWidth = currentDisplay.getDisplayMode().getWidth();
				currentHeight = currentDisplay.getDisplayMode().getHeight();
				setBounds((currentWidth - appWidth) / 2 + coordinateX, (currentHeight - appHeight) / 2 + coordinateY, appWidth, appHeight);
		    }
		}

		//OLD CODE
//		Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
//	    int appWidth = 1000;
//	    int appHeight = 700;
//		setPreferredSize(new Dimension(appWidth, appHeight));
//		setBounds(center.x - appWidth / 2, center.y - appHeight / 2, appWidth, appHeight);
	    //OLD CODE

		basePanel = new JPanel();
		basePanel.setLayout(new BorderLayout());
		basePanel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		getContentPane().add(basePanel);

		addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent e) {
				  if(session.getTimer() != null) {
					  session.getTimer().cancel();
				  }		  
				  PersistenceManager.closeDatabase(conn);
				  System.exit(0);
			  }
		});
		
		addWindowStateListener(new CenteredWindow(this, displays));
		addComponentListener(new CenteredWindow(this, displays));
	}
	
	/**
	 * Muestra el panel de bienvenida al pasar la pantalla de login
	 */
	public void setUpWindow(){
		basePanel.removeAll();

		centerPanel = new JPanel();
		JLabel welcome = new JLabel("EVENTSURFER APP " + versionNumber);
		welcome.setFont(new Font("Tahoma", Font.BOLD, 50));
		JLabel emptyLabel = new JLabel();
		emptyLabel.setPreferredSize(new Dimension(MAXIMIZED_HORIZ, 800));
		centerPanel.add(emptyLabel);
		centerPanel.add(welcome);

		leftPanel = new Selector(this, session);
		leftPanel.setBackground(Color.WHITE);
		leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		basePanel.add(centerPanel, BorderLayout.CENTER);
		basePanel.add(leftPanel, BorderLayout.WEST);
		
		this.revalidate();
		this.repaint();
	}
	
	private class CenteredWindow extends WindowAdapter implements ComponentListener{
		private JFrame parentFrame;
		private GraphicsDevice [] displays;
		
		public CenteredWindow (JFrame parentFrame, GraphicsDevice [] displays) {
			this.parentFrame = parentFrame;
			this.displays =  displays;
		}
		
		public void windowStateChanged (WindowEvent e) {
			GraphicsDevice currentDisplay = parentFrame.getGraphicsConfiguration().getDevice();
			session.setDisplays(displays);
			session.setCurrentDisplay(currentDisplay);
			
			//Debug
			System.out.println("Detectado cambio en ventana del programa. Monitor actual: " + currentDisplay.getIDstring());
			
		  }

		@Override
		public void componentResized(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			GraphicsDevice currentDisplay = parentFrame.getGraphicsConfiguration().getDevice();
			session.setDisplays(displays);
			session.setCurrentDisplay(currentDisplay);
			
			//Debug
			System.out.println("Detectado cambio en ventana del programa. Monitor actual: " + currentDisplay.getIDstring());
			
		}

		@Override
		public void componentShown(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentHidden(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}
	}

	public JPanel getUpPanel() {
		return upPanel;
	}

	public void setUpPanel(JPanel upPanel) {
		this.upPanel = upPanel;
	}

	public JPanel getDownPanel() {
		return downPanel;
	}

	public void setDownPanel(JPanel downPanel) {
		this.downPanel = downPanel;
	}

	public JPanel getCenterPanel() {
		return centerPanel;
	}

	public void setCenterPanel(JPanel centerPanel) {
		this.centerPanel = centerPanel;
	}

	public JPanel getLeftPanel() {
		return leftPanel;
	}

	public void setLeftPanel(JPanel leftPanel) {
		this.leftPanel = leftPanel;
	}

	public JPanel getRightPanel() {
		return rightPanel;
	}

	public void setRightPanel(JPanel rightPanel) {
		this.rightPanel = rightPanel;
	}

	public JPanel getBasePanel() {
		return basePanel;
	}

	public void setBasePanel(JPanel basePanel) {
		this.basePanel = basePanel;
	}

	public JLabel getInfoLabel() {
		return infoLabel;
	}

	public void setInfoLabel(JLabel infoLabel) {
		this.infoLabel = infoLabel;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	public CurrentSession getSession() {
		return session;
	}

	public void setSession(CurrentSession session) {
		this.session = session;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

}
