package bridge;

import execution.Executor;
import execution.Mode;
import execution.State;
import lejos.hardware.lcd.LCD;
import lejos.robotics.Color;
import robot.SensorController;
import robot.MotorController.Direction;

public class DriveToColorSearchState extends State {
	
	private static DriveToColorSearchState instance;

	private final static float SPEED = 180;
	
	private DriveToColorSearchState() {
	}

	public static DriveToColorSearchState get() {
		if (instance == null) {
			instance = new DriveToColorSearchState();
		}
		return instance;
	}

	@Override
	public void onBegin(boolean modeChanged) {
		LCD.clear();
        LCD.drawString("Drive to Color Search", 0, 0);
        SensorController.get().setColorModeToColorId();
        motors.setMotorSpeeds(SPEED, SPEED);
	    motors.setMotorDirections(Direction.Forward, Direction.Forward);
	}

	@Override
	public void onEnd(boolean modeWillChange) {
	}

	@Override
	public void mainloop() {
		if (SensorController.get().getColorId() == Color.BLUE)
		{
			Executor.get().requestChangeMode(Mode.ColorSearch);
			return;
		}
		
		if (SensorController.get().isLeftTouching() || SensorController.get().isRightTouching())
		{
			motors.stop();
			pmotors.travel(-6);
			pmotors.rotate(10);
			motors.setMotorDirections(Direction.Forward, Direction.Forward);
		}
	}
}
