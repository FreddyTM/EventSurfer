package main.java.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import main.java.company.BusinessUnit;

//import main.java.gui.CompanyUI.CancelAction;
//import main.java.gui.CompanyUI.EditAction;
//import main.java.gui.CompanyUI.OKAction;
//import main.java.gui.CompanyUI.TimerJob;
import main.java.persistence.CurrentSession;
import main.java.persistence.PersistenceManager;

public class BusinessUnitUI extends JPanel {
	
	private CurrentSession session;
	Timestamp tNow = PersistenceManager.getTimestampNow();
	//Temporizador de comprobación de cambios en los datos de la sesión
	private Timer timer;
	private boolean panelVisible;
	private JTextField nameField;
	private JTextField addressField;
	private JTextField provinceField;
	private JTextField stateField;
	private JTextField postalCodeField;
	private JTextField telephoneField;
	private JTextField mailField;
	//private JTextField webField;
	private JButton editButton;
	private JButton cancelButton;
	private JButton oKButton;
	private JLabel infoLabel;
	private List<JLabel> labelList = new ArrayList<JLabel>();
	private List<JTextField> textFieldList = new ArrayList<JTextField>();
	private List<String> textFieldContentList = new ArrayList<String>();
	private final Action editAction = new EditAction();
	private final Action cancelAction = new CancelAction();
	private final Action oKAction = new OKAction();

