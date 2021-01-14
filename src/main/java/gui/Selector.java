package main.java.gui;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.BoxLayout;

public class Selector extends JPanel {

	public Selector() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		// TODO Auto-generated constructor stub
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
