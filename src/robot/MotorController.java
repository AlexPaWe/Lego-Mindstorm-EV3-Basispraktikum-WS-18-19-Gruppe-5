package robot;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.utility.Delay;

/**
 * Encapsulates manually coded motor access.
 *
 */

public class MotorController {
	private static MotorController instance;
	
	private final NXTRegulatedMotor LEFT_MOTOR = Motor.B;
	private final NXTRegulatedMotor RIGHT_MOTOR = Motor.A;
	private final float MS_FOR_1DEG_TURN = 1500 / 90;
	private final float MS_FOR_1CM_DRIVE = 1500 / 10;
	

	private MotorController() {
	}

	/**
	 * Get the singleton instance.
	 * @return
	 */
	public static MotorController get() {
		if (instance == null) {
			instance = new MotorController();
		}
		return instance;
	}
	
	public void stop()
	{
		setMotorDirections(Direction.Stop, Direction.Stop);
	}
	
	public void forward()
	{
		setMotorDirections(Direction.Forward, Direction.Forward);
	}
	
	public void backward()
	{
		setMotorDirections(Direction.Backward, Direction.Backward);
	}
	
	public void forwardTimed(float duration)
	{
		forward();
		wait(duration);
		stop();
	}
	
	public void backwardTimed(float duration)
	{
		backward();
		wait(duration);
		stop();
	}
	
	public void forwardSpecific(float distance)
	{
		forwardTimed(distance * MS_FOR_1CM_DRIVE);
	}
	
	public void backwardSpecific(float distance)
	{
		backwardTimed(distance * MS_FOR_1CM_DRIVE);
	}
	
	public void turnRight()
	{
		setMotorDirections(Direction.Forward, Direction.Backward);
	}
	
	public void turnLeft()
	{
		setMotorDirections(Direction.Backward, Direction.Forward);
	}
	
	public void turnRightTimed(float duration)
	{
		turnRight();
		wait(duration);
		stop();
	}
	
	public void turnLeftTimed(float duration)
	{
		turnLeft();
		wait(duration);
		stop();
	}

	public void turnRightSpecific(float degrees)
	{
		turnRightTimed(degrees * MS_FOR_1DEG_TURN);
	}
	
	public void turnLeftSpecific(float degrees)
	{
		turnLeftTimed(degrees * MS_FOR_1DEG_TURN);
	}
	
	private void wait(float time)
	{
		Delay.msDelay((long) time);
	}
	
	private enum Direction {Stop, Forward, Backward};
	
	private void setMotorDirections(Direction leftDir, Direction rightDir) {
		//LEFT_MOTOR.startSynchronization();
		//RIGHT_MOTOR.startSynchronization();

		switch(leftDir)
		{
		case Stop:
			LEFT_MOTOR.stop(true);
			break;
		case Forward:
			LEFT_MOTOR.forward();
			break;
		case Backward:
			LEFT_MOTOR.backward();
			break;
		}
		
		switch(rightDir)
		{
		case Stop:
			RIGHT_MOTOR.stop(true);
			break;
		case Forward:
			RIGHT_MOTOR.forward();
			break;
		case Backward:
			RIGHT_MOTOR.backward();
			break;
		}
		
		//LEFT_MOTOR.endSynchronization();
		//RIGHT_MOTOR.endSynchronization();
	}

	/**
	 * Set the speed for both motors. Negative values are backwards motion.
	 * Useful for the P-controller.
	 * @param leftTargetSpeed
	 * @param rightTargetSpeed
	 */
	public void changeMotorSpeeds(float leftTargetSpeed, float rightTargetSpeed) {

		/* print the target speed of left and right motors on the brick's screen */
		LCD.drawString("L= " + leftTargetSpeed, 0, 0);
		LCD.drawString("R= " + rightTargetSpeed, 0, 1);
		
		//LEFT_MOTOR.startSynchronization();
		//RIGHT_MOTOR.startSynchronization();

		if (leftTargetSpeed < 0) {
			LEFT_MOTOR.setSpeed(-leftTargetSpeed);
			LEFT_MOTOR.backward();
		} else {
			LEFT_MOTOR.setSpeed(leftTargetSpeed);
			LEFT_MOTOR.forward();
		}
		if (rightTargetSpeed < 0) {
			RIGHT_MOTOR.setSpeed(-rightTargetSpeed);
			RIGHT_MOTOR.backward();
		} else {
			RIGHT_MOTOR.setSpeed(rightTargetSpeed);
			RIGHT_MOTOR.forward();
		}

		//LEFT_MOTOR.endSynchronization();
		//RIGHT_MOTOR.endSynchronization();
	}

}
