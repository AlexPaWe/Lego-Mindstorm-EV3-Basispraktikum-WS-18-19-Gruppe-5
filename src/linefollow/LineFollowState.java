package linefollow;

import execution.State;
import lejos.hardware.lcd.LCD;
import robot.SensorController;
import robot.MotorController.Direction;

public class LineFollowState extends State {
	
	private static LineFollowState instance;
	
	private static final int GENERAL_MOTOR_SPEED = 360; // TODO maybe slower?
	private static final float K_P_KRIT = 1000f;
	private static final float SHOULD_VALUE = 0.19f;
	private static final float THRESHOLD = 30f;
	
	private float speedL;
	private float speedR;

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
	    LCD.drawString("Line Follow", 0, 0);
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mainloop() {
		// compute the difference between measured light value and should be value.
		float xd = SensorController.get().getRedValue() - SHOULD_VALUE;
		
		// translate the difference to the control value y.
		float y = translate(xd);
		
		// TODO: Only for tests: Show control values
		System.out.println(xd);
		
		// TODO: Replace operators fittingly to direction change needed.
		if (y < (-1 * THRESHOLD)) {
			speedR = GENERAL_MOTOR_SPEED + Math.abs(y);
			speedR = 0.5f * speedR;
			//motorL.backward();
			motors.setLeftMotorDirection(Direction.Backward);
			speedL  = speedR;
			//motorR.forward();
			motors.setRightMotorDirection(Direction.Forward);
		} else if (y > THRESHOLD) {
			speedL = GENERAL_MOTOR_SPEED + Math.abs(y);
			speedL = 0.5f * speedL;
			//motorL.forward();
			motors.setLeftMotorDirection(Direction.Forward);
			speedR = speedL;
			//motorR.backward();
			motors.setRightMotorDirection(Direction.Backward);
		} else {
			speedL = GENERAL_MOTOR_SPEED;
			speedR = GENERAL_MOTOR_SPEED;
			//motorL.forward();
			motors.setLeftMotorDirection(Direction.Forward);
			//motorR.forward();
			motors.setRightMotorDirection(Direction.Forward);
		}
		
		motors.setLeftMotorSpeed(speedL);
		motors.setRightMotorSpeed(speedR);
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
