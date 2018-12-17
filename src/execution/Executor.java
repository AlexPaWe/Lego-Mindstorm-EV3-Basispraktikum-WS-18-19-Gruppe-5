package execution;

import boxpush.BoxPushState;
import bridge.BridgeState;
import colorsearch.ColorSearchState;
import lejos.hardware.Button;
import linefollow.LineFollowState;
import modemenu.ModeMenuState;
import robot.ButtonController;
import robot.MotorController;
import robot.SensorController;

/**
 * Executes the main loop, which updates the sensor values and runs logic depending on its state.
 *
 */
public class Executor {
	private static final Mode START_MODE = Mode.ModeMenu;
	
	private Mode mode;
	private State requestedState;
	private Mode requestedMode;
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
		
		if (requestedState != null)
		{
			changeState(requestedState);
			requestedState = null;
		}
		
		if (requestedMode != null)
		{
			changeMode(requestedMode);
			requestedMode = null;
		}
		
		state.mainloop();
	}
	
	/**
	 * Changes the mode of the program.
	 * Effectively switches to the start state of the given mode.
	 * Used when starting the robot or transitioning from one mode to another.
	 * @param newMode
	 */
	public void requestChangeMode(Mode newMode)
	{
		SensorController.get().tick();
		requestedMode = newMode;
	}
	 
	private void changeMode(Mode newMode)
	{
		mode = newMode;
		State startState = null;
		switch(newMode)
		{
		case BoxPush:
			startState = BoxPushState.get();
			break;
		case Bridge:
			startState = BridgeState.get();
			break;
		case ColorSearch:
			startState = ColorSearchState.get();
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
	
	public void requestChangeState(State newState)
	{
		requestedState = newState;
	}
	
	private void changeState(State newState)
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
