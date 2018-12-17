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
	private static final int PUSH_BUFFER = 3000;	// Time to wait till a push on both touch sensors is counted
	
	/* private float maximumDistance = 0f;
	private float[] sampleBuffer = {0f}; */
	
	private int pushBuffer;
	
	private KeyListener escapeKeyListener;
	private boolean escapeKeyPressed;

	private BoxPushState() {
		escapeKeyListener = new KeyListener() {

			@Override
			public void keyPressed(Key k) {
				System.out.println("Escape key pressed!");			// TODO: Remove this debug output
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
		// TODO
		// example stuff:
		
		escapeKeyPressed = false;
		
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
		/* TODO: Find a way to make program stoppable, a KeyListener is not working because of the absence of multitasking in
		   the OS. (To be tested!) */
		
		
		// find box: -drive slowly
		pmotors.travel(20);
		pmotors.setSpeed(SPEED_OF_WORK);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		pmotors.turnRight(180);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		pmotors.goBackward();
		pmotors.setSpeed(SPEED_OF_WORK);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		
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
		pmotors.goForward();
		pmotors.setSpeed(SPEED_OF_WORK);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		sensorController.tick();
		while (!pushBuffer(PUSH_BUFFER)) {
			if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
			sensorController.tick();
		}
		
		pmotors.quickStop();
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
		pmotors.goForward();
		pmotors.setSpeed(SPEED_OF_WORK);
		sensorController.tick();
		while (!pushBuffer(PUSH_BUFFER)) {
			if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
			sensorController.tick();
		}
		pmotors.quickStop();
		//			- pull back a bit
		pmotors.travel(-2);
		pmotors.setSpeed(SPEED_OF_WORK);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		//			- turn 90� right
		pmotors.turnRight(90);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		//			- drive forward until box is at the wall
		pmotors.goForward();
		pmotors.setSpeed(SPEED_OF_WORK);
		if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
		sensorController.tick();
		while (!pushBuffer(PUSH_BUFFER)) {
			if (escapeKeyPressed) { Executor.get().requestChangeMode(Mode.ModeMenu); return;}
			sensorController.tick();
		}
		pmotors.quickStop();
		
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
		
		//At the end TODO: Keep it current!
		Sound.beep();
		Executor.get().requestChangeMode(Mode.Bridge);
	}
	
	/**
	 * Method to check for a long push. If a push on both touch sensors is longer then the given amount if time it is
	 * is counted as a hard push.
	 * 
	 * @param ms to be waited till a push is recognized as a hard push.
	 * @return boolean indicating if hard push or not.
	 */
	private boolean pushBuffer(long ms) {
		boolean longPush = false;
		long startTime = System.currentTimeMillis();
		long dTime = 0;
		do {
			dTime = Math.abs(System.currentTimeMillis() - startTime);
			sensorController.tick();
			longPush = (sensorController.isLeftTouching() && sensorController.isRightTouching());
		} while (dTime < ms);
		return longPush;
	}
	
	
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
	
	private float createAverage(float[] array) {
		float sum = 0;
		for (int i = 0; i < array.length ; i++) {
			sum += array[i];
		}
		return sum / array.length;
	}
}

