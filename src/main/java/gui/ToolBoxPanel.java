package main.java.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import main.java.persistence.CurrentSession;

public class ToolBoxPanel extends JPanel {
	
	private int userType;
	private int panelType;
	private CurrentSession session;
	private JToolBar toolBar;

	public ToolBoxPanel(int userType, int panelType, CurrentSession session, JToolBar toolBar) {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(1500, dim.height);
		setLayout(new BorderLayout());
		this.userType = userType;
		this.panelType = panelType;
		this.session = session;
		this.toolBar = toolBar;
//		JLabel lblNewLabel = new JLabel("New label");
//		add(lblNewLabel, BorderLayout.WEST);
	}
	
	public ToolBoxPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("New label");
		add(lblNewLabel, BorderLayout.WEST);
		// TODO Auto-generated constructor stub
	}

	public ToolBoxPanel(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}

	public ToolBoxPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public ToolBoxPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}
	
	public void initialize(JToolBar toolBar) {
		
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public int getPanelType() {
		return panelType;
	}

	public void setPanelType(int panelType) {
		this.panelType = panelType;
	}

	public CurrentSession getSession() {
		return session;
	}

	public void setSession(CurrentSession session) {
		this.session = session;
	}

	public JToolBar getToolBar() {
		return toolBar;
	}

	public void setToolBar(JToolBar toolBar) {
		this.toolBar = toolBar;
	}

}
