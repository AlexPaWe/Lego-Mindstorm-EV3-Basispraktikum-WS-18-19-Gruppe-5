package boxpush;

import execution.Executor;
import execution.Mode;
import execution.State;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.robotics.Color;
import robot.SensorController;

public class BoxPushState extends State {
	
	private static BoxPushState instance;
	private SensorController sensorController;
	
	private static final int SPEED_OF_WORK = 150;	//TODO: check speed! Maybe faster?!
	
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
	    
	    // Set SensorController instance for this State.
	    sensorController = SensorController.get();
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mainloop() {
		/*// make program stoppable (apparently it does not work)
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
			
		}); */
		
		// find box: -drive slowly
		pmotors.travel(10);
		pmotors.setSpeed(SPEED_OF_WORK);
		pmotors.turnRight(180);
		pmotors.goBackward();
		pmotors.setSpeed(SPEED_OF_WORK);
		//			-scan for box:
		//				- difference in proximity of wall versus box greater then 20cm
		/* sensorController.tick();
		while(Math.abs(maximumDistance - sampleBuffer[0]) < 0.2) { // difference from the maximumDistance indicates a
																   // box TODO: Find the right value!
			sensorController.tick();
			sampleBuffer[0] = sensorController.getDistance();
			
			maximumDistance = Math.max(maximumDistance, sampleBuffer[0]);
			
			LCD.clear();
			LCD.drawString("maxDist: " + maximumDistance, 0, 0);
			
			//System.out.println(sampleBuffer[0] + "  " + sampleBuffer[1]); // TODO: Only for test purposes
		} */
		findBox();
		
		LCD.clear(); // TODO: Just to remove test printlns
		pmotors.quickStop();
		pmotors.travel(1.5);
		pmotors.setSpeed(SPEED_OF_WORK);
		//			- turn 90° right
		pmotors.turnLeft(90);
		//			- drive till the box is at the wall (or both touch sensors are activated)
		pmotors.goForward();
		pmotors.setSpeed(SPEED_OF_WORK);
		/* sensorController.tick();
		while (!(sensorController.isLeftTouching() && sensorController.isRightTouching())) {
			sensorController.tick();
		} */
		
		pmotors.travel(50);
		pmotors.setSpeed(SPEED_OF_WORK);
		
		pmotors.quickStop();
		//			- pull back a little (e.g. 3cm)
		pmotors.travel(-3);
		pmotors.setSpeed(SPEED_OF_WORK);
		//			- turn 90° left
		pmotors.turnLeft(90);
		//			- drive 23cm forward
		pmotors.travel(23);
		pmotors.setSpeed(SPEED_OF_WORK);
		//			- turn 90° right
		pmotors.turnRight(90);
		//			- drive until both touch sensors hit the wall
		pmotors.goForward();
		pmotors.setSpeed(SPEED_OF_WORK);
		sensorController.tick();
		while (!(sensorController.isLeftTouching() && sensorController.isRightTouching())) {
			sensorController.tick();
		}
		pmotors.quickStop();
		//			- pull back a bit
		pmotors.travel(-2);
		pmotors.setSpeed(SPEED_OF_WORK);
		//			- turn 90° right
		pmotors.turnRight(90);
		//			- drive forward until box is at the wall
		pmotors.goForward();
		pmotors.setSpeed(SPEED_OF_WORK);
		sensorController.tick();
		while (!(sensorController.isLeftTouching() && sensorController.isRightTouching())) {
			sensorController.tick();
		}
		pmotors.quickStop();
		
		// After box has been pushed in the goal quadrant
		
		// pull back a bit
		pmotors.travel(-3);
		pmotors.setSpeed(SPEED_OF_WORK);
		// turn 90° right
		pmotors.turnRight(90);
		// drive 15cm
		pmotors.travel(22);
		pmotors.setSpeed(SPEED_OF_WORK);
		// turn 90° left
		pmotors.turnLeft(90);
		
		pmotors.goForward();
		pmotors.setSpeed(SPEED_OF_WORK);
		sensorController.tick();
		sensorController.setColorModeToColorId();
		while(sensorController.getColorId() != Color.BLUE) {
			sensorController.tick();
		}
		pmotors.quickStop();
		
		//At the end TODO: Keep it current!
		Sound.beep();
		Executor.get().requestChangeMode(Mode.ModeMenu);
	}
	
	
	private void findBox() {
		float[] sampleArray = new float[10];
		for (int i = 0; i < sampleArray.length; i++)
			sampleArray[i] = 0.5f;	// TODO: Find best default distance to distant wall.
		boolean found = false;
		float average;
		int i = 0;
		while (!found) {
			average = createAverage(sampleArray);
			
			sensorController.tick();
			sampleArray[i] = sensorController.getDistance();
			i = (i + 1) % sampleArray.length;
			
			System.out.println("Av. = " + average + "; cur. = " + sampleArray[i]);	// TODO: Remove Debug Output.
			
			if (Math.abs(average - sampleArray[i]) > 0.2)
				found = true;
		}
	}
	
	private float createAverage(float[] array) {
		float sum = 0;
		for (int i = 0; i < array.length ; i++) {
			sum += array[i];
		}
		return sum / array.length;
	}
}

