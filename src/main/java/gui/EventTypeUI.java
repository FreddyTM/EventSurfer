package main.java.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;
import main.java.types_states.EventType;

public class EventTypeUI extends JPanel {

	//Se asignan a la variable okActionSelector para determinar el comportamiento
	//de la acción okAction
	private static final int OK_ACTION_UNDEFINED = 0; //Por defecto
	private static final int OK_ACTION_EDIT = 1;
	private static final int OK_ACTION_NEW = 2;
	
	private static final String NO_AREA = "<Ningún area seleccionable>";
	private static final String DIALOG_INFO = "info";
	private static final String DIALOG_YES_NO = "yes_no";
	
	private CurrentSession session;
	private Timestamp tNow = ToolBox.getTimestampNow();
	//Temporizador de comprobación de cambios en los datos de la sesión
	private Timer timer;
	//Registra si el panel está visible o no
	private boolean panelVisible;
	
//	private JTextField EventTypeNameField = new JTextField();
	//Lista de todas las areas existentes en la base de datos
	private List<EventType> allEventTypes;
	//Registra el area seleccionada en cada momento
	private EventType selectedEventType;
	//Etiqueta informativa de longitud máxima de datos
	private JLabel maxCharsLabel = new JLabel("Max: 100 caracteres");
	//Backup del contenido del campo Tipo de Evento
	private String textFieldBackup;
	
	//Elementos gráficos
	private JTextField eventTypeNameField = new JTextField();
	private JLabel infoLabel;
	private JButton editButton = new JButton();
	private JButton cancelButton;
	private JButton oKButton;
	private JButton newButton;
	private JButton deleteButton = new JButton();
	
	//Elementos que aparecerán en la lista de tipos de eventos
	private String[] registeredEventTypes;
	//Modelo de datos de la lista de tipos de eventos
	private DefaultListModel<String> registeredModel = new DefaultListModel<String>();
	//Lista de tipos de evento
	private JList<String> registeredList;
	//Contenedor de la lista de tipos de evento
	private JScrollPane registeredScrollPane;
	
	private final Action editAction = new EditAction();
	private final Action cancelAction = new CancelAction();
	private final Action oKAction = new OKAction();
	private final Action newAction = new NewAction();
	private final Action deleteAction = new DeleteAction();
	
	//Registra la acción a realizar por el botón aceptar
	private int okActionSelector = EventTypeUI.OK_ACTION_UNDEFINED;
	
	public EventTypeUI(CurrentSession session) {
		this.session = session;
		setLayout(null);
		panelVisible = true;
		
		JTextPane eventTypeTxt = new JTextPane();
		eventTypeTxt.setText("GESTIÓN DE TIPOS DE EVENTO");
		eventTypeTxt.setFont(new Font("Tahoma", Font.BOLD, 20));
		eventTypeTxt.setFocusable(false);
		eventTypeTxt.setEditable(false);
		eventTypeTxt.setBackground(UIManager.getColor(this.getBackground()));
		eventTypeTxt.setBounds(50, 50, 380, 30);
		add(eventTypeTxt);
		
		JLabel eventTypeLabel = new JLabel("Tipo de evento");
		eventTypeLabel.setHorizontalAlignment(SwingConstants.LEFT);
		eventTypeLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		eventTypeLabel.setBounds(100, 125, 140, 25);
		add(eventTypeLabel);
		
		maxCharsLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel.setBounds(655, 125, 146, 25);
		maxCharsLabel.setVisible(false);
		add(maxCharsLabel);
		
		eventTypeNameField.setBackground(UIManager.getColor(new JPanel().getBackground()));
		eventTypeNameField.setBounds(245, 125, 400, 25);
		eventTypeNameField.setEditable(false);
		add(eventTypeNameField);
		
		infoLabel = new JLabel();
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setBounds(50, 175, 900, 25);
		add(infoLabel);
		
		newButton = new JButton();
		newButton.setAction(newAction);
		newButton.setBounds(204, 225, 89, 23);
		if (session.getUser().getUserType().equals("USER")) {
			newButton.setEnabled(false);
		}
		add(newButton);
		
		editButton.setAction(editAction);
		editButton.setBounds(304, 225, 89, 23);
		if (session.getUser().getUserType().equals("USER")
				|| selectedEventType == null) {
			editButton.setEnabled(false);
		}
		add(editButton);
		
		deleteButton.setAction(deleteAction);
		deleteButton.setBounds(404, 225, 89, 23);
		if (session.getUser().getUserType().equals("USER")
				|| selectedEventType == null) {
			deleteButton.setEnabled(false);
		}
		add(deleteButton);
		
		cancelButton = new JButton();
		cancelButton.setAction(cancelAction);
		cancelButton.setBounds(504, 225, 89, 23);
		cancelButton.setEnabled(false);
		add(cancelButton);
		
		oKButton = new JButton();
		oKButton.setAction(oKAction);
		oKButton.setBounds(604, 225, 89, 23);
		oKButton.setEnabled(false);
		add(oKButton);
		
		JLabel availableLabel = new JLabel("Tipos de eventos registrados");
		availableLabel.setHorizontalAlignment(SwingConstants.LEFT);
		availableLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		availableLabel.setBounds(100, 300, 300, 25);
		add(availableLabel);
		
		registeredList = new JList<String>(registeredModel);
		registeredList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		registeredList.setLayoutOrientation(JList.VERTICAL);		
		registeredList.setVisibleRowCount(8);
		registeredList.setFont(new Font("Tahoma", Font.PLAIN, 15));
		registeredList.addListSelectionListener(new RegisteredListener());
		registeredScrollPane = new JScrollPane(registeredList);
		registeredScrollPane.setBounds(100, 350, 300, 200);
		if (session.getUser().getUserType().equals("USER")
				|| selectedEventType == null) {
			registeredList.setEnabled(false);
		}
		add(registeredScrollPane);
	}
	
	
	
	/**
	 * Listener que monitoriza la selección de la lista de unidades de negocio disponibles
	 */
	private class RegisteredListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
//			if (e.getValueIsAdjusting() == false) {
//		        if (availableList.getSelectedIndex() != -1) {
//		        	if (session.getUser().getUserType().equals("ADMIN")) {			        		
//		        		allocateButton.setEnabled(true);
//		        	} else if (session.getUser().getUserType().equals("MANAGER")) {
//		        		//Comparar bunit seleccionada con bunit de la sesión
//		        		//Si coincide, habilitar botón
//		        		if (session.getUser().getbUnit().getNombre().equals(availableList.getSelectedValue())) {
//		        			allocateButton.setEnabled(true);
//		        		} else {
//		        			allocateButton.setEnabled(false);
//		        		}
//		        	}
//		        }
//		    }
		}		
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
