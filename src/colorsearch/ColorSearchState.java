package colorsearch;

import execution.State;
import lejos.hardware.lcd.LCD;
import robot.SensorController;

public class ColorSearchState extends State {
	
	private static ColorSearchState instance;
	
	SensorController sensorController;
	
	boolean redFound = false;
	boolean whiteFound = false;

	private ColorSearchState() {
	}

	public static State get() {
		if (instance == null) {
			instance = new ColorSearchState();
		}
		return instance;
	}
	
	@Override
	public void onBegin(boolean modeChanged) {
		// TODO
		// example stuff:
		
		LCD.clear();
	    LCD.drawString("Color search", 0, 0);
	    
	    sensorController = sensorController.get();
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mainloop() {
		while (!redFound && !whiteFound) {
			//TODO: Implement algorithm here!
		}
	}
}
