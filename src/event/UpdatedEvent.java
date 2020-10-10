<<<<<<< HEAD
package event;

import java.util.Date;

public class UpdatedEvent {

	private Date date;
	private EventStatus status;
	private final int basicEventId;
	private static int id = 0;
	private String description;
	
	public UpdatedEvent (int basicEventId) {
		date = new Date();
		status = EventStatus.OPEN;
		this.basicEventId = basicEventId;
		id = id + 1;
	}
	
	
}
=======
package event;

import java.util.Date;

public class UpdatedEvent {

	private Date date;
	private EventStatus status;
	private final int basicEventId;
	private static int id = 0;
	private String description;
	
	public UpdatedEvent (int basicEventId) {
		date = new Date();
		status = EventStatus.OPEN;
		this.basicEventId = basicEventId;
		id = id + 1;
	}
	
	
}
>>>>>>> 0042cb994e9706a1ff63ed2912742ee974877efb
