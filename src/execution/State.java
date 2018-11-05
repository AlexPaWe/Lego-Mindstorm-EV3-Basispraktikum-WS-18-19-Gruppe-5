package execution;

import robot.ButtonController;
import robot.MotorController;
import robot.PilotedMotorController;
import robot.SensorController;

/**
 * Encapsulates behaviour of the state.
 *
 */
public abstract class State {
	// shortcut variables for easy access
	protected static MotorController motors = MotorController.get();
	protected static PilotedMotorController pmotors = PilotedMotorController.get();
	protected static SensorController sensors = SensorController.get();
	protected static ButtonController buttons = ButtonController.get();
	
	/**
	 * Executed, when the state is entered.
	 * @param modeChanged
	 */
	public abstract void onBegin(boolean modeChanged);
	
	/**
	 * Executed, when the state ist left.
	 * @param modeWillChange
	 */
	public abstract void onEnd(boolean modeWillChange);
	
	/**
	 * Method that is called each time in the main loop of the executor.
	 */
	public abstract void mainloop();
	
	/**
	 * Get the singleton instance.
	 * MUST BE HIDDEN BY EACH CHILD CLASS!
	 * @return
	 */
	public static State get() {
		throw new RuntimeException("Every state must provide its own get() method!");
	}
}
