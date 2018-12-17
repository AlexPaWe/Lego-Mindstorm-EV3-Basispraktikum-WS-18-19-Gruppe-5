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
	private static final float DISTANCE_MAX = 0.5f;
	
	private static int timesGapsSeen = 0;
	
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
	    timesGapsSeen = 0;
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mainloop() {
		float distance = SensorController.get().getDistance();
		
		Date now = new Date();
		long debugDiff = now.getTime() - lastOutput.getTime();
		if (debugDiff >= 100) // print every 250ms
		{
			lastOutput = now;
			System.out.println("g: " + timesGapsSeen + " |  d: " + distance);
		}
		
		
		if (timesGapsSeen == 0)
		{
			if (distance > DISTANCE_MIN && distance < DISTANCE_MAX)
			{
				++timesGapsSeen;
			}
			else
			{
				pmotors.rotate(-2);
				Delay.msDelay(100);
			}
		}
		else
		{
			if (distance > DISTANCE_MIN && distance < DISTANCE_MAX)
			{
				++timesGapsSeen;
				pmotors.rotate(-2);
				Delay.msDelay(100);
			}
			else
			{
				MotorController.get().pivotDistanceSensorPark();
				pmotors.travel(5);
				pmotors.rotate(30 + (timesGapsSeen * 2));
				Executor.get().requestChangeState(DriveToColorSearchState.get());
			}
		}
	}
}
