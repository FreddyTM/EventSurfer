package main.java.gui;

import javax.swing.JPanel;
import javax.swing.JPasswordField;

import main.java.persistence.CurrentSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

public class Login extends JPanel {

	
	private Connection conn;
	private CurrentSession session;
	private JTextField userField;
	private JPasswordField passwordField;
	
	public Login(Connection conn, CurrentSession session) {
		this.conn = conn;
		this.session = session;		
		setLayout(null);
		
		JTextPane userLoginTxt = new JTextPane();
		userLoginTxt.setText("INTRODUZCA USUARIO Y CONTRASEÑA");
		userLoginTxt.setFont(new Font("Tahoma", Font.BOLD, 20));
		userLoginTxt.setEditable(false);
		userLoginTxt.setFocusable(false);
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
		
		JLabel errorLabel = new JLabel("");
		errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		errorLabel.setBounds(50, 300, 700, 25);
		add(errorLabel);
		
		userField = new JTextField();
		userField.setBounds(260, 200, 300, 25);
		userField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		userField.setColumns(10);
		userField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					passwordField.requestFocusInWindow();
				}
			}
		});
		add(userField);
		
		//Declaramos aquí el botón para que pueda obtener el foco de passwordField
		JButton acceptButton = new JButton("Aceptar");
		
		passwordField = new JPasswordField();
		passwordField.setColumns(10);
		passwordField.setBounds(260, 250, 300, 25);
		passwordField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					acceptButton.requestFocusInWindow();
				}
			}
		});
		add(passwordField);
		
		String loginUser = userField.getText();
		String loginPassword = String.valueOf(passwordField.getPassword());
		
		//acceptButton = new JButton("Aceptar");
		acceptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userLogin(conn, loginUser, loginPassword, errorLabel);
			}
		});
		acceptButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					userLogin(conn, loginUser, loginPassword, errorLabel);
				}
			}
		});
		acceptButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		acceptButton.setBounds(460, 350, 100, 25);
		add(acceptButton);

	}
	
	public boolean userLogin(Connection conn, String alias, String password, JLabel label) {
		int userId = 0;
		int bUnitId = 0;
		int userTypeId = 0;
		boolean activo = false;
		PreparedStatement pstm = null;
		ResultSet results = null;
		String sql = "SELECT id, b_unit_id, user_type_id, activo "
				+ "FROM \"user\" "
				+ "WHERE user_alias = ? "
				+ "AND user_password = ?;";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, alias);
			pstm.setString(2, password);
			while(results.next()) {
				userId = results.getInt(1);
				bUnitId = results.getInt(2);
				userTypeId = results.getInt(3);
				activo = results.getBoolean(4);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//Usuario no existe o contraseña incorrecta
		if (userId == 0) {
			//Mensaje de error en el login panel
			label.setText("USUARIO O CONTRASEÑA ERRÓNEOS");
			return false;
		}
		//Usuario inactivo
		if (activo == false) {
			//Mensaje de error en el login panel
			label.setText("EL USUARIO NO ESTÁ ACTIVO. "
					+ "CONTACTE CON EL ADMINISTRADOR");
			return false;
		}
		switch (userTypeId) {
			//Usuario administrador
			case 1:
				//Pasar userTypeId por parámetro al panel
				
				break;
			//Usuario manager
			case 2:
				//Pasar userTypeId por parámetro al panel
				
				break;
			//Usuario user
			case 3:
				//Pasar userTypeId por parámetro al panel
				
				
				break;
			//Otros usuarios no contemplados todavía
			default:
				//Do nothing
		}
		
		
		return true;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public CurrentSession getSession() {
		return session;
	}

	public void setSession(CurrentSession session) {
		this.session = session;
	}

	public JTextField getUserField() {
		return userField;
	}

	public void setUserField(JTextField userField) {
		this.userField = userField;
	}

	public JTextField getPasswordField() {
		return passwordField;
	}

	public void setPasswordField(JTextField passwordField) {
		this.passwordField = passwordField;
	}
}
