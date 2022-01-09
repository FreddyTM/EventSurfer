package main.java.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
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
import main.java.company.User;
import main.java.event.Event;
import main.java.event.EventUpdate;
import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;
import main.java.types_states.TypesStatesContainer;

public class EventDataUI extends JPanel{
	
	private static final String[] EVENTS_TABLE_HEADER = {"ID", "Fecha / Hora", "Area", "Tipo", "Título", "Descripción", "Estado"};
	private static final String[] UPDATES_TABLE_HEADER = {"ID", "Event_ID", "Fecha / Hora", "Actualización", "Autor", "Usuario"};
	private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss";
	
	private CurrentSession session;
	private Selector selector;
	private Timestamp tNow = ToolBox.getTimestampNow();
	//Temporizador de comprobación de cambios en los datos de la sesión
	private Timer timer;
	//Registra si el panel está visible o no
	private boolean panelVisible;
	//Tamaño del monitor que ejecuta la aplicación
	String screenSize = getScreenSize();
	
	private JPanel eventsContainer;
	private JPanel updatesContainer;
	private JPanel filtersContainer;
	private JTable eventsTable;
	private JTable updatesTable;
	private Event eventSelected;
	private EventUpdate updateSelected;
	
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
	
	//Lista de elementos que aparecen en comboBox
	private String[] comboList;
	
	private final Action newEventAction = new NewEventAction();
	private final Action editEventAction = new EditEventAction();
	private final Action deleteEventAction = new DeleteEventAction();
	private final Action newUpdateAction = new NewUpdateAction();
	private final Action editUpdateAction = new EditUpdateAction();
	private final Action deleteUpdateAction = new DeleteUpdateAction();


