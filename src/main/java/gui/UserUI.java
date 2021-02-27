package main.java.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import main.java.company.BusinessUnit;
import main.java.company.Company;
import main.java.company.User;

import main.java.persistence.CurrentSession;
import main.java.persistence.PersistenceManager;
import main.java.types_states.TypesStatesContainer;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPasswordField;

public class UserUI extends JPanel {
	
	//Se asignan a la variable okActionSelector para determinar el comportamiento
	//de la acción okAction
	private static final int OK_ACTION_UNDEFINED = 0; //Por defecto
	private static final int OK_ACTION_EDIT = 1;
	private static final int OK_ACTION_NEW = 2;
	
	private CurrentSession session;
	private Timestamp tNow = PersistenceManager.getTimestampNow();
	//Temporizador de comprobación de cambios en los datos de la sesión
	private Timer timer;
	//Registra si el panel está visible o no
	private boolean panelVisible;
	//Usuario seleccionado en pantalla
	private User userSelected;
	private JTextField companyField;
	private JComboBox bUnitComboBox = new JComboBox();
	private JCheckBox bUnitActiveFilterCheckBox;
	private JComboBox userComboBox = new JComboBox();
	private JCheckBox userActiveFilterCheckBox;
	private JComboBox userTypeComboBox = new JComboBox();;
	private JTextField userAliasField;
	private JTextField nameField;
	private JTextField lastNameField;
	private JPasswordField currentPasswordField;
	private JPasswordField newPasswordField;
	private JPasswordField confirmPasswordField;
	private JCheckBox activeCheckBox;
	private JLabel infoLabel;
	private JButton editButton;
	private JButton cancelButton;
	private JButton oKButton;
	private JButton newButton;
	//Último valor de activeCheckBox. Sirve de backup para recuperarlo tras cancelar una edición de datos
	//o la creación de un nuevo usuario
	private boolean lastActive;
	//Último valor seleccionado de userTypeComboBox. Sirve de backup para recuperarlo tras cancelar una edición
	//de datos o la creación de un nuevo usuario
	private String lastUserType;
	//Índice de lastUserType
	private int lastUserTypeIndex;
	//Lista de elementos que aparecen en los comboBox
	private String[] bUnitComboList;
	private String[] userComboList;
	private String[] userTypeComboList;
	
	//Lista de etiquetas informativas de longitud máxima de datos
	private List<JLabel> labelList = new ArrayList<JLabel>();
	//Lista de campos de datos asociados a las etiquetas informativas
	private List<JTextField> textFieldList = new ArrayList<JTextField>();
	//Lista de contenidos de los campos de datos. Sirve de caché para recuperarlos
	//Tras cancelar una edición de datos o la creación de una nueva unidad de negocio
	private List<String> textFieldContentList = new ArrayList<String>();
	
	private final Action editAction = new EditAction();
	private final Action cancelAction = new CancelAction();
	private final Action oKAction = new OKAction();
	private final Action newAction = new NewAction();
	//Registra la acción a realizar por el botón aceptar
	private int okActionSelector = UserUI.OK_ACTION_UNDEFINED;
	
