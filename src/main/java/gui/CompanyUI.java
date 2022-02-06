package main.java.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import main.java.company.Company;
import main.java.persistence.PersistenceManager;
import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;

/**
 * Muestra la pantalla de edición de datos de la empresa
 * @author Alfred Tomey
 */
public class CompanyUI extends JPanel {

	private CurrentSession session;
	private Timestamp tNow = ToolBox.getTimestampNow();
	//Temporizador de comprobación de cambios en los datos de la sesión
	private Timer timer;

	private JTextField nameField;
	private JTextField addressField;
	private JTextField provinceField;
	private JTextField stateField;
	private JTextField postalCodeField;
	private JTextField telephoneField;
	private JTextField mailField;
	private JTextField webField;
	private JButton editButton;
	private JButton cancelButton;
	private JButton oKButton;
	private JLabel infoLabel;
	
	//Lista de etiquetas informativas de longitud máxima de datos
	private List<JLabel> labelList = new ArrayList<JLabel>();
	//Lista de campos de datos asociados a las etiquetas informativas
	private List<JTextField> textFieldList = new ArrayList<JTextField>();
	//Lista de contenidos de los campos de datos. Sirve de caché para recuperarlos
	//Tras cancelar una edición de datos o la creación de un nuevo centro de trabajo
	private List<String> textFieldContentList = new ArrayList<String>();
	private final Action editAction = new EditAction();
	private final Action cancelAction = new CancelAction();
	private final Action oKAction = new OKAction();
	
	//Pone en pausa la actualización de datos realizada por TimerJob si es la propia instancia
	//del programa la que ha grabado datos nuevos en la base de datos
	private volatile boolean selfUpdate = false;

