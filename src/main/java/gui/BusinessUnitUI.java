package main.java.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import main.java.company.BusinessUnit;
import main.java.company.Company;
import main.java.company.User;
import main.java.persistence.PersistenceManager;
import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;


/**
 * Muestra la pantalla de creación / edición de centros de trabajo
 * @author Alfred Tomey
 */
public class BusinessUnitUI extends JPanel {
	
	//Se asignan a la variable okActionSelector para determinar el comportamiento
	//de la acción okAction
	private static final int OK_ACTION_UNDEFINED = 0; //Por defecto
	private static final int OK_ACTION_EDIT = 1;
	private static final int OK_ACTION_NEW = 2;
	
	private CurrentSession session;
	private Timestamp tNow = ToolBox.getTimestampNow();
	//Temporizador de comprobación de cambios en los datos de la sesión
	private Timer timer;

	private JTextField companyField;
	private JTextField nameField;
	private JTextField addressField;
	private JTextField provinceField;
	private JTextField stateField;
	private JTextField postalCodeField;
	private JTextField telephoneField;
	private JTextField mailField;
	private JCheckBox activeCheckBox;
	private JComboBox comboBox;
	private JCheckBox activeFilterCheckBox;
	//Lista de elementos que aparecen en comboBox
	private String[] comboList;
	private JButton editButton;
	private JButton cancelButton;
	private JButton oKButton;
	private JButton newButton;
	private JLabel infoLabel;
	//Lista de etiquetas informativas de longitud máxima de datos
	private List<JLabel> labelList = new ArrayList<JLabel>();
	//Lista de campos de datos asociados a las etiquetas informativas
	private List<JTextField> textFieldList = new ArrayList<JTextField>();
	//Lista de contenidos de los campos de datos. Sirve de backup para recuperarlos
	//Tras cancelar una edición de datos o la creación de un nuevo centro de trabajo
	private List<String> textFieldContentList = new ArrayList<String>();
	//Último valor de activeCheckBox. Sirve de backup para recuperarlo
	//Tras cancelar una edición de datos o la creación de un nuevo centro de trabajo
	private boolean lastActive;
	private final Action editAction = new EditAction();
	private final Action cancelAction = new CancelAction();
	private final Action oKAction = new OKAction();
	private final Action newAction = new NewAction();
	//Registra la acción a realizar por el botón aceptar
	private int okActionSelector = BusinessUnitUI.OK_ACTION_UNDEFINED;
	
	//Pone en pausa la actualización de datos realizada por TimerJob si es la propia instancia
	//del programa la que ha grabado datos nuevos en la base de datos
	private volatile boolean selfUpdate = false;

