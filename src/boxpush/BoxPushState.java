package boxpush;

import execution.Executor;
import execution.Mode;
import execution.State;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.Sound;
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
	    motors.pivotDistanceSensorLeft();
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mainloop() {
		// make program stoppable
		Button.ESCAPE.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(Key k) {
				Sound.beep();
				Executor.get().requestChangeMode(Mode.ModeMenu);
			}

			@Override
			public void keyReleased(Key k) {
				//Executor.get().requestChangeMode(Mode.ModeMenu);
			}
			
		});
		
		// TODO: implement the following comments
		
		// find box: -drive slowly
		pmotors.travel(3);
		pmotors.turnRight(180);
		pmotors.goBackward();
		pmotors.setSpeed(220); //TODO: check speed! Maybe faster?!
		//			-scan for box:
		//				- difference in proximity of wall versus box greater then 20cm
		SensorController sensorController = SensorController.get();
		sensorController.tick();
		while(Math.abs(maximumDistance - sampleBuffer[0]) < 0.2) { // difference from the maximumDistance indicates a
																   // box TODO: Find the right value!
			sensorController.tick();
			sampleBuffer[0] = sensorController.getDistance();
			
			maximumDistance = Math.max(maximumDistance, sampleBuffer[0]);
			
			LCD.clear();
			LCD.drawString("maxDist: " + maximumDistance, 0, 0);
			
			//System.out.println(sampleBuffer[0] + "  " + sampleBuffer[1]); // TODO: Only for test purposes
		}
		LCD.clear(); // TODO: Just to remove test printlns
		pmotors.quickStop();
		pmotors.travel(1.5);
		//			- turn 90° right
		pmotors.turnLeft(90);
		//			- drive till the box is at the wall (or both touch sensors are activated)
		pmotors.goForward();
		pmotors.setSpeed(220);
		sensorController.tick();
		while (!(sensorController.isLeftTouching() && sensorController.isRightTouching())) {
			sensorController.tick();
		}
		pmotors.quickStop();
		//			- pull back a little (e.g. 3cm)
		pmotors.travel(-3);
		//			- turn 90° left
		pmotors.turnLeft(90);
		//			- drive 23cm forward
		pmotors.travel(23);
		//			- turn 90° right
		pmotors.turnRight(90);
		//			- drive until both touch sensors hit the wall
		pmotors.goForward();
		pmotors.setSpeed(220);
		sensorController.tick();
		while (!(sensorController.isLeftTouching() && sensorController.isRightTouching())) {
			sensorController.tick();
		}
		pmotors.quickStop();
		//			- pull back a bit
		pmotors.travel(-2);
		//			- turn 90° right
		pmotors.turnRight(90);
		//			- drive forward until box is at the wall
		pmotors.goForward();
		pmotors.setSpeed(220);
		sensorController.tick();
		while (!(sensorController.isLeftTouching() && sensorController.isRightTouching())) {
			sensorController.tick();
		}
		pmotors.quickStop();
		
		//At the end TODO: Keep it current!
		Sound.beep();
		Executor.get().requestChangeMode(Mode.ModeMenu);
	}
}