	public EventDataUI(CurrentSession session, Selector selector) {
		this.session = session;
		this.selector = selector;
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
		editEventButton.setEnabled(false);
		eventsContainer.add(editEventButton);
		
		deleteEventButton = new JButton();
		deleteEventButton.setAction(deleteEventAction);
		deleteEventButton.setBounds(225, 365, 89, 23);
		deleteEventButton.setEnabled(false);
		eventsContainer.add(deleteEventButton);
		
		JLabel totalEventsLabel = new JLabel("Eventos totales: ");
		totalEventsLabel.setHorizontalAlignment(SwingConstants.LEFT);
		totalEventsLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		totalEventsLabel.setBounds(400, 365, 150, 25);
		eventsContainer.add(totalEventsLabel);
		
		JLabel eventsShownLabel = new JLabel("Eventos mostrados: ");
		eventsShownLabel.setHorizontalAlignment(SwingConstants.LEFT);
		eventsShownLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		eventsShownLabel.setBounds(650, 365, 200, 25);
		eventsContainer.add(eventsShownLabel);
		
		totalEvents = new JLabel();
		totalEvents.setText(((Integer)session.getbUnit().getEvents().size()).toString());
		totalEvents.setHorizontalAlignment(SwingConstants.LEFT);
		totalEvents.setFont(new Font("Tahoma", Font.PLAIN, 20));
		totalEvents.setBounds(550, 365, 50, 25);
		eventsContainer.add(totalEvents);
		
		eventsShown = new JLabel();
		eventsShown.setText(((Integer)getLastEventsByNumber(session.getbUnit().getEvents(), 25).size()).toString());
		eventsShown.setHorizontalAlignment(SwingConstants.LEFT);
		eventsShown.setFont(new Font("Tahoma", Font.PLAIN, 20));
		eventsShown.setBounds(830, 365, 50, 25);
		eventsContainer.add(eventsShown);
		
		//This code to windowlistener
		
//		JFrame parentFrame = (JFrame) SwingUtilities.getRoot((Component) EventDataUI.this);
//		System.out.println(parentFrame == null);
//		
//		GraphicsDevice [] displays = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
//		GraphicsDevice currentDisplay = parentFrame.getGraphicsConfiguration().getDevice();
//		System.out.println(currentDisplay.getDefaultConfiguration().getBounds().x);
		
//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//		System.out.println(screenSize.height + " " + screenSize.width);
//		if (screenSize.width > 1024) {
//			eventsContainer.setBounds(50, 225, 1400, 400);
//		} else {
//			eventsContainer.setBounds(50, 225, 1100, 400);
//		}
		
//		eventsContainer.setBounds(50, 225, 1100, 400);
		
	
		updatesContainer = new JPanel();
		updatesContainer.setLayout(null);
		TitledBorder titledBorder2 = BorderFactory.createTitledBorder("Actualizaciones");
		updatesContainer.setBorder(titledBorder2);
		Border margin2 = new EmptyBorder(15, 15, 15, 15);
		updatesContainer.setBorder(new CompoundBorder(updatesContainer.getBorder(), margin2));
		
		newUpdateButton = new JButton();
		newUpdateButton.setAction(newUpdateAction);
		newUpdateButton.setBounds(25, 265, 89, 23);
		newUpdateButton.setEnabled(false);
		updatesContainer.add(newUpdateButton);
		
		editUpdateButton = new JButton();
		editUpdateButton.setAction(editUpdateAction);
		editUpdateButton.setBounds(125, 265, 89, 23);
		editUpdateButton.setEnabled(false);
		updatesContainer.add(editUpdateButton);
		
		deleteUpdateButton = new JButton();
		deleteUpdateButton.setAction(deleteUpdateAction);
		deleteUpdateButton.setBounds(225, 265, 89, 23);
		deleteUpdateButton.setEnabled(false);
		updatesContainer.add(deleteUpdateButton);
		
		filtersContainer = new JPanel();
		filtersContainer.setLayout(null);
//		filtersContainer.setBackground(Color.CYAN);
		ButtonGroup filterGroup = new ButtonGroup();
		
		allEvents.setFont(new Font("Tahoma", Font.PLAIN, 20));
		allEvents.setBounds(0, 0, 200, 25);
		allEvents.setText("Todos");
		filterGroup.add(allEvents);
		filtersContainer.add(allEvents);
		
		last25.setFont(new Font("Tahoma", Font.PLAIN, 20));
		last25.setBounds(0, 40, 200, 25);
		filterGroup.add(last25);
		last25.setSelected(true);
		filtersContainer.add(last25);
		
		last50.setFont(new Font("Tahoma", Font.PLAIN, 20));
		last50.setBounds(0, 80, 200, 25);
		filterGroup.add(last50);
		filtersContainer.add(last50);
		
		thisMonth.setFont(new Font("Tahoma", Font.PLAIN, 20));
		thisMonth.setBounds(0, 120, 200, 25);
		filterGroup.add(thisMonth);
		filtersContainer.add(thisMonth);
		
		last3Months.setFont(new Font("Tahoma", Font.PLAIN, 20));
		last3Months.setBounds(0, 160, 200, 25);
		filterGroup.add(last3Months);
		filtersContainer.add(last3Months);
		
		last6Months.setFont(new Font("Tahoma", Font.PLAIN, 20));
		last6Months.setBounds(0, 200, 200, 25);
		filterGroup.add(last6Months);
		filtersContainer.add(last6Months);
		
		thisYear.setFont(new Font("Tahoma", Font.PLAIN, 20));
		thisYear.setBounds(0, 240, 200, 25);
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
		
		infoLabel = new JLabel("Texto de prueba");
		infoLabel.setHorizontalAlignment(SwingConstants.LEFT);
		infoLabel.setBounds(50, 965, 900, 25);
		add(infoLabel);
		
		//Por defecto se muestran las últimas 25 incidencias registradas
//		buildEventTable(getLastEventsByNumber(session.getbUnit().getEvents(), 25), EVENTS_TABLE_HEADER);
		buildEventTable(getLastEventsByNumber(sortEventsByDate(session.getbUnit().getEvents()), 25), EVENTS_TABLE_HEADER);
		JScrollPane eventsPane = new JScrollPane(eventsTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);		
//		//Scroll al final de la tabla
		eventsTable.scrollRectToVisible(eventsTable.getCellRect(eventsTable.getRowCount()-1, 0, true));
		eventsPane.setBounds(25, 25, eventsContainer.getBounds().width - 250, 325);
		eventsContainer.add(eventsPane);
	
		//Por defecto la tabla de actualizaciones aparecerá vacía. Solo se llena cuando se selecciona un evento
		buildUpdatesTable(new ArrayList<EventUpdate>(), UPDATES_TABLE_HEADER);
		JScrollPane updatesPane = new JScrollPane(updatesTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		updatesTable.scrollRectToVisible(updatesTable.getCellRect(updatesTable.getRowCount()-1, 0, true));
		updatesPane.setBounds(25, 25, updatesContainer.getBounds().width - 50, 225);
		updatesContainer.add(updatesPane);
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
			eventsShown.setText(((Integer)list.size()).toString());
			return list;
		} else {
			//Debug
			System.out.println("Filtering...");
			
			List<Event> sublist = new ArrayList<Event>();
			sublist = list.subList(list.size() - number, list.size());
			eventsShown.setText(((Integer)sublist.size()).toString());
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
//	    calendarReference.add(Calendar.MONTH, Math.negateExact(months));
	    Calendar eventReference = Calendar.getInstance();
	    if (months >= 0) {
	    	switch (months) {
	    		case 0:
	    			for (Event event : list) {
	    				eventReference.setTimeInMillis(event.getUpdates().get(0).getFechaHora().getTime());
	    				if (calendarReference.get(Calendar.MONTH) == eventReference.get(Calendar.MONTH)
	    						&& calendarReference.get(Calendar.YEAR) == eventReference.get(Calendar.YEAR)) {
	    					filteredList.add(event);
	    				}
	    			}
	    			break;
	    		case 3:
	    		case 6:
	    			//orden descendente
//	    			for (int i = 0; i < months; i++) {
	    			//Orden ascendente
	    			months = - months + 1;
	    			
	    			//Debug
	    			System.out.println("months " + months);
	    			
	    			for (int i = months; i <= 0; i++) { //orden ascendente
	    				calendarReference = Calendar.getInstance();
	    				calendarReference.add(Calendar.MONTH, i);
	    				
	    				//Debug
	    				System.out.println("i " + i);
	    				System.out.println("calendarReference " + calendarReference.get(Calendar.MONTH));
	    				
	    				for (Event event : list) {
		    				eventReference.setTimeInMillis(event.getUpdates().get(0).getFechaHora().getTime());
		    				if (calendarReference.get(Calendar.MONTH) == eventReference.get(Calendar.MONTH)
		    						&& calendarReference.get(Calendar.YEAR) == eventReference.get(Calendar.YEAR)) {
		    					filteredList.add(event);
		    				}
		    			}
	    			}
	    			break;
	    		case 12:
	    			for (Event event : list) {
	    				eventReference.setTimeInMillis(event.getUpdates().get(0).getFechaHora().getTime());
	    				if (calendarReference.get(Calendar.YEAR) == eventReference.get(Calendar.YEAR)) {
	    					filteredList.add(event);
	    				}
	    			}
	    			break;
	    		default:
	    			System.out.println("Error de filtrado, criterio no previsto");
	    	}
	    }
	    eventsShown.setText(((Integer)filteredList.size()).toString());
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
		return list;
	}
	
	/**
	 * Ordena una lista de actualizaciones pasada por parámetro en orden ascendente según el criterio de EventUpdateComparator
	 * (fecha de creación la actualización) 
	 * @param list lista a ordenar
	 * @return lista ordenada
	 */
	private List<EventUpdate> sortEventUpdatesByDate (List<EventUpdate> list) {
		List<EventUpdate> sortedList = list;
		sortedList.sort(new EventUpdateComparator());
		return list;
	}
	
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
				updatesContainer.setBounds(50, 650, 1075, 300);
				break;
			case "medium":
				eventsContainer.setBounds(50, 225, 1175, 400);
				updatesContainer.setBounds(50, 650, 1175, 300);
				break;
			case "big":
				eventsContainer.setBounds(50, 225, 1675, 400);
				updatesContainer.setBounds(50, 650, 1675, 300);
		}
//		filtersContainer.setBounds(eventsContainer.getBounds().width - 250, 25, 225, 300);
		filtersContainer.setBounds(eventsContainer.getBounds().width - 200, 25, 175, 325);
	}
	
	/**
	 * Construye la tabla de incidencias con los datos y el formato deseados
	 * @param list lista de incidencias 
	 * @param header encabezados de las columnas de la tabla
	 */
	private void buildEventTable(List<Event> list, String[] header) {
		//Encabezados de la tabla
		Vector<Object> headerVector = new Vector<Object>(Arrays.asList(header));
		//Datos de la tabla
		Vector<Vector<Object>> dataVector = new Vector<Vector<Object>>();
		for (Event event: list) {
			Vector<Object> eventVector = new Vector<Object>();
			eventVector.add((Integer) event.getId());
			eventVector.add(event.getUpdates().get(0).getFechaHora());
			eventVector.add(event.getArea().getArea());
			eventVector.add(event.getEventType());
			eventVector.add(event.getTitulo());
			eventVector.add(event.getDescripcion());
			eventVector.add(event.getEventState());
			dataVector.add(eventVector);
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
					 
//					 //Debug
//					 System.out.println("row " + row + ", column " + col);

					 if (getValueAt(row, col).getClass() == Timestamp.class) {
						 //setToolTipText("<html><p width=\"500\">" +value+"</p></html>");
						 
						 jcomp.setToolTipText(ToolBox.formatTimestamp((Timestamp)getValueAt(row, col), DATE_TIME_PATTERN));
					 } else {
//						 //Debug
//						 System.out.println(getValueAt(row, col).toString() + " - " + dataLength);
						 						 
						 //Multi-line tooltips if needed
						 dataLength = getValueAt(row, col).toString().length(); 
//						 jcomp.setToolTipText("<html><p width=\"" + dataLength + "\">" + getValueAt(row, col).toString() + "</p></html>");
						 jcomp.setToolTipText(dataLength < 500 ? getValueAt(row, col).toString()
								 : "<html><p width=\"500\">" + getValueAt(row, col).toString() + "</p></html>");
					 }
				 }
				 return comp;
			}
		};
		
		//Restringimos la selección de la tabla a una única fila
		eventsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//Obtenemos la incidencia que corresponde a la fila de la tabla seleccionada
		eventsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent e) {
	            
//	        	DefaultListSelectionModel sourceTable = (DefaultListSelectionModel) e.getSource();
	        	
	        	if (!e.getValueIsAdjusting()) {
					//Debug
					System.out.print("Tabla incidencias, fila seleccionada :" + eventsTable.getSelectedRow() + " - ");
					if (eventsTable.getSelectedRow() > -1) {
						int eventSelectedID = (Integer) eventsTable.getModel().getValueAt(eventsTable.getSelectedRow(),	0);
						eventSelected = new Event().getEventById(session.getbUnit(), eventSelectedID);
						//Debug
						System.out.println(eventSelected.getId() + " " + eventSelected.getDescripcion());
						enableOrDisableEditDeleteEventButtons();
						//					//Deseleccionamos cualquier actualización que pudiera estar seleccionada en la tabla de actualizaciones
						//					updatesTable.clearSelection();

						//Debug
						System.out.println(eventSelected.getUpdates().get(0).getId());
						System.out.println(eventSelected.getUpdates().get(0).getEvent().getId());
						System.out.println(eventSelected.getUpdates().get(0).getFechaHora());
						System.out.println(eventSelected.getUpdates().get(0).getDescripcion());
						System.out.println(eventSelected.getUpdates().get(0).getAutor());
						if (eventSelected.getUpdates().get(0).getUser().getUserAlias() != null) {
							System.out.println(eventSelected.getUpdates().get(0).getUser().getUserAlias());
						}

					} 
					//Obtenemos las actualizaciones del evento seleccionado y las mostramos en la tabla de actualizaciones
					updateUpdatesTable(sortEventUpdatesByDate(eventSelected.getUpdates()), UPDATES_TABLE_HEADER);
				}
				//Tras renovar la tabla de actualizaciones, solo el botón de nueva actualización queda habilitado
	            newUpdateButton.setEnabled(true);
	            editUpdateButton.setEnabled(false);
	            deleteUpdateButton.setEnabled(false);
	        }
	    });
		eventsTable.setFillsViewportHeight(true);
		eventsTable.setAutoCreateRowSorter(true);
		formatEventTable();
		
