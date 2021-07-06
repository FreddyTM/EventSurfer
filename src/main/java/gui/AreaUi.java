package main.java.gui;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

//import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JTextField;

import main.java.company.Area;
import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;

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
	
	//Registra el area seleccionada por última vez
	private Area lastArea;
	//Lista de etiquetas informativas de longitud máxima de datos
	private List<JLabel> labelList = new ArrayList<JLabel>();
	
	
	private JLabel infoLabel;
	private JButton editButton;
	private JButton cancelButton;
	private JButton oKButton;
	private JButton newButton;
	private JButton deleteButton;
	
	//Lista de elementos que aparecen en los comboBox
	private String[] areaComboList;
	private String[] bUnitComboList;
	
	private final Action editAction = new EditAction();
	private final Action cancelAction = new CancelAction();
	private final Action oKAction = new OKAction();
	private final Action newAction = new NewAction();
	private final Action deleteAction = new DeleteAction();
	//Registra la acción a realizar por el botón aceptar
	private int okActionSelector = AreaUi.OK_ACTION_UNDEFINED;
	
	
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
		areaDescription.setBounds(260, 225, 400, 80);
		add(areaDescription);
		
		
		JLabel maxCharsLabel = new JLabel("Max: 100 caracteres");
		maxCharsLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel.setBounds(670, 175, 146, 25);
		maxCharsLabel.setVisible(false);
		labelList.add(maxCharsLabel);
		add(maxCharsLabel);
		
		JLabel maxCharsLabel2 = new JLabel("Max: 200 caracteres");
		maxCharsLabel2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel2.setBounds(670, 225, 146, 25);
		maxCharsLabel2.setVisible(false);
		labelList.add(maxCharsLabel2);
		add(maxCharsLabel2);
	}
	
	
	
	public class NewAction extends AbstractAction {
		public NewAction() {
			putValue(NAME, "Nuevo");
			putValue(SHORT_DESCRIPTION, "Add new area");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class EditAction extends AbstractAction {
		public EditAction() {
			putValue(NAME, "Editar");
			putValue(SHORT_DESCRIPTION, "Enable data edit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class CancelAction extends AbstractAction {
		public CancelAction() {
			putValue(NAME, "Cancelar");
			putValue(SHORT_DESCRIPTION, "Cancel data edit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class DeleteAction extends AbstractAction {
		public DeleteAction() {
			putValue(NAME, "Borrar");
			putValue(SHORT_DESCRIPTION, "Delete data");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class OKAction extends AbstractAction {
		public OKAction() {
			putValue(NAME, "Aceptar");
			putValue(SHORT_DESCRIPTION, "Save new data or data edit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
