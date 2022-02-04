package main.java.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import main.java.company.BusinessUnit;
import main.java.company.Company;
import main.java.event.Event;
import main.java.event.EventUpdate;
import main.java.persistence.PersistenceManager;
import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;
import main.java.types_states.TypesStatesContainer;

public class EventDataUI extends JPanel{
	
	//Encabezados de las tablas
	private static final String[] EVENTS_TABLE_HEADER = {"ID", "Fecha / Hora", "Area", "Tipo", "Título", "Descripción", "Estado"};
	private static final String[] UPDATES_TABLE_HEADER = {"ID", "Event_ID", "Fecha / Hora", "Descripción", "Autor", "Usuario"};
	
	//Formato de presentación de fecha/hora
	private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss";
	
	//Control de estado de botones
	private static final String EVENT_BUTTON_SET = "eventSet";
	private static final String UPDATE_BUTTON_SET = "updateSet";
	private static final String ALL_DISABLED = "000";
	private static final String NEW_ENABLED = "100";
	private static final String NEW_EDIT_ENABLED = "110";
	private static final String ALL_ENABLED = "111";
	
	//Se asignan a la variable actionSelector para determinar la acción a ejecutar
	private static final int EVENTDATA_ACTION_UNDEFINED = 0; //Default
	private static final int EVENTDATA_ACTION_DELETE_EVENT = 1;
	private static final int EVENTDATA_ACTION_DELETE_UPDATE = 2;
	//Tipo de cuadro de diálogo
	private static final String DIALOG_INFO = "info";
	private static final String DIALOG_YES_NO = "yes_no";
	//Registra la acción a realizar según el botón activado
	private int actionSelector = EVENTDATA_ACTION_UNDEFINED;
	
	private CurrentSession session;
	private Timestamp tNow = ToolBox.getTimestampNow();
	//Temporizador de comprobación de cambios en los datos de la sesión
	private Timer timer;
	//Registra si el panel está visible o no
	private boolean panelVisible;
	//Tamaño del monitor que ejecuta la aplicación
	private String screenSize = getScreenSize();
	
	private JPanel eventsContainer;
	private JPanel updatesContainer;
	private JPanel filtersContainer;
	private JTable eventsTable;
	private JTable updatesTable;
	private Event eventSelected;
	private EventUpdate updateSelected;
	private int eventsRow;
	private int updatesRow;
	
	private JTextField companyField;
	private JComboBox comboBox;
	private JCheckBox activeFilterCheckBox;
	private JButton newEventButton;
	private JButton editEventButton;
	private JButton deleteEventButton;
	private JButton newUpdateButton;
	private JButton editUpdateButton;
	private JButton deleteUpdateButton;
	
	private JLabel totalEvents;
	private JLabel eventsShown;
	private JLabel infoLabel;
	
	//Radio buttons
	private JRadioButton allEvents = new JRadioButton("Todos");
	private JRadioButton last25 = new JRadioButton("Últimos 25");
	private JRadioButton last50 = new JRadioButton("Últimos 50");
	private JRadioButton thisMonth = new JRadioButton("Presente mes");
	private JRadioButton last3Months = new JRadioButton("Último trimestre");
	private JRadioButton last6Months = new JRadioButton("Último semestre");
	private JRadioButton thisYear = new JRadioButton("Presente año");
	//private JradioButton previousYear = new JRadioButton("Año anterior");
	private JRadioButton filterSelected;
	
	//Lista de elementos que aparecen en comboBox
	private String[] comboList;
	//Lista de elementos que aparecen en la tabla de incidencias
	private List<Event> currentEventList;
	
	//Actualización inicial
	private EventUpdate firstUpdate;
	
	private final Action newEventAction = new NewEventAction();
	private final Action editEventAction = new EditEventAction();
	private final Action deleteEventAction = new DeleteAction();
	private final Action newUpdateAction = new NewUpdateAction();
	private final Action editUpdateAction = new EditUpdateAction();
	private final Action deleteUpdateAction = new DeleteAction();
	
	//Componente que proporciona las scrollbars al panel
	JScrollPane scrollPane;


	public EventDataUI(CurrentSession session) {
		this.session = session;

		setLayout(null);
		panelVisible = true;
		
		JTextPane eventDataTxt = new JTextPane();
		eventDataTxt.setText("GESTIÓN DE INCIDENCIAS");
		eventDataTxt.setFont(new Font("Tahoma", Font.BOLD, 20));
		eventDataTxt.setFocusable(false);
		eventDataTxt.setEditable(false);
		eventDataTxt.setBackground(UIManager.getColor(this.getBackground()));
		eventDataTxt.setBounds(50, 50, 380, 30);
		add(eventDataTxt);
		
		JLabel companyLabel = new JLabel("Empresa");
		companyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		companyLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		companyLabel.setBounds(50, 125, 200, 25);
		add(companyLabel);
		
		JLabel selectLabel = new JLabel("Unidades de negocio");
		selectLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		selectLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		selectLabel.setBounds(50, 175, 200, 25);
		add(selectLabel);
		
		eventsContainer = new JPanel();
		eventsContainer.setLayout(null);
		TitledBorder titledBorder = BorderFactory.createTitledBorder("Incidencias");
		eventsContainer.setBorder(titledBorder);
		Border margin = new EmptyBorder(15, 15, 15, 15);
		eventsContainer.setBorder(new CompoundBorder(eventsContainer.getBorder(), margin));
		
		newEventButton = new JButton();
		newEventButton.setAction(newEventAction);
		newEventButton.setBounds(25, 365, 89, 23);
		eventsContainer.add(newEventButton);
		
		editEventButton = new JButton();
		editEventButton.setAction(editEventAction);
		editEventButton.setBounds(125, 365, 89, 23);
		eventsContainer.add(editEventButton);
		
		deleteEventButton = new JButton();
		deleteEventButton.setAction(deleteEventAction);
		deleteEventButton.setBounds(225, 365, 89, 23);
		eventsContainer.add(deleteEventButton);
		
		//Estado inicial de los botones de la tabla de incidencias
		buttonSwitcher(EVENT_BUTTON_SET, NEW_ENABLED);
		
		JLabel totalEventsLabel = new JLabel("Incidencias totales: ");
		totalEventsLabel.setHorizontalAlignment(SwingConstants.LEFT);
		totalEventsLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		totalEventsLabel.setBounds(410, 365, 185, 25);
		eventsContainer.add(totalEventsLabel);
		
		JLabel eventsShownLabel = new JLabel("Incidencias mostradas: ");
		eventsShownLabel.setHorizontalAlignment(SwingConstants.LEFT);
		eventsShownLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		eventsShownLabel.setBounds(655, 365, 225, 25);
		eventsContainer.add(eventsShownLabel);
		
		totalEvents = new JLabel();
		totalEvents.setText(((Integer)session.getbUnit().getEvents().size()).toString());
		totalEvents.setHorizontalAlignment(SwingConstants.LEFT);
		totalEvents.setFont(new Font("Tahoma", Font.PLAIN, 20));
		totalEvents.setBounds(600, 365, 50, 25);
		eventsContainer.add(totalEvents);
		
		eventsShown = new JLabel();
		eventsShown.setText(((Integer)getLastEventsByNumber(session.getbUnit().getEvents(), 25).size()).toString());
		eventsShown.setHorizontalAlignment(SwingConstants.LEFT);
		eventsShown.setFont(new Font("Tahoma", Font.PLAIN, 20));
		eventsShown.setBounds(880, 365, 50, 25);
		eventsContainer.add(eventsShown);
		
		updatesContainer = new JPanel();
		updatesContainer.setLayout(null);
		TitledBorder titledBorder2 = BorderFactory.createTitledBorder("Actualizaciones");
		updatesContainer.setBorder(titledBorder2);
		Border margin2 = new EmptyBorder(15, 15, 15, 15);
		updatesContainer.setBorder(new CompoundBorder(updatesContainer.getBorder(), margin2));
		
		newUpdateButton = new JButton();
		newUpdateButton.setAction(newUpdateAction);
		newUpdateButton.setBounds(25, 265, 89, 23);
		updatesContainer.add(newUpdateButton);
		
		editUpdateButton = new JButton();
		editUpdateButton.setAction(editUpdateAction);
		editUpdateButton.setBounds(125, 265, 89, 23);
		updatesContainer.add(editUpdateButton);
		
		deleteUpdateButton = new JButton();
		deleteUpdateButton.setAction(deleteUpdateAction);
		deleteUpdateButton.setBounds(225, 265, 89, 23);
		updatesContainer.add(deleteUpdateButton);
		
		//Estado inicial de los botones de la tabla de actualizaciones
		buttonSwitcher(UPDATE_BUTTON_SET, ALL_DISABLED);
		
		filtersContainer = new JPanel();
		filtersContainer.setLayout(null);
		ButtonGroup filterGroup = new ButtonGroup();
		
		allEvents.setFont(new Font("Tahoma", Font.PLAIN, 20));
		allEvents.setBounds(0, 0, 200, 25);
		allEvents.setName("Todos");
		filterGroup.add(allEvents);
		filtersContainer.add(allEvents);
		
		last25.setFont(new Font("Tahoma", Font.PLAIN, 20));
		last25.setBounds(0, 40, 200, 25);
		last25.setName("Últimos 25");
		filterGroup.add(last25);
		last25.setSelected(true);
		filterSelected = last25;
		filtersContainer.add(last25);
		
		last50.setFont(new Font("Tahoma", Font.PLAIN, 20));
		last50.setBounds(0, 80, 200, 25);
		last50.setName("Últimos 50");
		filterGroup.add(last50);
		filtersContainer.add(last50);
		
		thisMonth.setFont(new Font("Tahoma", Font.PLAIN, 20));
		thisMonth.setBounds(0, 120, 200, 25);
		thisMonth.setName("Presente mes");
		filterGroup.add(thisMonth);
		filtersContainer.add(thisMonth);
		
		last3Months.setFont(new Font("Tahoma", Font.PLAIN, 20));
		last3Months.setBounds(0, 160, 200, 25);
		last3Months.setName("Último trimestre");
		filterGroup.add(last3Months);
		filtersContainer.add(last3Months);
		
		last6Months.setFont(new Font("Tahoma", Font.PLAIN, 20));
		last6Months.setBounds(0, 200, 200, 25);
		last6Months.setName("Último semestre");
		filterGroup.add(last6Months);
		filtersContainer.add(last6Months);
		
		thisYear.setFont(new Font("Tahoma", Font.PLAIN, 20));
		thisYear.setBounds(0, 240, 200, 25);
		thisYear.setName("Presente año");
		filterGroup.add(thisYear);
		filtersContainer.add(thisYear);
		
		allEvents.addActionListener(new EventFilterListener());
		last25.addActionListener(new EventFilterListener());
		last50.addActionListener(new EventFilterListener());
		thisMonth.addActionListener(new EventFilterListener());
		last3Months.addActionListener(new EventFilterListener());
		last6Months.addActionListener(new EventFilterListener());
		thisYear.addActionListener(new EventFilterListener());

		
		setPanelsDimensions(screenSize);
		
		eventsContainer.add(filtersContainer);
		add(eventsContainer);
		add(updatesContainer);
				
		companyField = new JTextField();
		companyField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		companyField.setBackground(UIManager.getColor(new JPanel().getBackground()));
		companyField.setBounds(260, 125, 400, 25);
		companyField.setText(session.getbUnit().getCompany().getNombre());
		companyField.setEditable(false);
		add(companyField);
		
		activeFilterCheckBox = new JCheckBox(" solo activas");
		activeFilterCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 20));
		activeFilterCheckBox.setBounds(666, 175, 154, 25);
		activeFilterCheckBox.setSelected(session.getbUnit().isActivo() ? true : false);
		activeFilterCheckBox.addItemListener(new CheckBoxListener());
		add(activeFilterCheckBox);
		if (!session.getUser().getUserType().equals("ADMIN")) {
			activeFilterCheckBox.setEnabled(false);
		}
		
		comboList = getComboBoxItemsFromSession(activeFilterCheckBox.isSelected());
		comboBox = new JComboBox(comboList);
		comboBox.setSelectedIndex(getSelectedIndexFromArray(comboList));
		comboBox.addItemListener(new ComboListener());
		comboBox.setBounds(260, 175, 400, 25);
		comboBox.setEditable(false);
		ToolBox.setBlackForeground(comboBox);
		comboBox.setBackground(Color.WHITE);
		add(comboBox);
		if (!session.getUser().getUserType().equals("ADMIN")) {
			comboBox.setEnabled(false);
		}
		
		infoLabel = new JLabel();
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setBounds(50, 950, eventsContainer.getBounds().width, 25);
//		infoLabel.setBounds(50, 965, 900, 25);
		infoLabel.setBackground(Color.RED);
