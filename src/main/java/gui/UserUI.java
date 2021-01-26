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
