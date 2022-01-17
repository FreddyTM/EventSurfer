package main.java.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import main.java.company.Area;
import main.java.company.User;
import main.java.event.Event;
import main.java.event.EventUpdate;
import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;
import main.java.types_states.EventState;
import main.java.types_states.EventType;
import main.java.types_states.TypesStatesContainer;

public class EventEditUI extends JPanel{
	
	//Formato de presentación de fecha/hora
	private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss";
	private static final String DATE_PATTERN = "dd-MM-yyyy";
	private static final String TIME_PATTERN = "HH:mm";
	
	//Se asignan a la variable actionSelector para determinar la acción a ejecutar
	private static final int EVENTEDIT_ACTION_UNDEFINED = 0; //Default
	private static final int EVENTEDIT_ACTION_NEW_EVENT = 1;
	private static final int EVENTEDIT_ACTION_EDIT_EVENT = 2;
	private static final int EVENTEDIT_ACTION_NEW_UPDATE = 4;
	private static final int EVENTEDIT_ACTION_EDIT_UPDATE = 5;

//	//Tipo de cuadro de diálogo
//	private static final String DIALOG_YES_NO = "yes_no";
	
	//Registra la acción a realizar según el botón activado
	private int actionSelector = EVENTEDIT_ACTION_UNDEFINED;
	
	private CurrentSession session;
	private Selector selector;
	private Timestamp tNow = ToolBox.getTimestampNow();
	//Registra si el panel está visible o no
	private boolean panelVisible;
	
	private JTextField companyField;
	private JTextField bUnitField;
	private JTextField eventDateField;
	private JTextField eventTimeField;
	private JTextField eventTitleField;
	private JTextArea eventDescriptionArea = new JTextArea();
	private JTextField updateDateField;
	private JTextField updateTimeField;
	private JTextArea updateDescriptionArea = new JTextArea();
	private JTextField updateAuthorField;
	private JTextField userField;
	
	private JComboBox areaComboBox = new JComboBox();
	private JComboBox eventTypeComboBox = new JComboBox();
//	private JComboBox userComboBox = new JComboBox();
	private JComboBox eventStateComboBox = new JComboBox();
	
	//Lista de elementos que aparecen en los comboBox
	private String[] areaComboList;
	private String[] eventTypeComboList;
//	private String[] userComboList;
	private String[] eventStateComboList;
	
	private JButton oKButton;
	private JButton cancelButton;
	
	Event eventSelected;
	EventUpdate updateSelected;
	
