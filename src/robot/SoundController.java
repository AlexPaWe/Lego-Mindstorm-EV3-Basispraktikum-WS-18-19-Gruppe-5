package robot;

import lejos.hardware.Sound;

/**
 * Encapsulates button and LED access.
 *
 */
public class SoundController {
	private static SoundController instance;
	
	// 1-100
	private static final int DEFAULT_VOLUME = 20;
	private static final int LOUD_VOLUME = 100;

	private SoundController() {
	}

	/**
	 * Get the singleton instance.
	 * @return
	 */
	public static SoundController get() {
		if (instance == null) {
			instance = new SoundController();
		}
		return instance;
	}
	
	public void loudBeep()
	{
		Sound.setVolume(LOUD_VOLUME);
		Sound.beep();
		Sound.setVolume(DEFAULT_VOLUME);
	}
	
	public void beep()
	{
		Sound.beep();
	}
}
