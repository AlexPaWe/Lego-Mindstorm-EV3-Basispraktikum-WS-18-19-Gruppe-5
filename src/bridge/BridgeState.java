package bridge;

import java.util.Date;

import execution.Executor;
import execution.State;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import linefollow.FindWhiteState;
import robot.MotorController;
import robot.SensorController;
import robot.MotorController.Direction;

public class BridgeState extends State {
	
	private static BridgeState instance;

	private static final int GENERAL_MOTOR_SPEED = 220;
	private static final float THRESHOLD = 0.24f; // 1f = 1m, 0.1f = 10cm, 0.01f = 1cm
	private static final float GOAL_HEIGHT_DISTANCE = 0.12f;
	
	private Date lastOutput;
	
	private Date stateStartDate;
	
	private Date rightTurnStarted;
	
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
	    stateStartDate = new Date(9);
	    MotorController.get().pivotDistanceSensorDown();
	    pmotors.travel(10);
	    motors.forward();
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mainloop() {
		float distance = SensorController.get().getDistance();
		
		String searchDirection = "";
		
		Date now = new Date();
		
		// TODO: to make the last 90deg turn, we need some changes
		// e.g. tilt the sensor a bit, so the robot keeps a larger distance to the cliff, but that introduces new problems
		// e.g. to physically offset the distance sensor away from the robot
		// e.g. when turning to the right, always drive for a second, so you increase the distance?
		
		//long timeSinceLastRightTurn = now.getTime() - rightTurnStarted.getTime();
		
		if (distance > THRESHOLD) {
			searchDirection = "R";
			
			//rightTurnStarted = new Date();
			
			motors.setMotorSpeeds(GENERAL_MOTOR_SPEED + 140, GENERAL_MOTOR_SPEED - 140);
		} else {
			searchDirection = "L";
			
			motors.setMotorSpeeds(GENERAL_MOTOR_SPEED - 140, GENERAL_MOTOR_SPEED + 140);
		}
		
		// To avoid hitting the wall at the bottom of the downwards ramp,
		// we check the height, but only after some time after mission start,
		// so we don't do this check for the upwards ramp.
		// If the height is small enough, we don't hug the left cliff,
		// but try to drive straight through the goal.
		long startDiff = now.getTime() - stateStartDate.getTime();
		if (startDiff > 30 * 1000)
		{
			if (distance > GOAL_HEIGHT_DISTANCE)
			{
				motors.stop();
				Delay.msDelay(1000);
				pmotors.rotate(-20);
				Executor.get().requestChangeState(DriveToColorSearchState.get());
			}
		}
		
		// print debug every 250ms
		long debugDiff = now.getTime() - lastOutput.getTime();
		if (debugDiff > 250)
		{
			lastOutput = now;
			System.out.println(searchDirection + " | " + distance);
		}
	}
}
