package main.java.gui;

import javax.swing.JPanel;

import main.java.persistence.CurrentSession;

import java.sql.Connection;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import java.awt.SystemColor;
import javax.swing.JButton;

public class Login extends JPanel {

	
	private Connection conn;
	private CurrentSession session;
	private JTextField userField;
	private JTextField passwordField;
	
	public Login(Connection conn, CurrentSession session) {
		this.conn = conn;
		this.session = session;		
		setLayout(null);
		
		JTextPane userLoginTxt = new JTextPane();
		userLoginTxt.setText("INTRODUZCA USUARIO Y CONTRASEÑA");
		userLoginTxt.setFont(new Font("Tahoma", Font.BOLD, 20));
		userLoginTxt.setEditable(false);
		userLoginTxt.setBackground(SystemColor.menu);
		userLoginTxt.setBounds(100, 110, 650, 30);
		add(userLoginTxt);
		
		JLabel userLabel = new JLabel("Usuario:");
		userLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		userLabel.setSize(150, 25);
		userLabel.setLocation(100, 200);
		add(userLabel);
		
		JLabel passLabel = new JLabel("Password:");
		passLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		passLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		passLabel.setBounds(100, 250, 150, 25);
		add(passLabel);
		
		JLabel errorInfoLabel = new JLabel("");
		errorInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		errorInfoLabel.setBounds(50, 300, 700, 25);
		add(errorInfoLabel);
		
		userField = new JTextField();
		userField.setBounds(260, 200, 300, 25);
		add(userField);
		userField.setColumns(10);
		
		passwordField = new JTextField();
		passwordField.setColumns(10);
		passwordField.setBounds(260, 250, 300, 25);
		add(passwordField);
		
		JButton acceptButton = new JButton("Aceptar");
		acceptButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		acceptButton.setBounds(460, 350, 100, 25);
		add(acceptButton);
		

		


	}
}
