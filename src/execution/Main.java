package execution;

import lejos.hardware.Button;
import linefollow.LineFollowState;
import robot.ButtonController;

public class Main {

	/**
	 * Starts new program execution.
	 * @param args
	 */
	public static void main(String[] args) {
		//Executor.get();
		State state = LineFollowState.get();
		while(true)
		{
			if (ButtonController.get().isKeyPressedAndReleased(Button.ESCAPE))
			{
	            System.exit(0);
			}
			
			state.mainloop();
			//Executor.get().mainloop();
			
		}
	}

}
