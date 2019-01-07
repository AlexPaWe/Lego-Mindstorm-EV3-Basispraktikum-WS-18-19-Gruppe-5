package robot;

import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.utility.Delay;

/**
 * Encapsulates motor access with the DifferentialPilot class.
 *
 */

public class PilotedMotorController {
	private static PilotedMotorController instance;
	
	private final NXTRegulatedMotor LEFT_MOTOR = Motor.B;
	private final NXTRegulatedMotor RIGHT_MOTOR = Motor.A;
	private final float WHEEL_DIAMETER = 3.33f; //urspr�nglich 3f, jetzt aber abgemessen; 3.28? a bit too small?
	private final float TRACK_WIDTH = 19f;	// urspr�nglich 12f, jetzt aber abgemessen;
	
	private DifferentialPilot pilot;

	private PilotedMotorController() {
		pilot = new DifferentialPilot(WHEEL_DIAMETER, TRACK_WIDTH, LEFT_MOTOR, RIGHT_MOTOR);
		//pilot.setAcceleration(60);
		pilot.setRotateSpeed(60);
	}

	/**
	 * Get the singleton instance.
	 * @return
	 */
	public static PilotedMotorController get() {
		if (instance == null) {
			instance = new PilotedMotorController();
		}
		return instance;
	}

	public void setSpeed(double speed) {
		this.pilot.setTravelSpeed((speed / 360) * this.pilot.getMaxTravelSpeed() );
	}

	public void goForward() {
		this.pilot.forward();
	}

	public void goBackward() {
		this.pilot.backward();
	}
	
	public void travel(double distance) {
		this.pilot.travel(distance);
	}

	public boolean isMoving() {
		return this.pilot.isMoving();
	}

	public void stopWithPilot() {
		this.pilot.stop();
	}

	public void quickStop() {
		this.pilot.quickStop();
	}

	public void rotate(double angle) {
		this.pilot.quickStop();
		Delay.msDelay(10);
		this.pilot.rotate(angle, false);
	}
	
	public void turnLeft(double angle) throws IllegalArgumentException {
		if (angle < 0 || angle > 180) {
			throw new IllegalArgumentException();
		}
		this.rotate(angle);
	}
	
	public void turnRight(double angle) throws IllegalArgumentException {
		if (angle < 0 || angle > 180) {
			throw new IllegalArgumentException();
		}
		this.rotate(-angle);
	}
}
