package main.java.gui;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

//import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import main.java.company.Area;
import main.java.company.BusinessUnit;
import main.java.event.Event;
import main.java.persistence.PersistenceManager;
import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;

import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JTextArea;
import javax.swing.JSeparator;

/**
 * Muestra la pantalla de creación / edición de areas
 * @author Alfred Tomey
 */
public class AreaUI extends JPanel {

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
	
	private JComboBox areaComboBox = new JComboBox();
	private JTextField areaNameField = new JTextField();
	private JTextArea areaDescription = new JTextArea();
	//Lista de todas las areas existentes en la base de datos
	private List<Area> allAreas;
	//Registra el area seleccionada en cada momento
	private Area selectedArea;
	//Lista de etiquetas informativas de longitud máxima de datos
	private List<JLabel> labelList = new ArrayList<JLabel>();
	//Lista de contenidos de los campos de datos. Sirve de backup para recuperarlos
	//Tras cancelar una edición de datos o la creación de un nuevo centro de trabajo
	private List<String> textFieldContentList = new ArrayList<String>();
	
	
	private JLabel infoLabel;
	private JLabel infoLabel2;
	private JButton editButton = new JButton();
	private JButton cancelButton;
	private JButton oKButton;
	private JButton newButton;
	private JButton deleteButton = new JButton();
	private JButton allocateButton;
	private JButton revokeButton;
	
	//Lista de elementos que aparecen en el comboBox
	private String[] areaComboList;
	//Lista de elementos que aparecerán en las listas de asignación
	private String[] availableBunits;
	private String[] allocatedBunits;
	//Modelos de datos de las listas de asignación
	private DefaultListModel<String> availableModel = new DefaultListModel<String>();
	private DefaultListModel<String> allocatedModel = new DefaultListModel<String>();
	//Listas de asignación
	private JList<String> availableList;
	private JList <String>allocatedList;
	//Contenedores de las listas de asignación
	private JScrollPane availableScrollPane;
	private JScrollPane allocatedScrollPane;
	
	private final Action editAction = new EditAction();
	private final Action cancelAction = new CancelAction();
	private final Action oKAction = new OKAction();
	private final Action newAction = new NewAction();
	private final Action deleteAction = new DeleteAction();
	private final Action allocateAction = new AllocateAction();
	private final Action revokeAllocationAction = new RevokeAllocationAction();
	//Registra la acción a realizar por el botón aceptar
	private int okActionSelector = AreaUI.OK_ACTION_UNDEFINED;
	
	//Pone en pausa la actualización de datos realizada por TimerJob si es la propia instancia
	//del programa la que ha grabado datos nuevos en la base de datos
	private volatile boolean selfUpdate = false;
	//Pone en pausa la actualización de datos realizada por TimerJob si es la propia instancia
	//del programa la que ha borrado datos de la base de datos
	private volatile boolean selfDelete = false;
	//Pone en pausa la actualización de datos realizada por TimerJob si es la propia instancia
	//del programa la que ha asignado un area en la base de datos
	private volatile boolean selfAllocate = false;
	//Pone en pausa la actualización de datos realizada por TimerJob si es la propia instancia
	//del programa la que ha eliminado la asignación de la base de datos
	private volatile boolean selfRevoke = false;
	
	
	public AreaUI(CurrentSession session) {
		this.session = session;
		setLayout(null);
		
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
		
		areaComboList = getAreaCombolistItemsFromSession();	
		areaComboBox = new JComboBox(areaComboList);
		areaComboBox.setSelectedIndex(0);
		setFirstSelectedArea();
		areaComboBox.addItemListener(new AreaComboListener());
		areaComboBox.setBounds(260, 125, 400, 25);
		areaComboBox.setEditable(false);
		ToolBox.setBlackForeground(areaComboBox);
		areaComboBox.setBackground(Color.WHITE);
		add(areaComboBox);
		

		areaNameField.setBackground(UIManager.getColor(new JPanel().getBackground()));
		areaNameField.setBounds(260, 175, 400, 25);
		areaNameField.setEditable(false);
		add(areaNameField);

		areaDescription.setLineWrap(true);
		areaDescription.setWrapStyleWord(true);
		areaDescription.setBounds(260, 225, 400, 75);
		areaDescription.setBackground(UIManager.getColor(new JPanel().getBackground()));
		areaDescription.setBorder(areaNameField.getBorder());
		areaDescription.setEditable(false);
		add(areaDescription);
		
		infoLabel = new JLabel();
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setBounds(50, 325, 900, 25);
		add(infoLabel);
		
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
		
		newButton = new JButton();
		newButton.setAction(newAction);
		newButton.setBounds(229, 375, 89, 23);
		if (session.getUser().getUserType().equals("USER")) {
			newButton.setEnabled(false);
		}
		add(newButton);
		
		editButton.setAction(editAction);
		editButton.setBounds(329, 375, 89, 23);
		if (session.getUser().getUserType().equals("USER")
				|| selectedArea == null) {
			editButton.setEnabled(false);
		}
		add(editButton);
		
		deleteButton.setAction(deleteAction);
		deleteButton.setBounds(429, 375, 89, 23);
		if (session.getUser().getUserType().equals("USER")
				|| selectedArea == null) {
			deleteButton.setEnabled(false);
		}
		add(deleteButton);
		
		cancelButton = new JButton();
		cancelButton.setAction(cancelAction);
		cancelButton.setBounds(529, 375, 89, 23);
		cancelButton.setEnabled(false);
		add(cancelButton);
		
		oKButton = new JButton();
		oKButton.setAction(oKAction);
		oKButton.setBounds(629, 375, 89, 23);
		oKButton.setEnabled(false);
		add(oKButton);
			
		JSeparator separator = new JSeparator();
		separator.setBounds(50, 425, 855, 2);
		add(separator);
		
		//Layout de asignación de areas
		
		JTextPane areaAllocateTxt = new JTextPane();
		areaAllocateTxt.setText("ASIGNACIÓN DE AREAS");
		areaAllocateTxt.setFont(new Font("Tahoma", Font.BOLD, 20));
		areaAllocateTxt.setFocusable(false);
		areaAllocateTxt.setEditable(false);
		areaAllocateTxt.setBackground(UIManager.getColor(this.getBackground()));
		areaAllocateTxt.setBounds(50, 475, 380, 30);
		add(areaAllocateTxt);
		
		JLabel availableLabel = new JLabel("Centros de trabajo disponibles");
		availableLabel.setHorizontalAlignment(SwingConstants.CENTER);
		availableLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		availableLabel.setBounds(100, 525, 300, 25);
		add(availableLabel);
		
		JLabel allocatedLabel = new JLabel("Centros de trabajo asignados");
		allocatedLabel.setHorizontalAlignment(SwingConstants.CENTER);
		allocatedLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		allocatedLabel.setBounds(600, 525, 300, 25);
		add(allocatedLabel);
	
		if (selectedArea != null) {			
			availableBunits = getAvailableBunitList(selectedArea);
			for (String item : availableBunits) {
				availableModel.addElement(item);
			}
		} 

		availableList = new JList<String>(availableModel);
		availableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		availableList.setLayoutOrientation(JList.VERTICAL);		
		availableList.setVisibleRowCount(8);
		availableList.setFont(new Font("Tahoma", Font.PLAIN, 15));
		availableList.addListSelectionListener(new AvailableListener());
		availableScrollPane = new JScrollPane(availableList);
		availableScrollPane.setBounds(100, 575, 300, 200);
		if (session.getUser().getUserType().equals("USER")
				|| selectedArea == null) {
			availableList.setEnabled(false);
		}
		add(availableScrollPane);
				
		if (selectedArea != null) {			
			allocatedBunits = getAllocatedBunitList(selectedArea);
			for (String item : allocatedBunits) {
				allocatedModel.addElement(item);
			}
		} 

		allocatedList = new JList<String>(allocatedModel);
		allocatedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		allocatedList.setLayoutOrientation(JList.VERTICAL);
		allocatedList.setVisibleRowCount(5);
		allocatedList.setFont(new Font("Tahoma", Font.PLAIN, 15));
		allocatedList.addListSelectionListener(new AllocatedListener());
		allocatedScrollPane = new JScrollPane(allocatedList);
		allocatedScrollPane.setBounds(600, 575, 300, 200);
		if (session.getUser().getUserType().equals("USER")
				|| selectedArea == null) {
			allocatedList.setEnabled(false);
		}
		add(allocatedScrollPane);
		
		allocateButton = new JButton();
		allocateButton.setAction(allocateAction);
		allocateButton.setBounds(465, 595, 75, 75);
		allocateButton.setFont(new Font("Tahoma", Font.BOLD, 40));
		allocateButton.setText("►");
		allocateButton.setEnabled(false);
		add(allocateButton);
		
		revokeButton = new JButton();
		revokeButton.setAction(revokeAllocationAction);
		revokeButton.setBounds(465, 685, 75, 75);
		revokeButton.setFont(new Font("Tahoma", Font.BOLD, 40));
		revokeButton.setText("◄");
		revokeButton.setEnabled(false);
		add(revokeButton);		
		
		infoLabel2 = new JLabel();
		infoLabel2.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel2.setBounds(50, 800, 900, 25);
		add(infoLabel2);
		
		timer = new Timer();
		TimerTask task = new TimerJob();
		timer.scheduleAtFixedRate(task, 5000, session.getPeriod());
	}
	
