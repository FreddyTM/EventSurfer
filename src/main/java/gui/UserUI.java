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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import main.java.company.BusinessUnit;
import main.java.company.Company;
import main.java.company.User;
import main.java.persistence.PersistenceManager;
import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;
import main.java.types_states.TypesStatesContainer;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPasswordField;

/**
 * Muestra la pantalla de creación / edición de usuarios
 * @author Alfred Tomey
 */
public class UserUI extends JPanel {
	
	//Se asignan a la variable okActionSelector para determinar el comportamiento
	//de la acción okAction
	private static final int OK_ACTION_UNDEFINED = 0; //Por defecto
	private static final int OK_ACTION_EDIT = 1;
	private static final int OK_ACTION_NEW = 2;
	
	private CurrentSession session;
	private Timestamp tNow = ToolBox.getTimestampNow();
	//Temporizador de comprobación de cambios en los datos de la sesión
	private Timer timer;

	//Usuario seleccionado en pantalla
	private User selectedUser;
	private JTextField companyField;
	private JComboBox bUnitComboBox = new JComboBox();
	private JCheckBox bUnitActiveFilterCheckBox;
	private JComboBox userComboBox = new JComboBox();
	private JCheckBox userActiveFilterCheckBox;
	private JComboBox userTypeComboBox = new JComboBox();
	private JTextField userAliasField;
	private JTextField userNameField;
	private JTextField userLastNameField;
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
	
	//Pone en pausa la actualización de datos realizada por TimerJob si es la propia instancia
	//del programa la que ha grabado datos nuevos en la base de datos
	private volatile boolean selfUpdate = false;
	

