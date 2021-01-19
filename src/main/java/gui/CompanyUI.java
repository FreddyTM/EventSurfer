package main.java.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import main.java.company.Company;
import main.java.persistence.CurrentSession;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;

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
	private JButton editButton;
	private JButton cancelButton;
	private JButton oKButton;
	JLabel errorInfoLabel;
	private List<JLabel> labelList = new ArrayList<JLabel>();
	private List<JTextField> textFieldList = new ArrayList<JTextField>();
	private List<String> textFieldContentList = new ArrayList<String>();
	private final Action editAction = new EditAction();
	private final Action cancelAction = new CancelAction();
	
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
					postalcodeField.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(stateField);
		textFieldContentList.add(session.getCompany().getEstado());
		add(stateField);
		
		postalcodeField = new JTextField();
		postalcodeField.setColumns(10);
		postalcodeField.setBounds(260, 325, 100, 25);
		postalcodeField.setText(session.getCompany().getCpostal());
		postalcodeField.setEditable(false);
		postalcodeField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					telephoneField.requestFocusInWindow();
				}
			}
		});
		textFieldList.add(postalcodeField);
		textFieldContentList.add(session.getCompany().getCpostal());
		add(postalcodeField);
		
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
		
		errorInfoLabel = new JLabel("");
		errorInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		errorInfoLabel.setBounds(50, 530, 770, 25);
		add(errorInfoLabel);
		
		oKButton = new JButton("Aceptar");
		oKButton.setBounds(731, 580, 89, 23);
		oKButton.setEnabled(false);
		add(oKButton);
		
		cancelButton = new JButton("Cancelar");
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
	}

	/**
	 * Comprueba la corrección de los datos introducidos en el formulario
	 * @return true si son correctos, false si no lo son
	 */
	public boolean testData(Company company) {
		//Comprobamos que los datos no exceden el tamaño máximo
		Boolean error = false;
		String errorText = "LA LONGITUD DE LOS DATOS EXCEDE EL TAMAÑO MÁXIMO";
		if (company.getNombre().length() > 100) {
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
			postalcodeField.setBackground(Color.YELLOW);
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
			errorInfoLabel.setText(errorText);
			return false;
		}
		return true;
	}
	
	/**
	 * Acción del botón Editar. Habilita la edición de la información del formulario, el botón
	 * de Cancelar para que los cambios no se registren y el de Aceptar para que sí lo hagan
	 */
	private class EditAction extends AbstractAction {
		public EditAction() {
			putValue(NAME, "Editar");
			putValue(SHORT_DESCRIPTION, "Enable edit data");
		}
		public void actionPerformed(ActionEvent e) {
			editButton.setEnabled(false);
			oKButton.setEnabled(true);
			cancelButton.setEnabled(true);
			for (JLabel label : labelList) {
				label.setVisible(true);
			}
			for (JTextField tField : textFieldList) {
				tField.setEditable(true);
			}
		}
	}
	private class CancelAction extends AbstractAction {
		public CancelAction() {
			putValue(NAME, "Cancelar");
			putValue(SHORT_DESCRIPTION, "Cancel edit data");
		}
		public void actionPerformed(ActionEvent e) {
			editButton.setEnabled(true);
			oKButton.setEnabled(false);
			cancelButton.setEnabled(false);
			for (JLabel label : labelList) {
				label.setVisible(false);
			}
			for (JTextField tField : textFieldList) {
				tField.setEditable(false);
			}
			for (int i = 0; i < textFieldList.size(); i++) {
				textFieldList.get(i).setText(textFieldContentList.get(i));
			}
			
		}
	}
}
