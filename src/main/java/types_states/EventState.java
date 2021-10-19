package main.java.types_states;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import main.java.persistence.PersistenceManager;

public class EventState {

	public static final String TABLE_NAME = "event_type";
	
	//Map <id, descripcion> Almacena la tabla event_state de la base de datos
	private Map <Integer, String> eventStates = new LinkedHashMap<Integer, String>();
	
	public EventState () {
		
	}
	
	/**
	 * Devuelve el número de estados de evento almacenados en la base de datos
	 * @return Número de estados de eventos 
	 */
	public int getNumberOfEventStates() {
		return eventStates.size();
	}
	
	/**
	 * Devuelve un array con el nombre de los diferentes estados de eventos
	 * @return Estados de eventos
	 */
	public String[] getEventStatesArray() {
		String[] estadosDeEvento = new String[getNumberOfEventStates()];
		Collection<String> estados = eventStates.values();
		estadosDeEvento = estados.toArray(estadosDeEvento);
		return estadosDeEvento;
	}
	
	/**
	 * Retorna el id del estado de evento pasado por parámetro
	 * @param eventState estado de evento del que queremos saber su id
	 * @return id del estado de evento (-1 si no existe)
	 */
	public int getEventStateId (String eventState) {
		for (Integer key : eventStates.keySet()) {
			if (eventState.equals(eventStates.get(key))) {
				return key;
			}
		}
		//El estado de evento no existe
		return -1;	
	}
	
	/**
	 * Retorna el estado de evento que corresponde a la clave pasada por parámetro
	 * @param key
	 * @return estado de evento o null si la clave no existe
	 */
	public String getEventState (int key) {
		if (eventStates.containsKey(key)) {
			return eventStates.get(key);
		} else {
			return null;
		}
	}
	
	/**
	 * Conecta con la base de datos y almacena cada estado de evento con su clave
	 * en eventStates
	 */
	public void loadData(Connection conn) {
		Statement statement = null;
		ResultSet results = null;
		String sql = "SELECT * FROM event_state;";
		try {
			statement = conn.createStatement();
			results = PersistenceManager.getResultSet(statement, sql);
			while(results.next()) {
				int id = results.getInt(1);
				String eventType = results.getString(2);
				eventStates.put(id, eventType);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PersistenceManager.closeResultSet(results);
			PersistenceManager.closeStatement(statement);
		}
		
		
		//Debug
		System.out.println("Estados de eventos cargados correctamente");
		System.out.print(eventStates.entrySet());
		System.out.println();

	}
	
	public Map <Integer, String> getEventStates() {
		return this.eventStates;
	}
	
}
