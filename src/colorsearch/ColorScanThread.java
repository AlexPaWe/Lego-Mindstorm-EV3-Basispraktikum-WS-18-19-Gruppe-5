package colorsearch;

import lejos.hardware.Sound;
import lejos.robotics.Color;
import robot.SensorController;

public class ColorScanThread extends Thread {

	private boolean whiteFound = false;
	
	private boolean redFound = false;
	
	private SensorController sensorController = SensorController.get();
	
	public void run() {
		sensorController.setColorModeToColorId();
		while (!(whiteFound && redFound)) {
			sensorController.tick();
			int colorID = sensorController.getColorId();
			switch (colorID) {
				case Color.RED:
					redFound = true;
					break;
				case Color.WHITE:
					whiteFound = true;
					break;
				default: // Do nothing!
			}
			
			if (whiteFound || redFound) {
				Sound.beep();
			}
			
			if (whiteFound && redFound) {
				// TODO!
			}
		}
	}
}
