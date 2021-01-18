package main.java.gui;

import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import main.java.persistence.CurrentSession;

public class CompanyUI extends JPanel {

	private CurrentSession session;
	private JTextField nameField;
	private JTextField addressField;
	private JTextField provinceField;
	private JTextField stateField;
	private JTextField postalcodeField;
	private JTextField telephoneField;
	private JTextField mailField;
	private JTextField webField;
	
	/**
	 * @wbp.parser.constructor
	 */
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
		nameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					addressField.requestFocusInWindow();
				}
			}
		});
		add(nameField);
		
		addressField = new JTextField();
		addressField.setColumns(10);
		addressField.setBounds(260, 175, 400, 25);
		addressField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					provinceField.requestFocusInWindow();
				}
			}
		});
		add(addressField);
		
		provinceField = new JTextField();
		provinceField.setColumns(10);
		provinceField.setBounds(260, 175, 400, 25);
		provinceField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					//lastNameField.requestFocusInWindow();
				}
			}
		});
		add(provinceField);
		
	}

	public CompanyUI(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}

	public CompanyUI(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public CompanyUI(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

}