	public UserUI(CurrentSession session) {
		this.session = session;
		setLayout(null);
		selectedUser = session.getUser();
		
		infoLabel = new JLabel();
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setBounds(50, 675, 1000, 25);
		add(infoLabel);
		
		oKButton = new JButton();
		oKButton.setAction(oKAction);
		oKButton.setBounds(727, 725, 89, 23);
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
		
		JLabel selectBunitLabel = new JLabel("Centros de trabajo");
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
		userAliasField.setText(selectedUser.getUserAlias());
		userAliasField.setEditable(false);
		userAliasField.setColumns(10);
		userAliasField.setBounds(260, 325, 400, 25);
		userAliasField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					userNameField.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(userAliasField);
		textFieldContentList.add(selectedUser.getUserAlias());
		add(userAliasField);
		
		userNameField = new JTextField();
		userNameField.setText(selectedUser.getNombre());
		userNameField.setEditable(false);
		userNameField.setColumns(10);
		userNameField.setBounds(260, 375, 400, 25);
		userNameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					userLastNameField.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(userNameField);
		textFieldContentList.add(selectedUser.getNombre());
		add(userNameField);
		
		userLastNameField = new JTextField();
		userLastNameField.setText(selectedUser.getApellido());
		userLastNameField.setEditable(false);
		userLastNameField.setColumns(10);
		userLastNameField.setBounds(260, 425, 400, 25);
		userLastNameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					currentPasswordField.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(userLastNameField);
		textFieldContentList.add(selectedUser.getApellido());
		add(userLastNameField);
		
		activeCheckBox = new JCheckBox();
		activeCheckBox.setBounds(260, 625, 100, 25);
		activeCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 20));
		activeCheckBox.setSelected(selectedUser.isActivo());
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
		ToolBox.setBlackForeground(bUnitComboBox);
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
		if (!session.getUser().getUserType().equals("ADMIN")) {
			userActiveFilterCheckBox.setEnabled(false);
		}
		add(userActiveFilterCheckBox);
		
		userComboList = getUserComboBoxItemsFromSession(userActiveFilterCheckBox.isSelected());
		//Si la lista está vacía
		if (userComboList.length == 0) {
			userComboList = new String[1];
			userComboList[0] = "<Ningún usuario seleccionable>";
		}		
		userComboBox = new JComboBox(userComboList);
		userComboBox.setSelectedIndex(getSelectedUserIndexFromArray(userComboList, true));
		userComboBox.setBounds(260, 225, 400, 25);
		userComboBox.addItemListener(new UserComboListener());
		userComboBox.setEditable(false);
		ToolBox.setBlackForeground(userComboBox);
		userComboBox.setBackground(Color.WHITE);
		add(userComboBox);
		if (session.getUser().getUserType().equals("USER")) {
			userComboBox.setEnabled(false);
		}
		
		userTypeComboList = getUserTypeComboBoxItemsFromSession();
		userTypeComboBox = new JComboBox(userTypeComboList);
		userTypeComboBox.setSelectedIndex(getSelectedUserTypeIndexFromArray(userTypeComboList));
		userTypeComboBox.setBounds(260, 275, 400, 25);
		userTypeComboBox.setEditable(false);
		userTypeComboBox.setEnabled(false);
		ToolBox.setBlackForeground(userTypeComboBox);
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
		
		JLabel maxCharsLabel4 = new JLabel("Min: 8 caracteres");
		maxCharsLabel4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel4.setBounds(670, 515, 146, 25);
		maxCharsLabel4.setVisible(false);
		labelList.add(maxCharsLabel4);
		add(maxCharsLabel4);
		
		JLabel maxCharsLabel5 = new JLabel("Max: 25 caracteres [a-z], [A-Z], [0-9], [*!$%&@#^]");
		maxCharsLabel5.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel5.setBounds(670, 535, 380, 25);
		maxCharsLabel5.setVisible(false);
		labelList.add(maxCharsLabel5);
		add(maxCharsLabel5);
		
		timer = new Timer();
		TimerTask task = new TimerJob();
		timer.scheduleAtFixedRate(task, 5000, session.getPeriod());

	}

	/**
	 * Obtiene la lista de centros de trabajo cargados en el objeto company. Serán todos los que
	 * existan en la base de datos si el usuario que abre sesión es de tipo administrador, y solo uno
	 * (el correspondiente al usuario que abre sesión) si es un usuario de otro tipo
	 * @param active true si se muestran solo los centros de trabajo activos, false para mostrarlos todos
	 * @return array ordenado alfabéticamente con la lista de centros de trabajo
	 */
	private String[] getBunitComboBoxItemsFromSession(boolean active) {
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
	 * Obiene el índice del elemento de bUnitComboBox que será seleccionado por defecto a partir
	 * del array pasado por parámetro
	 * @param array array con la lista de centros de trabajo
	 * @return índice del elemento a seleccionar por defecto
	 */
	private int getSelectedBunitIndexFromArray(String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(session.getbUnit().getNombre())) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Obtiene la lista de usuarios del centro de trabajo de la sesión. Serán todos los usuarios si
	 * userActiveFilterCheckBox está deseleccionado, y solo los usuarios activos si userActiveFilterCheckBox
	 * está seleccionado.
	 * @param active true si se muestran solo los usuarios activos, false para mostrarlos todos
	 * @return array ordenado alfabéticamente con la lista de usuarios
	 */
	private String[] getUserComboBoxItemsFromSession(boolean active) {
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
		return ToolBox.toSortedArray(tempList);
	}
	
	/**
	 * Obiene el índice del elemento de userComboBox que será seleccionado por defecto a partir del array de alias de usuarios
	 * pasado por parámetro. El alias seleccionado será asignado como usuario seleccionado.
	 * @param array array con la lista de usuarios del centro de trabajo de la sesión
	 * @param firstSearch determina si se accede al método por primera vez
	 * @return el índice que corresponda al alias del usuario que abrió sesión, o bien el índice del nuevo usuario seleccionado,
	 * creado o editado (si no queda inactivo y el filtro de usuarios está activo), o bien 0 si el usuario no está en la lista
	 * (se escoge el primer usuario de la lista)
	 */
	private int getSelectedUserIndexFromArray(String[] array, boolean firstSearch) {
		if (firstSearch) {
			for (int i = 0; i < array.length; i++) {
				if (array[i].equals(session.getUser().getUserAlias())) {
					selectedUser = buildUserSelected(new User().getUserByAlias(session.getbUnit(), array[i]));
					firstSearch = false;
					return i;
				}
			} 
		} else {
			for (int i = 0; i < array.length; i++) {
				if (array[i].equals(userAliasField.getText())) {
					selectedUser = buildUserSelected(new User().getUserByAlias(session.getbUnit(), array[i]));
					return i;
				}
			}
		}
		selectedUser = buildUserSelected(new User().getUserByAlias(session.getbUnit(), array[0]));
		return 0;
	}
	
	/**
	 * Devuelve el usuario entrado por parámetro si dicho usuario no es null. Si es null, crea un usuario dummy con
	 * un id (-1) que no existe en la base de datos para poderlo distinguir de cualquier otro usuario real
	 * @param user objeto User, o null
	 * @return usuario entrado por parámetro sin modificar, o un usuario vacío
	 */
	private User buildUserSelected (User user) {
		if (user == null) {
			user = new User(-1, session.getbUnit(), "", "", "", "", "", false);
		}
		return user;
	}
	
	/**
	 * Obtiene la lista de los tipos de usuarios definidos en el programa. Si userSelected es un usuario dummy con id -1
	 * y no estamos creando un usuario nuevo, la lista tendrá un único elemento vacío.
	 * @return lista de tipos de usuarios, o lista con un único elemento vacío
	 */
	private String [] getUserTypeComboBoxItemsFromSession() {
		String[] fullList = TypesStatesContainer.getuType().getUserTypesArray();
		if (selectedUser.getId() == -1 && okActionSelector != UserUI.OK_ACTION_NEW) {
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
	private int getSelectedUserTypeIndexFromArray(String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (selectedUser.getUserType().equals(array[i])) {
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
	private int getSelectedItemIndex (String[] itemList, String itemValue) {
		for (int i = 0; i < itemList.length; i++) {
			if (itemList[i].equals(itemValue)) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Refresca los datos del usuario seleccionado para que se visualicen en pantalla
	 */
	private void populateUserFields() {
		companyField.setText(session.getCompany().getNombre());
		userTypeComboList = getUserTypeComboBoxItemsFromSession();
		userTypeComboBox.setModel(new DefaultComboBoxModel(userTypeComboList));
		userTypeComboBox.setSelectedIndex(getSelectedUserTypeIndexFromArray(userTypeComboList));
		userAliasField.setText(selectedUser.getUserAlias());
		userNameField.setText(selectedUser.getNombre());
		userLastNameField.setText(selectedUser.getApellido());
		//Aunque no es un textfield, el valor de activeCheckBox también hay que mostrarlo actualizado
		activeCheckBox.setSelected(selectedUser.isActivo());
	}
	
	/**
	 * Hace una copia de los datos que figuran en el formulario. Al cancelar la edición o la creación de un
	 * nuev usuario, podremos recuperar por pantalla los datos del último usuario que estaba seleccionado
	 */
	private void updateDataCache() {
		//Vaciamos la lista de datos del caché de datos
		textFieldContentList.clear();
		//Añadimos los nuevos datos
		for (int i = 0; i < textFieldList.size(); i++) {
			textFieldContentList.add(textFieldList.get(i).getText());
		}
		//Guardamos el valor del ckeckbox "Activo"
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
		//El usuario administrador por defecto no puede cambiar su tipo de usuario
		//Un usuario de tipo user no puede cambiar su tipo de usuario
		if (session.getUser().getId() == 1 && selectedUser.getId() == 1 || session.getUser().getUserType().equals("USER")) {
			userTypeComboBox.setEnabled(false);
		//El resto de los usuarios administradores y los usuarios manager sí que pueden cambiar su tipo de usuario
		} else {
			userTypeComboBox.setEnabled(true);
		}
		currentPasswordField.setEditable(true);
		newPasswordField.setEditable(true);
		confirmPasswordField.setEditable(true);
		//Habilitamos checkbox "Activa" si el usuario de la sesión no es de tipo user y si no es el administrador por defecto
		if (session.getUser().getId() == 1 && selectedUser.getId() == 1 || session.getUser().getUserType().equals("USER")) {
			activeCheckBox.setEnabled(false);
		} else {
			activeCheckBox.setEnabled(true);
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
		userTypeComboBox.setEnabled(false);
		//Password fields no editables
		currentPasswordField.setEditable(false);
		currentPasswordField.setBackground(UIManager.getColor(new JPanel().getBackground()));
		newPasswordField.setEditable(false);
		newPasswordField.setBackground(UIManager.getColor(new JPanel().getBackground()));
		confirmPasswordField.setEditable(false);
		confirmPasswordField.setBackground(UIManager.getColor(new JPanel().getBackground()));
		//Vaciamos los passwordFields
		currentPasswordField.setText("");
		newPasswordField.setText("");
		confirmPasswordField.setText("");
		//Deshabilitamos checkbox "Activa"
		activeCheckBox.setEnabled(false);
	}
	
	/**
	 * Devuelve el formulario a su estado previo tras la creación o la edición de una unidad de negocio
	 */
	private void afterNewOrEditUser() {
		//Hacemos backup del contenido de los datos del formulario
		updateDataCache();
		//Formulario no editable
		editableDataOff();
		
		//Cambio de estado de los botones y los combobox
		//Impedimos que un usuario manager pueda editar usuarios admin tras cancelar
		if (session.getUser().getUserType().equals("MANAGER" )) {
			verifyManagerEditConditions();
		} else {
			editButton.setEnabled(true);
			disableEditIfDummyUserSelected();
		}
		newButton.setEnabled(true);
		if (session.getUser().getUserType().equals("ADMIN")) {
			bUnitComboBox.setEnabled(true);
			bUnitActiveFilterCheckBox.setEnabled(true);
		}
		//Impedimos que un usuario user pueda seleccionar usuarios tras cancelar
		if (!session.getUser().getUserType().equals("USER")) {
			userComboBox.setEnabled(true);
			userActiveFilterCheckBox.setEnabled(true);
		}
		userTypeComboBox.setEnabled(false);
		oKButton.setEnabled(false);
		cancelButton.setEnabled(false);
		
		//El selector de acción retorna al estado sin definir
		okActionSelector = UserUI.OK_ACTION_UNDEFINED;

	}
	
	/**
	 * Comprueba la corrección de los datos introducidos en el formulario. Cualquier dato incorrecto se resalta
	 * con el fondo del campo en amarillo
	 * @param userToCheck usuario del que se comprueban los datos
	 * @return true si son correctos, false si no lo son
	 */
	private boolean testData(User userToCheck) {
		//Comprobamos que los datos no exceden el tamaño máximo, no llegan al mínimo, o no hay nombres duplicados
		Boolean errorLength = false;
		String errorAliasText = "YA EXISTE UN USUARIO CON ESE ALIAS";
		String errorLengthText = "TAMAÑO MÁXIMO DE TEXTO SUPERADO O FALTAN DATOS.";
		String errorCurrentPassText = "SI DESEA GUARDAR UNA NUEVA CONTRASEÑA, INTRODUZCA LA CONTRASEÑA ACTUAL";
		String error2CurrentPassText = "CONTRASEÑA ACTUAL INCORRECTA";
		String errorSamePasswordText = "LA NUEVA CONTRASEÑA NO PUEDE SER IGUAL A LA CONTRASEÑA ACTUAL";
		String errorPassLengthText = "CONTRASEÑA DE LONGITUD INCORRECTA.";
		String errorPassTypeText = "LA NUEVA CONTRASEÑA DEBE INCLUIR AL MENOS UNA MAYÚSCULA,"
				+ "UNA MINÚSCULA, UN NÚMERO Y UN CARACTER ESPECIAL";
		String errorPassMatchText = "LA NUEVA CONTRASEÑA Y LA CONFIRMACIÓN NO COINCIDEN";
		
		//Si estamos creando un nuevo usuario y el alias del usuario creado ya existe en alguun centro de trabajo,
		//no se permite la asignación del alias. Los alias son únicos en la base de datos, no pueden tener duplicados
		if (okActionSelector == UserUI.OK_ACTION_NEW) {
			List<BusinessUnit> bUnitList = new BusinessUnit().getBusinessUnitsFromDB(session.getConnection(),
					session.getCompany());
			for (BusinessUnit unit : bUnitList) {
				List<User> userList = new User().getUsersFromDB(session.getConnection(), unit);
				for (User user : userList) {
					if (user.getUserAlias().equals(userToCheck.getUserAlias())) {
						infoLabel.setText(errorAliasText);
						userAliasField.setBackground(Color.YELLOW);
						return false;
					}
				}
			} 
		}
		//Comprobamos la longitud de los datos
		if (userToCheck.getUserAlias().length() > 20 || userToCheck.getUserAlias().length() == 0) {
			userAliasField.setBackground(Color.YELLOW);
			errorLength = true;
		}	
		if (userToCheck.getNombre().length() > 50 || userToCheck.getNombre().length() == 0) {
			userNameField.setBackground(Color.YELLOW);
			errorLength = true;
		}
		if (userToCheck.getApellido().length() > 50 || userToCheck.getApellido().length() == 0) {
			userLastNameField.setBackground(Color.YELLOW);
			errorLength = true;
		}
		//Si hay un error de longitud de datos, mensaje de error y retornamos false
		if (errorLength) {
			infoLabel.setText(errorLengthText);
			return false;
		}
		//Si estamos creando un nuevo usuario
		if (okActionSelector == UserUI.OK_ACTION_NEW) {
			//Comprobamos que la nueva contraseña tiene el tamaño correcto
			if (userToCheck.getPassword().length() > 25 || userToCheck.getPassword().length() < 8) {
				newPasswordField.setBackground(Color.YELLOW);
				infoLabel.setText(errorPassLengthText);
				return false;
			}
			//Comprobamos que la contraseña solo incluye caracteres permitidos
			if (!userToCheck.isAValidPassword(userToCheck.getPassword())) {
				newPasswordField.setBackground(Color.YELLOW);
				infoLabel.setText(errorPassTypeText);
				return false;
			}
			//Comprobamos que la nueva contraseña y la confirmación son iguales
			if (!String.valueOf(newPasswordField.getPassword())
					.equals(String.valueOf(confirmPasswordField.getPassword()))) {
				newPasswordField.setBackground(Color.YELLOW);
				confirmPasswordField.setBackground(Color.YELLOW);
				infoLabel.setText(errorPassMatchText);
				return false;
			} 
		}
		//Si estamos editando un usuario existente
		if (okActionSelector == UserUI.OK_ACTION_EDIT) {
			//Si hay datos en currentPasswordField, newPasswordField o confirmPasswordField
			if (!String.valueOf(currentPasswordField.getPassword()).equals("")
					|| !String.valueOf(newPasswordField.getPassword()).equals("")
					|| !String.valueOf(confirmPasswordField.getPassword()).equals("")) {
				//Comprobamos la contraseña actual del usuario seleccionado
				//Si currentPasswordField está vacío, error
				if (String.valueOf(currentPasswordField.getPassword()).equals("")) {
					currentPasswordField.setBackground(Color.YELLOW);
					infoLabel.setText(errorCurrentPassText);
					return false;
				//Si currentPasswordField no está vacío, comparamos con la contraseña del usuario seleccionado
				} else {
					String hashedPass = new User().passwordHash(String.valueOf(currentPasswordField.getPassword()));
					//Si no coinciden, error
					if (!hashedPass.equals(String.valueOf(selectedUser.getPassword()))) {
						currentPasswordField.setBackground(Color.YELLOW);
						infoLabel.setText(error2CurrentPassText);
						return false;
					} 
				}
				//Comprobamos que la nueva contraseña tiene el tamaño correcto
				if (userToCheck.getPassword().length() > 25 || userToCheck.getPassword().length() < 8) {
					newPasswordField.setBackground(Color.YELLOW);
					infoLabel.setText(errorPassLengthText);
					return false;
				}
				//Comprobamos que la nueva contraseña y la contraseña actual no son iguales
				String hashedPass = new User().passwordHash(String.valueOf(newPasswordField.getPassword()));
				if (hashedPass.equals(String.valueOf(selectedUser.getPassword()))) {
					newPasswordField.setBackground(Color.YELLOW);
					infoLabel.setText(errorSamePasswordText);
					return false;
				}
				//Comprobamos que la contraseña solo incluye caracteres permitidos
				if (!userToCheck.isAValidPassword(userToCheck.getPassword())) {
					newPasswordField.setBackground(Color.YELLOW);
					infoLabel.setText(errorPassTypeText);
					return false;
				}
				//Comprobamos que la nueva contraseña y la confirmación son iguales
				if (!String.valueOf(newPasswordField.getPassword())
						.equals(String.valueOf(confirmPasswordField.getPassword()))) {
					newPasswordField.setBackground(Color.YELLOW);
					confirmPasswordField.setBackground(Color.YELLOW);
					infoLabel.setText(errorPassMatchText);
					return false;
				} 
			} 
		}
		return true;		
	}
	
	/**
	 * Actualiza el contenido del comboBox que selecciona el centro de trabajo activo
	 */
	private void refreshBunitComboBox() {
		bUnitComboList = getBunitComboBoxItemsFromSession(bUnitActiveFilterCheckBox.isSelected());
		bUnitComboBox.setModel(new DefaultComboBoxModel(bUnitComboList));
		bUnitComboBox.setSelectedIndex(getSelectedBunitIndexFromArray(bUnitComboList));
	}
	
	/**
	 * Actualiza el contenido del comboBox que selecciona los usuarios
	 */
	private void refreshUserComboBox() {
		userComboList = getUserComboBoxItemsFromSession(userActiveFilterCheckBox.isSelected());
		//Si la lista está vacía
		if (userComboList.length == 0) {
			userComboList = new String[1];
			userComboList[0] = "<Ningún usuario seleccionable>";
		}
		userComboBox.setModel(new DefaultComboBoxModel(userComboList));
		userComboBox.setSelectedIndex(getSelectedUserIndexFromArray(userComboList, false));
	}
	
	/**
	 * Actualiza el contenido del combobox que selecciona los tipos de usuario. Si el usuario de la sesión
	 * es de tipo manager, y dicho usuario edita un usuario existente o crea uno nuevo, se elimina de la lista
	 * de tipos de usuario el usuario admin porque solo puede crear usuarios de tipo manager o user. 
	 */
	private void refreshUserTypeComboBox() {
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
	private void verifyManagerEditConditions() {

		//Un usuario manager no puede editar los datos de un usuario administrador
		if (session.getUser().getUserType().equals("MANAGER") && selectedUser.getUserType().equals("ADMIN")) {
			editButton.setEnabled(false);
		} else if (session.getUser().getUserType().equals("MANAGER") && !selectedUser.getUserType().equals("ADMIN")) {
			editButton.setEnabled(true);
		}
	}
	
	/**
	 * Si el usuario de la sesión es de tipo administrador, habilita la edición de sus propios datos y los de cualquier
	 * otro usuario que no sea de tipo administrador. Si el usuario de la sesión es el administrador por defecto también
	 * habilita la edición de los datos de cualquier usuario administrador.
	 */
	private void verifyAdminEditConditions() {

		//Si el usuario de la sesión es un usuario administrador y el usuario seleccionado no lo es, habilitamos
		//siempre la edición del usuario seleccionado
		if (session.getUser().getUserType().equals("ADMIN") && !selectedUser.getUserType().equals("ADMIN")) {
			editButton.setEnabled(true);
		}
		
		//Los datos del usuario administrador por defecto no pueden ser modificados por ningún otro usuario administrador
		if (session.getUser().getUserType().equals("ADMIN") && session.getUser().getId() != 1 && 
				selectedUser.getId() == 1) {
			editButton.setEnabled(false);
		} else if (session.getUser().getUserType().equals("ADMIN") && session.getUser().getId() == 1) {
			editButton.setEnabled(true);
		}
		
		//Los datos de un usuario administrador solo pueden ser modificados por si mismo y por el usuario administrador
		//por defecto
		if (session.getUser().getUserType().equals("ADMIN") && session.getUser().getId() == selectedUser.getId()) {
			editButton.setEnabled(true);
		} else if (session.getUser().getUserType().equals("ADMIN") && session.getUser().getId() != 1 &&
				selectedUser.getUserType().equals("ADMIN")) {
			editButton.setEnabled(false);
		}
	}
	
	/**
	 * Si userSelected es un usuario dummy con id -1, la edición de datos se deshabilita
	 */
	private void disableEditIfDummyUserSelected() {
		if (selectedUser.getId() == -1) {
			editButton.setEnabled(false);
		}
	}
	
	/**
	 * Listener que define el comportamiento del comboBox bUnitComboBox. Cada elemento se corresponde con
	 * los centros de trabajo de la compañía que se han cargado en la sesión. Por el nombre seleccionado
	 * se localiza el objeto BusinessUnit al que pertenece y se asigna dicho objeto como centro de trabajo
	 * de la sesión, reemplazando al que hubiera hasta ese momento. Si activeFilterCheckBox está seleccionado,
	 * no se mostrarán los centros de trabajo que estén marcados como no activos
	 */
	private class BunitComboListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
					
			String item = (String) bUnitComboBox.getSelectedItem();
			Company company = session.getCompany();
			//Recuperamos el centro de trabajo seleccionado
			BusinessUnit selectedBunit = new BusinessUnit().getBusinessUnitByName(company, item);			
			//La asignamos a la sesión
			session.setbUnit(selectedBunit);
			//Actualizamos los usuarios del centro de trabajo de la sesión
			refreshUserComboBox();
			//Mostramos los datos del usuario seleccionado
			populateUserFields();
			//Hacemos backup del contenido de los datos del formulario
			updateDataCache();
			//Vaciamos label de información
			infoLabel.setText("");
			
			//Si cambiamos el centro de trabajo de la sesión, comprobamos las condiciones de edición de datos de los usuarios
			//administradores y deshabilitamos la edición de datos si el usuario seleccionado es un usuario dummy
			verifyAdminEditConditions();
			disableEditIfDummyUserSelected();
		}		
	}
	
	/**
	 * Listener que define el comportamiento del checkbox bUnitActiveFilterCheckBox.
	 * Si activamos el checkbox solo visualizaremos los usuarios de los centros de trabajo activos. Si lo deseleccionamos
	 * visualizaremos los usuarios de todos los centros de trabajo. La visualización de usuarios está afectada también
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
					//Renovamos la lista de los centros de trabajo del comboBox
					refreshBunitComboBox();
					//Actualizamos los usuarios del centro de trabajo de la sesión
					refreshUserComboBox();
					//Mostramos los datos del usuario seleccionado
					populateUserFields();
					//Hacemos backup del contenido de los datos del formulario
					updateDataCache();
					//Vaciamos label de información
					infoLabel.setText("");
				//Si el centro de trabajo de la sesión está activo, hay que renovar el combobox igualmente para que ya no salgan los centros de trabajo
				//no activos
				} else {
					//Renovamos la lista de los centros de trabajo del comboBox
					refreshBunitComboBox();
				}
			} else if (state == ItemEvent.DESELECTED) {
				//Renovamos la lista de los centros de trabajo del comboBox
				refreshBunitComboBox();
			}
			
			//Si cambiamos el centro de trabajo de la sesión, comprobamos condiciones de edición de datos de los usuarios
			//administradores y deshabilitamos la edición de datos si el usuario seleccionado es un usuario dummy
			verifyAdminEditConditions();
			disableEditIfDummyUserSelected();
		}
	}
	
	/**
	 * Listener que define el comportamiento del ComboBox userComboBox. Cada elemento se corresponde con
	 * los usuarios del centro de trabajo seleccionado, que es el centro de trabajo de la sesión.
	 * Si userActiveFilterCheckBox está seleccionado, no se mostrarán los usuarios que estén marcados como no activos
	 */
	private class UserComboListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			
			String item = (String) userComboBox.getSelectedItem();
			//Recuperamos el usuario seleccionado
			selectedUser = new User().getUserByAlias(session.getbUnit(), item);
			//Mostramos los datos del usuario seleccionado
			populateUserFields();
			//Hacemos backup del contenido de los datos del formulario
			updateDataCache();
			//Vaciamos label de información
			infoLabel.setText("");
			
			//Si cambiamos el usuario seleccionado, comprobamos las condiciones de edición de datos de los usuarios manager
			//y administrador y deshabilitamos la edición de datos si el usuario seleccionado es un usuario dummy
			verifyManagerEditConditions();
			verifyAdminEditConditions();
			disableEditIfDummyUserSelected();
		}	
	}
	
	/**
	 * Listener que define el comportamiento del checkbox userActiveFilterCheckBox. Si activamos el checkbox solo
	 * visualizaremos los usuarios activos del centro de trabajo seleccionado. Si lo deseleccionamos visualizaremos
	 * todos los usuarios de dicho centro de trabajo. 
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
			disableEditIfDummyUserSelected();
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
			
			userTypeComboBox.requestFocusInWindow();
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
			
			userAliasField.requestFocusInWindow();
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
			if (session.getUser().getUserType().equals("ADMIN")) {
				bUnitComboBox.setEnabled(true);
				bUnitActiveFilterCheckBox.setEnabled(true);
			}
			//Impedimos que un usuario user pueda seleccionar usuarios tras cancelar
			//Botón "nuevo" deshabilitado para un usuario user tras cancelar
			if (!session.getUser().getUserType().equals("USER")) {
				userComboBox.setEnabled(true);
				userActiveFilterCheckBox.setEnabled(true);
				newButton.setEnabled(true);
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
		public synchronized void actionPerformed(ActionEvent e) {
			//Se recupera el fondo blanco de los campos para que una anterior validación errónea de los mismos
			//no los deje amarillos permanentemente
			for (JTextField tField : textFieldList) {
				if (tField != companyField) {
					tField.setBackground(Color.WHITE);
				}
			}
			newPasswordField.setBackground(Color.WHITE);
			confirmPasswordField.setBackground(Color.WHITE);
			//Selección de comportamiento
			
			try {
				selfUpdate = true;
				
				System.out.println("Grabación de datos propios iniciada, actualizaciones suspendidas................");
				
				//Aceptamos la creación de un nuevo usuario
				if (okActionSelector == UserUI.OK_ACTION_NEW) {
					
					//Creamos un nuevo usuario a partir de los datos del formulario
					User newUser = new User();
					newUser.setbUnit(session.getbUnit());
					newUser.setUserType(userTypeComboBox.getSelectedItem().toString());
					newUser.setUserAlias(userAliasField.getText());
					newUser.setNombre(userNameField.getText());
					newUser.setApellido(userLastNameField.getText());
					//Password en texto plano
					newUser.setPassword(String.valueOf(newPasswordField.getPassword()));
					newUser.setActivo(activeCheckBox.isSelected());
					//Validamos los datos del formulario
					if (testData(newUser)) {
						//Aplicamos hash a la contraseña del nuevo usuario
						newUser.setPassword(newUser.passwordHash(String.valueOf(newUser.getPassword())));
						
						//Intentamos grabar el nuevo usuario en la base de datos, retornando un objeto con idénticos
						//datos que incluye también el id que le ha asignado dicha base de datos
						User storedUser = new User().addNewUser(session.getConnection(), newUser);
						//Si el usuario se almacena correctamente en la base de datos
						if (storedUser != null) {
							//Registramos fecha y hora de la actualización de los datos de la tabla business_unit
							tNow = ToolBox.getTimestampNow();
							infoLabel.setText("NUEVO USUARIO REGISTRADO EN " + session.getbUnit().getNombre() + ": "  + ToolBox.formatTimestamp(tNow, null));
							//Actualizamos los datos de la tabla last_modification
							boolean changeRegister = PersistenceManager.updateTimeStampToDB(session.getConnection(), User.TABLE_NAME, tNow);
							//Si se produce un error de actualización de la tabla last_modification. La actualización de la tabla user
							//no queda registrada
							if(!changeRegister) {
								infoLabel.setText(infoLabel.getText() + " .ERROR DE REGISTRO DE ACTUALIZACIÓN");
							}
							//Añadimos al nuevo usuario a la lista de usuarios del centro de trabajo de la sesión
							session.getbUnit().getUsers().add(storedUser);
							
							//Si el filtro de usuarios está activo y el nuevo usuario se crea como no activo, no puede asignarse como usuario
							//seleccionado y por tanto tampoco puede visualizarse al aceptar su creación
							if (userActiveFilterCheckBox.isSelected() && storedUser.isActivo() == false) {
								
								//Mostramos los datos del anterior usuario seleccionado. No renovamos el contenido de userComboBox porque
								//no es necesario. Tampoco hacemos backup del contenido de los datos del formulario, se mantienen los anteriores
								populateUserFields();
							
							//Si el filtro de usuarios está activo y el usuario se crea como activo, pasa a ser el usuario seleccionado.
							//Si el filtro de usuarios no está activo, el nuevo usuario pasa a ser el usuario seleccionado tanto si se
							//crea como activo como si no
							} else {
								selectedUser = storedUser;
								//Renovamos la lista de usuarios del comboBox
								userComboList = getUserComboBoxItemsFromSession(userActiveFilterCheckBox.isSelected());
								userComboBox.setModel(new DefaultComboBoxModel(userComboList));
								for (int i = 0; i < userComboList.length; i++) {
									if (userComboList[i].equals(selectedUser.getUserAlias())) {
										userComboBox.setSelectedIndex(i);
									}
								}
							}
							//Retornamos el formulario a su estado previo a la creación de un nuevo usuario
							afterNewOrEditUser();

						//Si el usuario no se almacena correctamente en la base de datos 
						} else {
							infoLabel.setText("ERROR DE GRABACIÓN DEL NUEVO USUARIO EN LA BASE DE DATOS");
						}
					}
				
				//Aceptamos los cambios del usuario editado
				} else if (okActionSelector == UserUI.OK_ACTION_EDIT) {
					
					currentPasswordField.setBackground(Color.WHITE);
					//Objeto que recoge los datos actualizados
					User updatedUser = new User();
					updatedUser.setId(selectedUser.getId());
					updatedUser.setbUnit(selectedUser.getbUnit());
					updatedUser.setUserType(userTypeComboBox.getSelectedItem().toString());
					updatedUser.setUserAlias(userAliasField.getText());
					updatedUser.setNombre(userNameField.getText());
					updatedUser.setApellido(userLastNameField.getText());
					//Password en texto plano
					updatedUser.setPassword(String.valueOf(newPasswordField.getPassword()));
					updatedUser.setActivo(activeCheckBox.isSelected());
					
					//Validamos los datos del formulario
					if (testData(updatedUser)) {
						//Si no hay contraseña nueva dejamos la del usuario seleccionado
						if (updatedUser.getPassword().equals("")) {
							updatedUser.setPassword(selectedUser.getPassword());
						//Si hay contraseña nueva le aplicamos el hash
						} else {
							updatedUser.setPassword(new User().passwordHash(String.valueOf(updatedUser.getPassword())));
						}
						//Si los datos actualizados se graban en la base de datos
						if (new User().updateUserToDB(session.getConnection(), updatedUser)) {
							//Registramos fecha y hora de la actualización de los datos de la tabla user
							tNow = ToolBox.getTimestampNow();
							//Control de la actualización de la tabla last_modification por el cambio en la tabla user
							boolean UserChangeRegister = PersistenceManager.updateTimeStampToDB(session.getConnection(),
									User.TABLE_NAME, tNow);
							infoLabel.setText("DATOS DEL USUARIO ACTUALIZADOS: " + ToolBox.formatTimestamp(tNow, null));
							//Variable de control para saber si la sesión sigue activa tras la edición de un usuario
							boolean stillOpenSession = true;
							//Localizar en la lista de usuarios del centro de trabajo de la sesión al usuario con el mismo id que el usuario editado 
							// y suprimirlo
					        Iterator<User> iter = session.getbUnit().getUsers().iterator();
					        while (iter.hasNext()) {
					            User user = iter.next();
					            if (user.getId() == updatedUser.getId()) {
					                iter.remove();
					            }
					        }
							
							//Si el usuario que abre sesión deja activo al usuario editado, se actualizan los datos de la sesión
							//Si el usuario que abre sesión deja inactivo a un usuario que no es él mismo y el filtro de usuarios está inactivo,
							//también se actualizan los datos de la sesión
							if (updatedUser.isActivo() || (!updatedUser.isActivo() && updatedUser.getId() != session.getUser().getId()
									&& !userActiveFilterCheckBox.isSelected())) {
						        //El usuario editado pasa a ser el usuario seleccionado, y lo añadimos a la lista de usuarios de la unidad de negocio
								//de la sesión
								selectedUser = updatedUser;
						        session.getbUnit().getUsers().add(selectedUser);
						        //Renovamos la lista de usuarios del comboBox y recuperamos al anterior usuario seleccionado
								refreshUserComboBox();
								//Mostramos los datos del usuario seleccionado
								populateUserFields();
						        //Si se produce un error de actualización de la tabla last_modification. La actualización de la tabla user
								//no queda registrada
								if(!UserChangeRegister) {
									infoLabel.setText(infoLabel.getText() + " . ERROR DE REGISTRO DE ACTUALIZACIÓN");
								}
							
							//Si el usuario que abre sesión deja inactivo a un usuario que no es él mismo y el filtro de usuarios está activo,
							//el usuario editado no puede seguir siendo el usuario seleccionado y por tanto tampoco puede visualizarse
							} else if (!updatedUser.isActivo() && updatedUser.getId() != session.getUser().getId()
									&& userActiveFilterCheckBox.isSelected()) {
								//Añadimos al usuario editado a la lista de usuarios de la unidad de negocio de la sesión
								session.getbUnit().getUsers().add(updatedUser);
								//Renovamos la lista de usuarios del comboBox y recuperamos al anterior usuario seleccionado
								refreshUserComboBox();
								//Mostramos los datos del usuario seleccionado
								populateUserFields();

							//Si el usuario que abre sesión deja inactivo su propio usuario	
							} else if (!updatedUser.isActivo() && updatedUser.getId() == session.getUser().getId()) {
								//Se cerrará la sesión
								stillOpenSession = false;
								//Cerrar sesión y volver a login. El usuario que abrió sesión ya no puede hacer login porque ha sido desactivado
								session.setUsersUpdated(true);
								session.backToLogin(User.TABLE_NAME, session.getDisplays(), session.getCurrentDisplay());
								
							}
							//Si la sesión sigue abierta
							if (stillOpenSession) {
								//Devolvemos el formulario a su estado previo
								afterNewOrEditUser();
							}	
							
						//Si los datos actualizados no se graban en la base de datos
						} else {
							infoLabel.setText("ERROR DE ACTUALIZACIÓN DE DATOS DE USUARIO EN LA BASE DE DATOS");
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
			if (!UserUI.this.isShowing()) {
				this.cancel();
				UserUI.this.timer.cancel();
				 System.out.println("Se ha cerrado la ventana Usuarios");
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
					System.out.println("AreaUI esperando permiso para refrescar datos......");
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//No se comprueba la actualización de los datos si los estamos editando o añadiendo
			if (cancelButton.isEnabled() && oKButton.isEnabled() && UserUI.this.isShowing()) {
				//Do nothing
			//Se comprueba la actualización de los datos si no los estamos modificando
			} else if (UserUI.this.isShowing()){
				
				//Debug
				System.out.println("session.dateTimeReference = tNow: " + (session.getDateTimeReference().equals(tNow)));
				//Si los datos actualilzados en la base de datos provienen de la propia pantalla, no actualizamos los datos visualizados
				//porque no es necesario. En caso contrario, sí que actualizamos.
				if (!session.getDateTimeReference().equals(tNow) ) {
					
					//Debug
					System.out.println("session.dateTimeReference: " + session.getDateTimeReference());
					System.out.println("tNow: " + tNow);
					
					
					//Loop por el Map de CurrentSession, si aparece la tabla user, recargar datos
					for (Map.Entry<String, Timestamp> updatedTable : session.getUpdatedTables().entrySet()) {
						//Si en la tabla de actualizaciones aparece la clave User.TABLE_NAME
						if (updatedTable.getKey().equals(User.TABLE_NAME)) {

							//Si el centro de trabajo de la sesión ha sido marcada como no activa y el filtro de centros de trabajo está
							//activado, el centro de trabajo de la sesión pasa a ser el del usuario que abrió sesión
							if (bUnitActiveFilterCheckBox.isSelected() && session.getbUnit().isActivo() == false) {
								//Buscamos el centro de trabajo del usuario que abrió sesión
								BusinessUnit userBunit = new BusinessUnit().getBusinessUnitById(session.getCompany(),
										session.getUser().getbUnit().getId());
								//Lo asignamos como centro de trabajo de la sesión
								session.setbUnit(userBunit);
								//Renovamos la lista de los centros de trabajo del comboBox
								refreshBunitComboBox();
							}

							//Si el usuario seleccionado ha sido desactivado y el filtro de usuarios está activo, el usuario
							//seleccionado pasará a ser el usuario de la sesión (si estamos mostrando el centro de trabajo de dicho usuario)
							//o el primer usuario de la lista de usuarios de cualquier otro centro de trabajo que estemos mostrando
							//(si hay alguno que esté activo), y será este usuario (o ninguno) el que visualicemos
							//Si el usuario seleccionado no ha sido desactivado, o lo ha sido pero el filtro de usuarios no está activo,
							//seguirá siendo el usuario seleccionado y visualizaremos sus datos actualizados

							//Renovamos la lista de usuarios del comboBox y designamos un nuevo usuario seleccionado
							refreshUserComboBox();
							//Mostramos los datos del usuario seleccionado
							populateUserFields();
							//Hacemos backup del contenido de los datos del formulario
							updateDataCache();

							//Informamos por pantalla de la actualización
							//Si el usuario seleccionado no ha sufrido ninguna modificación no habrá ningún cambio en la información
							//mostrada, pero seguirá interesando saber que algún usuario ha sido modificado o añadido
							UserUI.this.infoLabel.setText("DATOS DE LOS USUARIOS ACTUALIZADOS: "
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
