package linefollow;

import java.util.Date;

import execution.Executor;
import execution.Mode;
import execution.State;
import lejos.hardware.lcd.LCD;
import lejos.robotics.Color;
import robot.MotorController;
import robot.SensorController;

public class DriveToBoxPushAreaState extends State {
	
	private static DriveToBoxPushAreaState instance;
	
	private static final int GENERAL_MOTOR_SPEED = 260;
	private static final float K_P_KRIT = 1500f;
	private static final float SHOULD_VALUE = 0.06f; // distance to the wall in m
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
		LCD.clear();
        LCD.drawString("Drive to Box Push Area", 0, 0);
        SensorController.get().setColorModeToColorId();
        MotorController.get().pivotDistanceSensorLeft();
        lastOutput = new Date();
        pmotors.setSpeed(GENERAL_MOTOR_SPEED);
		pmotors.rotate(-90);
	}

	@Override
	public void onEnd(boolean modeWillChange) {
	}

	@Override
	public void mainloop() {
		if (SensorController.get().getColorId() == Color.BLUE) 
		{
			//System.out.println("BOX PUSH");
			
			Executor.get().requestChangeMode(Mode.BoxPush);
			return;
		}
		
		float distance = SensorController.get().getDistance();
		
		float xd = distance - SHOULD_VALUE;
		
		/* calculate turn, based on the sample value */
		float turn = K_P_KRIT * xd; /* only a P-controller will be used. */

		/*
		 * adjust the power of left and right motors in order to make the robot follow
		 * the line
		 */
		float leftTargetSpeed = GENERAL_MOTOR_SPEED - turn;
		float rightTargetSpeed = GENERAL_MOTOR_SPEED + turn;

		/* adjust the robot's movement in order to make the robot follow the line */
		motors.setMotorSpeeds(leftTargetSpeed, rightTargetSpeed);
		
		Date now = new Date();
		long diff = now.getTime() - lastOutput.getTime();
		if (diff > 250)
		{
			lastOutput = now;
			//System.out.println(leftTargetSpeed + " | " + rightTargetSpeed);
			//System.out.println(searchDirection + " | " + distance + " | " + xd + " | " + y);
			//System.out.println(speedR + " " + speedL);
		}
	}
}
