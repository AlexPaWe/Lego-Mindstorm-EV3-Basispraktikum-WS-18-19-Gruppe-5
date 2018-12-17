package colorsearch;

import java.util.Date;

import execution.Executor;
import execution.Mode;
import execution.State;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.robotics.Color;
import robot.MotorController;
import robot.SensorController;
import robot.MotorController.Direction;

public class ColorSearchState extends State {
	
	private static final float GENERAL_MOTOR_SPEED = 180f;
	private static final float K_P_KRIT = 1000f;
	/*
	 * 0.01f is a good value for distance controlling
	 * 100000f basically disables controlling and just drives forward
	 */
	private static final float THRESHOLD = 10000f; // TODO
	
	private static final float TRACK_DELTA = 0.1f;
	private static final float START_DISTANCE_FORWARD = 0.73f;
	private static final float START_DISTANCE_BACKWARD = 0.07f;
	
	private boolean searchForward;
	private float distance_forward;
	private float distance_backward;

	private Date lastOutput;
	
	private static ColorSearchState instance;
	
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
		LCD.clear();
	    LCD.drawString("Color search", 0, 0);
	    lastOutput = new Date();
	    
	    searchForward = true;
	    distance_forward = START_DISTANCE_FORWARD;
	    distance_backward = START_DISTANCE_BACKWARD;
	    
	    SensorController.get().setColorModeToColorId();
	    pmotors.travel(10);
	    MotorController.get().pivotDistanceSensorLeft();
	}

	@Override
	public void onEnd(boolean modeWillChange) {
	}
	
	@Override
	public void mainloop() {
		if (checkColor()) { Executor.get().requestChangeMode(Mode.ModeMenu); }; // if we found both colors, end
		if (checkPush()) { return; }; // if we detected a push and turned around, we return the loop to get new sensor data
		controlDistance();
	}
	
	private void controlDistance()
	{
		float distance = SensorController.get().getDistance();
		if (distance > 10) // infinity = 0
		{
			distance = 0;
		}
		
		float should;
		if (searchForward)
		{
			should = distance_forward;
		}
		else 
		{
			should = distance_backward;
		}
		
		float xd = distance - should;
		
		// translate the difference to the control value y.
		float y = translate(xd);
		
		String searchDirection = "";
		if (xd < -THRESHOLD) {
			searchDirection = "R";
		
			motors.setMotorSpeeds(GENERAL_MOTOR_SPEED + Math.abs(y), (GENERAL_MOTOR_SPEED + Math.abs(y)) / 2);
			motors.setMotorDirections(Direction.Forward, Direction.Backward);
		} else if (xd > THRESHOLD) {
			searchDirection = "L";
		
			motors.setMotorSpeeds((GENERAL_MOTOR_SPEED + Math.abs(y)) / 2, GENERAL_MOTOR_SPEED + Math.abs(y));
			motors.setMotorDirections(Direction.Backward, Direction.Forward);
		} else {
			searchDirection = "N";
			
			motors.setMotorSpeeds(GENERAL_MOTOR_SPEED, GENERAL_MOTOR_SPEED);
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
			System.out.println(searchDirection2 + " " + searchDirection + " " + String.format("%.3f", should) + " | " + String.format("%.3f", distance));
		}
	}
	
	private boolean checkPush()
	{
		if (SensorController.get().isLeftTouching() && SensorController.get().isRightTouching())
		{
			pmotors.travel(-6);
			
			if (searchForward)
			{
				distance_forward -= TRACK_DELTA * 2;

				pmotors.rotate(90);
				pmotors.travel(TRACK_DELTA * 100);
				pmotors.rotate(90);
			}
			else
			{
				distance_backward += TRACK_DELTA * 2;
				
				pmotors.rotate(-90);
				pmotors.travel(TRACK_DELTA * 100);
				pmotors.rotate(-90);
			}
			
			searchForward = !searchForward;
			return true;
		}
		
		return false;
	}
	
	/*
	 * Returns true if BOTH colors were found.
	 * 
	 * Beeps when a color was found.
	 */
	private boolean checkColor()
	{
		if (!redFound && SensorController.get().getColorId() == Color.RED)
		{
			redFound = true;
			System.out.println("RED FOUND");
			if (whiteFound)
			{
				Sound.beep();
				return true;
			}
			else
			{
				Sound.beep();
			}
		}
		else if (!whiteFound && SensorController.get().getColorId() == Color.WHITE)
		{
			whiteFound = true;
			System.out.println("WHITE FOUND");
			if (redFound)
			{
				Sound.beep();
				return true;
			}
			else
			{
				Sound.beep();
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
