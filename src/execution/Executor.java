package execution;

import lejos.hardware.Button;
import linefollow.LineFollowState;
import modemenu.ModeMenuState;
import robot.ButtonController;
import robot.SensorController;

/**
 * Executes the main loop, which updates the sensor values and runs logic depending on its state.
 *
 */
public class Executor {
	private static final Mode START_MODE = Mode.ModeMenu;
	
	private Mode mode;
	private State state;
	
	private static Executor instance;

	/**
	 * Get the singleton instance.
	 * @return
	 */
	public static Executor get() {
		if (instance == null) {
			instance = new Executor();
		}
		return instance;
	}
	
	private Executor()
	{
		mode = null;
		state = null;
		changeMode(START_MODE);
	}

	public void mainloop() {
		if (ButtonController.get().isKeyPressedAndReleased(Button.ESCAPE))
		{
            changeMode(Mode.ModeMenu);
		}
		
		if (mode != Mode.ModeMenu)
		{
			SensorController.get().tick();
		}
		
		state.mainloop();
	}
	
	/**
	 * Changes the mode of the program.
	 * Effectively switches to the start state of the given mode.
	 * Used when starting the robot or transitioning from one mode to another.
	 * @param newMode
	 */
	public void changeMode(Mode newMode)
	{
		mode = newMode;
		State startState = null;
		switch(newMode)
		{
		case BoxPush:
			// TODO
			break;
		case Bridge:
			// TODO
			break;
		case ColorSearch:
			// TODO
			break;
		case LineFollow:
			startState = LineFollowState.get();
			break;
		case ModeMenu:
			startState = ModeMenuState.get();
			break;
		default:
			break;
		}
		changeState_Implementation(startState, true);
	}
	
	/**
	 * Changes the state of the program.
	 * Used to switch states within a mode.
	 * CANNOT BE USED TO TRANSITION FROM ONE MODE TO ANOTHER!
	 * @param newState
	 */
	public void changeState(State newState)
	{
		if (state.getClass().getPackage() != newState.getClass().getPackage())
		{
			throw new RuntimeException("Must not transition between modes with changeState.");
		}
		
		changeState_Implementation(newState, false);
	}

	private void changeState_Implementation(State newState, boolean changeMode)
	{
		if (state != null)
		{
			state.onEnd(changeMode);
		}
		
		state = newState;
		
		state.onBegin(changeMode);
	}
}
