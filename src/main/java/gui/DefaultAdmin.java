package main.java.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import main.java.company.User;
import main.java.session.CurrentSession;

import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.event.ActionListener;
import java.sql.Connection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DefaultAdmin extends JPanel {
	private JPasswordField currentPassField;
	private JPasswordField newPassField;
	private JPasswordField confirmNewPassField;
	private JTextField aliasField;
	private JTextField nameField;
	private JTextField lastNameField;
	private JLabel errorInfoLabel;
	private Connection conn;
	private User user;
	private CurrentSession session;
	private AppWindow frame;


	public DefaultAdmin(Connection conn, User user, CurrentSession session, AppWindow frame) {
		this.conn = conn ;
		this.user = user;
		this.session = session;
		this.frame = frame;
		setLayout(null);
		
		JTextPane changePassTxt = new JTextPane();
		changePassTxt.setFont(new Font("Tahoma", Font.BOLD, 20));
		changePassTxt.setText("ES NECESARIO CAMBIAR EL PASSWORD DEL ADMINISTRADOR POR DEFECTO.\r\n"
				+ "MODIFICAR EL RESTO DE DATOS ES OPCIONAL");
		changePassTxt.setBackground(UIManager.getColor(this.getBackground()));
		changePassTxt.setEditable(false);
		changePassTxt.setFocusable(false);
		changePassTxt.setBounds(50, 75, 900, 60);
		add(changePassTxt);
		
		JLabel currentPassLabel = new JLabel("Password actual");
		currentPassLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		currentPassLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		currentPassLabel.setBounds(50, 200, 200, 25);
		add(currentPassLabel);
		
		JLabel newPassLabel = new JLabel("Nuevo password");
		newPassLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		newPassLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		newPassLabel.setBounds(50, 250, 200, 25);
		add(newPassLabel);
		
		JLabel confirmPassLabel = new JLabel("Confirmar password");
		confirmPassLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		confirmPassLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		confirmPassLabel.setBounds(50, 300, 200, 25);
		add(confirmPassLabel);
		
		JLabel aliasLabel = new JLabel("Alias");
		aliasLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		aliasLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		aliasLabel.setBounds(50, 350, 200, 25);
		add(aliasLabel);
		
		JLabel nameLabel = new JLabel("Nombre");
		nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		nameLabel.setBounds(50, 400, 200, 25);
		add(nameLabel);
		
		JLabel lastNameLabel = new JLabel("Apellido");
		lastNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lastNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lastNameLabel.setBounds(50, 450, 200, 25);
		add(lastNameLabel);
		
		currentPassField = new JPasswordField();
		currentPassField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		currentPassField.setBounds(260, 200, 300, 25);
		currentPassField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					newPassField.requestFocusInWindow();
				}
			}
		});
		add(currentPassField);
		
		newPassField = new JPasswordField();
		newPassField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		newPassField.setBounds(260, 250, 300, 25);
		newPassField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					confirmNewPassField.requestFocusInWindow();
				}
			}
		});
		add(newPassField);
		
		confirmNewPassField = new JPasswordField();
		confirmNewPassField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		confirmNewPassField.setBounds(260, 300, 300, 25);
		confirmNewPassField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					aliasField.requestFocusInWindow();
				}
			}
		});
		add(confirmNewPassField);
		
		aliasField = new JTextField();
		aliasField.setBounds(260, 350, 300, 25);
		aliasField.setColumns(10);
		aliasField.setText(user.getUserAlias());
		aliasField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					nameField.requestFocusInWindow();
				}
			}
		});
		add(aliasField);
				
		nameField = new JTextField();
		nameField.setColumns(10);
		nameField.setBounds(260, 400, 300, 25);
		nameField.setText(user.getNombre());
		nameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					lastNameField.requestFocusInWindow();
				}
			}
		});
		add(nameField);
		
		//Declaramos aquí el botón para que pueda obtener el foco de lastNameField
		JButton updateButton = new JButton("Actualizar datos");;
		
		lastNameField = new JTextField();
		lastNameField.setColumns(10);
		lastNameField.setBounds(260, 450, 300, 25);
		lastNameField.setText(user.getApellido());
		lastNameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					updateButton.requestFocusInWindow();
				}
			}
		});
		add(lastNameField);
		
		JLabel minCharsLabel = new JLabel("Min: 8 caracteres");
		minCharsLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		minCharsLabel.setBounds(570, 240, 380, 25);
		add(minCharsLabel);
		
		JLabel maxCharsLabel = new JLabel("Max: 25 caracteres [a-z], [A-Z], [0-9], [*!$%&@#^]");
		maxCharsLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel.setBounds(570, 260, 380, 25);
		add(maxCharsLabel);
		
		JLabel maxCharsLabel2 = new JLabel("Max: 20 caracteres");
		maxCharsLabel2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel2.setBounds(570, 350, 150, 25);
		add(maxCharsLabel2);
		
		JLabel maxCharsLabel3 = new JLabel("Max: 50 caracteres");
		maxCharsLabel3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel3.setBounds(570, 400, 150, 25);
		add(maxCharsLabel3);
		
		JLabel maxCharsLabel4 = new JLabel("Max: 50 caracteres");
		maxCharsLabel4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel4.setBounds(570, 450, 150, 25);
		add(maxCharsLabel4);
		
		errorInfoLabel = new JLabel("");
		errorInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		errorInfoLabel.setBounds(50, 500, 900, 25);
		add(errorInfoLabel);
		
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (testData()) {
					if (updateData()) {
						startSession();
					} else {
						errorInfoLabel.setText("ERROR. DATOS NO ACTUALIZADOS");
					}
				}
			}
		});
		updateButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (testData()) {
						if (updateData()) {
							startSession();
						} else {
							errorInfoLabel.setText("ERROR. DATOS NO ACTUALIZADOS");
						}
					}
				}
			}
		});		
		updateButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		updateButton.setBounds(750, 550, 200, 25);
		add(updateButton);
	}
	
	/**
	 * Comprueba la corrección de los datos introducidos en el formulario
	 * @return true si son correctos, false si no lo son
	 */
	public boolean testData() {
		//Comprobamos que la contraseña actual introducida es correcta
		String currentPassword = String.valueOf(currentPassField.getPassword());
		if (!user.getPassword().equals(currentPassword)) {
			errorInfoLabel.setText("CONTRASEÑA ACTUAL INCORRECTA");
			return false;
		}
		
		//Comprobamos que la nueva contraseña no excede el tamaño máximo
		String newPassword = String.valueOf(newPassField.getPassword());
		if (newPassword.length() < 8 || newPassword.length() > 25) {
			errorInfoLabel.setText("LA LONGITUD DE LA NUEVA CONTRASEÑA ES INCORRECTA");
			return false;
		}
		//Comprobamos que la nueva contraseña y la confirmación son iguales
		String confirmPassword = String.valueOf(confirmNewPassField.getPassword());
		if (!newPassword.equals(confirmPassword)) {
			errorInfoLabel.setText("LA NUEVA CONTRASEÑA Y LA CONFIRMACIÓN NO COINCIDEN");
			return false;
		}
		
		//Comprobamos que la contraseña nueva no es igual a la antigua
		if (currentPassword.equals(newPassword) && currentPassword.equals(confirmPassword)) {
			errorInfoLabel.setText("LA NUEVA CONTRASEÑA NO PUEDE SER IGUAL A LA ANTIGUA");
			return false;
		}
		
		//Comprobamos que la contraseña solo incluye caracteres permitidos
		if(!user.isAValidPassword(newPassword)) {
			errorInfoLabel.setText("LA NUEVA CONTRASEÑA DEBE INCLUIR AL MENOS UNA MAYÚSCULA,"
					+ "UNA MINÚSCULA, UN DÍGITO Y UN CARACTER ESPECIAL");
			return false;
		}
	
		return true;
	}
	
	/**
	 * Actualizamos usuario con los datos del formulario
	 */
	public boolean updateData() {
		//Excepto bUnit, userType y activo que no deben cambiar
		//User id será 1, el administrador por defecto
		user.setId(1);
		user.setUserAlias(aliasField.getText());
		user.setNombre(nameField.getText());
		user.setApellido(lastNameField.getText());
		user.setPassword(user.passwordHash(String.valueOf(newPassField.getPassword())));
		//Actualizamos datos de usuario en la base de datos
		//Si se actualizan correctamente, iniciamos sesión
		if (user.updateDefaultAdminUserToDB(conn, user)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Inicia sesión y arma la ventana principal de la aplicación
	 */
	public void startSession() {
		//Cargamos datos de la sesión
		//User id será 1, el administrador por defecto
		//BUnit id será 1, la unidad de negocio por defecto
		session.loadCurrentSessionData(conn, 1, 1);
		//Pasar userTypeId por parámetro al panel (admin = 1)
		//Cargar paneles
//		String title = frame.getTitle() + "        Versión " + frame.getVersionNumber() + "  Usuario: " + user.getUserAlias();
//		frame.setTitle(title);
		frame.setFullTitle(user.getUserAlias());
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setUpWindow();
	}

	public JPasswordField getCurrentPassField() {
		return currentPassField;
	}

	public void setCurrentPassField(JPasswordField currentPassField) {
		this.currentPassField = currentPassField;
	}

	public JPasswordField getNewPassField() {
		return newPassField;
	}

	public void setNewPassField(JPasswordField newPassField) {
		this.newPassField = newPassField;
	}

	public JPasswordField getConfirmNewPassField() {
		return confirmNewPassField;
	}

	public void setConfirmNewPassField(JPasswordField confirmNewPassField) {
		this.confirmNewPassField = confirmNewPassField;
	}

	public JTextField getAliasField() {
		return aliasField;
	}

	public void setAliasField(JTextField aliasField) {
		this.aliasField = aliasField;
	}

	public JTextField getNameField() {
		return nameField;
	}

	public void setNameField(JTextField nameField) {
		this.nameField = nameField;
	}

	public JTextField getLastNameField() {
		return lastNameField;
	}

	public void setLastNameField(JTextField lastNameField) {
		this.lastNameField = lastNameField;
	}

	public JLabel getErrorInfoLabel() {
		return errorInfoLabel;
	}

	public void setErrorInfoLabel(JLabel errorInfoLabel) {
		this.errorInfoLabel = errorInfoLabel;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public CurrentSession getSession() {
		return session;
	}

	public void setSession(CurrentSession session) {
		this.session = session;
	}

	public AppWindow getFrame() {
		return frame;
	}

	public void setFrame(AppWindow frame) {
		this.frame = frame;
	}
}
