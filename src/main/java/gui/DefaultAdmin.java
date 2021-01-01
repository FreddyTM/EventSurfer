package main.java.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import main.java.company.User;
import main.java.persistence.CurrentSession;

import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.awt.event.ActionEvent;

public class DefaultAdmin extends JPanel {
	private JPasswordField currentPassField;
	private JPasswordField newPassField;
	private JPasswordField confirmNewPassField;
	private JTextField aliasField;
	private JTextField nameField;
	private JTextField lastNameField;
	private Connection conn;
	private User user;
	private CurrentSession session;

	/**
	 * Create the panel.
	 */
	public DefaultAdmin(Connection conn, User user, CurrentSession session) {
		this.conn = conn ;
		this.user = user;
		this.session = session;
		setLayout(null);
		
		JTextPane changePassTxt = new JTextPane();
		changePassTxt.setFont(new Font("Tahoma", Font.BOLD, 20));
		changePassTxt.setText("ES NECESARIO CAMBIAR EL PASSWORD DEL ADMINISTRADOR POR DEFECTO.\r\n"
				+ "MODIFICAR EL RESTO DE DATOS ES OPCIONAL");
		changePassTxt.setBackground(UIManager.getColor("Panel.background"));
		changePassTxt.setEditable(false);
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
		add(currentPassField);
		
		newPassField = new JPasswordField();
		newPassField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		newPassField.setBounds(260, 250, 300, 25);
		add(newPassField);
		
		confirmNewPassField = new JPasswordField();
		confirmNewPassField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		confirmNewPassField.setBounds(260, 300, 300, 25);
		add(confirmNewPassField);
		
		aliasField = new JTextField();
		aliasField.setBounds(260, 350, 300, 25);
		add(aliasField);
		aliasField.setColumns(10);
		
		nameField = new JTextField();
		nameField.setColumns(10);
		nameField.setBounds(260, 400, 300, 25);
		add(nameField);
		
		lastNameField = new JTextField();
		lastNameField.setColumns(10);
		lastNameField.setBounds(260, 450, 300, 25);
		add(lastNameField);
		
		JLabel maxCharsLabel = new JLabel("Max: 25 caracteres [a-z], [A-Z], [0-9], [*!$%&@#^]");
		maxCharsLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel.setBounds(570, 250, 380, 25);
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
		
		JButton updateButton = new JButton("Actualizar datos");
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (testData()) {
					//Actualizamos usuario con los datos del formulario
					//Excepto bUnit, userType y activo que no deben cambiar
					//User id será 1, el administrador por defecto
					user.setId(1);
					user.setUserAlias(aliasField.getText());
					user.setNombre(nameField.getText());
					user.setApellido(lastNameField.getText());
					user.setPassword(user.passwordHash(String.valueOf(newPassField.getPassword())));
					//Update user to database
					
					//load data to session
					//User id será 1, el administrador por defecto
					//BUnit id será 1, la unidad de negocio por defecto
					session.loadCurrentSessionData(conn, 1, 1);
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
		//comprobamos que la contraseña introducida es correcta
		String currentPassword = String.valueOf(currentPassField.getPassword());
		if (!user.getPassword().equals(currentPassword)) {
			//Throw exception
			
			return false;
		}
		
		
		//FALTA COMPROBAR TAMAÑO MÍNIMO CONTRASEÑA, Y LABEL PARA ANUNCIARLO
		
		//Comprobamos que la nueva contraseña no excede el tamaño máximo
		String newPassword = String.valueOf(newPassField.getPassword());
		if (newPassword.length() > 25) {
			//Throw exception
			
			return false;
		}
		//Comprobamos que la nueva contraseña y la confirmación son iguales
		String confirmPassword = String.valueOf(confirmNewPassField.getPassword());
		if (newPassword.length() != confirmPassword.length()) {
			//Throw exception
			
			return false;
		}
		//Comprobamos que la contraseña solo incluye caracteres permitidos
		String charList = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz"
				+ "0123456789"
				+ "*!$%&@#^";
		if(!newPassword.contains(charList)) {
			//Throw exception
			
			return false;
		}
		return true;
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
}
