package main.java.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
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
	
	private void initialize() {
		//GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		//setBounds(300, 300, 1000, 700);
		
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		//setPreferredSize(new Dimension(1000, 700));
		setMinimumSize(new Dimension(1000, 700));
		basePanel = new JPanel();
		basePanel.setLayout(new BorderLayout());
		basePanel.setBounds(0, 0, 1000, 700);
		basePanel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		getContentPane().add(basePanel);
		//getContentPane().setLayout(new BorderLayout());	

		//getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 15));
		//setBounds(300, 300, 1000, 700);
		
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
	
	public void setUpWindow(int userType){
		centerPanel.removeAll();
		centerPanel.setVisible(false);
		//Añadimos barra de información
		downPanel = new JPanel();
		downPanel.setBackground(Color.WHITE);
		centerPanel = new JPanel();
		leftPanel = new Selector();
		leftPanel.setBackground(Color.WHITE);
		leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
//		Border leftPanelBorder = leftPanel.getBorder();
//		Border blackline = BorderFactory.createLineBorder(Color.black);
//		leftPanel.setBorder(new CompoundBorder(blackline, leftPanelBorder));
		
		infoLabel = new JLabel("PANEL DE INFORMACIÓN");
		infoLabel.setFocusable(false);
		infoLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		downPanel.add(infoLabel);
		this.add(downPanel, BorderLayout.SOUTH);
		//Añadimos panel central con pestañas
		//desk = new TabbedDesk();
		//centerPanel.removeAll();
//		revalidate();
//		repaint();

		//centerPanel.add(desk);
		this.add(centerPanel, BorderLayout.CENTER);
		this.add(leftPanel, BorderLayout.WEST);
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

}