	public EventEditUI(CurrentSession session, Selector selector, int actionSelector) {
		this.session = session;
		this.selector = selector;
		this.actionSelector = actionSelector;
		setLayout(null);
		panelVisible = true;
		
		//Swing Components
		JTextPane eventEditTxt = new JTextPane();
		eventEditTxt.setText("CREACIÓN / EDICIÓN DE INCIDENCIAS Y ACTUALIZACIONES");
		eventEditTxt.setFont(new Font("Tahoma", Font.BOLD, 20));
		eventEditTxt.setFocusable(false);
		eventEditTxt.setEditable(false);
		eventEditTxt.setBackground(UIManager.getColor(this.getBackground()));
		eventEditTxt.setBounds(50, 50, 700, 30);
		add(eventEditTxt);
		
		JLabel companyLabel = new JLabel("Empresa");
		companyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		companyLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		companyLabel.setBounds(50, 125, 200, 25);
		add(companyLabel);
		
		JLabel bUnitLabel = new JLabel("Unidad de negocio");
		bUnitLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		bUnitLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		bUnitLabel.setBounds(50, 175, 200, 25);
		add(bUnitLabel);
		
		JLabel eventDate = new JLabel("Fecha incidencia");
		eventDate.setHorizontalAlignment(SwingConstants.RIGHT);
		eventDate.setFont(new Font("Tahoma", Font.PLAIN, 20));
		eventDate.setBounds(50, 225, 200, 25);
		add(eventDate);
		
		JLabel eventTime = new JLabel("Hora incidencia");
		eventTime.setHorizontalAlignment(SwingConstants.RIGHT);
		eventTime.setFont(new Font("Tahoma", Font.PLAIN, 20));
		eventTime.setBounds(50, 275, 200, 25);
		add(eventTime);
		
		JLabel areaLabel = new JLabel("Area");
		areaLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		areaLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		areaLabel.setBounds(400, 225, 100, 25);
		add(areaLabel);
		
		JLabel eventTypeLabel = new JLabel("Tipo de evento");
		eventTypeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		eventTypeLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		eventTypeLabel.setBounds(350, 275, 150, 25);
		add(eventTypeLabel);
		
		JLabel eventTitle = new JLabel("Título incidencia");
		eventTitle.setHorizontalAlignment(SwingConstants.RIGHT);
		eventTitle.setFont(new Font("Tahoma", Font.PLAIN, 20));
		eventTitle.setBounds(50, 325, 200, 25);
		add(eventTitle);
		
		JLabel eventDescription = new JLabel("Descripción incidencia");
		eventDescription.setHorizontalAlignment(SwingConstants.RIGHT);
		eventDescription.setFont(new Font("Tahoma", Font.PLAIN, 20));
		eventDescription.setBounds(50, 375, 200, 25);
		add(eventDescription);
		
		JLabel updateDate = new JLabel("Fecha actualización");
		updateDate.setHorizontalAlignment(SwingConstants.RIGHT);
		updateDate.setFont(new Font("Tahoma", Font.PLAIN, 20));
		updateDate.setBounds(50, 475, 200, 25);
		add(updateDate);
		
		JLabel updateTime = new JLabel("Hora actualización");
		updateTime.setHorizontalAlignment(SwingConstants.RIGHT);
		updateTime.setFont(new Font("Tahoma", Font.PLAIN, 20));
		updateTime.setBounds(50, 525, 200, 25);
		add(updateTime);
		
		JLabel authorLabel = new JLabel("Autor");
		authorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		authorLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		authorLabel.setBounds(400, 475, 100, 25);
		add(authorLabel);
		
		JLabel userLabel = new JLabel("Usuario");
		userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		userLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userLabel.setBounds(400, 525, 100, 25);
		add(userLabel);
		
		JLabel updateDescription = new JLabel("Descripción actualización");
		updateDescription.setHorizontalAlignment(SwingConstants.RIGHT);
		updateDescription.setFont(new Font("Tahoma", Font.PLAIN, 20));
		updateDescription.setBounds(25, 575, 225, 25);
		add(updateDescription);
		
		JLabel state = new JLabel("Estado incidencia");
		state.setHorizontalAlignment(SwingConstants.RIGHT);
		state.setFont(new Font("Tahoma", Font.PLAIN, 20));
		state.setBounds(50, 675, 200, 25);
		add(state);
		
		companyField = new JTextField();
		companyField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		companyField.setBackground(UIManager.getColor(new JPanel().getBackground()));
		companyField.setBounds(260, 125, 400, 25);
		companyField.setText(session.getbUnit().getCompany().getNombre());
		companyField.setEditable(false);
//		textFieldList.add(companyField);
//		textFieldContentList.add(session.getbUnit().getCompany().getNombre());
		add(companyField);
		
		bUnitField = new JTextField();
		bUnitField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		bUnitField.setBackground(UIManager.getColor(new JPanel().getBackground()));
		bUnitField.setBounds(260, 175, 400, 25);
		bUnitField.setText(session.getbUnit().getNombre());
		bUnitField.setEditable(false);
//		textFieldList.add(companyField);
//		textFieldContentList.add(session.getbUnit().getCompany().getNombre());
		add(bUnitField);
		
		eventDateField = new JTextField();
//		eventDateField.setText(ToolBox.formatTimestamp(eventSelected.getUpdates().get(0).getFechaHora(), DATE_PATTERN));
//		eventDateField.setEditable(false);
		eventDateField.setColumns(10);
		eventDateField.setBounds(260, 225, 100, 25);
		eventDateField.addActionListener(new EventDateListener());
		add(eventDateField);
		
		eventTimeField = new JTextField();
//		eventTimeField.setText(ToolBox.formatTimestamp(eventSelected.getUpdates().get(0).getFechaHora(), TIME_PATTERN));
//		eventTimeField.setEditable(false);
		eventTimeField.setColumns(10);
		eventTimeField.setBounds(260, 275, 50, 25);
		add(eventTimeField);
		
		eventTitleField = new JTextField();
//		eventTitleField.setText(eventSelected.getTitulo());
//		eventTitleField.setEditable(false);
		eventTitleField.setColumns(10);
		eventTitleField.setBounds(260, 325, 500, 25);
		add(eventTitleField);
		
		eventDescriptionArea.setLineWrap(true);
		eventDescriptionArea.setWrapStyleWord(true);
		eventDescriptionArea.setBounds(260, 375, 500, 75);
		eventDescriptionArea.setBackground(Color.WHITE);
//		eventDescriptionArea.setBackground(UIManager.getColor(new JPanel().getBackground()));	
		eventDescriptionArea.setBorder(eventTitleField.getBorder());
//		eventDescriptionArea.setEditable(false);
		add(eventDescriptionArea);
		
		updateDateField = new JTextField();
//		updateDateField.setText(ToolBox.formatTimestamp(eventSelected.getUpdates().get(0).getFechaHora(), DATE_PATTERN));
//		updateDateField.setEditable(false);
		updateDateField.setColumns(10);
		updateDateField.setBounds(260, 475, 100, 25);
		add(updateDateField);
		
		updateTimeField = new JTextField();
//		updateTimeField.setText(ToolBox.formatTimestamp(eventSelected.getUpdates().get(0).getFechaHora(), TIME_PATTERN));
//		updateTimeField.setEditable(false);
		updateTimeField.setColumns(10);
		updateTimeField.setBounds(260, 525, 50, 25);
		add(updateTimeField);
		
		updateDescriptionArea.setLineWrap(true);
		updateDescriptionArea.setWrapStyleWord(true);
		updateDescriptionArea.setBounds(260, 575, 500, 75);
		updateDescriptionArea.setBackground(Color.WHITE);
//		updateDescriptionArea.setBackground(UIManager.getColor(new JPanel().getBackground()));
		updateDescriptionArea.setBorder(eventTitleField.getBorder());
//		updateDescriptionArea.setEditable(false);
		add(updateDescriptionArea);
		
		updateAuthorField = new JTextField();
//		updateAuthorField.setEditable(false);
		updateAuthorField.setColumns(10);
		updateAuthorField.setBounds(510, 475, 250, 25);
		add(updateAuthorField);
		
		userField = new JTextField();
//		userField.setEditable(false);
		userField.setColumns(10);
		userField.setBounds(510, 525, 250, 25);
		add(userField);
		
		areaComboList = getAreaComboBoxItemsFromSession();
		//Si intentamos crear una nueva incidencia, la lista de areas no puede estar vacía
		//Comprobar en EventDataUI NewEventAction. Mensaje de advertencia de que no se puede añadir
		//incidencias a una unidad de negocio que no tiene ningún area asignada.		
		areaComboBox = new JComboBox();
//		areaComboBox.setSelectedIndex(getSelectedUserIndexFromArray(userComboList, true));
		areaComboBox.setBounds(510, 225, 250, 25);
//		areaComboBox.addItemListener(new AreaComboListener());
		areaComboBox.setEditable(false);
		ToolBox.setBlackForeground(areaComboBox);
		areaComboBox.setBackground(Color.WHITE);
		add(areaComboBox);
		
//		eventTypeComboList = getEventTypeComboBoxItems();
		//Si intentamos crear una nueva incidencia, la lista de tipos de incidencia no puede estar vacía
		//Comprobar en EventDataUI NewEventAction. Mensaje de advertencia de que no se puede añadir
		//incidencias a una unidad de negocio si no hay ningún tipo de incidencia registrada		
		eventTypeComboBox = new JComboBox();
//		eventTypeComboBox.setSelectedIndex(getSelectedUserIndexFromArray(userComboList, true));
		eventTypeComboBox.setBounds(510, 275, 250, 25);
//		eventTypeComboBox.addItemListener(new UserComboListener());
		eventTypeComboBox.setEditable(false);
		ToolBox.setBlackForeground(eventTypeComboBox);
		eventTypeComboBox.setBackground(Color.WHITE);
		add(eventTypeComboBox);
		
//		userComboList = getUserComboBoxItemsFromSession();
//		//La lista nunca estará vacía, porque siempre habrá al menos un usuario creado de inicio
//		userComboBox = new JComboBox(userComboList);
////		userComboBox.setSelectedIndex(getSelectedUserIndexFromArray(userComboList, true));
//		userComboBox.setBounds(510, 525, 250, 25);
////		userComboBox.addItemListener(new UserComboListener());
//		userComboBox.setEditable(false);
//		ToolBox.setBlackForeground(userComboBox);
//		userComboBox.setBackground(Color.WHITE);
//		add(userComboBox);
		
//		eventStateComboList = getEventStateComboBoxItems();
		//La lista nunca estará vacía, porque siempre habrá al menos tres estados de incidencia creados de inicio
//		}		
		eventStateComboBox = new JComboBox();
//		eventStateComboBox.setSelectedIndex(getSelectedUserIndexFromArray(userComboList, true));
		eventStateComboBox.setBounds(260, 675, 200, 25);
//		eventStateComboBox.addItemListener(new UserComboListener());
		eventStateComboBox.setEditable(false);
		ToolBox.setBlackForeground(eventStateComboBox);
		eventStateComboBox.setBackground(Color.WHITE);
		add(eventStateComboBox);
		
		oKButton = new JButton();
//		oKButton.setAction(oKAction);
		oKButton.setText("Aceptar");
		oKButton.setBounds(572, 675, 89, 23);
		add(oKButton);
		
		cancelButton = new JButton();
//		cancelButton.setAction(cancelAction);
		cancelButton.setText("Cancelar");
		cancelButton.setBounds(672, 675, 89, 23);
		add(cancelButton);
		
		//Initial setup
		setup(actionSelector);
		
		
	}
	
