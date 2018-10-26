package linefollow;

import execution.State;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

public class AvoidObstacleState extends State {
	
	private static AvoidObstacleState instance;

	private AvoidObstacleState() {
	}

	public static AvoidObstacleState get() {
		if (instance == null) {
			instance = new AvoidObstacleState();
		}
		return instance;
	}

	@Override
	public void onBegin(boolean modeChanged) {
		// TODO
		// example stuff:
				
		
		LCD.clear();
        LCD.drawString("Avoid obstacle", 0, 0);
        Sound.playTone(500, 500);
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tick() {
		// TODO
		// example stuff:
		
		motors.forward();
		
		executor.changeState(LineFollowState.get());
	}

}
