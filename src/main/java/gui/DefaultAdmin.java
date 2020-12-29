package main.java.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

public class DefaultAdmin extends JPanel {
	private JPasswordField currentPassField;
	private JPasswordField newPassField;
	private JPasswordField confirmNewPassField;
	private JTextField aliasField;
	private JTextField nameField;
	private JTextField lastNameField;

	/**
	 * Create the panel.
	 */
	public DefaultAdmin() {
		setLayout(null);
		
		JTextPane changePassTxt = new JTextPane();
		changePassTxt.setFont(new Font("Tahoma", Font.BOLD, 20));
		changePassTxt.setText("ES NECESARIO CAMBIAR EL PASSWORD DEL ADMINISTRADOR POR DEFECTO.\r\nMODIFICAR EL RESTO DE DATOS ES OPCIONAL");
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
		
		JLabel maxCharsLabel = new JLabel("Max: 25 caracteres");
		maxCharsLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel.setBounds(570, 250, 150, 25);
		add(maxCharsLabel);
		
		JLabel maxCharsLabel2 = new JLabel("Max: 20 caracteres");
		maxCharsLabel2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel2.setBounds(570, 350, 150, 25);
		add(maxCharsLabel2);
		
		JLabel maxCharsLabel3 = new JLabel("Max: 50 caracteres");
		maxCharsLabel3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel3.setBounds(570, 400, 150, 25);
		add(maxCharsLabel3);
		
		JLabel maxCharsLabel4 = new JLabel("Max: 20 caracteres");
		maxCharsLabel4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		maxCharsLabel4.setBounds(570, 450, 150, 25);
		add(maxCharsLabel4);

	}
}
