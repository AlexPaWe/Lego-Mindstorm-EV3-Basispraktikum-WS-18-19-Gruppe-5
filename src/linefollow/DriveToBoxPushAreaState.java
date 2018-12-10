package linefollow;

import java.util.Date;

import execution.Executor;
import execution.Mode;
import execution.State;
import lejos.hardware.lcd.LCD;
import lejos.robotics.Color;
import robot.MotorController;
import robot.SensorController;
import robot.MotorController.Direction;

public class DriveToBoxPushAreaState extends State {
	
	private static DriveToBoxPushAreaState instance;
	
	private static final int GENERAL_MOTOR_SPEED = 150; // TODO maybe slower?
	private static final float K_P_KRIT = 2000f;
	private static final float SHOULD_VALUE = 0.05f; // distance to the wall in m
	private static final float THRESHOLD = 0.01f;
	
	private float speedL;
	private float speedR;
	
	private Date lastOutput;

	private DriveToBoxPushAreaState() {
	}

	public static DriveToBoxPushAreaState get() {
		if (instance == null) {
			instance = new DriveToBoxPushAreaState();
		}
		return instance;
	}

	@Override
	public void onBegin(boolean modeChanged) {
		// TODO
		// example stuff:
		
		LCD.clear();
        LCD.drawString("Drive to Box Push Area", 0, 0);
        SensorController.get().setColorModeToColorId();
        MotorController.get().pivotDistanceSensorLeft();
        lastOutput = new Date();
        pmotors.setSpeed(GENERAL_MOTOR_SPEED);
		pmotors.rotate(-80);
		pmotors.travel(15);
		pmotors.rotate(-20);
        motors.setLeftMotorDirection(Direction.Forward);
		motors.setRightMotorDirection(Direction.Forward);
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mainloop() {
		if (SensorController.get().getColorId() == Color.BLUE) 
		{
			System.out.println("BOX PUSH");
			Executor.get().requestChangeMode(Mode.BoxPush);
			return;
		}
		
		float distance = SensorController.get().getDistance();
		if (distance > 10) // infinity = 0
		{
			distance = 0;
		}
		float xd = distance - SHOULD_VALUE;
		
		// translate the difference to the control value y.
		float y = translate(xd);
		
		String searchDirection = "";
		
		if (xd < THRESHOLD) {
			searchDirection = "R";
			
			speedL = GENERAL_MOTOR_SPEED + Math.abs(y);
			speedR = GENERAL_MOTOR_SPEED - Math.abs(y);
		} else if (xd > THRESHOLD) {
			searchDirection = "L";
			
			speedR = GENERAL_MOTOR_SPEED + Math.abs(y);
			speedL = GENERAL_MOTOR_SPEED - Math.abs(y);
		} else {
			searchDirection = "N";
			
			speedL = GENERAL_MOTOR_SPEED;
			speedR = GENERAL_MOTOR_SPEED;
		}
		
		motors.setLeftMotorSpeed(speedL);
		motors.setRightMotorSpeed(speedR);
		motors.setLeftMotorDirection(Direction.Forward);
		motors.setRightMotorDirection(Direction.Forward);
		
		Date now = new Date();
		long diff = now.getTime() - lastOutput.getTime();
		if (diff > 250)
		{
			lastOutput = now;
			System.out.println(searchDirection + " | " + distance + " | " + xd + " | " + y);
			//System.out.println(speedR + " " + speedL);
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
