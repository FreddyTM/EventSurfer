package main.java.gui;

import java.awt.Font;
import java.awt.LayoutManager;
import java.sql.Timestamp;
import java.util.Timer;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import main.java.persistence.CurrentSession;
import main.java.persistence.PersistenceManager;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class UserUI extends JPanel {
	
	//Se asignan a la variable okActionSelector para determinar el comportamiento
	//de la acción okAction
	private static final int OK_ACTION_UNDEFINED = 0; //Por defecto
	private static final int OK_ACTION_EDIT = 1;
	private static final int OK_ACTION_NEW = 2;
	
	private CurrentSession session;
	private Timestamp tNow = PersistenceManager.getTimestampNow();
	//Temporizador de comprobación de cambios en los datos de la sesión
	private Timer timer;
	//Registra si el panel está visible o no
	private boolean panelVisible;
	
	
	//Lista de elementos que aparecen en los comboBox
	private String[] bUnitComboList;
	private String[] userComboList;
	private String[] userTypeComboList;

	/**
	 * @wbp.parser.constructor
	 */
	public UserUI(CurrentSession session) {
		this.session = session;
		setLayout(null);
		panelVisible = true;
		
		JTextPane bUnitTxt = new JTextPane();
		bUnitTxt.setFont(new Font("Tahoma", Font.BOLD, 20));
		bUnitTxt.setText("DATOS DE LOS USUARIOS");
		bUnitTxt.setBackground(UIManager.getColor(this.getBackground()));
		bUnitTxt.setEditable(false);
		bUnitTxt.setFocusable(false);
		bUnitTxt.setBounds(50, 50, 380, 30);
		add(bUnitTxt);
		
		JLabel companyLabel = new JLabel("Empresa");
		companyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		companyLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		companyLabel.setBounds(50, 125, 200, 25);
		add(companyLabel);
		
		JLabel selectBunitLabel = new JLabel("Unidad de negocio");
		selectBunitLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		selectBunitLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		selectBunitLabel.setBounds(50, 175, 200, 25);
		add(selectBunitLabel);
		
		JLabel userLabel = new JLabel("Usuario");
		userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		userLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userLabel.setBounds(50, 225, 200, 25);
		add(userLabel);
		
		JLabel userTypeLabel = new JLabel("Tipo de usuario");
		userTypeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		userTypeLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userTypeLabel.setBounds(50, 275, 200, 25);
		add(userTypeLabel);
		
		JLabel userAliasLabel = new JLabel("Alias de usuario");
		userAliasLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		userAliasLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userAliasLabel.setBounds(50, 325, 200, 25);
		add(userAliasLabel);
		
		JLabel userNameLabel = new JLabel("Nombre");
		userNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		userNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userNameLabel.setBounds(50, 375, 200, 25);
		add(userNameLabel);
		
		JLabel userLastNameLabel = new JLabel("Apellido");
		userLastNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		userLastNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userLastNameLabel.setBounds(50, 425, 200, 25);
		add(userLastNameLabel);
		
		JLabel currentPassLabel = new JLabel("Password actual");
		currentPassLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		currentPassLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		currentPassLabel.setBounds(50, 475, 200, 25);
		add(currentPassLabel);
		
		JLabel newPassLabel = new JLabel("Nuevo password");
		newPassLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		newPassLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		newPassLabel.setBounds(50, 525, 200, 25);
		add(newPassLabel);
		
		JLabel confirmPassLabel = new JLabel("Confirmar password");
		confirmPassLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		confirmPassLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		confirmPassLabel.setBounds(50, 575, 200, 25);
		add(confirmPassLabel);
		

	}

	public UserUI(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}

	public UserUI(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public UserUI(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}
}
