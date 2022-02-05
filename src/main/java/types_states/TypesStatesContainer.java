package main.java.types_states;

/**
 * Clase contenedora de los objetos que almacenan las listas de tipos de eventos, tipos de usuarios
 * y tipos de incidencias
 * @author Alfred Tomey
 */
public class TypesStatesContainer {

	private static EventState evState;
	private static EventType evType;
	private static UserType uType;
	
	public TypesStatesContainer(EventState evState, EventType evType, UserType uType) {
		TypesStatesContainer.evState = evState;
		TypesStatesContainer.evType = evType;
		TypesStatesContainer.uType = uType;
	}

	public static EventState getEvState() {
		return evState;
	}

	public static void setEvState(EventState evState) {
		TypesStatesContainer.evState = evState;
	}

	public static EventType getEvType() {
		return evType;
	}

	public static void setEvType(EventType evType) {
		TypesStatesContainer.evType = evType;
	}

	public static UserType getuType() {
		return uType;
	}

	public static void setuType(UserType uType) {
		TypesStatesContainer.uType = uType;
	}


	
	
}