	/**
	 * Obtiene la liste de areas que aparecerán en el combobox de gestión de areas.
	 * @return array ordenado alfabéticamente con la lista de areas
	 */
	private String[] getAreaCombolistItemsFromSession() {
		List<String> tempList = new ArrayList<String>();
		allAreas = new Area().getAllAreasFromDB(this.session.getConnection());
		for (Area area: allAreas) {
			tempList.add(area.getAreaNombre());
		}
		//Si la lista está vacía
		if (tempList.size() == 0) {
			String[] emptyList = new String[1];
			emptyList[0] = NO_AREA;
			return emptyList;
		} else {
			return ToolBox.toSortedArray(tempList);
		}
	}
	
	/**
	 * Obiene el índice del elemento del objeto comboBox que será seleccionado por defecto a partir
	 * del array pasado por parámetro
	 * @param array array con la lista de areas
	 * @return índice del elemento a seleccionar por defecto
	 */
	private int getSelectedIndexFromArray(String[] array) {
		if (!array[0].equals(NO_AREA)) {		
			for (int i = 0; i < array.length; i++) {
				if (array[i].equals(selectedArea.getAreaNombre())) {
					return i;
				}
			}
		}
		return 0;
	}
	
	/**
	 * Actualiza el contenido del comboBox que selecciona el area activa
	 */
	private void refreshComboBox() {
		areaComboList = getAreaCombolistItemsFromSession();
		areaComboBox.setModel(new DefaultComboBoxModel(areaComboList));
		areaComboBox.setSelectedIndex(getSelectedIndexFromArray(areaComboList));
	}
	
	/**
	 * Refresca los datos del area seleccionada para que se visualicen en pantalla
	 */
	private void populateAreaFields() {
		areaNameField.setText(selectedArea.getAreaNombre());
		areaDescription.setText(selectedArea.getDescripcion());
	}
	
	/**
	 * Hace una copia de los datos que figuran en el formulario. Al cancelar la edición o la creación de un
	 * nuevo centro de trabajo, podremos recuperar por pantalla los datos del último centro de trabajo que
	 * estaba seleccionado.
	 */
	private void updateDataCache() {
		//Vaciamos la lista de datos del caché de datos
		textFieldContentList.clear();
		//Añadimos los nuevos datos al caché de datos
		textFieldContentList.add(areaNameField.getText());
		textFieldContentList.add(areaDescription.getText());
	}
	
