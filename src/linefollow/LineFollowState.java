package linefollow;

import execution.Executor;
import execution.State;
import lejos.hardware.lcd.LCD;

public class LineFollowState extends State {
	
	private static LineFollowState instance;

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
		// TODO
		// example stuff:
		
		/*
		if (sensors.getDistance() < 100) {
			Executor.get().changeState(AvoidObstacleState.get());
		}
		
		if (sensors.getColorId() == lejos.robotics.Color.RED) {
			Executor.get().changeState(DriveToBoxPushAreaState.get());
		}
		*/
		
		motors.forward();
	}
	
}
