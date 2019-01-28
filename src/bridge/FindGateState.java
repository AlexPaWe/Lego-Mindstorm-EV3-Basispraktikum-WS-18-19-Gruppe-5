package bridge;

import java.util.Date;

import execution.Executor;
import execution.State;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import robot.MotorController;
import robot.SensorController;
import robot.SoundController;

public class FindGateState extends State {
	
	private static FindGateState instance;
	
	/*
	 * Smaller than this distance counts as ground.
	 */
	private static final float GROUND_DISTANCE_THRESHOLD = 0.05f;
	
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
	}

	@Override
	public void onEnd(boolean modeWillChange) {
	}
	
	@Override
	public void mainloop() {
		float distance = SensorController.get().getDistance();
		
		logDebug(distance);
		
		if (distance > GROUND_DISTANCE_THRESHOLD)
		{
			pmotors.rotate(-2);
			Delay.msDelay(25);
		}
		else
		{
			//System.out.println("-d: " + distance);
		    Delay.msDelay(100);
		    //System.out.println("POSITION CORRECTED");
		    SoundController.get().beep();
		    
		    MotorController.get().pivotDistanceSensorPark();
		    // TODO could this be useful?
		    //pmotors.travel(10);
		    //pmotors.rotate(5);
		    
		    Executor.get().requestChangeState(DriveToColorSearchState.get());
		}
	}
	
	private void logDebug(float distance)
	{
		Date now = new Date();
		long debugDiff = now.getTime() - lastOutput.getTime();
		if (debugDiff >= 100) // print every 100ms
		{
			lastOutput = now;
			//System.out.println("d: " + distance);
		}
	}
}