	/**
	 * Muestra los datos del area seleccionada por defecto si el area existe, y la asigna como
	 * area seleccionada. Si no hay areas no se muestran datos y el area seleccionada se iguala a null.
	 */
	private void setFirstSelectedArea() {
		String item = (String) areaComboBox.getSelectedItem();
		//Hay un area seleccionada
		if(!item.equals(NO_AREA)) {
			selectedArea = new Area().getAreaByName(allAreas, item);
			//Mostramos los datos del area seleccionada			
			populateAreaFields();
			//Copiamos los datos al caché de datos
			updateDataCache();
		//No hay areas a seleccionar
		} else {
			selectedArea = null;
			areaNameField.setText("");
			areaDescription.setText("");
			//Vaciamos el caché de datos
			updateDataCache();
		}
	}
	
	/**
	 * Si el usuario de la sesión es de tipo manager, habilita la edición del area seleccionada
	 * solo en el caso de que esté asignada exclusivamente al mismo centro de trabajo que dicho
	 * usuario, o bien que no esté asignada a ningún centro de trabajo
	 * @return true si se cumplen las condiciones para la edición, false si no se cumplen
	 */
	private boolean verifyManagerEditConditions() {
		List<Integer> bUnitsList = new BusinessUnit().getBunitsWithArea(session.getConnection(), selectedArea);
		String action = "";
		//Si estamos editando el area
		if (okActionSelector == AreaUI.OK_ACTION_EDIT) {
			action = "editar";
		//Si queremos borrar el area
		} else {
			action = "borrar";
		}
		//Area no asignada a ningún centro de trabajo
		if (bUnitsList.size() == 0) {
			return true;
		}
		//Area asignada a más de un centro de trabajo
		if (bUnitsList.size() > 1) {
			ToolBox.showDialog(
					"Un usuario Manager no puede " + action + " areas asignadas a más de un centro de trabajo", AreaUI.this,
					DIALOG_INFO);
			return false;
		}
		//Area asignada al centro de trabajo de usuario manager que abre sesión
		if (bUnitsList.size() == 1 && session.getUser().getbUnit().getId() == bUnitsList.get(0)) {
			return true;
		//Area asignada a un centro de trabajo distinto a la del usuario manager que abre sesión
		} else {
			ToolBox.showDialog(
					"Un usuario Manager no puede " + action + " areas no asignadas a su centro de trabajo", AreaUI.this,
					DIALOG_INFO);
			return false;
		}
	}
	
	/**
	 * Si el usuario de la sesión es de tipo admin y el area seleccionada está asignada a más de un
	 * centro de trabajo, advierte al usuario de esta circunstancia. Si el usuario acepta continuar,
	 * se habilita la edición del area seleccionada. También se habilita la edición directamente en el
	 * caso de que el area seleccionada esté asignada a un solo centro de trabajo o a ninguno. 
	 * @return true si se cumplen las condiciones para la edición, false si no se cumplen
	 */
	private boolean verifyAdminEditConditions() {
		List<Integer> bUnitsList = new BusinessUnit().getBunitsWithArea(session.getConnection(), selectedArea);
		String info = "";
		//Si estamos editando el area
		if (okActionSelector == AreaUI.OK_ACTION_EDIT) {
			info = "Edición de area asignada a más de un centro de trabajo. ¿Desea continuar?";
		//Si queremos borrar el area
		} else {
			info = "Borrado de area asignada a más de un centro de trabajo. "
					+ "No se puede deshacer. " + "¿Desea continuar?";
		}
		//Area seleccionada asignada a más de un centro de trabajo
		if (bUnitsList.size() > 1) {
			int optionSelected = ToolBox.showDialog(
					info, AreaUI.this,
					DIALOG_YES_NO);
			if (optionSelected == JOptionPane.YES_OPTION) {
				return true;
			} else {
				return false;
			}
		//Area seleccionada asignada a uno o ningún centro de trabajo
		} else {
			if (okActionSelector == AreaUI.OK_ACTION_EDIT) {
				return true;
			} else {	
				int optionSelected = ToolBox.showDialog(
						"El borrado de areas no se puede deshacer. ¿Desea continuar?", AreaUI.this,
						DIALOG_YES_NO);
				if (optionSelected == JOptionPane.YES_OPTION) {
					return true;
				} else {							
					return false;
				}
			}
		}
	}
	
	/**
	 * Habilita los campos del formulario para que pueda introducirse información
	 */
	private void editableDataOn() {
		//Activar visibilidad de etiquetas de longitud máxima de datos
		for (JLabel label : labelList) {
			label.setVisible(true);
		}
		//Datos editables
		areaNameField.setEditable(true);
		areaDescription.setEditable(true);
		areaDescription.setBackground(Color.WHITE);
	}
	
	/**
	 * Deshabilita los campos del formulario para impedir que se modifique su contenido
	 */
	private void editableDataOff() {
		//Quitar visibilidad de etiquetas de longitud máxima de datos
		for (JLabel label : labelList) {
			label.setVisible(false);
		}
		//Datos no editables
		areaNameField.setEditable(false);
		areaDescription.setEditable(false);
		areaNameField.setBackground(UIManager.getColor(new JPanel().getBackground()));
		areaDescription.setBackground(UIManager.getColor(new JPanel().getBackground()));
	}
	
	/**
	 * Devuelve el formulario a su estado previo tras la creación o la edición de un area
	 */
	private void afterNewOrEditArea() {
		//Hacemos backup del contenido de los datos del formulario
		updateDataCache();
		//Formulario no editable
		editableDataOff();
		editButton.setEnabled(true);
		newButton.setEnabled(true);
		deleteButton.setEnabled(true);
		areaComboBox.setEnabled(true);
		oKButton.setEnabled(false);
		cancelButton.setEnabled(false);
		//El selector de acción retorna al estado sin definir
		okActionSelector = AreaUI.OK_ACTION_UNDEFINED;
	}
	
