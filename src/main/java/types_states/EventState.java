package main.java.types_states;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import main.java.persistence.PersistenceManager;

/**
 * Almacena los estados de las incidencias y proporciona los métodos necesarios para
 * realizar las operaciones CRUD con la base de datos y con el resto de componentes del 
 * programa
 * @author Alfred Tomey
 */
public class EventState {

	public static final String TABLE_NAME = "event_state";
	
	//Map <id, descripcion> Almacena la tabla event_state de la base de datos
	private Map <Integer, String> eventStates = new LinkedHashMap<Integer, String>();
	
	public EventState () {
		
	}
	
	/**
	 * Devuelve el número de estados de evento almacenados en la base de datos
	 * @return Número de estados de incidencias 
	 */
	public int getNumberOfEventStates() {
		return eventStates.size();
	}
	
	/**
	 * Devuelve un array con el nombre de los diferentes estados de incidencias ordenados
	 * alfabéticamente
	 * @return Estados de incidencias
	 */
	public String[] getEventStatesArray() {
		String[] estadosDeEvento = new String[getNumberOfEventStates()];
		Collection<String> estados = eventStates.values();
		estadosDeEvento = estados.toArray(estadosDeEvento);
		return estadosDeEvento;
	}
	
	/**
	 * Retorna el id del estado de incidencia pasado por parámetro
	 * @param eventState estado de incidencia del que queremos saber su id
	 * @return id del estado de incidencia (-1 si no existe)
	 */
	public int getEventStateId (String eventState) {
		for (Integer key : eventStates.keySet()) {
			if (eventState.equals(eventStates.get(key))) {
				return key;
			}
		}
		return -1;	
	}
	
	/**
	 * Retorna el estado de incidencia que corresponde a la clave pasada por parámetro
	 * @param key clave del Map eventStates con el que localizar el estado de la incidencia
	 * @return estado de incidencias o null si la clave no existe
	 */
	public String getEventState (int key) {
		if (eventStates.containsKey(key)) {
			return eventStates.get(key);
		} else {
			return null;
		}
	}
	
	/**
	 * Conecta con la base de datos y almacena cada estado de incidencia con su clave
	 * en eventStates
	 * @param conn conexión cno la base de datos
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
	}
	
	public Map <Integer, String> getEventStates() {
		return this.eventStates;
	}
	
}
