package boxpush;

import execution.State;
import lejos.hardware.lcd.LCD;

public class BoxPushState extends State {
	
	private static BoxPushState instance;

	private BoxPushState() {
	}

	public static State get() {
		if (instance == null) {
			instance = new BoxPushState();
		}
		return instance;
	}
	
	@Override
	public void onBegin(boolean modeChanged) {
		// TODO
		// example stuff:
		
		LCD.clear();
	    LCD.drawString("Line Follow", 0, 0);
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mainloop() {
		// TODO
	}
}