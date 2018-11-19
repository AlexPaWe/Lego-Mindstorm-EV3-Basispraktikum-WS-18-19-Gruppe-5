package modemenu;

import java.util.ArrayList;

import execution.Executor;
import execution.Mode;
import execution.State;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import robot.MotorController;

public class ModeMenuState extends State {
    private static ModeMenuState instance = null;
    
    private int selectedState;
    private ArrayList<Mode> menuEntries;

    private ModeMenuState() {
    	selectedState = 0;
    	menuEntries = new ArrayList<Mode>();
    	
    	// we want to display all modes, except the mode menu itself
    	for (Mode mode : Mode.values())
    	{
    		if (mode != Mode.ModeMenu)
    		{
    			menuEntries.add(mode);
    		}
    	}
    }

    public static ModeMenuState get() {
        if (instance == null) {
            instance = new ModeMenuState();
        }
        return instance;
    }

	@Override
	public void onBegin(boolean modeChanged) {
		motors.stop();
		switch(MotorController.get().START_PIVOT)
		{
		case Left:
			motors.pivotDistanceSensorLeft();
			break;
		case Right:
			motors.pivotDistanceSensorRight();
			break;
		case Down:
			motors.pivotDistanceSensorDown();
			break;
		}
		redraw();
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		LCD.clear();
	}

	@Override
	public void mainloop() {
		if (buttons.isKeyPressedAndReleased(Button.UP)) {
            selectedState = (selectedState - 1 + menuEntries.size()) % menuEntries.size();
            redraw();
        } else if (buttons.isKeyPressedAndReleased(Button.DOWN)) {
            selectedState = (selectedState + 1) % menuEntries.size();
            redraw();
        } else if (buttons.isKeyPressedAndReleased(Button.ENTER)) {
        	Executor.get().requestChangeMode(menuEntries.get(selectedState));
        } else if (buttons.isKeyPressedAndReleased(Button.ESCAPE)) {
            System.exit(0);
        }
	}
	
    private void redraw() {
        LCD.clear();
        LCD.drawString("Mode Menu", 0, 0);

        for (int i = 0; i < menuEntries.size(); i++) {
            if (selectedState == i) {
                LCD.drawString(">" + menuEntries.get(i).getName(), 0, i + 3);
            } else {
                LCD.drawString(" " + menuEntries.get(i).getName(), 0, i + 3);
            }
        }
    }
}