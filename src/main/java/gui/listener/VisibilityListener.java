package main.java.gui.listener;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Timer;

public class VisibilityListener implements ComponentListener {

	Timer timer;
	String panel;
	
	public VisibilityListener(Timer timer, String panel) {
		this.timer = timer;
		this.panel = panel;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentHidden(ComponentEvent e) {
		  if(timer != null) {
			  timer.cancel();
			  System.out.println("Se ha cerrado la ventana " + panel);
		  }

	}

}
