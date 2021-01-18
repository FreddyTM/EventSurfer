package main.java.gui;

import javax.swing.JPanel;
import javax.swing.JPasswordField;

import main.java.company.User;
import main.java.persistence.CurrentSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Login extends JPanel {

	
	private Connection conn;
	private CurrentSession session;
	private AppWindow frame;
	private JPanel basePanel;
	private JTextField userField;
	private JPasswordField passwordField;
	
	public Login(Connection conn, CurrentSession session, AppWindow frame) {
		this.conn = conn;
		this.session = session;
		this.setFrame(frame);
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
		
		JLabel errorInfoLabel = new JLabel("");
		errorInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		errorInfoLabel.setBounds(50, 300, 700, 25);
		add(errorInfoLabel);
		
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
		
		acceptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String loginUser = userField.getText();
				String password = String.valueOf(passwordField.getPassword());
				if (password.equals("")) {
					errorInfoLabel.setText("USUARIO O CONTRASEÑA ERRÓNEOS");
				} else {
					String loginPassword = (new User().passwordHash(String.valueOf(passwordField.getPassword())));
					if (userLogin(conn, loginUser, loginPassword, errorInfoLabel)) {
						frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
					}
				}
			}
		});
		acceptButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String loginUser = userField.getText();
					String password = String.valueOf(passwordField.getPassword());
					if (password.equals("")) {
						errorInfoLabel.setText("USUARIO O CONTRASEÑA ERRÓNEOS");
					} else {
						String loginPassword = (new User().passwordHash(String.valueOf(passwordField.getPassword())));
						if (userLogin(conn, loginUser, loginPassword, errorInfoLabel)) {
							frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
						}
					}
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
			results = pstm.executeQuery();
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
			case 1: //Usuario administrador
				//Cargamos datos de la sesión
				session.loadAllData(conn, bUnitId, userId);
				frame.setUpWindow(userTypeId);
				break;
			case 2: //Usuario manager
				//Cargamos datos de la sesión
				session.loadCurrentSessionData(conn, bUnitId, userId);
				frame.setUpWindow(userTypeId);
				break;
			case 3: //Usuario user
				//Cargamos datos de la sesión
				session.loadCurrentSessionData(conn, bUnitId, userId);
				frame.setUpWindow(userTypeId);
				break;
			default: //Otros tipos de usuario no contemplados todavía
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

	public AppWindow getFrame() {
		return frame;
	}

	public void setFrame(AppWindow frame) {
		this.frame = frame;
	}

	public JPanel getBasePanel() {
		return basePanel;
	}

	public void setBasePanel(JPanel basePanel) {
		this.basePanel = basePanel;
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

	public void setPasswordField(JPasswordField passwordField) {
		this.passwordField = passwordField;
	}
}
