package main.java.gui;

import java.awt.Color;
import java.awt.Component;
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
	private static final String DATE_PATTERN = "dd-MM-yyyy";
	private static final String VALIDATION_DATE_PATTERN = "dd-MM-uuuu";
	private static final String TIME_PATTERN = "HH:mm";
	private static final String TIMESTAMP_GENERATOR_DATE_TIME_PATTERN = "uuuu-MM-dd HH:mm";
	
	//Se asignan a la variable actionSelector para determinar la acción a ejecutar
	private static final int EVENTEDIT_ACTION_UNDEFINED = 0; //Default
	private static final int EVENTEDIT_ACTION_NEW_EVENT = 1;
	private static final int EVENTEDIT_ACTION_EDIT_EVENT = 2;
	private static final int EVENTEDIT_ACTION_NEW_UPDATE = 3;
	private static final int EVENTEDIT_ACTION_EDIT_UPDATE = 4;
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
	private Timestamp tNow = ToolBox.getTimestampNow();
	
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
//	private Event eventSelected;
//	private EventUpdate updateSelected;
	
	//Recoge el texto que será convertido a Timestamp
	private String stringToParse;
	//Habilita o deshabilita las acciones Aceptar y Cancelar en función de la validez
	//de las fechas del formulario
	private boolean dateTimeOk = true;
	
	private EventDataUI eDataUI;
	
	
	public EventEditUI(CurrentSession session, int actionSelector, EventDataUI eDataUI) {
		this.session = session;
		this.actionSelector = actionSelector;
		this.eDataUI = eDataUI;
		setLayout(null);
		
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
		eventDescriptionArea.setBorder(eventTitleField.getBorder());
		errorList.add(eventDescriptionArea);
		add(eventDescriptionArea);
		
		updateDateField = new JTextField();
		updateDateField.setText(eventDateField.getText());
		updateDateField.setColumns(10);
		updateDateField.setBounds(260, 475, 70, 25);
		add(updateDateField);
		
		updateTimeField = new JTextField();
		updateTimeField.setText(eventTimeField.getText());
		updateTimeField.setColumns(10);
		updateTimeField.setBounds(260, 525, 40, 25);
		add(updateTimeField);
		
		//Listeners para los campos de fecha y hora		
		eventDateIntroListener = new EventDateTimeIntroListener();
		eventTimeIntroListener = new EventDateTimeIntroListener();
		updateDateIntroListener = new EventDateTimeIntroListener();
		updateTimeIntroListener = new EventDateTimeIntroListener();
		eventDateFocusListener = new EventDateTimeFocusListener(eventDateField.getText(), VALIDATION_DATE_PATTERN);
		eventTimeFocusListener = new EventDateTimeFocusListener(eventTimeField.getText(), TIME_PATTERN);
		updateDateFocusListener = new EventDateTimeFocusListener(updateDateField.getText(), VALIDATION_DATE_PATTERN);
		updateTimeFocusListener = new EventDateTimeFocusListener(updateTimeField.getText(), TIME_PATTERN);
				
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
		areaComboBox = new JComboBox();
		areaComboBox.setBounds(550, 225, 250, 25);
		areaComboBox.setEditable(false);
		ToolBox.setBlackForeground(areaComboBox);
		areaComboBox.setBackground(Color.WHITE);
		add(areaComboBox);
		
		eventTypeComboList = TypesStatesContainer.getEvType().getEventTypesArray();	
		eventTypeComboBox = new JComboBox();
		eventTypeComboBox.setBounds(550, 275, 250, 25);
		eventTypeComboBox.setEditable(false);
		ToolBox.setBlackForeground(eventTypeComboBox);
		eventTypeComboBox.setBackground(Color.WHITE);
		add(eventTypeComboBox);
		
		eventStateComboList = TypesStatesContainer.getEvState().getEventStatesArray();
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

		//CANDIDATOS A DELETE
//		eventSelected = eDataUI.getEventSelected();
//		updateSelected = eDataUI.getUpdateSelected();
		
		switch (setupOption) {
			//New event
			case EVENTEDIT_ACTION_NEW_EVENT:
				
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
				
				//Labels
				for (JLabel label: newEventList) {
					label.setVisible(true);
				}
				break;
				
			//Edit event
			case EVENTEDIT_ACTION_EDIT_EVENT:
				
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
				eventDateField.setText(ToolBox.formatTimestamp(eDataUI.getFirstUpdate().getFechaHora(),	DATE_PATTERN));
				eventTimeField.setText(ToolBox.formatTimestamp(eDataUI.getFirstUpdate().getFechaHora(),	TIME_PATTERN));
				index = getSelectedIndexFromArray(areaComboList, eDataUI.getEventSelected().getArea());
				fillAreaComboBox(index);
				index = getSelectedIndexFromArray(eventTypeComboList, TypesStatesContainer.getEvType());
				fillEventTypeComboBox(index);
				eventTitleField.setText(eDataUI.getEventSelected().getTitulo());
				eventDescriptionArea.setText(eDataUI.getEventSelected().getDescripcion());
				updateDateField.setText(eventDateField.getText());
				updateTimeField.setText(eventTimeField.getText());
				updateAuthorField.setText(eDataUI.getFirstUpdate().getAutor());
				userField.setText(eDataUI.getFirstUpdate().getUser().getUserAlias());
				updateDescriptionArea.setText(eDataUI.getFirstUpdate().getDescripcion());
				index = getSelectedIndexFromArray(eventStateComboList, TypesStatesContainer.getEvState());
				fillEventStateComboBox(index);
				
				//Labels
				for (JLabel label: editEventList) {
					label.setVisible(true);
				}
				//Actualizamos datos de los listeners
				eventDateFocusListener.setOldEventDateText(eventDateField.getText());
				eventDateFocusListener.setOldEventTimeText(eventTimeField.getText()); //CHECK
				eventTimeFocusListener.setOldEventDateText(eventDateField.getText()); //CHECK
				eventTimeFocusListener.setOldEventTimeText(eventTimeField.getText());
				
//				//Debug
//				System.out.println("EDIT EVENT");
//				System.out.println("Valor de eventDateFocusListener.getOldEventDateText(): " + eventDateFocusListener.oldEventDateText);
//				System.out.println("Valor de eventDateFocusListener.getOldEventTimeText(): " + eventDateFocusListener.oldEventTimeText);
//				System.out.println("Valor de eventTimeFocusListener.getOldEventDateText(): " + eventTimeFocusListener.oldEventDateText);
//				System.out.println("Valor de eventTimeFocusListener.getOldEventTimeText(): " + eventTimeFocusListener.oldEventTimeText);
				
				break;
				
			//New update
			case EVENTEDIT_ACTION_NEW_UPDATE:
				
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
				eventDateField.setText(ToolBox.formatTimestamp(eDataUI.getFirstUpdate().getFechaHora(),	DATE_PATTERN));
				eventTimeField.setText(ToolBox.formatTimestamp(eDataUI.getFirstUpdate().getFechaHora(),	TIME_PATTERN));
				index = getSelectedIndexFromArray(areaComboList, eDataUI.getEventSelected().getArea());
				fillAreaComboBox(index);
				index = getSelectedIndexFromArray(eventTypeComboList, TypesStatesContainer.getEvType());
				fillEventTypeComboBox(index);
				eventTitleField.setText(eDataUI.getEventSelected().getTitulo());
				eventDescriptionArea.setText(eDataUI.getEventSelected().getDescripcion());
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
				updateDateFocusListener.setOldUpdateDateText(updateDateField.getText());
				updateTimeFocusListener.setOldUpdateTimeText(updateTimeField.getText());
				break;
				
			//Edit update
			case EVENTEDIT_ACTION_EDIT_UPDATE:
				
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
				eventDateField.setText(ToolBox.formatTimestamp(eDataUI.getFirstUpdate().getFechaHora(),	DATE_PATTERN));
				eventTimeField.setText(ToolBox.formatTimestamp(eDataUI.getFirstUpdate().getFechaHora(),	TIME_PATTERN));
				index = getSelectedIndexFromArray(areaComboList, eDataUI.getEventSelected().getArea());
				fillAreaComboBox(index);
				index = getSelectedIndexFromArray(eventTypeComboList, TypesStatesContainer.getEvType());
				fillEventTypeComboBox(index);
				eventTitleField.setText(eDataUI.getEventSelected().getTitulo());
				eventDescriptionArea.setText(eDataUI.getEventSelected().getDescripcion());
				updateDateField.setText(ToolBox.formatTimestamp(eDataUI.getUpdateSelected().getFechaHora(), DATE_PATTERN));
				updateTimeField.setText(ToolBox.formatTimestamp(eDataUI.getUpdateSelected().getFechaHora(), TIME_PATTERN));
				updateAuthorField.setText(eDataUI.getUpdateSelected().getAutor());
				userField.setText(eDataUI.getUpdateSelected().getUser().getUserAlias());
				index = getSelectedIndexFromArray(eventStateComboList, TypesStatesContainer.getEvState());
				fillEventStateComboBox(index);
				updateDescriptionArea.setText(eDataUI.getUpdateSelected().getDescripcion());
				//Labels
				//Edición de la actualización inicial
				if (eDataUI.getUpdateSelected().equals(eDataUI.getFirstUpdate())) {
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
				if (array[i].equals(eDataUI.getEventSelected().getArea().getAreaNombre())) {
					return i;
				}
				continue;
			}
			//Si buscamos el índice de una lista de tipos de incidencia
			if (object.getClass() == EventType.class) {
				if (array[i].equals(eDataUI.getEventSelected().getEventType())) {
					return i;
				}
				continue;
			}			
			//Si buscamos el índice de una lista de estados de incidencia
			if (object.getClass() == EventState.class) {
				if (array[i].equals(eDataUI.getEventSelected().getEventState())) {
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
	public boolean dateIsValid(String date, String pattern) {
        boolean validDate = false;
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT));
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
	public boolean timeIsValid(String time, String pattern) {
        boolean validTime = false;
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT));
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
		if (dateTimeTimestamp.before(eDataUI.getFirstUpdate().getFechaHora())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Comprueba si la fecha/hora pasada por parámetro es posterior a la fecha/hora de
	 * la actualización siguiente a la actualización inicial (si existe)
	 * @param stringToParse texto que contiene la fecha y la hora a comprobar
	 * @return true si la fecha es posterior, false si es anterior
	 */
	private boolean isDateTimeAfterEventUpdatesDateTime(String stringToParse) {
		Timestamp dateTimeTimestamp = stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN);
		//Si la incidencia seleccionada tiene actualizaciones (aparte de la actualización inicial)
		if (eDataUI.getEventSelected().getUpdates().size() > 1) {
			//Cogemos como referencia el primer timestamp de la lista de actualizaciones que no sea el de la
			//actualización inicial
			Timestamp tempDateTime = eDataUI.getEventSelected().getUpdates().get(0).getId() == eDataUI.getFirstUpdate().getId() ?
					eDataUI.getEventSelected().getUpdates().get(1).getFechaHora() : eDataUI.getEventSelected().getUpdates().get(0).getFechaHora();
			for (EventUpdate update : eDataUI.getEventSelected().getUpdates()) {
				if (update.getId() == eDataUI.getFirstUpdate().getId()) {
					continue;
				} else {
					if (tempDateTime.after(update.getFechaHora())) {
						tempDateTime = update.getFechaHora();
					}
				}
			}
			//tempDateTime tiene el timestamp menor de todos los de la lista de actualilzaciones
			if (dateTimeTimestamp.after(tempDateTime)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Comprueba si la fecha de la actualización es anterior a la de anteriores actualizaciones
	 * @param stringToParse texto que contiene la fecha y la hora a comprobar
	 * @return true si la fecha de la actualización a comprobar es anterior a la fecha de la actualización previa,
	 * false en caso contrario
	 */
	private boolean isDateTimebeforePreviousUpdatesDateTime(String stringToParse) {
		Timestamp dateTimeTimestamp = stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN);
		//Cogemos como referencia el primer timestamp de la lista de actualizaciones que no sea el de la
		//actualización inicial
		Timestamp tempDateTime = eDataUI.getEventSelected().getUpdates().get(0).getId() == eDataUI.getFirstUpdate().getId() ?
				eDataUI.getEventSelected().getUpdates().get(1).getFechaHora() : eDataUI.getEventSelected().getUpdates().get(0).getFechaHora();
		for (EventUpdate update : eDataUI.getEventSelected().getUpdates()) {
			if (update.getId() == eDataUI.getFirstUpdate().getId()) {
				continue;
			} else {
				if (tempDateTime.before(update.getFechaHora())) {
					tempDateTime = update.getFechaHora();
				}
			}
		}
		//tempDateTime tiene el timestamp mayor de todos los de la lista de actualilzaciones
		if (dateTimeTimestamp.before(tempDateTime)) {
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
		try {
			eDataUI.getTimer().cancel();
			eDataUI.startAnewTimer(30000);
			
			//Debug
			System.out.println("Hemos matado al timer anterior y hemos iniciado uno nuevo");
			
		} catch (Exception e) {
			
			//Debug
			System.out.println("Hemos intentado matar un timer que ya estaba muerto... pero ahora inciamos otro igualmente");
			
			eDataUI.startAnewTimer(30000);
		}
		EventEditUI.this.setVisible(false);
		eDataUI.getScrollPane().getViewport().remove(EventEditUI.this);
		eDataUI.getScrollPane().setViewportView(eDataUI);
		eDataUI.setVisible(true);
		eDataUI.setPanelVisible(true);
		
	}
	
	/**
	 * Listener que pasa el foco del componente que se está editando al siguiente componente del formulario en caso de
	 * pulsar la tecla INTRO para acabar la edición. Delega en EventDateTimeFocusListener la comprobación de la corrección
	 * de los datos introducidos
	 */
	private class EventDateTimeIntroListener implements ActionListener {
		
		public EventDateTimeIntroListener () {

		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			//Pasamos el foco al siguiente componente
			KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		    manager.focusNextComponent();
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
		}
		
		@Override
		public void focusLost(FocusEvent e) {
			FocusEvent.Cause cause = e.getCause();
			
//			//Debug
//			System.out.println("We lost the focus! Is temporary? " + e.isTemporary() + ". La causa es: " + FocusEvent.Cause.valueOf(cause.toString()));
			
			String newText = ((JTextField) e.getSource()).getText();
			
			//FECHA DE INCIDENCIA
			if (e.getSource() == eventDateField) {
				//Si es una fecha válida
				if (dateIsValid(newText, pattern)) {
					//Comprobación de fecha al editar una incidencia
					if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {
						stringToParse = buildStringTimestamp(eventDateField.getText(), eventTimeField.getText());
						//Comprobamos que la fecha introducida no sea posterior a la de las actualizaciones de la incidencia
						if (isDateTimeAfterEventUpdatesDateTime(stringToParse)) {
							eventDateField.setText(oldEventDateText);
							eventTimeField.setText(oldEventTimeText); //CHECK
							dateTimeOk = false;
							ToolBox.showDialog(
									"<html><body><div align='center'>La fecha de una incidencia no puede ser posterior<br>"
									+ "a la fecha de sus actualizaciones</div></body></html>",
									EventEditUI.this, DIALOG_INFO);
							dateTimeOk = true;
						} else {
							dateTimeOk = true;
						}
					}
					updateDateField.setText(eventDateField.getText());
//					oldText = newText; //Opción de recuperar el último texto válido introducido, no el primer texto que apareció en el textfield
				
				//Si no es una fecha válida
				} else {
					if (actionSelector == EVENTEDIT_ACTION_NEW_EVENT) {
						eventDateField.setText(oldText);
					} else if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {	
						eventDateField.setText(oldEventDateText);
					}
					updateDateField.setText(eventDateField.getText());
					dateTimeOk = false;
					ToolBox.showDialog(
							"<html><body><div align='center'>Fecha incorrecta</div></body></html>",
							EventEditUI.this, DIALOG_INFO);
					dateTimeOk = true;
				}
			}
			
			//HORA DE INCIDENCIA
			if (e.getSource() == eventTimeField) {
				//Si es una fecha válida
				if (timeIsValid(newText, pattern)) {
					//Comprobación de fecha al editar una incidencia
					if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {
						stringToParse = buildStringTimestamp(eventDateField.getText(), eventTimeField.getText());
						//Comprobamos que la fecha introducida no sea posterior a la de las actualizaciones de la incidencia
						if (isDateTimeAfterEventUpdatesDateTime(stringToParse)) {
							eventTimeField.setText(oldEventTimeText);
							eventDateField.setText(oldEventDateText); //CHECK
							dateTimeOk = false;
							ToolBox.showDialog(
									"<html><body><div align='center'>La fecha de una incidencia no puede ser posterior<br>"
									+ "a la fecha de sus actualizaciones</div></body></html>",
									EventEditUI.this, DIALOG_INFO);
							dateTimeOk = true;
						} else {
							dateTimeOk = true;
						}
					}
					updateTimeField.setText(eventTimeField.getText());
//					oldText = newText; //Opción de recuperar el último texto válido introducido, no el primer texto que apareció en el textfield
				
				//Si no es una fecha válida
				} else {
					if (actionSelector == EVENTEDIT_ACTION_NEW_EVENT) {
						eventTimeField.setText(oldText);
					} else if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {	
						eventTimeField.setText(oldEventTimeText);
					}
					updateTimeField.setText(eventTimeField.getText());
					dateTimeOk = false;
					ToolBox.showDialog(
							"<html><body><div align='center'>Fecha incorrecta</div></body></html>",
							EventEditUI.this, DIALOG_INFO);
					dateTimeOk = true;
				}
			}
			
			//FECHA DE ACTUALIZACIÓN
			if (e.getSource() == updateDateField) {
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
							dateTimeOk = false;
							ToolBox.showDialog(
									"<html><body><div align='center'>La fecha de una nueva actualización no pueden ser anteriores<br>"
									+ "a la fecha de la creación de la incidencia</div></body></html>",
									EventEditUI.this, DIALOG_INFO);
							dateTimeOk = true;
						} else {
							secondTest = true;
						}
						//Avisamos si la fecha de la actualización editada es anterior a las actualizaciones registradas (excluyendo la primera)
						if (secondTest) {
							stringToParse = buildStringTimestamp(updateDateField.getText(), updateTimeField.getText());
							//Si hay más de dos actualizaciones
							if (eDataUI.getEventSelected().getUpdates().size() > 2) {
								//Comprobamos si la fecha de la actualización seleccionada es anterior a la actualización previa
								if (isDateTimebeforePreviousUpdatesDateTime(stringToParse)) {
									if ((!e.isTemporary() && cause.toString().equals("TRAVERSAL_FORWARD"))) {
										dateTimeOk = false;
										int optionSelected = ToolBox.showDialog(
												"<html><body><div align='center'>La fecha de la actualización es anterior<br>"
														+ "a la de las actualizaciones previas<br>¿Desea continuar?</div></body></html>",
												EventEditUI.this, DIALOG_YES_NO);
										dateTimeOk = true;
										if (optionSelected != JOptionPane.YES_OPTION) {
											updateDateField.setText(oldUpdateDateText);
										}
									}
								}
							}
							secondTest = false;
							dateTimeOk = true;
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
					dateTimeOk = false;
					ToolBox.showDialog(
							"<html><body><div align='center'>Fecha incorrecta</div></body></html>",
							EventEditUI.this, DIALOG_INFO);
					dateTimeOk = true;
				}
			}
			
			//HORA DE ACTUALIZACIÓN
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
							dateTimeOk = false;
							ToolBox.showDialog(
									"<html><body><div align='center'>La fecha de una nueva actualización no pueden ser anteriores<br>"
									+ "a la fecha de la creación de la incidencia</div></body></html>",
									EventEditUI.this, DIALOG_INFO);
							dateTimeOk = true;
						} else {
							secondTest = true;
						}
						//Avisamos si la fecha de la actualización editada es anterior a las actualizaciones registradas (excluyendo la primera)
						if (secondTest) {
							stringToParse = buildStringTimestamp(updateDateField.getText(), updateTimeField.getText());
//							
//							//Debug
//							System.out.println(stringToParse);
							
							if (eDataUI.getEventSelected().getUpdates().size() > 2) {
								//Comprobamos si la fecha de la actualización seleccionada es anterior a la actualización previa
								if (isDateTimebeforePreviousUpdatesDateTime(stringToParse)) {
									if (!e.isTemporary() || !cause.toString().equals("TRAVERSAL_FORWARD")) {
										dateTimeOk = false;
										int optionSelected = ToolBox.showDialog(
												"<html><body><div align='center'>La fecha de la actualización es anterior<br>"
														+ "a la de las actualizaciones previas<br>¿Desea continuar?</div></body></html>",
												EventEditUI.this, DIALOG_YES_NO);
										dateTimeOk = true;
										if (optionSelected != JOptionPane.YES_OPTION) {
											updateTimeField.setText(oldUpdateTimeText);
										} 
									}
								}
							}
							secondTest = false;
							dateTimeOk = true;
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
					dateTimeOk = false;
					ToolBox.showDialog(
							"<html><body><div align='center'>Hora incorrecta</div></body></html>",
							EventEditUI.this, DIALOG_INFO);
					dateTimeOk = true;
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
			if (dateTimeOk) {
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
					if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE
							|| actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {
						if (comp == updateDescriptionArea) {
							comp.setBackground(Color.WHITE);
						}
					}
				}
				//Si los datos del formulario son correctos
				if (testData()) {

					System.out.println("TODO CORRECTO PARA ACEPTAR EventEditUI");

					//Nueva incidencia **********************************************************************************************
					if (actionSelector == EVENTEDIT_ACTION_NEW_EVENT) {
						
						//Debug
						System.out.println("Dentro de EVENTEDIT_ACTION_NEW_EVENT");

						//Creamos nuevo Event a partir de los datos del formulario
						Event newEvent = new Event();
						newEvent.setbUnit(session.getbUnit());
						newEvent.setArea(new Area().getAreaByName(session.getbUnit(), areaComboBox.getSelectedItem().toString()));
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
							PersistenceManager.updateTimeStampToDB(session.getConnection(), Event.TABLE_NAME, tNow);
							
							//Debug
							System.out.println("Se ha grabado una nueva incidencia y ahora se grabará su actualización inicial");
							
							//Creamos nuevo EventUpdate a partir de los datos del formulario y del nuevo Event
							EventUpdate newEventUpdate = new EventUpdate();
							newEventUpdate.setEvent(storedEvent);
							//Construir fecha/hora a partir de los campos del formulario correspondientes
							stringToParse = buildStringTimestamp(eventDateField.getText(), eventTimeField.getText());
							newEventUpdate.setFechaHora(
									stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN));
							newEventUpdate.setAutor(updateAuthorField.getText());
							newEventUpdate.setUser(new User().getUserByAlias(session.getCompany().getAllCompanyUsers(),
									userField.getText()));
							newEventUpdate.setDescripcion(updateDescriptionArea.getText());

							//Intentamos grabar la actualización inicial de la incidencia en la base de datos, retornando un objeto con idénticos
							//datos que incluye también el id que le ha asignado dicha base de datos
							EventUpdate storedEventUpdate = new EventUpdate().addNewEventUpdate(session.getConnection(),
									newEventUpdate);
							//Si la actualización se almacena correctamente en la base de datos
							if (storedEventUpdate != null) {
								//Registramos fecha y hora de la actualización de los datos de la tabla event_update
								PersistenceManager.registerTableModification(eDataUI.getInfoLabel(),
										"NUEVA INCIDENCIA REGISTRADA EN " + session.getbUnit().getNombre() + ": ",
										session.getConnection(), tNow, EventUpdate.TABLE_NAME);
								
								//Debug
								System.out.println("Se ha grabado la actualización inicial de la nueva incidencia");
								
								//Almacenamos la actualización inicial de la nueva incidencia
								storedEvent.getUpdates().add(storedEventUpdate);
								//Almacenamos la nueva incidencia en la unidad de negocio de la sesión
								session.getbUnit().getEvents().add(storedEvent);

								//CÓDIGO DE ACTUALIZACIÓN DE EVENTDATAUI AL RETORNAR A SU PANTALLA
								//Ejecutamos de nuevo el filtro seleccionado para actualizar la tabla de incidencias
								eDataUI.getFilterSelected().doClick();
								
								//Esto ya no hace falta porque lo hace EventSelectionListener
//								Borramos la incidencia y la actualización seleccionados si los hubiera
//								eDataUI.setEventSelected(null);
//								eDataUI.setUpdateSelected(null);
										
								//Volvemos a la pantalla de gestión de incidencias
								backToEventDataScreen();
							// Si la actualización de la incidencia no se almacena en la base de datos
							} else {
								eDataUI.getInfoLabel().setText(
										"ERROR DE CREACIÓN DE UNA NUEVA ACTUALIZACIÓN DE INCIDENCIA EN LA BASE DE DATOS");
							}
						// Si la incidencia no se almacena en la base de datos
						} else {
							eDataUI.getInfoLabel()
									.setText("ERROR DE CREACIÓN DE UNA NUEVA INCIDENCIA EN LA BASE DE DATOS");
						}
					}

					//Edición de incidencia **********************************************************************************************
					if (actionSelector == EVENTEDIT_ACTION_EDIT_EVENT) {

						//Debug
						System.out.println("Dentro de EVENTEDIT_ACTION_EDIT_EVENT");
						
						//Actualizamos la incidencia seleccionada
						eDataUI.getEventSelected().setArea(new Area().getAreaByName(session.getbUnit(), areaComboBox.getSelectedItem().toString()));
						eDataUI.getEventSelected().setEventType(eventTypeComboBox.getSelectedItem().toString());
						eDataUI.getEventSelected().setTitulo(eventTitleField.getText());
						eDataUI.getEventSelected().setDescripcion(eventDescriptionArea.getText());
						eDataUI.getEventSelected().setEventState(eventStateComboBox.getSelectedItem().toString());
						//Intentamos actualizar la incidencia en la base de datos
						if (new Event().updateEventToDB(session.getConnection(), eDataUI.getEventSelected())) {
							tNow = ToolBox.getTimestampNow();
							//Registramos fecha y hora de la actualización de los datos de la tabla event
							PersistenceManager.updateTimeStampToDB(session.getConnection(), Event.TABLE_NAME, tNow);
						
							//Debug
							System.out.println("Se ha actualizado la incidencia seleccionada y ahora se actualizará su actualización inicial");
							
							//Construir fecha/hora a partir de los campos del formulario correspondientes para modificar la actualización inicial
							//de la incidencia seleccionada
							String stringToParse = buildStringTimestamp(eventDateField.getText(), eventTimeField.getText());
							eDataUI.getFirstUpdate().setFechaHora(stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN));
							//Intentamos actualizar la actualización inicial de la incidencia en la base de datos
							if (new EventUpdate().updateEventUpdateToDB(session.getConnection(), eDataUI.getFirstUpdate())) {
								//Registramos fecha y hora de la actualización de los datos de la tabla event_update
								PersistenceManager.registerTableModification(eDataUI.getInfoLabel(), "INCIDENCIA ACTUALIZADA: ",
										session.getConnection(), tNow, EventUpdate.TABLE_NAME);
								
								//Debug
								System.out.println("Se ha actualizado la actualización inicial de la incidencia seleccionada");
								
								//CÓDIGO DE ACTUALIZACIÓN DE EVENTDATAUI AL RETORNAR A SU PANTALLA
								//Registramos el id de la incidencia seleccionada
								int eventSelectedID = eDataUI.getEventSelected().getId();
								//Ejecutamos de nuevo el filtro seleccionado para actualizar la tabla de incidencias
								eDataUI.getFilterSelected().doClick(); 
								//Buscamos la fila de la tabla de incidencias que estaba seleccionada
								int row = -1;
								for (int i = 0; i < eDataUI.getEventsTable().getRowCount(); i++) {
									if (eventSelectedID == (int) eDataUI.getEventsTable().getModel().getValueAt(i, 0)) {
										row = i;
									}
								}
								//Podría ser que el filtro seleccionado deje fuera de la tabla de incidencias la incidencia editada
								//si le hemos cambiado la fecha y ésta queda fuera del rango del filtro
								if (row != -1) {
									//Volvemos a seleccionar la fila de la incidencia seleccionada
									//EventTableSelectionListener se encarga de reasignar eventSelected y firstUpdate, de renovar el estado
									//de los botones del panel de incidencias y de actualizar la tabla de actualizaciones de incidencias
									eDataUI.getEventsTable().setRowSelectionInterval(row, row);
									
									//Esto ya no hace falta porque lo hace EventUpdateSelectionListener
//									//Borramos la actualización seleccionada si la hubiera
//									eDataUI.setUpdateSelected(null); ////////////////CHECK THIS!!!
									
								} else {
									//Notificamos que la incidencia no aparece en la tabla
									eDataUI.getInfoLabel().setText(eDataUI.getInfoLabel().getText()
											+ ". LA INCIDENCIA EDITADA QUEDA FUERA DEL RANGO DEL FILTRO SELECCIONADO");
								}		
							// Si la actualización de la incidencia no se almacena en la base de datos
							} else {
									eDataUI.getInfoLabel().setText(
											"ERROR DE GRABACIÓN DE ACTUALIZACIÓN DE INCIDENCIA EN LA BASE DE DATOS");
							}
						// Si la incidencia no se almacena en la base de datos	
						} else {
								eDataUI.getInfoLabel().setText("ERROR DE GRABACIÓN DE INCIDENCIA EN LA BASE DE DATOS");
						}
						
						//Volvemos a la pantalla de gestión de incidencias
						backToEventDataScreen();

//						//Nueva incidencia actualizada
//						Event updatedEvent = new Event();
//						updatedEvent.setId(eDataUI.getEventSelected().getId());
//						updatedEvent.setbUnit(eDataUI.getEventSelected().getbUnit());
//						
//						//Construir fecha/hora a partir de los campos del formulario correspondientes para modificar la actualización inicial
//						//de la incidencia seleccionada
//						String stringToParse = buildStringTimestamp(eventDateField.getText(), eventTimeField.getText());
//						eDataUI.getFirstUpdate().setFechaHora(stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN));
////						updatedEvent.findFirstUpdate().setFechaHora(stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN));
//						updatedEvent.setArea(new Area().getAreaByName(session.getbUnit(), areaComboBox.getSelectedItem().toString()));
//						updatedEvent.setEventType(eventTypeComboBox.getSelectedItem().toString());
//						updatedEvent.setTitulo(eventTitleField.getText());
//						updatedEvent.setDescripcion(eventDescriptionArea.getText());
//						updatedEvent.setEventState(eventStateComboBox.getSelectedItem().toString());
//						//Copiamos la lista de actualizaciones de la incidencia seleccionada al objeto actualizado
//						updatedEvent.setUpdates(eDataUI.getEventSelected().getUpdates());
//
//						//Intentamos actualizar la incidencia en la base de datos
//						if (new Event().updateEventToDB(session.getConnection(), updatedEvent)) {
//							tNow = ToolBox.getTimestampNow();
//							
//							//Debug
//							System.out.println("EDIT_EVENT grabando cambios en la incidencia");
//							
//							//Registramos fecha y hora de la actualización de los datos de la tabla event
//							PersistenceManager.updateTimeStampToDB(session.getConnection(), Event.TABLE_NAME, tNow);
//
//							//Intentamos actualizar la actualización inicial de la incidencia en la base de datos
//							if (new EventUpdate().updateEventUpdateToDB(session.getConnection(), eDataUI.getFirstUpdate())) {
//								//Registramos fecha y hora de la actualización de los datos de la tabla event_update
//								PersistenceManager.registerTableModification(eDataUI.getInfoLabel(),
//										"INCIDENCIA ACTUALIZADA: ", session.getConnection(), tNow,
//										EventUpdate.TABLE_NAME);
//
//								//Actualizamos la incidencia seleccionada
//
//								//Debug
//								System.out.println("Eventos en bUnit? " + session.getbUnit().getEvents().size());
//
//								//							session.getbUnit().getEvents().remove(eventSelected);
//
//								Iterator<Event> iterator = session.getbUnit().getEvents().iterator();
//								while (iterator.hasNext()) {
//									Event ev = (Event) iterator.next();
//									if (ev.getId() == eDataUI.getEventSelected().getId()) {
//										iterator.remove();
//										System.out.println("eventSelected eliminado");
//										break;
//									}
//								}
//
//								//Debug
//								System.out.println("Eventos en bUnit después de eliminar eventSelected? " + session.getbUnit().getEvents().size());
//
//								session.getbUnit().getEvents().add(updatedEvent);
//
//								//Debug
//								System.out.println("updatedEvent añadido. Eventos en bUnit? "
//										+ session.getbUnit().getEvents().size());
//
//
//								//CÓDIGO DE ACTUALIZACIÓN DE EVENTDATAUI AL RETORNAR A SU PANTALLA
//								
//								//Buscamos el id de la fila seleccionada en la tabla de incidencias
//								int selectedEventId = (int) eDataUI.getEventsTable().getModel().getValueAt(eDataUI.getEventsRow(), 0);
//								//Ejecutamos de nuevo el filtro seleccionado para actualizar la tabla de incidencias
//								eDataUI.getFilterSelected().doClick(); 
//								//Buscamos la fila de la tabla de incidencias que estaba seleccionada
//								int row = 0;
//								for (int i = 0; i < eDataUI.getEventsTable().getRowCount(); i++) {
//									if (selectedEventId == (int) eDataUI.getEventsTable().getModel().getValueAt(i, 0)) {
//										row = i;
//									}
//								}
//								//Volvemos a seleccionar la fila de la incidencia seleccionada
//								//EventTableSelectionListener se encarga de reasignar eventSelected y firstUpdate, de renovar el estado
//								//de los botones del panel de incidencias y de actualizar la tabla de actualizaciones de incidencias
//								eDataUI.getEventsTable().setRowSelectionInterval(row, row);
//								//Borramos la actualización seleccionada si la hubiera
//								eDataUI.setUpdateSelected(null); ////////////////CHECK THIS!!!
//								//Volvemos a la pantalla de gestión de incidencias
//								backToEventDataScreen();
//							} else {
//								eDataUI.getInfoLabel().setText(
//										"ERROR DE GRABACIÓN DE ACTUALIZACIÓN DE INCIDENCIA EN LA BASE DE DATOS");
//							}
//						} else {
//							eDataUI.getInfoLabel().setText("ERROR DE GRABACIÓN DE INCIDENCIA EN LA BASE DE DATOS");
//						}
											
					}

					//Nueva actualización **********************************************************************************************
					if (actionSelector == EVENTEDIT_ACTION_NEW_UPDATE) {
						
						//Debug
						System.out.println("Dentro de EVENTEDIT_ACTION_NEW_UPDATE");
						
						//Registramos el estado de la incidencia en la incidencia seleccionada
						eDataUI.getEventSelected().setEventState(eventStateComboBox.getSelectedItem().toString());
						//Intentamos actualizar el estado de la incidencia seleccionada en la base de datos
						if (new Event().updateEventStateOfEventToDB(session.getConnection(), eDataUI.getEventSelected())) {
							tNow = ToolBox.getTimestampNow();
							//Registramos fecha y hora de la actualización de los datos de la tabla event
							PersistenceManager.updateTimeStampToDB(session.getConnection(), Event.TABLE_NAME, tNow);
							
							//Debug
							System.out.println("Se ha actualizado el estado de la incidencia seleccionada y ahora se le añadirá una actualización");
							
							//Creamos nuevo EventUpdate a partir de los datos del formulario y de la incidencia seleccionada
							EventUpdate newEventUpdate = new EventUpdate();
							newEventUpdate.setEvent(eDataUI.getEventSelected());
							//Construir fecha/hora a partir de los campos del formulario correspondientes
							stringToParse = buildStringTimestamp(updateDateField.getText(), updateTimeField.getText());
							newEventUpdate.setFechaHora(stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN));
							newEventUpdate.setAutor(updateAuthorField.getText());
							newEventUpdate.setUser(new User().getUserByAlias(session.getCompany().getAllCompanyUsers(),
									userField.getText()));
							newEventUpdate.setDescripcion(updateDescriptionArea.getText());
							//Intentamos grabar la nueva actualización en la base de datos, retornando un objeto con idénticos
							//datos que incluye también el id que le ha asignado dicha base de datos
							EventUpdate storedEventUpdate = new EventUpdate().addNewEventUpdate(session.getConnection(),
									newEventUpdate);
							//Si la actualización se almacena correctamente en la base de datos
							if (storedEventUpdate != null) {
								//Registramos fecha y hora de la actualización de los datos de la tabla event_update
								PersistenceManager.registerTableModification(eDataUI.getInfoLabel(),
										"NUEVA ACTUALIZACIÓN DE INCIDENCIA REGISTRADA: ", session.getConnection(), tNow,
										EventUpdate.TABLE_NAME);
								
								//Debug
								System.out.println("Se ha creado una nueva actualización de la incidencia seleccionada");
								
								//Añadimos la actualización a la incidencia seleccionada
								eDataUI.getEventSelected().getUpdates().add(storedEventUpdate);
								
								//CÓDIGO DE ACTUALIZACIÓN DE EVENTDATAUI AL RETORNAR A SU PANTALLA
								//Registramos el id de la incidencia seleccionada
								int eventSelectedID = eDataUI.getEventSelected().getId();
								//Ejecutamos de nuevo el filtro seleccionado para actualizar la tabla de incidencias
								eDataUI.getFilterSelected().doClick(); 
								//Buscamos la fila de la tabla de incidencias que estaba seleccionada
								int row = -1;
								for (int i = 0; i < eDataUI.getEventsTable().getRowCount(); i++) {
									if (eventSelectedID == (int) eDataUI.getEventsTable().getModel().getValueAt(i, 0)) {
										row = i;
									}
								}
								//Volvemos a seleccionar la fila de la incidencia seleccionada
								//EventTableSelectionListener se encarga de reasignar eventSelected y firstUpdate, de renovar el estado
								//de los botones del panel de incidencias y de actualizar la tabla de actualizaciones de incidencias
								eDataUI.getEventsTable().setRowSelectionInterval(row, row);
								
								//Esto ya no hace falta porque lo hace EventUpdateSelectionListener
//								//Borramos la actualización seleccionada si la hubiera
//								eDataUI.setUpdateSelected(null); ////////////////CHECK THIS!!!
								
							//Si la actualización de incidencia no se graba en la base de datos	
							} else {
								eDataUI.getInfoLabel()
										.setText("ERROR DE GRABACIÓN DE ACTUALIZACIÓN DE INCIDENCIA EN LA BASE DE DATOS");
							}
	
						//Si el estado de la la incidencia seleccionada no se actualiza en la base de datos		
						} else {
							eDataUI.getInfoLabel().setText(
									"ERROR DE ACTUALIZACIÓN DE LA INCIDENCIA EN LA BASE DE DATOS");
						}
						
						//Volvemos a la pantalla de gestión de incidencias
						backToEventDataScreen();

						
//						//Nueva incidencia actualizada
//						Event updatedEvent = new Event();
//						updatedEvent.setId(eDataUI.getEventSelected().getId());
//						updatedEvent.setbUnit(session.getbUnit());
//						updatedEvent.setArea(eDataUI.getEventSelected().getArea());
////						updatedEvent.setEventType(eventTypeComboBox.getSelectedItem().toString());
//						updatedEvent.setEventType(eDataUI.getEventSelected().getEventType());
//						updatedEvent.setTitulo(eventTitleField.getText());
//						updatedEvent.setDescripcion(eventDescriptionArea.getText());
//						updatedEvent.setEventState(eventStateComboBox.getSelectedItem().toString());
//				
//						//Intentamos actualizar el estado de la la incidencia seleccionada en la base de datos
//						if (new Event().updateEventToDB(session.getConnection(), updatedEvent)) {
//							tNow = ToolBox.getTimestampNow();
//							//Registramos fecha y hora de la actualización de los datos de la tabla event
//							PersistenceManager.updateTimeStampToDB(session.getConnection(), Event.TABLE_NAME, tNow);
//							
//							//Debug
//							System.out.println("NEW_UPDATE grabando incidencia editada");
//
//							
//							//Creamos nuevo EventUpdate a partir de los datos del formulario y de la incidencia seleccionada
//							EventUpdate newEventUpdate = new EventUpdate();
//							newEventUpdate.setEvent(updatedEvent);
//							//Construir fecha/hora a partir de los campos del formulario correspondientes
//							stringToParse = buildStringTimestamp(updateDateField.getText(), updateTimeField.getText());
//							newEventUpdate.setFechaHora(stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN));
//							newEventUpdate.setAutor(updateAuthorField.getText());
//							newEventUpdate.setUser(new User().getUserByAlias(session.getCompany().getAllCompanyUsers(),
//									userField.getText()));
//							newEventUpdate.setDescripcion(updateDescriptionArea.getText());
//							
//							//Intentamos grabar la nueva actualización en la base de datos, retornando un objeto con idénticos
//							//datos que incluye también el id que le ha asignado dicha base de datos
//							EventUpdate storedEventUpdate = new EventUpdate().addNewEventUpdate(session.getConnection(),
//									newEventUpdate);
//							//Si la actualización se almacena correctamente en la base de datos
//							if (storedEventUpdate != null) {
//								//Registramos fecha y hora de la actualización de los datos de la tabla event_update
//								PersistenceManager.registerTableModification(eDataUI.getInfoLabel(),
//										"NUEVA ACTUALIZACIÓN DE INCIDENCIA REGISTRADA: ", session.getConnection(), tNow,
//										EventUpdate.TABLE_NAME);
//								
//								//Añadimos la actualización a la incidencia seleccionada
//								eDataUI.getEventSelected().getUpdates().add(storedEventUpdate);
//								//Añadimos las actualizaciones de la incidencia seleccionada al nuevo evento actualizado
//								updatedEvent.setUpdates(eDataUI.getEventSelected().getUpdates());
//								
//								//Eliminamos la incidencia selecionada de la lista de incidencias del centro de trabajo de la sesión
//								Iterator<Event> eIterator = session.getbUnit().getEvents().iterator();
//								while(eIterator.hasNext()) {
//									Event ev = (Event) eIterator.next();
//									if (ev.getId() == eDataUI.getEventSelected().getId()) {
//										eIterator.remove();
//										break;
//									}
//								}
//								
//								session.getbUnit().getEvents().remove(eDataUI.getEventSelected());
//								
//								//Añadimos la incidencia actualizada a la lista de incidencias del centro de trabajo de la sesión
//								session.getbUnit().getEvents().add(updatedEvent);
//								
//								//CÓDIGO DE ACTUALIZACIÓN DE EVENTDATAUI AL RETORNAR A SU PANTALLA
//								//Buscamos el id de la fila seleccionada en la tabla de incidencias
//								int selectedEventId = (int) eDataUI.getEventsTable().getModel().getValueAt(eDataUI.getEventsRow(), 0);
//								//Actualizamos la tabla de incidencias
//								eDataUI.getFilterSelected().doClick();
//								//Buscamos la fila de la tabla de incidencias que estaba seleccionada
//								int row = 0;
//								for (int i = 0; i < eDataUI.getEventsTable().getRowCount(); i++) {
//									if (selectedEventId == (int) eDataUI.getEventsTable().getModel().getValueAt(i, 0)) {
//										row = i;
//									}
//								}
//								//Volvemos a seleccionar la fila de la incidencia seleccionada
//								//EventTableSelectionListener se encarga de reasignar eventSelected y firstUpdate, de renovar el estado
//								//de los botones del panel de incidencias y de actualizar la tabla de actualizaciones de incidencias
//								eDataUI.getEventsTable().setRowSelectionInterval(row, row);
//								//Borramos la actualización seleccionada si la hubiera
//								eDataUI.setUpdateSelected(null); ////////////////CHECK THIS!!!
//								//Volvemos a la pantalla de gestión de incidencias
//								backToEventDataScreen();
//								
//							//Si la actualización de incidencia no se graba en la base de datos	
//							} else {
//								eDataUI.getInfoLabel()
//										.setText("ERROR DE GRABACIÓN DE ACTUALIZACIÓN DE INCIDENCIA EN LA BASE DE DATOS");
//							}
//							
//						//Si el estado de la la incidencia seleccionada no se actualiza en la base de datos		
//						} else {
//							eDataUI.getInfoLabel().setText(
//									"ERROR DE ACTUALIZACIÓN DE LA INCIDENCIA EN LA BASE DE DATOS");
//						}
						
					}

					//Edición de actualización **********************************************************************************************
					if (actionSelector == EVENTEDIT_ACTION_EDIT_UPDATE) {

						//Debug
						System.out.println("Dentro de EVENTEDIT_ACTION_EDIT_UPDATE");

						//Registramos el estado de la incidencia en la incidencia seleccionada
						eDataUI.getEventSelected().setEventState(eventStateComboBox.getSelectedItem().toString());
						//Intentamos actualizar el estado de la incidencia seleccionada en la base de datos
						if (new Event().updateEventStateOfEventToDB(session.getConnection(), eDataUI.getEventSelected())) {
							tNow = ToolBox.getTimestampNow();
							//Registramos fecha y hora de la actualización de los datos de la tabla event
							PersistenceManager.updateTimeStampToDB(session.getConnection(), Event.TABLE_NAME, tNow);
							
							//Debug
							System.out.println("Se ha actualizado el estado de la incidencia seleccionada y ahora se actualizará su actualización");
						
							//Actualizamos la actualización seleccionada
							stringToParse = buildStringTimestamp(updateDateField.getText(), updateTimeField.getText());
							eDataUI.getUpdateSelected().setFechaHora(stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN));
							eDataUI.getUpdateSelected().setAutor(updateAuthorField.getText());
							eDataUI.getUpdateSelected().setUser(new User().getUserByAlias(session.getCompany().getAllCompanyUsers(),
									userField.getText()));
							eDataUI.getUpdateSelected().setDescripcion(updateDescriptionArea.getText());
							//Intentamos actualizar la actualización de incidencia en la base de datos
							if (new EventUpdate().updateEventUpdateToDB(session.getConnection(), eDataUI.getUpdateSelected())) {
								//Registramos fecha y hora de la actualización de los datos de la tabla event_update
								PersistenceManager.registerTableModification(eDataUI.getInfoLabel(),
										"ACTUALIZACIÓN DE INCIDENCIA ACTUALIZADA: ", session.getConnection(), tNow, EventUpdate.TABLE_NAME);
						
								//Debug
								System.out.println("Se ha actualizado la actualización de la incidencia seleccionada");
								
								//CÓDIGO DE ACTUALIZACIÓN DE EVENTDATAUI AL RETORNAR A SU PANTALLA
								//Registramos el id de la incidencia seleccionada
								int eventSelectedID = eDataUI.getEventSelected().getId();
								//Registramos el id de la actualización seleccionada
								int updateSelectedID = eDataUI.getUpdateSelected().getId();
								//Actualizamos la tabla de incidencias
								eDataUI.getFilterSelected().doClick();
								//Buscamos la fila de la tabla de incidencias que estaba seleccionada
								int eventRow = -1;
								for (int i = 0; i < eDataUI.getEventsTable().getRowCount(); i++) {
									if (eventSelectedID == (int) eDataUI.getEventsTable().getModel().getValueAt(i, 0)) {
										eventRow = i;
									}
								}
								//Volvemos a seleccionar la fila de la incidencia seleccionada
								//EventTableSelectionListener se encarga de reasignar eventSelected y firstUpdate, de renovar el estado
								//de los botones del panel de incidencias y de actualizar la tabla de actualizaciones de incidencias
								eDataUI.getEventsTable().setRowSelectionInterval(eventRow, eventRow);
								//Buscamos la fila de la tabla de actualizaciones que estaba seleccionada
								int updateRow = - 1;
								for (int i = 0; i < eDataUI.getUpdatesTable().getRowCount(); i++) {
									if (updateSelectedID == (int) eDataUI.getUpdatesTable().getModel().getValueAt(i, 0)) {
										updateRow = i;
									}
								}
								//Volvemos a seleccionar la fila de la actualización seleccionada
								//EventUpdateTableSelectionListener se encarga de reasignar updateSelected y renovar el estado de los botones
								//del panel de actualizaciones
								eDataUI.getUpdatesTable().setRowSelectionInterval(updateRow, updateRow);

								//Si la actualización de incidencia no se actualiza en la base de datos
							} else {
								eDataUI.getInfoLabel()
								.setText("ERROR DE GRABACIÓN DE ACTUALIZACIÓN DE INCIDENCIA EN LA BASE DE DATOS");
							}
						
							//Si el estado de la la incidencia seleccionada no se actualiza en la base de datos		
						} else {
							eDataUI.getInfoLabel().setText(
									"ERROR DE ACTUALIZACIÓN DE LA INCIDENCIA EN LA BASE DE DATOS");
						}	
						
						//Volvemos a la pantalla de gestión de incidencias
						backToEventDataScreen();
						

//						//Hora a la que se registra la actualización
//						tNow = ToolBox.getTimestampNow();
//						
//						//Nueva incidencia actualizada
//						Event updatedEvent = new Event();
//						updatedEvent.setId(eDataUI.getEventSelected().getId());
//						updatedEvent.setbUnit(session.getbUnit());
//						updatedEvent.setArea(eDataUI.getEventSelected().getArea());
////						updatedEvent.setEventType(eventTypeComboBox.getSelectedItem().toString());
//						updatedEvent.setEventType(eDataUI.getEventSelected().getEventType());
//						updatedEvent.setTitulo(eventTitleField.getText());
//						updatedEvent.setDescripcion(eventDescriptionArea.getText());
//						updatedEvent.setEventState(eventStateComboBox.getSelectedItem().toString());
//				
//						//Intentamos actualizar el estado de la la incidencia seleccionada en la base de datos
//						if (new Event().updateEventToDB(session.getConnection(), updatedEvent)) {
//							
//							//Debug
//							System.out.println("EDIT_UPDATE grabando incidencia editada");
//
//							//Registramos fecha y hora de la actualización de los datos de la tabla event
//							PersistenceManager.updateTimeStampToDB(session.getConnection(), Event.TABLE_NAME, tNow);
//
//							//Objeto que recoge los datos actualizados
//							EventUpdate updatedEventUpdate = new EventUpdate();
//							updatedEventUpdate.setId(updatedEvent.getId());
//							updatedEventUpdate.setEvent(updatedEvent);
//							//Construir fech/hora a partir de los campos del formulario correspondientes
//							stringToParse = buildStringTimestamp(updateDateField.getText(), updateTimeField.getText());
//							updatedEventUpdate.setFechaHora(stringToTimestamp(stringToParse, TIMESTAMP_GENERATOR_DATE_TIME_PATTERN));
//							updatedEventUpdate.setAutor(updateAuthorField.getText());
//							updatedEventUpdate.setUser(new User().getUserByAlias(session.getCompany().getAllCompanyUsers(),
//									userField.getText()));
//							updatedEventUpdate.setDescripcion(updateDescriptionArea.getText());
//							
//							//Intentamos actualizar la actualización de incidencia en la base de datos
//							if (new EventUpdate().updateEventUpdateToDB(session.getConnection(), updatedEventUpdate)) {
//								//Registramos fecha y hora de la actualización de los datos de la tabla event_update
//								PersistenceManager.registerTableModification(eDataUI.getInfoLabel(),
//										"ACTUALIZACIÓN DE INCIDENCIA ACTUALIZADA: ", session.getConnection(), tNow, EventUpdate.TABLE_NAME);
//								
////								//Eliminamos la actualización selecionada de la lista de actualizaciones de la incidencia seleccionada
////								Iterator<EventUpdate> eUiterator = eDataUI.getEventSelected().getUpdates().iterator();
////								while(eUiterator.hasNext()) {
////									EventUpdate eup = (EventUpdate) eUiterator.next();
////									if (eup.getId() == eDataUI.getUpdateSelected().getId()) {
////										eUiterator.remove();
////										break;
////									}
////								}
//								
//								eDataUI.getEventSelected().getUpdates().remove(eDataUI.getUpdateSelected());
//
//								
//								//Añadimos la actualización actualizada a la lista de actualizaciones de la incidencia seleccionada
//								eDataUI.getEventSelected().getUpdates().add(updatedEventUpdate);
//								//Añadimos las actualizaciones de la incidencia seleccionada al nuevo evento actualizado
//								updatedEvent.setUpdates(eDataUI.getEventSelected().getUpdates());
//								
////								//Eliminamos la incidencia selecionada de la lista de incidencias del centro de trabajo de la sesión
////								Iterator<Event> eIterator = session.getbUnit().getEvents().iterator();
////								while(eIterator.hasNext()) {
////									Event ev = (Event) eIterator.next();
////									if (ev.getId() == eDataUI.getEventSelected().getId()) {
////										eIterator.remove();
////										break;
////									}
////								}
//								
//								session.getbUnit().getEvents().remove(eDataUI.getEventSelected());
//								
//								//Añadimos la incidencia actualizada a la lista de incidencias del centro de trabajo de la sesión
//								session.getbUnit().getEvents().add(updatedEvent);
//								
//								//CÓDIGO DE ACTUALIZACIÓN DE EVENTDATAUI AL RETORNAR A SU PANTALLA
//								
//								//Buscamos el id de la fila seleccionada en la tabla de incidencias
//								int selectedEventId = (int) eDataUI.getEventsTable().getModel().getValueAt(eDataUI.getEventsRow(), 0);
//								//Buscamos el id de la fila seleccionada en la tabla de actualizaciones
//								int selectedUpdateId = (int) eDataUI.getUpdatesTable().getModel().getValueAt(eDataUI.getUpdatesRow(), 0);
//								//Actualizamos la tabla de incidencias
//								eDataUI.getFilterSelected().doClick();
//								//Buscamos la fila de la tabla de incidencias que estaba seleccionada
//								int eventRow = 0;
//								for (int i = 0; i < eDataUI.getEventsTable().getRowCount(); i++) {
//									if (selectedEventId == (int) eDataUI.getEventsTable().getModel().getValueAt(i, 0)) {
//										eventRow = i;
//									}
//								}
//								//Volvemos a seleccionar la fila de la incidencia seleccionada
//								//EventTableSelectionListener se encarga de reasignar eventSelected y firstUpdate, de renovar el estado
//								//de los botones del panel de incidencias y de actualizar la tabla de actualizaciones de incidencias
//								eDataUI.getEventsTable().setRowSelectionInterval(eventRow, eventRow);
//								
//								
//								//Buscamos la fila de la tabla de actualizaciones que estaba seleccionada
//								int updateRow = 0;
//								for (int i = 0; i < eDataUI.getUpdatesTable().getRowCount(); i++) {
//									if (selectedUpdateId == (int) eDataUI.getUpdatesTable().getModel().getValueAt(i, 0)) {
//										updateRow = i;
//									}
//								}
//								//Volvemos a seleccionar la fila de la actualización seleccionada
//								//EventUpdateTableSelectionListener se encarga de reasignar updateSelected y renovar el estado de los botones
//								//del panel de actualizaciones
//								eDataUI.getUpdatesTable().setRowSelectionInterval(updateRow, updateRow);
//								
//								//Volvemos a la pantalla de gestión de incidencias
//								backToEventDataScreen();
//
//							//Si la actualización de incidencia no se actualiza en la base de datos
//							} else {
//								eDataUI.getInfoLabel()
//								.setText("ERROR DE GRABACIÓN DE ACTUALIZACIÓN DE INCIDENCIA EN LA BASE DE DATOS");
//							}
//
//						//Si el estado de la la incidencia seleccionada no se actualiza en la base de datos		
//						} else {
//							eDataUI.getInfoLabel().setText(
//									"ERROR DE ACTUALIZACIÓN DE LA INCIDENCIA EN LA BASE DE DATOS");
//						}

					}
//					//CÓDIGO DE ACTUALIZACIÓN DE EVENTDATAUI AL RETORNAR A SU PANTALLA
//					//Ejecutamos de nuevo el filtro seleccionado para actualizar la tabla de incidencias
//					eDataUI.getFilterSelected().doClick();
//					//Borramos la incidencia y la actualización seleccionados si los hubiera
//					eDataUI.setEventSelected(null);
//					eDataUI.setUpdateSelected(null);
//					//Volvemos a la pantalla de gestión de incidencias
//					backToEventDataScreen();
				}
//				if (!eDataUI.getInfoLabel().getText().equals("")) {
//					eDataUI.getInfoLabel().setText("");
//				}
			}
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
			if (dateTimeOk) {
				eDataUI.getInfoLabel().setText("");
				backToEventDataScreen();
				actionSelector = EVENTEDIT_ACTION_UNDEFINED;
			}
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

//	public Event getEventSelected() {
//		return eventSelected;
//	}
//
//	public void setEventSelected(Event eventSelected) {
//		this.eventSelected = eventSelected;
//	}

//	public EventUpdate getUpdateSelected() {
//		return updateSelected;
//	}
//
//	public void setUpdateSelected(EventUpdate updateSelected) {
//		this.updateSelected = updateSelected;
//	}

}
