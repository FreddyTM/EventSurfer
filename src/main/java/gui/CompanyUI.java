package main.java.gui;

import java.awt.Font;
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import main.java.persistence.CurrentSession;

public class CompanyUI extends JPanel {

	private CurrentSession session;
	
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
		companyTxt.setBounds(50, 75, 300, 30);
		add(companyTxt);
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