	/**
	 * @wbp.parser.constructor
	 */
	public BusinessUnitUI(CurrentSession session) {

		this.session = session;
		setLayout(null);
		panelVisible = true;
		
		JTextPane bUnitTxt = new JTextPane();
		bUnitTxt.setFont(new Font("Tahoma", Font.BOLD, 20));
		bUnitTxt.setText("DATOS DE LA UNIDAD DE NEGOCIO");
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
		
		JLabel nameLabel = new JLabel("Nombre");
		nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		nameLabel.setBounds(50, 175, 200, 25);
		add(nameLabel);
		
		JLabel addressLabel = new JLabel("Dirección");
		addressLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		addressLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		addressLabel.setBounds(50, 225, 200, 25);
		add(addressLabel);
		
		JLabel provinceLabel = new JLabel("Provincia");
		provinceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		provinceLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		provinceLabel.setBounds(50, 275, 200, 25);
		add(provinceLabel);
		
		JLabel stateLabel = new JLabel("Estado");
		stateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		stateLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		stateLabel.setBounds(50, 325, 200, 25);
		add(stateLabel);
		
		JLabel postalCodeLabel = new JLabel("Código postal");
		postalCodeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		postalCodeLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		postalCodeLabel.setBounds(50, 375, 200, 25);
		add(postalCodeLabel);
		
		JLabel telephoneLabel = new JLabel("Teléfono");
		telephoneLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		telephoneLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		telephoneLabel.setBounds(50, 425, 200, 25);
		add(telephoneLabel);
		
		JLabel mailLabel = new JLabel("E-mail");
		mailLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		mailLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		mailLabel.setBounds(50, 475, 200, 25);
		add(mailLabel);
		
		JLabel companyValueLabel = new JLabel();
		//companyValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		companyValueLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		companyValueLabel.setBounds(260, 125, 400, 25);
		companyValueLabel.setText(session.getCompany().getNombre());
		add(companyValueLabel);
		
		nameField = new JTextField();
		nameField.setColumns(10);
		nameField.setBounds(260, 175, 400, 25);
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
		addressField.setBounds(260, 225, 400, 25);
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
		provinceField.setBounds(260, 275, 200, 25);
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
		stateField.setBounds(260, 325, 200, 25);
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
		postalCodeField.setBounds(260, 375, 100, 25);
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
		telephoneField.setBounds(260, 425, 100, 25);
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
		mailField.setBounds(260, 475, 400, 25);
		mailField.setText(session.getbUnit().getMail());
		mailField.setEditable(false);
		mailField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					oKButton.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(mailField);
		textFieldContentList.add(session.getbUnit().getMail());
		add(mailField);
		
//		webField = new JTextField();
//		webField.setColumns(10);
//		webField.setBounds(260, 475, 400, 25);
//		webField.setText(session.getCompany().getWeb());
//		webField.setEditable(false);
//		webField.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyPressed(KeyEvent e) {
//				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//					oKButton.requestFocusInWindow();
//				}
//			}
//		});
//		textFieldList.add(webField);
//		textFieldContentList.add(session.getCompany().getWeb());
//		add(webField);
		
		JLabel maxCharsLabel = new JLabel("Max: 100 caracteres");
		maxCharsLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel.setBounds(670, 175, 150, 25);
		maxCharsLabel.setVisible(false);
		labelList.add(maxCharsLabel);
		add(maxCharsLabel);
		
		JLabel maxCharsLabel2 = new JLabel("Max: 150 caracteres");
		maxCharsLabel2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel2.setBounds(670, 225, 150, 25);
		maxCharsLabel2.setVisible(false);
		labelList.add(maxCharsLabel2);
		add(maxCharsLabel2);
		
		JLabel maxCharsLabel3 = new JLabel("Max: 50 caracteres");
		maxCharsLabel3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel3.setBounds(470, 277, 150, 25);
		maxCharsLabel3.setVisible(false);
		labelList.add(maxCharsLabel3);
		add(maxCharsLabel3);
		
		JLabel maxCharsLabel4 = new JLabel("Max: 50 caracteres");
		maxCharsLabel4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel4.setBounds(470, 327, 150, 25);
		maxCharsLabel4.setVisible(false);
		labelList.add(maxCharsLabel4);
		add(maxCharsLabel4);
		
		JLabel maxCharsLabel5 = new JLabel("Max: 8 caracteres");
		maxCharsLabel5.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel5.setBounds(370, 377, 150, 25);
		maxCharsLabel5.setVisible(false);
		labelList.add(maxCharsLabel5);
		add(maxCharsLabel5);
		
		JLabel maxCharsLabel6 = new JLabel("Max: 15 caracteres");
		maxCharsLabel6.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel6.setBounds(370, 425, 150, 25);
		maxCharsLabel6.setVisible(false);
		labelList.add(maxCharsLabel6);
		add(maxCharsLabel6);
		
		JLabel maxCharsLabel7 = new JLabel("Max: 100 caracteres");
		maxCharsLabel7.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel7.setBounds(670, 475, 150, 25);
		maxCharsLabel7.setVisible(false);
		labelList.add(maxCharsLabel7);
		add(maxCharsLabel7);
		
//		JLabel maxCharsLabel8 = new JLabel("Max: 200 caracteres");
//		maxCharsLabel8.setFont(new Font("Tahoma", Font.PLAIN, 15));
//		maxCharsLabel8.setBounds(670, 475, 150, 25);
//		maxCharsLabel8.setVisible(false);
//		labelList.add(maxCharsLabel8);
//		add(maxCharsLabel8);
		
		infoLabel = new JLabel();
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setBounds(50, 530, 770, 25);
		add(infoLabel);
		
		oKButton = new JButton();
		oKButton.setAction(oKAction);
		oKButton.setBounds(731, 580, 89, 23);
		oKButton.setEnabled(false);
		add(oKButton);
		
		cancelButton = new JButton();
		cancelButton.setAction(cancelAction);
		cancelButton.setBounds(632, 580, 89, 23);
		cancelButton.setEnabled(false);
		add(cancelButton);
		
		editButton = new JButton();
		editButton.setAction(editAction);
		editButton.setBounds(531, 580, 89, 23);
		if (!session.getUser().getUserType().equals("ADMIN")) {
			editButton.setEnabled(false);
		}
		add(editButton);
		
		/*Iniciamos la comprobación periódica de actualizaciones
		* Se realiza 2 veces por cada comprobación de los cambios en la base de datos que hace
		* el objeto session. Esto evita que si se produce la comprobación de datos que hace cada panel
		* cuando la actualización de datos que hace el objeto session aún no ha finalizado, se considere
		* por error que no había cambios.
		* Existe la posibilidad de que eso ocurra porque se comprueban y actualizan los datos de cada tabla
		* de manera consecutiva. Si a media actualización de los datos, un panel comprueba los datos que le
		* atañen y su actualización aún no se ha hecho, no los actualizará. Además, el registro de cambios
		* interno del objeto session se sobreescribirá en cuanto inicie una nueva actualización, y el panel
		* nunca podrá reflejar los cambios. Esto pasaría si la actualización del panel se hace al mismo ritmo
		* o más lenta que la comprobación de los datos que hace el objeto session.
		*/
		timer = new Timer();
		TimerTask task = new TimerJob();
		timer.scheduleAtFixedRate(task, 1000, 30000);

	}
	
