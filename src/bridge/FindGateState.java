package bridge;

import java.util.Date;

import execution.Executor;
import execution.State;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import robot.MotorController;
import robot.SensorController;

public class FindGateState extends State {
	
	private static FindGateState instance;
	
	private static final float GROUND_DISTANCE_THRESHOLD = 0.09f;
	
	/* Distance that is read, when the sensor looks inside the color search area.
	 * 
	 * The typical value is 0.33, but sometimes it is around 0.44.
	 * Around 0.5 is a difficult case, where it could be both a false or a correct read.
	 */
	private static final float DISTANCE_MIN = 0.25f;
	private static final float DISTANCE_MAX = 0.5f;
	
	private Date lastOutput;
	
	private boolean correctedPosition;
	
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
	    correctedPosition = false;
	}

	@Override
	public void onEnd(boolean modeWillChange) {
	}
	
	@Override
	public void mainloop() {
		float distance = SensorController.get().getDistance();
		
		logDebug(distance);
		
		if (!correctedPosition)
		{
			if (distance > GROUND_DISTANCE_THRESHOLD)
			{
				pmotors.rotate(-2);
				Delay.msDelay(25);
			}
			else
			{
				MotorController.get().pivotDistanceSensorLeft();
			    Delay.msDelay(100);
			    // decrease time by skipping first few angles
			    // TODO: test safe values
			    pmotors.rotate(-20);
			    correctedPosition = true;
			    System.out.println("POSITION CORRECTED");
			}
		}
		else
		{
			if (distance > DISTANCE_MIN && distance < DISTANCE_MAX)
			{
				System.out.println("GAP FOUND");
				System.out.println(" d: " + distance);
				MotorController.get().pivotDistanceSensorPark();
				pmotors.travel(3);
				pmotors.rotate(45);
				Executor.get().requestChangeState(DriveToColorSearchState.get());
			}
			else
			{
				pmotors.rotate(-2);
				Delay.msDelay(25);
			}
		}
	}
	
	private void logDebug(float distance)
	{
		Date now = new Date();
		long debugDiff = now.getTime() - lastOutput.getTime();
		if (debugDiff >= 100) // print every 100ms
		{
			lastOutput = now;
			System.out.println("d: " + distance);
		}
	}
}