	/**
	 * Comprueba la corrección de los datos introducidos en el formulario. Cualquier dato incorrecto se resalta
	 * con el fondo del campo en amarillo
	 * @param areaToCheck area de la que se comprueban los datos
	 * @return true si son correctos, false si no lo son
	 */
	private boolean testData (Area areaToCheck) {
		//Comprobamos que los datos no exceden el tamaño máximo, no llegan al mínimo, o no hay nombres duplicados
		Boolean error = false;
		String errorLengthText = "TAMAÑO MAXIMO DE TEXTO SUPERADO O FALTAN DATOS.";
		String errorNameText = "YA EXISTE UN AREA CON ESE NOMBRE";
		
		//Comprobamos que el nombre del area no existe ya en la base de datos
		//Obtenemos la lista de todas las areas
		List<Area> areaList = new Area().getAllAreasFromDB(session.getConnection());
		
		//Si estamos creando una area nueva
		if (okActionSelector == AreaUI.OK_ACTION_NEW) {
			//Si el nombre del area creada ya existe no se permite su creación
			for (Area area: areaList) {
				if (areaToCheck.getAreaNombre().equals(area.getAreaNombre())) {
					infoLabel.setText(errorNameText);
					areaNameField.setBackground(Color.YELLOW);
					return false;
				}
			}
		//Si estamos editando una area existente
		} else if (okActionSelector == AreaUI.OK_ACTION_EDIT) {
			//Si cambiamos el nombre del area editada
			if (!selectedArea.getAreaNombre().equals(areaToCheck.getAreaNombre())) {
				//Comprobamos que el nombre editado no pertenezca a otra area existente
				for (Area area: areaList) {
					if (areaToCheck.getAreaNombre().equals(area.getAreaNombre())) {
						infoLabel.setText(errorNameText);
						areaNameField.setBackground(Color.YELLOW);
						return false;
					}
				}
			}
		}

		if (areaToCheck.getAreaNombre().length() > 100 || areaToCheck.getAreaNombre().length() == 0) {
			areaNameField.setBackground(Color.YELLOW);
			error = true;
		}
		if (areaToCheck.getDescripcion().length() > 200 || areaToCheck.getDescripcion().length() == 0) {
			areaDescription.setBackground(Color.YELLOW);
			error = true;
		}
		//Si hay un error, mensaje de error y retornamos false
		if (error) {
			infoLabel.setText(errorLengthText);
			return false;
		}
		return true;
	}
	
	/**
	 * Obtiene la lista de nombres de centros de trabajo en los que el area pasada por parámetro ha sido asignada
	 * @param area area de la que queremos saber a qué centros de trabajo ha sido asignada
	 * @return array ordenado alfabéticamente con los nombres de los centros de trabajo asignados
	 */
	private String[] getAllocatedBunitList(Area area) {
		List<String> allocatedList = new BusinessUnit().getAllBunitNamesWithArea(session.getConnection(), session.getCompany(), area);
		return ToolBox.toSortedArray(allocatedList);
	}
	
	/**
	 * Obtiene la lista de nombres de centros de trabajo en los que el area pasada por parámetro no ha sido asignada
	 * @param area area de la que queremos saber a qué centros de trabajo no ha sido asignada
	 * @return array ordenado alfabéticamente con los nombres de los centros de trabajo no asignadas
	 */
	private String[] getAvailableBunitList(Area area) {
		List<String> allBunits = new BusinessUnit().getAllBunitNames(session.getConnection(), session.getCompany());
		List<String> allocatedList = new BusinessUnit().getAllBunitNamesWithArea(session.getConnection(), session.getCompany(), area);
		allBunits.removeAll(allocatedList);
		return ToolBox.toSortedArray(allBunits);
	}
	
	/**
	 * Refresca el contenido de las listas de asignación de areas
	 */
	private void refreshLists() {
		if (selectedArea != null) {
			availableBunits = getAvailableBunitList(selectedArea);
			allocatedBunits = getAllocatedBunitList(selectedArea);
			availableModel.clear();
			allocatedModel.clear();
			for (String item : availableBunits) {
				availableModel.addElement(item);
			}
			for (String item : allocatedBunits) {
				allocatedModel.addElement(item);
			}

			availableList.setModel(availableModel);;
			allocatedList.setModel(allocatedModel);;
		} else {
			availableModel.clear();
			allocatedModel.clear();
		}
	}
	
	/**
	 * Vacía el contenido de las listas de asignación de areas
	 */
	private void emptyLists() {
		availableModel.clear();
		allocatedModel.clear();
		availableList.setModel(availableModel);;
		allocatedList.setModel(allocatedModel);;
	}
	
	/**
	 * Listener que define el comportamiento del comboBox. Cada elemento se corresponde con un area
	 * guardada en la base de datos. El area seleccionada se almacena en la variable selectedArea.
	 */
	private class AreaComboListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			String item = (String) areaComboBox.getSelectedItem();
			//Si no hay areas a visualizar 
			if(item.equals(NO_AREA)) {
				selectedArea = null;
				//Deshabilitamos edit y delete
				editButton.setEnabled(false);
				deleteButton.setEnabled(false);
				//Vaciamos campos de texto
				areaNameField.setText("");
				areaDescription.setText("");
				return;
			}
				
