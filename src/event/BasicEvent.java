package event;

import java.util.LinkedList;
import java.util.List;

public class BasicEvent {

	private static int id = 0;
	private String title;
	private List<UpdatedEvent> updates;
	
	public BasicEvent (String title) {
		id = id + 1;
		int titleLength = title.length();
		if (titleLength <= 200) {
			this.title = title;
		} else {
			this.title = title.substring(0, 200);
		}
		updates = new LinkedList<UpdatedEvent>();
	}
	
}