	/**
	 * Configura la activación / desactivación de los componentes en pantalla en función de la acción que se vaya a realizar, y la
	 * información inicial que presentan
	 */
	private void setup(int setupOption) {
		switch (setupOption) {
			//New event
			case EVENTEDIT_ACTION_NEW_EVENT:
				//Debug
				System.out.println("Nueva incidencia");
				
				//Estado inicial de los componentes
				//Part of event
				eventDateField.setEditable(true);
				eventTimeField.setEditable(true);
				areaComboBox.setEnabled(true);
				eventTypeComboBox.setEnabled(true);
				eventTitleField.setEditable(true);
				eventDescriptionArea.setEditable(true);
				eventStateComboBox.setEnabled(true);
				//Part of event update
				updateDateField.setEditable(false);
				updateTimeField.setEditable(false);
				updateAuthorField.setEditable(true);
				userField.setEditable(false);
				updateDescriptionArea.setEditable(true);
			
				
				//Información inicial de los componentes
				tNow = ToolBox.getTimestampNow();
				eventDateField.setText(ToolBox.formatTimestamp(tNow, DATE_PATTERN));
				eventTimeField.setText(ToolBox.formatTimestamp(tNow, TIME_PATTERN));
				fillAreaComboBox(-1);
				fillEventTypeComboBox(-1);
				updateDateField.setText(eventDateField.getText());
				updateTimeField.setText(eventTimeField.getText());
				userField.setText(session.getUser().getUserAlias());
				updateDescriptionArea.setText("NUEVA INCIDENCIA");
				fillEventStateComboBox(0);
				
				break;
			//Edit event
			case EVENTEDIT_ACTION_EDIT_EVENT:
				//Debug
				System.out.println("Editando incidencia");
				
				//Estado inicial de los componentes
				//Part of event
				eventDateField.setEditable(true);
				eventTimeField.setEditable(true);
				areaComboBox.setEnabled(true);
				eventTypeComboBox.setEnabled(true);
				eventTitleField.setEditable(true);
				eventDescriptionArea.setEditable(true);
				eventStateComboBox.setEnabled(true);
				//Part of event update
				updateDateField.setEditable(false);
				updateTimeField.setEditable(false);
				updateAuthorField.setEditable(true);
				userField.setEditable(false);
				updateDescriptionArea.setEditable(true);
				
				//Información inicial de los componentes
				
				
				break;
			//New update
			case EVENTEDIT_ACTION_NEW_UPDATE:
				//Debug
				System.out.println("Nueva actualización");
				
				//Estado inicial de los componentes
				//Part of event
				eventDateField.setEditable(false);
				eventTimeField.setEditable(false);
				areaComboBox.setEnabled(false);
				eventTypeComboBox.setEnabled(false);
				eventTitleField.setEditable(false);
				eventDescriptionArea.setEditable(false);
				eventStateComboBox.setEnabled(true);
				//Part of event update
				updateDateField.setEditable(true);
				updateTimeField.setEditable(true);
				updateAuthorField.setEditable(true);
				userField.setEditable(false);
				updateDescriptionArea.setEditable(true);
				
				//Información inicial de los componentes
				
				
				break;
			//Edit update
			case EVENTEDIT_ACTION_EDIT_UPDATE:
				//Debug
				System.out.println("Editando actualización");
				
				//Estado inicial de los componentes
				//Part of event
				eventDateField.setEditable(false);
				eventTimeField.setEditable(false);
				areaComboBox.setEnabled(false);
				eventTypeComboBox.setEnabled(false);
				eventTitleField.setEditable(false);
				eventDescriptionArea.setEditable(false);
				eventStateComboBox.setEnabled(true);
				//Part of event update
				updateDateField.setEditable(true);
				updateTimeField.setEditable(true);
				updateAuthorField.setEditable(true);
				userField.setEditable(false);
				updateDescriptionArea.setEditable(true);
				
				//Información inicial de los componentes
				
				
				break;
			default:
				//Undefined option
		}
	}
	
	
	/**
	 * Obiene el índice del elemento del comboBox que será seleccionado por defecto a partir
	 * del array y el objeto pasados por parámetro
	 * @param array array con la lista de elementos que aparecerán en el combobox
	 * @param object objeto que filtrará el tipo de búsqueda de índice en función de su clase
	 * @return índice del elemento a seleccionar por defecto, 0 si no lo encuentra
	 */
	private int getSelectedIndexFromArray(String[] array, Object object) {
		for (int i = 0; i < array.length; i++) {
			//Si buscamos el índice de una lista de areas 
			if (object.getClass() == Area.class) {
				if (array[i].equals(eventSelected.getArea().getAreaNombre())) {
					return i;
				}
				continue;
			}
			//Si buscamos el índice de una lista de tipos de incidencia
			if (object.getClass() == EventType.class) {
				if (array[i].equals(TypesStatesContainer.getEvType().getEventTypes().get(i + 1))) {
					return i;
				}
				continue;
			}			
			//Si buscamos el índice de una lista de estados de incidencia
			if (object.getClass() == EventState.class) {
				if (array[i].equals(TypesStatesContainer.getEvState().getEventStates().get(i + 1))) {
					return i;
				}
			}
		}
		return 0;
	}
	
