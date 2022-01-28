package main.java.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.awt.KeyboardFocusManager;
import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import main.java.persistence.PersistenceManager;
import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;
import main.java.types_states.EventState;
import main.java.types_states.EventType;
import main.java.types_states.TypesStatesContainer;

public class EventEditUI extends JPanel{
	
	//Formato de presentación de fecha/hora
	private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss";
	private static final String VALIDATION_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final String DATE_PATTERN = "dd-MM-yyyy";
	private static final String VALIDATION_DATE_PATTERN = "dd-MM-uuuu";
	private static final String TIME_PATTERN = "HH:mm";
	private static final String TIMESTAMP_GENERATOR_DATE_TIME_PATTERN = "uuuu-MM-dd HH:mm";
	
	//Se asignan a la variable actionSelector para determinar la acción a ejecutar
	private static final int EVENTEDIT_ACTION_UNDEFINED = 0; //Default
	private static final int EVENTEDIT_ACTION_NEW_EVENT = 1;
	private static final int EVENTEDIT_ACTION_EDIT_EVENT = 2;
	private static final int EVENTEDIT_ACTION_NEW_UPDATE = 4;
	private static final int EVENTEDIT_ACTION_EDIT_UPDATE = 5;
	//Registra la acción a realizar según el botón activado
	private int actionSelector = EVENTEDIT_ACTION_UNDEFINED;
	
	//Títulos de pantalla
	private static final String NEW_EVENT_TITLE = "NUEVA INCIDENCIA";
	private static final String EDIT_EVENT_TITLE = "EDITAR INCIDENCIA";
	private static final String NEW_UPDATE_TITLE = "NUEVA ACTUALIZACIÓN";
	private static final String EDIT_UPDATE_TITLE = "EDITAR ACTUALIZACIÓN";
	
	//Tipo de cuadro de diálogo
	private static final String DIALOG_INFO = "info";
	private static final String DIALOG_YES_NO = "yes_no";
	
	private CurrentSession session;
//	private Selector selector;
	private Timestamp tNow = ToolBox.getTimestampNow();
	//Registra si el panel está visible o no
//	private boolean panelVisible;
	
	private JTextPane eventEditTxt;
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
	private JLabel infoLabel;
	private JLabel areaLabel;
	private JLabel eventTypeLabel;
	
	//Lista de etiquetas informativas creación/edición de incidencias/actualizaciones
	private List<JLabel> newEventList = new ArrayList<JLabel>();
	private List<JLabel> editEventList = new ArrayList<JLabel>();
	private List<JLabel> newEditUpdateList = new ArrayList<JLabel>();
	private List<JLabel> editFirstUpdateList = new ArrayList<JLabel>();
	
	private JComboBox areaComboBox = new JComboBox();
	private JComboBox eventTypeComboBox = new JComboBox();
	private JComboBox eventStateComboBox = new JComboBox();
	
	//Lista de elementos que aparecen en los comboBox
	private String[] areaComboList;
	private String[] eventTypeComboList;
	private String[] eventStateComboList;
	
	//Lista de componentes que alertan de error en el formulario
	private List <Component> errorList = new ArrayList<Component>();
	
	private JButton oKButton;
	private JButton cancelButton;
	private final Action oKAction = new OKAction();
	private final Action cancelAction = new CancelAction();
	
	//Listeners
	private EventDateTimeIntroListener eventDateIntroListener;
	private EventDateTimeIntroListener eventTimeIntroListener;
	private EventDateTimeIntroListener updateDateIntroListener;
	private EventDateTimeIntroListener updateTimeIntroListener;
	private EventDateTimeFocusListener eventDateFocusListener;
	private EventDateTimeFocusListener eventTimeFocusListener;
	private EventDateTimeFocusListener updateDateFocusListener;
	private EventDateTimeFocusListener updateTimeFocusListener;
	
	//Incidencia y actualización seleccionados
	private Event eventSelected;
	private EventUpdate updateSelected;
	
	private String stringToParse;
	private EventDataUI eDataUI;
	
