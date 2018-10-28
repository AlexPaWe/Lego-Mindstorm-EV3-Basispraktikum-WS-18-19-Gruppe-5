package de.kit.legomindstorm.gruppe05.robot;

import lejos.hardware.Key;
import lejos.hardware.Sound;
import lejos.utility.Delay;

/**
 * Encapsulates button and LED access.
 *
 */
public class ButtonController {
	private static ButtonController instance;

	private ButtonController() {
	}

	/**
	 * Get the singleton instance.
	 * @return
	 */
	public static ButtonController get() {
		if (instance == null) {
			instance = new ButtonController();
		}
		return instance;
	}
	
	/**
	 * LED patterns to use with "changeLEDPattern".
	 *
	 */
	public enum LEDPattern { Off, StaticGreen, StaticRed, StaticYellow };
	
	/**
	 * Changes the LED pattern. Patterns are provided in class enum "LEDPattern".
	 * @param pattern
	 */
	public void changeLEDPattern(LEDPattern pattern) {
		lejos.hardware.Button.LEDPattern(pattern.ordinal());
	}
	
	/**
	 * Returns if a button was pressed.
	 * Blocks while the button is still pressed, so the action does not fire every loop.
	 * @param k
	 * @return
	 */
	public boolean isKeyPressedAndReleased(Key k) {
        if (k.isDown()) {
            Sound.playTone(500, 20);
            while (k.isDown()) {
                // Wait for button release
                Delay.msDelay(10);
            }
            Sound.playTone(600, 20);
            return true;
        } else {
            return false;
        }
    }
}