	/**
	 * @wbp.parser.constructor
	 */
	public UserUI(CurrentSession session) {
		this.session = session;
		setLayout(null);
		panelVisible = true;
		userSelected = session.getUser();
		
		infoLabel = new JLabel();
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setBounds(50, 675, 1000, 25);
		add(infoLabel);
		
		oKButton = new JButton();
		oKButton.setAction(oKAction);
		oKButton.setBounds(727, 725, 89, 23);
		oKButton.setEnabled(false);
		add(oKButton);
		
		cancelButton = new JButton();
		cancelButton.setAction(cancelAction);
		cancelButton.setBounds(628, 725, 89, 23);
		cancelButton.setEnabled(false);
		add(cancelButton);
		
		editButton = new JButton();
		editButton.setAction(editAction);
		editButton.setBounds(527, 725, 89, 23);
		add(editButton);
		
		newButton = new JButton();
		newButton.setAction(newAction);
		newButton.setBounds(427, 725, 89, 23);
		if (session.getUser().getUserType().equals("USER")) {
			newButton.setEnabled(false);
		}
		add(newButton);
		
		JTextPane bUnitTxt = new JTextPane();
		bUnitTxt.setFont(new Font("Tahoma", Font.BOLD, 20));
		bUnitTxt.setText("DATOS DE LOS USUARIOS");
		bUnitTxt.setBackground(UIManager.getColor(this.getBackground()));
		bUnitTxt.setEditable(false);
		bUnitTxt.setFocusable(false);
		bUnitTxt.setBounds(50, 50, 380, 30);
		add(bUnitTxt);
		
		JLabel companyLabel = new JLabel("Empresa");
		companyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		companyLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		companyLabel.setBounds(50, 125, 200, 25);
		add(companyLabel);
		
		JLabel selectBunitLabel = new JLabel("Unidades de negocio");
		selectBunitLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		selectBunitLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		selectBunitLabel.setBounds(50, 175, 200, 25);
		add(selectBunitLabel);
		
		JLabel userLabel = new JLabel("Usuarios");
		userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		userLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userLabel.setBounds(50, 225, 200, 25);
		add(userLabel);
		
		JLabel userTypeLabel = new JLabel("Tipo de usuario");
		userTypeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		userTypeLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userTypeLabel.setBounds(50, 275, 200, 25);
		add(userTypeLabel);
		
		JLabel userAliasLabel = new JLabel("Alias de usuario");
		userAliasLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		userAliasLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userAliasLabel.setBounds(50, 325, 200, 25);
		add(userAliasLabel);
		
		JLabel userNameLabel = new JLabel("Nombre");
		userNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		userNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userNameLabel.setBounds(50, 375, 200, 25);
		add(userNameLabel);
		
		JLabel userLastNameLabel = new JLabel("Apellido");
		userLastNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		userLastNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userLastNameLabel.setBounds(50, 425, 200, 25);
		add(userLastNameLabel);
		
		JLabel currentPassLabel = new JLabel("Password actual");
		currentPassLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		currentPassLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		currentPassLabel.setBounds(50, 475, 200, 25);
		add(currentPassLabel);
		
		JLabel newPassLabel = new JLabel("Nuevo password");
		newPassLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		newPassLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		newPassLabel.setBounds(50, 525, 200, 25);
		add(newPassLabel);
		
		JLabel confirmPassLabel = new JLabel("Confirmar password");
		confirmPassLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		confirmPassLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		confirmPassLabel.setBounds(50, 575, 200, 25);
		add(confirmPassLabel);
		
		JLabel activaLabel = new JLabel("Activo");
		activaLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		activaLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		activaLabel.setBounds(50, 625, 200, 25);
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
		
		userAliasField = new JTextField();
		userAliasField.setText(userSelected.getUserAlias());
		userAliasField.setEditable(false);
		userAliasField.setColumns(10);
		userAliasField.setBounds(260, 325, 400, 25);
		userAliasField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					nameField.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(userAliasField);
		textFieldContentList.add(userSelected.getUserAlias());
		add(userAliasField);
		
		nameField = new JTextField();
		nameField.setText(userSelected.getNombre());
		nameField.setEditable(false);
		nameField.setColumns(10);
		nameField.setBounds(260, 375, 400, 25);
		nameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					lastNameField.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(nameField);
		textFieldContentList.add(userSelected.getNombre());
		add(nameField);
		
		lastNameField = new JTextField();
		lastNameField.setText(userSelected.getApellido());
		lastNameField.setEditable(false);
		lastNameField.setColumns(10);
		lastNameField.setBounds(260, 425, 400, 25);
		lastNameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					currentPasswordField.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(lastNameField);
		textFieldContentList.add(userSelected.getApellido());
		add(lastNameField);
		
		activeCheckBox = new JCheckBox();
		activeCheckBox.setBounds(260, 625, 100, 25);
		activeCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 20));
		activeCheckBox.setSelected(userSelected.isActivo());
		activeCheckBox.setEnabled(false);
		activeCheckBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					oKButton.requestFocusInWindow();
				}
			}
		});
		lastActive = activeCheckBox.isSelected();
		add(activeCheckBox);
		
		bUnitActiveFilterCheckBox = new JCheckBox(" solo activas");
		bUnitActiveFilterCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 20));
		bUnitActiveFilterCheckBox.setBounds(666, 175, 150, 25);
		bUnitActiveFilterCheckBox.addItemListener(new BunitCheckBoxListener());
		bUnitActiveFilterCheckBox.setSelected(session.getbUnit().isActivo() ? true : false);
		if (!session.getUser().getUserType().equals("ADMIN")) {
			bUnitActiveFilterCheckBox.setEnabled(false);
		}
		add(bUnitActiveFilterCheckBox);
		
		bUnitComboList = getBunitComboBoxItemsFromSession(bUnitActiveFilterCheckBox.isSelected());
		bUnitComboBox = new JComboBox(bUnitComboList);
		bUnitComboBox.setSelectedIndex(getSelectedBunitIndexFromArray(bUnitComboList));
		bUnitComboBox.setBounds(260, 175, 400, 25);
		bUnitComboBox.addItemListener(new BunitComboListener());
		bUnitComboBox.setEditable(false);
		setBlackForeground(bUnitComboBox);
		bUnitComboBox.setBackground(Color.WHITE);
		add(bUnitComboBox);
		if (!session.getUser().getUserType().equals("ADMIN")) {
			bUnitComboBox.setEnabled(false);
		}
		
		userActiveFilterCheckBox = new JCheckBox(" solo activos");
		userActiveFilterCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userActiveFilterCheckBox.setBounds(666, 225, 150, 25);
		userActiveFilterCheckBox.addItemListener(new UserCheckBoxListener());
		userActiveFilterCheckBox.setSelected(session.getbUnit().isActivo() ? true : false);
		add(userActiveFilterCheckBox);
		
		userComboList = getUserComboBoxItemsFromSession(userActiveFilterCheckBox.isSelected());
		//Si la lista está vacía
		if (userComboList.length == 0) {
			userComboList = new String[1];
			userComboList[0] = "<Ningún usuario seleccionable>";
		}		
		userComboBox = new JComboBox(userComboList);
		userComboBox.setSelectedIndex(getSelectedUserIndexFromArray(userComboList));
		userComboBox.setBounds(260, 225, 400, 25);
		userComboBox.addItemListener(new UserComboListener());
		userComboBox.setEditable(false);
		setBlackForeground(userComboBox);
		userComboBox.setBackground(Color.WHITE);
		add(userComboBox);
		if (session.getUser().getUserType().equals("USER")) {
			userComboBox.setEnabled(false);
		}
		
		userTypeComboList = getUserTypeComboBoxItemsFromSession();
		userTypeComboBox = new JComboBox(userTypeComboList);
		userTypeComboBox.setSelectedIndex(getSelectedUserTypeIndexFromArray(userTypeComboList));
		userTypeComboBox.setBounds(260, 275, 400, 25);
		userTypeComboBox.addItemListener(new UserTypeComboListener());
		userTypeComboBox.setEditable(false);
		userTypeComboBox.setEnabled(false);
		setBlackForeground(userTypeComboBox);
		userTypeComboBox.setBackground(Color.WHITE);
		add(userTypeComboBox);
		lastUserType = userTypeComboBox.getSelectedItem().toString();
		lastUserTypeIndex = getSelectedItemIndex(userTypeComboList, lastUserType);
		
		currentPasswordField = new JPasswordField();
		currentPasswordField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		currentPasswordField.setColumns(10);
		currentPasswordField.setBounds(260, 475, 400, 25);
		currentPasswordField.setEditable(false);
		currentPasswordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					newPasswordField.requestFocusInWindow();
				}
			}
		});
		add(currentPasswordField);
		
		newPasswordField = new JPasswordField();
		newPasswordField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		newPasswordField.setColumns(10);
		newPasswordField.setBounds(260, 525, 400, 25);
		newPasswordField.setEditable(false);
		newPasswordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					confirmPasswordField.requestFocusInWindow();
				}
			}
		});
		add(newPasswordField);
		
		confirmPasswordField = new JPasswordField();
		confirmPasswordField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		confirmPasswordField.setColumns(10);
		confirmPasswordField.setBounds(260, 575, 400, 25);
		confirmPasswordField.setEditable(false);
		confirmPasswordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					activeCheckBox.requestFocusInWindow();
				}
			}
		});
		add(confirmPasswordField);
		
		JLabel maxCharsLabel = new JLabel("Max: 20 caracteres");
		maxCharsLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel.setBounds(670, 325, 146, 25);
		maxCharsLabel.setVisible(false);
		labelList.add(maxCharsLabel);
		add(maxCharsLabel);
		
		JLabel maxCharsLabel2 = new JLabel("Max: 50 caracteres");
		maxCharsLabel2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel2.setBounds(670, 375, 146, 25);
		maxCharsLabel2.setVisible(false);
		labelList.add(maxCharsLabel2);
		add(maxCharsLabel2);
		
		JLabel maxCharsLabel3 = new JLabel("Max: 50 caracteres");
		maxCharsLabel3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel3.setBounds(670, 425, 146, 25);
		maxCharsLabel3.setVisible(false);
		labelList.add(maxCharsLabel3);
		add(maxCharsLabel3);
		
		JLabel maxCharsLabel4 = new JLabel("Max: 50 caracteres");
		maxCharsLabel4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel4.setBounds(670, 525, 146, 25);
		maxCharsLabel4.setVisible(false);
		labelList.add(maxCharsLabel4);
		add(maxCharsLabel4);
		
	}

	/**
	 * Obtiene la lista de unidades de negocio cargadas en el objeto company. Serán todas las que
	 * existan en la base de datos si el usuario que abre sesión es de tipo administrador, y solo una
	 * (la correspondiente al usuario que abre sesión) si es un usuario de otro tipo
	 * @param active true si se muestran solo las unidades de negocio activas, false para mostrarlas todas
	 * @return array ordenado alfabéticamente con la lista de unidades de negocio
	 */
	public String[] getBunitComboBoxItemsFromSession(boolean active) {
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
		Object[] object = (Object[]) tempList.toArray();
		String[] itemList = new String[object.length];
		for (int i = 0; i < object.length; i++) {
			itemList[i] = object[i].toString();
		}
		Arrays.sort(itemList);
		return itemList;
	}
	
	/**
	 * Obiene el índice del elemento de bUnitComboBox que será seleccionado por defecto a partir
	 * del array pasado por parámetro
	 * --- CANDIDATO A REFACTOR >> MANDAR A CURRENTSESSION ---
	 * @param array array con la lista de unidades de negocio
	 * @return índice del elemento a seleccionar por defecto
	 */
	public int getSelectedBunitIndexFromArray(String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(session.getbUnit().getNombre())) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Obtiene la lista de usuarios de la unidad de negocio de la sesión. Serán todos los usuarios si
	 * userActiveFilterCheckBox está deseleccionado, y solo los usuarios activos si userActiveFilterCheckBox
	 * está seleccionado.
	 * @param active true si se muestran solo los usuarios activos, false para mostrarlos todos
	 * @return array ordenado alfabéticamente con la lista de usuarios
	 */
	public String[] getUserComboBoxItemsFromSession(boolean active) {
		List<String> tempList = new ArrayList<String>();
		for (User user : session.getbUnit().getUsers()) {
			if (active) {
				if (user.isActivo()) {
					tempList.add(user.getUserAlias());
				}
			} else {
				tempList.add(user.getUserAlias());
			}
		}
		Object[] object = (Object[]) tempList.toArray();
		String[] itemList = new String[object.length];
		for (int i = 0; i < object.length; i++) {
			itemList[i] = object[i].toString();
		}
		Arrays.sort(itemList);
		return itemList;
	}
	
	/**
	 * Obiene el índice del elemento de userComboBox que será seleccionado por defecto a partir
	 * del array pasado por parámetro
	 * @param array array con la lista de usuarios de la unidad de negocio de la sesión
	 * @return el índice que corresponda al alias del usuario que abrió sesión si dicho usuario está en la lista,
	 * 0 si el usuario que abrió sesión no está en la lista (equivale a seleccionar el primer usuario que aparezca)
	 */
	public int getSelectedUserIndexFromArray(String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(session.getUser().getUserAlias())) {
				userSelected = new User().getUserByAlias(session.getbUnit(), array[i]);
				return i;
			}
		}
		userSelected = buildUserSelected(new User().getUserByAlias(session.getbUnit(), array[0]));
		return 0;
	}
	
	/**
	 * Devuelve el usuario entrado por parámetro si dicho usuario no es null. Si es null, crea un usuario dummy con
	 * un id (-1) que no existe en la base de datos para poderlo distinguir de cualquier otro usuario real
	 * @param user objeto User, o null
	 * @return usuario entrado por parámetro sin modificar, o un usuario vacío
	 */
	public User buildUserSelected (User user) {
		if (user == null) {
			user = new User(-1, session.getbUnit(), "", "", "", "", "", false);
		}
		return user;
	}
	
	/**
	 * Obtiene la lista de los tipos de usuarios definidos en el programa. Si userSelected es un usuario dummy con id -1
	 * la lista tendrá un único elemento vacío.
	 * @return lista de tipos de usuarios, o lista con un único elemento vacío
	 */
	public String [] getUserTypeComboBoxItemsFromSession() {
		String[] fullList = TypesStatesContainer.getuType().getUserTypesArray();
		if (userSelected.getId() == -1) {
			String[] emptyList = {""};
			return emptyList;
		} else {
			return fullList;
		}
	}
	
	/**
	 * Obiene el índice del elemento de userTypeComboBox que será seleccionado por defecto a partir
	 * del array pasado por parámetro
	 * @param array array con la lista de tipos de usuarios, o array con un único elemento vacío si
	 * userSelected es un usuario dummy con id -1
	 * @return índice del elemento a seleccionar por defecto
	 */
	public int getSelectedUserTypeIndexFromArray(String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (userSelected.getUserType().equals(array[i])) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Obtiene el índice del elemento seleccionado en userTypeComboBox
	 * @param itemList lista de valores que aparecen en userTypeComboBox
	 * @param itemValue valor del que buscamos el índice
	 * @return índice del valor buscado, o 0 si el valor no está en la lista
	 */
	public int getSelectedItemIndex (String[] itemList, String itemValue) {
		for (int i = 0; i < itemList.length; i++) {
			if (itemList[i].equals(itemValue)) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Establece por defecto el color negro como color de la letra del JComboBox pasado por parámetro, incluso
	 * en el caso de que el combobox esté deshabilitado
	 * @param combobox JComboBox al que le queremos aplicar el formato
	 */
	public void setBlackForeground(JComboBox combobox) {
		combobox.setRenderer(new DefaultListCellRenderer() {
		   @Override
		   public void paint(Graphics g) {
		       setForeground(Color.BLACK);
		       super.paint(g);
		   };
	   });
	}
	
	/**
	 * Refresca los datos del usuario seleccionado para que se visualicen en pantalla
	 */
	public void populateUserFields() {
		companyField.setText(session.getCompany().getNombre());
		userTypeComboList = getUserTypeComboBoxItemsFromSession();
		userTypeComboBox.setModel(new DefaultComboBoxModel(userTypeComboList));
		userTypeComboBox.setSelectedIndex(getSelectedUserTypeIndexFromArray(userTypeComboList));
		userAliasField.setText(userSelected.getUserAlias());
		nameField.setText(userSelected.getNombre());
		lastNameField.setText(userSelected.getApellido());
		//Aunque no es un textfield, el valor de activeCheckBox también hay que mostrarlo actualizado
		activeCheckBox.setSelected(userSelected.isActivo());
	}
	
	/**
	 * Hace una copia de los datos que figuran en el formulario. Al cancelar la edición o la creación de un
	 * nuev usuario, podremos recuperar por pantalla los datos del último usuario que estaba seleccionado
	 */
	public void updateDataCache() {
		//Vaciamos la lista de datos del caché de datos
		textFieldContentList.clear();
		//Añadimos los nuevos datos
		for (int i = 0; i < textFieldList.size(); i++) {
			textFieldContentList.add(textFieldList.get(i).getText());
		}
		//Guardamos el valor del ckeckbox "Activa"
		lastActive = session.getbUnit().isActivo();
		//Guardamos el valor de userTypeComboBox
		lastUserType = userTypeComboBox.getSelectedItem().toString();
		lastUserTypeIndex = getSelectedItemIndex(userTypeComboList, lastUserType);
	}
	
	/**
	 * Habilita los campos del formulario para que pueda introducirse información. El usuario administrador por defecto
	 * no puede cambiar su condición de administrador. Un usuario de tipo user no puede cambiar su tipo de usuario. Los
	 * usuarios de tipo user y el administrador por defecto tampoco pueden cambiar su condición de activos.
	 */
	public void editableDataOn() {
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
		//El usuario administrador por defecto no puede cambiar su condición de administrador
		//Un usuario de tipo user no puede cambiar su tipo de usuario
		if (session.getUser().getId() == 1 && userSelected.getId() == 1 || session.getUser().getUserType().equals("USER")) {
			userTypeComboBox.setEnabled(false);
		} else {
			userTypeComboBox.setEnabled(true);
		}
		
		currentPasswordField.setEditable(true);
		newPasswordField.setEditable(true);
		confirmPasswordField.setEditable(true);
		//Habilitamos checkbox "Activa" si el usuario de la sesión no es de tipo user y si no es el administrador por defecto
		if (session.getUser().getUserType().equals("USER") || (session.getUser().getId() == 1 && userSelected.getId() == 1)) {
			activeCheckBox.setEnabled(false);
		} else {
			activeCheckBox.setEnabled(true);
		}
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
		for (JTextField tField : textFieldList) {
			tField.setBackground(UIManager.getColor(new JPanel().getBackground()));
			tField.setEditable(false);
		}
		userTypeComboBox.setEnabled(false);
		//Password fields no editables
		currentPasswordField.setEditable(false);
		newPasswordField.setEditable(false);
		confirmPasswordField.setEditable(false);
		//Deshabilitamos checkbox "Activa"
		activeCheckBox.setEnabled(false);
	}
	
	/**
	 * Actualiza el contenido del comboBox que selecciona la unidad de negocio activa
	 */
	public void refreshBunitComboBox() {
		bUnitComboList = getBunitComboBoxItemsFromSession(bUnitActiveFilterCheckBox.isSelected());
		bUnitComboBox.setModel(new DefaultComboBoxModel(bUnitComboList));
		bUnitComboBox.setSelectedIndex(getSelectedBunitIndexFromArray(bUnitComboList));
	}
	
	/**
	 * Actualiza el contenido del comboBox que selecciona los usuarios
	 */
	public void refreshUserComboBox() {
		userComboList = getUserComboBoxItemsFromSession(userActiveFilterCheckBox.isSelected());
		//Si la lista está vacía
		if (userComboList.length == 0) {
			userComboList = new String[1];
			userComboList[0] = "<Ningún usuario seleccionable>";
		}
		userComboBox.setModel(new DefaultComboBoxModel(userComboList));
		userComboBox.setSelectedIndex(getSelectedUserIndexFromArray(userComboList));
	}
	
	/**
	 * Actualiza el contenido del combobox que selecciona los tipos de usuario. Si el usuario de la sesión
	 * es de tipo manager, y dicho usuario edita un usuario existente o crea uno nuevo, se elimina de la lista
	 * de tipos de usuario el usuario admin porque solo puede crear usuarios de tipo manager o user. 
	 */
	public void refreshUserTypeComboBox() {
		//Almacenamos la anterior lista de tipos de usuario
		lastUserType = userTypeComboBox.getSelectedItem().toString();
		lastUserTypeIndex = getSelectedItemIndex(userTypeComboList, lastUserType);
		//Recargamos la lista completa
		userTypeComboList = getUserTypeComboBoxItemsFromSession();
		//Recortamos la lista si es necesario
		if (session.getUser().getUserType().equals("MANAGER")) {
			String [] managerList = {userTypeComboList[1], userTypeComboList[2]};
			userTypeComboList = managerList;
		}
		userTypeComboBox.setModel(new DefaultComboBoxModel(userTypeComboList));
	}
	
	/**
	 * Si el usuario de la sesión es de tipo manager y el usuario seleccionado no es de tipo administrador, habilita
	 * la edición de datos del usuario seleccionado 
	 */
	public void verifyManagerEditConditions() {

		//Un usuario manager no puede editar los datos de un usuario administrador
		if (session.getUser().getUserType().equals("MANAGER") && userSelected.getUserType().equals("ADMIN")) {
			editButton.setEnabled(false);
		} else if (session.getUser().getUserType().equals("MANAGER") && !userSelected.getUserType().equals("ADMIN")) {
			editButton.setEnabled(true);
		}
	}
	
	/**
	 * Si el usuario de la sesión es de tipo administrador, habilita la edición de sus propios datos y los de cualquier
	 * otro usuario que no sea de tipo administrador. Si el usuario de la sesión es el administrador por defecto también
	 * habilita la edición de los datos de cualquier usuario administrador.
	 */
	public void verifyAdminEditConditions() {

		//Si el usuario de la sesión es un usuario administrador y el usuario seleccionado no lo es, habilitamos
		//siempre la edición del usuario seleccionado
		if (session.getUser().getUserType().equals("ADMIN") && !userSelected.getUserType().equals("ADMIN")) {
			editButton.setEnabled(true);
		}
		
		//Los datos del usuario administrador por defecto no pueden ser modificados por ningún otro usuario administrador
		if (session.getUser().getUserType().equals("ADMIN") && session.getUser().getId() != 1 && 
				userSelected.getId() == 1) {
			editButton.setEnabled(false);
		} else if (session.getUser().getUserType().equals("ADMIN") && session.getUser().getId() == 1) {
			editButton.setEnabled(true);
		}
		
		//Los datos de un usuario administrador solo pueden ser modificados por si mismo y por el usuario administrador
		//por defecto
		if (session.getUser().getUserType().equals("ADMIN") && session.getUser().getId() == userSelected.getId()) {
			editButton.setEnabled(true);
		} else if (session.getUser().getUserType().equals("ADMIN") && session.getUser().getId() != 1 &&
				userSelected.getUserType().equals("ADMIN")) {
			editButton.setEnabled(false);
		}
	}
	
	/**
	 * Si userSelected es un usuario dummy con id -1, la edición de datos se deshabilita
	 */
	public void verifyUserSelectedExists() {
		if (userSelected.getId() == -1) {
			editButton.setEnabled(false);
		}
	}
	
	/**
	 * Listener que define el comportamiento del comboBox bUnitComboBox. Cada elemento se corresponde con
	 * las unidades de negocio de la compañía que se han cargado en la sesión. Por el nombre seleccionado
	 * se localiza el objeto BusinessUnit al que pertenece y se asigna dicho objeto como unidad de negocio
	 * de la sessión, reemplazando al que hubiera hasta ese momento. Si activeFilterCheckBox está seleccionado,
	 * no se mostrarán las unidades de negocio que estén marcadas como no activas
	 */
	private class BunitComboListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
					
			String item = (String) bUnitComboBox.getSelectedItem();
			Company company = session.getCompany();
			//Recuperamos la unidad de negocio seleccionada
			BusinessUnit selectedBunit = new BusinessUnit().getBusinessUnitByName(company, item);			
			//La asignamos a la sesión
			session.setbUnit(selectedBunit);
			//Actualizamos los usuarios de la unidad de negocio de la sesión
			refreshUserComboBox();
			//Mostramos sus datos
			populateUserFields();
			//Hacemos backup del contenido de los datos del formulario
			updateDataCache();
			//Vaciamos label de información
			infoLabel.setText("");
			
			//Si cambiamos la unidad de negocio de la sesión, comprobamos las condiciones de edición de datos de los usuarios
			//administradores y deshabilitamos la edición de datos si el usuario seleccionado es un usuario dummy
			verifyAdminEditConditions();
			verifyUserSelectedExists();
		}		
	}
	
	/**
	 * Listener que define el comportamiento del checkbox bUnitActiveFilterCheckBox.
	 * Si activamos el checkbox solo visualizaremos los usuarios de las unidades de negocio activas. Si lo deseleccionamos
	 * visualizaremos los usuarios de todas las unidades de negocio. La visualización de usuarios está afectada también
	 * por el propio filtro de usuarios activos.
	 */
	private class BunitCheckBoxListener implements ItemListener {

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
					//Renovamos la lista de las unidades de negocio del comboBox
					refreshBunitComboBox();
					//Actualizamos los usuarios de la unidad de negocio de la sesión
					refreshUserComboBox();
					//Mostramos sus datos
					populateUserFields();
					//Hacemos backup del contenido de los datos del formulario
					updateDataCache();
					//Vaciamos label de información
					infoLabel.setText("");
				//Si la bUnit de la sesión está activa, hay que renovar el combobox igualmente para que ya no salgan las bUnits no activas
				} else {
					//Renovamos la lista de las unidades de negocio del comboBox
					refreshBunitComboBox();
				}
			} else if (state == ItemEvent.DESELECTED) {
				//Renovamos la lista de las unidades de negocio del comboBox
				refreshBunitComboBox();
			}
			
			//Si cambiamos la unidad de negocio de la sesión, comprobamos condiciones de edición de datos de los usuarios
			//administradores y deshabilitamos la edición de datos si el usuario seleccionado es un usuario dummy
			verifyAdminEditConditions();
			verifyUserSelectedExists();
		}
	}
	
	/**
	 * Listener que define el comportamiento del ComboBox userComboBox. Cada elemento se corresponde con
	 * los usuarios de la unidad de negocio seleccionada, que es la unidad de negocio de la sesión.
	 * Si userActiveFilterCheckBox está seleccionado, no se mostrarán los usuarios que estén marcados como no activos
	 */
	private class UserComboListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			
			String item = (String) userComboBox.getSelectedItem();
			//Recuperamos el usuario seleccionado
			userSelected = new User().getUserByAlias(session.getbUnit(), item);
			//Mostramos sus datos
			populateUserFields();
			//Hacemos backup del contenido de los datos del formulario
			updateDataCache();
			//Vaciamos label de información
			infoLabel.setText("");
			
			//Si cambiamos el usuario seleccionado, comprobamos las condiciones de edición de datos de los usuarios manager
			//y administrador y deshabilitamos la edición de datos si el usuario seleccionado es un usuario dummy
			verifyManagerEditConditions();
			verifyAdminEditConditions();
			verifyUserSelectedExists();
		}
		
	}
	
	/**
	 * Listener que define el comportamiento del checkbox userActiveFilterCheckBox. Si activamos el checkbox solo
	 * visualizaremos los usuarios activos de la unidad de negocio seleccionada. Si lo deseleccionamos visualizaremos
	 * todos los usuarios de dicha unidad de negocio. 
	 */
	private class UserCheckBoxListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			//Renovamos la lista de usuarios del comboBox
			refreshUserComboBox();
			//Mostramos sus datos
			populateUserFields();
			//Hacemos backup del contenido de los datos del formulario
			updateDataCache();
			//Vaciamos label de información
			infoLabel.setText("");
			
			//Si el cambio de estado de userActiveFilterCheckBox cambia el usuario seleccionado, comprobamos las condiciones
			//de edición de datos de los usuarios manager y administrador y deshabilitamos la edición de datos si el usuario
			//seleccionado es un usuario dummy
			verifyManagerEditConditions();
			verifyAdminEditConditions();
			verifyUserSelectedExists();
		}
	}
	
	/**
	 * Listener que define el comportamiento del comboBox userTypeComboBox. Cada elemento se corresponde con los tipos de usuarios
	 * que existen en la aplicación: ADMIN, MANAGER y USER. Al crear o editar un usuario se podrá asignar dicho tipo.
	 * 
	 */
	private class UserTypeComboListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			
