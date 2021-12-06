package main.java.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import main.java.company.Area;
import main.java.event.Event;
import main.java.persistence.PersistenceManager;
import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;
import main.java.types_states.EventType;
import main.java.types_states.TypesStatesContainer;

public class EventTypeUI extends JPanel {

	//Se asignan a la variable okActionSelector para determinar el comportamiento
	//de la acción okAction
	private static final int OK_ACTION_UNDEFINED = 0; //Por defecto
	private static final int OK_ACTION_EDIT = 1;
	private static final int OK_ACTION_NEW = 2;
	
	private static final String NO_EVENT_TYPE = "<Ningún tipo de evento seleccionable>";
	private static final String DIALOG_INFO = "info";
	private static final String DIALOG_YES_NO = "yes_no";
	
	private CurrentSession session;
	private Timestamp tNow = ToolBox.getTimestampNow();
	//Temporizador de comprobación de cambios en los datos de la sesión
	private Timer timer;
	//Registra si el panel está visible o no
	private boolean panelVisible;
	
	//Elementos que aparecerán en la lista de tipos de eventos
	private String[] registeredEventTypes = getEventTypesFromSession();
	//Registra el tipo de evento seleccionado en cada momento
	private String selectedEventType;
	//Backup del tipo de evento seleccionado
	private String selectedEventTypeBackup;
	//Registra el índice del elemento seleccionado en la lista
	private int itemSelectedIndex = 0;
	//Backup del índice seleccionado en la lista
	private int itemSelectedBackupIndex = itemSelectedIndex;
	
	//Elementos gráficos
	private JTextField eventTypeNameField = new JTextField();
	//Etiqueta informativa de longitud máxima de datos
	private JLabel maxCharsLabel = new JLabel("Max: 100 caracteres");
	private JLabel infoLabel;
	private JButton editButton = new JButton();
	private JButton cancelButton;
	private JButton oKButton;
	private JButton newButton;
	private JButton deleteButton = new JButton();
	
	//Modelo de datos de la lista de tipos de eventos
	private DefaultListModel<String> registeredModel = new DefaultListModel<String>();
	//Lista de tipos de evento
	private JList<String> registeredList;
	//Contenedor de la lista de tipos de evento
	private JScrollPane registeredScrollPane;
	//Registra si la lista de tipos de evento ha sido modificada
	private boolean listModified = false;
	
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
		eventTypeNameField.setText(registeredEventTypes[0].equals(NO_EVENT_TYPE) ? null : registeredEventTypes[0]);
		eventTypeNameField.setEditable(false);
		add(eventTypeNameField);
		
		selectedEventType = eventTypeNameField.getText();
		selectedEventTypeBackup = selectedEventType;
		
		//Debug
		System.out.println("Constructor. Tipo de evento seleccionado: " + selectedEventType);
		System.out.println("Constructor. Backup tipo de evento seleccionado: " + selectedEventType);
		
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
		
		for (String item : registeredEventTypes) {
			registeredModel.addElement(item);
		}
		
