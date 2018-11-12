package linefollow;

import java.util.Date;

import execution.Executor;
import execution.State;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import robot.SensorController;
import robot.MotorController.Direction;

public class LineFollowState extends State {
	
	private static LineFollowState instance;
	
	private static final int GENERAL_MOTOR_SPEED = 220; // TODO maybe slower?
	private static final float K_P_KRIT = 1000f;
	private static final float SHOULD_VALUE = 0.19f;
	private static final float THRESHOLD = 30f;
	
	private float speedL;
	private float speedR;
	
	private Date lastOutput;

	private LineFollowState() {
	}

	public static State get() {
		if (instance == null) {
			instance = new LineFollowState();
		}
		return instance;
	}
	
	@Override
	public void onBegin(boolean modeChanged) {
		// TODO
		// example stuff:
		
		LCD.clear();
	    LCD.drawString("Line: P controller", 0, 0);
	    lastOutput = new Date();
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mainloop() {
		if (SensorController.get().isRightTouching() && SensorController.get().isLeftTouching()) 
		{
			Executor.get().requestChangeState(AvoidObstacleState.get());
			return;
		}
		
		// compute the difference between measured light value and should be value.
		float xd = SensorController.get().getRedValue() - SHOULD_VALUE;
		
		// translate the difference to the control value y.
		float y = translate(xd);
		
		String searchDirection = "";
		
		// TODO: Replace operators fittingly to direction change needed.
		if (y < (-1 * THRESHOLD)) {
			searchDirection = "L";
			
			speedR = GENERAL_MOTOR_SPEED + Math.abs(y);
			speedR = 0.5f * speedR;
			//motorL.backward();
			motors.setLeftMotorDirection(Direction.Backward);
			speedL  = speedR;
			//motorR.forward();
			motors.setRightMotorDirection(Direction.Forward);
			
			if (motors.LEFT_MOTOR.getTachoCount() < -400)
			{
				navigateOverGap();
				FindWhiteState state = (FindWhiteState)FindWhiteState.get();
				state.leftSpeed = 220;
				state.rightSpeed = 220;
				Executor.get().requestChangeState(FindWhiteState.get());
				return;
			}
		} else if (y > THRESHOLD) {
			searchDirection = "R";
			
			speedL = GENERAL_MOTOR_SPEED + Math.abs(y);
			speedL = 0.5f * speedL;
			//motorL.forward();
			motors.setLeftMotorDirection(Direction.Forward);
			speedR = speedL;
			//motorR.backward();
			motors.setRightMotorDirection(Direction.Backward);
		} else {
			motors.LEFT_MOTOR.resetTachoCount();
			searchDirection = "N";
			
			speedL = GENERAL_MOTOR_SPEED;
			speedR = GENERAL_MOTOR_SPEED;
			//motorL.forward();
			motors.setLeftMotorDirection(Direction.Forward);
			//motorR.forward();
			motors.setRightMotorDirection(Direction.Forward);
		}
		
		motors.setLeftMotorSpeed(speedL);
		motors.setRightMotorSpeed(speedR);
		
		Date now = new Date();
		long diff = now.getTime() - lastOutput.getTime();
		if (diff > 250)
		{
			lastOutput = now;
			System.out.println(searchDirection + ": " + motors.LEFT_MOTOR.getTachoCount());
		}
	}
	
	private void navigateOverGap()
	{
		motors.setMotorDirections(Direction.Stop, Direction.Stop);
		pmotors.setSpeed(220);
		pmotors.rotate(-120);
		pmotors.travel(20);
		SensorController.get().tick();
		if (SensorController.get().getRedValue() > 0.19)
		{
	    	// already on white
	    	// might happen if the robot did not stand straight
		}
	    else
	    {
	    	pmotors.rotate(70);
	    }
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