//		First remove the column from the view
//			table.removeColumn(table.getColumnModel().getColumn(4));
//		Then retrieve the data from the model.
//			table.getModel().getValueAt(table.getSelectedRow(),4);
		
		//Debug
		System.out.println("Creando el contenido de la tabla de eventos");
		
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
		for (Event event: list) {
			Vector<Object> eventVector = new Vector<Object>();
			eventVector.add((Integer) event.getId());
			eventVector.add(event.getUpdates().get(0).getFechaHora());
			eventVector.add(event.getArea().getArea());
			eventVector.add(event.getEventType());
			eventVector.add(event.getTitulo());
			eventVector.add(event.getDescripcion());
			eventVector.add(event.getEventState());
			dataVector.add(eventVector);
		}
		DefaultTableModel model = new DefaultTableModel(dataVector, headerVector);
		eventsTable.setModel(model);
		formatEventTable();
		eventsTable.repaint();
		newUpdateButton.setEnabled(false);
		editUpdateButton.setEnabled(false);
		deleteUpdateButton.setEnabled(false);
	}
	
	/**
	 * Dimensiona el ancho de las columnas de la tabla de incidencias y aplica el formato establecido por la clase
	 * EventTableCellRenderer para las columnas Fecha / Hora y Estado
	 */
	private void formatEventTable() {
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
					 
//					 //Debug
//					 System.out.println("row " + row + ", column " + col);

					 if (getValueAt(row, col).getClass() == Timestamp.class) {
						 //setToolTipText("<html><p width=\"500\">" +value+"</p></html>");
						 
						 jcomp.setToolTipText(ToolBox.formatTimestamp((Timestamp)getValueAt(row, col), DATE_TIME_PATTERN));
					 } else {
//						 //Debug
//						 System.out.println(getValueAt(row, col).toString() + " - " + dataLength);
						 						 
						 //Multi-line tooltips if needed
						 dataLength = getValueAt(row, col).toString().length(); 
//						 jcomp.setToolTipText("<html><p width=\"" + dataLength + "\">" + getValueAt(row, col).toString() + "</p></html>");
						 jcomp.setToolTipText(dataLength < 600 ? getValueAt(row, col).toString()
								 : "<html><p width=\"600\">" + getValueAt(row, col).toString() + "</p></html>");
					 }
				 }
				 return comp;
			}
		};
		
		//Restringimos la selección de la tabla a una única fila
		updatesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//Obtenemos la actualización que corresponde a la fila de la tabla seleccionada
		updatesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent e) {
	            
	        	//Debug
	        	System.out.print("Tabla actualizaciones, fila seleccionada :" + updatesTable.getSelectedRow() + " - ");
	            
	            if (updatesTable.getSelectedRow() > -1) {
					int updateID = (Integer) updatesTable.getModel().getValueAt(updatesTable.getSelectedRow(), 0);
					updateSelected = new EventUpdate().getEventUpdateById(eventSelected, updateID);
					
					//Debug
					System.out.println(updateSelected.getId() + " " + updateSelected.getDescripcion());
					
					enableOrDisableEditDeleteEventUpdateButtons(updatesTable.getSelectedRow());
				}       
	        }
		});
		updatesTable.setFillsViewportHeight(true);
		updatesTable.setAutoCreateRowSorter(true);
		formatUpdatesTable();
		
