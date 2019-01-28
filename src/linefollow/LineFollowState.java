package linefollow;

import java.util.Date;

import execution.Executor;
import execution.State;
import lejos.hardware.lcd.LCD;
import robot.SensorController;
import robot.MotorController.Direction;

public class LineFollowState extends State {
	
	private static LineFollowState instance;
	
	private static final int GENERAL_MOTOR_SPEED = 200; // 220 worked most of the time // 180 safer
	private static final float K_P_KRIT = 2750f; // 3000 worked most of the time // 2500 safer
	private static final float SHOULD_VALUE = 0.19f;
	private static final float THRESHOLD = 0.1f;
	
	private Date lastOutput;
	
	private Date rightTouched;
	
	private boolean gapsNavigated;

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
		LCD.clear();
	    LCD.drawString("Line: P controller", 0, 0);
	    lastOutput = new Date();
	    SensorController.get().setColorModeToRed();
	    motors.LEFT_MOTOR.resetTachoCount();
	    rightTouched = null;
	    
	    if (modeChanged)
	    {
	    	gapsNavigated = false;
	    }
	}

	@Override
	public void onEnd(boolean modeWillChange) {
	}
	
	@Override
	public void mainloop() {
		if (rightTouched == null)
		{
			if (SensorController.get().isRightTouching())
			{
				rightTouched = new Date();
			}
			else
			{
			}
		}
		else
		{
			if (SensorController.get().isRightTouching())
			{
				Date now = new Date();
				
				long diff = now.getTime() - rightTouched.getTime();
				System.out.print("diff: " + diff);
				if (diff > 2000)
				{
					gapsNavigated = true;
					Executor.get().requestChangeState(AvoidObstacleState.get());
					return;
				}
			}
			else
			{
				rightTouched = null;
			}
		}
		
		// compute the difference between measured light value and should be value.
		float xd = SensorController.get().getRedValue() - SHOULD_VALUE;
		
		// translate the difference to the control value y.
		//float y = translate(xd);
		
		@SuppressWarnings("unused")
		String searchDirection = "";
		
		/* calculate turn, based on the sample value */
		float turn = K_P_KRIT * xd; /* only a P-controller will be used. */

		/*
		 * adjust the power of left and right motors in order to make the robot follow
		 * the line
		 */
		float leftTargetSpeed = GENERAL_MOTOR_SPEED + turn;
		float rightTargetSpeed = GENERAL_MOTOR_SPEED - turn;
		
		float overflow = leftTargetSpeed - 360;
		if (overflow > 0)
		{
			rightTargetSpeed -= overflow;
		}
		
		/* adjust the robot's movement in order to make the robot follow the line */
		motors.setMotorSpeeds(leftTargetSpeed, rightTargetSpeed);

		if (xd < -1 * THRESHOLD) {
			searchDirection = "L";
			
			if (motors.LEFT_MOTOR.getTachoCount() < -240) // -400
			{
				navigateOverGap();
				return;
			}
		} else if (xd > THRESHOLD) {
			searchDirection = "R";
		} else {
			motors.LEFT_MOTOR.resetTachoCount();
			searchDirection = "N";
		}
		
		Date now = new Date();
		long diff = now.getTime() - lastOutput.getTime();
		if (diff > 250)
		{
			lastOutput = now;
			//System.out.println(searchDirection + " | " + motors.LEFT_MOTOR.getTachoCount());
		}
	}
	
	private void navigateOverGap()
	{	
		motors.setMotorDirections(Direction.Stop, Direction.Stop);
		
		if (gapsNavigated) {
			Executor.get().requestChangeState(DriveToBoxPushAreaState.get());
		}
		else {
			pmotors.setSpeed(220);
			pmotors.rotate(-110);
			pmotors.travel(20);
			SensorController.get().tick();
			if (SensorController.get().getRedValue() > 0.19)
			{
		    	// already on white
		    	// might happen if the robot did not stand straight
			}
		    else
		    {
		    	pmotors.rotate(60);
		    }	
			FindWhiteState state = (FindWhiteState)FindWhiteState.get();
			state.leftSpeed = 220;
			state.rightSpeed = 220;
			Executor.get().requestChangeState(FindWhiteState.get());
		}
	}
}
