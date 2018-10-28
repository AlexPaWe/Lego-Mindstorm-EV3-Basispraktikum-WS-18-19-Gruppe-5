package de.kit.legomindstorm.gruppe05.robot;

import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.utility.Delay;

/**
 * Encapsulates manually coded motor access.
 *
 */

public class MotorController {
	private static MotorController instance;
	
	private final NXTRegulatedMotor LEFT_MOTOR = Motor.A;
	private final NXTRegulatedMotor RIGHT_MOTOR = Motor.B;
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
	
	public void stop() {
		changeMotorDirections(Direction.Stop, Direction.Stop);
	}
	
	public void forward() {
		changeMotorDirections(Direction.Forward, Direction.Forward);
	}
	
	public void backward() {
		changeMotorDirections(Direction.Backward, Direction.Backward);
	}
	
	public void forwardTimed(float duration) {
		forward();
		wait(duration);
		stop();
	}
	
	public void backwardTimed(float duration) {
		backward();
		wait(duration);
		stop();
	}
	
	public void forwardSpecific(float distance) {
		forwardTimed(distance * MS_FOR_1CM_DRIVE);
	}
	
	public void backwardSpecific(float distance) {
		backwardTimed(distance * MS_FOR_1CM_DRIVE);
	}
	
	public void turnRight() {
		changeMotorDirections(Direction.Forward, Direction.Backward);
	}
	
	public void turnLeft() {
		changeMotorDirections(Direction.Backward, Direction.Forward);
	}
	
	public void turnRightTimed(float duration) {
		turnRight();
		wait(duration);
		stop();
	}
	
	public void turnLeftTimed(float duration) {
		turnLeft();
		wait(duration);
		stop();
	}

	public void turnRightSpecific(float degrees) {
		turnRightTimed(degrees * MS_FOR_1DEG_TURN);
	}
	
	public void turnLeftSpecific(float degrees) {
		turnLeftTimed(degrees * MS_FOR_1DEG_TURN);
	}
	
	private void wait(float time) {
		Delay.msDelay((long) time);
	}
	
	private enum Direction {Stop, Forward, Backward};
	
	private void changeMotorDirections(Direction leftDir, Direction rightDir) {
		LEFT_MOTOR.startSynchronization();
		RIGHT_MOTOR.startSynchronization();

		switch(leftDir) {
			case Stop:
				LEFT_MOTOR.stop();
			case Forward:
				LEFT_MOTOR.forward();
			case Backward:
				LEFT_MOTOR.backward();
		}
		
		switch(rightDir) {
			case Stop:
				RIGHT_MOTOR.stop();
			case Forward:
				RIGHT_MOTOR.forward();
			case Backward:
				RIGHT_MOTOR.backward();
		}
		
		LEFT_MOTOR.endSynchronization();
		RIGHT_MOTOR.endSynchronization();
	}

	public void setMotorSpeed(float leftMotorTargetSpeed, float rightMotorTargetSpeed) {
		LEFT_MOTOR.startSynchronization();
		LEFT_MOTOR.setSpeed(leftMotorTargetSpeed);
		RIGHT_MOTOR.setSpeed(rightMotorTargetSpeed);
		LEFT_MOTOR.endSynchronization();
	}

	public void setLeftMotorSpeed(float targetSpeed) {
		LEFT_MOTOR.setSpeed(targetSpeed);
	}
	
	public void setRightMotorSpeed(float targetSpeed) {
		RIGHT_MOTOR.setSpeed(targetSpeed);
	}

}
