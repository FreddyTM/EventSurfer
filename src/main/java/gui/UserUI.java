package main.java.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
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
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
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
	private JTextField companyField;
	private JComboBox bUnitComboBox;
	private JCheckBox bUnitActiveFilterCheckBox;
	private JComboBox userComboBox;
	private JCheckBox userActiveFilterCheckBox;
	private JComboBox userTypeComboBox;
	private JTextField userAliasField;
	private JTextField nameField;
	private JTextField lastNameField;
	private JPasswordField currentPasswordField;
	private JPasswordField newPasswordField;
	private JPasswordField confirmPasswordField;
	private JCheckBox activeCheckBox;
	
	
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
	
	
	
	


	/**
	 * @wbp.parser.constructor
	 */
	public UserUI(CurrentSession session) {
		this.session = session;
		setLayout(null);
		panelVisible = true;
		
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
		
		bUnitActiveFilterCheckBox = new JCheckBox(" solo activas");
		bUnitActiveFilterCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 20));
		bUnitActiveFilterCheckBox.setBounds(666, 175, 154, 25);
		bUnitActiveFilterCheckBox.addItemListener(new BunitCheckBoxListener());
		bUnitActiveFilterCheckBox.setSelected(session.getbUnit().isActivo() ? true : false);
		add(bUnitActiveFilterCheckBox);
		
		bUnitComboList = getBunitComboBoxItemsFromSession(bUnitActiveFilterCheckBox.isSelected());
		bUnitComboBox = new JComboBox(bUnitComboList);
		bUnitComboBox.setSelectedIndex(getSelectedBunitIndexFromArray(bUnitComboList));
		bUnitComboBox.setBounds(260, 175, 400, 25);
		bUnitComboBox.addItemListener(new BunitComboListener());
		bUnitComboBox.setEditable(false);
		bUnitComboBox.setBackground(Color.WHITE);
		add(bUnitComboBox);
		if (!session.getUser().getUserType().equals("ADMIN")) {
			bUnitComboBox.setEnabled(false);
		}
		
		userActiveFilterCheckBox = new JCheckBox(" solo activos");
		userActiveFilterCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userActiveFilterCheckBox.setBounds(666, 225, 154, 25);
		userActiveFilterCheckBox.addItemListener(new UserCheckBoxListener());
		add(userActiveFilterCheckBox);
		
		//userComboList = (String []) new User().getUsersFromDB(session.getConnection(), session.getbUnit()).toArray();
		userComboList = (String[]) session.getbUnit().getUsers().toArray();
		userComboBox = new JComboBox(userComboList);
		//userComboBox = new JComboBox();
		userComboBox.setBounds(260, 225, 400, 25);
		userComboBox.addItemListener(new UserComboListener());
		userComboBox.setEditable(false);
		userComboBox.setBackground(Color.WHITE);
		add(userComboBox);
		if (!session.getUser().getUserType().equals("ADMIN")) {
			userComboBox.setEnabled(false);
		}
		
		userTypeComboList = null;
		//userTypeComboBox = new JComboBox(userTypeComboList);
		userTypeComboBox = new JComboBox();
		userTypeComboBox.setBounds(260, 275, 400, 25);
		userTypeComboBox.addItemListener(new UserTypeComboListener());
		userTypeComboBox.setEditable(false);
		userTypeComboBox.setBackground(Color.WHITE);
		add(userTypeComboBox);
		if (!session.getUser().getUserType().equals("ADMIN")) {
			userTypeComboBox.setEnabled(false);
		}
		
		userAliasField = new JTextField();
		userAliasField.setText((String) null);
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
		//textFieldContentList.add(session.getbUnit().getCompany().getNombre());
		add(userAliasField);
		
		nameField = new JTextField();
		nameField.setText((String) null);
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
		add(nameField);
		
		lastNameField = new JTextField();
		lastNameField.setText((String) null);
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
		add(lastNameField);
		
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
		
		activeCheckBox = new JCheckBox();
		activeCheckBox.setBounds(260, 625, 100, 25);
		activeCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 20));
		activeCheckBox.setSelected(session.getbUnit().isActivo());
		activeCheckBox.setEnabled(false);
		activeCheckBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					//oKButton.requestFocusInWindow();
				}
			}
		});
		//lastActive = activeCheckBox.isSelected();
		add(activeCheckBox);
		
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
	 * @return -1 si la lista está vacía, el índice que corresponda al alias del usuario que abrió sesión si
	 * dicho usuario está en la lista, 0 si el usuario que abrió sesión no está en la lista (equivale a 
	 * seleccionar el primer usuario que aparezca)
	 */
	public int getSelectedUserIndexFromArray(String[] array) {
		//La unidad de negocio no tiene usuarios, o userActiveFilterCheckBox está activados y la unidad de
		//negocio no tiene usuarios activos.
		if (array.length == 0) {
			return -1;
		} else {
			for (int i = 0; i < array.length; i++) {
				if (array[i].equals(session.getUser().getUserAlias())) {
					return i;
				}
			}
		}
		return 0;
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
			
			//*** - NO ACTIVAR TODAVÍA - ***// 
//			String item = (String) bUnitComboBox.getSelectedItem();
//			Company company = session.getCompany();
//			//Recuperamos la unidad de negocio seleccionada
//			BusinessUnit selectedBunit = new BusinessUnit().getBusinessUnitByName(company, item);			
//			//La asignamos a la sesión
//			session.setbUnit(selectedBunit);
			//------------------------------//
			
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
//				//Si la bUnit de la sesión no está activa
//				if (session.getbUnit().isActivo() == false) {		
//					//Buscamos la bUnit del usuario que abrió sesión
//					BusinessUnit userBunit = new BusinessUnit().getBusinessUnitById(session.getCompany(), session.getUser().getbUnit().getId());
//					//La asignamos como bUnit de la sesión
//					session.setbUnit(userBunit);
//					//Registramos que la unidad de negocio seleccionada es la que se está mostrando
//					bUnitShowing = userBunit;
//					//Mostramos sus datos
//					populateTextFields();
//					//Hacemos backup del contenido de los datos del formulario
//					updateDataCache();
//					//Renovamos la lista de las unidades de negocio del comboBox
//					refreshComboBox();
//					//Vaciamos label de información
//					infoLabel.setText("");
//				//Si la bUnit de la sesión está activa, hay que renovar el combobox igualmente para que ya no salgan las bUnits no activas
//				} else {
//					//Renovamos la lista de las unidades de negocio del comboBox
//					refreshComboBox();
//				}
			} else if (state == ItemEvent.DESELECTED) {
				//Renovamos la lista de las unidades de negocio del comboBox
//				refreshComboBox();
			}
		}
	}
	
	/**
	 * Listener que define el comportamiento del comboBox userComboBox. Cada elemento se corresponde con
	 * los usuarios de la unidad de negocio seleccionada, que pasará a ser la unidad de negocio de la sesión.
	 * Si userActiveFilterCheckBox está seleccionado, no se mostrarán los usuarios que estén marcadas como no activos
	 */
	private class UserComboListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			
			//*** - NO ACTIVAR TODAVÍA - ***// 