	/**
	 * Actualiza los datos de la compañía que se visualizan en pantalla
	 */
	public void populateTextFields() {
		nameField.setText(session.getbUnit().getNombre());
		addressField.setText(session.getbUnit().getDireccion());
		provinceField.setText(session.getbUnit().getProvincia());
		stateField.setText(session.getbUnit().getEstado());
		postalCodeField.setText(session.getbUnit().getCpostal());
		telephoneField.setText(session.getbUnit().getTelefono());
		mailField.setText(session.getbUnit().getMail());
		//webField.setText(session.getCompany().getWeb());
	}
	
	/**
	 * Comprueba la corrección de los datos introducidos en el formulario
	 * @return true si son correctos, false si no lo son
	 */
	public boolean testData(BusinessUnit bUnit) {
		//Comprobamos que los datos no exceden el tamaño máximo
		Boolean error = false;
		String errorText = "LA LONGITUD DE LOS DATOS EXCEDE EL TAMAÑO MÁXIMO";
		if (bUnit.getNombre().length() > 100) {
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
			infoLabel.setText(errorText);
			return false;
		}
		return true;
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
			editButton.setEnabled(false);
			oKButton.setEnabled(true);
			cancelButton.setEnabled(true);
			infoLabel.setText("");
			//Activar visibilidad de etiquetas de longitud máxima de datos
			for (JLabel label : labelList) {
				label.setVisible(true);
			}
			//Datos editables
			for (JTextField tField : textFieldList) {
				tField.setEditable(true);
				tField.setBackground(Color.WHITE);
			}
		}
	}
	
	/**
	 * Acción del botón Cancelar. Se deshabilita el propio botón y el botón Aceptar. Se habilita el botón Editar.
	 * Descarta los cambios en los datos introducidos en el formulario. No se graban en la base de datos ni en el
	 * objeto businessUnit. Se recupera la información que figuraba anteriormente en el formulario. Se borra cualquier
	 * mensaje de error mostrado anteriormente
	 */
	private class CancelAction extends AbstractAction {
		public CancelAction() {
			putValue(NAME, "Cancelar");
			putValue(SHORT_DESCRIPTION, "Cancel data edit");
		}
		public void actionPerformed(ActionEvent e) {
			editButton.setEnabled(true);
			oKButton.setEnabled(false);
			cancelButton.setEnabled(false);
			infoLabel.setText("");
			//Quitar visibilidad de etiquetas de longitud máxima de datos
			for (JLabel label : labelList) {
				label.setVisible(false);
			}
			//Datos no editables
			for (JTextField tField : textFieldList) {
				tField.setBackground(UIManager.getColor(new JPanel().getBackground()));
				tField.setEditable(false);
			}
			//Recuperar valores previos a la edición de los datos
			for (int i = 0; i < textFieldList.size(); i++) {
				textFieldList.get(i).setText(textFieldContentList.get(i));
			}
			
		}
	}
	
	/**
	 * Acción del botón Aceptar. Se deshabilita el propio botón y el botón Cancelar. Se habilita el
	 * botón Editar. Se intenta guardar los datos de la compañía actualizados en la base de datos.
	 * Si se consigue, se actualiza el objeto businessUnit con dichos datos. Si no se consigue, no se actualiza
	 * el objeto businessUnit con los datos y se muestra un mensaje de error. Se intenta guardar el registro
	 * de la actualización de datos en la base de datos si dicha actualización tuvo éxito. Si no se consigue
	 * se muestra un mensaje de error
	 */
	private class OKAction extends AbstractAction {
		public OKAction() {
			putValue(NAME, "Aceptar");
			putValue(SHORT_DESCRIPTION, "Execute data edit");
		}
		public void actionPerformed(ActionEvent e) {
			//Objeto que recoge los datos actualizados
			BusinessUnit updatedBunit = new BusinessUnit();
			updatedBunit.setId(session.getbUnit().getId());
			updatedBunit.setNombre(nameField.getText());
			updatedBunit.setDireccion(addressField.getText());
			updatedBunit.setProvincia(provinceField.getText());
			updatedBunit.setEstado(stateField.getText());
			updatedBunit.setCpostal(postalCodeField.getText());
			updatedBunit.setTelefono(telephoneField.getText());
			updatedBunit.setMail(mailField.getText());

			//Si los datos están validados
			if (testData(updatedBunit)) {
				//Si los datos actualizados se graban en la base de datos, se actualizan los datos de la sesión
				if (new BusinessUnit().updateBusinessUnitToDB(session.getConnection(), updatedBunit)) {
					session.getbUnit().setNombre(updatedBunit.getNombre());
					session.getbUnit().setDireccion(updatedBunit.getDireccion());
					session.getbUnit().setProvincia(updatedBunit.getProvincia());
					session.getbUnit().setEstado(updatedBunit.getEstado());
					session.getbUnit().setCpostal(updatedBunit.getCpostal());
					session.getbUnit().setTelefono(updatedBunit.getTelefono());
					session.getbUnit().setMail(updatedBunit.getMail());

					//Registramos fecha y hora de la actualización de los datos de la tabla business_unit
					tNow = PersistenceManager.getTimestampNow();
					//Actualizamos los datos de la tabla last_modification
					boolean changeRegister = PersistenceManager.updateTimeStampToDB(session.getConnection(), BusinessUnit.TABLE_NAME, tNow);
					//Si se produce un error de actualización de la tabla last_modification. La actualización de la tabla business_unit
					//no queda registrada
					if(!changeRegister) {
						infoLabel.setText("ERROR DE REGISTRO DE ACTUALIZACIÓN DE LA BASE DE DATOS");
					} else {
						infoLabel.setText("DATOS DE LA EMPRESA ACTUALIZADOS: " + session.formatTimestamp(tNow, null));
					}
					editButton.setEnabled(true);
					oKButton.setEnabled(false);
					cancelButton.setEnabled(false);
					for (JLabel label : labelList) {
						label.setVisible(false);
					}
					for (JTextField tField : textFieldList) {
						tField.setEditable(false);
						tField.setBackground(UIManager.getColor(new JPanel().getBackground()));
					}
					textFieldContentList.clear();
					for (int i = 0; i < textFieldList.size(); i++) {
						textFieldContentList.add(textFieldList.get(i).getText());
					}
				//Error de actualización de los datos en la base de datos
				} else {
					infoLabel.setText("ERROR DE ACTUALIZACIÓN DE DATOS EN LA BASE DE DATOS");
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
			if (!BusinessUnitUI.this.isShowing()) {
				BusinessUnitUI.this.panelVisible = false;
				this.cancel();
				BusinessUnitUI.this.timer.cancel();
				 System.out.println("Se ha cerrado la ventana Empresa");
			}
			if (cancelButton.isEnabled() && oKButton.isEnabled() && BusinessUnitUI.this.isShowing()) {
				//Do nothing
				//No se comprueba la actualización de los datos si los estamos editando
			} else if (BusinessUnitUI.this.panelVisible == true){
				//Se comprueba la actualización de los datos si no los estamos editando
				System.out.println("Comprobando actualización de datos de la compañía");
				
				//Debug
				System.out.println(session.getUpdatedTables().size());
				
				//Loop por el Map de CurrentSession, si aparece la tabla business_unit, recargar datos
				for (Map.Entry<String, Timestamp> updatedTable : session.getUpdatedTables().entrySet()) {
					
					//Debug
					System.out.println(updatedTable.getKey());
					System.out.println(updatedTable.getValue());
					
					if (updatedTable.getKey().equals(BusinessUnit.TABLE_NAME)) {
						//Asignamos el nuevo contenido a los textfields
						BusinessUnitUI.this.populateTextFields();
						//renovamos la lista de contenidos de los textfields
						textFieldContentList.clear();
						for (int i = 0; i < textFieldList.size(); i++) {
							textFieldContentList.add(textFieldList.get(i).getText());
						}
						BusinessUnitUI.this.infoLabel.setText("DATOS DE LA EMPRESA ACTUALIZADOS: " +
						//updatedTable.getValue().toString());
						session.formatTimestamp(updatedTable.getValue(), null));
					}
				}
			}
		}
		
	}

//
//	public BusinessUnitUI(LayoutManager layout) {
//		super(layout);
//		// TODO Auto-generated constructor stub
//	}
//
//	public BusinessUnitUI(boolean isDoubleBuffered) {
//		super(isDoubleBuffered);
//		// TODO Auto-generated constructor stub
//	}
//
//	public BusinessUnitUI(LayoutManager layout, boolean isDoubleBuffered) {
//		super(layout, isDoubleBuffered);
//		// TODO Auto-generated constructor stub
//	}

}
