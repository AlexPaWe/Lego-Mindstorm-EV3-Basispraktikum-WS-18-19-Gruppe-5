package linefollow;

import execution.Executor;
import execution.State;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import robot.MotorController.Direction;

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
		LCD.clear();
        LCD.drawString("Line: Avoid obstacle", 0, 0);
        Sound.playTone(500, 500);
        motors.setMotorDirections(Direction.Stop, Direction.Stop);
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mainloop() {
		// TODO
		// example stuff:
		
		pmotors.setSpeed(360);
		pmotors.travel(-10);
		pmotors.rotate(-90);
		pmotors.travel(20);
		pmotors.rotate(90);
		pmotors.travel(40);
		
		pmotors.rotate(60);
		
		FindWhiteState state = (FindWhiteState)FindWhiteState.get();
		state.leftSpeed = 110;
		state.rightSpeed = 220;
		Executor.get().requestChangeState(FindWhiteState.get());
		
		//motors.forward();
		
		//Executor.get().changeState(LineFollowState.get());
	}

}