	/**
	 * Devuelve la lista de nombres de areas asignadas a la unidad de negocio de la sesión
	 * en orden alfabético
	 * @return array de nombres
	 */
	private String[] getAreaComboBoxItemsFromSession() {
		List<String> tempList = new ArrayList<String>();
		for (Area area: session.getbUnit().getAreas()) {
			tempList.add(area.getAreaNombre());
		}
		return ToolBox.toSortedArray(tempList);
	}
	
	/**
	 * Rellena el contenido del areaComboBox y selecciona el elemento de la lista pasado por parámetro
	 * @param index índice del elemento que se seleccionará por defecto
	 */
	private void fillAreaComboBox(int index) {
		areaComboList = getAreaComboBoxItemsFromSession();
		areaComboBox.setModel(new DefaultComboBoxModel(areaComboList));
		areaComboBox.setSelectedIndex(index);
	}
	
	/**
	 * Rellena el contenido del eventTypeComboBox y selecciona el elemento de la lista pasado por parámetro
	 * @param index índice del elemento que se seleccionará por defecto
	 */
	private void fillEventTypeComboBox(int index) {
		eventTypeComboList = TypesStatesContainer.getEvType().getEventTypesArray();
		eventTypeComboBox.setModel(new DefaultComboBoxModel(eventTypeComboList));
		eventTypeComboBox.setSelectedIndex(index);
	}
	
