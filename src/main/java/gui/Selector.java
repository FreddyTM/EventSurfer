package main.java.gui;

import java.awt.LayoutManager;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import main.java.persistence.CurrentSession;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;

public class Selector extends JPanel {
	
	private AppWindow frame;
	private CurrentSession session;
	private final Action logOutAction = new LogOutAction();
	private final Action CompanyAction = new CompanyAction();
	private final Action bUnitAction = new BunitAction();
	private final Action userAction = new UserAction();
	private JButton eventButton;
	private JButton eventTypeButton;
	private JButton areaButton;
	private JButton userButton;
	private JButton bUnitButton;
	private JButton companyButton;
	private JButton logOutButton;


	/**
	 * @wbp.parser.constructor
	 */
	public Selector(AppWindow frame, CurrentSession session) {
		this.frame = frame;
		this.session = session;
		session.setLogOutAction(logOutAction);
		
		setLayout(new GridLayout(8, 0, 10, 10));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		Dimension dim = new Dimension(100,80);
		
		eventButton = new JButton();
		eventButton.setMargin(new Insets(2,2,2,2));
		eventButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		eventButton.setText("EVENTOS");
		eventButton.setMinimumSize(dim);
		eventButton.setPreferredSize(dim);
		add(eventButton);
		
		eventTypeButton = new JButton();
		eventTypeButton.setMargin(new Insets(2,2,2,2));
		eventTypeButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		eventTypeButton.setText("<html><center>"+"TIPOS" + "<br>" + "DE" + "<br>" + "EVENTO" + "</center></html>");
		eventTypeButton.setMinimumSize(dim);
		eventTypeButton.setPreferredSize(dim);
		add(eventTypeButton);
		
		areaButton = new JButton();
		areaButton.setMargin(new Insets(2,2,2,2));
		areaButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		areaButton.setText("AREAS");
		areaButton.setPreferredSize(dim);
		add(areaButton);
		
		userButton = new JButton();
		userButton.setAction(userAction);
		userButton.setMargin(new Insets(2,2,2,2));
		userButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		userButton.setText("USUARIOS");
		userButton.setPreferredSize(dim);
		add(userButton);
		
		bUnitButton = new JButton();
		bUnitButton.setAction(bUnitAction);
		bUnitButton.setMargin(new Insets(2,2,2,2));
		bUnitButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		bUnitButton.setText("<html><center>"+"UNIDADES" + "<br>" + "DE" + "<br>" + "NEGOCIO" + "</center></html>");
		bUnitButton.setPreferredSize(dim);
		add(bUnitButton);
		
		companyButton = new JButton();
		companyButton.setAction(CompanyAction);
		companyButton.setMargin(new Insets(2,2,2,2));
		companyButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		companyButton.setText("EMPRESA");
		companyButton.setPreferredSize(dim);
		add(companyButton);
		
		JLabel separatorLabel = new JLabel();
		separatorLabel.setPreferredSize(new Dimension(100,160));
		add(separatorLabel);
		
		logOutButton = new JButton();
		logOutButton.setAction(logOutAction);
		logOutButton.setBackground(Color.GRAY);
		logOutButton.setForeground(Color.BLACK);
		logOutButton.setBorder(null);
		logOutButton.setMargin(new Insets(2,2,2,2));
		logOutButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		logOutButton.setText("LOG OUT");
		logOutButton.setPreferredSize(dim);
		add(logOutButton);
	}
	
//	public Selector(LayoutManager layout) {
//		super(layout);
//		// TODO Auto-generated constructor stub
//	}
//
//	public Selector(boolean isDoubleBuffered) {
//		super(isDoubleBuffered);
//		// TODO Auto-generated constructor stub
//	}
//
//	public Selector(LayoutManager layout, boolean isDoubleBuffered) {
//		super(layout, isDoubleBuffered);
//		// TODO Auto-generated constructor stub
//	}
	
	/**
	 * Asigna el panel entrado por parámetro al centerPanel del frame, lo coloca
	 * en la zona central del basePanel y lo hace visible
	 * @param frame ventana de la aplicación
	 * @param panel panel a mostrar
	 */
	public void showPanel(AppWindow frame, JPanel panel) {
		frame.setCenterPanel(panel);
		frame.getBasePanel().add(frame.getCenterPanel(), BorderLayout.CENTER);
		frame.revalidate();
		frame.repaint();
	}
	
	/**
	 * Quita la visibilidad del centerPanel y lo elimina del basePanel
	 * @param frame ventana de la aplicación
	 * @param panel panel a eliminar
	 */
	public void hidePanel(AppWindow frame, JPanel panel) {
		panel.setVisible(false);
		frame.getBasePanel().remove(panel);
	}

	/**
	 * Acción del botón Log Out. Permite salir a la pantalla de login para entrar
	 * al programa con un usuario distinto
	 */
	private class LogOutAction extends AbstractAction {
		public LogOutAction() {
			putValue(NAME, "LogOutAction");
			putValue(SHORT_DESCRIPTION, "Back to login screen");
		}
		public void actionPerformed(ActionEvent e) {
			//Vaciamos el panel base y le quitamos visibilidad
			frame.getBasePanel().removeAll();
			frame.getBasePanel().setVisible(false);
			//Reseteamos la pantalla de la aplicación para cargar de nuevo el panel de login
			frame.initialize();
			Login loginPanel = new Login(frame.getConn(), frame.getSession(), frame);
			//Mostramos el panel
			showPanel(frame, loginPanel);
		}
	}
	
	/**
	 * Acción del botón Empresa. Muestra los datos de la empresa.
	 */
	private class CompanyAction extends AbstractAction {
		public CompanyAction() {
			putValue(NAME, "CompanyAction");
			putValue(SHORT_DESCRIPTION, "Show Company screen");
		}
		public void actionPerformed(ActionEvent e) {
			//Vaciamos el panel base y le quitamos visibilidad
			hidePanel(frame, frame.getCenterPanel());
			//Creamos panel de empresa			
			CompanyUI companyUI = new CompanyUI(session);
			//Mostramos el panel
			showPanel(frame, companyUI);			
		}
	}
	private class BunitAction extends AbstractAction {
		public BunitAction() {
			putValue(NAME, "BunitAction");
			putValue(SHORT_DESCRIPTION, "Show BusinessUnit screen");
		}
		public void actionPerformed(ActionEvent e) {
			//Vaciamos el panel base y le quitamos visibilidad
			hidePanel(frame, frame.getCenterPanel());
			//Creamos panel de unidad de negocio	
			BusinessUnitUI bUnitUI = new BusinessUnitUI(session);
			//Mostramos el panel
			showPanel(frame, bUnitUI);
		}
	}
	private class UserAction extends AbstractAction {
		public UserAction() {
			putValue(NAME, "UserAction");
			putValue(SHORT_DESCRIPTION, "Show user screen");
		}
		public void actionPerformed(ActionEvent e) {
			//Vaciamos el panel base y le quitamos visibilidad
			hidePanel(frame, frame.getCenterPanel());
			//Creamos panel de usuario
			UserUI userUI = new UserUI(session);
			//Mostramos el panel
			showPanel(frame, userUI);
		}
	}

}
