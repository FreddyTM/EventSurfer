package main.java.toolbox;

import java.awt.Color;
import java.awt.Graphics;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;

public class ToolBox {

	public ToolBox() {
		// TODO Auto-generated constructor stub
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
	
}