	//Registra si una alerta de fecha anterior a actualizaciones previas se ha mostrado ya o no
//	private boolean alertShown = false;
//	private Timestamp updatedTimestamp;
	

	
	public EventEditUI(CurrentSession session, int actionSelector, EventDataUI eDataUI) {
		this.session = session;
//		this.selector = selector;
		this.actionSelector = actionSelector;
		this.eDataUI = eDataUI;
//		eventSelected = eDataUI.getEventSelected();
//		updateSelected = eDataUI.getUpdateSelected();
		setLayout(null);
//		panelVisible = true;
		
		//Swing Components
		eventEditTxt = new JTextPane();
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
		
		areaLabel = new JLabel("Area");
		areaLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		areaLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		areaLabel.setBounds(494, 225, 46, 25);
		errorList.add(areaLabel);
		add(areaLabel);
		
		eventTypeLabel = new JLabel("Tipo de incidencia");
		eventTypeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		eventTypeLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		eventTypeLabel.setBounds(398, 275, 142, 25);
		errorList.add(eventTypeLabel);
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
		authorLabel.setBounds(440, 475, 100, 25);
		add(authorLabel);
		
		JLabel userLabel = new JLabel("Usuario");
		userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		userLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userLabel.setBounds(440, 525, 100, 25);
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
		
		JLabel dateFormatLabel = new JLabel("DD-MM-AAAA");
		dateFormatLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		dateFormatLabel.setBounds(340, 225, 146, 25);
		dateFormatLabel.setVisible(false);
		newEventList.add(dateFormatLabel);
		editEventList.add(dateFormatLabel);
		add(dateFormatLabel);
		
		JLabel timeFormatLabel = new JLabel("HH:MM");
		timeFormatLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		timeFormatLabel.setBounds(310, 275, 146, 25);
		timeFormatLabel.setVisible(false);
		newEventList.add(timeFormatLabel);
		editEventList.add(timeFormatLabel);
		add(timeFormatLabel);
		
		JLabel chooseAreaLabel = new JLabel("Escoger");
		chooseAreaLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		chooseAreaLabel.setBounds(810, 225, 146, 25);
		chooseAreaLabel.setVisible(false);
		newEventList.add(chooseAreaLabel);
		editEventList.add(chooseAreaLabel);
		add(chooseAreaLabel);
		
		JLabel chooseEventTypeLabel = new JLabel("Escoger");
		chooseEventTypeLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		chooseEventTypeLabel.setBounds(810, 275, 146, 25);
		chooseEventTypeLabel.setVisible(false);
		newEventList.add(chooseEventTypeLabel);
		editEventList.add(chooseEventTypeLabel);
		add(chooseEventTypeLabel);
		
		JLabel maxCharsTitleLabel = new JLabel("Max: 200 caracteres");
		maxCharsTitleLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsTitleLabel.setBounds(810, 325, 146, 25);
		maxCharsTitleLabel.setVisible(false);
		newEventList.add(maxCharsTitleLabel);
		editEventList.add(maxCharsTitleLabel);
		add(maxCharsTitleLabel);
		
		JLabel mustDescribeEventLabel = new JLabel("Descripción obligatoria");
		mustDescribeEventLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mustDescribeEventLabel.setBounds(810, 375, 146, 25);
		mustDescribeEventLabel.setVisible(false);
		newEventList.add(mustDescribeEventLabel);
		editEventList.add(mustDescribeEventLabel);
		add(mustDescribeEventLabel);
		
		JLabel maxCharsAuthorLabel = new JLabel("Max: 50 caracteres (opcional)");
		maxCharsAuthorLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsAuthorLabel.setBounds(810, 475, 200, 25);
		maxCharsAuthorLabel.setVisible(false);
		newEventList.add(maxCharsAuthorLabel);
		newEditUpdateList.add(maxCharsAuthorLabel);
		editFirstUpdateList.add(maxCharsAuthorLabel);
		add(maxCharsAuthorLabel);
		
		JLabel mustDescribeUpdateLabel = new JLabel("Descripción obligatoria");
		mustDescribeUpdateLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mustDescribeUpdateLabel.setBounds(810, 575, 146, 25);
		mustDescribeUpdateLabel.setVisible(false);
		newEventList.add(mustDescribeUpdateLabel);
		newEditUpdateList.add(mustDescribeUpdateLabel);
		editFirstUpdateList.add(mustDescribeUpdateLabel);
		add(mustDescribeUpdateLabel);
		
		JLabel dateFormatLabel2 = new JLabel("DD-MM-AAAA");
		dateFormatLabel2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		dateFormatLabel2.setBounds(340, 475, 146, 25);
		dateFormatLabel2.setVisible(false);
		newEditUpdateList.add(dateFormatLabel2);
		add(dateFormatLabel2);
		
		JLabel timeFormatLabel2 = new JLabel("HH:MM");
		timeFormatLabel2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		timeFormatLabel2.setBounds(310, 525, 146, 25);
		timeFormatLabel2.setVisible(false);
		newEditUpdateList.add(timeFormatLabel2);
		add(timeFormatLabel2);
		
		companyField = new JTextField();
		companyField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		companyField.setBackground(UIManager.getColor(new JPanel().getBackground()));
		companyField.setBounds(260, 125, 400, 25);
		companyField.setText(session.getbUnit().getCompany().getNombre());
		companyField.setEditable(false);
		companyField.setFocusable(false);
		add(companyField);
		
		bUnitField = new JTextField();
		bUnitField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		bUnitField.setBackground(UIManager.getColor(new JPanel().getBackground()));
		bUnitField.setBounds(260, 175, 400, 25);
		bUnitField.setText(session.getbUnit().getNombre());
		bUnitField.setEditable(false);
		bUnitField.setFocusable(false);
		add(bUnitField);
		
		eventDateField = new JTextField();
		eventDateField.setText(ToolBox.formatTimestamp(tNow, DATE_PATTERN));
		eventDateField.setColumns(10);
		eventDateField.setBounds(260, 225, 70, 25);
		add(eventDateField);
		
		eventTimeField = new JTextField();
		eventTimeField.setText(ToolBox.formatTimestamp(tNow, TIME_PATTERN));
		eventTimeField.setColumns(10);
		eventTimeField.setBounds(260, 275, 40, 25);
		add(eventTimeField);
		
		eventTitleField = new JTextField();
		eventTitleField.setColumns(10);
		eventTitleField.setBounds(260, 325, 540, 25);
		errorList.add(eventTitleField);
		add(eventTitleField);
		
		eventDescriptionArea.setLineWrap(true);
		eventDescriptionArea.setWrapStyleWord(true);
		eventDescriptionArea.setBounds(260, 375, 540, 75);
		eventDescriptionArea.setBackground(Color.WHITE);
//		eventDescriptionArea.setBackground(UIManager.getColor(new JPanel().getBackground()));	
		eventDescriptionArea.setBorder(eventTitleField.getBorder());
//		eventDescriptionArea.setEditable(false);
		errorList.add(eventDescriptionArea);
		add(eventDescriptionArea);
		
		updateDateField = new JTextField();
		updateDateField.setText(eventDateField.getText());
//		updateDateField.setEditable(false);
		updateDateField.setColumns(10);
		updateDateField.setBounds(260, 475, 70, 25);
		add(updateDateField);
		
		updateTimeField = new JTextField();
		updateTimeField.setText(eventTimeField.getText());
//		updateTimeField.setEditable(false);
		updateTimeField.setColumns(10);
		updateTimeField.setBounds(260, 525, 40, 25);
		add(updateTimeField);
		
		//Listeners para los campos de fecha y hora		
		eventDateIntroListener = new EventDateTimeIntroListener(eventDateField.getText(), VALIDATION_DATE_PATTERN);
		eventTimeIntroListener = new EventDateTimeIntroListener(eventTimeField.getText(), TIME_PATTERN); //
		updateDateIntroListener = new EventDateTimeIntroListener(updateDateField.getText(), VALIDATION_DATE_PATTERN); //
		updateTimeIntroListener = new EventDateTimeIntroListener(updateTimeField.getText(), TIME_PATTERN); //
		eventDateFocusListener = new EventDateTimeFocusListener(eventDateField.getText(), VALIDATION_DATE_PATTERN); //
		eventTimeFocusListener = new EventDateTimeFocusListener(eventTimeField.getText(), TIME_PATTERN); //
		updateDateFocusListener = new EventDateTimeFocusListener(updateDateField.getText(), VALIDATION_DATE_PATTERN); //
		updateTimeFocusListener = new EventDateTimeFocusListener(updateTimeField.getText(), TIME_PATTERN); //
				
		eventDateField.addActionListener(eventDateIntroListener);
		eventTimeField.addActionListener(eventTimeIntroListener);
		updateDateField.addActionListener(updateDateIntroListener);
		updateTimeField.addActionListener(updateTimeIntroListener);
		eventDateField.addFocusListener(eventDateFocusListener);
		eventTimeField.addFocusListener(eventTimeFocusListener);
		updateDateField.addFocusListener(updateDateFocusListener);
		updateTimeField.addFocusListener(updateTimeFocusListener);
		
		updateDescriptionArea.setLineWrap(true);
		updateDescriptionArea.setWrapStyleWord(true);
		updateDescriptionArea.setBounds(260, 575, 540, 75);
		updateDescriptionArea.setBackground(Color.WHITE);
//		updateDescriptionArea.setBackground(UIManager.getColor(new JPanel().getBackground()));
		updateDescriptionArea.setBorder(eventTitleField.getBorder());
		errorList.add(updateDescriptionArea);
		add(updateDescriptionArea);
		
		updateAuthorField = new JTextField();
		updateAuthorField.setColumns(10);
		updateAuthorField.setBounds(550, 475, 250, 25);
		add(updateAuthorField);
		
		userField = new JTextField();
		userField.setColumns(10);
		userField.setBounds(550, 525, 250, 25);
		userField.setFocusable(false);
		add(userField);
		
		areaComboList = getAreaComboBoxItemsFromSession();
		//Si intentamos crear una nueva incidencia, la lista de areas no puede estar vacía
		//Comprobar en EventDataUI NewEventAction. Mensaje de advertencia de que no se puede añadir
		//incidencias a una unidad de negocio que no tiene ningún area asignada.		
		areaComboBox = new JComboBox();
		areaComboBox.setBounds(550, 225, 250, 25);
		areaComboBox.setEditable(false);
		ToolBox.setBlackForeground(areaComboBox);
		areaComboBox.setBackground(Color.WHITE);
		add(areaComboBox);
		
		eventTypeComboList = TypesStatesContainer.getEvType().getEventTypesArray();
		//Si intentamos crear una nueva incidencia, la lista de tipos de incidencia no puede estar vacía
		//Comprobar en EventDataUI NewEventAction. Mensaje de advertencia de que no se puede añadir
		//incidencias a una unidad de negocio si no hay ningún tipo de incidencia registrada		
		eventTypeComboBox = new JComboBox();
		eventTypeComboBox.setBounds(550, 275, 250, 25);
		eventTypeComboBox.setEditable(false);
		ToolBox.setBlackForeground(eventTypeComboBox);
		eventTypeComboBox.setBackground(Color.WHITE);
		add(eventTypeComboBox);
		
		eventStateComboList = TypesStatesContainer.getEvState().getEventStatesArray();
		//La lista nunca estará vacía, porque siempre habrá al menos tres estados de incidencia creados de inicio
		eventStateComboBox = new JComboBox();
		eventStateComboBox.setBounds(260, 675, 200, 25);
		eventStateComboBox.setEditable(false);
		ToolBox.setBlackForeground(eventStateComboBox);
		eventStateComboBox.setBackground(Color.WHITE);
		add(eventStateComboBox);
		
		oKButton = new JButton();
		oKButton.setAction(oKAction);
		oKButton.setText("Aceptar");
		oKButton.setBounds(611, 675, 89, 23);
		add(oKButton);
		
		cancelButton = new JButton();
		cancelButton.setAction(cancelAction);
		cancelButton.setText("Cancelar");
		cancelButton.setBounds(711, 675, 89, 23);
		add(cancelButton);
		
		infoLabel = new JLabel();
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setBounds(50, 725, 900, 25);
		add(infoLabel);
		
		//Initial setup
		setup(actionSelector);
		
		
	}
	
