package main.java.gui;

import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import main.java.session.CurrentSession;
import main.java.toolbox.ToolBox;
import main.java.types_states.EventType;

public class EventTypeUI extends JPanel {

	//Se asignan a la variable okActionSelector para determinar el comportamiento
	//de la acción okAction
	private static final int OK_ACTION_UNDEFINED = 0; //Por defecto
	private static final int OK_ACTION_EDIT = 1;
	private static final int OK_ACTION_NEW = 2;
	
	private static final String NO_AREA = "<Ningún area seleccionable>";
	private static final String DIALOG_INFO = "info";
	private static final String DIALOG_YES_NO = "yes_no";
	
	private CurrentSession session;
	private Timestamp tNow = ToolBox.getTimestampNow();
	//Temporizador de comprobación de cambios en los datos de la sesión
	private Timer timer;
	//Registra si el panel está visible o no
	private boolean panelVisible;
	
	private JTextField EventTypeNameField = new JTextField();
	//Lista de todas las areas existentes en la base de datos
	private List<EventType> allEventTypes;
	//Registra el area seleccionada en cada momento
	private EventType selectedEventType;
	//Etiqueta informativa de longitud máxima de datos
	private JLabel maxLengthLabel = new JLabel();
	//Backup del contenido del campo Tipo de Evento
	private String textFieldBackup;
	
	//Elementos gráficos
	
	
	//Elementos que aparecerán en la lista de tipos de eventos
	private String[] registeredEventTypes;
	//Modelo de datos de la lista de tipos de eventos
	private DefaultListModel<String> registeredModel = new DefaultListModel<String>();
	//Lista de tipos de evento
	private JList<String> registeredList;
	//Contenedor de la lista de tipos de evento
	private JScrollPane registeredScrollPane;
	
//	private final Action editAction = new EditAction();
//	private final Action cancelAction = new CancelAction();
//	private final Action oKAction = new OKAction();
//	private final Action newAction = new NewAction();
//	private final Action deleteAction = new DeleteAction();
	
	//Registra la acción a realizar por el botón aceptar
	private int okActionSelector = EventTypeUI.OK_ACTION_UNDEFINED;
	
	public EventTypeUI(CurrentSession session) {
		// TODO Auto-generated constructor stub
	}

//	public EventTypeUI(LayoutManager layout) {
//		super(layout);
//		// TODO Auto-generated constructor stub
//	}
//
//	public EventTypeUI(boolean isDoubleBuffered) {
//		super(isDoubleBuffered);
//		// TODO Auto-generated constructor stub
//	}
//
//	public EventTypeUI(LayoutManager layout, boolean isDoubleBuffered) {
//		super(layout, isDoubleBuffered);
//		// TODO Auto-generated constructor stub
//	}

}