//			String item = (String) bUnitComboBox.getSelectedItem();
//			Company company = session.getCompany();
//			//Recuperamos la unidad de negocio seleccionada
//			BusinessUnit selectedBunit = new BusinessUnit().getBusinessUnitByName(company, item);			
//			//La asignamos a la sesión
//			session.setbUnit(selectedBunit);
			//------------------------------//
			
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
	 * Listener que define el comportamiento del checkbox userActiveFilterCheckBox. Si activamos el checkbox solo
	 * visualizaremos los usuarios activos de la unidad de negocio seleccionada. Si lo deseleccionamos visualizaremos
	 * todos los usuarios de dicha unidad de negocio. 
	 */
	private class UserCheckBoxListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			
			int state = e.getStateChange();
			if (state == ItemEvent.SELECTED) {
//				//Si la bUnit de la sesión no está activa
//				if (session.getbUnit().isActivo() == false) {		
//					//Buscamos la bUnit del usuario que abrió sesión
//					BusinessUnit userBunit = new BusinessUnit().getBusinessUnitById(session.getCompany(), session.getUser().getbUnit().getId());
//					//La asignamos como bUnit de la sesión
//					session.setbUnit(userBunit);
//					//Registramos que la unidad de negocio seleccionada es la que se está mostrando
//					bUnitShowing = userBunit;
//					//Mostramos sus datos
//					populateTextFields();
//					//Hacemos backup del contenido de los datos del formulario
//					updateDataCache();
//					//Renovamos la lista de las unidades de negocio del comboBox
//					refreshComboBox();
//					//Vaciamos label de información
//					infoLabel.setText("");
//				//Si la bUnit de la sesión está activa, hay que renovar el combobox igualmente para que ya no salgan las bUnits no activas
//				} else {
//					//Renovamos la lista de las unidades de negocio del comboBox
//					refreshComboBox();
//				}
			} else if (state == ItemEvent.DESELECTED) {
				//Renovamos la lista de las unidades de negocio del comboBox
//				refreshComboBox();
			}
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
			
			//*** - NO ACTIVAR TODAVÍA - ***// 
//			String item = (String) bUnitComboBox.getSelectedItem();
//			Company company = session.getCompany();
//			//Recuperamos la unidad de negocio seleccionada
//			BusinessUnit selectedBunit = new BusinessUnit().getBusinessUnitByName(company, item);			
//			//La asignamos a la sesión
//			session.setbUnit(selectedBunit);
			//------------------------------//
			
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
}