//		First remove the column from the view
//			table.removeColumn(table.getColumnModel().getColumn(4));
//		Then retrieve the data from the model.
//			table.getModel().getValueAt(table.getSelectedRow(),4);
		
		//Debug
		System.out.println("Creando el contenido de la tabla de eventos");
		
	}
	
	/**
	 * Actualiza la tabla de actualizaciones con los datos y el formato deseados
	 * @param list lista de actualizaciones del evento seleccionado
	 * @param header encabezados de las columnas de la tabla
	 */
	private void updateUpdatesTable(List<EventUpdate> list, String[] header) {
		//Encabezados de la tabla
		Vector<Object> headerVector = new Vector<Object>(Arrays.asList(header));
		//Datos de la tabla
		Vector<Vector<Object>> dataVector = new Vector<Vector<Object>>();
		for (EventUpdate update: list) {
			
			//Debug
			System.out.println("La lista contiene " + list.size() + " elementos");
			
						
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
	}
	
	/**
	 * Dimensiona el ancho de las columnas de la tabla de actualizaciones y aplica el formato establecido por la clase
	 * UpdatesTableCellRenderer para la columna Fecha / Hora
	 */
	private void formatUpdatesTable() {
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
		
		//Scroll al final de la tabla
		updatesTable.scrollRectToVisible(updatesTable.getCellRect(updatesTable.getRowCount()-1, 0, true));
	}
	
	
	/**
	 * Retorna un timestamp a partir de un string que sigue el formato de DATE_TIME_PATTERN
	 * @param stringToParse string que contiene la fecha y la hora a transformar en un objeto Timestamp
	 * @return Timestamp con la fecha y la hora pasadas por parámetro
	 */
	private Timestamp stringToTimestamp(String stringToParse) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
		LocalDateTime ldt = LocalDateTime.from(formatter.parse(stringToParse));
		return Timestamp.valueOf(ldt);
	}
	
	/**
	 * Habilita los botones de editar y borrar incidencias en caso de que el usuario de la sesión sea de tipo ADMIN
	 * o MANAGER. Si el usuario de la sesión es de tipo USER, habilita los botones de editar y borrar incidencias
	 * en caso de que la incidencia haya sido creada por él. En caso contrario, los botones editar y borrar se 
	 * deshabilitan.
	 */
	private void enableOrDisableEditDeleteEventButtons() {
		//Usuario de sesión de tipo USER
		if (session.getUser().getUserType().equals("USER")) {
			if(eventSelected.getUpdates().get(0).getUser().getId() == session.getUser().getId()) {
				editEventButton.setEnabled(true);
				deleteEventButton.setEnabled(true);
			} else {
				editEventButton.setEnabled(false);
				deleteEventButton.setEnabled(false);
			}
		//Usuarios ADMIN y MANAGER
		} else {
			editEventButton.setEnabled(true);
			deleteEventButton.setEnabled(true);
		}
	}
	
	/**
	 * Deshabilita el botón de borrar actualizaciones con cualquier tipo de usuario si la actualización
	 * seleccionada es la actualización inicial (la que se crea siempre con la creación de una nueva incidencia).
	 * Habilita el botón de borrar con cualquier actualización que no sea la actualización inicial en caso de que
	 * el usuario de la sesión sea de tipo ADMIN o MANAGER. Habilita el botón de editar con cualquier actualización
	 * en caso de que el usuario de la sesión sea de tipo ADMIN o MANAGER. Si el usuario de la sesión es de tipo USER,
	 * habilita los botones de editar y borrar actualizaciones en caso de que la actualización haya sido creada por él
	 * (exceptuando la actualización inicial). En caso contrario, los botones editar y borrar se deshabilitan.
	 * @param selectedRow fila de la tabla de actualizaciones seleccionada
	 */
	private void enableOrDisableEditDeleteEventUpdateButtons(int selectedRow) {
		if (selectedRow == 0 && !session.getUser().getUserType().equals("USER")) {
			//Debug
			System.out.println("La actualización inicial no se puede borrar");
			editUpdateButton.setEnabled(true);
			deleteUpdateButton.setEnabled(false);
//			return;
		}
		
		if (selectedRow != 0 && !session.getUser().getUserType().equals("USER")) {
			editUpdateButton.setEnabled(true);
			deleteUpdateButton.setEnabled(true);
		}
		
		if (selectedRow != 0 && session.getUser().getUserType().equals("USER")) {
			//Debug
			System.out.println("Usuario user");
			
			if (updateSelected.getUser().getId() == session.getUser().getId()) {
				
				//Debug
				System.out.println("El usuario user ha creado la actualización");
				
				//Debug
				System.out.println("id del usuario de la actualización: " + eventSelected.getUpdates().get(0).getUser().getId());
				System.out.println("id del usuario de la sesión: " + session.getUser().getId());
				
				
				editUpdateButton.setEnabled(true);
				deleteUpdateButton.setEnabled(true);
			} else {
				
				//Debug
				System.out.println("El usuario user no ha creado la actualización");
				
				editUpdateButton.setEnabled(false);
				deleteUpdateButton.setEnabled(false);
			}
		}

//		//Usuario de sesión de tipo USER
//		if (session.getUser().getUserType().equals("USER")) {
//			
//			//Debug
//			System.out.println("Usuario user");
//			
//			if (updateSelected.getUser().getId() == session.getUser().getId()) {
//				
//				//Debug
//				System.out.println("El usuario user ha creado la actualización");
//				
//				//Debug
//				System.out.println("id del usuario de la actualización: " + eventSelected.getUpdates().get(0).getUser().getId());
//				System.out.println("id del usuario de la sesión: " + session.getUser().getId());
//				
//				
//				editUpdateButton.setEnabled(true);
//				deleteUpdateButton.setEnabled(true);
//			} else {
//				
//				//Debug
//				System.out.println("El usuario user no ha creado la actualización");
//				
//				editUpdateButton.setEnabled(false);
//				deleteUpdateButton.setEnabled(false);
//			}
//			//Usuarios ADMIN y MANAGER
//		} else {
//			
//			//Debug
//			System.out.println("No es un usuario user");
//			
//			editUpdateButton.setEnabled(true);
//			deleteUpdateButton.setEnabled(true);
//		} 
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
				//Debug
				for (User user : session.getbUnit().getUsers()) {
					System.out.println(user.getUserAlias());
				}
				//Renovamos la tabla de eventos
				updateEventTable(getLastEventsByNumber(session.getbUnit().getEvents(), 25), EVENTS_TABLE_HEADER);
				//Seleccionamos el filtro
				last25.setSelected(true);
				//Actualizamos el número total de incidencias de la unidad de negocio seleccionada
				totalEvents.setText(((Integer) session.getbUnit().getEvents().size()).toString());
				//Vaciamos la tabla de actualizaciones
				updateUpdatesTable(new ArrayList<EventUpdate>(), UPDATES_TABLE_HEADER);
			
//				//Mostramos sus datos
//				populateTextFields();
//				//Hacemos backup del contenido de los datos del formulario
//				updateDataCache();
			
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
					
					//Debug
					System.out.println("BUnit de la sesión: " + session.getbUnit().getNombre());
					
					//Mostramos sus datos
//					populateTextFields();
//					//Hacemos backup del contenido de los datos del formulario
//					updateDataCache();
					
					//Renovamos la lista de las unidades de negocio del comboBox
					refreshComboBox();
					//Renovamos la tabla de eventos
					updateEventTable(getLastEventsByNumber(session.getbUnit().getEvents(), 25), EVENTS_TABLE_HEADER);
					//Seleccionamos el filtro
					last25.setSelected(true);
					//Actualizamos el número total de incidencias de la unidad de negocio seleccionada
					totalEvents.setText(((Integer)session.getbUnit().getEvents().size()).toString());
					//Vaciamos la tabla de actualizaciones
					updateUpdatesTable(new ArrayList<EventUpdate>(), UPDATES_TABLE_HEADER);
					
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
	 * Listener que dispara el filtrado de incidencias
	 */
	private class EventFilterListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (e.getSource() == allEvents) {
				//Todas las incidencias de la unidad de negocio de la sesión
				updateEventTable(sortEventsByDate(session.getbUnit().getEvents()), EVENTS_TABLE_HEADER);
				eventsShown.setText(((Integer)session.getbUnit().getEvents().size()).toString());
				
				//Debug
				System.out.println("Listener All");

			} else if (e.getSource() == last25) {
				//Últimas 25 incidencias
				//Son las últimas por fecha de creación de la incidencia, no necesariamente las últimas 25 registradas, ya que se
				//pueden crear incidencias de fechas anteriores a la fecha actual
				updateEventTable(getLastEventsByNumber(sortEventsByDate(session.getbUnit().getEvents()), 25), EVENTS_TABLE_HEADER);
				
				//Debug
				System.out.println("Listener 25");
				
			} else if (e.getSource() == last50) {
				//Últimas 50 incidencias
				//Son las últimas por fecha de creación de la incidencia, no necesariamente las últimas 25 registradas, ya que se
				//pueden crear incidencias de fechas anteriores a la fecha actual
				updateEventTable(getLastEventsByNumber(sortEventsByDate(session.getbUnit().getEvents()), 50), EVENTS_TABLE_HEADER);
				
				//Debug
				System.out.println("Listener 50");
				
			} else if (e.getSource() == thisMonth) {
				//Incidencias del mes actual
				updateEventTable(getLastEventsByDate(session.getbUnit().getEvents(), 0), EVENTS_TABLE_HEADER);
				
				//Debug
				System.out.println("Listener thisMonth. List size: " + getLastEventsByDate(session.getbUnit().getEvents(), 0).size());
				
			} else if (e.getSource() == last3Months) {
				//Incidencias del mes actual y 2 meses más hacia atrás
				updateEventTable(getLastEventsByDate(session.getbUnit().getEvents(), 3), EVENTS_TABLE_HEADER);
				
				//Debug
				System.out.println("Listener last3Months. List size: " + getLastEventsByDate(session.getbUnit().getEvents(), 3).size());
				
			} else if (e.getSource() == last6Months) {
				//Incidencias del mes actual y 5 meses más hacia atrás
				updateEventTable(getLastEventsByDate(session.getbUnit().getEvents(), 6), EVENTS_TABLE_HEADER);
				
				//Debug
				System.out.println("Listener last6Months. List size: " + getLastEventsByDate(session.getbUnit().getEvents(), 6).size());
				
			} else if (e.getSource() == thisYear) {
				//Incidencias del año actual
				updateEventTable(getLastEventsByDate(session.getbUnit().getEvents(), 12), EVENTS_TABLE_HEADER);
				
				//Debug
				System.out.println("Listener thisYear. List size: " + getLastEventsByDate(session.getbUnit().getEvents(), 12).size());
				
			}
			//Vaciamos la tabla de actualizaciones
			updateUpdatesTable(new ArrayList<EventUpdate>(), UPDATES_TABLE_HEADER);
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
			return o1.getUpdates().get(0).getFechaHora().compareTo(o2.getUpdates().get(0).getFechaHora());
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
	
	private class NewEventAction extends AbstractAction {
		public NewEventAction() {
			putValue(NAME, "Nueva");
			putValue(SHORT_DESCRIPTION, "Add new event");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class EditEventAction extends AbstractAction {
		public EditEventAction() {
			putValue(NAME, "Editar");
			putValue(SHORT_DESCRIPTION, "Enable event edit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class DeleteEventAction extends AbstractAction {
		public DeleteEventAction() {
			putValue(NAME, "Borrar");
			putValue(SHORT_DESCRIPTION, "Delete event");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class NewUpdateAction extends AbstractAction {
		public NewUpdateAction() {
			putValue(NAME, "Nueva");
			putValue(SHORT_DESCRIPTION, "Add new update");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class EditUpdateAction extends AbstractAction {
		public EditUpdateAction() {
			putValue(NAME, "Editar");
			putValue(SHORT_DESCRIPTION, "Enable update edit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class DeleteUpdateAction extends AbstractAction {
		public DeleteUpdateAction() {
			putValue(NAME, "Borrar");
			putValue(SHORT_DESCRIPTION, "Delete update");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
	}

}
