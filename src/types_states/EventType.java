package types_states;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import persistence.PersistenceManager;

public class EventType {

	private Connection connection;
	//Map <id, descripcion> Almacena la tabla event_type de la base de datos
	private Map <Integer, String> eventTypes = new LinkedHashMap<Integer, String>();
	
	public EventType (Connection connection) {
		this.connection = connection;
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
	public String[] getEventTypes() {
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
	 */
	public void loadData() {
		Statement statement = null;
		ResultSet results = null;
		String sql = "SELECT * FROM event_type;";
		try {
			statement = connection.createStatement();
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
//		System.out.println("TIPOS DE USUARIO: ");
//		for (int i = 0; i < getNumberOfUserTypes(); i++) {
//			System.out.print(getUserTypes()[i] + ", ");
//		}
	}
}
