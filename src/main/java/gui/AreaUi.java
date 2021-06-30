package main.java.gui;

import java.sql.Timestamp;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

//import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JTextField;

import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextArea;

public class AreaUi extends JPanel {

	//Se asignan a la variable okActionSelector para determinar el comportamiento
	//de la acción okAction
	private static final int OK_ACTION_UNDEFINED = 0; //Por defecto
	private static final int OK_ACTION_EDIT = 1;
	private static final int OK_ACTION_NEW = 2;
	
	private CurrentSession session;
	private Timestamp tNow = ToolBox.getTimestampNow();
	//Temporizador de comprobación de cambios en los datos de la sesión
	private Timer timer;
	//Registra si el panel está visible o no
	private boolean panelVisible;
	
	private JComboBox areaComboBox = new JComboBox();
	private JComboBox bUnitComboBox = new JComboBox();
	private JTextField areaNameField;
	private JTextArea areaDescription;
	
	
	private JLabel infoLabel;
	private JButton editButton;
	private JButton cancelButton;
	private JButton oKButton;
	private JButton newButton;
	private JButton deleteButton;
	
	//Lista de elementos que aparecen en los comboBox
		private String[] areaComboList;
		private String[] bUnitComboList;
	
//	public AreaUi() {
//		
//		
//		// TODO Auto-generated constructor stub
//	}
	
	public AreaUi(CurrentSession session) {
		this.session = session;
		setLayout(null);
		panelVisible = true;
		
		JTextPane areaTxt = new JTextPane();
		areaTxt.setText("GESTIÓN DE AREAS");
		areaTxt.setFont(new Font("Tahoma", Font.BOLD, 20));
		areaTxt.setFocusable(false);
		areaTxt.setEditable(false);
		areaTxt.setBackground(UIManager.getColor(this.getBackground()));
		areaTxt.setBounds(50, 50, 380, 30);
		add(areaTxt);
		
		JLabel areaLabel = new JLabel("Area");
		areaLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		areaLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		areaLabel.setBounds(50, 125, 200, 25);
		add(areaLabel);
		
		JLabel nameLabel = new JLabel("Nombre");
		nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		nameLabel.setBounds(50, 175, 200, 25);
		add(nameLabel);
		
		JLabel descriptionLabel = new JLabel("Descripción");
		descriptionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		descriptionLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		descriptionLabel.setBounds(50, 225, 200, 25);
		add(descriptionLabel);
		
		areaComboBox = new JComboBox();
		areaComboBox.setBounds(260, 125, 400, 25);
		add(areaComboBox);
		
		areaNameField = new JTextField();
		areaNameField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		areaNameField.setBackground(UIManager.getColor(new JPanel().getBackground()));
		areaNameField.setBounds(260, 175, 400, 25);
//		areaNameField.setText(session.getbUnit().getCompany().getNombre());
		areaNameField.setEditable(false);
//		textFieldList.add(areaNameField);
//		textFieldContentList.add(session.getbUnit().getCompany().getNombre());
		add(areaNameField);
		
		areaDescription = new JTextArea();
		areaDescription.setBounds(260, 225, 600, 50);
		add(areaDescription);
	}
}
