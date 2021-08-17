package main.java.gui;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

//import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JTextField;

import main.java.company.Area;
import main.java.company.BusinessUnit;
import main.java.persistence.PersistenceManager;
import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JTextArea;
import javax.swing.JSeparator;

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
	//Registra si el panel está visible o no
	private boolean panelVisible;
	
	private JComboBox areaComboBox = new JComboBox();
	private JComboBox bUnitComboBox = new JComboBox();
	private JTextField areaNameField = new JTextField();
	private JTextArea areaDescription = new JTextArea();
	//Lista de todas las areas existentes en la base de datos
	private List<Area> allAreas;
	//Registra el area seleccionada en cada momento
	private Area selectedArea;
	//Registra el area seleccionada por última vez
	private Area lastArea;
	//Lista de etiquetas informativas de longitud máxima de datos
	private List<JLabel> labelList = new ArrayList<JLabel>();

	//Lista de contenidos de los campos de datos. Sirve de backup para recuperarlos
	//Tras cancelar una edición de datos o la creación de una nueva unidad de negocio
	private List<String> textFieldContentList = new ArrayList<String>();
	
	
	private JLabel infoLabel;
	private JButton editButton = new JButton();
	private JButton cancelButton;
	private JButton oKButton;
	private JButton newButton;
	private JButton deleteButton = new JButton();
	
	//Lista de elementos que aparecen en los comboBox
	private String[] areaComboList;
	private String[] bUnitComboList;
	
	private final Action editAction = new EditAction();
	private final Action cancelAction = new CancelAction();
	private final Action oKAction = new OKAction();
	private final Action newAction = new NewAction();
	private final Action deleteAction = new DeleteAction();
	//Registra la acción a realizar por el botón aceptar
	private int okActionSelector = AreaUI.OK_ACTION_UNDEFINED;
	
	
	public AreaUI(CurrentSession session) {
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
		
//		areaComboList = getAreaCombolistItemsFromSession();
//		//Si la lista está vacía
//		if (areaComboList.length == 0) {
//			areaComboList = new String[1];
//			areaComboList[0] = NO_AREA;
//		}	
//		areaComboBox = new JComboBox(areaComboList);
//		areaComboBox.setSelectedIndex(0);
//		setFirstSelectedArea();
//		areaComboBox.addItemListener(new AreaComboListener());
//		areaComboBox.setBounds(260, 125, 400, 25);
//		areaComboBox.setEditable(false);
//		ToolBox.setBlackForeground(areaComboBox);
//		areaComboBox.setBackground(Color.WHITE);
//		add(areaComboBox);
		
		areaComboList = getAreaCombolistItemsFromSession();
//		//Si la lista está vacía
//		if (areaComboList.length == 0) {
//			areaComboList = new String[1];
//			areaComboList[0] = NO_AREA;
//		}	
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
//		areaNameField.setText(session.getbUnit().getCompany().getNombre());
		areaNameField.setEditable(false);
//		textFieldContentList.add(session.getbUnit().getCompany().getNombre());
		add(areaNameField);
		
//		areaDescription = new JTextArea();
		areaDescription.setBounds(260, 225, 400, 75);
		areaDescription.setBackground(UIManager.getColor(new JPanel().getBackground()));
		Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		areaDescription.setBorder(border);
		areaDescription.setEditable(false);
		add(areaDescription);
		
		infoLabel = new JLabel();
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setBounds(50, 325, 1000, 25);
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
		
//		editButton = new JButton();
		editButton.setAction(editAction);
		editButton.setBounds(329, 375, 89, 23);
		if (session.getUser().getUserType().equals("USER")
				|| selectedArea == null) {
			editButton.setEnabled(false);
		}
		add(editButton);
		
//		deleteButton = new JButton();
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
		separator.setBounds(50, 425, 1000, 2);
		add(separator);
	}
	
