package bridge;

import java.util.Date;

import execution.Executor;
import execution.State;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import robot.MotorController;
import robot.SensorController;

public class BridgeState extends State {
	
	private static BridgeState instance;

	private static final int GENERAL_MOTOR_SPEED = 360;
	
	/*
	 * If the distance sensor value is below this, it counts as ground.
	 * Higher values count as cliff.
	 * The distance from the sensor to the ground needs some tolerance,
	 * especially for the transition from ramps to straight grounds.
	 * The low cliff is 
	 * The ground is 4-8cm. (the annoying wood in the corner is 12.5cm)
	 * The cliff is 13-30 cm.
	 * 
	 * 1f = 1m, 0.1f = 10cm, 0.01f = 1cm
	 */
	private static final float GROUND_DISTANCE_THRESHOLD_PEACE_TIME = 0.09f;
	private static final float GROUND_DISTANCE_THRESHOLD = 0.14f;
	
	/*
	 * Time to ignore the win condition. Used that we don't check the
	 * win condition during upwards ramp.
	 * 
	 * In seconds!
	 */
	private static final long PEACE_TIME_DURATION = 10; // TODO
	private static final float PEACE_TIME_THRESHOLD = 0.2f;
	private boolean peaceTime;
	
	/*
	 * When the distance sensor value is higher than the GROUND_DISTANCE_THRESHOLD,
	 * but lower than this, it detects the goal.
	 */
	private static final float GOAL_HEIGHT_DISTANCE_MIN = 0.14f;
	private static final float GOAL_HEIGHT_DISTANCE_MAX = 0.16f;
	
	private Date lastOutput;
	
	private Date stateStartDate;
	
	private BridgeState() {
	}

	public static State get() {
		if (instance == null) {
			instance = new BridgeState();
		}
		return instance;
	}
	
	@Override
	public void onBegin(boolean modeChanged) {
		// TODO
		// example stuff:
		
		LCD.clear();
	    LCD.drawString("Bridge", 0, 0);
	    lastOutput = new Date();
	    stateStartDate = new Date();
	    pmotors.travel(30);
	    MotorController.get().pivotDistanceSensorDown();
	    Delay.msDelay(250);
	    motors.forward();
	    peaceTime = true;
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// TODO Auto-generated method stub
		
	}
	

	// TODO: to make the last 90deg turn, we need some changes
	// e.g. tilt the sensor a bit, so the robot keeps a larger distance to the cliff, but that introduces new problems
	// e.g. to physically offset the distance sensor away from the robot
	// e.g. when turning to the right, always drive for a second, so you increase the distance?
	@Override
	public void mainloop() {
		float distance = SensorController.get().getDistance();
		
		if (distance > 10) {distance=0;}
		
		String searchDirection = "";
		
		float distanceThreshold;
		if (peaceTime)
		{
			distanceThreshold = GROUND_DISTANCE_THRESHOLD_PEACE_TIME;
			//Date now = new Date();
			//long startDiff = now.getTime() - stateStartDate.getTime();
			//if (startDiff > PEACE_TIME_DURATION * 1000)
			if (distance > PEACE_TIME_THRESHOLD)
			{
				peaceTime = false;
				System.out.println("PEACE TIME OVER");
			}
		}
		else {
			distanceThreshold = GROUND_DISTANCE_THRESHOLD;
			checkForGoal(distance);
		}
		
		if (distance > distanceThreshold) {
			searchDirection = "R";
			
			motors.setMotorSpeeds(GENERAL_MOTOR_SPEED, GENERAL_MOTOR_SPEED * 0.25f);
		} else {
			searchDirection = "L";
			
			motors.setMotorSpeeds(GENERAL_MOTOR_SPEED * 0.25f, GENERAL_MOTOR_SPEED);
		}
		
		logDebug(searchDirection, distance);
	}
	
	private void checkForGoal(float distance)
	{
		if (distance > GOAL_HEIGHT_DISTANCE_MIN && distance < GOAL_HEIGHT_DISTANCE_MAX)
		{
			System.out.println("GOAL FOUND");
			motors.stop();
			Delay.msDelay(1000);
			Executor.get().requestChangeState(FindGateState.get());
		}
	}
	
	private void logDebug(String searchDirection, float distance)
	{
		Date now = new Date();
		
		long debugDiff = now.getTime() - lastOutput.getTime();
		if (debugDiff > 250) // print every 250ms
		{
			lastOutput = now;
			System.out.println(searchDirection + " | " + distance);
		}
	}
}
