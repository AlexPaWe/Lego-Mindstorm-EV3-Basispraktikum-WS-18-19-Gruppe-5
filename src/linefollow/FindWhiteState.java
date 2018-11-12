package linefollow;

import execution.Executor;
import execution.State;
import lejos.hardware.lcd.LCD;
import robot.SensorController;
import robot.MotorController.Direction;

public class FindWhiteState extends State {
	
	private static FindWhiteState instance;
	
	public float leftSpeed = 220;
	public float rightSpeed = 220;

	private FindWhiteState() {
	}

	public static State get() {
		if (instance == null) {
			instance = new FindWhiteState();
		}
		return instance;
	}
	
	@Override
	public void onBegin(boolean modeChanged) {
		LCD.clear();
	    LCD.drawString("Line: Search white", 0, 0);
	    
	    motors.setMotorSpeeds(leftSpeed, rightSpeed);
	    motors.setMotorDirections(Direction.Forward, Direction.Forward);
	}

	@Override
	public void onEnd(boolean modeWillChange) {
	}
	
	@Override
	public void mainloop() {
		if (SensorController.get().getRedValue() > 0.19)
		{
			Executor.get().requestChangeState(LineFollowState.get());
		}
	}
}
