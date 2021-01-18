package main.java.gui;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import main.java.persistence.CurrentSession;

public class CompanyUI extends JPanel {

	private CurrentSession session;
	
	public CompanyUI(CurrentSession session) {
		this.session = session;
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