	public CompanyUI(CurrentSession session) {
		this.session = session;
		setLayout(null);
		
		JTextPane companyTxt = new JTextPane();
		companyTxt.setFont(new Font("Tahoma", Font.BOLD, 20));
		companyTxt.setText("DATOS DE LA EMPRESA");
		companyTxt.setBackground(UIManager.getColor(this.getBackground()));
		companyTxt.setEditable(false);
		companyTxt.setFocusable(false);
		companyTxt.setBounds(50, 50, 300, 30);
		add(companyTxt);
		
		JLabel nameLabel = new JLabel("Nombre");
		nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		nameLabel.setBounds(50, 125, 200, 25);
		add(nameLabel);
		
		JLabel addressLabel = new JLabel("Dirección");
		addressLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		addressLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		addressLabel.setBounds(50, 175, 200, 25);
		add(addressLabel);
		
		JLabel provinceLabel = new JLabel("Provincia");
		provinceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		provinceLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		provinceLabel.setBounds(50, 225, 200, 25);
		add(provinceLabel);
		
		JLabel stateLabel = new JLabel("Estado");
		stateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		stateLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		stateLabel.setBounds(50, 275, 200, 25);
		add(stateLabel);
		
		JLabel postalCodeLabel = new JLabel("Código postal");
		postalCodeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		postalCodeLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		postalCodeLabel.setBounds(50, 325, 200, 25);
		add(postalCodeLabel);
		
		JLabel telephoneLabel = new JLabel("Teléfono");
		telephoneLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		telephoneLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		telephoneLabel.setBounds(50, 375, 200, 25);
		add(telephoneLabel);
		
		JLabel mailLabel = new JLabel("E-mail");
		mailLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		mailLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		mailLabel.setBounds(50, 425, 200, 25);
		add(mailLabel);
		
		JLabel webLabel = new JLabel("Web");
		webLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		webLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		webLabel.setBounds(50, 475, 200, 25);
		add(webLabel);
		
		nameField = new JTextField();
		nameField.setColumns(10);
		nameField.setBounds(260, 125, 400, 25);
		nameField.setText(session.getCompany().getNombre());
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
		textFieldContentList.add(session.getCompany().getNombre());
		add(nameField);
		
		addressField = new JTextField();
		addressField.setColumns(10);
		addressField.setBounds(260, 175, 400, 25);
		addressField.setText(session.getCompany().getDireccion());
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
		textFieldContentList.add(session.getCompany().getDireccion());
		add(addressField);
		
		provinceField = new JTextField();
		provinceField.setColumns(10);
		provinceField.setBounds(260, 225, 200, 25);
		provinceField.setText(session.getCompany().getProvincia());
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
		textFieldContentList.add(session.getCompany().getProvincia());
		add(provinceField);
		
		stateField = new JTextField();
		stateField.setColumns(10);
		stateField.setBounds(260, 275, 200, 25);
		stateField.setText(session.getCompany().getEstado());
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
		textFieldContentList.add(session.getCompany().getEstado());
		add(stateField);
		
		postalCodeField = new JTextField();
		postalCodeField.setColumns(10);
		postalCodeField.setBounds(260, 325, 100, 25);
		postalCodeField.setText(session.getCompany().getCpostal());
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
		textFieldContentList.add(session.getCompany().getCpostal());
		add(postalCodeField);
		
		telephoneField = new JTextField();
		telephoneField.setColumns(10);
		telephoneField.setBounds(260, 375, 100, 25);
		telephoneField.setText(session.getCompany().getTelefono());
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
		textFieldContentList.add(session.getCompany().getTelefono());
		add(telephoneField);
		
		mailField = new JTextField();
		mailField.setColumns(10);
		mailField.setBounds(260, 425, 400, 25);
		mailField.setText(session.getCompany().getMail());
		mailField.setEditable(false);
		mailField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					webField.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(mailField);
		textFieldContentList.add(session.getCompany().getMail());
		add(mailField);
		
		webField = new JTextField();
		webField.setColumns(10);
		webField.setBounds(260, 475, 400, 25);
		webField.setText(session.getCompany().getWeb());
		webField.setEditable(false);
		webField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					oKButton.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(webField);
		textFieldContentList.add(session.getCompany().getWeb());
		add(webField);
		
		JLabel maxCharsLabel = new JLabel("Max: 100 caracteres");
		maxCharsLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel.setBounds(670, 125, 150, 25);
		maxCharsLabel.setVisible(false);
		labelList.add(maxCharsLabel);
		add(maxCharsLabel);
		
		JLabel maxCharsLabel2 = new JLabel("Max: 150 caracteres");
		maxCharsLabel2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel2.setBounds(670, 175, 150, 25);
		maxCharsLabel2.setVisible(false);
		labelList.add(maxCharsLabel2);
		add(maxCharsLabel2);
		
		JLabel maxCharsLabel3 = new JLabel("Max: 50 caracteres");
		maxCharsLabel3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel3.setBounds(470, 227, 150, 25);
		maxCharsLabel3.setVisible(false);
		labelList.add(maxCharsLabel3);
		add(maxCharsLabel3);
		
		JLabel maxCharsLabel4 = new JLabel("Max: 50 caracteres");
		maxCharsLabel4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel4.setBounds(470, 277, 150, 25);
		maxCharsLabel4.setVisible(false);
		labelList.add(maxCharsLabel4);
		add(maxCharsLabel4);
		
		JLabel maxCharsLabel5 = new JLabel("Max: 8 caracteres");
		maxCharsLabel5.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel5.setBounds(370, 327, 150, 25);
		maxCharsLabel5.setVisible(false);
		labelList.add(maxCharsLabel5);
		add(maxCharsLabel5);
		
		JLabel maxCharsLabel6 = new JLabel("Max: 15 caracteres");
		maxCharsLabel6.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel6.setBounds(370, 377, 150, 25);
		maxCharsLabel6.setVisible(false);
		labelList.add(maxCharsLabel6);
		add(maxCharsLabel6);
		
		JLabel maxCharsLabel7 = new JLabel("Max: 100 caracteres");
		maxCharsLabel7.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel7.setBounds(670, 425, 150, 25);
		maxCharsLabel7.setVisible(false);
		labelList.add(maxCharsLabel7);
		add(maxCharsLabel7);
		
		JLabel maxCharsLabel8 = new JLabel("Max: 200 caracteres");
		maxCharsLabel8.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel8.setBounds(670, 475, 150, 25);
		maxCharsLabel8.setVisible(false);
		labelList.add(maxCharsLabel8);
		add(maxCharsLabel8);
		
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
		
		timer = new Timer();
		TimerTask task = new TimerJob();
		timer.scheduleAtFixedRate(task, 5000, session.getPeriod());
	}
	
