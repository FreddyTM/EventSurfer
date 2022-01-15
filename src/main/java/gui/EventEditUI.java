package main.java.gui;

import java.sql.Timestamp;

import javax.swing.JPanel;

import main.java.event.Event;
import main.java.event.EventUpdate;
import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;

public class EventEditUI extends JPanel{
	
	//Formato de presentación de fecha/hora
	private static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss";
	
	//Se asignan a la variable actionSelector para determinar la acción a ejecutar
	private static final int EVENTEDIT_ACTION_UNDEFINED = 0; //Default
	private static final int EVENTEDIT_ACTION_NEW_EVENT = 1;
	private static final int EVENTEDIT_ACTION_EDIT_EVENT = 2;
	private static final int EVENTEDIT_ACTION_NEW_UPDATE = 4;
	private static final int EVENTEDIT_ACTION_EDIT_UPDATE = 5;

//	//Tipo de cuadro de diálogo
//	private static final String DIALOG_YES_NO = "yes_no";
	
	//Registra la acción a realizar según el botón activado
	private int actionSelector = EVENTEDIT_ACTION_UNDEFINED;
	
	private CurrentSession session;
	private Selector selector;
	private Timestamp tNow = ToolBox.getTimestampNow();
	//Registra si el panel está visible o no
	private boolean panelVisible;
	
	Event eventSelected;
	EventUpdate updateSelected;
	
	public EventEditUI(CurrentSession session, Selector selector, int actionSelector) {
		this.session = session;
		this.selector = selector;
		this.actionSelector = actionSelector;
		
		//Swing Components
		
		
		//Initial setup
		setup(actionSelector);
		
		
	}
	
	/**
	 * Configura la activación / desactivación de los componentes en pantalla en función de la acción que se vaya a realizar, y la
	 * información inicial que presentan
	 */
	private void setup(int setupOption) {
		switch (setupOption) {
			//New event
			case EVENTEDIT_ACTION_NEW_EVENT:
				//Debug
				System.out.println("Nueva incidencia");
				
				
				break;
			//Edit event
			case EVENTEDIT_ACTION_EDIT_EVENT:
				//Debug
				System.out.println("Editando incidencia");
				
				
				break;
			//New update
			case EVENTEDIT_ACTION_NEW_UPDATE:
				//Debug
				System.out.println("Nueva actualización");
				
				
				break;
			//Edit update
			case EVENTEDIT_ACTION_EDIT_UPDATE:
				//Debug
				System.out.println("Editando actualización");
				
				
				break;
			default:
				//Undefined option
		}
	}

}
