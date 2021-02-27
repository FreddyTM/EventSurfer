package main.java.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import main.java.persistence.CurrentSession;
import main.java.persistence.PersistenceManager;

public class AppWindow extends JFrame {
	
	private JPanel upPanel;
	private JPanel downPanel;
	private JPanel centerPanel;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel basePanel;
	private JLabel infoLabel;
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

	public AppWindow(GraphicsConfiguration gc) {
		super(gc);
		// TODO Auto-generated constructor stub
	}

	public AppWindow(String title) throws HeadlessException {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public AppWindow(String title, GraphicsConfiguration gc) {
		super(title, gc);
		// TODO Auto-generated constructor stub
	}
	
	public void initialize() {
		//GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
//		GraphicsDevice gDevice = this.getGraphicsConfiguration().getDevice();
//		GraphicsConfiguration[] gConfig = gDevice.getConfigurations();
		
		GraphicsDevice [] displays = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		GraphicsDevice currentDisplay = this.getGraphicsConfiguration().getDevice();
//		int currentWidth = currentDisplay.getDisplayMode().getWidth();
//		int currentHeight = currentDisplay.getDisplayMode().getHeight();
		int currentWidth = 0;
		int currentHeight = 0;
		int appWidth = 1000;
	    int appHeight = 700;
	    setPreferredSize(new Dimension(appWidth, appHeight));
		
		//Debug
		System.out.println(currentDisplay.getIDstring());
		System.out.println("Ancho pantalla: " + currentWidth + "\nAlto pantalla: " + currentHeight);
		
//		int widthToTrim = 0;
//		int heigthToTrim = 0;
		
		if (currentDisplay.getIDstring().equals(displays[0].getIDstring())) {
			currentWidth = currentDisplay.getDisplayMode().getWidth();
			currentHeight = currentDisplay.getDisplayMode().getHeight();
			setBounds((currentWidth - appWidth) / 2, (currentHeight - appHeight) / 2, appWidth, appHeight);
		} else {
			int widthToTrim = displays[0].getDisplayMode().getWidth();
			int heigthToTrim = displays[0].getDisplayMode().getHeight();
			
			for (int i = 0; i < displays.length; i++) {
				
				currentWidth = currentWidth + ((displays[i].getDisplayMode().getWidth() - appWidth) / 2 + widthToTrim);
				currentHeight = currentHeight + ((displays[i].getDisplayMode().getHeight() - appHeight) / 2 + heigthToTrim);
				if (currentDisplay.getIDstring().equals(displays[i].getIDstring())) {
					setBounds(currentWidth, currentHeight, appWidth, appHeight);
					break;
				}
				
				for (int j = 0; j < i; j++) {
					currentWidth = currentWidth + displays[j].getDisplayMode().getWidth();
					currentHeight = currentHeight + displays[j].getDisplayMode().getHeight();
				}
				
				
				
				
				
				currentWidth = currentWidth + displays[i].getDisplayMode().getWidth();
				currentHeight = currentHeight + displays[i].getDisplayMode().getHeight();
				widthToTrim = widthToTrim + displays[i - 1].getDisplayMode().getWidth();
				heigthToTrim = heigthToTrim + displays[i - 1].getDisplayMode().getHeight();
				if (currentDisplay.getIDstring().equals(displays[i].getIDstring())) {
					currentWidth = (currentWidth - widthToTrim) / 2 + widthToTrim;
					currentHeight = (currentHeight - heigthToTrim) / 2 + heigthToTrim;
					break;
				}
//				currentWidth = currentWidth + displays[i].getDisplayMode().getWidth();
//				currentHeight = currentHeight + displays[i].getDisplayMode().getHeight();
//				widthToTrim = widthToTrim + displays[i].getDisplayMode().getWidth();
//				heigthToTrim = heigthToTrim + displays[i].getDisplayMode().getHeight();
			}
			setBounds((currentWidth - appWidth), (currentHeight - appHeight), appWidth, appHeight);
		}
		
		
//		for (int i = 0; i < displays.length; i++) {
//
//			if (currentDisplay.getIDstring().equals(displays[i].getIDstring())) {
//				currentWidth = (currentWidth - widthToTrim) / 2 + widthToTrim;
//				currentHeight = (currentHeight - heigthToTrim) / 2 + heigthToTrim;
//				break;
//			}
//			currentWidth = currentWidth + displays[i].getDisplayMode().getWidth();
//			currentHeight = currentHeight + displays[i].getDisplayMode().getHeight();
//			widthToTrim = widthToTrim + displays[i].getDisplayMode().getWidth();
//			heigthToTrim = heigthToTrim + displays[i].getDisplayMode().getHeight();
//		}
		

//		Rectangle currentDisplayBounds = this.getGraphicsConfiguration().getBounds();
//		int displayWidth = currentDisplayBounds.width;
//		int displayHeight = currentDisplayBounds.height;
		
		//Debug
		System.out.println(currentDisplay.getIDstring());
		System.out.println("Ancho pantalla: " + currentWidth + "\nAlto pantalla: " + currentHeight);
		
	    //OLD CODE
//		int appWidth = 1000;
//	    int appHeight = 700;
//	    setPreferredSize(new Dimension(appWidth, appHeight));
//	    setBounds((currentWidth - appWidth) / 2, (currentHeight - appHeight) / 2, appWidth, appHeight);
//	    setBounds(new Rectangle(appWidth, appHeight));
//	    setLocation((currentWidth - appWidth) / 2, (currentHeight - appHeight) / 2);
		

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
	}
	
	public void setUpWindow(){
		basePanel.removeAll();

		centerPanel = new JPanel();
		JLabel welcome = new JLabel("EVENT SURFER 0.1.7");
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
