package main.java.gui;

import java.awt.LayoutManager;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;

public class Selector extends JPanel {

	public Selector() {
		
		setLayout(new GridLayout(8, 0, 10, 10));
		setAlignmentX(Component.CENTER_ALIGNMENT);
		Dimension dim = new Dimension(100,80);
		
		JButton eventButton = new JButton();
		eventButton.setMargin(new Insets(2,2,2,2));
		eventButton.setFont(new Font("Tahoma", Font.BOLD, 14));
//		eventButton.setText("<html><center>"+"GESTIÓN" + "<br>" + "DE" + "<br>" + "EVENTOS" + "</center></html>");
		eventButton.setText("EVENTOS");
		eventButton.setMinimumSize(dim);
		eventButton.setPreferredSize(dim);
		add(eventButton);
		
		JButton eventTypeButton = new JButton();
		eventTypeButton.setMargin(new Insets(2,2,2,2));
		eventTypeButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		eventTypeButton.setText("<html><center>"+"TIPOS" + "<br>" + "DE" + "<br>" + "EVENTO" + "</center></html>");
		eventTypeButton.setMinimumSize(dim);
		eventTypeButton.setPreferredSize(dim);
		add(eventTypeButton);
		
		JButton areaButton = new JButton();
		areaButton.setMargin(new Insets(2,2,2,2));
		areaButton.setFont(new Font("Tahoma", Font.BOLD, 14));
//		areaButton.setText("<html><center>"+"GESTIÓN" + "<br>" + "DE" + "<br>" + "AREAS" + "</center></html>");
		areaButton.setText("AREAS");
		areaButton.setPreferredSize(dim);
		add(areaButton);
		
		JButton userButton = new JButton();
		userButton.setMargin(new Insets(2,2,2,2));
		userButton.setFont(new Font("Tahoma", Font.BOLD, 14));
//		userButton.setText("<html><center>"+"GESTIÓN" + "<br>" + "DE" + "<br>" + "USUARIOS" + "</center></html>");
		userButton.setText("USUARIOS");
		userButton.setPreferredSize(dim);
		add(userButton);
		
		JButton bUnitButton = new JButton();
		bUnitButton.setMargin(new Insets(2,2,2,2));
		bUnitButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		bUnitButton.setText("<html><center>"+"UNIDADES" + "<br>" + "DE" + "<br>" + "NEGOCIO" + "</center></html>");
		bUnitButton.setPreferredSize(dim);
		add(bUnitButton);
		
		JButton companyButton = new JButton();
		companyButton.setMargin(new Insets(2,2,2,2));
		companyButton.setFont(new Font("Tahoma", Font.BOLD, 14));
//		userButton.setText("<html><center>"+"GESTIÓN" + "<br>" + "DE" + "<br>" + "USUARIOS" + "</center></html>");
		companyButton.setText("EMPRESA");
		companyButton.setPreferredSize(dim);
		add(companyButton);
		
		JLabel separatorLabel = new JLabel();
		separatorLabel.setPreferredSize(new Dimension(100,160));
		add(separatorLabel);
		
		//add(Box.createVerticalGlue());
		
		JButton logOutButton = new JButton();
		logOutButton.setBackground(Color.GRAY);
		logOutButton.setForeground(Color.BLACK);
		logOutButton.setBorder(null);
		logOutButton.setMargin(new Insets(2,2,2,2));
		logOutButton.setFont(new Font("Tahoma", Font.BOLD, 14));
//		userButton.setText("<html><center>"+"GESTIÓN" + "<br>" + "DE" + "<br>" + "USUARIOS" + "</center></html>");
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

}
