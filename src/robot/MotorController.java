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
	
	public final NXTRegulatedMotor LEFT_MOTOR = Motor.B; // TODO make private
	public final NXTRegulatedMotor RIGHT_MOTOR = Motor.A; // TODO make private
	private final NXTRegulatedMotor SMALL_MOTOR = Motor.C;
	private final float MS_FOR_1DEG_TURN = 1500 / 90;
	private final float MS_FOR_1CM_DRIVE = 1500 / 10;
	public final Pivot START_PIVOT = Pivot.Left;
	
	// if 0, the distance sensor aims straight at the ground (in a 90 degree manor), when pivotted downwards
	// if 45, the down state is 45 degrees to the left
	// problem is e.g.: 15 deg is already A LOT. when approaching the downwards ramp of the bridge,
	// the distance values get pretty huge!
	private final int DOWNPIVOT_LEFT_OFFSET = 5;
	
	public enum Pivot {Right,Left,Down};
	private Pivot distanceSensorPivot;
	
	private MotorController() {
		distanceSensorPivot = START_PIVOT;
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
	
	public enum Direction {Stop, Forward, Backward};
	
	public void setMotorDirections(Direction leftDir, Direction rightDir) {
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
	public void setMotorSpeeds(float leftTargetSpeed, float rightTargetSpeed) {

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

	/**
	 * Negative values dont work here.
	 * @param speed
	 */
	public void setLeftMotorSpeed(float speed)
	{
		LEFT_MOTOR.setSpeed(speed);
	}
	
	/**
	 * Negative values dont work here.
	 * @param speed
	 */
	public void setRightMotorSpeed(float speed)
	{
		RIGHT_MOTOR.setSpeed(speed);
	}
	
	public void setRightMotorDirection(Direction dir)
	{
		switch(dir)
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
	}
	
	public void setLeftMotorDirection(Direction dir)
	{
		switch(dir)
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
	}
	
	public void pivotDistanceSensorDown()
	{
		switch(distanceSensorPivot)
		{
		case Left:
			SMALL_MOTOR.rotate(90 - DOWNPIVOT_LEFT_OFFSET);
			break;
		case Right:
			SMALL_MOTOR.rotate(-90 - DOWNPIVOT_LEFT_OFFSET);
			break;
		case Down:
			break;
		}

		distanceSensorPivot = Pivot.Down;
	}
	
	public void pivotDistanceSensorRight()
	{
		switch(distanceSensorPivot)
		{
		case Left:
			SMALL_MOTOR.rotate(180);
			break;
		case Right:
			break;
		case Down:
			SMALL_MOTOR.rotate(90 + DOWNPIVOT_LEFT_OFFSET);
			break;
		}

		distanceSensorPivot = Pivot.Right;
	}
	
	public void pivotDistanceSensorLeft()
	{
		switch(distanceSensorPivot)
		{
		case Left:
			break;
		case Right:
			SMALL_MOTOR.rotate(-180);
			break;
		case Down:
			SMALL_MOTOR.rotate(-90 + DOWNPIVOT_LEFT_OFFSET);
			break;
		}
		
		distanceSensorPivot = Pivot.Left;
	}
}
