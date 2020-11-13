package types_states;

public class EventsStatesContainer {

	private static EventState evState;
	private static EventType evType;
	private static UserType uType;
	
	public EventsStatesContainer(EventState evState, EventType evType, UserType uType) {
		EventsStatesContainer.evState = evState;
		EventsStatesContainer.evType = evType;
		EventsStatesContainer.uType = uType;
	}

	public static EventState getEvState() {
		return evState;
	}

	public static void setEvState(EventState evState) {
		EventsStatesContainer.evState = evState;
	}

	public static EventType getEvType() {
		return evType;
	}

	public static void setEvType(EventType evType) {
		EventsStatesContainer.evType = evType;
	}

	public static UserType getuType() {
		return uType;
	}

	public static void setuType(UserType uType) {
		EventsStatesContainer.uType = uType;
	}


	
	
}
