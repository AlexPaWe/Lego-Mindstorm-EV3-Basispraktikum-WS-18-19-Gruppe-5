package execution;

/**
 * Represents an operation mode.
 * Knows the correct start state of the mode.
 *
 */
public enum Mode {
	// all the modes
	// parameters are name and start state
	LineFollow("Line follow"),
	BoxPush("Box push"),
	ColorSearch("Color search"),
	Bridge("Bridge"),
	ModeMenu(""); // name is not used, because it is not displayed in the menu
	
	private String name;

	private Mode(String name) {
		this.name = name;
	}
	
	/**
	 * Get the name of the mode.
	 * E.g. for display in menus.
	 * @return
	 */
	public String getName() {
		return name;
	}
}