	/**
	 * Refresca los datos de la compañía de la sesión para que se visualicen en pantalla
	 */
	private void populateTextFields() {
		nameField.setText(session.getCompany().getNombre());
		addressField.setText(session.getCompany().getDireccion());
		provinceField.setText(session.getCompany().getProvincia());
		stateField.setText(session.getCompany().getEstado());
		postalCodeField.setText(session.getCompany().getCpostal());
		telephoneField.setText(session.getCompany().getTelefono());
		mailField.setText(session.getCompany().getMail());
		webField.setText(session.getCompany().getWeb());
	}

	/**
	 * Comprueba la corrección de los datos introducidos en el formulario. Cualquier
	 * dato incorrecto se resalta con el fondo del campo en amarillo
	 * @return true si son correctos, false si no lo son
	 */
	private boolean testData(Company company) {
		//Comprobamos que los datos no exceden el tamaño máximo
		Boolean error = false;
		String errorText = "LA LONGITUD DE LOS DATOS EXCEDE EL TAMAÑO MÁXIMO O FALTAN DATOS";
		if (company.getNombre().length() > 100 || company.getNombre().length() == 0) {
			nameField.setBackground(Color.YELLOW);
			error = true;
		}
		if (company.getDireccion().length() > 150) {
			addressField.setBackground(Color.YELLOW);
			error = true;
		}
		if (company.getProvincia().length() > 50) {
			provinceField.setBackground(Color.YELLOW);
			error = true;
		}
		if (company.getEstado().length() > 50) {
			stateField.setBackground(Color.YELLOW);
			error = true;
		}
		if (company.getCpostal().length() > 8) {
			postalCodeField.setBackground(Color.YELLOW);
			error = true;
		}
		if (company.getTelefono().length() > 15) {
			telephoneField.setBackground(Color.YELLOW);
			error = true;
		}
		if (company.getMail().length() > 100) {
			mailField.setBackground(Color.YELLOW);
			error = true;
		}
		if (company.getWeb().length() > 200) {
			webField.setBackground(Color.YELLOW);
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
			nameField.requestFocusInWindow();
		}
	}
	