//		infoLabel.setText("TEXTO DE PRUEBA");
		add(infoLabel);
		
		//Por defecto se muestran las últimas 25 incidencias registradas
		buildEventTable(getLastEventsByNumber(sortEventsByDate(session.getbUnit().getEvents()), 25), EVENTS_TABLE_HEADER);
		JScrollPane eventsPane = new JScrollPane(eventsTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);		
//		//Scroll al final de la tabla
		eventsTable.scrollRectToVisible(eventsTable.getCellRect(eventsTable.getRowCount()-1, 0, true));
		eventsPane.setBounds(25, 25, eventsContainer.getBounds().width - 250, 325);
		eventsContainer.add(eventsPane);
	
		//Por defecto la tabla de actualizaciones aparecerá vacía. Solo se llena cuando se selecciona un evento
		buildUpdatesTable(new ArrayList<EventUpdate>(), getUpdatesTableHeader());
		JScrollPane updatesPane = new JScrollPane(updatesTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		updatesTable.scrollRectToVisible(updatesTable.getCellRect(updatesTable.getRowCount()-1, 0, true));
		updatesPane.setBounds(25, 25, updatesContainer.getBounds().width - 50, 225);
		updatesContainer.add(updatesPane);
		
		
		/*Iniciamos la comprobación periódica de actualizaciones
		* Se realiza 2 veces por cada comprobación de los cambios en la base de datos que hace
		* el objeto session. Esto evita que si se produce la comprobación de datos que hace cada panel
		* cuando la actualización de datos que hace el objeto session aún no ha finalizado, se considere
		* por error que no había cambios.
		* Existe la posibilidad de que eso ocurra porque se comprueban y actualizan los datos de cada tabla
		* de manera consecutiva. Si a media actualización de los datos, un panel comprueba los datos que le
		* atañen y su actualización aún no se ha hecho, no los actualizará. Además, el registro de cambios
		* interno del objeto session se sobreescribirá en cuanto inicie una nueva comprobación, y el panel
		* nunca podrá reflejar los cambios. Eso pasaría si la actualización del panel se hace al mismo ritmo
		* o más lenta que la comprobación de los datos que hace el objeto session.
		*/
		
//		timer = new Timer();
//		TimerTask task = new TimerJob();
//		timer.scheduleAtFixedRate(task, 1000, session.getPeriod() / 2);
		
		startAnewTimer(1000);
	}
	
	/**
	 * Crea un nuevo Timer y le asigna un Timertask
	 * @param delay retardo en el comienzo de la ejecución del timer en milisegundos
	 */
	void startAnewTimer(long delay) {
		timer = new Timer();
		TimerTask task = new TimerJob();
		timer.scheduleAtFixedRate(task, 1000, session.getPeriod() / 2);
	}
	
	/**
	 * Obtiene la lista de unidades de negocio cargadas en el objeto company. Serán todas las que
	 * existan en la base de datos si el usuario que abre sesión es de tipo administrador, y solo una
	 * (la correspondiente al usuario que abre sesión) si es un usuario de otro tipo
	 * @param active true si se muestran solo las unidades de negocio activas, false para mostrarlas todas
	 * @return array ordenado alfabéticamente con la lista de unidades de negocio
	 */
	private String[] getComboBoxItemsFromSession(boolean active) {
		List<String> tempList = new ArrayList<String>();
		for (BusinessUnit bUnit: session.getCompany().getBusinessUnits()) {
			if (active) {
				if (bUnit.isActivo()) {
					tempList.add(bUnit.getNombre());
				}
			} else {
				tempList.add(bUnit.getNombre());
			}
		}	
		return ToolBox.toSortedArray(tempList);
	}
	
	/**
	 * Obiene el índice del elemento del objeto comboBox que será seleccionado por defecto a partir
	 * del array pasado por parámetro
	 * @param array array con la lista de unidades de negocio
	 * @return índice del elemento a seleccionar por defecto
	 */
	private int getSelectedIndexFromArray(String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(session.getbUnit().getNombre())) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Actualiza el contenido del comboBox que selecciona la unidad de negocio activa
	 */
	private void refreshComboBox() {
		comboList = getComboBoxItemsFromSession(activeFilterCheckBox.isSelected());
		comboBox.setModel(new DefaultComboBoxModel(comboList));
		comboBox.setSelectedIndex(getSelectedIndexFromArray(comboList));
	}
	
	/**
	 * Devuelve las últimas incidencias de la lista. Si la lista tiene menos incidencias de las que buscamos,
	 * retornamos la lista sin modificar.
	 * @param list lista de la que buscamos las últimas incidencias
	 * @param number número de incidencias que obtendremos de la lista
	 * @return lista filtrada con las últimas incidencias
	 */
	private List<Event> getLastEventsByNumber(List<Event> list, int number) {
		if(list.size() <= number) {
			return list;
		} else {
			List<Event> sublist = new ArrayList<Event>();
			sublist = list.subList(list.size() - number, list.size());
			return sublist;
		}
	}
	
	/**
	 * Devuelve las incidencias de la lista que están en el rango de fechas delimitado por el parámetro months
	 * @param list lista de la que buscamos las incidencias que están dentro del rango de fechas
	 * @param months número de meses que se incluyen en la lista de incidencias desde el presente mes (incluido)
	 * hacia atrás. Las incidencias se tienen que haber registrado en alguno de esos meses para formar parte de la lista.
	 * Si months = 12 se devuelven las incidencias del año en curso
	 * @return lista filtrada con las incidencias que están dentro del rango de fechas
	 */
	private List<Event> getLastEventsByDate(List<Event> list, int months) {
		List<Event> filteredList = new ArrayList<Event>();
		Calendar calendarReference = Calendar.getInstance();
	    Calendar eventReference = Calendar.getInstance();
	    if (months >= 0) {
	    	switch (months) {
	    		case 0:
	    			for (Event event : list) {
	    				eventReference.setTimeInMillis(event.findFirstUpdate().getFechaHora().getTime());
	    				if (calendarReference.get(Calendar.MONTH) == eventReference.get(Calendar.MONTH)
	    						&& calendarReference.get(Calendar.YEAR) == eventReference.get(Calendar.YEAR)) {
	    					filteredList.add(event);
	    				}
	    			}
	    			break;
	    		case 3:
	    		case 6:
	    			//orden descendente
//	    			for (int i = 0; i < months; i++)
	    			//Orden ascendente
	    			months = - months + 1;
	    			for (int i = months; i <= 0; i++) { //orden ascendente
	    				calendarReference = Calendar.getInstance();
	    				calendarReference.add(Calendar.MONTH, i);
	    				for (Event event : list) {
		    				eventReference.setTimeInMillis(event.findFirstUpdate().getFechaHora().getTime());
		    				if (calendarReference.get(Calendar.MONTH) == eventReference.get(Calendar.MONTH)
		    						&& calendarReference.get(Calendar.YEAR) == eventReference.get(Calendar.YEAR)) {
		    					filteredList.add(event);
		    				}
		    			}
	    			}
	    			break;
	    		case 12:
	    			for (Event event : list) {
	    				eventReference.setTimeInMillis(event.findFirstUpdate().getFechaHora().getTime());
	    				if (calendarReference.get(Calendar.YEAR) == eventReference.get(Calendar.YEAR)) {
	    					filteredList.add(event);
	    				}
	    			}
	    			break;
	    		default:
	    			System.out.println("Error de filtrado, criterio no previsto");
	    	}
	    }
		return filteredList;
	}
	
	/**
	 * Ordena una lista de incidencias pasada por parámetro en orden ascendente según el criterio de EventComparator
	 * (fecha de creación la incidencia) 
	 * @param list lista a ordenar
	 * @return lista ordenada
	 */
	private List<Event> sortEventsByDate (List<Event> list) {
		List<Event> sortedList = list;
		sortedList.sort(new EventComparator());
		return sortedList;
	}
	
	/**
	 * Ordena una lista de actualizaciones pasada por parámetro en orden ascendente según el criterio de EventUpdateComparator
	 * (fecha de creación la actualización) 
	 * @param list lista a ordenar
	 * @return lista ordenada
	 */
	List<EventUpdate> sortEventUpdatesByDate (List<EventUpdate> list) {
		List<EventUpdate> sortedList = list;
		sortedList.sort(new EventUpdateComparator());
		return list;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	EventUpdate findFirstUpdate() {
//		if (eventSelected.getUpdates().size() == 1) {
//
//			//Debug
//			System.out.println("Solo hay una actualización. La actualización inicial es esa");
//			
//			return eventSelected.getUpdates().get(0);
//		}
//		
//		EventUpdate tempUpdate = eventSelected.getUpdates().get(0);
//		int i;
//		for (i = 1; i < eventSelected.getUpdates().size(); i++) {
//			if (tempUpdate.getFechaHora().after(eventSelected.getUpdates().get(i).getFechaHora())) {
//				tempUpdate = eventSelected.getUpdates().get(i);
//			}
//		}
//		
//		//Debug
//		System.out.println("La tabla de actualizaciones tiene  " + eventSelected.getUpdates().size() + " elementos");
//		System.out.println("La actualización inicial está en la posición " + i);
//		
//		return tempUpdate;
//	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Busca la anchura de la pantalla en la que se ejecuta la aplicación. En base a esa anchura se dimensionarán los elementos de la pantalla
	 * @return small para pantallas inferiores a 1280px, medium para pantallas entre 1281 y 1450px, big para pantallas de más de 1450px
	 */
	private String getScreenSize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.width <= 1280) {
			return "small";
		} else if (screenSize.width > 1280 && screenSize.width <= 1450 ) {
			return "medium";
		} else {
			return "big";
		}
	}
	
	/**
	 * Dimensiona los paneles que contendrán las tablas de incidencias y actualizaciones en función del tamaño de la pantalla
	 * @param panel panel a dimensionar
	 * @param screenSize tamaño de pantalla
	 */
	private void setPanelsDimensions(String screenSize) {
		switch (screenSize) {
			case "small":
				eventsContainer.setBounds(50, 225, 1075, 400);
				updatesContainer.setBounds(50, 635, 1075, 300); //50, 650, 1075, 300
				break;
			case "medium":
				eventsContainer.setBounds(50, 225, 1175, 400);
				updatesContainer.setBounds(50, 635, 1175, 300);
				break;
			case "big":
				eventsContainer.setBounds(50, 225, 1675, 400);
				updatesContainer.setBounds(50, 635, 1675, 300);
		}

		filtersContainer.setBounds(eventsContainer.getBounds().width - 200, 25, 175, 325);
	}
	
	/**
	 * Dimensiona los paneles que contendrán las tablas de incidencias y actualizaciones en función del tamaño de la pantalla
	 * Método que dimensiona automáticamente los paneles en función del tamaño de la pantalla
	 * MÉTODO INCOMPLETO (work in progress...)
	 */
	private void setPanelsDimensions() {
		//This code to windowlistener
		JPanel parentPanel = (JPanel) SwingUtilities.getRoot((Component) EventDataUI.this);
		JFrame parentFrame = (JFrame) SwingUtilities.getRoot((Component) parentPanel);
		System.out.println(parentFrame == null);
		
		GraphicsDevice [] displays = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		GraphicsDevice currentDisplay = parentFrame.getGraphicsConfiguration().getDevice();
		System.out.println(currentDisplay.getDefaultConfiguration().getBounds().x);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
//		//Debug
//		System.out.println(screenSize.height + " " + screenSize.width);
		
		//work in progress...
		
	}
	
	/**
	 * Construye la tabla de incidencias con los datos y el formato deseados
	 * @param list lista de incidencias 
	 * @param header encabezados de las columnas de la tabla
	 */
	private void buildEventTable(List<Event> list, String[] header) {
		//Encabezados de la tabla
		Vector<Object> headerVector = new Vector<Object>(Arrays.asList(header));
		
		//Debug
		System.out.println();
		System.out.println("CONSTRUYENDO TABLA DE INCIDENCIAS....");
		System.out.println("-------------------------------------");
		
//		//Debug
//		int num = 0;
		
		//Datos de la tabla
		Vector<Vector<Object>> dataVector = new Vector<Vector<Object>>();
		for (Event event: list) {
			Vector<Object> eventVector = new Vector<Object>();
			eventVector.add((Integer) event.getId());
			eventVector.add(event.findFirstUpdate().getFechaHora());
			eventVector.add(event.getArea().getAreaNombre());
			eventVector.add(event.getEventType());
			eventVector.add(event.getTitulo());
			eventVector.add(event.getDescripcion());
			eventVector.add(event.getEventState());
			dataVector.add(eventVector);
			
//			//Debug
//			System.out.println("Elemento " + num + " de la lista");
//			System.out.println("id " + event.getId());
//			System.out.println("fechahora: " + event.getUpdates().get(0).getFechaHora());
//			System.out.println("ACTUALIZACIONES");
//			for (int i = 0; i < event.getUpdates().size(); i++) {
//				EventUpdate update = event.getUpdates().get(i);
//				System.out.println("Update id " + update.getId());
//				System.out.println("Update event id " + update.getEvent().getId());
//				System.out.println("Update fechahora " + update.getFechaHora().toString());
//				System.out.println("Update descripcion " + update.getDescripcion());
//			}
			
//			//Debug
//			num +=1;
		}
		eventsTable = new JTable(dataVector, headerVector) {
					
			//Celdas no editables
			@Override
		    public boolean isCellEditable(int row, int col) {
		        return false; 
		    }
				
			//Table tooltips 
			public Component prepareRenderer(TableCellRenderer renderer,int row, int col) {
				 Component comp = super.prepareRenderer(renderer, row, col);
				 JComponent jcomp = (JComponent)comp;
				 int dataLength = 0;
				 if (comp == jcomp) {
					 //Formato para Timestamp
					 int initialDelay = ToolTipManager.sharedInstance().getInitialDelay();
					 ToolTipManager.sharedInstance().setDismissDelay(initialDelay);
					 if (getValueAt(row, col).getClass() == Timestamp.class) {		 
						 jcomp.setToolTipText(ToolBox.formatTimestamp((Timestamp)getValueAt(row, col), DATE_TIME_PATTERN));
					 } else {			 
						 //Multi-line tooltips & Increase display tooltip time if needed (15 seconds)
						 dataLength = getValueAt(row, col).toString().length(); 
						 ToolTipManager.sharedInstance().setDismissDelay(initialDelay);
						 ToolTipManager.sharedInstance().setDismissDelay(dataLength >= 100 ? 15000 : initialDelay);
						 jcomp.setToolTipText(dataLength < 100 ? getValueAt(row, col).toString()
								 : "<html><p width=\"500\">" + getValueAt(row, col).toString() + "</p></html>");
					 }
				 }
				 return comp;
			}
		};
		
		//Restringimos la selección de la tabla a una única fila
		eventsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//Obtenemos la incidencia que corresponde a la fila de la tabla seleccionada
		eventsTable.getSelectionModel().addListSelectionListener(new EventTableSelectionListener());
		//Detectamos doble click en las filas de la tabla para habilitar su edición
		eventsTable.addMouseListener(new DoubleClickOnRowListener());

		eventsTable.setFillsViewportHeight(true);
		eventsTable.setAutoCreateRowSorter(true);
		formatEventsTable();
	}
	
	/**
	 * Actualiza la tabla de incidencias con los datos y el formato deseados
	 * @param list lista de incidencias de la unidad de negocio de la sesión
	 * @param header encabezados de las columnas de la tabla
	 */
	private void updateEventTable(List<Event> list, String[] header) {
		//Encabezados de la tabla
		Vector<Object> headerVector = new Vector<Object>(Arrays.asList(header));
		//Datos de la tabla
		Vector<Vector<Object>> dataVector = new Vector<Vector<Object>>();
		
		//Debug
		System.out.println("[updateEventsTable] La lista de incidencias contiene " + list.size() + " elementos");
		
		try {
			for (Event event : list) {
				Vector<Object> eventVector = new Vector<Object>();
				eventVector.add((Integer) event.getId());
				eventVector.add(event.findFirstUpdate().getFechaHora());
				eventVector.add(event.getArea().getAreaNombre());
				eventVector.add(event.getEventType());
				eventVector.add(event.getTitulo());
				eventVector.add(event.getDescripcion());
				eventVector.add(event.getEventState());
				dataVector.add(eventVector);
			}
			DefaultTableModel model = new DefaultTableModel(dataVector, headerVector);
			eventsTable.setModel(model);
			formatEventsTable();
			eventsTable.repaint();
		} catch (Exception e) {
			// TODO: handle exception
			//Debug
			System.out.println("*** [updateEventsTable] Se intenta actualizar la tabla sin datos...");
			System.out.println(e.getCause() + " - " + e.getMessage());
		}
	}
	
	/**
	 * Dimensiona el ancho de las columnas de la tabla de incidencias y aplica el formato establecido por la clase
	 * EventTableCellRenderer para las columnas Fecha / Hora y Estado
	 */
	private void formatEventsTable() {
		
		try {
			eventsTable.removeColumn(eventsTable.getColumnModel().getColumn(0));
			//Cell renderer para la columna Fecha / Hora
			TableColumn tableCol = eventsTable.getColumnModel().getColumn(0);
			tableCol.setCellRenderer(new EventTableCellRenderer());
			//Cell renderer para la columna Estado
			tableCol = eventsTable.getColumnModel().getColumn(5);
			tableCol.setCellRenderer(new EventTableCellRenderer());
			//Ancho columna Fecha / Hora
			eventsTable.getColumnModel().getColumn(0).setMinWidth(125);
			eventsTable.getColumnModel().getColumn(0).setMaxWidth(125);
			//Ancho columna Area
			eventsTable.getColumnModel().getColumn(1).setMinWidth(175);
			eventsTable.getColumnModel().getColumn(1).setMaxWidth(175);
			//Ancho columna Tipo
			eventsTable.getColumnModel().getColumn(2).setMinWidth(175);
			eventsTable.getColumnModel().getColumn(2).setMaxWidth(175);
			//Ancho columna Título
			eventsTable.getColumnModel().getColumn(3).setMinWidth(225);
			eventsTable.getColumnModel().getColumn(3).setMaxWidth(225);
			//Ancho columna Estado
			eventsTable.getColumnModel().getColumn(5).setMinWidth(100);
			eventsTable.getColumnModel().getColumn(5).setMaxWidth(100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Debug
			System.out.println("*** [formatEventTable] Se queja de algo...");
			System.out.println(e.getCause() + " - " + e.getMessage());
		}
		
		//Scroll al final de la tabla
		eventsTable.scrollRectToVisible(eventsTable.getCellRect(eventsTable.getRowCount()-1, 0, true));
	}
	
	/**
	 * Construye la tabla de actualizaciones con los datos y el formato deseados
	 * @param list lista de actualizaciones (las de la incidencia seleccionada)
	 * @param header encabezados de las columnas de la tabla
	 */
	private void buildUpdatesTable(List<EventUpdate> list, String[] header) {
		//Encabezados de la tabla
		Vector<Object> headerVector = new Vector<Object>(Arrays.asList(header));
		//Datos de la tabla
		Vector<Vector<Object>> dataVector = new Vector<Vector<Object>>();
		for (EventUpdate update: list) {
			Vector<Object> updateVector = new Vector<Object>();
			updateVector.add((Integer) update.getId());
			updateVector.add((Integer) update.getEvent().getId());
			updateVector.add(update.getFechaHora());
			updateVector.add(update.getDescripcion());
			updateVector.add(update.getAutor());
			updateVector.add(update.getUser().getUserAlias());
			dataVector.add(updateVector);
		}
		updatesTable = new JTable(dataVector, headerVector) {
			
			//Celdas no editables
			@Override
		    public boolean isCellEditable(int row, int col) {
		        return false; 
		    }
			
			//Table tooltips 
			public Component prepareRenderer(TableCellRenderer renderer,int row, int col) {
				 Component comp = super.prepareRenderer(renderer, row, col);
				 JComponent jcomp = (JComponent)comp;
				 int dataLength = 0;
				 if (comp == jcomp) {
					//Formato para Timestamp
					 int initialDelay = ToolTipManager.sharedInstance().getInitialDelay();
					 ToolTipManager.sharedInstance().setDismissDelay(initialDelay);
					 if (getValueAt(row, col).getClass() == Timestamp.class) {		 
						 jcomp.setToolTipText(ToolBox.formatTimestamp((Timestamp)getValueAt(row, col), DATE_TIME_PATTERN));
					 } else {				 
						//Multi-line tooltips & Increase display tooltip time if needed (15 seconds)
						 dataLength = getValueAt(row, col).toString().length(); 
						 ToolTipManager.sharedInstance().setDismissDelay(initialDelay);
						 ToolTipManager.sharedInstance().setDismissDelay(dataLength >= 200 ? 15000 : initialDelay);
						 jcomp.setToolTipText(dataLength < 250 ? getValueAt(row, col).toString()
								 : "<html><p width=\"500\">" + getValueAt(row, col).toString() + "</p></html>");
					 }
				 }
				 return comp;
			}
		};
		
		//Restringimos la selección de la tabla a una única fila
		updatesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//Obtenemos la actualización que corresponde a la fila de la tabla seleccionada
		updatesTable.getSelectionModel().addListSelectionListener(new EventUpdateTableSelectionListener());
		//Detectamos doble click en las filas de la tabla para habilitar su edición
		updatesTable.addMouseListener(new DoubleClickOnRowListener());
		
		updatesTable.setFillsViewportHeight(true);
		updatesTable.setAutoCreateRowSorter(true);
		formatUpdatesTable();
	}
	
	/**
	 * Actualiza la tabla de actualizaciones con los datos y el formato deseados
	 * @param list lista de actualizaciones del evento seleccionado
	 * @param header encabezados de las columnas de la tabla
	 */
	void updateUpdatesTable(List<EventUpdate> list, String[] header) {
		//Encabezados de la tabla
		Vector<Object> headerVector = new Vector<Object>(Arrays.asList(header));
		//Datos de la tabla
		Vector<Vector<Object>> dataVector = new Vector<Vector<Object>>();
		
		//Debug
		System.out.println("[updateUpdatesTable] La lista de actualizaciones contiene " + list.size() + " elementos");
		

		try {
			for (EventUpdate update : list) {

				Vector<Object> updateVector = new Vector<Object>();
				updateVector.add((Integer) update.getId());
				updateVector.add((Integer) update.getEvent().getId());
				updateVector.add(update.getFechaHora());
				updateVector.add(update.getDescripcion());
				updateVector.add(update.getAutor());
				updateVector.add(update.getUser().getUserAlias());
				dataVector.add(updateVector);
			}
			DefaultTableModel model = new DefaultTableModel(dataVector, headerVector);
			updatesTable.setModel(model);
			formatUpdatesTable();
			updatesTable.repaint();
		} catch (Exception e) {
			// TODO: handle exception
			//Debug
			System.out.println("*** [updateUpdatesTable] Se intenta actualizar la tabla sin datos...");
			System.out.println(e.getCause() + " - " + e.getMessage());
		}

	}
	
	/**
	 * Dimensiona el ancho de las columnas de la tabla de actualizaciones y aplica el formato establecido por la clase
	 * UpdatesTableCellRenderer para la columna Fecha / Hora
	 */
	private void formatUpdatesTable() {
		
		try {
			updatesTable.removeColumn(updatesTable.getColumnModel().getColumn(0));
			updatesTable.removeColumn(updatesTable.getColumnModel().getColumn(0));
			//Cell renderer para la columna Fecha / Hora
			TableColumn tableCol = updatesTable.getColumnModel().getColumn(0);
			tableCol.setCellRenderer(new UpdatesTableCellRenderer());
			//Ancho columna Fecha / Hora
			updatesTable.getColumnModel().getColumn(0).setMinWidth(125);
			updatesTable.getColumnModel().getColumn(0).setMaxWidth(125);
			//Ancho columna Autor
			updatesTable.getColumnModel().getColumn(2).setMinWidth(200);
			updatesTable.getColumnModel().getColumn(2).setMaxWidth(200);
			//Ancho columna Usuario
			updatesTable.getColumnModel().getColumn(3).setMinWidth(200);
			updatesTable.getColumnModel().getColumn(3).setMaxWidth(200);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Debug
			System.out.println("*** [formatUpdatesTable] Se queja de algo...");
			System.out.println(e.getCause() + " - " + e.getMessage());
		}
		
		//Scroll al final de la tabla
		updatesTable.scrollRectToVisible(updatesTable.getCellRect(updatesTable.getRowCount()-1, 0, true));
	}
	
	/**
	 * Habilita o deshabilita los botones de nuevo, editar y borrar de las tablas de incidencias y actualizaciones
	 * @param buttonSet determina el grupo de botones afectado (tablas incidencias o actualizaciones)
	 * @param buttonState determina el estado de los botones
	 */
	void buttonSwitcher (String buttonSet, String buttonState) {
		switch (buttonState) {
			case ALL_DISABLED:
				if (buttonSet.equals(EVENT_BUTTON_SET)) {
					newEventButton.setEnabled(false);
					editEventButton.setEnabled(false);
					deleteEventButton.setEnabled(false);
					break;
				}
				newUpdateButton.setEnabled(false);
				editUpdateButton.setEnabled(false);
				deleteUpdateButton.setEnabled(false);
				break;
			case NEW_ENABLED:
				if (buttonSet.equals(EVENT_BUTTON_SET)) {
					newEventButton.setEnabled(true);
					editEventButton.setEnabled(false);
					deleteEventButton.setEnabled(false);
					break;
				}
				newUpdateButton.setEnabled(true);
				editUpdateButton.setEnabled(false);
				deleteUpdateButton.setEnabled(false);
				break;
			case NEW_EDIT_ENABLED:
				if (buttonSet.equals(EVENT_BUTTON_SET)) {
					newEventButton.setEnabled(true);
					editEventButton.setEnabled(true);
					deleteEventButton.setEnabled(false);
					break;
				}
				newUpdateButton.setEnabled(true);
				editUpdateButton.setEnabled(true);
				deleteUpdateButton.setEnabled(false);
				break;
			case ALL_ENABLED:
				if (buttonSet.equals(EVENT_BUTTON_SET)) {
					newEventButton.setEnabled(true);
					editEventButton.setEnabled(true);
					deleteEventButton.setEnabled(true);
					break;
				}
				newUpdateButton.setEnabled(true);
				editUpdateButton.setEnabled(true);
				deleteUpdateButton.setEnabled(true);
				break;
			default:
				System.out.println("Button switch no contemplado");
		}
	}
	
	/**
	 * Habilita los botones de editar y borrar incidencias en caso de que el usuario de la sesión sea de tipo ADMIN
	 * o MANAGER. Si el usuario de la sesión es de tipo USER, habilita los botones de editar y borrar incidencias
	 * en caso de que la incidencia haya sido creada por él. En caso contrario, los botones editar y borrar se 
	 * deshabilitan.
	 */
	private void updateEventButtonsStateOnSelection() {

		//Usuario de sesión de tipo USER
		if (session.getUser().getUserType().equals("USER")) {
			//El usuario de tipo USER ha creado la incidencia
//			if(eventSelected.getUpdates().get(0).getUser().getId() == session.getUser().getId()) {
			if(firstUpdate.getUser().getId() == session.getUser().getId()) {
				buttonSwitcher(EVENT_BUTTON_SET, ALL_ENABLED);
			//El usuario de tipo USER no ha creado la incidencia
			} else {
				buttonSwitcher(EVENT_BUTTON_SET, NEW_ENABLED);
			}
		//Usuarios ADMIN y MANAGER
		} else {
			buttonSwitcher(EVENT_BUTTON_SET, ALL_ENABLED);
		}
	}
	
	/**
	 * Deshabilita el botón de borrar actualizaciones con cualquier tipo de usuario si la actualización
	 * seleccionada es la actualización inicial (la que se crea siempre con la creación de una nueva incidencia).
	 * Habilita el botón de editar con cualquier actualización en caso de que el usuario de la sesión sea de tipo
	 * ADMIN o MANAGER. Habilita el botón de borrar con cualquier actualización que no sea la actualización inicial
	 * en caso de que el usuario de la sesión sea de tipo ADMIN o MANAGER. Si el usuario de la sesión es de tipo USER,
	 * habilita los botones de editar y borrar actualizaciones en caso de que la actualización haya sido creada por él
	 * (exceptuando la actualización inicial que nunca tendrá el botón borrar habilitado). Si la actualización no ha
	 * sido creada por el usuario de tipo USER, los botones editar y borrar se deshabilitan.
	 * @param selectedRow fila de la tabla de actualizaciones seleccionada
	 */
	private void updateEventUpdateButtonsStateOnSelection(int selectedRow) {
		//Si no hay selección de actualización
		if (selectedRow == -1) {
			buttonSwitcher(UPDATE_BUTTON_SET, NEW_ENABLED);
			return;
		}
		//Actualización inicial, usuarios ADMIN y MANAGER
		if (selectedRow == 0 && !session.getUser().getUserType().equals("USER")) {
			buttonSwitcher(UPDATE_BUTTON_SET, NEW_EDIT_ENABLED);
			return;
		}
		
		//Resto de actualizaciones, usuarios ADMIN y MANAGER
		if (selectedRow != 0 && !session.getUser().getUserType().equals("USER")) {
			buttonSwitcher(UPDATE_BUTTON_SET, ALL_ENABLED);
			return;
		}
		
		//Actualización inicial, usuarios USER
		if (selectedRow == 0 && session.getUser().getUserType().equals("USER")) {
			//El usuario de tipo USER ha creado la incidencia
			if (updateSelected.getUser().getId() == session.getUser().getId()) {
				buttonSwitcher(UPDATE_BUTTON_SET, NEW_EDIT_ENABLED);
			//El usuario de tipo USER no ha creado la incidencia
			} else {
				buttonSwitcher(UPDATE_BUTTON_SET, NEW_ENABLED);
			}
			return;
		}
		
		//Resto de actualizaciones, usuarios USER
		if (selectedRow != 0 && session.getUser().getUserType().equals("USER")) {
			//El usuario de tipo USER ha creado la actualización
			if (updateSelected.getUser().getId() == session.getUser().getId()) {
				buttonSwitcher(UPDATE_BUTTON_SET, ALL_ENABLED);
			//El usuario de tipo USER no ha creado la actualización
			} else {
				buttonSwitcher(UPDATE_BUTTON_SET, NEW_ENABLED);
			}
		}
	}
	
	/**
	 * Borra una incidencia o una actualización en función de la acción pasada por parámetro. El borrado de una incidencia
	 * implica el borrado de todas sus actualizaciones.
	 * @param action determina si se borra una incidencia o una actualización
	 */
	private void delete(int action) {
		String text = "";
		if (action == EVENTDATA_ACTION_DELETE_EVENT) {
			text = "El borrado de incidencias no se puede deshacer. ¿Desea continuar?";
		}
		if (action == EVENTDATA_ACTION_DELETE_UPDATE) {
			text = "El borrado de actualizaciones no se puede deshacer. ¿Desea continuar?";
		}
		int optionSelected = ToolBox.showDialog(text, EventDataUI.this,	DIALOG_YES_NO);
		if (optionSelected != JOptionPane.YES_OPTION) {
//			//Debug
//			System.out.println("Borrado cancelado");
			return;
		} else {
//			//Debug
//			System.out.println("Borrado autorizado");
			switch (action) {
				case EVENTDATA_ACTION_DELETE_EVENT:
					//Debug
					System.out.println("Borrando incidencia... ID " + eventSelected.getId());
					
					//Si todas las actualizaciones de la incidencia se borran correctamente de la base de datos
					if (new EventUpdate().deleteAllEventUpdatesFromDb(session.getConnection(), eventSelected)) {
						//Registramos fecha y hora de la actualización de los datos de la tabla event_update
						PersistenceManager.registerTableModification(infoLabel, "", session.getConnection(), tNow,
								EventUpdate.TABLE_NAME);
						//Si la incidencia seleccionada se borra correctamente de la base de datos
						if (new Event().deleteEventFromDb(session.getConnection(), eventSelected)) {
							//Registramos fecha y hora de la actualización de los datos de la tabla event
							PersistenceManager.registerTableModification(infoLabel, "INCIDENCIA BORRADA: ", session.getConnection(), tNow,
									Event.TABLE_NAME);
						}
						//Eliminamos la incidencia de la lista de incidencias de la unidad de negocio de la sesión
						session.getbUnit().getEvents().remove(eventSelected);
						//Reseleccionamos el último filtro seleccionado, que a su vez actualiza la tabla de incidencias
						//y el número de incidencias mostradas y totales
						filterSelected.doClick();

					}
					
					break;
				case EVENTDATA_ACTION_DELETE_UPDATE:
					//Debug
					System.out.println("Borrando actualización... ID " + updateSelected.getId());
					
					//Si la actualización seleccionada se borra correctamente de la base de datos
					if (new EventUpdate().deleteEventUpdateFromDb(session.getConnection(), updateSelected)) {
						//Registramos fecha y hora de la actualización de los datos de la tabla event_update
						PersistenceManager.registerTableModification(infoLabel, "ACTUALIZACIÓN BORRADA: ", session.getConnection(), tNow,
								EventUpdate.TABLE_NAME);
						//Eliminamos la actualización de la lista de actualizaciones de la incidencia seleccionada
						eventSelected.getUpdates().remove(updateSelected);
//						new EventUpdate().deleteEventUpdate(eventSelected, updateSelected);
						//Obtenemos las actualizaciones de la incidencia seleccionada y las mostramos en la tabla de actualizaciones
						updateUpdatesTable(sortEventUpdatesByDate(eventSelected.getUpdates()), getUpdatesTableHeader());
						//Tras renovar la tabla de actualizaciones, solo el botón de nueva actualización queda habilitado
			        	buttonSwitcher(UPDATE_BUTTON_SET, NEW_ENABLED);
					}
					
					break;
				default:
					//Debug
					System.out.println("Error de borrado");
			}
		}
	}
	
	/**
	 * Muestra la pantalla de creación / edición de incidencias y actualizaciones
	 * @param mode modo de creación / edición de incidencias y actualizaciones
	 */
	private void goToNewEditScreen(int mode) {
		EventEditUI eEditUI = new EventEditUI(session, mode, this);
		eEditUI.setOpaque(true);
		this.setVisible(false);
		eEditUI.setVisible(true);
		scrollPane.getViewport().add(eEditUI);
		panelVisible = false;
	}
	
	/**
	 * Listener que define el comportamiento del objeto comboBox. Cada elemento se corresponde con
	 * las unidades de negocio de la compañía que se han cargado en la sesión. Por el nombre seleccionado
	 * se localiza el objeto BusinessUnit al que pertenece y se asigna dicho objeto como unidad de negocio
	 * de la sessión, reemplazando al que hubiera hasta ese momento. Si activeFilterCheckBox está seleccionado,
	 * no se mostrarán las unidades de negocio que estén marcadas como no activas
	 */
	private class ComboListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			
			if (e.getStateChange()== ItemEvent.SELECTED) {
				//Debug
				System.out.println("BUnit de la sesión: " + session.getbUnit().getNombre());
				String item = (String) comboBox.getSelectedItem();
				Company company = session.getCompany();
				//Recuperamos la unidad de negocio seleccionada
				BusinessUnit selectedBunit = new BusinessUnit().getBusinessUnitByName(company, item);
				//La asignamos a la sesión
				session.setbUnit(selectedBunit);
//				//Debug
//				for (User user : session.getbUnit().getUsers()) {
//					System.out.println(user.getUserAlias());
//				}
				//Reseleccionamos el último filtro seleccionado, que a su vez actualiza la tabla de incidencias
				//y el número de incidencias mostradas y totales
				filterSelected.doClick();
			
				//EN PRINCIPIO NO REACTIVAR
//				//Vaciamos label de información
//				infoLabel.setText("");
			}
		}		
	}
	
	/**
	 * Listener que define el comportamiento del checkbox activeFilterCheckBox.
	 * Si activamos el checkbox y la unidad de negocio en pantalla está activa no habrá ningún cambio. Si no está activa,
	 * la unidad de negocio de la sesión pasará a ser la del usuario que abrió sesión, y se mostrará en pantalla. En ese
	 * caso comboBox dejará de mostrar las unidades de negocio no activas.
	 * Si desactivamos el checkbox no habrá ningún cambio. comboBox pasará a mostrar todas las unidades de negocio cargadas en
	 * la sesión.
	 */
	private class CheckBoxListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			
			int state = e.getStateChange();
			if (state == ItemEvent.SELECTED) {
				//Si la bUnit de la sesión no está activa
				if (session.getbUnit().isActivo() == false) {		
					//Buscamos la bUnit del usuario que abrió sesión
					BusinessUnit userBunit = new BusinessUnit().getBusinessUnitById(session.getCompany(), session.getUser().getbUnit().getId());
					//La asignamos como bUnit de la sesión
					session.setbUnit(userBunit);
					
//					//Debug
//					System.out.println("BUnit de la sesión: " + session.getbUnit().getNombre());
					
					//Renovamos la lista de las unidades de negocio del comboBox
					refreshComboBox();
					//Reseleccionamos el último filtro seleccionado, que a su vez actualiza la tabla de incidencias
					//y el número de incidencias mostradas y totales
					filterSelected.doClick();
					
					//EN PRINCIPIO NO REACTIVAR
//					//Vaciamos label de información
//					infoLabel.setText("");
					
				//Si la bUnit de la sesión está activa, hay que renovar el combobox igualmente para que ya no salgan las bUnits no activas
				} else {
					//Renovamos la lista de las unidades de negocio del comboBox
					refreshComboBox();
				}
			} else if (state == ItemEvent.DESELECTED) {
				//Renovamos la lista de las unidades de negocio del comboBox
				refreshComboBox();
			}
		}
	}
		
	/**
	 * Listener que dispara el filtrado de incidencias. La tabla de incidencias se actualiza en función del filtro seleccionado,
	 * la tabla de actualizaciones se borra (ninguna incidencia queda seleccionada tras la actualización), solo el botón de nueva
	 * incidencia queda seleccionado
	 */
	private class EventFilterListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			filterSelected = (JRadioButton) e.getSource();
			if (e.getSource() == allEvents) {
				//Todas las incidencias de la unidad de negocio de la sesión
				currentEventList = session.getbUnit().getEvents();
				updateEventTable(sortEventsByDate(currentEventList), EVENTS_TABLE_HEADER);
				eventsShown.setText(((Integer)session.getbUnit().getEvents().size()).toString());
				
				//Debug
				System.out.println("Listener All");

			} else if (e.getSource() == last25) {
				//Últimas 25 incidencias
				//Son las últimas por fecha de creación de la incidencia, no necesariamente las últimas 25 registradas, ya que se
				//pueden crear incidencias de fechas anteriores a la fecha actual
				currentEventList = getLastEventsByNumber(sortEventsByDate(session.getbUnit().getEvents()), 25);
				updateEventTable(currentEventList, EVENTS_TABLE_HEADER);
				//Actualizamos el número de incidencias mostradas
				eventsShown.setText(((Integer)currentEventList.size()).toString());

				//Debug
				System.out.println("Listener 25");
				
			} else if (e.getSource() == last50) {
				//Últimas 50 incidencias
				//Son las últimas por fecha de creación de la incidencia, no necesariamente las últimas 25 registradas, ya que se
				//pueden crear incidencias de fechas anteriores a la fecha actual
				currentEventList = getLastEventsByNumber(sortEventsByDate(session.getbUnit().getEvents()), 50);
				updateEventTable(currentEventList, EVENTS_TABLE_HEADER);
				//Actualizamos el número de incidencias mostradas
				eventsShown.setText(((Integer)currentEventList.size()).toString());

				//Debug
				System.out.println("Listener 50");
				
			} else if (e.getSource() == thisMonth) {
				//Incidencias del mes actual
				currentEventList = getLastEventsByDate(session.getbUnit().getEvents(), 0);
				updateEventTable(currentEventList, EVENTS_TABLE_HEADER);
				//Actualizamos el número de incidencias mostradas
				eventsShown.setText(((Integer)currentEventList.size()).toString());

				//Debug
				System.out.println("Listener thisMonth. List size: " + getLastEventsByDate(session.getbUnit().getEvents(), 0).size());
				
			} else if (e.getSource() == last3Months) {
				//Incidencias del mes actual y 2 meses más hacia atrás
				currentEventList = getLastEventsByDate(session.getbUnit().getEvents(), 3);
				updateEventTable(currentEventList, EVENTS_TABLE_HEADER);
				//Actualizamos el número de incidencias mostradas
				eventsShown.setText(((Integer)currentEventList.size()).toString());

				//Debug
				System.out.println("Listener last3Months. List size: " + getLastEventsByDate(session.getbUnit().getEvents(), 3).size());
				
			} else if (e.getSource() == last6Months) {
				//Incidencias del mes actual y 5 meses más hacia atrás
				currentEventList = getLastEventsByDate(session.getbUnit().getEvents(), 6);
				updateEventTable(currentEventList, EVENTS_TABLE_HEADER);
				//Actualizamos el número de incidencias mostradas
				eventsShown.setText(((Integer)currentEventList.size()).toString());

				//Debug
				System.out.println("Listener last6Months. List size: " + getLastEventsByDate(session.getbUnit().getEvents(), 6).size());
				
			} else if (e.getSource() == thisYear) {
				//Incidencias del año actual
				currentEventList = getLastEventsByDate(session.getbUnit().getEvents(), 12);
				updateEventTable(currentEventList, EVENTS_TABLE_HEADER);
				//Actualizamos el número de incidencias mostradas
				eventsShown.setText(((Integer)currentEventList.size()).toString());
				
				//Debug
				System.out.println("Listener thisYear. List size: " + getLastEventsByDate(session.getbUnit().getEvents(), 12).size());
				
			}
			//Actualizamos el número total de incidencias de la unidad de negocio seleccionada
			totalEvents.setText(((Integer) session.getbUnit().getEvents().size()).toString());
			//Seleccionamos el filtro
			filterSelected.setSelected(true);
			//Vaciamos la tabla de actualizaciones
			updateUpdatesTable(new ArrayList<EventUpdate>(), getUpdatesTableHeader());
			//Solo el botón nueva incidencia queda habilitado
			buttonSwitcher(EVENT_BUTTON_SET, NEW_ENABLED);
			buttonSwitcher(UPDATE_BUTTON_SET, ALL_DISABLED);
		}
	}
	
	/**
	 * Listener que registra la fila seleccionada en la tabla de incidencias. En función de la selección, muestra las actualizaciones
	 * vinculadas a la incidencia en la tabla de incidencias, y habilita o deshabilita los botones de nueva, editar y borrar de la
	 * tabla de incidencias según los criterios de updateEventButtonsStateOnSelection(). Se registra el correspondiente evento
	 * seleccionado
	 */
	private class EventTableSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
        	//Consideramos solo el valor final de la selección (el listener se ejecuta una sola vez)
        	if (!e.getValueIsAdjusting()) {
        		
				//Debug
        		System.out.println("###############################################################");
        		System.out.println("EventTableSelectionListener");
				System.out.println("Tabla incidencias, fila seleccionada :" + eventsTable.getSelectedRow());
				System.out.println("###############################################################");
				
				if (eventsTable.getSelectedRow() > -1) {
					int eventSelectedID = (int) eventsTable.getModel().getValueAt(eventsTable.getSelectedRow(),	0);
					eventSelected = new Event().getEventById(session.getbUnit(), eventSelectedID);
					firstUpdate = eventSelected.findFirstUpdate();
					eventsRow = eventsTable.getSelectedRow();
					//Debug
					
//					System.out.println("Id Incidencia: " + eventSelected.getId() + " " + eventSelected.getDescripcion());
//					System.out.println("Id actualización inicial: " + firstUpdate.getId() + " " + firstUpdate.getDescripcion());
//					System.out.println("-------------------------------------------------------------------------------------");
					
					//Actualizamos el estado de los botones de la tabla de incidencias
					updateEventButtonsStateOnSelection();
					//Obtenemos las actualizaciones de la incidencia seleccionada y las mostramos en la tabla de actualizaciones
					updateUpdatesTable(sortEventUpdatesByDate(eventSelected.getUpdates()), getUpdatesTableHeader());
				} else {
					//Aquí deberíamos deseleccionar eventSelected, updateSelected y firstUpdate???
					eventSelected = null; /////CHECK!!!
					updateSelected = null; /////CHECK!!!
					firstUpdate = null; /////CHECK!!!
				}
				
				
			}
			//Tras renovar la tabla de actualizaciones, solo el botón de nueva actualización queda habilitado
        	buttonSwitcher(UPDATE_BUTTON_SET, NEW_ENABLED);
        }
	}
	
	/**
	 * Listener que registra la fila seleccionada en la tabla de actualizaciones. En función de la selección, habilita
	 * o deshabilita los botones de nueva, editar y borrar de la tabla de actualizaciones según los criterios de
	 * updateEventUpdateButtonsStateOnSelection(). Se registra la correspondiente actualización seleccionada
	 */
	private class EventUpdateTableSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			//Consideramos solo el valor final de la selección (el listener se ejecuta una sola vez)
        	if (!e.getValueIsAdjusting()) {
       	
				//Debug
        		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        		System.out.println("EventUpdateTableSelectionListener");
				System.out.println("Tabla actualizaciones, fila seleccionada :" + updatesTable.getSelectedRow());
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				
				
				if (updatesTable.getSelectedRow() > -1) {
					int updateID = (int) updatesTable.getModel().getValueAt(updatesTable.getSelectedRow(), 0);
					updateSelected = new EventUpdate().getEventUpdateById(eventSelected, updateID);
					updatesRow = updatesTable.getSelectedRow();
					
					//Debug
					System.out.println("Id Actualización: " + updateSelected.getId() + " " + updateSelected.getDescripcion());
					System.out.print("Es la actualización inicial? ");
					System.out.println(updateSelected.getId() == firstUpdate.getId());
					System.out.println("-------------------------------------------------------------------------------------");

					updateEventUpdateButtonsStateOnSelection(updatesTable.getSelectedRow());
				} else {
					//Aquí deberíamos deseleccionar updateSelected???
					updateSelected = null; /////CHECK!!!
				}
			} 
		}
	}
	
	/**
	 * Listener que detecta el doble click sobre alguna de las filas de las tablas de incidencias y actualizaciones
	 * El doble click sobre una fila equivale a entrar a la pantalla de edición de dicha fila, siempre y cuando el
	 * botón de edición esté activado.
	 */
	private class DoubleClickOnRowListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			//Detectamos doble click
			if (e.getClickCount() == 2) {
				JTable tableSelected = (JTable) e.getSource();
				
				//Detectamos origen
				//Tabla incidencias
				if (tableSelected == eventsTable) {
					//Si el botón de edición está activado
					if (editEventButton.isEnabled()) {
						editEventButton.doClick();
					}
				}
				//Tabla actualizaciones
				if (tableSelected == updatesTable) {
					//Si el botón de edición está activado
					if (editUpdateButton.isEnabled()) {
						editUpdateButton.doClick();
					}
				}
			}
		}
	}
	
	/**
	 * Cell Renderer que da formato a la tabla de incidencias. En concreto da formato a la columna Fecha / Hora y a la columna Estado
	 * La columna Fecha / Hora muestra la fecha y la hora en el formato DATE_TIME_PATTERN
	 * Las celdas de la columna Estado tendrán fondo rojo si la incidencia está abierta, amarillo si está en curso, y verde si está cerrada
	 */
	private class EventTableCellRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent (JTable table, 
				Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(
					table, obj, isSelected, hasFocus, row, column);
			if (eventsTable.getValueAt(row, column).getClass() == Timestamp.class) {
				setText(ToolBox.formatTimestamp((Timestamp)eventsTable.getValueAt(row, column), DATE_TIME_PATTERN));
			} else if (eventsTable.getValueAt(row, column).equals(TypesStatesContainer.getEvState().getEventState(1))) {
				cell.setBackground(Color.RED);
			} else if (eventsTable.getValueAt(row, column).equals(TypesStatesContainer.getEvState().getEventState(2))) {
				cell.setBackground(Color.YELLOW);
			} else if (eventsTable.getValueAt(row, column).equals(TypesStatesContainer.getEvState().getEventState(3))) {
				cell.setBackground(Color.GREEN);	
			}
			return cell;
		}
	}
	
	/**
	 * Cell Renderer que da formato a la tabla de actualizaciones. En concreto da formato a la columna Fecha / Hora 
	 * La columna Fecha / Hora muestra la fecha y la hora en el formato DATE_TIME_PATTERN
	 */
	private class UpdatesTableCellRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent (JTable table, 
				Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(
					table, obj, isSelected, hasFocus, row, column);
			if (updatesTable.getValueAt(row, column).getClass() == Timestamp.class) {
				setText(ToolBox.formatTimestamp((Timestamp)updatesTable.getValueAt(row, column), DATE_TIME_PATTERN));
			}
			return cell;
		}
	}
	
	/**
	 * Comparator para ordenar las incidencias por su fecha de creación en orden ascendente
	 */
	private class EventComparator implements Comparator<Event> {
		@Override
		public int compare(Event o1, Event o2) {
			Timestamp fechaHoraO1 = o1.findFirstUpdate().getFechaHora();
			Timestamp fechaHoraO2 = o2.findFirstUpdate().getFechaHora();
			
//			if (o1.getUpdates().size() == 1) {
//				fechaHoraO1 = o1.getUpdates().get(0).getFechaHora();
//			} else {
//				Timestamp tempFechaHora1 = o1.getUpdates().get(0).getFechaHora();
//				int i;
//				for (i = 1; i < o1.getUpdates().size(); i++) {
//					if (tempFechaHora1.after(o1.getUpdates().get(i).getFechaHora())) {
//						tempFechaHora1 = o1.getUpdates().get(i).getFechaHora();
//					}
//				}
//				fechaHoraO1 = tempFechaHora1;
//			}
//			
//			if (o2.getUpdates().size() == 1) {
//				fechaHoraO2 = o2.getUpdates().get(0).getFechaHora();
//			} else {
//				Timestamp tempFechaHora2 = o2.getUpdates().get(0).getFechaHora();
//				int i;
//				for (i = 1; i < o2.getUpdates().size(); i++) {
//					if (tempFechaHora2.after(o2.getUpdates().get(i).getFechaHora())) {
//						tempFechaHora2 = o2.getUpdates().get(i).getFechaHora();
//					}
//				}
//				fechaHoraO2 = tempFechaHora2;
//			}
			
			return fechaHoraO1.compareTo(fechaHoraO2);
		}	
	}
	
	/**
	 * Comparator para ordenar las actualizaciones por su fecha de creación en orden ascendente
	 */
	private class EventUpdateComparator implements Comparator<EventUpdate> {
		@Override
		public int compare(EventUpdate o1, EventUpdate o2) {
			return o1.getFechaHora().compareTo(o2.getFechaHora());
		}	
	}
	
	/**
	 * Acción del botón nueva incidencia. Se comprueba la existencia de areas asignadas a la unidad de negocio de la sesión. Si no las hay,
	 * se avisa y se impide la acción. Si las hay, se comprueba la existencia de tipos de eventos registrados. Si no los hay, se avisa y se
	 * impide la acción. Si los hay, se pasa a la pantalla de creación / edición de incidencias y actualizaciones en modo de creación de nueva
	 * incidencia.
	 */
	private class NewEventAction extends AbstractAction {
		public NewEventAction() {
			putValue(NAME, "Nueva");
			putValue(SHORT_DESCRIPTION, "Add new event");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean okNew = true;
			
			//Si no hay areas asignadas a la unidad de negocio de la sesión, se avisa y se bloquea la creación de una nueva incidencia
			if (session.getbUnit().getAreas().size() == 0) {
				ToolBox.showDialog(
						"<html><body><div align='center'>No existen areas asignadas la unidad de negocio seleccionada<br>"
						+ "No se pueden abrir incidencias</div></body></html>",
						EventDataUI.this, DIALOG_INFO);
				okNew = false;
			} 
			//Si no hay tipos de eventos registrados, se avisa y se bloquea la creación de una nueva incidencia
			if (TypesStatesContainer.getEvType().getEventTypes().size() == 0) {
				if (session.getbUnit().getAreas().size() == 0) {
					ToolBox.showDialog(
							"<html><body><div align='center'>No existen tipos de incidencias registrados<br>"
							+ "No se pueden abrir incidencias</div></body></html>", EventDataUI.this,
							DIALOG_INFO);
					okNew = false;
				} 
			} 
			//Si no hay errores, procedemos a crear la nueva incidencia
			if (okNew) {
				
				//Debug
				System.out.println("Nueva incidencia");
				
				goToNewEditScreen(EventEditUI.getEventEditActionNewEvent());
			}	
		}
	}
	
	private class EditEventAction extends AbstractAction {
		public EditEventAction() {
			putValue(NAME, "Editar");
			putValue(SHORT_DESCRIPTION, "Enable event edit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
			//Debug
			System.out.println("Editar incidencia");
			
			goToNewEditScreen(EventEditUI.getEventEditActionEditEvent());
		}
	}
	
	/**
	 * Acción de los botones borrar. Si el origen es deleteEventButton se borra la incidencia seleccionada,
	 * si es deleteUpdateButton se borra la actualización seleccionada
	 */
	private class DeleteAction extends AbstractAction {
		public DeleteAction() {
			putValue(NAME, "Borrar");
			putValue(SHORT_DESCRIPTION, "Delete event");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			//borrado de eventos
			if (e.getSource() == deleteEventButton) {
				actionSelector = EVENTDATA_ACTION_DELETE_EVENT;
			}
			
			//borrado de actualizaciones
			if (e.getSource() == deleteUpdateButton) {
				actionSelector = EVENTDATA_ACTION_DELETE_UPDATE;
			}
			delete(actionSelector);
			actionSelector = EVENTDATA_ACTION_UNDEFINED;
		}
	}
	
	private class NewUpdateAction extends AbstractAction {
		public NewUpdateAction() {
			putValue(NAME, "Nueva");
			putValue(SHORT_DESCRIPTION, "Add new update");
		}
		@Override
		public void actionPerformed(ActionEvent e) {

			//Debug
			System.out.println("Crear nueva actualización");
			
			goToNewEditScreen(EventEditUI.getEventEditActionNewUpdate());
			
		}
	}
	
	private class EditUpdateAction extends AbstractAction {
		public EditUpdateAction() {
			putValue(NAME, "Editar");
			putValue(SHORT_DESCRIPTION, "Enable update edit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {

			//Debug
			System.out.println("Editar actualización");
			
			goToNewEditScreen(EventEditUI.getEventEditActionEditUpdate());
			
		}
	}

	/**
	 * Clase que consulta al objeto session si los datos que le atañen han sido actualizados en la base de datos,
	 * de manera que pueda actualizar el contenido del panel con dichos datos. Si el panel no está visible, no se
	 * produce la comprobación porque no es necesaria.
	 */
	private class TimerJob extends TimerTask {
		
		//TimerJob se ejecuta dos veces por cada ejecución del TimerJob de CurrentSession. timerCycle registra en
		//qué ciclo de ejecución estamos
		private int timerCycle = 0;
		
		@Override
		public void run() {
			//Si se ha cerrado el panel, se cancelan la tarea y el temporizador
			if (!EventDataUI.this.isShowing()) {
				EventDataUI.this.panelVisible = false;
				this.cancel();
				EventDataUI.this.timer.cancel();
				
				//Debug
				 System.out.println("Se ha cerrado la ventana gestión de eventos");
			}
			//Se comprueba la actualización de los datos si el panel es visible
			if (EventDataUI.this.panelVisible == true) {
				
				//Debug
				System.out.println("Timerjob run()");
				 
				//Si estamos en el ciclo 0, TimerJob hace su trabajo 
				if (timerCycle == 0) {
					
					//Debug
					System.out.println("Ciclo 0");
					
					//Si la unidad de negocio de la sesión ha sido marcada como no activa y el filtro de unidades de negocio está
					//activado, la unidad de negocio de la sesión pasa a ser la del usuario que abrió sesión
					if (activeFilterCheckBox.isSelected() && session.getbUnit().isActivo() == false) {
						//Buscamos la bUnit del usuario que abrió sesión
						BusinessUnit userBunit = new BusinessUnit().getBusinessUnitById(session.getCompany(),
								session.getUser().getbUnit().getId());
						//La asignamos como bUnit de la sesión
						session.setbUnit(userBunit);
						//Renovamos la lista de las unidades de negocio del comboBox
						refreshComboBox();
						//Actualizamos la tabla de incidencias
						filterSelected.doClick();
					}
					//Loop por el Map de CurrentSession, si aparece la tabla event o event_update, recargar datos
					for (Map.Entry<String, Timestamp> updatedTable : session.getUpdatedTables().entrySet()) {

						//Debug
						System.out.println("Entramos en el map de tablas actualizadas...");
						System.out.print(updatedTable.getKey().toString());
						System.out.println(" - " + updatedTable.getValue().toString());

						//Si en la tabla de actualizaciones aparece la clave BusinessUnit.TABLE_NAME
						//Si en la tabla de actualizaciones aparece la clave Event.TABLE_NAME
						//Si en la tabla de actualizaciones aparece la clave EventUpdate.TABLE_NAME
						if (updatedTable.getKey().equals(BusinessUnit.TABLE_NAME)
								//							|| updatedTable.getKey().equals(Event.TABLE_NAME)
								|| updatedTable.getKey().equals(EventUpdate.TABLE_NAME)) {

							//Debug
							System.out.println(
									"Si la tabla es business_unit o eventUpdate, actualizamos datos en pantalla");

							// 3 SUPUESTOS
							// 1 - EVENTSELECTED + UPDATESELECTED
							// 2 - EVENTSELECTED + NO UPDATESELECTED
							// 3 - NO EVENTSELECTED + NO UPDATESELECTED

							// 1 - EVENTSELECTED + UPDATESELECTED
							if (eventsTable.getSelectedRow() != -1 && updatesTable.getSelectedRow() != -1) {
								//Buscamos el id de la incidencia seleccionada
								int selectedEventId = (int) eventsTable.getModel()
										.getValueAt(eventsTable.getSelectedRow(), 0);
								//Buscamos el id de la actualización seleccionada
								int selectedUpdateId = (int) updatesTable.getModel()
										.getValueAt(updatesTable.getSelectedRow(), 0);
								//Actualizamos la tabla de incidencias
								filterSelected.doClick();
								//Buscamos la fila de la tabla de incidencias que estaba seleccionada
								int row = 0;
								for (int i = 0; i < eventsTable.getRowCount(); i++) {
									if (selectedEventId == (int) eventsTable.getModel().getValueAt(i, 0)) {
										row = i;
									}
								}
								//Volvemos a seleccionar la fila de la incidencia seleccionada
								//EventTableSelectionListener se encarga de reasignar eventSelected y firstUpdate, de renovar el estado
								//de los botones del panel de incidencias y de actualizar la tabla de actualizaciones de incidencias
								eventsTable.setRowSelectionInterval(row, row);
								//Buscamos la fila de la tabla de actualizaciones que estaba seleccionada
								for (int i = 0; i < updatesTable.getRowCount(); i++) {
									if (selectedUpdateId == (int) updatesTable.getModel().getValueAt(i, 0)) {
										row = i;
									}
								}
								//Volvemos a seleccionar la fila de la actualización seleccionada
								//EventUpdateTableSelectionListener se encarga de reasignar updateSelected y renovar el estado de los botones
								//del panel de actualizaciones
								updatesTable.setRowSelectionInterval(row, row);
							}

							// 2 - EVENTSELECTED + NO UPDATESELECTED
							if (eventsTable.getSelectedRow() != -1 && updatesTable.getSelectedRow() == -1) {
								//Buscamos el id de la incidencia seleccionada
								int selectedEventId = (int) eventsTable.getModel()
										.getValueAt(eventsTable.getSelectedRow(), 0);
								//Actualizamos la tabla de incidencias
								filterSelected.doClick();
								//Buscamos la fila de la tabla de incidencias que estaba seleccionada
								int row = 0;
								for (int i = 0; i < eventsTable.getRowCount(); i++) {
									if (selectedEventId == (int) eventsTable.getModel().getValueAt(i, 0)) {
										row = i;
									}
								}
								//Volvemos a seleccionar la fila de la incidencia seleccionada
								//EventTableSelectionListener se encarga de reasignar eventSelected y firstUpdate, de renovar el estado
								//de los botones del panel de incidencias y de actualizar la tabla de actualizaciones de incidencias
								eventsTable.setRowSelectionInterval(row, row);

							}

							// 3 - NO EVENTSELECTED + NO UPDATESELECTED
							//Si no hay ninguna fila seleccionada en la tabla de incidencias, solo la actualizamos. No hace falta actualizar
							//la tabla de actualizaciones porque no mostraba ningún dato

							if (eventsTable.getSelectedRow() == -1 && updatesTable.getSelectedRow() == -1) {
								filterSelected.doClick();
								//No hay ni incidencias ni actualizaciones seleccionadas
								eventSelected = null;
								updateSelected = null;
							}

							//Informamos por pantalla de la actualización
							//Si las incidencias de la unidad de negocio seleccionada no han sufrido ninguna modificación no habrá ningún cambio en la
							//información mostrada, pero seguirá interesando saber que alguna incidencia ha sido modificada, añadida o borrada
							EventDataUI.this.infoLabel.setText("DATOS DE LAS INCIDENCIAS ACTUALIZADOS: "
									+ ToolBox.formatTimestamp(updatedTable.getValue(), null));	
						}
					}
					
					//Incrementamos timerCycle para entrar en el ciclo 1
					timerCycle = 1;
					
				//Si estamos en el ciclo 1, reseteamos el ciclo	
				} else if (timerCycle == 1) {
					
					//Debug
					System.out.println("Ciclo 1");
					
					infoLabel.setText("");
					timerCycle = 0;
				}
			}
		}
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

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

	public JLabel getInfoLabel() {
		return infoLabel;
	}

	public JRadioButton getFilterSelected() {
		return filterSelected;
	}

	public List<Event> getCurrentEventList() {
		return currentEventList;
	}

	public JRadioButton getAllEvents() {
		return allEvents;
	}

	public static String[] getUpdatesTableHeader() {
		return UPDATES_TABLE_HEADER;
	}

	public JTable getEventsTable() {
		return eventsTable;
	}

	public JTable getUpdatesTable() {
		return updatesTable;
	}

	public static String getEventButtonSet() {
		return EVENT_BUTTON_SET;
	}

	public static String getUpdateButtonSet() {
		return UPDATE_BUTTON_SET;
	}

	public static String getAllDisabled() {
		return ALL_DISABLED;
	}

	public static String getNewEnabled() {
		return NEW_ENABLED;
	}

	public static String getNewEditEnabled() {
		return NEW_EDIT_ENABLED;
	}

	public static String getAllEnabled() {
		return ALL_ENABLED;
	}

	public JButton getDeleteUpdateButton() {
		return deleteUpdateButton;
	}

	public EventUpdate getFirstUpdate() {
		return firstUpdate;
	}

//	public void setFirstUpdate(EventUpdate firstUpdate) {
//		this.firstUpdate = firstUpdate;
//	}

	public void setPanelVisible(boolean panelVisible) {
		this.panelVisible = panelVisible;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public int getEventsRow() {
		return eventsRow;
	}

	public void setEventsRow(int eventsRow) {
		this.eventsRow = eventsRow;
	}

	public int getUpdatesRow() {
		return updatesRow;
	}

	public void setUpdatesRow(int updatesRow) {
		this.updatesRow = updatesRow;
	}

}
