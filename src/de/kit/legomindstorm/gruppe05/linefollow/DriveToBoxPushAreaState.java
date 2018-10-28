package de.kit.legomindstorm.gruppe05.linefollow;

import de.kit.legomindstorm.gruppe05.execution.Mode;
import de.kit.legomindstorm.gruppe05.execution.State;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

public class DriveToBoxPushAreaState extends State {
	
	private static DriveToBoxPushAreaState instance;

	private DriveToBoxPushAreaState() {
	}

	public static DriveToBoxPushAreaState get() {
		if (instance == null) {
			instance = new DriveToBoxPushAreaState();
		}
		return instance;
	}

	@Override
	public void onBegin(boolean modeChanged) {
		// TODO
		// example stuff:
		
		LCD.clear();
        LCD.drawString("Drive to Box Push Area", 0, 0);
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
		
		if (sensors.getColorId() == lejos.robotics.Color.BLUE) {
			executor.changeMode(Mode.BoxPush);
		}
	}

}