	/**
	 * Acción del botón Cancelar. Se deshabilita el propio botón y el botón Aceptar. Se habilita el botón Editar.
	 * Descarta los cambios en los datos introducidos en el formulario. No se graban en la base de datos ni en el
	 * objeto company. Se recupera la información que figuraba anteriormente en el formulario. Se borra cualquier
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
	 * Si se consigue, se actualiza el objeto company con dichos datos. Si no se consigue, no se actualiza
	 * el objeto company con los datos y se muestra un mensaje de error. Se intenta guardar el registro
	 * de la actualización de datos en la base de datos si dicha actualización tuvo éxito. Si no se consigue
	 * se muestra un mensaje de error
	 */
	private class OKAction extends AbstractAction {
		public OKAction() {
			putValue(NAME, "Aceptar");
			putValue(SHORT_DESCRIPTION, "Execute data edit");
		}
		public synchronized void actionPerformed(ActionEvent e) {
			//Se recupera el fondo blanco de los campos para que una anterior validación errónea de los mismos
			//no los deje amarillos permanentemente
			for (JTextField tField : textFieldList) {
				tField.setBackground(Color.WHITE);
			}
			
			try {
				selfUpdate = true;
				
				System.out.println("Grabación de datos propios iniciada, actualizaciones suspendidas................");
				
				//Objeto que recoge los datos actualizados
				Company updatedCompany = new Company();
				updatedCompany.setId(1);
				updatedCompany.setNombre(nameField.getText());
				updatedCompany.setDireccion(addressField.getText());
				updatedCompany.setProvincia(provinceField.getText());
				updatedCompany.setEstado(stateField.getText());
				updatedCompany.setCpostal(postalCodeField.getText());
				updatedCompany.setTelefono(telephoneField.getText());
				updatedCompany.setMail(mailField.getText());
				updatedCompany.setWeb(webField.getText());
				//Si los datos están validados
				if (testData(updatedCompany)) {
					//Si los datos actualizados se graban en la base de datos, se actualizan los datos de la sesión
					if (new Company().updateCompanyToDB(session.getConnection(), updatedCompany)) {
						session.getCompany().setNombre(updatedCompany.getNombre());
						session.getCompany().setDireccion(updatedCompany.getDireccion());
						session.getCompany().setProvincia(updatedCompany.getProvincia());
						session.getCompany().setEstado(updatedCompany.getEstado());
						session.getCompany().setCpostal(updatedCompany.getCpostal());
						session.getCompany().setTelefono(updatedCompany.getTelefono());
						session.getCompany().setMail(updatedCompany.getMail());
						session.getCompany().setWeb(updatedCompany.getWeb());
						//Registramos fecha y hora de la actualización de los datos de la tabla company
						tNow = ToolBox.getTimestampNow();
						//Actualizamos los datos de la tabla last_modification
						boolean changeRegister = PersistenceManager.updateTimeStampToDB(session.getConnection(), Company.TABLE_NAME, tNow);
						//Si se produce un error de actualización de la tabla last_modification. La actualización de la tabla company
						//no queda registrada
						if(!changeRegister) {
							infoLabel.setText("ERROR DE REGISTRO DE ACTUALIZACIÓN DE LA BASE DE DATOS");
						} else {
							infoLabel.setText("DATOS DE LA EMPRESA ACTUALIZADOS: " + ToolBox.formatTimestamp(tNow, null));
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
			if (!CompanyUI.this.isShowing()) {
				this.cancel();
				CompanyUI.this.timer.cancel();
				 System.out.println("Se ha cerrado la ventana Empresa");
			}
			
			if (session.isLocked()) {
				try {
					System.out.println("EventDataUI esperando permiso para refrescar datos......");
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (selfUpdate) {
				try {
					System.out.println("EventDataUI esperando permiso para refrescar datos......");
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//No se comprueba la actualización de los datos si los estamos editando o añadiendo
			if (cancelButton.isEnabled() && oKButton.isEnabled() && CompanyUI.this.isShowing()) {
				//Do nothing
			//Se comprueba la actualización de los datos si no los estamos modificando
			} else if (CompanyUI.this.isShowing()){
				//Loop por el Map de CurrentSession, si aparece la tabla company, recargar datos
				for (Map.Entry<String, Timestamp> updatedTable : session.getUpdatedTables().entrySet()) {
					//Si en la tabla de actualizaciones aparece la clave Company.TABLE_NAME
					if (updatedTable.getKey().equals(Company.TABLE_NAME)) {
						//Asignamos el nuevo contenido a los textfields
						CompanyUI.this.populateTextFields();
						//renovamos la lista de contenidos de los textfields
						textFieldContentList.clear();
						for (int i = 0; i < textFieldList.size(); i++) {
							textFieldContentList.add(textFieldList.get(i).getText());
						}
						CompanyUI.this.infoLabel.setText("DATOS DE LA EMPRESA ACTUALIZADOS: " +
						ToolBox.formatTimestamp(updatedTable.getValue(), null));
					}
				}
			}
		}
		
	}
}