	public BusinessUnitUI(CurrentSession session) {

		this.session = session;
		setLayout(null);
		
		JTextPane bUnitTxt = new JTextPane();
		bUnitTxt.setFont(new Font("Tahoma", Font.BOLD, 20));
		bUnitTxt.setText("DATOS DE LOS CENTROS DE TRABAJO");
		bUnitTxt.setBackground(UIManager.getColor(this.getBackground()));
		bUnitTxt.setEditable(false);
		bUnitTxt.setFocusable(false);
		bUnitTxt.setBounds(50, 50, 410, 30);
		add(bUnitTxt);
		
		JLabel companyLabel = new JLabel("Empresa");
		companyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		companyLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		companyLabel.setBounds(50, 125, 200, 25);
		add(companyLabel);
		
		JLabel selectLabel = new JLabel("Centros de trabajo");
		selectLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		selectLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		selectLabel.setBounds(50, 175, 200, 25);
		add(selectLabel);
		
		JLabel nameLabel = new JLabel("Nombre");
		nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		nameLabel.setBounds(50, 225, 200, 25);
		add(nameLabel);
		
		JLabel addressLabel = new JLabel("Dirección");
		addressLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		addressLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		addressLabel.setBounds(50, 275, 200, 25);
		add(addressLabel);
		
		JLabel provinceLabel = new JLabel("Provincia");
		provinceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		provinceLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		provinceLabel.setBounds(50, 325, 200, 25);
		add(provinceLabel);
		
		JLabel stateLabel = new JLabel("Estado");
		stateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		stateLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		stateLabel.setBounds(50, 375, 200, 25);
		add(stateLabel);
		
		JLabel postalCodeLabel = new JLabel("Código postal");
		postalCodeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		postalCodeLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		postalCodeLabel.setBounds(50, 425, 200, 25);
		add(postalCodeLabel);
		
		JLabel telephoneLabel = new JLabel("Teléfono");
		telephoneLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		telephoneLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		telephoneLabel.setBounds(50, 475, 200, 25);
		add(telephoneLabel);
		
		JLabel mailLabel = new JLabel("E-mail");
		mailLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		mailLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		mailLabel.setBounds(50, 525, 200, 25);
		add(mailLabel);
		
		JLabel activaLabel = new JLabel("Activo");
		activaLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		activaLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		activaLabel.setBounds(50, 575, 200, 25);
		add(activaLabel);
		
		companyField = new JTextField();
		companyField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		companyField.setBackground(UIManager.getColor(new JPanel().getBackground()));
		companyField.setBounds(260, 125, 400, 25);
		companyField.setText(session.getbUnit().getCompany().getNombre());
		companyField.setEditable(false);
		textFieldList.add(companyField);
		textFieldContentList.add(session.getbUnit().getCompany().getNombre());
		add(companyField);
		
		activeFilterCheckBox = new JCheckBox(" solo activos");
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
	
		nameField = new JTextField();
		nameField.setColumns(10);
		nameField.setBounds(260, 225, 400, 25);
		nameField.setText(session.getbUnit().getNombre());
		nameField.setEditable(false);
		nameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					addressField.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(nameField);
		textFieldContentList.add(session.getbUnit().getNombre());
		add(nameField);
		
		addressField = new JTextField();
		addressField.setColumns(10);
		addressField.setBounds(260, 275, 400, 25);
		addressField.setText(session.getbUnit().getDireccion());
		addressField.setEditable(false);
		addressField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					provinceField.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(addressField);
		textFieldContentList.add(session.getbUnit().getDireccion());
		add(addressField);
		
		provinceField = new JTextField();
		provinceField.setColumns(10);
		provinceField.setBounds(260, 325, 200, 25);
		provinceField.setText(session.getbUnit().getProvincia());
		provinceField.setEditable(false);
		provinceField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					stateField.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(provinceField);
		textFieldContentList.add(session.getbUnit().getProvincia());
		add(provinceField);
		
		stateField = new JTextField();
		stateField.setColumns(10);
		stateField.setBounds(260, 375, 200, 25);
		stateField.setText(session.getbUnit().getEstado());
		stateField.setEditable(false);
		stateField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					postalCodeField.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(stateField);
		textFieldContentList.add(session.getbUnit().getEstado());
		add(stateField);
		
		postalCodeField = new JTextField();
		postalCodeField.setColumns(10);
		postalCodeField.setBounds(260, 425, 100, 25);
		postalCodeField.setText(session.getbUnit().getCpostal());
		postalCodeField.setEditable(false);
		postalCodeField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					telephoneField.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(postalCodeField);
		textFieldContentList.add(session.getbUnit().getCpostal());
		add(postalCodeField);
		
		telephoneField = new JTextField();
		telephoneField.setColumns(10);
		telephoneField.setBounds(260, 475, 100, 25);
		telephoneField.setText(session.getbUnit().getTelefono());
		telephoneField.setEditable(false);
		telephoneField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					mailField.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(telephoneField);
		textFieldContentList.add(session.getbUnit().getTelefono());
		add(telephoneField);
		
		mailField = new JTextField();
		mailField.setColumns(10);
		mailField.setBounds(260, 525, 400, 25);
		mailField.setText(session.getbUnit().getMail());
		mailField.setEditable(false);
		mailField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					activeCheckBox.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(mailField);
		textFieldContentList.add(session.getbUnit().getMail());
		add(mailField);
		
		activeCheckBox = new JCheckBox();
		activeCheckBox.setBounds(260, 575, 100, 25);
		activeCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 20));
		activeCheckBox.setSelected(session.getbUnit().isActivo());
		activeCheckBox.setEnabled(false);
		activeCheckBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					activeCheckBox.setSelected(activeCheckBox.isSelected() ? false : true);
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					oKButton.requestFocusInWindow();
				}
			}
		});
		lastActive = activeCheckBox.isSelected();
		add(activeCheckBox);
				
		JLabel maxCharsLabel = new JLabel("Max: 100 caracteres");
		maxCharsLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel.setBounds(670, 225, 150, 25);
		maxCharsLabel.setVisible(false);
		labelList.add(maxCharsLabel);
		add(maxCharsLabel);
		
		JLabel maxCharsLabel2 = new JLabel("Max: 150 caracteres");
		maxCharsLabel2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel2.setBounds(670, 275, 150, 25);
		maxCharsLabel2.setVisible(false);
		labelList.add(maxCharsLabel2);
		add(maxCharsLabel2);
		
		JLabel maxCharsLabel3 = new JLabel("Max: 50 caracteres");
		maxCharsLabel3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel3.setBounds(470, 327, 150, 25);
		maxCharsLabel3.setVisible(false);
		labelList.add(maxCharsLabel3);
		add(maxCharsLabel3);
		
		JLabel maxCharsLabel4 = new JLabel("Max: 50 caracteres");
		maxCharsLabel4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel4.setBounds(470, 377, 150, 25);
		maxCharsLabel4.setVisible(false);
		labelList.add(maxCharsLabel4);
		add(maxCharsLabel4);
		
		JLabel maxCharsLabel5 = new JLabel("Max: 8 caracteres");
		maxCharsLabel5.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel5.setBounds(370, 427, 150, 25);
		maxCharsLabel5.setVisible(false);
		labelList.add(maxCharsLabel5);
		add(maxCharsLabel5);
		
		JLabel maxCharsLabel6 = new JLabel("Max: 15 caracteres");
		maxCharsLabel6.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel6.setBounds(370, 475, 150, 25);
		maxCharsLabel6.setVisible(false);
		labelList.add(maxCharsLabel6);
		add(maxCharsLabel6);
		
		JLabel maxCharsLabel7 = new JLabel("Max: 100 caracteres");
		maxCharsLabel7.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel7.setBounds(670, 525, 150, 25);
		maxCharsLabel7.setVisible(false);
		labelList.add(maxCharsLabel7);
		add(maxCharsLabel7);
		
		infoLabel = new JLabel();
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setBounds(50, 625, 1000, 25);
		add(infoLabel);
		
		oKButton = new JButton();
		oKButton.setAction(oKAction);
		oKButton.setBounds(731, 675, 89, 23);
		oKButton.setEnabled(false);
		oKButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					oKButton.doClick();
				}
			}
		});
		add(oKButton);
		
		cancelButton = new JButton();
		cancelButton.setAction(cancelAction);
		cancelButton.setBounds(632, 675, 89, 23);
		cancelButton.setEnabled(false);
		add(cancelButton);
		
		editButton = new JButton();
		editButton.setAction(editAction);
		editButton.setBounds(531, 675, 89, 23);
		if (!session.getUser().getUserType().equals("ADMIN")) {
			editButton.setEnabled(false);
		}
		add(editButton);
		
		newButton = new JButton();
		newButton.setAction(newAction);
		newButton.setBounds(431, 675, 89, 23);
		if (!session.getUser().getUserType().equals("ADMIN")) {
			newButton.setEnabled(false);
		}
		add(newButton);
		
		timer = new Timer();
		TimerTask task = new TimerJob();
		timer.scheduleAtFixedRate(task, 5000, session.getPeriod());
	}
	
	/**
	 * Refresca los datos del centro de trabajo de la sesión para que se visualicen en pantalla
	 */
	private void populateTextFields() {
		companyField.setText(session.getCompany().getNombre());
		nameField.setText(session.getbUnit().getNombre());
		addressField.setText(session.getbUnit().getDireccion());
		provinceField.setText(session.getbUnit().getProvincia());
		stateField.setText(session.getbUnit().getEstado());
		postalCodeField.setText(session.getbUnit().getCpostal());
		telephoneField.setText(session.getbUnit().getTelefono());
		mailField.setText(session.getbUnit().getMail());
		//Aunque no es un textfield, el valor de activeCheckBox también hay que mostrarlo actualizado
		activeCheckBox.setSelected(session.getbUnit().isActivo());
	}
	
	/**
	 * Comprueba la corrección de los datos introducidos en el formulario. Cualquier dato incorrecto se resalta
	 * con el fondo del campo en amarillo
	 * @param bUnit centro de trabajo del que se comprueban los datos
	 * @return true si son correctos, false si no lo son
	 */
	private boolean testData(BusinessUnit bUnit) {
		//Comprobamos que los datos no exceden el tamaño máximo, no llegan al mínimo, o no hay nombres duplicados
		Boolean error = false;
		String errorLengthText = "LA LONGITUD DE LOS DATOS EXCEDE EL TAMAÑO MÁXIMO O FALTAN DATOS";
		String errorNameText = "YA EXISTE UNA UNIDAD DE NEGOCIO CON ESE NOMBRE";
		
		List<BusinessUnit> bUnitList = new BusinessUnit().getBusinessUnitsFromDB(session.getConnection(), session.getCompany());
		for (BusinessUnit unit: bUnitList) {
			//Si el nombre de la unidad de negocio creada o editada ya existe en alguna unidad de negocio (excluyéndose ella misma),
			//no se permite la asignación de nombre
			if (bUnit.getNombre().equals(unit.getNombre()) && !bUnit.getNombre().equals(session.getbUnit().getNombre())) {
				infoLabel.setText(errorNameText);
				nameField.setBackground(Color.YELLOW);
				return false;
			}
		}
		if (bUnit.getNombre().length() > 100 || bUnit.getNombre().length() == 0) {
			nameField.setBackground(Color.YELLOW);
			error = true;
		}
		if (bUnit.getDireccion().length() > 150) {
			addressField.setBackground(Color.YELLOW);
			error = true;
		}
		if (bUnit.getProvincia().length() > 50) {
			provinceField.setBackground(Color.YELLOW);
			error = true;
		}
		if (bUnit.getEstado().length() > 50) {
			stateField.setBackground(Color.YELLOW);
			error = true;
		}
		if (bUnit.getCpostal().length() > 8) {
			postalCodeField.setBackground(Color.YELLOW);
			error = true;
		}
		if (bUnit.getTelefono().length() > 15) {
			telephoneField.setBackground(Color.YELLOW);
			error = true;
		}
		if (bUnit.getMail().length() > 100) {
			mailField.setBackground(Color.YELLOW);
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
	 * Obtiene la lista de centro de trabajo cargadas en el objeto Company. Serán todos los que
	 * existan en la base de datos si el usuario que abre sesión es de tipo administrador, y solo uno
	 * (la correspondiente al usuario que abre sesión) si es un usuario de otro tipo
	 * @param active true si se muestran solo los centros de trabajo activas, false para mostrarlas todas
	 * @return array ordenado alfabéticamente con la lista de centros de trabajo
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
	 * @param array array con la lista de centros de trabajo
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
	 * Habilita los campos del formulario para que pueda introducirse información
	 */
	private void editableDataOn() {
		//Activar visibilidad de etiquetas de longitud máxima de datos
		for (JLabel label : labelList) {
			label.setVisible(true);
		}
		//Datos editables
		for (JTextField tField : textFieldList) {
			if (tField != companyField) {
				tField.setEditable(true);
				tField.setBackground(Color.WHITE);
			}
		}
		//Habilitamos checkbox "Activo" si creamos un centro de trabajo nuevo, o si el centro
		//de trabajo no es el centro de trabajo por defecto
		if (session.getbUnit().getId() != 1 ) {
			activeCheckBox.setEnabled(true);
		} else if (session.getbUnit().getId() == 1 && okActionSelector == OK_ACTION_NEW) {
			activeCheckBox.setEnabled(true);
		} else {
			activeCheckBox.setEnabled(false);
		}
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
		for (JTextField tField : textFieldList) {
			tField.setBackground(UIManager.getColor(new JPanel().getBackground()));
			tField.setEditable(false);
		}
		//Deshabilitamos checkbox "Activa"
		activeCheckBox.setEnabled(false);
	}
	
	/**
	 * Hace una copia de los datos que figuran en el formulario. Al cancelar la edición o la creación de un
	 * nuevo centro de trabajo, podremos recuperar por pantalla los datos del último centro de trabajo que
	 * estaba seleccionado.
	 */
	private void updateDataCache() {
		//Vaciamos la lista de datos del caché de datos
		textFieldContentList.clear();
		//Añadimos los nuevos datos
		for (int i = 0; i < textFieldList.size(); i++) {
			textFieldContentList.add(textFieldList.get(i).getText());
		}
		//Guardamos el valor del ckeckbox "Activa"
		lastActive = session.getbUnit().isActivo();
	}
	
	/**
	 * Devuelve el formulario a su estado previo tras la creación o la edición de un centro de trabajo
	 */
	private void afterNewOrEditBunit() {
		//Hacemos backup del contenido de los datos del formulario
		updateDataCache();
		//Formulario no editable
		editableDataOff();
		//Cambio de estado de los botones y el combobox
		editButton.setEnabled(true);
		newButton.setEnabled(true);
		comboBox.setEnabled(true);
		activeFilterCheckBox.setEnabled(true);
		oKButton.setEnabled(false);
		cancelButton.setEnabled(false);					
		//El selector de acción retorna al estado sin definir
		okActionSelector = BusinessUnitUI.OK_ACTION_UNDEFINED;
	}
	
	/**
	 * Listener que define el comportamiento del objeto comboBox. Cada elemento se corresponde con
	 * los centros de trabajo de la empresa que se han cargado en la sesión. Por el nombre seleccionado
	 * se localiza el objeto BusinessUnit al que pertenece y se asigna dicho objeto como centro de trabajo
	 * de la sessión, reemplazando al que hubiera hasta ese momento. Si activeFilterCheckBox está seleccionado,
	 * no se mostrarán ls centros de trabajo que estén marcadas como no activas
	 */
	private class ComboListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			String item = (String) comboBox.getSelectedItem();
			Company company = session.getCompany();
			//Recuperamos la unidad de negocio seleccionada
			BusinessUnit selectedBunit = new BusinessUnit().getBusinessUnitByName(company, item);			
			//La asignamos a la sesión
			session.setbUnit(selectedBunit);
			//Mostramos sus datos
			populateTextFields();
			//Hacemos backup del contenido de los datos del formulario
			updateDataCache();
		}
		
	}
	
	/**
	 * Listener que define el comportamiento del checkbox activeFilterCheckBox.
	 * Si activamos el checkbox y el centro de trabajo en pantalla está activo no habrá ningún cambio. Si no está activo,
	 * el centro de trabajo de la sesión pasará a ser la del usuario que abrió sesión, y se mostrará en pantalla. En ese
	 * caso comboBox dejará de mostrar los centros de trabajo no activos.
	 * Si desactivamos el checkbox no habrá ningún cambio. comboBox pasará a mostrar todas los centros de trabajo cargadas
	 * en la sesión.
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
					//Mostramos sus datos
					populateTextFields();
					//Hacemos backup del contenido de los datos del formulario
					updateDataCache();
					//Renovamos la lista de los centros de trabajo del comboBox
					refreshComboBox();
					//Vaciamos label de información
					infoLabel.setText("");
				//Si la bUnit de la sesión está activa, hay que renovar el combobox igualmente para que ya no salgan las bUnits no activas
				} else {
					//Renovamos la lista de los centros de trabajo del comboBox
					refreshComboBox();
				}
			} else if (state == ItemEvent.DESELECTED) {
				//Renovamos la lista de los centros de trabajo del comboBox
				refreshComboBox();
			}
		}
	}
	
	/**
	 * Acción del botón Nueva. Se deshabilita el propio botón, el botón Editar y el combobox. Vaciamos los
	 * campos de texto y habilitamos su edición para añadir la información de un nuevo centro de trabajo.
	 * Habilitamos el botón de Cancelar para que los cambios no se registren y el de Aceptar para que sí lo hagan.
	 */
	private class NewAction extends AbstractAction {
		public NewAction() {
			putValue(NAME, "Nuevo");
			putValue(SHORT_DESCRIPTION, "Add new business unit");
		}
		public void actionPerformed(ActionEvent e) {
			okActionSelector = BusinessUnitUI.OK_ACTION_NEW;
			//Cambio de estado de los botones y el combobox
			oKButton.setEnabled(true);
			cancelButton.setEnabled(true);
			editButton.setEnabled(false);
			newButton.setEnabled(false);
			comboBox.setEnabled(false);
			activeFilterCheckBox.setEnabled(false);
			infoLabel.setText("");
			//Formulario editable
			editableDataOn();
			//Vaciamos los campos de texto
			for (JTextField tField : textFieldList) {
				if (tField != companyField) {
					tField.setText("");
				}
			}
			//checkbox "Activa" activo por defecto
			activeCheckBox.setSelected(true);
			nameField.requestFocusInWindow();
		}
	}
	
	/**
	 * Acción del botón Editar. Se deshabilita el propio botón. Habilita la edición de la información
	 * del formulario, el botón de Cancelar para que los cambios no se registren y el de Aceptar para
	 * que sí lo hagan.
	 */
	private class EditAction extends AbstractAction {
		public EditAction() {
			putValue(NAME, "Editar");
			putValue(SHORT_DESCRIPTION, "Enable data edit");
		}
		public void actionPerformed(ActionEvent e) {
			okActionSelector = BusinessUnitUI.OK_ACTION_EDIT;
			//Cambio de estado de los botones y el combobox
			oKButton.setEnabled(true);
			cancelButton.setEnabled(true);
			editButton.setEnabled(false);
			newButton.setEnabled(false);
			comboBox.setEnabled(false);
			activeFilterCheckBox.setEnabled(false);
			infoLabel.setText("");
			//Formulario editable
			editableDataOn();
			nameField.requestFocusInWindow();
		}
	}
	
	/**
	 * Acción del botón Cancelar. Se deshabilita el propio botón y el botón Aceptar. Se habilita el botón Editar y el
	 * botón Nueva. Descarta los cambios en los datos introducidos en el formulario. No se graban en la base de datos 
	 * ni en el objeto businessUnit. Se recupera la información que figuraba anteriormente en el formulario. Se borra 
	 * cualquier mensaje de error mostrado anteriormente
	 */
	private class CancelAction extends AbstractAction {
		public CancelAction() {
			putValue(NAME, "Cancelar");
			putValue(SHORT_DESCRIPTION, "Cancel data edit");
		}
		public void actionPerformed(ActionEvent e) {
			okActionSelector = BusinessUnitUI.OK_ACTION_UNDEFINED;
			//Cambio de estado de los botones y el combobox
			editButton.setEnabled(true);
			newButton.setEnabled(true);
			comboBox.setEnabled(true);
			activeFilterCheckBox.setEnabled(true);
			oKButton.setEnabled(false);
			cancelButton.setEnabled(false);
			infoLabel.setText("");
			//Formulario no editable
			editableDataOff();		
			//Recuperar valores previos a la edición de los datos
			for (int i = 0; i < textFieldList.size(); i++) {
				textFieldList.get(i).setText(textFieldContentList.get(i));
			}
			//Recuperamos el valor anterior del checkbox "Activa"
			activeCheckBox.setSelected(lastActive);
		}
	}
	
	/**
	 * Acción del botón Aceptar. Se deshabilita el propio botón y el botón Cancelar. Se habilitan los
	 * botones Editar y Nueva. Se intentan guardar los datos del centro de trabajo actualizados en la base
	 * de datos, o bien los datos de un nuevo centro de trabajo. Si se consigue, se actualiza el objeto businessUnit
	 * con dichos datos o se crea uno nuevo. Si no se consigue, no se produce la actualización o la creación
	 * del objeto businessUnit y se muestra un mensaje de error. Se intenta guardar el registro
	 * de la actualización de datos en la base de datos. Si no se consigue se muestra un mensaje de error.
	 */
	private class OKAction extends AbstractAction {
		public OKAction() {
			putValue(NAME, "Aceptar");
			putValue(SHORT_DESCRIPTION, "Execute new data or data edit");
		}
		public synchronized void actionPerformed(ActionEvent e) {
			//Se recupera el fondo blanco de los campos para que una anterior validación errónea de los mismos
			//no los deje amarillos permanentemente
			for (JTextField tField : textFieldList) {
				if (tField != companyField) {
					tField.setBackground(Color.WHITE);
				}
			}
			
			tNow = ToolBox.getTimestampNow();
			
			//Selección de comportamiento
			
			try {
				selfUpdate = true;
				
				System.out.println("Grabación de datos propios iniciada, actualizaciones suspendidas................");
				
				//Aceptamos la creación de un nuevo centro de trabajo
				if (okActionSelector == BusinessUnitUI.OK_ACTION_NEW) {

					System.out.println("Acción de grabar un nuevo centro de trabajo");

					//Creamos nuevo BusinessUnit a partir de los datos del formulario
					BusinessUnit newBunit = new BusinessUnit();
					newBunit.setCompany(session.getCompany());
					newBunit.setNombre(nameField.getText());
					newBunit.setDireccion(addressField.getText());
					newBunit.setProvincia(provinceField.getText());
					newBunit.setEstado(stateField.getText());
					newBunit.setCpostal(postalCodeField.getText());
					newBunit.setTelefono(telephoneField.getText());
					newBunit.setMail(mailField.getText());
					newBunit.setActivo(activeCheckBox.isSelected());
					//Validamos los datos del formulario
					if(testData(newBunit)) {
						//Intentamos grabar el nuevo centro de trabajo en la base de datos, retornando un objeto con idénticos
						//datos que incluye también el id que le ha asignado dicha base de datos
						BusinessUnit storedBunit = new BusinessUnit().addNewBusinessUnit(session.getConnection(), newBunit);
						//Si el centro de trabajo se almacena correctamente en la base de datos
						if (storedBunit != null) {
							//Registramos fecha y hora de la actualización de los datos de la tabla business_unit
							PersistenceManager.registerTableModification(infoLabel, "NUEVO CENTRO DE TRABAJO REGISTRADO: ", session.getConnection(), tNow,
									BusinessUnit.TABLE_NAME);
							//Añadimos el nuevo centro de trabajo a la lista de centros de trabajo de la compañía
							session.getCompany().getBusinessUnits().add(storedBunit);
							
							//Si el filtro de centros de trabajo está activo y el nuevo centro de trabajo se crea como no activo, no puede asignarse
							//como centro de trabajo de la sesión y por tanto tampoco puede visualizarse al aceptar su creación
							if (activeFilterCheckBox.isSelected() && storedBunit.isActivo() == false) {
								//Recuperamos la bUnit del usuario que abre sesión
								BusinessUnit userBunit = new BusinessUnit().getBusinessUnitById(session.getCompany(), session.getUser().getbUnit().getId());
								//La asignamos como bUnit de la sesión
								session.setbUnit(userBunit);
								//Mostramos sus datos
								populateTextFields();
								//Seleccionamos la bUnit de la sesión en el combobox. No hace falta actualizar los elementos del combobox
								comboBox.setSelectedIndex(getSelectedIndexFromArray(comboList));
								
							//Si el filtro de centros de trabajo no está activo, el nuevo centro de trabajo pasa a ser el centro de trabajo de la sesión,
							//tanto si se crea como activa como si no
							} else {
								//Asignamos el nuevo centro de trabajo como centro de trabajo de la sesión
								session.setbUnit(storedBunit);
								//Renovamos la lista de los centros de trabajo del comboBox
								refreshComboBox();
							}
							
							//Devolvemos el formulario a su estado previo
							afterNewOrEditBunit();
					
						//Si el centro de trabajo no se almacena correctamente en la base de datos 
						} else {
							infoLabel.setText("ERROR DE GRABACIÓN DEL NUEVO CENTRO DE TRABAJO EN LA BASE DE DATOS");
						}
					}
					
				//Aceptamos los cambios del centro de trabajo editado	
				} else if (okActionSelector == BusinessUnitUI.OK_ACTION_EDIT) {

					System.out.println("Guardando los cambios del centro de trabajo " + nameField.getText());
					
					//Objeto que recoge los datos actualizados
					BusinessUnit updatedBunit = new BusinessUnit();
					updatedBunit.setId(session.getbUnit().getId());
					updatedBunit.setCompany(session.getCompany());
					updatedBunit.setNombre(nameField.getText());
					updatedBunit.setDireccion(addressField.getText());
					updatedBunit.setProvincia(provinceField.getText());
					updatedBunit.setEstado(stateField.getText());
					updatedBunit.setCpostal(postalCodeField.getText());
					updatedBunit.setTelefono(telephoneField.getText());
					updatedBunit.setMail(mailField.getText());
					updatedBunit.setActivo(activeCheckBox.isSelected());
					
					//Si los datos están validados
					if (testData(updatedBunit)) {
						//Si los datos actualizados se graban en la base de datos
						if (new BusinessUnit().updateBusinessUnitToDB(session.getConnection(), updatedBunit)) {
							//Control de la actualización de la tabla last_modification por el cambio en la tabla user
							boolean UserChangeRegister = true;
							//Registramos fecha y hora de la actualización de los datos de la tabla business_unit
							boolean bUnitChangeRegister = PersistenceManager.updateTimeStampToDB(session.getConnection(),
									BusinessUnit.TABLE_NAME, tNow);
							infoLabel.setText("DATOS DEL CENTRO DE TRABAJO ACTUALIZADOS: " + ToolBox.formatTimestamp(tNow, null));
							//Variable de control para saber si la sesión sigue activa tras la edición de un centro de trabajo
							boolean stillOpenSession = true;
							//Si el usuario que abre sesión deja activa el centro de trabajo editada, se actualizan los datos de la sesión
							//Esta opción puede darse con el filtro de centros de trabajo activo o inactivo y se gestiona igual en ambos casos
							//Importante: Si el centro de trabajo editado pasa de inactivo a activo, sus usuarios (previamente desactivados también)
							//no vuelven automáticamente al estado activo. Hay que reactivarlos en la pantalla de gestión de usuarios
							if (updatedBunit.isActivo()) {
								
								session.getbUnit().setCompany(updatedBunit.getCompany());
								session.getbUnit().setNombre(updatedBunit.getNombre());
								session.getbUnit().setDireccion(updatedBunit.getDireccion());
								session.getbUnit().setProvincia(updatedBunit.getProvincia());
								session.getbUnit().setEstado(updatedBunit.getEstado());
								session.getbUnit().setCpostal(updatedBunit.getCpostal());
								session.getbUnit().setTelefono(updatedBunit.getTelefono());
								session.getbUnit().setMail(updatedBunit.getMail());
								session.getbUnit().setActivo(updatedBunit.isActivo());

								//Si se produce un error de actualización de la tabla last_modification. La actualización de la tabla business_unit
								//no queda registrada
								if(!bUnitChangeRegister) {
									infoLabel.setText(infoLabel.getText() + " . ERROR DE REGISTRO DE ACTUALIZACIÓN");
								}
								//Si el centrosde trabajo se marca como activo viniendo de un estado inactivo, anunciamos que sus usuarios no han 
								//cambiado de estado
								if (!lastActive) {
									infoLabel.setText(infoLabel.getText() + " . EL ESTADO DE LOS USUARIOS NO HA CAMBIADO");
								}
								//Renovamos la lista de los centros de trabajo del comboBox
								refreshComboBox();	
								
							//Si el usuario que abre sesión deja inactiva un centro de trabajo que no es la suya
							//Esta opción puede darse con el filtro de centros de trabajo activo o inactivo, pero se gestiona de
							//manera diferente en cada caso
							} else if (!updatedBunit.isActivo() && session.getUser().getbUnit().getId() != updatedBunit.getId()) {
								
								List<User> updatedUserList = null;
								//Si la unidad de negocio estaba activa antes de editarla
								if (lastActive) {

//									System.out.println("Marcando usuarios inactivos");
									
									//Pasar a inactivos todos los usuarios del centro de trabajo en la base de datos
									updatedUserList = new User().setNoActiveUsersToDB(session.getConnection(), updatedBunit);
									//Actualizamos los datos de la tabla last_modification por el cambio en la tabla user
									UserChangeRegister = PersistenceManager.updateTimeStampToDB(
											session.getConnection(), User.TABLE_NAME, tNow);
								}
								//Si el filtro de centros de trabajo no está activo, se actualizan los datos de la sesión
								if (!activeFilterCheckBox.isSelected()) {
									
									session.getbUnit().setCompany(updatedBunit.getCompany());
									session.getbUnit().setNombre(updatedBunit.getNombre());
									session.getbUnit().setDireccion(updatedBunit.getDireccion());
									session.getbUnit().setProvincia(updatedBunit.getProvincia());
									session.getbUnit().setEstado(updatedBunit.getEstado());
									session.getbUnit().setCpostal(updatedBunit.getCpostal());
									session.getbUnit().setTelefono(updatedBunit.getTelefono());
									session.getbUnit().setMail(updatedBunit.getMail());
									session.getbUnit().setActivo(updatedBunit.isActivo());

//									if (updatedUserList != null) {
//										System.out.println("updatedUserList.size(): " + updatedUserList.size());
//									}
									
									//Si se ha actualizado la lista de usuarios del centro de trabajo editado
									if (updatedUserList != null && updatedUserList.size() > 0) {
										
//										System.out.println("Anunciando usuarios inactivos");
										
										//Recargar los usuarios pasados a inactivos en el centro de trabajo editado
										session.getbUnit().setUsers(updatedUserList);
										infoLabel.setText(infoLabel.getText() + " . USUARIOS INACTIVOS");
									//Si no se ha actualizado la lista de usuarios del centro de trabajo editado
									} else {
										infoLabel.setText(infoLabel.getText() + " . EL ESTADO DE LOS USUARIOS NO HA CAMBIADO");
									}							
									
									//Si se produce un error de actualización de la tabla last_modification. La actualización de la tabla business_unit
									//o de la tabla user no queda registrada
									if (!bUnitChangeRegister || !UserChangeRegister) {
										infoLabel.setText(infoLabel.getText() + "ERROR DE REGISTRO DE ACTUALIZACIÓN");
									}
									//Renovamos la lista de las unidades de negocio del comboBox
									refreshComboBox();

								//Si el filtro de centros de trabajo está activo y el centro de trabajo editado queda inactiv, no puede seguir siendo
								//el centro de trabajo de la sesión y por tanto tampoco puede visualizarse
								} else {
									
									//Recuperamos la bUnit editada de la lista de bUnits y le aplicamos los cambios
									//Se podrían aplicar los cambios al centro de trabajo de la sesión porque aún no la hemos cambiado, pero de esta
									//manera evitamos ambigüedades
									BusinessUnit targetBunit = new BusinessUnit().getBusinessUnitById(session.getCompany(), session.getbUnit().getId());
									targetBunit.setCompany(updatedBunit.getCompany());
									targetBunit.setNombre(updatedBunit.getNombre());
									targetBunit.setDireccion(updatedBunit.getDireccion());
									targetBunit.setProvincia(updatedBunit.getProvincia());
									targetBunit.setEstado(updatedBunit.getEstado());
									targetBunit.setCpostal(updatedBunit.getCpostal());
									targetBunit.setTelefono(updatedBunit.getTelefono());
									targetBunit.setMail(updatedBunit.getMail());
									targetBunit.setActivo(updatedBunit.isActivo());
									//Sobreescribimos la notificación de la actualización del centro de trabajo editado a citando su nombre porque ya no
									//se visualizará por pantalla
									infoLabel.setText("DATOS DEL CENTRO DE TRABAJO " + targetBunit.getNombre() + " ACTUALIZADOS: " + 
											ToolBox.formatTimestamp(tNow, null));
									//Si se ha actualizado la lista de usuarios del centro de trabajo editada
									if (updatedUserList != null && updatedUserList.size() > 0) {
										//Recargar los usuarios pasados a inactivos en el centro de trabajo editado
										targetBunit.setUsers(updatedUserList);
										infoLabel.setText(infoLabel.getText() +	" . USUARIOS INACTIVOS");
									} else {
										infoLabel.setText(infoLabel.getText() + " . EL ESTADO DE LOS USUARIOS NO HA CAMBIADO");
									}
									//Recuperamos la bUnit del usuario que abre sesión
									BusinessUnit userBunit = new BusinessUnit().getBusinessUnitById(session.getCompany(), session.getUser().getbUnit().getId());
									//La asignamos como bUnit de la sesión
									session.setbUnit(userBunit);
									//Mostramos sus datos
									populateTextFields();
									//Renovamos la lista de los centros de trabajo del comboBox
									refreshComboBox();						
								}
								//Los usuarios ya han sido actualizados
								session.setUsersUpdated(true);
								
							//Si el usuario que abre sesión deja inactiva su propio centro de trabajo
							//Esta opción puede darse con el filtro de centros de trabajo activo o inactivo y se gestiona igual en ambos casos 
							} else if (!updatedBunit.isActivo() && session.getUser().getbUnit().getId() == updatedBunit.getId()) {
												
								//Pasar a inactivos todos los usuarios del centro de trabajo en la base de datos
								new User().setNoActiveUsersToDB(session.getConnection(), updatedBunit);
								//Actualizamos los datos de la tabla last_modification por el cambio en la tabla user
								PersistenceManager.updateTimeStampToDB(session.getConnection(),
										User.TABLE_NAME, tNow);
								//Se cerrará la sesión
								stillOpenSession = false;
								//Usuarios de la sesión actualizados
								session.setUsersUpdated(true);
								
								//Cerrar sesión y volver a login. El usuario que abrió sesión ya no puede hacer login porque también ha sido desactivado
									session.setUsersUpdated(true);
									session.backToLogin(BusinessUnit.TABLE_NAME, session.getDisplays(), session.getCurrentDisplay());
							}
									
							//Si la sesión sigue abierta
							if (stillOpenSession) {
								//Devolvemos el formulario a su estado previo
								afterNewOrEditBunit();
							}

						//Si los datos actualizados no se graban en la base de datos
						} else {
							infoLabel.setText("ERROR DE ACTUALIZACIÓN DE DATOS EN LA BASE DE DATOS");
						}
					}
				}
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
	 * Clase que consulta al objeto session si los datos que le atañen han sido actualizados en la base de datos,
	 * de manera que pueda actualizar el contenido del panel con dichos datos. Si el panel se encuentra en modo
	 * de edición de los datos, o no está visible, no se produce la comprobación porque no es necesaria.
	 */
	private class TimerJob extends TimerTask {

		@Override
		public void run() {
			//Si se ha cerrado el panel, se cancelan la tarea y el temporizador
			if (!BusinessUnitUI.this.isShowing()) {
				this.cancel();
				BusinessUnitUI.this.timer.cancel();
				 System.out.println("Se ha cerrado la ventana Centros de Trabajo");
			}
			
			if (session.isLocked()) {
				try {
					System.out.println("BusinessUnitUI esperando permiso para refrescar datos......");
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (selfUpdate) {
				try {
					System.out.println("BusinessUnitUI esperando permiso para refrescar datos......");
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//No se comprueba la actualización de los datos si los estamos editando o añadiendo
			if (cancelButton.isEnabled() && oKButton.isEnabled() && BusinessUnitUI.this.isShowing()) {
				//Do nothing
			//Se comprueba la actualización de los datos si no los estamos modificando
			} else if (BusinessUnitUI.this.isShowing()){

				System.out.println("Comprobando actualización de datos del centro de trabajo");
				System.out.println(session.getUpdatedTables().size());
				
				//Debug
				System.out.println("session.dateTimeReference = tNow: " + (session.getDateTimeReference().equals(tNow)));
				//Si los datos actualilzados en la base de datos provienen de la propia pantalla, no actualizamos los datos visualizados
				//porque no es necesario. En caso contrario, sí que actualizamos.
				if (!session.getDateTimeReference().equals(tNow)) {
					
					//Debug
					System.out.println("session.dateTimeReference: " + session.getDateTimeReference());
					System.out.println("tNow: " + tNow);
					
					//Loop por el Map de CurrentSession, si aparece la tabla business_unit, recargar datos
					for (Map.Entry<String, Timestamp> updatedTable : session.getUpdatedTables().entrySet()) {

						System.out.println(updatedTable.getKey());
						System.out.println(updatedTable.getValue());

						//Si en la tabla de actualizaciones aparece la clave BusinessUnit.TABLE_NAME
						if (updatedTable.getKey().equals(BusinessUnit.TABLE_NAME)) {
							//Si el centro de trabajo de la sesión ha sido desactivada y el filtro del combobox está activo, el centro
							//de trabajo de la sesión pasa a ser el del usuario que abrió sesión, y será el que se visualize
							if (activeFilterCheckBox.isSelected() && session.getbUnit().isActivo() == false) {

								System.out.println("Actualizando pantalla cambiando la bUnit de la sesión");
								System.out.println("La bUnit de la sesión era " + session.getbUnit().getNombre());

								//Recuperamos la bUnit del usuario que abre sesión
								BusinessUnit userBunit = new BusinessUnit().getBusinessUnitById(session.getCompany(),
										session.getUser().getbUnit().getId());
								//La asignamos como bUnit de la sesión
								session.setbUnit(userBunit);

								System.out.println("La nueva bUnit de la sesión es " + session.getbUnit().getNombre());

								//Renovamos la lista de los centros de trabajo del comboBox
								refreshComboBox();
								//Asignamos el nuevo contenido a los textfields
								populateTextFields();
								//Hacemos backup del contenido de los datos del formulario
								updateDataCache();

							} else {

								System.out.println("Actualizando pantalla sin cambiar la bUnit de la sesión");

								//Renovamos la lista de los centros de trabajo del comboBox
								refreshComboBox();
								//Asignamos el nuevo contenido a los textfields
								populateTextFields();
								//Hacemos backup del contenido de los datos del formulario
								updateDataCache();
							}

							//Informamos por pantalla de la actualización 
							//Si el centro de trabajo que teníamos en pantalla no ha sufrido ninguna modificación
							//no habrá ningún cambio en la información mostrada, pero seguirá interesando saber
							//que algun centro de trabajo ha sido modificado o añadido
							BusinessUnitUI.this.infoLabel.setText("DATOS DE LOS CENTROS DE TRABAJO ACTUALIZADOS: "
									+ ToolBox.formatTimestamp(updatedTable.getValue(), null));
							//Actualizamos tNow para evitar actualizaciones de pantalla innecesarias
							tNow = session.getDateTimeReference();
						}
					} 
				}
			}
		}
		
	}
}
