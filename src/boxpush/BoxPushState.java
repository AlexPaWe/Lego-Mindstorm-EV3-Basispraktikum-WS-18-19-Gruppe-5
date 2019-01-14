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
	private SensorController sensorController;
	
	private static final int SPEED_OF_WORK = 150;	//TODO: check speed! Maybe faster?!
	
	private KeyListener escapeKeyListener;
	private boolean escapeKeyPressed;

	private BoxPushState() {
		escapeKeyListener = new KeyListener() {

			@Override
			public void keyPressed(Key k) {
				Sound.beep();
				escapeKeyPressed = true;
			}

			@Override
			public void keyReleased(Key k) {
				// Do nothing
			}
		};

		Button.ESCAPE.addKeyListener(escapeKeyListener);
	}

	public static State get() {
		if (instance == null) {
			instance = new BoxPushState();
		}
		return instance;
	}
	
	@Override
	public void onBegin(boolean modeChanged) {
		
		escapeKeyPressed = false;
		
		LCD.clear();
	    LCD.drawString("Box push", 0, 0);
	    motors.stop();
	    
	    // Set SensorController instance for this State.
	    sensorController = SensorController.get();
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// Do nothing
	}
	
	@Override
	public void mainloop() {
		motors.pivotDistanceSensorPark();
		
		// drive deeper into the zone, because the edge distracts rotating
		pmotors.travel(20);
		
		// adjust rotation by hugging the wall
		pmotors.rotate(90);
		pmotors.travel(20);
		pmotors.travel(-2);
		
		// rotate for backward drive
		pmotors.rotate(90);
		
		// find box: -drive slowly
		pmotors.setSpeed(SPEED_OF_WORK);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		pmotors.goBackward();
		pmotors.setSpeed(SPEED_OF_WORK);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		
		motors.pivotDistanceSensorLeft();
		findBox();
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		
		// set distance sensor to start position to avoid collision
		motors.pivotDistanceSensorPark();
		
		pmotors.quickStop();
		pmotors.travel(-1.5);
		pmotors.setSpeed(SPEED_OF_WORK);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		
		//			- turn 90� right
		pmotors.turnLeft(90);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		
		//			- drive till the box is at the wall (or both touch sensors are activated)
		pmotors.travel(40); // TODO
		
		//			- pull back a little (e.g. 3cm)
		pmotors.travel(-4);
		pmotors.setSpeed(SPEED_OF_WORK);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		
		//			- turn 90� left
		pmotors.turnLeft(90);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		
		//			- drive 28cm forward
		pmotors.travel(28);
		pmotors.setSpeed(SPEED_OF_WORK);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		
		//			- turn 90� right
		pmotors.turnRight(90);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		
		//			- drive until both touch sensors hit the wall
		pmotors.travel(20);  // TODO
		
		//			- pull back a bit
		pmotors.travel(-2);
		pmotors.setSpeed(SPEED_OF_WORK);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		
		//			- turn 90� right
		pmotors.turnRight(90);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		
		//			- drive forward until box is at the wall
		pmotors.travel(30);  // TODO
		
		// After box has been pushed in the goal quadrant
		
		// pull back a bit
		pmotors.travel(-3);
		pmotors.setSpeed(SPEED_OF_WORK);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		// turn 90� right
		pmotors.turnRight(90);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		// drive 22cm
		pmotors.travel(22);
		pmotors.setSpeed(SPEED_OF_WORK);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		// turn 90� left
		pmotors.turnLeft(90);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		
		pmotors.goForward();
		pmotors.setSpeed(SPEED_OF_WORK);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		sensorController.setColorModeToRGB();
		
		boolean loop = true;
		
		/*
		 * rgb[2] =
		 * 0.018 brown
		 * 0.054 blue with distance
		 * 0.086 blue
		 */
		while(loop) {
			if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
			sensorController.tick();
			float[] rgb = sensorController.getRgbValue();
			if (rgb[2] > 0.04)
			{
				loop = false;
			}
		}
		pmotors.quickStop();
		
		Sound.beep();
		Executor.get().requestChangeMode(Mode.Bridge);
	}
	
	/**
	 * Method used to find the box while driving. It is accomplished by continuously measuring the distance with
	 * the ultrasonic sensor and detecting the difference between an average value and the most current measurement.
	 */
	private void findBox() {
		float[] sampleArray = new float[10];
		for (int i = 0; i < sampleArray.length; i++)
			sampleArray[i] = 0.5f;	// TODO: Find best default distance to distant wall. Works just fine at the moment!
		boolean found = false;
		float average;
		int i = 0;
		while (!found) {
			average = createAverage(sampleArray);
			
			sensorController.tick();
			sampleArray[i] = sensorController.getDistance();
			i = (i + 1) % sampleArray.length;
			
			//System.out.println("Av. = " + average + "; cur. = " + sampleArray[i]);	// TODO: Remove Debug Output.
			
			if (Math.abs(average - sampleArray[i]) > 0.2)
				found = true;
		}
	}
	
	/**
	 * Method to compute an average of a given array.
	 * 
	 * @param array containing the values
	 * @return average value of the array
	 */
	private float createAverage(float[] array) {
		float sum = 0;
		for (int i = 0; i < array.length ; i++) {
			sum += array[i];
		}
		return sum / array.length;
	}
}