			selectedArea = new Area().getAreaByName(allAreas, item);
			//Mostramos los datos del area seleccionada			
			populateAreaFields();
			//Hacemos backup del contenido de los datos del formulario
			updateDataCache();
			//Refrescamos listas
			refreshLists();
			//Deshabilitamos los botones de asignación de areas
			allocateButton.setEnabled(false);
			revokeButton.setEnabled(false);
		}	
	}
	
	/**
	 * Listener que monitoriza la selección de la lista de centros de trabajo disponibles
	 */
	private class AvailableListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {
		        if (availableList.getSelectedIndex() != -1) {
		        	if (session.getUser().getUserType().equals("ADMIN")) {			        		
		        		allocateButton.setEnabled(true);
		        	} else if (session.getUser().getUserType().equals("MANAGER")) {
		        		//Comparar bunit seleccionada con bunit de la sesión
		        		//Si coincide, habilitar botón
		        		if (session.getUser().getbUnit().getNombre().equals(availableList.getSelectedValue())) {
		        			allocateButton.setEnabled(true);
		        		} else {
		        			allocateButton.setEnabled(false);
		        		}
		        	}
		        }
		    }
		}		
	}
	
	/**
	 * Listener que monitoriza la selección de la lista de centros de trabajo asignadas
	 */
	private class AllocatedListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {
		        if (allocatedList.getSelectedIndex() != -1) {
		        	if (session.getUser().getUserType().equals("ADMIN")) {			        		
		        		revokeButton.setEnabled(true);
		        	} else if (session.getUser().getUserType().equals("MANAGER")) {
		        		//Comparar bunit seleccionada con bunit de la sesión
		        		//Si coincide, habilitar botón
		        		if (session.getUser().getbUnit().getNombre().equals(allocatedList.getSelectedValue())) {
		        			revokeButton.setEnabled(true);
		        		} else {
		        			revokeButton.setEnabled(false);
		        		}
		        	}
		        }
		    }
		}	
	}
	
	/**
	 * Acción del botón Nueva. Se deshabilita el propio botón, el botón Editar y el combobox. Vaciamos los
	 * campos de texto y habilitamos su edición para añadir la información de una nueva area. Se bloquean las
	 * listas de asignación. Habilitamos el botón de Cancelar para que los cambios no se registren y el de
	 * Aceptar para que sí lo hagan.
	 */
	public class NewAction extends AbstractAction {
		public NewAction() {
			putValue(NAME, "Nuevo");
			putValue(SHORT_DESCRIPTION, "Add new area");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			okActionSelector = AreaUI.OK_ACTION_NEW;
			oKButton.setEnabled(true);
			cancelButton.setEnabled(true);
			editButton.setEnabled(false);
			newButton.setEnabled(false);
			deleteButton.setEnabled(false);
			areaComboBox.setEnabled(false);
			infoLabel.setText("");
			//Formulario editable
			editableDataOn();
			//Vaciamos los campos de texto
			areaNameField.setText("");
			areaDescription.setText("");
			//Bloqueamos asignaciones
			availableList.setEnabled(false);
			allocatedList.setEnabled(false);
			allocateButton.setEnabled(false);
			revokeButton.setEnabled(false);
			
			areaNameField.requestFocusInWindow();
		}
		
	}
	
	/**
	 * Acción del botón Editar. Se deshabilita el propio botón. Se bloquean las listas de asignación.
	 * Habilita la edición de la información del formulario, el botón de Cancelar para que los cambios
	 * no se registren y el de Aceptar para que sí lo hagan.
	 */
	public class EditAction extends AbstractAction {
		public EditAction() {
			putValue(NAME, "Editar");
			putValue(SHORT_DESCRIPTION, "Enable data edit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			okActionSelector = AreaUI.OK_ACTION_EDIT;
			boolean editEnabled = false;
			if (session.getUser().getUserType().equals("MANAGER")) {
				editEnabled = verifyManagerEditConditions();
			} else {
				editEnabled = verifyAdminEditConditions();
			}
			
			if (editEnabled) {
				oKButton.setEnabled(true);
				cancelButton.setEnabled(true);
				editButton.setEnabled(false);
				newButton.setEnabled(false);
				deleteButton.setEnabled(false);
				areaComboBox.setEnabled(false);
				infoLabel.setText("");
				//Formulario editable
				editableDataOn();
				//Bloqueamos asignaciones
				availableList.setEnabled(false);
				allocatedList.setEnabled(false);
				allocateButton.setEnabled(false);
				revokeButton.setEnabled(false);
				
				areaNameField.requestFocusInWindow();
			}
			
		}
	}
	
	/**
	 * Acción del botón cancelar. Se deshabilita el propio botón y el botón Aceptar. Se habilita el botón Editar, el
	 * botón Borrar y el botón Nueva. Se habilitan las listas de asignación. Descarta los cambios en los datos introducidos
	 * en el formulario. No se graban en la base de datos ni en el objeto Area. Se recupera la información que figuraba
	 * anteriormente en el formulario. Se borra cualquier mensaje de error mostrado anteriormente
	 */
	public class CancelAction extends AbstractAction {
		public CancelAction() {
			putValue(NAME, "Cancelar");
			putValue(SHORT_DESCRIPTION, "Cancel data edit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			okActionSelector = AreaUI.OK_ACTION_UNDEFINED;
			//Cambio de estado de los botones y el combobox
			if (selectedArea != null) {
				editButton.setEnabled(true);
				newButton.setEnabled(true);
				deleteButton.setEnabled(true);
				areaComboBox.setEnabled(true);
				oKButton.setEnabled(false);
				cancelButton.setEnabled(false);
				infoLabel.setText("");
				//Recuperar valores previos a la edición de los datos
				areaNameField.setText(textFieldContentList.get(0));
				areaDescription.setText(textFieldContentList.get(1));
			} else {
				newButton.setEnabled(true);
				areaComboBox.setEnabled(true);
				editButton.setEnabled(false);
				deleteButton.setEnabled(false);
				oKButton.setEnabled(false);
				cancelButton.setEnabled(false);
				infoLabel.setText("");
				//Campos vacíos
				areaNameField.setText("");
				areaDescription.setText("");
			}
			//Refrescamos listas
			refreshLists();
			//Formulario no editable
			editableDataOff();
			//Habilitamos asignaciones
			availableList.setEnabled(true);
			allocatedList.setEnabled(true);
		}		
	}
	
	/**
	 * Acción del botón borrar. Se comprueba que el area a borrar no tenga incidencias registradas
	 * y que el usuario que abre sesión tiene permiso para borrar dicha area. En caso de que el
	 * area esté asignada a varios centros de trabajo se emite un aviso (admin), y siempre se pide
	 * confirmación para realizar el borrado. Una vez borrada el area de la base de datos, se
	 * elimina también de la tabla b_unit_area y de la lista de areas asignadas de los centros
	 * de trabajo cargados en la sesión (si es que procede)
	 */
	public class DeleteAction extends AbstractAction {
		public DeleteAction() {
			putValue(NAME, "Borrar");
			putValue(SHORT_DESCRIPTION, "Delete data");
		}
		@Override
		public synchronized void actionPerformed(ActionEvent e) {
			okActionSelector = AreaUI.OK_ACTION_UNDEFINED;
			boolean deleteOK = false;
			//Comprobamos que el area a borrar no tiene eventos registrados
			List<Event> eventList = new Event().getAreaEventsFromDB(session.getConnection(), selectedArea, session.getCompany());
			//Si hay eventos, borrado prohibido para cualquier usuario
			if (eventList.size() > 0) {
				ToolBox.showDialog(
						"No se puede borrar areas asignadas a incidencias registradas", AreaUI.this,
						DIALOG_INFO);
			//Si no hay eventos, se comprueban las condiciones para el borrado del area seleccionada	
			} else {
				//Si el usuario de la sesión es de tipo manager
				if (session.getUser().getUserType().equals("MANAGER")) {			
					//Verificamos que el usuario está autorizado a borrar el area seleccionada
					if (verifyManagerEditConditions()) {
						int optionSelected = ToolBox.showDialog(
								"El borrado de areas no se puede deshacer. ¿Desea continuar?", AreaUI.this,
								DIALOG_YES_NO);
						if (optionSelected != JOptionPane.YES_OPTION) {
							return;
						} else {							
							deleteOK = true;
						}		
					} 
				//Si el usuario de la sesión es de tipo admin
				} else {
					if (verifyAdminEditConditions()) {						
						deleteOK = true;
					}
				}
			}
			//Si el borrado se autoriza, se borra el area seleccionada de la base de datos
			if (deleteOK) {

				try {
					selfDelete = true;
					
					System.out.println("Borrado de datos propios iniciado, actualizaciones suspendidas................");
					
					tNow = ToolBox.getTimestampNow();
					
					//Si el area a borrar está asignada a algún centro de trabajo en la tabla b_unit area
					//borramos primero las referencias a dicha area en la tabla b_unit_area
					
					//Si el area se borra correctamente de todos los registros de la tabla b_unit_area donde aparezca
					if (new Area().deleteBUnitAreaFromDB(session.getConnection(), selectedArea)) {
						//Borramos el area de la tabla area
						//Si el area se borra correctamente de la base de datos
						if (new Area().deleteAreaFromDB(session.getConnection(), selectedArea)) {
							//Registramos fecha y hora de la actualización de los datos de la tabla area
							PersistenceManager.registerTableModification(infoLabel, "AREA BORRADA: ", session.getConnection(), tNow,
									Area.TABLE_NAME);
							//Si el area está asignada a algún centro de trabajo
							if (new Area().isAllocatedArea(session.getConnection(), selectedArea)) {
								//Eliminamos el area borrada de cualquier centro de trabajo al que estuviera asignada
								for (BusinessUnit bUnit : session.getCompany().getBusinessUnits()) {
									boolean areaDeleted = new Area().deleteArea(bUnit, selectedArea);
									if (!areaDeleted) {
										infoLabel.setText("ERROR DE BORRADO DEL AREA DE LOS CENTROS DE TRABAJO");
									}
								} 
							}
							//Refrescamos la lista de areas del combobox y mostramos los datos de la nueva area seleccionada
							//por defecto
							areaComboList = getAreaCombolistItemsFromSession();
							areaComboBox.setModel(new DefaultComboBoxModel(areaComboList));
							areaComboBox.setSelectedIndex(0);
							setFirstSelectedArea();	
							//Refrescamos listas
							refreshLists();
							//Habilitamos asignaciones
							availableList.setEnabled(true);
							allocatedList.setEnabled(true);
							allocateButton.setEnabled(false);
							revokeButton.setEnabled(false);
						//Si el area no se borra correctamente de la base de datos
						} else {
							infoLabel.setText("ERROR DE BORRADO DEL AREA DE LA BASE DE DATOS");
						}
					//Si el area no se borra correctamente de la tabla b_unit_area
					} else {
						infoLabel.setText("ERROR DE BORRADO DEL AREA DE LA TABLA B_UNIT_AREA");
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					selfDelete = false;
					notifyAll();
					
					System.out.println("Borrado de datos propios finalizada, actualizaciones permitidas................");
				}	
			}
		}
	}
	
	/**
	 * Acción del botón Aceptar. Se deshabilita el propio botón y el botón Cancelar. Se habilitan los
	 * botones Editar, Nueva y Borrar. Se intentan guardar los datos del araea actualizados en la base
	 * de datos, o bien los datos de una area nueva. Si se consigue, se actualiza el objeto Area con dichos datos
	 * o se crea uno nuevo. Si no se consigue, no se produce la actualización o la creación del objeto Area y se
	 * muestra un mensaje de error. Se intenta guardar el registro de la actualización de datos en la base de datos.
	 * Si no se consigue se muestra un mensaje de error.
	 */
	public class OKAction extends AbstractAction {
		public OKAction() {
			putValue(NAME, "Aceptar");
			putValue(SHORT_DESCRIPTION, "Save new data or data edit");
		}
		@Override
		public synchronized void actionPerformed(ActionEvent e) {
			//Se recupera el fondo blanco de los campos para que una anterior validación errónea de los mismos
			//no los deje amarillos permanentemente
			areaNameField.setBackground(Color.WHITE);
			areaDescription.setBackground(Color.WHITE);
			
			tNow = ToolBox.getTimestampNow();
			//Selección de comportamiento
			
			try {
				selfUpdate = true;
				
				System.out.println("Grabación de datos propios iniciada, actualizaciones suspendidas................");
				
				//Aceptamos la creación de una nueva area
				if (okActionSelector == AreaUI.OK_ACTION_NEW) {
					//Debug
					System.out.println("Acción de grabar un area nueva");
					
					//Creamos un area nueva a partir de los datos del formulario
					Area newArea = new Area();
					newArea.setArea(areaNameField.getText());
					newArea.setDescripcion(areaDescription.getText());
					//Validamos los datos del formulario
					if (testData(newArea)) {
						//Intentamos grabar la nueva area en la base de datos, retornando un objeto con idénticos
						//datos que incluye también el id que le ha asignado dicha base de datos
						Area storedArea = new Area().addNewArea(session.getConnection(), newArea);
						//Si el area se almacena correctamente en la base de datos
						if (storedArea != null) {
							//Registramos fecha y hora de la actualización de los datos de la tabla area
							PersistenceManager.registerTableModification(infoLabel, "NUEVA AREA REGISTRADA: ", session.getConnection(), tNow,
									Area.TABLE_NAME);
							//Asignamos el area guardada como area seleccionada
							selectedArea = storedArea;
							//Renovamos la lista de areas del comboBox
							refreshComboBox();
							//Devolvemos el formulario a su estado previo
							afterNewOrEditArea();
							//Refrescamos listas
							refreshLists();
							//Si el area no se almacena correctamente en la base de datos
						} else {
							infoLabel.setText("ERROR DE GRABACIÓN DE LA NUEVA AREA EN LA BASE DE DATOS");
						}
					}
					
				
				//Aceptamos los cambios del area editada
				} else if (okActionSelector == AreaUI.OK_ACTION_EDIT) {
					//Debug
					System.out.println("Guardando los cambios del area " + areaNameField.getText());
					
					//Objeto que recoge los datos actualizados
					Area updatedArea = new Area();
					updatedArea.setId(selectedArea.getId());
					updatedArea.setArea(areaNameField.getText());
					updatedArea.setDescripcion(areaDescription.getText());
					
					//Si los datos están validados
					if (testData(updatedArea)) {
						//Si los datos actualizados se graban en la base de datos
						if (new Area().updateAreaToDB(session.getConnection(), updatedArea)) {
							//Registramos fecha y hora de la actualización de los datos de la tabla area
							PersistenceManager.registerTableModification(infoLabel, "DATOS DEL AREA ACTUALIZADOS: ", session.getConnection(), tNow,
									Area.TABLE_NAME);
							//Renovamos los datos del area seleccionada
							selectedArea = updatedArea;
							//Renovamos la lista de areas del comboBox
							refreshComboBox();
							//Devolvemos el formulario a su estado previo
							afterNewOrEditArea();
							
						//Si los datos actualizados no se graban en la base de datos
						} else {
							infoLabel.setText("ERROR DE ACTUALIZACIÓN DE DATOS EN LA BASE DE DATOS");
						}
					}
				}
				//Refrescamos listas
				refreshLists();
				//Habilitamos asignaciones
				availableList.setEnabled(true);
				allocatedList.setEnabled(true);
				infoLabel2.setText("");
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				selfUpdate = false;
				notifyAll();
				
				System.out.println("Grabación de datos propios finalizada, actualizaciones permitidas................");
			}	
		}
		
	}
	
	/**
	 * Acción del botón Asignar. Los usuarios de tipo ADMIN pueden asignar cualquier area a cualquier unidad de negocio.
	 * Los usuarios de tipo MANAGER solo pueden asignar areas a su propia unidad de negocio. Los usuarios de tipo USER no
	 * pueden asignar areas. Se intenta añadir a la tabla b_unit_area una nueva entrada con el id del area y el id del
	 * centro de trabajo al que se asigna. Si se consigue, se añade el area a la lista de areas del objeto BusinessUnit
	 * al que se ha asignado.
	 */
	public class AllocateAction extends AbstractAction {
		public AllocateAction() {
			putValue(NAME, "Asignar");
			putValue(SHORT_DESCRIPTION, "Allocate area");
		}
		@Override
		public synchronized void actionPerformed(ActionEvent e) {
			try {
				selfAllocate = true;
				
				System.out.println("Grabación de datos propios iniciada, actualizaciones suspendidas................");
				
				tNow = ToolBox.getTimestampNow();
				
				BusinessUnit bUnit = new BusinessUnit().getBusinessUnitByName(session.getCompany(), availableList.getSelectedValue());
				if (new Area().saveBUnitAreaToDB(session.getConnection(), bUnit, selectedArea)) {
					//Registramos fecha y hora de la actualización de los datos de la tabla b_unit_area
					PersistenceManager.registerTableModification(infoLabel2, "DATOS DE ASIGNACIÓN DE AREAS ACTUALIZADOS: ", session.getConnection(), tNow,
							Area.B_UNIT_AREA_TABLE_NAME);			
					//Añadir el area al centro de trabajo
					bUnit.getAreas().add(selectedArea);
					infoLabel2.setText(selectedArea.getAreaNombre() + " SE AÑADE A " + bUnit.getNombre() + ". "
							+ infoLabel2.getText());
					//Borramos el texto informativo del area de edición
					infoLabel.setText("");
					//Refrescar listas
					refreshLists();
					//Deshabilitar botones de asignación
					allocateButton.setEnabled(false);
					revokeButton.setEnabled(false);
				//Si los datos actualizados no se graban en la base de datos
				} else {
					infoLabel2.setText("ERROR DE ACTUALIZACIÓN DE DATOS EN LA BASE DE DATOS");
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				selfAllocate = false;
				notifyAll();
				
				System.out.println("Grabación de datos propios finalizada, actualizaciones permitidas................");
			}
		}
		
	}
	
	/**
	 * Acción del botón Revocar Asignación. Los usuarios de tipo ADMIN pueden revocar la asignación de cualquier area a
	 * cualquier centro de trabajo. Los usuarios de tipo MANAGER solo pueden revocar asignaciones de areas de su propio
	 * centro de trabajo. Los usuarios de tipo USER no pueden revocar asignaciones de areas. Se intenta eliminar de la
	 * tabla b_unit_area la entrada que contenga el id del area y el id del centro de trabajo a la que estaba asignado.
	 * Si se consigue, se elimina el area a la lista de areas del objeto BusinessUnit al que estaba asignado.
	 */
	public class RevokeAllocationAction extends AbstractAction {
		public RevokeAllocationAction() {
			putValue(NAME, "Revocar asignación");
			putValue(SHORT_DESCRIPTION, "Revoke area allocation");
		}
		@Override
		public synchronized void actionPerformed(ActionEvent e) {
			try {
				selfRevoke = true;
				
				System.out.println("Grabación de datos propios iniciada, actualizaciones suspendidas................");
				
				tNow = ToolBox.getTimestampNow();
				
				BusinessUnit bUnit = new BusinessUnit().getBusinessUnitByName(session.getCompany(), allocatedList.getSelectedValue());
				if (new Area().deleteOneBUnitAreaFromDB(session.getConnection(), bUnit, selectedArea)) {
					//Registramos fecha y hora de la actualización de los datos de la tabla b_unit_area
					PersistenceManager.registerTableModification(infoLabel2, "DATOS DE ASIGNACIÓN DE AREAS ACTUALIZADOS: ", session.getConnection(), tNow,
							Area.B_UNIT_AREA_TABLE_NAME);	
					//Eliminar el area del centro de trabajo
					if (new Area().deleteArea(bUnit, selectedArea)) {
						infoLabel2.setText(selectedArea.getAreaNombre() + " YA NO PERTENECE A " + bUnit.getNombre() + ". "
								+ infoLabel2.getText());
						//Borramos el texto informativo del area de edición
						infoLabel.setText("");
					}
					//Refrescar listas
					refreshLists();
					//Deshabilitar botones de asignación
					allocateButton.setEnabled(false);
					revokeButton.setEnabled(false);
				//Si los datos actualizados no se graban en la base de datos
				} else {
					infoLabel2.setText("ERROR DE ACTUALIZACIÓN DE DATOS EN LA BASE DE DATOS");
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				selfRevoke = false;
				notifyAll();
				
				System.out.println("Grabación de datos propios finalizada, actualizaciones permitidas................");
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
			if (!AreaUI.this.isShowing()) {
				this.cancel();
				AreaUI.this.timer.cancel();
				 System.out.println("Se ha cerrado la ventana Area");
			}
			
			System.out.println("AreaUI timerjob");
			
			if (session.isLocked()) {
				try {
					System.out.println("AreaUI esperando permiso para refrescar datos......");
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (selfUpdate) {
				try {
					System.out.println("AreaUI esperando permiso para refrescar datos......");
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (selfDelete) {
				try {
					System.out.println("AreaUI esperando permiso para refrescar datos......");
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (selfAllocate) {
				try {
					System.out.println("AreaUI esperando permiso para refrescar datos......");
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (selfRevoke) {
				try {
					System.out.println("AreaUI esperando permiso para refrescar datos......");
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//No se comprueba la actualización de los datos si los estamos editando o añadiendo
			if (cancelButton.isEnabled() && oKButton.isEnabled() && AreaUI.this.isShowing()) {
				//Do nothing
			//Se comprueba la actualización de los datos si no los estamos modificando
			} else if (AreaUI.this.isShowing()){

				//Debug
				System.out.println("session.dateTimeReference = tNow: " + (session.getDateTimeReference().equals(tNow)));
				//Si los datos actualilzados en la base de datos provienen de la propia pantalla, no actualizamos los datos visualizados
				//porque no es necesario. En caso contrario, sí que actualizamos.
				if (!session.getDateTimeReference().equals(tNow)) {
					
					//Loop por el Map de CurrentSession, si aparece la tabla area, recargar datos
					for (Map.Entry<String, Timestamp> updatedTable : session.getUpdatedTables().entrySet()) {

						//Debug
						System.out.println("session.dateTimeReference: " + session.getDateTimeReference());
						System.out.println("tNow: " + tNow);
						
						//Debug
						System.out.println(updatedTable.getKey());
						System.out.println(updatedTable.getValue());

						//Si en la tabla de actualizaciones aparece la clave Area.TABLE_NAME
						if (updatedTable.getKey().equals(Area.TABLE_NAME)) {

							//LÓGICA DE ACTUALIZACIÓN
							//Si se ha borrado el area seleccionada, refrescamos la lista de areas del
							//combobox y mostramos los datos de la nueva area seleccionada por defecto
							List<Area> updatedAreaList = new Area().getAllAreasFromDB(session.getConnection());
							boolean areaDeleted = true;
							for (Area area : updatedAreaList) {
								if (area.getAreaNombre().equals(selectedArea.getAreaNombre())) {
									areaDeleted = false;
								}
							}
							//Area seleccionada borrada
							if (areaDeleted) {
								areaComboList = getAreaCombolistItemsFromSession();
								areaComboBox.setModel(new DefaultComboBoxModel(areaComboList));
								areaComboBox.setSelectedIndex(0);
								setFirstSelectedArea();
							}
							//Renovamos la lista de areas del comboBox
							refreshComboBox();
							//Asignamos el nuevo contenido a los textfields
							populateAreaFields();
							//Hacemos backup del contenido de los datos del formulario
							updateDataCache();
							//Refrescamos listas
							refreshLists();
							allocateButton.setEnabled(false);
							revokeButton.setEnabled(false);

							//Informamos por pantalla de la actualización
							//Si el area que teníamos en pantalla no ha sufrido ninguna modificación
							//no habrá ningún cambio en la información mostrada, pero seguirá interesando saber
							//que alguna unidad de negocio ha sido modificada o añadida
							AreaUI.this.infoLabel.setText("DATOS DE LAS AREAS ACTUALIZADOS: "
									+ ToolBox.formatTimestamp(updatedTable.getValue(), null));
							//Actualizamos tNow para evitar actualizaciones de pantalla innecesarias
							tNow = session.getDateTimeReference();
						}

						//Si en la tabla de actualizaciones aparece la clave Area.B_UNIT_AREA_TABLE_NAME
						if (updatedTable.getKey().equals(Area.B_UNIT_AREA_TABLE_NAME)) {

							//Debug
							System.out.println(
									"en la tabla de actualizaciones aparece la clave Area.B_UNIT_AREA_TABLE_NAME");

							//Refrescamos listas
							refreshLists();
							allocateButton.setEnabled(false);
							revokeButton.setEnabled(false);

							//Informamos por pantalla de la actualización
							//Si el area que teníamos en pantalla no ha sufrido ninguna modificación
							//no habrá ningún cambio en la información mostrada, pero seguirá interesando saber
							//que alguna unidad de negocio ha sido modificada o añadida
							AreaUI.this.infoLabel2.setText("DATOS DE ASIGNACIÓN DE AREAS ACTUALIZADOS: "
									+ ToolBox.formatTimestamp(updatedTable.getValue(), null));
							//Actualizamos tNow para evitar actualizaciones de pantalla innecesarias
							tNow = session.getDateTimeReference();
						}
//						//Actualizamos tNow para evitar actualizaciones de pantalla innecesarias
//						tNow = session.getDateTimeReference();
					} 
				}
			}
		}
	}
}