//Si el usuario de la sesión es de tipo ADMIN, aparecerán todas las areas de todas las unidades de negocio.
//Si es MANAGER o USER solo aparecerán las areas de la unidad de negocio a la que pertenezcan.
	
	/**
	 * Obtiene la liste de areas que aparecerán en el combobox de gestión de areas. 
	 * @return array ordenado alfabéticamente con la lista de areas
	 */
	public String[] getAreaCombolistItemsFromSession() {
//		List<String> tempList = new ArrayList<String>();
//		if(session.getUser().getUserType() == "ADMIN") {
//			for (BusinessUnit bUnit : session.getCompany().getBusinessUnits()) {
//				for (Area area : bUnit.getAreas()) {
//					tempList.add(area.getArea());
//					allAreas.add(area);
//				}
//			}
//		} else {
//			for (Area area : session.getbUnit().getAreas()) {
//				tempList.add(area.getArea());
//			}			 
//		}
		
		//New code
		List<String> tempList = new ArrayList<String>();
		allAreas = new Area().getAllAreasFromDB(this.session.getConnection());
		for (Area area: allAreas) {
			tempList.add(area.getArea());
		}
		//Si la lista está vacía
		if (tempList.size() == 0) {
			String[] emptyList = new String[1];
			emptyList[0] = NO_AREA;
			return emptyList;
		} else {
			return ToolBox.toSortedArray(tempList);
		}
		
		//End of new code
		
//		return ToolBox.toSortedArray(tempList);
		
//		Object[] object = (Object[]) tempList.toArray();
//		String[] itemList = new String[object.length];
//		for (int i = 0; i < object.length; i++) {
//			itemList[i] = object[i].toString();
//		}
//		Arrays.sort(itemList);
//		return itemList;
	}
	
	/**
	 * Obiene el índice del elemento del objeto comboBox que será seleccionado por defecto a partir
	 * del array pasado por parámetro
	 * @param array array con la lista de areas
	 * @return índice del elemento a seleccionar por defecto
	 */
	public int getSelectedIndexFromArray(String[] array) {
		if (!array[0].equals(NO_AREA)) {		
			for (int i = 0; i < array.length; i++) {
				if (array[i].equals(selectedArea.getArea())) {
					return i;
				}
			}
		}
		return 0;
	}
	
	/**
	 * Actualiza el contenido del comboBox que selecciona el area activa
	 */
	public void refreshComboBox() {
		areaComboList = getAreaCombolistItemsFromSession();
		areaComboBox.setModel(new DefaultComboBoxModel(areaComboList));
		areaComboBox.setSelectedIndex(getSelectedIndexFromArray(areaComboList));
	}
	
	/**
	 * Refresca los datos del area seleccionada para que se visualicen en pantalla
	 */
	public void populateAreaFields() {
//		//Debug
//		System.out.println("Populating area fields");
//		System.out.println(selectedArea == null);
		
		areaNameField.setText(selectedArea.getArea());
		areaDescription.setText(selectedArea.getDescripcion());
	}
	
	/**
	 * Hace una copia de los datos que figuran en el formulario. Al cancelar la edición o la creación de una
	 * nueva unidad de negocio, podremos recuperar por pantalla los datos de la última unidad de negocio que
	 * estaba seleccionada.
	 */
	public void updateDataCache() {
		//Vaciamos la lista de datos del caché de datos
		textFieldContentList.clear();
		//Añadimos los nuevos datos al caché de datos
		textFieldContentList.add(areaNameField.getText());
		textFieldContentList.add(areaDescription.getText());
	}
	
	/**
	 * Muestra los datos del area seleccionada por defecto la primera vez que se muestra la pantalla de
	 * gestión de areas, si el area existe.
	 */
	private void setFirstSelectedArea() {
		String item = (String) areaComboBox.getSelectedItem();
		if(!item.equals(NO_AREA)) {
//			if(session.getUser().getUserType() == "ADMIN") {
//				selectedArea = new Area().getAreaByName(allAreas, item);
//			} else {
//				selectedArea = new Area().getAreaByName(session.getbUnit(), item);
//			}
			
			selectedArea = new Area().getAreaByName(allAreas, item);
			//Mostramos los datos del area seleccionada			
			populateAreaFields();
			//Copiamos los datos al caché de datos
			textFieldContentList.add(areaNameField.getText());
			textFieldContentList.add(areaDescription.getText());
		} 
	}
	
	/**
	 * Si el usuario de la sesión es de tipo manager, habilita la edición del area seleccionada
	 * solo en el caso de que esté asignada exclusivamente a la misma unidad de negocio que dicho
	 * usuario, o bien que no esté asignada a ninguna unidad de negocio
	 * @return true si se cumplen las condiciones para la edición, false si no se cumplen
	 */
	public boolean verifyManagerEditConditions() {
		List<Integer> bUnitsList = new BusinessUnit().getBunitsWithArea(session.getConnection(), selectedArea);
		//Area no asignada a ninguna unidad de negocio
		if (bUnitsList.size() == 0) {
			return true;
		}
		//Area asignada a más de una unidad de negocio
		if (bUnitsList.size() > 1) {
			ToolBox.showDialog(
					"Un usuario Manager no puede editar areas asignadas a más de una unidad de negocio", AreaUI.this,
					DIALOG_INFO);
			return false;
		}
		//Area asignada a la unidad de negocio de usuario manager que abre sesión
		if (bUnitsList.size() == 1 && session.getUser().getbUnit().getId() == bUnitsList.get(0)) {
			return true;
		//Area asignada a una unidad de negocio distinta a la del usuario manager que abre sesión
		} else {
			ToolBox.showDialog(
					"Un usuario Manager no puede editar areas no asignadas a su unidad de negocio", AreaUI.this,
					DIALOG_INFO);
			return false;
		}
	}
	
	/**
	 * Si el usuario de la sesión es de tipo admin y el area seleccionada está asignada a más de una 
	 * unidad de negocio, advierte al usuario de esta circunstancia. Si el usuario acepta continuar,
	 * se habilita la edición del area seleccionada. También se habilita la edición directamente en el
	 * caso de que el area seleccionada esté asignada a una sola unidad de negocio o a ninguna. 
	 * @return true si se cumplen las condiciones para la edición, false si no se cumplen
	 */
	public boolean verifyAdminEditConditions() {
		List<Integer> bUnitsList = new BusinessUnit().getBunitsWithArea(session.getConnection(), selectedArea);
		if (bUnitsList.size() > 1) {
			int optionSelected = ToolBox.showDialog(
					"Area seleccionada asignada a más de una unidad de negocio. ¿Desea continuar?", AreaUI.this,
					DIALOG_YES_NO);
			if (optionSelected == JOptionPane.YES_OPTION) {
				//Debug
				System.out.println("Edición OK");
				return true;
			} else {
				//Debug
				System.out.println("Edición cancelada");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Si el usuario de la sesión es de tipo manager, habilita el borrado del area seleccionada
	 * solo en el caso de que pertenezca a la misma unidad de negocio que el usuario manager y no
	 * esté asignada a otras unidades de negocio ni a ningún evento registrado, o en el caso de
	 * que no esté asignada a ninguna unidad de negocio.
	 * @return true si se cumplen las condiciones para el borrado, false si no se cumplen
	 */
	public boolean verifyManagerDeleteConditions() {
		return false;
	}
	
	/**
	 * Si el usuario de la sesión es de tipo admin, habilita el borrado del area seleccionada
	 * solo en el caso de que no esté asignada a ningún evento registrado. si el area está asignada
	 * a más de una unidad de negocio, advierte al usuario de esta circunstancia. Si el usuario acepta
	 * continuar, se habilita el borrado del area seleccionada.
	 * @return true si se cumplen las condiciones para el borrado, false si no se cumplen
	 */
	public boolean verifyAdminDeleteConditions() {
		return false;
	}
	
	/**
	 * Habilita los campos del formulario para que pueda introducirse información
	 */
	public void editableDataOn() {
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
	public void editableDataOff() {
		//Quitar visibilidad de etiquetas de longitud máxima de datos
		for (JLabel label : labelList) {
			label.setVisible(false);
		}
		//Datos no editables
		areaNameField.setEditable(false);
		areaDescription.setEditable(false);
		areaDescription.setBackground(UIManager.getColor(new JPanel().getBackground()));
	}
	
	/**
	 * Devuelve el formulario a su estado previo tras la creación o la edición de un area
	 */
	public void afterNewOrEditArea() {
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
	public boolean testData (Area areaToCheck) {
		//Comprobamos que los datos no exceden el tamaño máximo, no llegan al mínimo, o no hay nombres duplicados
		Boolean error = false;
		String errorLengthText = "TAMAÑO MÁXIMO DE TEXTO SUPERADO O FALTAN DATOS.";
		String errorNameText = "YA EXISTE UN AREA CON ESE NOMBRE";
		
		//Comprobamos que el nombre del area no existe ya en la base de datos
		//Obtenemos la lista de todas las areas
		List<Area> areaList = new Area().getAllAreasFromDB(session.getConnection());
		//Si el nombre del area creada ya existe no se permite su creación
		for (Area area: areaList) {
			if (areaToCheck.getArea().equals(area.getArea())) {
				infoLabel.setText(errorNameText);
				areaNameField.setBackground(Color.YELLOW);
				return false;
			}
		}

		if (areaToCheck.getArea().length() > 100 || areaToCheck.getArea().length() == 0) {
			areaNameField.setBackground(Color.YELLOW);
			error = true;
		}
		if (areaToCheck.getDescripcion().length() > 200 || areaToCheck.getDescripcion().length() == 0) {
			areaNameField.setBackground(Color.YELLOW);
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
	 * Listener que define el comportamiento del comboBox. Cada elemento se corresponde con un area
	 * guardada en la base de datos. Si el usuario que abre sesión es de tipo administrador aparecerán
	 * todas las areas existentes, si es otro tipo de usuario solo aparecerán las areas asignadas a su
	 * unidad de negocio, o ninguna si no se han asignado. El area seleccionada se almacena en la variable
	 * selectedArea.
	 *
	 */
	private class AreaComboListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			String item = (String) areaComboBox.getSelectedItem();
			//Si no hay areas a visualizar (usuarios user o manager que solo tienen acceso a su propia
			//unidad de negocio, y ésta no tiene areas asignadas, o bien si se han borrado todas las areas
			//de la base de datos)
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
			//Vaciamos label de información
			infoLabel.setText("");
			
		}
		
	}
	
	/**
	 * Acción del botón Nueva. Se deshabilita el propio botón, el botón Editar y el combobox. Vaciamos los
	 * campos de texto y habilitamos su edición para añadir la información de una nueva unidad de negocio.
	 * Habilitamos el botón de Cancelar para que los cambios no se registren y el de Aceptar para que sí lo hagan.
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
		}
		
	}
	
	/**
	 * Acción del botón Editar. Se deshabilita el propio botón. Habilita la edición de la información
	 * del formulario, el botón de Cancelar para que los cambios no se registren y el de Aceptar para
	 * que sí lo hagan.
	 */
	public class EditAction extends AbstractAction {
		public EditAction() {
			putValue(NAME, "Editar");
			putValue(SHORT_DESCRIPTION, "Enable data edit");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean editEnabled = false;
			if (session.getUser().getUserType().equals("MANAGER")) {
				editEnabled = verifyManagerEditConditions();
			} else {
				editEnabled = verifyAdminEditConditions();
			}
			
			if (editEnabled) {
				okActionSelector = AreaUI.OK_ACTION_EDIT;
				oKButton.setEnabled(true);
				cancelButton.setEnabled(true);
				editButton.setEnabled(false);
				newButton.setEnabled(false);
				deleteButton.setEnabled(false);
				areaComboBox.setEnabled(false);
				infoLabel.setText("");
				//Formulario editable
				editableDataOn();
			}
			
		}
	}
	
	/**
	 * Acción del botón cancelar. Se deshabilita el propio botón y el botón Aceptar. Se habilita el botón Editar, el
	 * botón Borrar y el botón Nueva. Descarta los cambios en los datos introducidos en el formulario. No se graban
	 * en la base de datos ni en el objeto Area. Se recupera la información que figuraba anteriormente en el formulario.
	 * Se borra cualquier mensaje de error mostrado anteriormente
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
			
			//Formulario no editable
			editableDataOff();
//			//Recuperar valores previos a la edición de los datos
//			if (selectedArea != null) {			
//				areaNameField.setText(textFieldContentList.get(0));
//				areaDescription.setText(textFieldContentList.get(1));
//			} else {
//				areaNameField.setText("");
//				areaDescription.setText("");
//			}
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
			//Se recupera el fondo blanco de los campos para que una anterior validación errónea de los mismos
			//no los deje amarillos permanentemente
			areaNameField.setBackground(Color.WHITE);
			areaDescription.setBackground(Color.WHITE);
			
			//Selección de comportamiento
			
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
						tNow = ToolBox.getTimestampNow();
						infoLabel.setText("NUEVA AREA REGISTRADA: " + session.formatTimestamp(tNow, null));
						//Actualizamos los datos de la tabla last_modification
						boolean changeRegister = PersistenceManager.updateTimeStampToDB(session.getConnection(), Area.TABLE_NAME, tNow);
						//Si se produce un error de actualización de la tabla last_modification. La actualización de la tabla area
						//no queda registrada
						if(!changeRegister) {
							infoLabel.setText(infoLabel.getText() + " .ERROR DE REGISTRO DE ACTUALIZACIÓN");
						}
					}
				}
				
			
			//Aceptamos los cambios del area editada
			} else if (okActionSelector == AreaUI.OK_ACTION_EDIT) {
				//Debug
				System.out.println("Guardando los cambios del area " + areaNameField.getText());
				
				
			}
		}
		
	}
}
