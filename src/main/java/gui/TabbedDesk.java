package main.java.gui;

import java.awt.CardLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class TabbedDesk extends JTabbedPane {

	JPanel eventPanel;
	JPanel eventTypePane;
	JPanel areaPane;
	JPanel userPane;
	JPanel bUnitPane;
	JPanel companyPane;
	
	public TabbedDesk() {
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		eventPanel = new JPanel();
		eventPanel.setLayout(new FlowLayout());
		eventPanel.add(new JLabel("VENTANA DE GESTIÓN DE INCIDENCIAS"));
		addTab("INCIDENCIAS", null, eventPanel, null);
		
//		JPanel panel = new JPanel();
//		panel.setLayout(new FlowLayout());
//		panel.add(new JLabel("VENTANA DE GESTIÓN DE INCIDENCIAS"));
//		eventPane.add(panel);
		
//		eventTypePane = new JTabbedPane(JTabbedPane.TOP);
//		addTab("TIPOS DE INCIDENCIA", null, eventTypePane, null);
//		eventTypePane.setLayout(new FlowLayout());
//		eventTypePane.add(new JLabel("VENTANA DE GESTIÓN DE TIPOS DE INCIDENCIA"));
//		
//		areaPane = new JTabbedPane(JTabbedPane.TOP);
//		addTab("AREAS", null, areaPane, null);
//		areaPane.setLayout(new FlowLayout());
//		areaPane.add(new JLabel("VENTANA DE GESTIÓN DE AREAS"));
//		
//		userPane = new JTabbedPane(JTabbedPane.TOP);
//		addTab("USUARIOS", null, userPane, null);
//		userPane.setLayout(new FlowLayout());
//		userPane.add(new JLabel("VENTANA DE GESTIÓN DE USUARIOS"));
//		
//		bUnitPane = new JTabbedPane(JTabbedPane.TOP);
//		addTab("UNIDADES DE NEGOCIO", null, bUnitPane, null);
//		bUnitPane.setLayout(new FlowLayout());
//		bUnitPane.add(new JLabel("VENTANA DE GESTIÓN DE UNIDADES DE NEGOCIO"));
//		
//		companyPane = new JTabbedPane(JTabbedPane.TOP);
//		addTab("EMPRESA", null, companyPane, null);
//		companyPane.setLayout(new FlowLayout());
//		companyPane.add(new JLabel("VENTANA DE INFORMACIÓN DE LA EMPRESA"));

	}

	public TabbedDesk(int tabPlacement) {
		super(tabPlacement);
		// TODO Auto-generated constructor stub
	}

	public TabbedDesk(int tabPlacement, int tabLayoutPolicy) {
		super(tabPlacement, tabLayoutPolicy);
		// TODO Auto-generated constructor stub
	}

//	public JTabbedPane getEventPane() {
//		return eventPane;
//	}
//
//	public void setEventPane(JTabbedPane eventPane) {
//		this.eventPane = eventPane;
//	}
//
//	public JTabbedPane getEventTypePane() {
//		return eventTypePane;
//	}
//
//	public void setEventTypePane(JTabbedPane eventTypePane) {
//		this.eventTypePane = eventTypePane;
//	}
//
//	public JTabbedPane getAreaPane() {
//		return areaPane;
//	}
//
//	public void setAreaPane(JTabbedPane areaPane) {
//		this.areaPane = areaPane;
//	}
//
//	public JTabbedPane getUserPane() {
//		return userPane;
//	}
//
//	public void setUserPane(JTabbedPane userPane) {
//		this.userPane = userPane;
//	}
//
//	public JTabbedPane getbUnitPane() {
//		return bUnitPane;
//	}
//
//	public void setbUnitPane(JTabbedPane bUnitPane) {
//		this.bUnitPane = bUnitPane;
//	}
//
//	public JTabbedPane getCompanyPane() {
//		return companyPane;
//	}
//
//	public void setCompanyPane(JTabbedPane companyPane) {
//		this.companyPane = companyPane;
//	}
	
//	public void test() {
//		areaPane.setLayout(new CardLayout());
//		areaPane.add(new JButton());
//	}

}
