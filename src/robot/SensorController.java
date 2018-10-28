package robot;

import lejos.hardware.Sound;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;

/**
 * Encapsulates sensor access.
 *
 */
public class SensorController {
	private static SensorController instance;
	
	private final Port COLOR_SENSOR_PORT = SensorPort.S4;
	private final Port DISTANCE_SENSOR_PORT = SensorPort.S1;
	private final Port TOUCH_SENSOR_PORT = SensorPort.S3;
	
	private int colorId;
	private float distance;
	private boolean touching;
	
	private EV3ColorSensor colorSensor;
	private EV3UltrasonicSensor distanceSensor;
	private EV3TouchSensor touchSensor;

	private SensorController() {
		colorId = 0;
		distance = 0;
		colorSensor = new EV3ColorSensor(COLOR_SENSOR_PORT);
		distanceSensor = new EV3UltrasonicSensor(DISTANCE_SENSOR_PORT);
		touchSensor = new EV3TouchSensor(TOUCH_SENSOR_PORT);
	}

	/**
	 * Get the singleton instance.
	 * @return
	 */
	public static SensorController get() {
		if (SensorController.instance == null) {
			SensorController.instance = new SensorController();
		}
		return SensorController.instance;
	}
	
	/**
	 * Update all sensor values.
	 * Used in the main loop.
	 */
	public void tick() {
		updateColorId();
		updateDistance();
		updateTouch();
	}
	
	private void updateColorId() {
		colorId = colorSensor.getColorID();
	}
	
	private void updateDistance() {
		SensorMode distanceSampler = distanceSensor.getMode("Distance");
        float[] sample = new float[distanceSampler.sampleSize()];
        distanceSampler.fetchSample(sample, 0);
        distance = sample[0];
	}
	
	private void updateTouch() {
        float[] sample = new float[touchSensor.sampleSize()];
        sample[0] = 0;
        touchSensor.fetchSample(sample, 0);
        if(sample[0] == 1) {
            Sound.playTone(800, 20);
        }
        touching = (sample[0] == 1);
    }
	
	/**
	 * Get the measured distance of the ultrasonic sensor.
	 * @return
	 */
	public float getDistance() {
		return distance;
	}
	
	/**
	 * Get the measured color id of the color sensor.
	 * @return
	 */
	public int getColorId() {
		return colorId;
	}
	
	/**
	 * Get whether the touch sensor is pressed.
	 * @return
	 */
	public boolean isTouching() {
		return touching;
	}
}