//			String item = (String) userTypeComboBox.getSelectedItem();
//			if (session.getUser().getUserType().equals("MANAGER") && userSelected.getUserType().equals("ADMIN") 
//					&& !item.equals("ADMIN")) {
//				infoLabel.setText("UN USUARIO MANAGER NO PUEDE CAMBIAR EL TIPO DE USUARIO DE UN USUARIO ADMINISTRADOR");
//			}

//			//Recuperamos la unidad de negocio seleccionada
//			BusinessUnit selectedBunit = new BusinessUnit().getBusinessUnitByName(company, item);			
//			//La asignamos a la sesión
//			session.setbUnit(selectedBunit);

			
//			//Registramos que la unidad de negocio seleccionada es la que se está mostrando
//			bUnitShowing = selectedBunit;
//			//Mostramos sus datos
//			populateTextFields();
//			//Hacemos backup del contenido de los datos del formulario
//			updateDataCache();
//			//Vaciamos label de información
//			infoLabel.setText("");
		}
		
	}
	
	/**
	 * Acción del botón Nueva. Se deshabilita el propio botón, el botón Editar y el combobox. Vaciamos los
	 * campos de texto y habilitamos su edición para añadir la información de un nuevo usuario.
	 * Habilitamos el botón de Cancelar para que los cambios no se registren y el de Aceptar para que sí lo hagan.
	 */
	private class NewAction extends AbstractAction {
		public NewAction() {
			putValue(NAME, "Nuevo");
			putValue(SHORT_DESCRIPTION, "Add new user");
		}
		public void actionPerformed(ActionEvent e) {
			okActionSelector = UserUI.OK_ACTION_NEW;
			//Cambio de estado de los botones y userTypeComboBox
			oKButton.setEnabled(true);
			cancelButton.setEnabled(true);
			userTypeComboBox.setEnabled(true);
			//Actualizamos lista de tipos de usuario
			refreshUserTypeComboBox();
			//Tipo de usuario user habilitado por defecto
			userTypeComboBox.setSelectedIndex(userTypeComboList.length - 1);		
			//Password fields editables
			currentPasswordField.setEditable(true);
			newPasswordField.setEditable(true);
			confirmPasswordField.setEditable(true);
			//Habilitamos checkbox "Activa"
			activeCheckBox.setEnabled(true);
			activeCheckBox.setSelected(true);
			//Activar visibilidad de etiquetas de longitud máxima de datos
			for (JLabel label : labelList) {
				label.setVisible(true);
			}
			//Datos editables y campos vacíos
			for (JTextField tField : textFieldList) {
				if (tField != companyField) {
					tField.setEditable(true);
					tField.setBackground(Color.WHITE);
					tField.setText("");
				}
			}
			infoLabel.setText("");
			//Cambio de estado del resto de botones y combobox
			editButton.setEnabled(false);
			newButton.setEnabled(false);
			bUnitComboBox.setEnabled(false);
			bUnitActiveFilterCheckBox.setEnabled(false);
			userComboBox.setEnabled(false);
			userActiveFilterCheckBox.setEnabled(false);
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
			okActionSelector = UserUI.OK_ACTION_EDIT;
			//Cambio de estado de los botones y el combobox
			oKButton.setEnabled(true);
			cancelButton.setEnabled(true);
			userTypeComboBox.setEnabled(true);
			//Actualizamos lista de tipos de usuario
			refreshUserTypeComboBox();
			//Si la lista de tipos de usuario tiene solo 2 elementos, desplazamos el índice a seleccionar
			if (userTypeComboList.length == 2) {
				userTypeComboBox.setSelectedIndex(lastUserTypeIndex - 1);
			} else {
				userTypeComboBox.setSelectedIndex(lastUserTypeIndex);
			}
			editButton.setEnabled(false);
			newButton.setEnabled(false);
			bUnitComboBox.setEnabled(false);
			bUnitActiveFilterCheckBox.setEnabled(false);
			userComboBox.setEnabled(false);
			userActiveFilterCheckBox.setEnabled(false);
			infoLabel.setText("");
			//Formulario editable
			editableDataOn();
		}
	}
	
	/**
	 * Acción del botón Cancelar. Se deshabilita el propio botón y el botón Aceptar. Se habilita el botón Editar y el
	 * botón Nueva. Descarta los cambios en los datos introducidos en el formulario. No se graban en la base de datos 
	 * ni en el objeto User. Se recupera la información que figuraba anteriormente en el formulario. Se borra 
	 * cualquier mensaje de error mostrado anteriormente
	 */
	private class CancelAction extends AbstractAction {
		public CancelAction() {
			putValue(NAME, "Cancelar");
			putValue(SHORT_DESCRIPTION, "Cancel data edit");
		}
		public void actionPerformed(ActionEvent e) {
			okActionSelector = UserUI.OK_ACTION_UNDEFINED;
			//Cambio de estado de los botones y los combobox
			//Impedimos que un usuario manager pueda editar usuarios admin tras cancelar
			if (session.getUser().getUserType().equals("MANAGER" )) {
				verifyManagerEditConditions();
			} else {
				editButton.setEnabled(true);
			}
			newButton.setEnabled(true);
			if (session.getUser().getUserType().equals("ADMIN")) {
				bUnitComboBox.setEnabled(true);
				bUnitActiveFilterCheckBox.setEnabled(true);
			}
			if (!session.getUser().getUserType().equals("USER")) {
				userComboBox.setEnabled(true);
				userActiveFilterCheckBox.setEnabled(true);
			}
			userTypeComboBox.setEnabled(false);
			oKButton.setEnabled(false);
			cancelButton.setEnabled(false);
			infoLabel.setText("");
			//Formulario no editable
			editableDataOff();		
			//Recuperar valores previos a la edición de los datos
			for (int i = 0; i < textFieldList.size(); i++) {
				textFieldList.get(i).setText(textFieldContentList.get(i));
			}
			//Recuperamos el valor de userTypeComboBox
			userTypeComboList = getUserTypeComboBoxItemsFromSession();
			userTypeComboBox.setModel(new DefaultComboBoxModel(userTypeComboList));
			userTypeComboBox.setSelectedIndex(lastUserTypeIndex);
			//Vaciamos los passwordFields
			currentPasswordField.setText("");
			newPasswordField.setText("");
			confirmPasswordField.setText("");
			//Recuperamos el valor anterior del checkbox "Activa"
			activeCheckBox.setSelected(lastActive);
		}
	}
	
	/**
	 * Acción del botón Aceptar. Se deshabilita el propio botón y el botón Cancelar. Se habilitan los
	 * botones Editar y Nueva. Se intentan guardar los datos del usuario actualizados en la base
	 * de datos, o bien los datos de un nuevo usuario. Si se consigue, se actualiza el objeto User
	 * con dichos datos o se crea uno nuevo. Si no se consigue, no se produce la actualización o la creación
	 * del objeto User y se muestra un mensaje de error. Se intenta guardar el registro
	 * de la actualización de datos en la base de datos. Si no se consigue se muestra un mensaje de error.
	 */
	private class OKAction extends AbstractAction {
		public OKAction() {
			putValue(NAME, "Aceptar");
			putValue(SHORT_DESCRIPTION, "Save new data or data edit");
		}
		public void actionPerformed(ActionEvent e) {
			
		}
	}
}