	/**
	 * Configura la activación / desactivación / visualización de los componentes en pantalla en función de la acción
	 * que se vaya a realizar, y la información inicial que presentan (si dicha información depende de la incidencia
	 * y/o de la actualización seleccionadas)
	 */
	private void setup(int setupOption) {
		actionSelector = setupOption;
		int index = -1;

		eventSelected = eDataUI.getEventSelected();
		updateSelected = eDataUI.getUpdateSelected();
		switch (setupOption) {
			//New event
			case EVENTEDIT_ACTION_NEW_EVENT:
				//Debug
				System.out.println("Nueva incidencia");
				
				eventEditTxt.setText(NEW_EVENT_TITLE);
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
				//Labels
				for (JLabel label: newEventList) {
					label.setVisible(true);
				}
				
				//Información inicial de los componentes
				eventDateField.setText(ToolBox.formatTimestamp(tNow, DATE_PATTERN));
				eventTimeField.setText(ToolBox.formatTimestamp(tNow, TIME_PATTERN));
				fillAreaComboBox(index);
				fillEventTypeComboBox(index);
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
				
				eventEditTxt.setText(EDIT_EVENT_TITLE);
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
				updateAuthorField.setEditable(false);
				userField.setEditable(false);
				updateDescriptionArea.setEditable(false);
				updateDescriptionArea.setBackground(UIManager.getColor(new JPanel().getBackground()));
				//Información inicial de los componentes
				eventDateField.setText(ToolBox.formatTimestamp(eventSelected.getUpdates().get(0).getFechaHora(),
						DATE_PATTERN));
				eventTimeField.setText(ToolBox.formatTimestamp(eventSelected.getUpdates().get(0).getFechaHora(),
						TIME_PATTERN));
				index = getSelectedIndexFromArray(areaComboList, eventSelected.getArea());
				fillAreaComboBox(index);
				index = getSelectedIndexFromArray(eventTypeComboList, TypesStatesContainer.getEvType());
				fillEventTypeComboBox(index);
				eventTitleField.setText(eventSelected.getTitulo());
				eventDescriptionArea.setText(eventSelected.getDescripcion());
				updateDateField.setText(eventDateField.getText());
				updateTimeField.setText(eventTimeField.getText());
				updateAuthorField.setText(eventSelected.getUpdates().get(0).getAutor());
				userField.setText(eventSelected.getUpdates().get(0).getUser().getUserAlias());
				updateDescriptionArea.setText(eventSelected.getUpdates().get(0).getDescripcion());
				index = getSelectedIndexFromArray(eventStateComboList, TypesStatesContainer.getEvState());
				fillEventStateComboBox(index);
				
				//Labels
				for (JLabel label: editEventList) {
					label.setVisible(true);
				}
				//Actualizamos datos de los listeners
				eventDateIntroListener.setOldEventDateText(eventDateField.getText());
				eventTimeIntroListener.setOldEventTimeText(eventTimeField.getText());
				eventDateFocusListener.setOldEventDateText(eventDateField.getText());
				eventTimeFocusListener.setOldEventTimeText(eventTimeField.getText());
				
				break;
			//New update
			case EVENTEDIT_ACTION_NEW_UPDATE:
				//Debug
				System.out.println("Nueva actualización");
				
				eventEditTxt.setText(NEW_UPDATE_TITLE);
				//Estado inicial de los componentes
				//Part of event
				eventDateField.setEditable(false);
				eventTimeField.setEditable(false);
				areaComboBox.setEnabled(false);
				eventTypeComboBox.setEnabled(false);
				eventTitleField.setEditable(false);
				eventDescriptionArea.setEditable(false);
				eventDescriptionArea.setBackground(UIManager.getColor(new JPanel().getBackground()));
				eventStateComboBox.setEnabled(true);
				//Part of event update
				updateDateField.setEditable(true);
				updateTimeField.setEditable(true);
				updateAuthorField.setEditable(true);
				userField.setEditable(false);
				updateDescriptionArea.setEditable(true);
				
				//Información inicial de los componentes
				eventDateField.setText(ToolBox.formatTimestamp(eventSelected.getUpdates().get(0).getFechaHora(),
						DATE_PATTERN));
				eventTimeField.setText(ToolBox.formatTimestamp(eventSelected.getUpdates().get(0).getFechaHora(),
						TIME_PATTERN));
				index = getSelectedIndexFromArray(areaComboList, eventSelected.getArea());
				fillAreaComboBox(index);
				index = getSelectedIndexFromArray(eventTypeComboList, TypesStatesContainer.getEvType());
				fillEventTypeComboBox(index);
				eventTitleField.setText(eventSelected.getTitulo());
				eventDescriptionArea.setText(eventSelected.getDescripcion());
				updateDateField.setText(ToolBox.formatTimestamp(tNow, DATE_PATTERN));
				updateTimeField.setText(ToolBox.formatTimestamp(tNow, TIME_PATTERN));
				userField.setText(session.getUser().getUserAlias());
				index = getSelectedIndexFromArray(eventStateComboList, TypesStatesContainer.getEvState());
				fillEventStateComboBox(index);
				
				//Labels
				for (JLabel label: newEditUpdateList) {
					label.setVisible(true);
				}
				//Actualizamos datos de los listeners
				updateDateIntroListener.setOldUpdateDateText(updateDateField.getText());
				updateTimeIntroListener.setOldUpdateTimeText(updateTimeField.getText());
				updateDateFocusListener.setOldUpdateDateText(updateDateField.getText());
				updateTimeFocusListener.setOldUpdateTimeText(updateTimeField.getText());
				
				break;
			//Edit update
			case EVENTEDIT_ACTION_EDIT_UPDATE:
				//Debug
				System.out.println("Editando actualización");
				
				eventEditTxt.setText(EDIT_UPDATE_TITLE);
				//Estado inicial de los componentes
				//Part of event
				eventDateField.setEditable(false);
				eventTimeField.setEditable(false);
				areaComboBox.setEnabled(false);
				eventTypeComboBox.setEnabled(false);
				eventTitleField.setEditable(false);
				eventDescriptionArea.setEditable(false);
				eventDescriptionArea.setBackground(UIManager.getColor(new JPanel().getBackground()));
				eventStateComboBox.setEnabled(true);
				//Part of event update
				if (!eDataUI.getDeleteUpdateButton().isEnabled()) {
					updateDateField.setEditable(false);
					updateTimeField.setEditable(false);
				} else {
					updateDateField.setEditable(true);
					updateTimeField.setEditable(true);
				}
				updateAuthorField.setEditable(true);
				userField.setEditable(false);
				updateDescriptionArea.setEditable(true);
				
				//Información inicial de los componentes
				eventDateField.setText(ToolBox.formatTimestamp(eventSelected.getUpdates().get(0).getFechaHora(),
						DATE_PATTERN));
				eventTimeField.setText(ToolBox.formatTimestamp(eventSelected.getUpdates().get(0).getFechaHora(),
						TIME_PATTERN));
				index = getSelectedIndexFromArray(areaComboList, eventSelected.getArea());
				fillAreaComboBox(index);
				index = getSelectedIndexFromArray(eventTypeComboList, TypesStatesContainer.getEvType());
				fillEventTypeComboBox(index);
				eventTitleField.setText(eventSelected.getTitulo());
				eventDescriptionArea.setText(eventSelected.getDescripcion());
				updateDateField.setText(ToolBox.formatTimestamp(updateSelected.getFechaHora(), DATE_PATTERN));
				updateTimeField.setText(ToolBox.formatTimestamp(updateSelected.getFechaHora(), TIME_PATTERN));
				updateAuthorField.setText(updateSelected.getAutor());
				userField.setText(updateSelected.getUser().getUserAlias());
				index = getSelectedIndexFromArray(eventStateComboList, TypesStatesContainer.getEvState());
				fillEventStateComboBox(index);
				updateDescriptionArea.setText(updateSelected.getDescripcion());
				//Labels
				//Edición de la actualización inicial
				if (updateSelected == eventSelected.getUpdates().get(0)) {
					for (JLabel label : editFirstUpdateList) {
						label.setVisible(true);
					} 
				//Edición del resto de actualizaciones
				} else {
					for (JLabel label : newEditUpdateList) {
						label.setVisible(true);
					} 
				}
				//Actualizamos datos de los listeners
				updateDateIntroListener.setOldUpdateDateText(updateDateField.getText());
				updateTimeIntroListener.setOldUpdateTimeText(updateTimeField.getText());
				updateDateFocusListener.setOldUpdateDateText(updateDateField.getText());
				updateTimeFocusListener.setOldUpdateTimeText(updateTimeField.getText());
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
				if (array[i].equals(eventSelected.getEventType())) {
					return i;
				}
//				if (array[i].equals(TypesStatesContainer.getEvType().getEventTypes().get(i + 1))) {
//					return i;
//				}
				continue;
			}			
			//Si buscamos el índice de una lista de estados de incidencia
			if (object.getClass() == EventState.class) {
				if (array[i].equals(eventSelected.getEventState())) {
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
		areaComboList = getAreaComboBoxItemsFromSession(); //Comprobar si se puede borrar
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
	 * Construye un string con formato Timestamp a partir de dos strings que contienen
	 * la fecha y la hora
	 * @param date texto que contiene la fecha en formato dd-MM-uuuu
	 * @param time texto que contiene la hora en formato HH:mm
	 * @return texto en formato Timestamp (uuuu-MM-dd HH:mm)
	 */
	private String buildStringTimestamp (String date, String time) {
		String[] splitDate = date.split("-");
		String stringTimestamp = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0] + " "
				+ time;
		return stringTimestamp;
	}
	
	/**
	 * Retorna un timestamp a partir de un string que sigue el formato de pattern
	 * @param stringToParse string que contiene la fecha y la hora a transformar en un objeto Timestamp
	 * @param pattern patrón para dar el formato
	 * @return Timestamp con la fecha y la hora pasadas por parámetro
	 */
	private Timestamp stringToTimestamp(String stringToParse, String pattern) {
		LocalDateTime ldt = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
			ldt = LocalDateTime.parse(stringToParse, formatter);
		} catch (Exception e) {
			System.out.println("Error de formato de fecha / hora");
			return null;
		}
		return Timestamp.valueOf(ldt);
	}
	
    /**
     * Comprueba que la fecha introducida respeta el formato establecido y es una fecha válida
     * @param date fecha a comprobar
     * @param pattern formato de fecha
     * @return true si la fecha es correcta y respeta el formato, falso en caso contrario
     */
	public static boolean dateIsValid(final String date, String pattern) {
        boolean validDate = false;
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern)
                            .withResolverStyle(ResolverStyle.STRICT));
            System.out.println("Formato de fecha correcto");
            validDate = true;
        } catch (DateTimeParseException e) {
            System.out.println("Error de formato de fecha");
            validDate = false;
        }
        return validDate;
    }

    /**
     * Comprueba que la hora introducida respeta el formato establecido y es una hora válida
     * @param time hora a comprobar
     * @param pattern formato de hora
     * @return true si la hora es correcta y respeta el formato, falso en caso contrario
     */
	public static boolean timeIsValid(final String time, String pattern) {
        boolean validTime = false;
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern(pattern)
            				.withResolverStyle(ResolverStyle.STRICT));
            System.out.println("Formato de hora correcto");
            validTime = true;
        } catch (DateTimeParseException e) {
            System.out.println("Error de formato de hora");
            validTime = false;
        }
        return validTime;
    }
	
	/**
	 * Comprueba si la fecha y la hora pasada por parámetro son anteriores a la fecha y la hora de
	 * la creación de la incidencia seleccionada
	 * @param stringToParse texto que contiene la fecha y la hora a comprobar
	 * @return true si la fecha es posterior, false si es anterior
	 */
	private boolean isDateTimeBeforeEventDateTime(String stringToParse) {
		Timestamp dateTimeTimestamp = stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN);
		if (dateTimeTimestamp.before(eventSelected.getUpdates().get(0).getFechaHora())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Comprueba si la fecha y la hora pasada por parámetro son anteriores a la fecha y la hora de
	 * la actualización siguiente a la actualización inicial (si existe)
	 * @param stringToParse texto que contiene la fecha y la hora a comprobar
	 * @return true si la fecha es posterior, false si es anterior
	 */
	private boolean isDateTimeAfterEventUpdatesDateTime(String stringToParse) {
		Timestamp dateTimeTimestamp = stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN);
		//Si la incidencia seleccionada tiene actualizaciones (aparte de la actualización inicial)
		if (eventSelected.getUpdates().size() > 1) {
			if (dateTimeTimestamp.after(eventSelected.getUpdates().get(1).getFechaHora())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Comprueba si la fecha de la actualización es anterior a la de anteriores actualizaciones
	 * @param index índice de la fila que ocupa la actualización cuya fecha queremos comprobar
	 * @return true si la fecha de la actualización a comprobar es anterior a la fecha de la actualización previa,
	 * false en caso contrario
	 */
	private boolean isDateTimebeforePreviousUpdatesDateTime(String stringToParse, int index) {
		
//		//Debug
//		System.out.println("Indice de entrada al método isDateTimebeforePreviousUpdatesDateTime: " + index);
		Timestamp dateTimeAtIndex = stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN);
//	
//		//Debug
//				System.out.println("dateTimeAtIndex: " + dateTimeAtIndex);

		Timestamp dateTimeAtPreviousIndex = (Timestamp) eDataUI.getUpdatesTable().getModel().getValueAt(index - 1, 2);
//		
//		//Debug
//		System.out.println("dateTimeAtPreviousIndex: " + dateTimeAtPreviousIndex);

		//Si la fecha a comprobar es anterior a la fecha de la actualización anterior
		if (dateTimeAtIndex.before(dateTimeAtPreviousIndex)) {
//			System.out.println("dateTimeAtIndex.before(dateTimeAtPreviousIndex)");
			return true;
		}
		return false;
	}
	
	
	/**
	 * Comprueba la corrección de los datos introducidos en el formulario. Cualquier dato incorrecto se resalta
	 * con el fondo del campo en amarillo
	 * @return true si son correctos, false si no lo son
	 */
	private boolean testData() { 
		
		//FALTA COMPROBAR FECHAS EN TODOS LOS CASOS PARA QUE NO SEAN ANTERIORES A LAS DE INCIDENCIAS O
		//ACTUALIZACIONES YA CREADAS O A LAS PRIMERAS ACTUALIZACIONES
		
		//Comprobamos que los datos no exceden el tamaño máximo, no llegan al mínimo, o no se selecciona ninguna
		//opción de los combobox
		boolean goodData = true;
		String errorLengthText = "TAMAÑO MÁXIMO DE TEXTO SUPERADO O FALTAN DATOS.";
		String errorComboBoxItem = "SE DEBE DE ESCOGER UNA OPCIÓN";
		
		//Si estamos creando una incidencia nueva
		if (actionSelector == EVENTEDIT_ACTION_NEW_EVENT) {
			if (areaComboBox.getSelectedIndex() == -1) {
				infoLabel.setText(errorComboBoxItem);
				areaLabel.setOpaque(true);
				areaLabel.setBackground(Color.YELLOW);
				goodData = false;
			}

			if (eventTypeComboBox.getSelectedIndex() == -1) {
				infoLabel.setText(errorComboBoxItem);
				eventTypeLabel.setOpaque(true);
				eventTypeLabel.setBackground(Color.YELLOW);
				goodData =  false;
			}
			//Comprobamos la longitud de los datos
			if (eventTitleField.getText().length() > 200 || eventTitleField.getText().length() == 0) {
				eventTitleField.setBackground(Color.YELLOW);
				infoLabel.setText(errorLengthText);
				goodData = false;
			}
			if (eventDescriptionArea.getText().length() == 0) {
				eventDescriptionArea.setBackground(Color.YELLOW);
				infoLabel.setText(errorLengthText);
				goodData = false;
			}
			if (updateDescriptionArea.getText().length() == 0) {
				updateDescriptionArea.setBackground(Color.YELLOW);
				infoLabel.setText(errorLengthText);
				goodData = false;
			}
		}
		
		if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {
			//Comprobamos la longitud de los datos
			if (eventTitleField.getText().length() > 200 || eventTitleField.getText().length() == 0) {
				eventTitleField.setBackground(Color.YELLOW);
				infoLabel.setText(errorLengthText);
				goodData = false;
			}
			if (eventDescriptionArea.getText().length() == 0) {
				eventDescriptionArea.setBackground(Color.YELLOW);
				infoLabel.setText(errorLengthText);
				goodData = false;
			}
		}
		
		if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE || actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
			if (updateDescriptionArea.getText().length() == 0) {
				updateDescriptionArea.setBackground(Color.YELLOW);
				infoLabel.setText(errorLengthText);
				goodData = false;
			}
		}		
		
		return goodData;
	}
	
	/**
	 * Cierra la pantalla de creación / edición de incidencias y actualizaciones y devuelve la vista a la pantalla
	 * de gestión de incidencias
	 */
	void backToEventDataScreen() {
		EventEditUI.this.setVisible(false);
		eDataUI.getScrollPane().getViewport().remove(EventEditUI.this);
		eDataUI.getScrollPane().setViewportView(eDataUI);
		eDataUI.setVisible(true);
	}
	
	/**
	 * COMPROBACIÓN AL HACER INTRO
	 * Listener que comprueba que el texto de eventDateField, eventTimeField, updateDateField y updateTimeField
	 * son correctos para validar una fecha o una hora según sea el origen del texto a comprobar. Si el origen es
	 * eventDateField o eventTimeField y se puede validar la fecha o la hora, se replica el texto de dichos
	 * componentes en updateDateField o updateTimeField. En caso contrario se retorna a la fecha inicial que
	 * tuviesen los textfields
	 */
	private class EventDateTimeIntroListener implements ActionListener {
		String oldText; //Recoge fecha u hora actuales
		String oldEventDateText; //Recoge la fecha de creación de la incidencia
		String oldEventTimeText; //Recoge la hora de creación de la incidencia
		String oldUpdateDateText; //Recoge la fecha de creación de la actualización
		String oldUpdateTimeText; //Recoge la hora de creación de la actualización
		String pattern; //Patrón de validación de fechas

//		boolean secondTest = false; //Control de tercera validación de fechas en NEW_UPDATE y EDIT_UPDATE
		
		public EventDateTimeIntroListener (String oldText, String pattern) {
			this.oldText = oldText;
			this.pattern = pattern;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
//			String newText = ((JTextField) e.getSource()).getText();
//			//Fecha de incidencia
//			if (e.getSource() == eventDateField) {
//				//Si es una fecha válida
//				if (dateIsValid(newText, pattern)) {
//					//Comprobación de fecha al editar una incidencia
//					if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {
//						stringToParse = buildStringTimestamp(eventDateField.getText(), eventTimeField.getText());
//						//Comprobamos que la fecha introducida no sea posterior a la de las actualizaciones de la incidencia
//						if (isDateTimeAfterEventUpdatesDateTime(stringToParse)) {
//							eventDateField.setText(oldEventDateText);
//							ToolBox.showDialog(
//									"<html><body><div align='center'>La fecha de una incidencia no puede ser posterior<br>"
//									+ "a la fecha de sus actualizaciones</div></body></html>",
//									EventEditUI.this, DIALOG_INFO);
//						}
//					}
//					updateDateField.setText(eventDateField.getText());
////					oldText = newText; //Opción de recuperar el último texto válido introducido, no el primer texto que apareció en el textfield
//				} else {
//					if (actionSelector == EVENTEDIT_ACTION_NEW_EVENT) {
//						eventDateField.setText(oldText);
//					} else if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {	
//						eventDateField.setText(oldEventDateText);
//					}
//					updateDateField.setText(eventDateField.getText());
//					ToolBox.showDialog(
//							"<html><body><div align='center'>Fecha incorrecta</div></body></html>",
//							EventEditUI.this, DIALOG_INFO);
//				}
//			}
//			//Hora de incidencia
//			if (e.getSource() == eventTimeField) {
//				//Si es una fecha válida
//				if (timeIsValid(newText, pattern)) {
//					//Comprobación de fecha al editar una incidencia
//					if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {
//						stringToParse = buildStringTimestamp(eventDateField.getText(), eventTimeField.getText());
//						//Comprobamos que la fecha introducida no sea posterior a la de las actualizaciones de la incidencia
//						if (isDateTimeAfterEventUpdatesDateTime(stringToParse)) {
//							eventTimeField.setText(oldEventTimeText);
//							ToolBox.showDialog(
//									"<html><body><div align='center'>La fecha de una incidencia no puede ser posterior<br>"
//									+ "a la fecha de sus actualizaciones</div></body></html>",
//									EventEditUI.this, DIALOG_INFO);
//						}
//					}
//					updateTimeField.setText(eventTimeField.getText());
////					oldText = newText; //Opción de recuperar el último texto válido introducido, no el primer texto que apareció en el textfield
//				} else {
//					if (actionSelector == EVENTEDIT_ACTION_NEW_EVENT) {
//						eventTimeField.setText(oldText);
//					} else if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {	
//						eventTimeField.setText(oldEventTimeText);
//					}
//					updateTimeField.setText(eventTimeField.getText());
//					ToolBox.showDialog(
//							"<html><body><div align='center'>Fecha incorrecta</div></body></html>",
//							EventEditUI.this, DIALOG_INFO);
//				}
//			}
//			//Fecha de actualización
//			if (e.getSource() == updateDateField) {
////				oldUpdateText = updateDateField.getText();
//				//Si es una fecha válida
//				if (dateIsValid(newText, pattern)) {
//					//Comprobación de fecha de actualización al crear o editar una actualización
//					if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE || actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
//						stringToParse = buildStringTimestamp(updateDateField.getText(), updateTimeField.getText());
//						//Comprobamos que la fecha introducida no sea anterior a la creación de la incidencia
//						if (isDateTimeBeforeEventDateTime(stringToParse)) {
////							updateDateField.setText(oldText);
//							if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE) {
//								updateDateField.setText(oldText);								
//							}
//							if (actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
////								oldText = updateDateField.getText();
//								updateDateField.setText(oldUpdateDateText);
//							}
//							ToolBox.showDialog(
//									"<html><body><div align='center'>La fecha de una nueva actualización no pueden ser anteriores<br>"
//									+ "a la fecha de la creación de la incidencia</div></body></html>",
//									EventEditUI.this, DIALOG_INFO);
//						}
//					}
//				//Si no es una fecha válida 
//				} else {
////					updateDateField.setText(oldText);
//					if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE) {
//						updateDateField.setText(oldText);								
//					}
//					if (actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
////						oldText = updateDateField.getText();
//						updateDateField.setText(oldUpdateDateText);
//					}
//					ToolBox.showDialog(
//							"<html><body><div align='center'>Fecha incorrecta</div></body></html>",
//							EventEditUI.this, DIALOG_INFO);
//				}
//			}
//			//Hora de actualización
//			if (e.getSource() == updateTimeField) {
////				oldUpdateText = updateTimeField.getText();
//				//Si es una hora válida
//				if (timeIsValid(newText, pattern)) {
//					//Comprobación de hora de actualización al crear o editar una actualización
//					if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE || actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
//						stringToParse = buildStringTimestamp(updateDateField.getText(), updateTimeField.getText());
//						//Comprobamos que la fecha introducida no sea anterior a la creación de la incidencia
//						if (isDateTimeBeforeEventDateTime(stringToParse)) {
////							updateTimeField.setText(oldText);
//							if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE) {
//								updateTimeField.setText(oldText);								
//							}
//							if (actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
////								oldText = updateTimeField.getText();
//								updateTimeField.setText(oldUpdateTimeText);
//							}
//							ToolBox.showDialog(
//									"<html><body><div align='center'>La fecha de una nueva actualización no pueden ser anteriores<br>"
//									+ "a la fecha de la creación de la incidencia</div></body></html>",
//									EventEditUI.this, DIALOG_INFO);
//						} 
//					}
//				//Si no es una hora válida
//				} else {
////					updateTimeField.setText(oldText);
//					if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE) {
//						updateTimeField.setText(oldText);								
//					}
//					if (actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
////						oldText = updateTimeField.getText();
//						updateTimeField.setText(oldUpdateTimeText);
//					}
//					ToolBox.showDialog(
//							"<html><body><div align='center'>Hora incorrecta</div></body></html>",
//							EventEditUI.this, DIALOG_INFO);
//				}
//			}
//			//Pasamos el foco al siguiente componente
////			manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		    manager.focusNextComponent();
		}

		public void setOldUpdateDateText(String text) {
			oldUpdateDateText = text;
		}

		public void setOldUpdateTimeText(String text) {
			oldUpdateTimeText = text;;
		}

		public void setOldEventDateText(String oldEventDateText) {
			this.oldEventDateText = oldEventDateText;
		}

		public void setOldEventTimeText(String oldEventTimeText) {
			this.oldEventTimeText = oldEventTimeText;
		}

	}
	
	/**
	 * COMPROBACIÓN AL PERDER EL FOCO
	 * Listener que comprueba que el texto de eventDateField, eventTimeField, updateDateField y updateTimeField
	 * son correctos para validar una fecha o una hora según sea el origen del texto a comprobar. Si el origen es
	 * eventDateField o eventTimeField y se puede validar la fecha o la hora, se replica el texto de dichos
	 * componentes en updateDateField o updateTimeField. En caso contrario se retorna a la fecha inicial que
	 * tuviesen los textfields.
	 */
	private class EventDateTimeFocusListener extends FocusAdapter {
		String oldText; //Recoge fecha u hora actuales
		String oldEventDateText; //Recoge la fecha de creación de la incidencia
		String oldEventTimeText; //Recoge la hora de creación de la incidencia
		String oldUpdateDateText; //Recoge la fecha de creación de la actualización
		String oldUpdateTimeText; //Recoge la hora de creación de la actualización
		String pattern; //Patrón de validación de fechas
		boolean secondTest = false; //Control de tercera validación de fechas en NEW_UPDATE y EDIT_UPDATE
		
		public EventDateTimeFocusListener (String oldText, String pattern) {
			this.oldText = oldText;
			this.pattern = pattern;
		}
		
		@Override
		public void focusGained(FocusEvent e) {
			// TODO Auto-generated method stub
			//Debug
			System.out.println("We have the focus!");
		}
		
		@Override
		public void focusLost(FocusEvent e) {
			//Debug
//			System.out.println("We lost the focus!");
			FocusEvent.Cause cause = e.getCause();
			System.out.println("We lost the focus! Is temporary? " + e.isTemporary() + ". La causa es: " + FocusEvent.Cause.valueOf(cause.toString()));
			
			String newText = ((JTextField) e.getSource()).getText();
			//Fecha de incidencia
			if (e.getSource() == eventDateField) {
				//Si es una fecha válida
				if (dateIsValid(newText, pattern)) {
					//Comprobación de fecha al editar una incidencia
					if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {
						stringToParse = buildStringTimestamp(eventDateField.getText(), eventTimeField.getText());
						//Comprobamos que la fecha introducida no sea posterior a la de las actualizaciones de la incidencia
						if (isDateTimeAfterEventUpdatesDateTime(stringToParse)) {
							eventDateField.setText(oldEventDateText);
							ToolBox.showDialog(
									"<html><body><div align='center'>La fecha de una incidencia no puede ser posterior<br>"
									+ "a la fecha de sus actualizaciones</div></body></html>",
									EventEditUI.this, DIALOG_INFO);
						}
					}
					updateDateField.setText(eventDateField.getText());
//					oldText = newText; //Opción de recuperar el último texto válido introducido, no el primer texto que apareció en el textfield
				} else {
					if (actionSelector == EVENTEDIT_ACTION_NEW_EVENT) {
						eventDateField.setText(oldText);
					} else if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {	
						eventDateField.setText(oldEventDateText);
					}
					updateDateField.setText(eventDateField.getText());
					ToolBox.showDialog(
							"<html><body><div align='center'>Fecha incorrecta</div></body></html>",
							EventEditUI.this, DIALOG_INFO);
				}
			}
			//Hora de incidencia
			if (e.getSource() == eventTimeField) {
				//Si es una fecha válida
				if (timeIsValid(newText, pattern)) {
					//Comprobación de fecha al editar una incidencia
					if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {
						stringToParse = buildStringTimestamp(eventDateField.getText(), eventTimeField.getText());
						//Comprobamos que la fecha introducida no sea posterior a la de las actualizaciones de la incidencia
						if (isDateTimeAfterEventUpdatesDateTime(stringToParse)) {
							eventTimeField.setText(oldEventTimeText);
							ToolBox.showDialog(
									"<html><body><div align='center'>La fecha de una incidencia no puede ser posterior<br>"
									+ "a la fecha de sus actualizaciones</div></body></html>",
									EventEditUI.this, DIALOG_INFO);
						}
					}
					updateTimeField.setText(eventTimeField.getText());
//					oldText = newText; //Opción de recuperar el último texto válido introducido, no el primer texto que apareció en el textfield
				} else {
					if (actionSelector == EVENTEDIT_ACTION_NEW_EVENT) {
						eventTimeField.setText(oldText);
					} else if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {	
						eventTimeField.setText(oldEventTimeText);
					}
					updateTimeField.setText(eventTimeField.getText());
					ToolBox.showDialog(
							"<html><body><div align='center'>Fecha incorrecta</div></body></html>",
							EventEditUI.this, DIALOG_INFO);
				}
			}
			//Fecha de actualización
			if (e.getSource() == updateDateField) {
//				oldUpdateText = updateDateField.getText();
				//Si es una fecha válida
				if (dateIsValid(newText, pattern)) {
					//Comprobación de fecha de actualización al crear o editar una actualización
					if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE || actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
						stringToParse = buildStringTimestamp(updateDateField.getText(), updateTimeField.getText());
						//Comprobamos que la fecha introducida no sea anterior a la creación de la incidencia
						if (isDateTimeBeforeEventDateTime(stringToParse)) {
//							updateDateField.setText(oldText);
							if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE) {
								updateDateField.setText(oldText);								
							}
							if (actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
//								oldText = updateDateField.getText();
								updateDateField.setText(oldUpdateDateText);
							}
							ToolBox.showDialog(
									"<html><body><div align='center'>La fecha de una nueva actualización no pueden ser anteriores<br>"
									+ "a la fecha de la creación de la incidencia</div></body></html>",
									EventEditUI.this, DIALOG_INFO);
						} else {
							secondTest = true;
						}
						//Avisamos si la fecha de la actualización editada es anterior a las actualizaciones registradas (excluyendo la primera)
						if (secondTest) {
							stringToParse = buildStringTimestamp(updateDateField.getText(), updateTimeField.getText());
							
							//Debug
							System.out.println(stringToParse);
							
							if (eventSelected.getUpdates().size() > 2) {
								int selectedRowIndex = 0;
								if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE) {
									selectedRowIndex = eDataUI.getUpdatesTable().getRowCount();
								}
								if (actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
									selectedRowIndex = eDataUI.getUpdatesTable().getSelectedRow();
								}
								//Comprobamos si la fecha de la actualización seleccionada es anterior a la actualización previa
								if (isDateTimebeforePreviousUpdatesDateTime(stringToParse, selectedRowIndex)) {
									if ((!e.isTemporary() && cause.toString().equals("TRAVERSAL_FORWARD"))) {
										int optionSelected = ToolBox.showDialog(
												"<html><body><div align='center'>La fecha de la actualización es anterior<br>"
														+ "a la de las actualizaciones previas<br>¿Desea continuar?</div></body></html>",
												EventEditUI.this, DIALOG_YES_NO);
										if (optionSelected != JOptionPane.YES_OPTION) {
											updateDateField.setText(oldUpdateDateText);
										}
									}
								}
							}
							secondTest = false;
						}
					}
				//Si no es una fecha válida 
				} else {
					if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE) {
						updateDateField.setText(oldText);								
					}
					if (actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
						updateDateField.setText(oldUpdateDateText);
					}
					ToolBox.showDialog(
							"<html><body><div align='center'>Fecha incorrecta</div></body></html>",
							EventEditUI.this, DIALOG_INFO);
				}
			}
			//Hora de actualización
			if (e.getSource() == updateTimeField) {
//				oldUpdateText = updateTimeField.getText();
				//Si es una hora válida
				if (timeIsValid(newText, pattern)) {
					//Comprobación de hora de actualización al crear o editar una actualización
					if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE || actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
						stringToParse = buildStringTimestamp(updateDateField.getText(), updateTimeField.getText());
						//Comprobamos que la fecha introducida no sea anterior a la creación de la incidencia
						if (isDateTimeBeforeEventDateTime(stringToParse)) {
//							updateTimeField.setText(oldText);
							if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE) {
								updateTimeField.setText(oldText);								
							}
							if (actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
//								oldText = updateTimeField.getText();
								updateTimeField.setText(oldUpdateTimeText);
							}
							ToolBox.showDialog(
									"<html><body><div align='center'>La fecha de una nueva actualización no pueden ser anteriores<br>"
									+ "a la fecha de la creación de la incidencia</div></body></html>",
									EventEditUI.this, DIALOG_INFO);
						} else {
							secondTest = true;
						}
						//Avisamos si la fecha de la actualización editada es anterior a las actualizaciones registradas (excluyendo la primera)
						if (secondTest) {
							stringToParse = buildStringTimestamp(updateDateField.getText(), updateTimeField.getText());
							
							//Debug
							System.out.println(stringToParse);
							
							if (eventSelected.getUpdates().size() > 2) {
								int selectedRowIndex = 0;
								if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE) {
									selectedRowIndex = eDataUI.getUpdatesTable().getRowCount();
								}
								if (actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
									selectedRowIndex = eDataUI.getUpdatesTable().getSelectedRow();
								}
								//Comprobamos si la fecha de la actualización seleccionada es anterior a la actualización previa
								if (isDateTimebeforePreviousUpdatesDateTime(stringToParse, selectedRowIndex)) {
									if (!e.isTemporary() || !cause.toString().equals("TRAVERSAL_FORWARD")) {
										int optionSelected = ToolBox.showDialog(
												"<html><body><div align='center'>La fecha de la actualización es anterior<br>"
														+ "a la de las actualizaciones previas<br>¿Desea continuar?</div></body></html>",
												EventEditUI.this, DIALOG_YES_NO);
										if (optionSelected != JOptionPane.YES_OPTION) {
											updateTimeField.setText(oldUpdateTimeText);
										} 
									}
								}
							}
							secondTest = false;
						}
					}
				//Si no es una hora válida
				} else {
					if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE) {
						updateTimeField.setText(oldText);								
					}
					if (actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
						updateTimeField.setText(oldUpdateTimeText);
					}
					ToolBox.showDialog(
							"<html><body><div align='center'>Hora incorrecta</div></body></html>",
							EventEditUI.this, DIALOG_INFO);
				}
			}
			//Pasamos el foco al siguiente componente
			KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		    manager.focusNextComponent();
		}
		
		public void setOldUpdateDateText(String text) {
			oldUpdateDateText = text;
		}

		public void setOldUpdateTimeText(String text) {
			oldUpdateTimeText = text;;
		}

		public void setOldEventDateText(String oldEventDateText) {
			this.oldEventDateText = oldEventDateText;
		}

		public void setOldEventTimeText(String oldEventTimeText) {
			this.oldEventTimeText = oldEventTimeText;
		}
	}
	
	private class OKAction extends AbstractAction {
		public OKAction() {
			putValue(NAME, "Aceptar");
			putValue(SHORT_DESCRIPTION, "Save new data or data edit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			//Se recupera el fondo de los campos para que una anterior validación errónea de los mismos
			//no los deje amarillos permanentemente
			for (Component comp : errorList) {
				if (actionSelector == EVENTEDIT_ACTION_NEW_EVENT) {
					if (comp == areaLabel || comp == eventTypeLabel) {
						comp.setBackground(new JPanel().getBackground());
					} else {
						comp.setBackground(Color.WHITE);
					} 
				}
				if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {
					if (comp != areaLabel && comp != eventTypeLabel && comp != updateDescriptionArea) {
						comp.setBackground(Color.WHITE);
					}
				}
				if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE || actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
					if (comp == updateDescriptionArea) {
						comp.setBackground(Color.WHITE);
					}
				}
			}
			
			//Si los datos del formulario son correctos
			if (testData()) {

				//infoLabel.setText("TODO CORRECTO PARA ACEPTAR");
				System.out.println("TODO CORRECTO PARA ACEPTAR EventEditUI");
				
				//Nueva incidencia
				if (actionSelector == EVENTEDIT_ACTION_NEW_EVENT) {
					
					//Creamos nuevo Event a partir de los datos del formulario
					Event newEvent = new Event();
					newEvent.setbUnit(session.getbUnit());
					newEvent.setArea(new Area().getAreaByName(session.getbUnit(), areaComboBox.getSelectedItem().toString()));
					//Debug
//					System.out.println(areaComboBox.getSelectedItem().toString());
//					newEvent.setEventType((String) eventTypeComboBox.getSelectedItem());
					newEvent.setEventType(eventTypeComboBox.getSelectedItem().toString());
					newEvent.setTitulo(eventTitleField.getText());
					newEvent.setDescripcion(eventDescriptionArea.getText());
					newEvent.setEventState(eventStateComboBox.getSelectedItem().toString());
					
					//Intentamos grabar la nueva incidencia en la base de datos, retornando un objeto con idénticos datos que incluye también
					//el id que le ha asignado dicha base de datos
					Event storedEvent = new Event().addNewEvent(session.getConnection(), newEvent);
					//Si la incidencia se almacena correctamente en la base de datos
					if (storedEvent != null) {
						tNow = ToolBox.getTimestampNow();
						//Registramos fecha y hora de la actualización de los datos de la tabla event
						PersistenceManager.registerTableModification(eDataUI.getInfoLabel(), "", session.getConnection(), tNow, Event.TABLE_NAME);
//						PersistenceManager.registerTableModification(infoLabel, "NUEVA INCIDENCIA REGISTRADA EN " + session.getbUnit().getNombre(),
//								session.getConnection(), tNow, Event.TABLE_NAME);
						
						//Creamos nuevo EventUpdate a partir de los datos del formulario y del nuevo Event
						EventUpdate newEventUpdate = new EventUpdate();
						newEventUpdate.setEvent(storedEvent);
						//Construir fech/hora a partir de los campos del formulario correspondientes
						stringToParse = buildStringTimestamp(eventDateField.getText(), eventTimeField.getText());
						newEventUpdate.setFechaHora(stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN));
						newEventUpdate.setAutor(updateAuthorField.getText());
						newEventUpdate.setUser(new User().getUserByAlias(session.getCompany().getAllCompanyUsers(),	userField.getText()));
						newEventUpdate.setDescripcion(updateDescriptionArea.getText());
						
						//Intentamos grabar la actualización inicial de la incidencia en la base de datos, retornando un objeto con idénticos
						//datos que incluye también el id que le ha asignado dicha base de datos
						EventUpdate storedEventUpdate = new EventUpdate().addNewEventUpdate(session.getConnection(), newEventUpdate);
						//Si la actualización se almacena correctamente en la base de datos
						if (storedEventUpdate != null) {
							//Registramos fecha y hora de la actualización de los datos de la tabla event_update
							PersistenceManager.registerTableModification(eDataUI.getInfoLabel(), "NUEVA INCIDENCIA REGISTRADA EN " + session.getbUnit().getNombre()
									+ ": ",	session.getConnection(), tNow, EventUpdate.TABLE_NAME);
							//Almacenamos la actualización inicial de la nueva incidencia
							storedEvent.getUpdates().add(storedEventUpdate);
							//Almacenamos la nueva incidencia en la unidad de negocio de la sesión
							session.getbUnit().getEvents().add(storedEvent);
							
							//CÓDIGO DE ACTUALIZACIÓN DE EVENTDATAUI AL RETORNAR A SU PANTALLA
							//Ejecutamos de nuevo el filtro seleccionado para actualizar la tabla de incidencias
							eDataUI.getFilterSelected().doClick();
//							//Si el filtro seleccionado no es allEvents
//							if (eDataUI.getFilterSelected() != eDataUI.getAllEvents()) {
//								//Si la incidencia creada queda fuera de la tabla a causa del filtro seleccionado, mostramos un aviso
//								//Buscamos la incidencia creada en la lista de incidencias filtrada
//								if (!eDataUI.getCurrentEventList().contains(storedEvent)) {
//									eDataUI.getInfoLabel().setText(eDataUI.getInfoLabel().getText()
//											+ ". LA INCIDENCIA CREADA NO APARECE EN LA TABLA DEBIDO AL FILTRO SELECCIONADO");
//								}
//							}
							//Deseleccionamos incidencia y actualización seleccionados si los hubiera
							eDataUI.setEventSelected(null);
							eDataUI.setUpdateSelected(null);
//							//Tras renovar la tabla de actualizaciones, solo el botón de nueva actualización queda habilitado
//							eDataUI.buttonSwitcher(eDataUI.getUpdateButtonSet(), eDataUI.getNewEnabled());
							//Volvemos a la pantalla de gestión de incidencias
							backToEventDataScreen();
						} else {
							eDataUI.getInfoLabel().setText("ERROR DE CREACIÓN DE UNA NUEVA ACTUALIZACIÓN DE INCIDENCIA EN LA BASE DE DATOS");
						}
					} else {
						eDataUI.getInfoLabel().setText("ERROR DE CREACIÓN DE UNA NUEVA INCIDENCIA EN LA BASE DE DATOS");
					}
				}
				
				//Edición de incidencia **********************************************************************************************
				if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {
					
					//Debug
					System.out.println("Dentro de EVENTEDIT_ACTION_EDIT_EVENT");
					
					eventSelected = eDataUI.getEventSelected();
//					//Debug
//					int index = session.getbUnit().getEvents().indexOf(eventSelected);
//					System.out.println("eventSelected null: " + eventSelected == null);
//					System.out.println("Buscamos el índice de eventSelected antes de grabar a la dB");
//					if (index != -1) {
//						System.out.println("indice encontrado: " + index);
//					} else {
//						System.out.println("indice no encontrado: " + index);
//					}
					
					//Objeto que recoge los datos actualizados
					Event updatedEvent = new Event();
					updatedEvent.setId(eventSelected.getId());
					updatedEvent.setbUnit(eventSelected.getbUnit());
					//Construir fech/hora a partir de los campos del formulario correspondientes para modificar la actualización inicial
					//de la incidencia seleccionada
					String stringToParse = buildStringTimestamp(eventDateField.getText(), eventTimeField.getText());
					eventSelected.getUpdates().get(0).setFechaHora(stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN));
					
//					//Debug
//					System.out.println("La actualización inicial es " + eventSelected.getUpdates().get(0).getId() + " - "
//							+ eventSelected.getUpdates().get(0).getFechaHora() + " - " + eventSelected.getUpdates().get(0).getDescripcion());
					
					
					
					updatedEvent.setArea(new Area().getAreaByName(session.getbUnit(), areaComboBox.getSelectedItem().toString()));
					updatedEvent.setEventType(eventTypeComboBox.getSelectedItem().toString());
					updatedEvent.setTitulo(eventTitleField.getText());
					updatedEvent.setDescripcion(eventDescriptionArea.getText());
					updatedEvent.setEventState(eventStateComboBox.getSelectedItem().toString());
					//Copiamos la lista de actualizaciones de la incidencia seleccionada al objeto actualizado
					updatedEvent.setUpdates(eventSelected.getUpdates());
					
					//Intentamos actualizar la incidencia en la base de datos
					if (new Event().updateEventToDB(session.getConnection(), updatedEvent)) {
						tNow = ToolBox.getTimestampNow();
						//Registramos fecha y hora de la actualización de los datos de la tabla event
						PersistenceManager.registerTableModification(eDataUI.getInfoLabel(), "",
								session.getConnection(), tNow, Event.TABLE_NAME);
						//Intentamos actualizar la actualización inicial de la incidencia en la base de datos
						if (new EventUpdate().updateEventUpdateToDB(session.getConnection(), updatedEvent.getUpdates().get(0))) {
							//Registramos fecha y hora de la actualización de los datos de la tabla event_update
							PersistenceManager.registerTableModification(eDataUI.getInfoLabel(), "INCIDENCIA ACTUALIZADA: ",
									session.getConnection(), tNow, EventUpdate.TABLE_NAME);
							
							//Actualizamos la incidencia seleccionada
							
							//Debug
							System.out.println("Eventos en bUnit? " + session.getbUnit().getEvents().size());
							
//							session.getbUnit().getEvents().remove(eventSelected);
							
							Iterator<Event> iterator = session.getbUnit().getEvents().iterator();
							while(iterator.hasNext()) {
								Event ev = (Event) iterator.next();
								if (ev.getId() == updatedEvent.getId()) {
									iterator.remove();
									System.out.println("eventSelected eliminado");
									break;
								}
							}
							
							//Debug
							System.out.println("Eventos en bUnit? " + session.getbUnit().getEvents().size());
							
							session.getbUnit().getEvents().add(updatedEvent);
							
							//Debug
							System.out.println("updatedEvent añadido. Eventos en bUnit? " + session.getbUnit().getEvents().size());
//							eventSelected = updatedEvent;
							
//							eventSelected.setArea(updatedEvent.getArea());
//							eventSelected.setEventType(updatedEvent.getEventType());
//							eventSelected.setTitulo(updatedEvent.getTitulo());
//							eventSelected.setDescripcion(updatedEvent.getDescripcion());
//							eventSelected.setEventState(updatedEvent.getEventState());
							

//							//Debug
//							int index2 = session.getbUnit().getEvents().indexOf(eventSelected);
//							if (index2 != -1) {
//								System.out.println("indice encontrado: " + index2);
//							} else {
//								System.out.println("indice no encontrado: " + index2);
//							}
							
							
//							VERIFICAR QUE EL BORRADO Y LA REINSERCIÓN DE LA INCIDENCIA NO ROMPEN NADA
//							//Borramos la incidencia seleccionada de la lista de incidencias de la bUnit de la sesión, y le añadimos
//							//la incidencia actualizada
//							session.getbUnit().getEvents().remove(eventSelected);
//							session.getbUnit().getEvents().add(updatedEvent);
//							eventSelected = updatedEvent;

							//CÓDIGO DE ACTUALIZACIÓN DE EVENTDATAUI AL RETORNAR A SU PANTALLA
							
							//Ejecutamos de nuevo el filtro seleccionado para actualizar la tabla de incidencias
							eDataUI.getFilterSelected().doClick();
							
//							//Encontrar la fila que ocupa la actualilzación editada , y seleccionarla en la tabla
//							for (int i = 0; i < eDataUI.getEventsTable().getRowCount(); i++) {
//								
//								//Debug
//								System.out.println("El número de filas es: " + eDataUI.getEventsTable().getRowCount());
//								System.out.println("Buscando en fila: " + i);
//								
//								int eventIdAtSelectedRow = (int) eDataUI.getEventsTable().getModel().getValueAt(i, 0);
//								
//								//Debug
//								System.out.println("El id encontrado es: " + (int) eDataUI.getEventsTable().getModel().getValueAt(i, 0));
//								
//								if (eventIdAtSelectedRow == eventSelected.getId()) {
//									eDataUI.getEventsTable().setRowSelectionInterval(i, i);
////									eventSelected = new Event().getEventById(session.getbUnit(), eventIdAtSelectedRow);
//									
//									//Debug
//									System.out.println("Id de eventSelected" + eventSelected.getId());
//									System.out.println("Id de rowId: " + eventIdAtSelectedRow);
//									System.out.println("Fechahora de 1a actualización de eventselected" + eventSelected.getUpdates().get(0).getFechaHora());
//									
//									eDataUI.getEventsTable().repaint();
//									System.out.println("Fechahora de 1a actualización de eventselected después de repintar la tabla de incidencias"
//									+ eventSelected.getUpdates().get(0).getFechaHora());
//									System.out.println(eventSelected.getUpdates().get(0).getFechaHora());
//									//eDataUI EventTableSelectionListener se encarga de gestionar el estado adecuado de los botones
//								}
//							}
							
//							//Si el filtro seleccionado no es allEvents
//							if (eDataUI.getFilterSelected() != eDataUI.getAllEvents()) {
//								//Buscamos la incidencia seleccionada en la lista de incidencias filtrada
//								//Si la incidencia actualizada queda fuera de la tabla a causa del filtro seleccionado, mostramos un aviso
//								if (!eDataUI.getCurrentEventList().contains(eDataUI.getEventSelected())) {
//									eDataUI.getInfoLabel().setText(eDataUI.getInfoLabel().getText()
//											+ ". LA INCIDENCIA EDITADA NO APARECE EN LA TABLA DEBIDO AL FILTRO SELECCIONADO");
//									//Deseleccionamos incidencia y actualización seleccionados si los hubiera
//									eDataUI.setEventSelected(null);
//									eDataUI.setUpdateSelected(null);
//									
//									//Debug
//									System.out.println("setEventSelected y setUpdateSelected valen null");
//									
////									//Vaciamos la tabla de actualizaciones. --YA LO HACE EL FILTRO--
////									eDataUI.updateUpdatesTable(new ArrayList<EventUpdate>(),EventDataUI.getUpdatesTableHeader());		
//									//Solo el botón de nueva incidencia queda habilitado --YA LO HACE EL TABLE SELECTION LISTENER--
////									eDataUI.buttonSwitcher(EventDataUI.getEventButtonSet(), EventDataUI.getNewEnabled());
////									eDataUI.buttonSwitcher(EventDataUI.getUpdateButtonSet(), EventDataUI.getAllDisabled());
//									
//								//Si la incidencia actualizada no queda fuera de la tabla a causa del filtro seleccionado
//								} else {
//									//Encontrar la fila que ocupa la actualilzación editada , y seleccionarla en la tabla
//									for (int i = 0; i < eDataUI.getEventsTable().getRowCount(); i++) {
//										
//										//Debug
//										System.out.println("El número de filas es: " + eDataUI.getEventsTable().getRowCount());
//										System.out.println("Buscando en fila: " + i);
//										
//										int rowId = (int) eDataUI.getEventsTable().getModel().getValueAt(i, 0);
//										
//										//Debug
//										System.out.println("El id encontrado es: " + (int) eDataUI.getEventsTable().getModel().getValueAt(i, 0));
//										
//										if (rowId == eventSelected.getId()) {
//											eDataUI.getEventsTable().setRowSelectionInterval(i, i);
//											eventSelected = new Event().getEventById(session.getbUnit(), rowId);
//											
//											//Debug
//											System.out.println("Id de eventSelected" + eventSelected.getId());
//											System.out.println("Id de rowId: " + rowId);
//											System.out.println("Fechahora de 1a actualización de eventselected" + eventSelected.getUpdates().get(0).getFechaHora());
//											
//											eDataUI.getEventsTable().repaint();
//											System.out.println("Fechahora de 1a actualización de eventselected después de repintar la tabla de incidencias"
//											+ eventSelected.getUpdates().get(0).getFechaHora());
//											System.out.println(eventSelected.getUpdates().get(0).getFechaHora());
//											//eDataUI EventTableSelectionListener se encarga de gestionar el estado adecuado de los botones
//										}
//									}
//									
//								}
//							} 
							//Deseleccionamos incidencia y actualización seleccionados si los hubiera
							eDataUI.setEventSelected(null);
							eDataUI.setUpdateSelected(null);
							//Volvemos a la pantalla de gestión de incidencias
							backToEventDataScreen();
						} else {
							eDataUI.getInfoLabel().setText("ERROR DE GRABACIÓN DE ACTUALIZACIÓN DE INCIDENCIA EN LA BASE DE DATOS");
						}
					} else {
						eDataUI.getInfoLabel().setText("ERROR DE GRABACIÓN DE INCIDENCIA EN LA BASE DE DATOS");
					}
				}
				
				//Nueva actualización
				if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE) {
					//Hora a la que se registra la actualización
					tNow = ToolBox.getTimestampNow();
					//Registramos el estado actual de la incidencia seleccionada
					String eventSelectedState = eventSelected.getEventState();
					//Si la nueva actualización ha cambiado el estado de la incidencia, hay que actualizar también la incidencia
					if (!eventStateComboBox.getSelectedItem().toString().equals(eventSelectedState)) {
						//Actualizamos el estado de la incidencia seleccionada
						eventSelected.setEventState(eventStateComboBox.getSelectedItem().toString());
						//Intentamos actualizar el estado de la la incidencia seleccionada en la base de datos
						if (new Event().updateEventStateOfEventToDB(session.getConnection(), eventSelected)) {
							//Registramos fecha y hora de la actualización de los datos de la tabla event
							PersistenceManager.registerTableModification(eDataUI.getInfoLabel(), "",
									session.getConnection(), tNow, Event.TABLE_NAME);
						} else {
							eDataUI.getInfoLabel().setText("ERROR DE ACTUALIZACIÓN DEL ESTADO DE LA INCIDENCIA EN LA BASE DE DATOS");
							eventSelected.setEventState(eventSelectedState);
						}
					} 		

					//Si la nueva actualización no cambia el estado de la incidencia, solo añadimos la nueva actualización
					//Creamos nuevo EventUpdate a partir de los datos del formulario y de la incidencia seleccionada
					EventUpdate newEventUpdate = new EventUpdate();
					newEventUpdate.setEvent(eventSelected);
					//Construir fech/hora a partir de los campos del formulario correspondientes
					stringToParse = buildStringTimestamp(updateDateField.getText(), updateTimeField.getText());
					newEventUpdate.setFechaHora(stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN));
					newEventUpdate.setAutor(updateAuthorField.getText());
					newEventUpdate.setUser(new User().getUserByAlias(session.getCompany().getAllCompanyUsers(),	userField.getText()));
					newEventUpdate.setDescripcion(updateDescriptionArea.getText());
					
					//Intentamos grabar la nueva actualización en la base de datos, retornando un objeto con idénticos
					//datos que incluye también el id que le ha asignado dicha base de datos
					EventUpdate storedEventUpdate = new EventUpdate().addNewEventUpdate(session.getConnection(), newEventUpdate);
					//Si la actualización se almacena correctamente en la base de datos
					if (storedEventUpdate != null) {
						//Registramos fecha y hora de la actualización de los datos de la tabla event_update
						PersistenceManager.registerTableModification(eDataUI.getInfoLabel(), "NUEVA ACTUALIZACIÓN DE INCIDENCIA REGISTRADA: ",
								session.getConnection(), tNow, EventUpdate.TABLE_NAME);
						//Añadimos la actualización a la incidencia seleccionada
						eventSelected.getUpdates().add(storedEventUpdate);
						
						//CÓDIGO DE ACTUALIZACIÓN DE EVENTDATAUI AL RETORNAR A SU PANTALLA
						//Registramos la fila seleccionada en la tabla de incidencias
						int rowId = eDataUI.getEventsTable().getSelectedRow();
						//Ejecutamos de nuevo el filtro seleccionado para actualizar la tabla de incidencias
						eDataUI.getFilterSelected().doClick(); //eventSelected no cambia
						//Volvemos a seleccionar la fila de la incidencia seleccionada
						eDataUI.getEventsTable().setRowSelectionInterval(rowId, rowId);
						
						//Renovamos la tabla de actualizaciones
						eDataUI.updateUpdatesTable(eDataUI.sortEventUpdatesByDate(eventSelected.getUpdates()), EventDataUI.getUpdatesTableHeader());
						//Reseteamos la actualización seleccionada
						eDataUI.setUpdateSelected(null);
						//Solo el botón nueva actualización queda activado
						eDataUI.buttonSwitcher(EventDataUI.getUpdateButtonSet(), EventDataUI.getNewEnabled());					
						//Volvemos a la pantalla de gestión de incidencias
						backToEventDataScreen();
					} else {
						eDataUI.getInfoLabel().setText("ERROR DE GRABACIÓN DE ACTUALIZACIÓN DE INCIDENCIA EN LA BASE DE DATOS");
					}
				}
				
				//Edición de actualización
				if (actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
					
					//Debug
					System.out.println("Dentro de EVENTEDIT_ACTION_EDIT_UPDATE");
					
					updateSelected = eDataUI.getUpdateSelected();
					
					//Hora a la que se registra la actualización
					tNow = ToolBox.getTimestampNow();
					//Registramos el estado actual de la incidencia seleccionada
					String eventSelectedState = eventSelected.getEventState();
					//Si la nueva actualización ha cambiado el estado de la incidencia, hay que actualizar también la incidencia
					if (!eventStateComboBox.getSelectedItem().toString().equals(eventSelectedState)) {
						//Actualizamos el estado de la incidencia seleccionada
						eventSelected.setEventState(eventStateComboBox.getSelectedItem().toString());
						//Intentamos actualizar el estado de la la incidencia seleccionada en la base de datos
						if (new Event().updateEventStateOfEventToDB(session.getConnection(), eventSelected)) {
							//Registramos fecha y hora de la actualización de los datos de la tabla event
							PersistenceManager.registerTableModification(eDataUI.getInfoLabel(), "",
									session.getConnection(), tNow, Event.TABLE_NAME);
						} else {
							eDataUI.getInfoLabel().setText("ERROR DE ACTUALIZACIÓN DEL ESTADO DE LA INCIDENCIA EN LA BASE DE DATOS");
							eventSelected.setEventState(eventSelectedState);
						}
					}
					//Objeto que recoge los datos actualizados
					EventUpdate updatedEventUpdate = new EventUpdate();
					updatedEventUpdate.setId(updateSelected.getId());
					updatedEventUpdate.setEvent(eventSelected);
					//Construir fech/hora a partir de los campos del formulario correspondientes
					stringToParse = buildStringTimestamp(updateDateField.getText(), updateTimeField.getText());
					updatedEventUpdate.setFechaHora(stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN));
					updatedEventUpdate.setAutor(updateAuthorField.getText());
					updatedEventUpdate.setUser(new User().getUserByAlias(session.getCompany().getAllCompanyUsers(),	userField.getText()));
					updatedEventUpdate.setDescripcion(updateDescriptionArea.getText());
					
					//Intentamos actualizar la actualización de incidencia en la base de datos
					if (new EventUpdate().updateEventUpdateToDB(session.getConnection(), updatedEventUpdate)) {
						//Registramos fecha y hora de la actualización de los datos de la tabla event_update
						PersistenceManager.registerTableModification(eDataUI.getInfoLabel(), "INCIDENCIA ACTUALIZADA: ",
								session.getConnection(), tNow, EventUpdate.TABLE_NAME);
						
						
						Iterator<EventUpdate> eUiterator = eventSelected.getUpdates().iterator();
						int index = 0;
						while(eUiterator.hasNext()) {
							EventUpdate up = (EventUpdate) eUiterator.next();
							if (up.getId() == updatedEventUpdate.getId()) {
								eUiterator.remove();
								System.out.println("eventUpdate eliminado en índice " + index);
								break;
							}
							index ++;
						}
						
//						session.getbUnit().getEvents().add(updatedEvent); // add update at index
						
						eventSelected.getUpdates().add(index, updatedEventUpdate);
						
//						Iterator<Event> eIterator = session.getbUnit().getEvents().iterator();
//						while(eIterator.hasNext()) {
//							Event ev = (Event) eIterator.next();
//							if (ev.getId() == eventSelected.getId()) {
//								eIterator.remove();
//								System.out.println("eventSelected eliminado");
//								break;
//							}
//						}
//						
//						session.getbUnit().getEvents().add(eventSelected);
						
//						//Debug
//						System.out.println("eventUpdate eliminado");
						
						//Actualizamos la actualización seleccionada en la lista de actualizaciones de la incidencia seleccionada
//						int index = eventSelected.getUpdates().indexOf(updateSelected);	
//						eventSelected.getUpdates().remove(index);
//						eventSelected.getUpdates().add(index, updatedEventUpdate);
//						eventSelected.getUpdates().remove(updateSelected);
//						eventSelected.getUpdates().add(updatedEventUpdate);
//						updateSelected = updatedEventUpdate;
						
						
//						updateSelected.setFechaHora(updatedEventUpdate.getFechaHora());
//						updateSelected.setAutor(updatedEventUpdate.getAutor());
//						updateSelected.setDescripcion(updatedEventUpdate.getDescripcion());
						
						
						//Debug
						System.out.println("El id de la actualización actualizada es: " + updatedEventUpdate.getId());
						
						//CÓDIGO DE ACTUALIZACIÓN DE EVENTDATAUI AL RETORNAR A SU PANTALLA
						//Registramos la fila seleccionada en la tabla de incidencias
						int row = eDataUI.getEventsTable().getSelectedRow();
						//Ejecutamos de nuevo el filtro seleccionado para actualizar la tabla de incidencias
						eDataUI.getFilterSelected().doClick(); //eventSelected no cambia
						//Volvemos a seleccionar la fila de la incidencia seleccionada
						eDataUI.getEventsTable().setRowSelectionInterval(row, row);
						
						//Renovamos la tabla de actualizaciones
						eDataUI.updateUpdatesTable(eDataUI.sortEventUpdatesByDate(eventSelected.getUpdates()), EventDataUI.getUpdatesTableHeader());
//						//Mantenemos la incidencia seleccionada y registramos la actualización editada
//						eDataUI.setUpdateSelected(updateSelected);
//						//Encontrar la fila que ocupa la actualilzación editada , y seleccionarla en la tabla
//						for (int i = 0; i < eDataUI.getUpdatesTable().getRowCount(); i++) {
//							
//							//Debug
//							System.out.println("El número de filas es: " + eDataUI.getUpdatesTable().getRowCount());
//							System.out.println("Buscando en fila: " + i);
//							
//							rowId = (int) eDataUI.getUpdatesTable().getModel().getValueAt(i, 0);
//							
//							//Debug
//							System.out.println("El id encontrado es: " + (int) eDataUI.getUpdatesTable().getModel().getValueAt(i, 0));
//							
//							if (rowId == updateSelected.getId()) {
//								eDataUI.getUpdatesTable().setRowSelectionInterval(i, i);
//							}
//						}
						//Deseleccionamos la actualización seleccionada
						eDataUI.setUpdateSelected(null);
						
						//Volvemos a la pantalla de gestión de incidencias
						backToEventDataScreen();
					} else {
						eDataUI.getInfoLabel().setText("ERROR DE GRABACIÓN DE ACTUALIZACIÓN DE INCIDENCIA EN LA BASE DE DATOS");
					}
				}
			}
			eDataUI.getInfoLabel().setText("");
		}
		
	}
	
	/**
	 * Acción del botón cancelar. Se descarta cualquier introducción / edición de datos y se retorna a la
	 * pantalla anterior
	 */
	private class CancelAction extends AbstractAction {
		public CancelAction() {
			putValue(NAME, "Cancelar");
			putValue(SHORT_DESCRIPTION, "Cancel data edit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			eDataUI.getInfoLabel().setText("");
			backToEventDataScreen();
			actionSelector = EVENTEDIT_ACTION_UNDEFINED;
		}
		
	}

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

	public Event getEventSelected() {
		return eventSelected;
	}

	public void setEventSelected(Event eventSelected) {
		this.eventSelected = eventSelected;
	}

	public EventUpdate getUpdateSelected() {
		return updateSelected;
	}

	public void setUpdateSelected(EventUpdate updateSelected) {
		this.updateSelected = updateSelected;
	}

}