		registeredList = new JList<String>(registeredModel);
		registeredList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		registeredList.setLayoutOrientation(JList.VERTICAL);		
		registeredList.setVisibleRowCount(8);
		registeredList.setFont(new Font("Tahoma", Font.PLAIN, 15));
		registeredList.setSelectedIndex(itemSelectedIndex);
		registeredList.addListSelectionListener(new RegisteredListener());
		registeredScrollPane = new JScrollPane(registeredList);
		registeredScrollPane.setBounds(100, 350, 300, 200);
		add(registeredScrollPane);
		
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
		timer = new Timer();
		TimerTask task = new TimerJob();
		timer.scheduleAtFixedRate(task, 1000, 30000);
	}
	
	/**
	 * Obtiene la lista de tipos de evento registrados en la sesión abierta
	 * @return lista de tipos de evento registrados. Si no hay ninguno, la lista incluye solo NO_EVENT_TYPE 
	 */
	private String[] getEventTypesFromSession() {
		String[] itemList = TypesStatesContainer.getEvType().getEventTypesArray();
		if (itemList.length == 0) {
			itemList = new String[1];
			itemList[0] = NO_EVENT_TYPE;
			return itemList;
		}
		return itemList;
	}
	
	/**
	 * Si el usuario de la sesión es de tipo manager, habilita la edición del tipo de evento seleccionado solo en el caso
	 * de que esté asignado exclusivamente a eventos registrados en la misma unidad de negocio que dicho usuario, o bien que
	 * no esté asignado a ningún evento
	 * @return true si se cumplen las condiciones para la edición, false si no se cumplen
	 */
	private boolean verifyManagerEditConditions() {
		int selectedEventTypeId = TypesStatesContainer.getEvType().getEventTypeId(selectedEventType);
		List<Integer> bUnitsList = new Event().getBunitsIdsWithEventTypes(session.getConnection(), selectedEventTypeId);
		String action = "";
		//Si estamos editando el tipo de evento
		if (okActionSelector == EventTypeUI.OK_ACTION_EDIT) {
			action = "editar";
		//Si queremos borrar el tipo de evento
		} else {
			action = "borrar";
		}
		//Tipo de evento no registrado en eventos de ninguna unidad de negocio
		if (bUnitsList.size() == 0) {
			return true;
		}
		//Tipo de evento registrado en eventos de más de una unidad de negocio
		if (bUnitsList.size() > 1) {
			ToolBox.showDialog(
					"Un usuario Manager no puede " + action + " tipos de eventos registrados en eventos de varias unidades de negocio",
					EventTypeUI.this, DIALOG_INFO);
			return false;
		}
		//Tipo de evento registrado en eventos de la unidad de negocio de usuario manager que abre sesión
		if (bUnitsList.size() == 1 && session.getUser().getbUnit().getId() == bUnitsList.get(0)) {
			return true;
		//Tipo de evento registrado en eventos de una unidad de negocio distinta a la del usuario manager que abre sesión
		} else {
			ToolBox.showDialog(
					"Un usuario Manager no puede " + action + " tipos de evento registrados en eventos de unidades de negocio distintas a la suya",
					EventTypeUI.this, DIALOG_INFO);
			return false;
		}
	}
	
	/**
	 * Si el usuario de la sesión es de tipo admin y el tipo de evento seleccionado está registrado en eventos de más
	 * de una unidad de negocio, advierte al usuario de esta circunstancia. Si el usuario acepta continuar, se habilita
	 * la edición del tipo de evento seleccionado. También se habilita la edición directamente en el caso de que el
	 * tipo de evento seleccionado esté registrado en eventos de una sola unidad de negocio o de ninguna. 
	 * @return true si se cumplen las condiciones para la edición, false si no se cumplen
	 */
	private boolean verifyAdminEditConditions() {
		int selectedEventTypeId = TypesStatesContainer.getEvType().getEventTypeId(selectedEventType);
		List<Integer> bUnitsList = new Event().getBunitsIdsWithEventTypes(session.getConnection(), selectedEventTypeId);
		String info = "";
		//Si estamos editando el tipo de evento
		if (okActionSelector == EventTypeUI.OK_ACTION_EDIT) {
			info = "Edición de tipo de evento registrado en más de una unidad de negocio. ¿Desea continuar?";
		//Si queremos borrar el tipo de evento 
		} else {
			info = "Borrado de tipo de evento registrado en más de más de una unidad de negocio. "
					+ "No se puede deshacer. " + "¿Desea continuar?";
		}
		//Tipo de evento registrado en eventos de más de una unidad de negocio
		if (bUnitsList.size() > 1) {
			int optionSelected = ToolBox.showDialog(
					info, EventTypeUI.this,
					DIALOG_YES_NO);
			if (optionSelected == JOptionPane.YES_OPTION) {
				//Debug
				System.out.println("Edición o borrado OK");
				return true;
			} else {
				//Debug
				System.out.println("Edición o borrado cancelado");
				return false;
			}
		//Tipo de evento registrado en eventos de una o ninguna unidad de negocio
		} else {
			if (okActionSelector == EventTypeUI.OK_ACTION_EDIT) {
				return true;
			} else {	
				int optionSelected = ToolBox.showDialog(
						"El borrado de tipos de eventos no se puede deshacer. ¿Desea continuar?", EventTypeUI.this,
						DIALOG_YES_NO);
				if (optionSelected == JOptionPane.YES_OPTION) {
					//Debug
					System.out.println("Borrado OK");
					return true;
				} else {							
					//Debug
					System.out.println("Borrado cancelado");
					return false;
				}
			}
		}
	}
	
	/**
	 * Comprueba la corrección de los datos introducidos en el formulario. Cualquier dato incorrecto se resalta
	 * con el fondo del campo en amarillo
	 * @return true si son correctos, false si no lo son
	 */
	private boolean testData () {
		//Comprobamos que los datos no exceden el tamaño máximo, no llegan al mínimo, o no hay nombres duplicados
		String errorLengthText = "TAMAÑO MÁXIMO DE TEXTO SUPERADO O FALTAN DATOS.";
		String errorNameText = "YA EXISTE UN TIPO DE EVENTO CON ESE NOMBRE";
		
		//Comprobamos que el nombre del tipo de evento no existe ya en la base de datos
		//Obtenemos la lista de todos los tipos de evento
		String[] itemList = TypesStatesContainer.getEvType().getEventTypesArray();
		
		//Si estamos creando tipo de evento nuevo
		if (okActionSelector == EventTypeUI.OK_ACTION_NEW) {
			//Si el nombre del tipo de evento creado ya existe no se permite su creación
			for (String eType: itemList) {
				if (eType.equals(eventTypeNameField.getText())) {
					infoLabel.setText(errorNameText);
					eventTypeNameField.setBackground(Color.YELLOW);
					return false;
				}
			}
		//Si estamos editando un tipo de evento existente
		} else if (okActionSelector == EventTypeUI.OK_ACTION_EDIT) {
			//Si cambiamos el nombre del tipo de evento editado
			if (!selectedEventType.equals(eventTypeNameField.getText())) {
				//Comprobamos que el nombre editado no pertenezca a otra area existente
				for (String eType: itemList) {
					if (eType.equals(eventTypeNameField.getText())) {
						infoLabel.setText(errorNameText);
						eventTypeNameField.setBackground(Color.YELLOW);
						return false;
					}
				}
			}
		}

		if (eventTypeNameField.getText().length() > 100 || eventTypeNameField.getText().length() == 0) {
			eventTypeNameField.setBackground(Color.YELLOW);
			infoLabel.setText(errorLengthText);
			return false;
		}
		return true;
	}
	
	/**
	 * Habilita los campos del formulario para que pueda introducirse información
	 */
	private void editableDataOn() {
		//Activar visibilidad de etiqueta de longitud máxima de datos
		maxCharsLabel.setVisible(true);
		//Datos editables
		eventTypeNameField.setEditable(true);
		eventTypeNameField.setBackground(Color.WHITE);
		
		//Debug
		System.out.println("EditableDataOn. Tipo de evento seleccionado: " + selectedEventType);
	}
	
	/**
	 * Deshabilita los campos del formulario para impedir que se modifique su contenido
	 */
	private void editableDataOff() {
		//Quitar visibilidad de etiquetas de longitud máxima de datos
		maxCharsLabel.setVisible(false);
		//Datos no editables
		eventTypeNameField.setEditable(true);
		eventTypeNameField.setBackground(UIManager.getColor(new JPanel().getBackground()));

		//Debug
		System.out.println("EditableDataOff. Tipo de evento seleccionado: " + selectedEventType);
	}
	
	/**
	 * Vacía el contenido de la lista de tipos de evento
	 */
	private void emptyList() {
		
		//Debug
		System.out.println("EmptyList. Tipo de evento seleccionado PRE CLEAR: " + selectedEventType);
		
		selectedEventType = null;
		registeredModel.clear();
		registeredList.setModel(registeredModel);
		listModified = true;
		
		//Debug
		System.out.println("EmptyList. Tipo de evento seleccionado POST CLEAR: " + selectedEventType);
	}
	
	/**
	 * Refresca el contenido de las listas de asignación de areas
	 */
	private void refreshList() {
		if (selectedEventType != null) {
			//Obtenemos la lista de todos los tipos de evento
			registeredEventTypes = getEventTypesFromSession();
			registeredModel.clear();
			for (String item : registeredEventTypes) {
				registeredModel.addElement(item);
			}
			registeredList.setModel(registeredModel);
		} else {
			//Debug
			System.out.println("selectedEventType = null y el modelo se borra");
			registeredModel.clear();
		}
	}
	
	/**
	 * Busca el índice de la lista de tipos de evento que ocupa el nuevo tipo de evento pasado por parámetro
	 * @param newElement tipo de evento del que buscamos su índice en la lista
	 * @return índice del tipo de evento (-1 si el elemento no está en la lista)
	 */
	private int getIndexOfElement(String newElement) {
		for (int i = 0; i < registeredList.getModel().getSize(); i++) {
			Object item = registeredList.getModel().getElementAt(i);
			if (newElement.equals((String)item)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Devuelve el formulario a su estado previo tras la creación o la edición de un tipo de evento
	 */
	private void afterNewOrEditData() {
		//Hacemos backup del tipo de evento y del índice que ocupa en la lista
		updateDataCache();
		//Formulario no editable
		editableDataOff();
		editButton.setEnabled(true);
		newButton.setEnabled(true);
		deleteButton.setEnabled(true);
		oKButton.setEnabled(false);
		cancelButton.setEnabled(false);
		//El selector de acción retorna al estado sin definir
		okActionSelector = EventTypeUI.OK_ACTION_UNDEFINED;
	}
	
	/**
	 * Hace una copia del tipo de evento seleccionado y del índice que ocupa en la lista de tipos de evento
	 */
	private void updateDataCache() {
		selectedEventTypeBackup = selectedEventType;
		itemSelectedBackupIndex = registeredList.getSelectedIndex();
	}
	
	/**
	 * Listener que monitoriza la selección de la lista de tipos de evento disponibles
	 */
	private class RegisteredListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {			
				if (selectedEventType != null) {
					if (okActionSelector == EventTypeUI.OK_ACTION_EDIT) {
						selectedEventType = selectedEventTypeBackup;
						itemSelectedIndex = itemSelectedBackupIndex;
					} else {
						selectedEventType = registeredList.getSelectedValue();
						itemSelectedIndex = registeredList.getSelectedIndex();						
					}
				} else {
					selectedEventType = selectedEventTypeBackup;
					itemSelectedIndex = itemSelectedBackupIndex;
				}
				//Debug
				System.out.println("Listener. Indice seleccionado: " + itemSelectedIndex);
				
				eventTypeNameField.setText(selectedEventType);
				
				//Debug
				System.out.println("Listener. Tipo de evento seleccionado: " + selectedEventType);
			}
		}		
	}
	
	/**
	 * Acción del botón Nueva. Se deshabilita el propio botón, el botón Editar y el botón Borrar. Se vacían
	 * los campos de texto y se habilita su edición para añadir la información de un nuevo tipo de evento.
	 * Se habilita el botón Cancelar para que los cambios no se registren y el de Aceptar para que sí lo hagan.
	 */
	public class NewAction extends AbstractAction {
		public NewAction() {
			putValue(NAME, "Nuevo");
			putValue(SHORT_DESCRIPTION, "Add new event type");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			okActionSelector = EventTypeUI.OK_ACTION_NEW;
			oKButton.setEnabled(true);
			cancelButton.setEnabled(true);
			editButton.setEnabled(false);
			newButton.setEnabled(false);
			deleteButton.setEnabled(false);
			infoLabel.setText("");
			
			//Hacemos backup del tipo de evento y del índice que ocupa en la lista
			updateDataCache();
			
			//Debug
			System.out.println("NewAction. Tipo de evento seleccionado: " + selectedEventTypeBackup);
			System.out.println("NewAction. Indice de evento seleccionado: " + itemSelectedBackupIndex);
			
			//Formulario editable
			editableDataOn();
			//Vaciamos las listas de asignación de areas
			emptyList();
			//Vaciamos los campos de texto
			eventTypeNameField.setText("");
			eventTypeNameField.requestFocusInWindow(); //REPLICATE IN OTHER SCREENS IF IT WORKS
		}	
	}
	
	/**
	 * Acción del botón Editar. Se deshabilita el propio botón, el botón Nuevo y el botón Borrar. Permite
	 * la edición de la información del formulario. Se habilita el botón de Cancelar para que los cambios
	 * no se registren y el de Aceptar para que sí lo hagan.
	 */
	public class EditAction extends AbstractAction {
		public EditAction() {
			putValue(NAME, "Editar");
			putValue(SHORT_DESCRIPTION, "Enable data edit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			okActionSelector = EventTypeUI.OK_ACTION_EDIT;
			boolean editEnabled = false;
			if (session.getUser().getUserType().equals("MANAGER")) {
				editEnabled = verifyManagerEditConditions();
			} else {
				editEnabled = verifyAdminEditConditions();
			}
			
			if (editEnabled) {
				//Hacemos backup del tipo de evento y del índice que ocupa en la lista
				updateDataCache();
				oKButton.setEnabled(true);
				cancelButton.setEnabled(true);
				editButton.setEnabled(false);
				newButton.setEnabled(false);
				deleteButton.setEnabled(false);
				registeredList.setEnabled(false);
				infoLabel.setText("");
				//Formulario editable
				editableDataOn();
				eventTypeNameField.requestFocusInWindow(); //REPLICATE IN OTHER SCREENS IF IT WORKS
			}
		}
		
	}
	
	/**
	 * Acción del botón cancelar. Se deshabilita el propio botón y el botón Aceptar. Se habilita el botón Editar,
	 * el botón Borrar y el botón Nuevo. Descarta los cambios en los datos introducidos en el formulario. No se
	 * graban en la base de datos ni en la lista de tipos de evento. Se recupera la información que figuraba
	 * anteriormente en el formulario. Se borra cualquier mensaje de error mostrado anteriormente
	 */
	public class CancelAction extends AbstractAction {
		public CancelAction() {
			putValue(NAME, "Cancelar");
			putValue(SHORT_DESCRIPTION, "Cancel data edit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			okActionSelector = EventTypeUI.OK_ACTION_UNDEFINED;
			//Cambio de estado de los botones y el combobox
			if (selectedEventType != null) {
				editButton.setEnabled(true);
				newButton.setEnabled(true);
				deleteButton.setEnabled(true);
				oKButton.setEnabled(false);
				cancelButton.setEnabled(false);
				infoLabel.setText("");
			} else {
				newButton.setEnabled(true);
				editButton.setEnabled(false);
				deleteButton.setEnabled(false);
				oKButton.setEnabled(false);
				cancelButton.setEnabled(false);
				infoLabel.setText("");
			}

			//Si la lista de tipos de evento ha cambiado
			if (listModified) {
				//Refrescamos listas
				refreshList();
				listModified = false;
			}
			//Reactivamos la lista de tipos de evento
			registeredList.setEnabled(true);
			//Recuperamos valores previos a la edición de los datos
			eventTypeNameField.setText(selectedEventTypeBackup);
			registeredList.setSelectedIndex(itemSelectedBackupIndex);

			//Formulario no editable
			editableDataOff();
		}
	}
	
	public class DeleteAction extends AbstractAction {
		public DeleteAction() {
			putValue(NAME, "Borrar");
			putValue(SHORT_DESCRIPTION, "Delete data");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
			//Debug
			infoLabel.setText("Acción de borrar un tipo de evento");
			
			okActionSelector = EventTypeUI.OK_ACTION_UNDEFINED;
			boolean deleteOK = false;
			//Comprobamos que el tipo de evento a borrar no está registrado en ningún evento
			//Obtenemos el id del tipo de evento seleccionado
			int id = TypesStatesContainer.getEvType().getEventTypeId(selectedEventType);
			if (new Event().getEventTypesOnEventFromDB(session.getConnection(), id) != 0) {
				//Debug
				System.out.println("Borrado prohibido (ALL)");
				
				ToolBox.showDialog(
						"No se pueden borrar tipos de evento registrados en eventos", EventTypeUI.this,
						DIALOG_INFO);
			//Se advierte de que el borrado es irreversible, y se autoriza si se acepta
			} else {
				
				//Debug
				System.out.println("Borrado autorizado (MANAGER)");
				
				int optionSelected = ToolBox.showDialog(
						"El borrado de areas no se puede deshacer. ¿Desea continuar?", EventTypeUI.this,
						DIALOG_YES_NO);
				if (optionSelected != JOptionPane.YES_OPTION) {
					//Debug
					System.out.println("Borrado cancelado");
					return;
				} else {							
					deleteOK = true;
				}			
			}
//			//Si no hay eventos, se comprueban las condiciones para el borrado del tipo de evento seleccionado
//			} else {
//				//Si el usuario de la sesión es de tipo manager
//				if (session.getUser().getUserType().equals("MANAGER")) {			
//					//Verificamos que el usuario está autorizado a borrar el tipo de evento seleccionado
//					if (verifyManagerEditConditions()) {
//						//Debug
//						System.out.println("Borrado autorizado (MANAGER)");
//						
//						int optionSelected = ToolBox.showDialog(
//								"El borrado de areas no se puede deshacer. ¿Desea continuar?", EventTypeUI.this,
//								DIALOG_YES_NO);
//						if (optionSelected != JOptionPane.YES_OPTION) {
//							//Debug
//							System.out.println("Borrado cancelado");
//							return;
//						} else {							
//							deleteOK = true;
//						}
//					//El usuario no está autorizado a borrar el area seleccionada		
//					} else {
//						//Debug
//						System.out.println("Borrado prohibido (MANAGER)");				
//					}
//				//Si el usuario de la sesión es de tipo admin
//				} else {
//					//Debug
//					System.out.println("Borrado autorizado (ADMIN)");
//					
//					if (verifyAdminEditConditions()) {						
//						deleteOK = true;
//					}
//				}
//			}
			
			//Si el borrado se autoriza, se borra el tipo de evento seleccionado de la base de datos
			if (deleteOK) {
				
				//Debug
				System.out.println("Borrando area de la base de datos...");
				
				//Si el tipo de evento se borra correctamente de la base de datos
				if (TypesStatesContainer.getEvType().deleteEventTypeFromDB(session.getConnection(), selectedEventType)) {
					//Registramos fecha y hora de la actualización de los datos de la tabla event_type
					tNow = ToolBox.getTimestampNow();
					infoLabel.setText("TIPO DE DATOS BORRADO: " + ToolBox.formatTimestamp(tNow, null));
					//Actualizamos los datos de la tabla last_modification
					boolean changeRegister = PersistenceManager.updateTimeStampToDB(session.getConnection(), EventType.TABLE_NAME, tNow);
					//Si se produce un error de actualización de la tabla last_modification. La actualización de la tabla area
					//no queda registrada
					if(!changeRegister) {
						infoLabel.setText(infoLabel.getText() + " .ERROR DE REGISTRO DE ACTUALIZACIÓN");
					}
				}
				//Eliminamos el tipo de evento seleccionado de la lista de tipos de evento
				TypesStatesContainer.getEvType().getEventTypes().remove(id);
				selectedEventType = null;
				//Refrescamos la lista de tipos de evento
				refreshList();
				//Designamos el primer elemento de la lista como tipo de evento seleccionado
				itemSelectedIndex = 0;
				registeredList.setSelectedIndex(itemSelectedIndex);
				//Mostramos en pantalla y designamos como tipo de evento seleccionado el primer elemento de la lista
				eventTypeNameField.setText(registeredEventTypes[0].equals(NO_EVENT_TYPE) ? null : registeredEventTypes[0]);
				selectedEventType = eventTypeNameField.getText();
				//Devolvemos el formulario a su estado previo
				afterNewOrEditData();
			}
		}
		
	}
	
	public class OKAction extends AbstractAction {
		public OKAction() {
			putValue(NAME, "Aceptar");
			putValue(SHORT_DESCRIPTION, "Save new data or data edit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			//Se recupera el fondo blanco de los campos para que una anterior validación errónea de los mismos
			//no los deje amarillos permanentemente
			eventTypeNameField.setBackground(Color.WHITE);

			//Selección de comportamiento
			
			//Aceptamos la creación de un nuevo tipo de evento
			if (okActionSelector == EventTypeUI.OK_ACTION_NEW) {
				//Debug
				System.out.println("Acción de grabar un tipo de evento nuevo");
				//Validamos los datos del formulario
				if (testData()) {
					
					//Debug
					System.out.println("Pasamos el test!!");
					
					//Intentamos grabar el nuevo tipo de evento en la base de datos, insertando una nueva entrada de tipos
					//de eventos en TypesStatesContainer que incluye también el id que le ha asignado dicha base de datos
					if (TypesStatesContainer.getEvType().addNewEventType(session.getConnection(), eventTypeNameField.getText())) {
						//Si el tipo de evento se almacena correctamente en la base de datos
						//Registramos fecha y hora de la actualización de los datos de la tabla event_type
						tNow = ToolBox.getTimestampNow();
						infoLabel.setText("NUEVO TIPO DE DATOS REGISTRADO: " + ToolBox.formatTimestamp(tNow, null));
						//Actualizamos los datos de la tabla last_modification
						boolean changeRegister = PersistenceManager.updateTimeStampToDB(session.getConnection(), EventType.TABLE_NAME, tNow);
						//Si se produce un error de actualización de la tabla last_modification. La actualización de la tabla area
						//no queda registrada
						if(!changeRegister) {
							infoLabel.setText(infoLabel.getText() + " .ERROR DE REGISTRO DE ACTUALIZACIÓN");
						}

						//Asignamos el tipo de evento guardado como tipo de evento seleccionado
						selectedEventType = eventTypeNameField.getText();
						//Refrescamos la lista de tipos de evento
						refreshList();
						//Buscamos el índice del nuevo tipo de evento y lo seleccionamos en la lista
						int newElementIndex = getIndexOfElement(selectedEventType);
						registeredList.setSelectedIndex(newElementIndex);		
						//Devolvemos el formulario a su estado previo
						afterNewOrEditData();
						
					//Si el area no se almacena correctamente en la base de datos	
					} else {
						infoLabel.setText("ERROR DE GRABACIÓN DEL NUEVO TIPO DE DATOS EN LA BASE DE DATOS");
					}
					
					
					//Debug
					for (Entry<Integer, String> item : TypesStatesContainer.getEvType().getEventTypes().entrySet()) {
						System.out.print(item);
					}
				}
				
			//Aceptamos la edición de un tipo de evento	existente
			} else if (okActionSelector == EventTypeUI.OK_ACTION_EDIT) {
				
				//Debug
				System.out.println("Guardando los cambios del tipo de evento " + eventTypeNameField.getText());
				
				//Validamos los datos del formulario
				if (testData()) {
					//Buscamos el id del tipo de evento a editar
					int itemId = TypesStatesContainer.getEvType().getEventTypeId(selectedEventTypeBackup);
					//Si el id obtenido corresponde a un tipo de evento almacenado en la base de datos
					if (itemId != -1) {
						//Si los datos actualizados se graban en la base de datos
						if (TypesStatesContainer.getEvType().updateEventTypeToDB(session.getConnection(), itemId, eventTypeNameField.getText())) {
							//Registramos fecha y hora de la actualización de los datos de la tabla area
							tNow = ToolBox.getTimestampNow();
							infoLabel.setText("DATOS DEL TIPO DE EVENTO ACTUALIZADOS: " + ToolBox.formatTimestamp(tNow, null));
							//Actualizamos los datos de la tabla last_modification
							boolean changeRegister = PersistenceManager.updateTimeStampToDB(session.getConnection(),
									EventType.TABLE_NAME, tNow);
							//Si se produce un error de actualización de la tabla last_modification. La actualización de la tabla area
							//no queda registrada
							if(!changeRegister) {
								infoLabel.setText(infoLabel.getText() + " .ERROR DE REGISTRO DE ACTUALIZACIÓN");
							}
							
							//Actualizamos los datos de backup
							selectedEventTypeBackup = eventTypeNameField.getText();
							itemSelectedBackupIndex = itemSelectedIndex;
							//Actualizamos la lista de tipos de evento
							TypesStatesContainer.getEvType().getEventTypes().replace(itemId, eventTypeNameField.getText());
							
							//Debug
							System.out.println("Tipo de evento seleccionado antes de la edición: " + selectedEventType);
							System.out.println("Nuevo valor del tipo de evento: " + eventTypeNameField.getText());
							
							//Refrescamos la lista de tipos de evento
							refreshList();
							//Reasignamos el tipo de evento seleccionado y su índice en la lista
							eventTypeNameField.setText(selectedEventType);
							registeredList.setSelectedIndex(itemSelectedIndex);
							//Reactivamos la lista de tipos de evento
							registeredList.setEnabled(true);
							//Devolvemos el formulario a su estado previo
							afterNewOrEditData();
						
						//Si los datos actualizados no se graban en la base de datos
						} else {
							infoLabel.setText("ERROR DE ACTUALIZACIÓN DE DATOS EN LA BASE DE DATOS");							
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * Clase que consulta al objeto session si los datos que le atañen han sido actualizados en la base de datos,
	 * de manera que pueda actualizar el contenido del panel con dichos datos. Si el panel se encuentra en modo
	 * de edición de los datos, o no está visible, no se produce la comprobación porque no es necesaria.
	 */
	private class TimerJob extends TimerTask {

		@Override
		public void run() {
			//Si se ha cerrado el panel, se cancelan la tarea y el temporizador
			if (!EventTypeUI.this.isShowing()) {
				EventTypeUI.this.panelVisible = false;
				this.cancel();
				EventTypeUI.this.timer.cancel();
				 System.out.println("Se ha cerrado la ventana Tipo de Evento");
			}
			//No se comprueba la actualización de los datos si los estamos editando o añadiendo
			if (cancelButton.isEnabled() && oKButton.isEnabled() && EventTypeUI.this.isShowing()) {
				//Do nothing
			//Se comprueba la actualización de los datos si no los estamos modificando
			} else if (EventTypeUI.this.panelVisible == true){
				
				//Debug
				System.out.println("Comprobando actualización de datos de tipos de eventos");
				System.out.println(session.getUpdatedTables().size());
				
				//Loop por el Map de CurrentSession, si aparece la tabla event_type, recargar datos
				for (Map.Entry<String, Timestamp> updatedTable : session.getUpdatedTables().entrySet()) {
					
					//Debug
					System.out.println(updatedTable.getKey());
					System.out.println(updatedTable.getValue());
					
					//Si en la tabla de actualizaciones aparece la clave Area.TABLE_NAME
					if (updatedTable.getKey().equals(EventType.TABLE_NAME)) {
						
						//LÓGICA DE ACTUALIZACIÓN
						
						
					}
					
				}
			}
		}
		
	}
}
