package execution;

import linefollow.LineFollowState;
import modemenu.ModeMenuState;

/**
 * Represents an operation mode.
 * Knows the correct start state of the mode.
 *
 */
public enum Mode {
	// all the modes
	// parameters are name and start state
	LineFollow("Line follow", LineFollowState.get()),
	BoxPush("Box push", null), // TODO
	ColorSearch("Color search", null), // TODO
	Bridge("Bridge", null), // TODO
	ModeMenu("", ModeMenuState.get()); // name is not used, because it is not displayed in the menu
	
	private String name;
	private State startState;

	private Mode(String name, State startState) {
		this.name = name;
		this.startState = startState;
	}
	
	/**
	 * Get the name of the mode.
	 * E.g. for display in menus.
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the start state of the mode.
	 * Used for switching modes.
	 * @return
	 */
	public State getStartState() {
		return startState;
	}
}
