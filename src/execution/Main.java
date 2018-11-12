package execution;

public class Main {

	/**
	 * Starts new program execution.
	 * @param args
	 */
	public static void main(String[] args) {
		Executor.get();
		while(true)
		{
			Executor.get().mainloop();
		}
	}

}
