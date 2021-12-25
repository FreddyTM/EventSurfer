package main.java.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import main.java.company.BusinessUnit;
import main.java.company.Company;
import main.java.event.Event;
import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;

public class EventDataUI extends JPanel{
	
	private static final String[] EVENTS_TABLE_HEADER = {"Fecha", "Area", "Tipo", "Título", "Descripción", "Estado"};
	private static final String[] UPDATES_TABLE_HEADER = {"Fecha / Hora", "Actualización", "Autor", "Usuario"};
	
	private CurrentSession session;
	private Timestamp tNow = ToolBox.getTimestampNow();
	//Temporizador de comprobación de cambios en los datos de la sesión
	private Timer timer;
	//Registra si el panel está visible o no
	private boolean panelVisible;
	
	private JTextField companyField;
	private JComboBox comboBox;
	private JCheckBox activeFilterCheckBox;
	private JTable eventsTable;
	private JTable updatesTable;
	
	//Lista de elementos que aparecen en comboBox
	private String[] comboList;

	public EventDataUI(CurrentSession session) {
		this.session = session;
		setLayout(null);
		panelVisible = true;
		
		JTextPane eventDataTxt = new JTextPane();
		eventDataTxt.setText("GESTIÓN DE EVENTOS");
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
		
		JPanel eventsContainer = new JPanel();
		eventsContainer.setLayout(null);
		TitledBorder titledBorder = BorderFactory.createTitledBorder("Eventos");
		eventsContainer.setBorder(titledBorder);
		Border margin = new EmptyBorder(15, 15, 15, 15);
		eventsContainer.setBorder(new CompoundBorder(eventsContainer.getBorder(), margin));
		//Componente de prueba para añadir al panel
		JLabel oneLabel = new JLabel("Texto de prueba");
		oneLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		oneLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		oneLabel.setBounds(50, 50, 200, 25);
		eventsContainer.add(oneLabel);
		eventsContainer.setBounds(50, 225, 900, 200);
		add(eventsContainer);
		
		
		companyField = new JTextField();
		companyField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		companyField.setBackground(UIManager.getColor(new JPanel().getBackground()));
		companyField.setBounds(260, 125, 400, 25);
		companyField.setText(session.getbUnit().getCompany().getNombre());
		companyField.setEditable(false);
//		textFieldList.add(companyField);
//		textFieldContentList.add(session.getbUnit().getCompany().getNombre());
		add(companyField);
		
		activeFilterCheckBox = new JCheckBox(" solo activas");
		activeFilterCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 20));
		activeFilterCheckBox.setBounds(666, 175, 154, 25);
		activeFilterCheckBox.setSelected(session.getbUnit().isActivo() ? true : false);
		activeFilterCheckBox.addItemListener(new CheckBoxListener());
		add(activeFilterCheckBox);
		
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
		
		//Por defecto se muestran los últimos 25 eventos registrados
		buildEventTable(getLastEventsByNumber(session.getbUnit().getEvents(), 25), EVENTS_TABLE_HEADER);
	
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
	 * Devuelve los últimos elementos de una lista. Si la lista tiene menos elementos de los que buscamos, retornamos la lista
	 * sin modificar
	 * @param list lista de la que buscamos los últimos elementos
	 * @param number número de elementos que obtendremos de la lista
	 * @return lista filtrada con los últimos elementos
	 */
	private List<Event> getLastEventsByNumber(List<Event> list, int number) {
		if (list.size() > 25) {
			return list.subList(list.size() - number, list.size() - 1);
		}
		return list;
	}
	
	/**
	 * 
	 * @param list
	 * @param header
	 */
	private void buildEventTable(List<Event> list, String[] header) {
		//Encabezados de la tabla
		Vector<String> headerVector = new Vector<String>(Arrays.asList(header));
		//Datos de la tabla
		Vector<Vector<Object>> dataVector = new Vector<Vector<Object>>();
		for (Event event: session.getbUnit().getEvents()) {
			Vector<Object> eventVector = new Vector<Object>();
			eventVector.add((Integer) event.getId());
			eventVector.add(event.getArea().getArea());
			eventVector.add(event.getEventType());
			eventVector.add(event.getTitulo());
			eventVector.add(event.getDescripcion());
			eventVector.add(event.getEventState());
			dataVector.add(eventVector);
		}
		eventsTable = new JTable(dataVector, headerVector);
		
		//Debug
		System.out.println("Cambiando el contenido de la tabla de eventos");
		
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
			
			//Debug
			System.out.println("BUnit de la sesión: " + session.getbUnit().getNombre());
			
			String item = (String) comboBox.getSelectedItem();
			Company company = session.getCompany();
			//Recuperamos la unidad de negocio seleccionada
			BusinessUnit selectedBunit = new BusinessUnit().getBusinessUnitByName(company, item);			
			//La asignamos a la sesión
			session.setbUnit(selectedBunit);
			//Renovamos la tabla de eventos
			buildEventTable(session.getbUnit().getEvents(), EVENTS_TABLE_HEADER);

			
//			//Mostramos sus datos
//			populateTextFields();
//			//Hacemos backup del contenido de los datos del formulario
//			updateDataCache();
			
			//EN PRINCIPIO NO REACTIVAR
//			//Vaciamos label de información
//			infoLabel.setText("");
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
					buildEventTable(session.getbUnit().getEvents(), EVENTS_TABLE_HEADER);
					
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

}
