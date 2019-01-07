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
	
	private int touchSensorDetectionCount;
	
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
        
        touchSensorDetectionCount = 0;
        
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
			touchSensorDetectionCount++;
			
			motors.stop();
			pmotors.travel(-6);
			
			if (touchSensorDetectionCount == 1)
			{
				// first try a left turn
				pmotors.rotate(10);
			}
			else
			{
				// if we hit a wall again, try a right turn
				pmotors.rotate(-10);
			}
			
			motors.setMotorDirections(Direction.Forward, Direction.Forward);
		}
	}
}
