package bridge;

import java.util.Date;

import execution.Executor;
import execution.State;
import lejos.hardware.lcd.LCD;
import linefollow.FindWhiteState;
import robot.MotorController;
import robot.SensorController;
import robot.MotorController.Direction;

public class BridgeState extends State {
	
	private static BridgeState instance;

	private static final int GENERAL_MOTOR_SPEED = 220; // TODO maybe slower?
	private static final float THRESHOLD = 0.1f; // 1f = 1m, 0.1f = 10cm, 0.01f = 1cm
	
	private Date lastOutput;
	
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
	    MotorController.get().pivotDistanceSensorDown();
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
		
		if (distance > THRESHOLD) {
			searchDirection = "R";
			
			motors.setMotorSpeeds(100, 300);
		} else {
			searchDirection = "L";
			
			motors.setMotorSpeeds(300, 100);
		}
		
		// print debug every 250ms
		Date now = new Date();
		long diff = now.getTime() - lastOutput.getTime();
		if (diff > 250)
		{
			lastOutput = now;
			System.out.println(searchDirection + " | " + distance);
		}
	}
}
