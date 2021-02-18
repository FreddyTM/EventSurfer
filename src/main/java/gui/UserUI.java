package main.java.gui;

import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import main.java.company.BusinessUnit;
import main.java.company.Company;
import main.java.persistence.CurrentSession;
import main.java.persistence.PersistenceManager;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;

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
		
		JLabel selectBunitLabel = new JLabel("Unidad de negocio");
		selectBunitLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		selectBunitLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		selectBunitLabel.setBounds(50, 175, 200, 25);
		add(selectBunitLabel);
		
		JLabel userLabel = new JLabel("Usuario");
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
		

		companyField = new JTextField();
		companyField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		companyField.setBackground(UIManager.getColor(new JPanel().getBackground()));
		companyField.setBounds(260, 125, 400, 25);
		companyField.setText(session.getbUnit().getCompany().getNombre());
		companyField.setEditable(false);
		textFieldList.add(companyField);
		textFieldContentList.add(session.getbUnit().getCompany().getNombre());
		add(companyField);
		
		bUnitComboList = null;
		//bUnitComboBox = new JComboBox(bUnitComboList);
		bUnitComboBox = new JComboBox();
		bUnitComboBox.setBounds(260, 175, 400, 22);
		bUnitComboBox.addItemListener(null);
		add(bUnitComboBox);
		
		bUnitActiveFilterCheckBox = new JCheckBox(" solo activas");
		bUnitActiveFilterCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 20));
		bUnitActiveFilterCheckBox.setBounds(666, 175, 154, 25);
		bUnitActiveFilterCheckBox.addItemListener(new BunitCheckBoxListener());
		add(bUnitActiveFilterCheckBox);
		
	}

	public UserUI(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}

	public UserUI(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public UserUI(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
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
	
}
