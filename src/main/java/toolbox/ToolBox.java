package main.java.toolbox;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ToolBox {

	public ToolBox() {

	}
	
	/**
	 * Devuelve la fecha y la hora actuales
	 * @return Timestamp con la fecha y la hora actuales
	 */
	public static Timestamp getTimestampNow() {
		Calendar calendar = Calendar.getInstance();
		Date dNow = calendar.getTime();
		Timestamp tNow = new Timestamp(dNow.getTime());
		return tNow;
	}
	
	/**
	 * Da formato a la fecha y la hora del Timestamp pasado por parámetro
	 * @param timestamp Timestamp a formatear
	 * @param pattern patrón de formateo, si es null se usa el patrón por defecto del método
	 * @return Timestamp formateado
	 */
	public static String formatTimestamp(Timestamp timestamp, String pattern) {
		if (pattern == null) {
			pattern = "EEEE, dd-MM-yyyy HH:mm:ss";
		}
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		String niceTimestamp = formatter.format(timestamp);
		return niceTimestamp;
	}

	/**
	 * Establece por defecto el color negro como color de la letra del JComboBox pasado por parámetro, incluso
	 * en el caso de que el combobox esté deshabilitado
	 * @param combobox JComboBox al que le queremos aplicar el formato
	 */
	public static void setBlackForeground(JComboBox combobox) {
		combobox.setRenderer(new DefaultListCellRenderer() {
		   @Override
		   public void paint(Graphics g) {
		       setForeground(Color.BLACK);
		       super.paint(g);
		   };
	   });
	}
	
	/**
	 * Devuelve un array de String ordenado alfabéticamente a partir de un ArrayList de String desordenado
	 * @param list ArrayList a transformar y ordenar
	 * @return Array de String ordenado
	 */
	public static String[] toSortedArray(List<String> list) {
		Object[] object = (Object[]) list.toArray();
		String[] itemList = new String[object.length];
		for (int i = 0; i < object.length; i++) {
			itemList[i] = object[i].toString();
		}
		Arrays.sort(itemList);
		return itemList;
	}
	
	/**
	 * Centra la ventana del JFrame pasádo por parámetro en el monitor que esté ejecutando la aplicación
	 * @param messageFrame ventana a centrar
	 * @param displays lista de monitores del sistema
	 * @param currentDisplay monitor en el que se está ejecutando la aplicación
	 * @param width anchura de la ventana
	 * @param height altura de la ventana 
	 */
	public static void centerFrame(Frame messageFrame, GraphicsDevice [] displays, GraphicsDevice currentDisplay, int width, int height) {
		int panelWidth = width;
		int panelHeight = height;
		int coordinateX = currentDisplay.getDefaultConfiguration().getBounds().x;
		int coordinateY = currentDisplay.getDefaultConfiguration().getBounds().y;
		int currentWidth = 0;
		int currentHeight = 0;
		//Centrado de pantalla multimonitor
		for (int i = 0; i < displays.length; i++) {
			if (currentDisplay.getIDstring().equals(displays[i].getIDstring())) {
				currentWidth = currentDisplay.getDisplayMode().getWidth();
				currentHeight = currentDisplay.getDisplayMode().getHeight();
				messageFrame.setBounds((currentWidth - panelWidth) / 2 + coordinateX, (currentHeight - panelHeight) / 2 + coordinateY, panelWidth, panelHeight);
			}
		}
	}
	
	/**
	 * Muestra un cuadro de diálogo centrado en el monitor en el que se esté ejecutando la aplicación.
	 * @param message Mensaje que aparecerá en el cuadro de diálogo
	 * @param panel componente raíz del cuadro de diálogo
	 * @param dialogType tipo de cuadro de diálogo a mostrar
	 * @return valor numérico de la opción escogida por el usuario
	 */
	public static int showDialog(String message, JPanel panel, String dialogType) {
		Frame parentFrame = (Frame) SwingUtilities.getRoot((Component) panel);
		GraphicsDevice [] displays = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		GraphicsDevice currentDisplay = parentFrame.getGraphicsConfiguration().getDevice();
		Frame messageFrame = new Frame(currentDisplay.getDefaultConfiguration());
		Rectangle frameRectangle = messageFrame.getBounds();
		centerFrame(messageFrame, displays, currentDisplay, frameRectangle.width, frameRectangle.height);
		if (dialogType.equals("info")) {
			JOptionPane.showMessageDialog(messageFrame, message, "Información", JOptionPane.WARNING_MESSAGE);
			return -1;
		} else if (dialogType.equals("yes_no")) {		
			int optionSelected = JOptionPane.showConfirmDialog(messageFrame, message, "Advertencia", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			return optionSelected;
		} else {
			return -1;
		}
	}
	
	
}
