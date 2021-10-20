package main.java.types_states;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import main.java.persistence.PersistenceManager;

public class EventType {

	public static final String TABLE_NAME = "event_type";
	
	//Map <id, descripcion> Almacena la tabla event_type de la base de datos
	private Map <Integer, String> eventTypes = new LinkedHashMap<Integer, String>();
	
	public EventType () {

	}
	
	/**
	 * Devuelve el número de tipos de evento almacenados en la base de datos
	 * @return Número de tipos de eventos 
	 */
	public int getNumberOfEventTypes() {
		return eventTypes.size();
	}
	
	/**
	 * Devuelve un array con el nombre de los diferentes tipos de eventos
	 * @return Tipos de eventos
	 */
	public String[] getEventTypesArray() {
		String[] tiposDeEvento = new String[getNumberOfEventTypes()];
		Collection<String> tipos = eventTypes.values();
		tiposDeEvento = tipos.toArray(tiposDeEvento);
		return tiposDeEvento;
	}
	
	/**
	 * Retorna el id del tipo de evento pasado por parámetro
	 * @param eventType tipo de evento del que queremos saber su id
	 * @return id del tipo de evento (-1 si no existe)
	 */
	public int getEventTypeId (String eventType) {
		for (Integer key : eventTypes.keySet()) {
			if (eventType.equals(eventTypes.get(key))) {
				return key;
			}
		}
		//El tipo de evento no existe
		return -1;	
	}
	
	/**
	 * Retorna el tipo de evento que corresponde a la clave pasada por parámetro
	 * @param key
	 * @return tipo de evento o null si la clave no existe
	 */
	public String getEventType (int key) {
		if (eventTypes.containsKey(key)) {
			return eventTypes.get(key);
		} else {
			return null;
		}
	}
	
	/**
	 * Conecta con la base de datos y almacena cada tipo de evento con su clave
	 * en eventTypes
	 * @param conn conexión con la base de datos
	 */
	public void loadData(Connection conn) {
		Statement statement = null;
		ResultSet results = null;
		String sql = "SELECT * FROM event_type;";
		try {
			statement = conn.createStatement();
			results = PersistenceManager.getResultSet(statement, sql);
			while(results.next()) {
				int id = results.getInt(1);
				String eventType = results.getString(2);
				eventTypes.put(id, eventType);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closeStatement(statement);
		}
		
		
		//Debug
		System.out.println("Tipos de eventos cargados correctamente");
		System.out.print(eventTypes.entrySet());
		System.out.println();

	}
	
	/**
	 * Inserta un nuevo tipo de evento en la tabla event_type
	 * @param conn conexión con la base de datos
	 * @param descripcion descripción del tipo de evento
	 * @return true si la insercion se hizo con éxito, false si no
	 */
	public boolean saveEventTypeToDB(Connection conn, String descripcion) {
		PreparedStatement pstm = null;
		String sql = "INSERT INTO event_type (descripcion) "
				+ "VALUES (?);";
		try {
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, descripcion);
			pstm.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			PersistenceManager.closePrepStatement(pstm);
		}
	}
	
	/**
	 * Añade un nuevo tipo de evento a la lista eventTypes
	 * @param conn conexión con la base de datos
	 * @param descripcion descripción del nuevo tipo de evento
	 * @return true si la insercion se hizo con éxito, false si no
	 */
	public boolean addNewEventType (Connection conn, String descripcion) {
		if (saveEventTypeToDB(conn, descripcion)) {
			int id = PersistenceManager.getLastElementIdFromDB(conn, TABLE_NAME);
			eventTypes.put(id, descripcion);
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * Métodos necesarios para completar la clase
	 * deleteEventTypeFromDB(Connection conn, String descripcion)
	 * updateEventTypeToDB(Connection conn, String descripcion)
	 * isEventTypeUsed(Connection conn, String descripcion) --comprobar todos los eventos para saber si se ha usado un tipo de evento determinado--
	 */
	
	public Map <Integer, String> getEventTypes() {
		return this.eventTypes;
	}

}
