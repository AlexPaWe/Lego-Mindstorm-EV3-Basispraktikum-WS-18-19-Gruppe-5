package colorsearch;

import java.util.Date;

import execution.Executor;
import execution.Mode;
import execution.State;
import lejos.hardware.lcd.LCD;
import robot.MotorController;
import robot.SensorController;
import robot.SoundController;
import robot.MotorController.Direction;

public class ColorSearchState extends State {
	
	private static final float GENERAL_MOTOR_SPEED = 320f; // 180 works. TODO: faster
	private static final float K_P_KRIT = 2000f; // 1000 works. TODO: adjust to higher speed
	/*
	 * 0.01f is a good value for distance controlling
	 * 100000f basically disables controlling and just drives forward
	 */
	private static final float THRESHOLD = 0.01f;
	
	private static final float TRACK_DELTA = 0.06f;
	private static final float START_DISTANCE_FORWARD = 0.73f;
	private static final float START_DISTANCE_BACKWARD = 0.04f;
	
	private boolean searchForward;
	private float distance_forward;
	private float distance_backward;

	private Date lastOutput;
	
	private static ColorSearchState instance;
	
	/*
	 * For the very first backward track, we shift the track a bit, so we have more distance to the hole we are coming from. Hacky.
	 */
	private boolean isFirstBackwardTrack;
	
	boolean redFound;
	boolean whiteFound;

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
		LCD.clear();
	    LCD.drawString("Color search", 0, 0);
	    lastOutput = new Date();
	    
	    isFirstBackwardTrack = true;
	    searchForward = true;
	    distance_forward = START_DISTANCE_FORWARD;
	    distance_backward = START_DISTANCE_BACKWARD;
	    
	    redFound = false;
	    whiteFound = false;
	    
	    SensorController.get().setColorModeToRGB();
	    pmotors.travel(10);
	    MotorController.get().pivotDistanceSensorLeft();
	    SensorController.get().tick();
	}

	@Override
	public void onEnd(boolean modeWillChange) {
	}
	
	@Override
	public void mainloop() {
		checkColor();
		if (checkPush()) { return; }; // if we detected a push and turned around, we return the loop to get new sensor data
		if (redFound && whiteFound)
		{
			motors.stop();
			SoundController.get().playVictoryMusic();
			Executor.get().requestChangeMode(Mode.ModeMenu);
			return;
		}
		controlDistance();
	}
	
	private void controlDistance()
	{
		float distance = SensorController.get().getDistance();
		
		float should;
		if (searchForward)
		{
			should = distance_forward;
		}
		else 
		{
			if (isFirstBackwardTrack)
			{
				should = distance_backward + 0.03f;
			}
			else
			{
				should = distance_backward;
			}
		}
		
		float xd = distance - should;
		
		// translate the difference to the control value y.
		float y = translate(xd);
		
		String searchDirection = "";
		if (xd < -THRESHOLD) {
			searchDirection = "R";
		
			motors.setLeftMotorSpeed(GENERAL_MOTOR_SPEED + Math.abs(y));
			motors.setRightMotorSpeed((GENERAL_MOTOR_SPEED + Math.abs(y)) / 2);
			motors.setMotorDirections(Direction.Forward, Direction.Backward);
		} else if (xd > THRESHOLD) {
			searchDirection = "L";
		
			motors.setLeftMotorSpeed((GENERAL_MOTOR_SPEED + Math.abs(y)) / 2);
			motors.setRightMotorSpeed(GENERAL_MOTOR_SPEED + Math.abs(y));
			motors.setMotorDirections(Direction.Backward, Direction.Forward);
		} else {
			searchDirection = "N";
			
			motors.setLeftMotorSpeed(GENERAL_MOTOR_SPEED);
			motors.setRightMotorSpeed(GENERAL_MOTOR_SPEED);
			motors.setMotorDirections(Direction.Forward, Direction.Forward);
		}
		
		// LOG DEBUG
		Date now = new Date();
		long diff = now.getTime() - lastOutput.getTime();
		if (diff > 250)
		{
			lastOutput = now;
			String searchDirection2;
			if (searchForward)
			{
				searchDirection2 = "F";
			}
			else 
			{
				searchDirection2 = "B";
			}
			System.out.println(searchDirection2 + " " + searchDirection + " " + String.format("%.3f", should) + " | " + String.format("%.3f", distance) + " | " + String.format("%.1f", y));
		}
	}
	
	private boolean checkPush()
	{
		if (SensorController.get().isLeftTouching() && SensorController.get().isRightTouching())
		{
			// set back. -6 works.
			// TODO: maybe decrease distance? but it is already very close on the first backward track!
			pmotors.travel(-5);
			
			if (searchForward)
			{
				distance_forward -= TRACK_DELTA * 2;

				pmotors.rotate(90);
				checkColor();
				pmotors.travel(TRACK_DELTA * 100);
				checkColor();
				pmotors.rotate(90);
			}
			else
			{
				distance_backward += TRACK_DELTA * 2;
				isFirstBackwardTrack = false;
				
				pmotors.rotate(-90);
				checkColor();
				pmotors.travel(TRACK_DELTA * 100);
				checkColor();
				pmotors.rotate(-90);
			}
			
			searchForward = !searchForward;
			return true;
		}
		
		return false;
	}
	
	/*
	 * Returns true if BOTH colors were found, but the result can also be read in the members redFound and whiteFound.
	 * 
	 * Beeps when a color was found.
	 */
	private boolean checkColor()
	{
		float[] color = SensorController.get().getRgbValue();
	
		if (!whiteFound && color[0] > 0.1 && color[1] > 0.1 && color[2] > 0.1)
		{
			whiteFound = true;
			System.out.println("WHITE FOUND");
			SoundController.get().loudBeep();
			if (redFound)
			{
				return true;
			}
		}
		else if (!redFound && color[0] > 0.1)
		{
			redFound = true;
			System.out.println("RED FOUND");
			SoundController.get().loudBeep();
			if (whiteFound)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Method that implements the translation from the difference into the control value.
	 * Description: See exercise sheet 29.
	 * @param xd: Difference between measured light value and should be value
	 * @return translated control value y
	 */
	private float translate(float xd) {
		return xd * 0.5f * K_P_KRIT;
	}
}
