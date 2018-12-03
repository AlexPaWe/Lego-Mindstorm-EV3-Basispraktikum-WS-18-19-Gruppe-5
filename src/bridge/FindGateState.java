package bridge;

import java.util.Date;

import execution.Executor;
import execution.State;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import linefollow.FindWhiteState;
import robot.MotorController;
import robot.SensorController;
import robot.MotorController.Direction;

public class FindGateState extends State {
	
	private static FindGateState instance;
		
	private static final float DISTANCE_MIN = 0.25f;
	private static final float DISTANCE_MAX = 0.4f;
	
	private Date lastOutput;
	
	private FindGateState() {
	}

	public static State get() {
		if (instance == null) {
			instance = new FindGateState();
		}
		return instance;
	}
	
	@Override
	public void onBegin(boolean modeChanged) {
		LCD.clear();
	    LCD.drawString("Bridge: Find Gate", 0, 0);
	    lastOutput = new Date();
	    MotorController.get().pivotDistanceSensorLeft();
	    motors.stop();
	    Delay.msDelay(2000);
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mainloop() {
		float distance = SensorController.get().getDistance();
		
		//if (false)
		if (distance > DISTANCE_MIN && distance < DISTANCE_MAX)
		{
			pmotors.travel(5);
			pmotors.rotate(35);
			Executor.get().requestChangeState(DriveToColorSearchState.get());
		}
		else
		{
			pmotors.rotate(-5);
			Delay.msDelay(250);
		}
		
		Date now = new Date();
		long debugDiff = now.getTime() - lastOutput.getTime();
		if (debugDiff >= 250) // print every 250ms
		{
			lastOutput = now;
			System.out.println(distance);
		}
	}
}
