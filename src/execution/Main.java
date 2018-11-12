package execution;

import linefollow.LineFollowState;

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
			state.mainloop();
			//Executor.get().mainloop();
			
		}
	}

}
