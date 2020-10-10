<<<<<<< HEAD
package event;

public enum EventStatus {
	
	OPEN, IN_PROCESS, CLOSED;
	
	public String toString() {
		switch(this) {
			case OPEN: return "OPEN";
			case IN_PROCESS: return "IN PROCESS";
			case CLOSED: return "CLOSED";
			default : return "UNDEFINED";
		}
	}
}
=======
package event;

public enum EventStatus {
	
	OPEN, IN_PROCESS, CLOSED;
	
	public String toString() {
		switch(this) {
			case OPEN: return "OPEN";
			case IN_PROCESS: return "IN PROCESS";
			case CLOSED: return "CLOSED";
			default : return "UNDEFINED";
		}
	}
}
>>>>>>> 0042cb994e9706a1ff63ed2912742ee974877efb
