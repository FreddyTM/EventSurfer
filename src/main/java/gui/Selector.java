package main.java.gui;

import java.awt.LayoutManager;
import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

public class Selector extends JPanel {

	public Selector() {
		
		setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JButton eventoButton = new JButton("GESTIÓN EVENTOS");
		eventoButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		eventoButton.setText("<html><center>"+"GESTIÓN" + "<br>" + "DE" + "<br>" + "EVENTOS" + "</center></html>");
		eventoButton.setPreferredSize(new Dimension(100,100));
		add(eventoButton);
		
		JButton tipoEventoButton = new JButton("New button");
		tipoEventoButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		tipoEventoButton.setText("<html><center>"+"TIPOS" + "<br>" + "DE" + "<br>" + "EVENTO" + "</center></html>");
		tipoEventoButton.setPreferredSize(new Dimension(100,100));
		add(tipoEventoButton);
		
		JButton areaButton = new JButton("New button");
		areaButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		areaButton.setText("<html><center>"+"GESTIÓN" + "<br>" + "DE" + "<br>" + "AREAS" + "</center></html>");
		areaButton.setPreferredSize(new Dimension(100,100));
		add(areaButton);
		

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
