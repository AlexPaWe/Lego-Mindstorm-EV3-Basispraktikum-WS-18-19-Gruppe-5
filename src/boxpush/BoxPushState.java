package boxpush;

import execution.State;
import lejos.hardware.lcd.LCD;
import robot.SensorController;

public class BoxPushState extends State {
	
	private static BoxPushState instance;
	
	private float maximumDistance = 0f;
	private float[] sampleBuffer = {0f};

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
	    LCD.drawString("Box push", 0, 0);
	    motors.stop();
	    motors.pivotDistanceSensorRight();
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mainloop() {
		// TODO: implement the following comments
		
		// find box: -drive slowly
		pmotors.goForward();
		pmotors.setSpeed(220); //TODO: check speed! Maybe faster?!
		//			-scan for box:
		//				- difference in proximity of wall versus box greater then 20cm
		SensorController sensorController = SensorController.get();
		while(Math.abs(maximumDistance - sampleBuffer[0]) < 0.2) { // difference from the maximumDistance indicates a box
			
			sensorController.tick();
			sampleBuffer[0] = sensorController.getDistance();
			
			maximumDistance = Math.max(maximumDistance, sampleBuffer[0]);
			
			//System.out.println(sampleBuffer[0] + "  " + sampleBuffer[1]); // TODO: Only for test purposes
		}
		LCD.clear(); // TODO: Just to remove test printlns
		pmotors.quickStop();
		//			- drive 11.5cm more (to center box)
		pmotors.travel(11.5);
		//			- turn 90� right
		pmotors.turnRight(90);
		//			- drive till the box is at the wall (or both touch sensors are activated)
		while (!sensorController.isLeftTouching() && !sensorController.isRightTouching()) {
			pmotors.goForward();
		}
		//			- pull back a little (e.g. 3cm)
		pmotors.travel(-3);
		//			- turn 90� left
		pmotors.turnLeft(90);
		//			- drive 20cm forward
		pmotors.travel(20);
		//			- turn 90� right
		pmotors.turnRight(90);
		//			- drive until both touch sensors hit the wall
		while (!sensorController.isLeftTouching() && !sensorController.isRightTouching()) {
			pmotors.goForward();
		}
		//			- pull back a bit
		pmotors.travel(-3);
		//			- turn 90� right
		pmotors.turnRight(90);
		//			- drive forward until box is at the wall
		while (!sensorController.isLeftTouching() && !sensorController.isRightTouching()) {
			pmotors.goForward();
		}
	}
}
