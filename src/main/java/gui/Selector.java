package main.java.gui;

import java.awt.LayoutManager;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;

public class Selector extends JPanel {
	
	AppWindow frame;
	
	private final Action logOutAction = new LogOutAction();
	JButton eventButton;
	JButton eventTypeButton;
	JButton areaButton;
	JButton userButton;
	JButton bUnitButton;
	JButton companyButton;
	JButton logOutButton;

	public Selector(AppWindow frame) {
		this.frame = frame;
		
		setLayout(new GridLayout(8, 0, 10, 10));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		Dimension dim = new Dimension(100,80);
		
		eventButton = new JButton();
		eventButton.setMargin(new Insets(2,2,2,2));
		eventButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		eventButton.setText("EVENTOS");
		eventButton.setMinimumSize(dim);
		eventButton.setPreferredSize(dim);
		add(eventButton);
		
		eventTypeButton = new JButton();
		eventTypeButton.setMargin(new Insets(2,2,2,2));
		eventTypeButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		eventTypeButton.setText("<html><center>"+"TIPOS" + "<br>" + "DE" + "<br>" + "EVENTO" + "</center></html>");
		eventTypeButton.setMinimumSize(dim);
		eventTypeButton.setPreferredSize(dim);
		add(eventTypeButton);
		
		areaButton = new JButton();
		areaButton.setMargin(new Insets(2,2,2,2));
		areaButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		areaButton.setText("AREAS");
		areaButton.setPreferredSize(dim);
		add(areaButton);
		
		userButton = new JButton();
		userButton.setMargin(new Insets(2,2,2,2));
		userButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		userButton.setText("USUARIOS");
		userButton.setPreferredSize(dim);
		add(userButton);
		
		bUnitButton = new JButton();
		bUnitButton.setMargin(new Insets(2,2,2,2));
		bUnitButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		bUnitButton.setText("<html><center>"+"UNIDADES" + "<br>" + "DE" + "<br>" + "NEGOCIO" + "</center></html>");
		bUnitButton.setPreferredSize(dim);
		add(bUnitButton);
		
		companyButton = new JButton();
		companyButton.setMargin(new Insets(2,2,2,2));
		companyButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		companyButton.setText("EMPRESA");
		companyButton.setPreferredSize(dim);
		add(companyButton);
		
		JLabel separatorLabel = new JLabel();
		separatorLabel.setPreferredSize(new Dimension(100,160));
		add(separatorLabel);
		
		logOutButton = new JButton();
		logOutButton.setAction(logOutAction);
		logOutButton.setBackground(Color.GRAY);
		logOutButton.setForeground(Color.BLACK);
		logOutButton.setBorder(null);
		logOutButton.setMargin(new Insets(2,2,2,2));
		logOutButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		logOutButton.setText("LOG OUT");
		logOutButton.setPreferredSize(dim);
		add(logOutButton);
	}
	
	public Selector(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}

	public Selector(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public Selector(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	private class LogOutAction extends AbstractAction {
		public LogOutAction() {
			putValue(NAME, "LogOutAction");
			putValue(SHORT_DESCRIPTION, "Back to login screen");
		}
		public void actionPerformed(ActionEvent e) {
			//logOutButton.setBackground(Color.ORANGE);
			//System.out.println(frame != null);
			
			frame.getBasePanel().removeAll();
			frame.getBasePanel().setVisible(false);
			frame.getDownPanel().removeAll();
			frame.getDownPanel().setVisible(false);
			frame.initialize();
			Login loginPanel = new Login(frame.getConn(), frame.getSession(), frame);
			frame.getBasePanel().add(loginPanel, BorderLayout.CENTER);
			frame.getDownPanel().setVisible(true);
			frame.revalidate();
			frame.repaint();
		}
	}
}