	/**
	 * Rellena el contenido del eventStateComboBox y selecciona el elemento de la lista pasado por parámetro
	 * @param index índice del elemento que se seleccionará por defecto
	 */
	private void fillEventStateComboBox(int index) {
		eventStateComboList = TypesStatesContainer.getEvState().getEventStatesArray();
		eventStateComboBox.setModel(new DefaultComboBoxModel(eventStateComboList));
		eventStateComboBox.setSelectedIndex(index);
	}
	
	/**
	 * Retorna un timestamp a partir de un string que sigue el formato de pattern
	 * @param stringToParse string que contiene la fecha y la hora a transformar en un objeto Timestamp
	 * @param pattern patrón para dar el formato
	 * @return Timestamp con la fecha y la hora pasadas por parámetro
	 */
	private Timestamp stringToTimestamp(String stringToParse, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		LocalDateTime ldt = LocalDateTime.from(formatter.parse(stringToParse));
		return Timestamp.valueOf(ldt);
	}
	
	/**
	 * Listener que replica el texto de eventDateField en updateDateField
	 */
	private class EventDateListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			updateDateField.setText(eventDateField.getText());
		}

//		@Override
//		public void propertyChange(PropertyChangeEvent evt) {
//			String propertyName = evt.getPropertyName();
//			if (propertyName.equals("graphicsConfiguration")) {
//				updateDateField.setText(eventDateField.getText());
//				
//			}
//			
//			if (propertyName.equals("ancestor")) {
//				updateDateField.setText(eventDateField.getText());
//				
//			}
//			//Debug
//			System.out.println("propiedad " + propertyName);
//			
//			
//		}
		
	}
	
//	/**
//	 * Listener que replica el texto de eventDateField en updateDateField
//	 */
//	private class EventDateListener implements PropertyChangeListener {
////		@Override
////		public void actionPerformed(ActionEvent e) {
////			updateDateField.setText(eventDateField.getText());
////		}
//
//		@Override
//		public void propertyChange(PropertyChangeEvent evt) {
//			String propertyName = evt.getPropertyName();
//			if (propertyName.equals("graphicsConfiguration")) {
//				updateDateField.setText(eventDateField.getText());
//				
//			}
//			
//			if (propertyName.equals("ancestor")) {
//				updateDateField.setText(eventDateField.getText());
//				
//			}
//			//Debug
//			System.out.println("propiedad " + propertyName);
//			
//			
//		}
//		
//	}

	public static int getEventEditActionNewEvent() {
		return EVENTEDIT_ACTION_NEW_EVENT;
	}

	public static int getEventEditActionEditEvent() {
		return EVENTEDIT_ACTION_EDIT_EVENT;
	}

	public static int getEventEditActionNewUpdate() {
		return EVENTEDIT_ACTION_NEW_UPDATE;
	}

	public static int getEventEditActionEditUpdate() {
		return EVENTEDIT_ACTION_EDIT_UPDATE;
	}

}
