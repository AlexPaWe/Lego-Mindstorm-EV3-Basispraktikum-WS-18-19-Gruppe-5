package linefollow;

import execution.Executor;
import execution.State;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import robot.SensorController;
import robot.MotorController.Direction;

public class LineFollowState extends State {
	
	private static LineFollowState instance;
	
	private static final int GENERAL_MOTOR_SPEED = 220;
	private static final float K_P_KRIT = 1000f;
	private static final float SHOULD_VALUE = 0.19f;
	private static final float THRESHOLD = 40f;
	
	private float speedL;
	private float speedR;
	
	private final Port COLOR_SENSOR_PORT = SensorPort.S1;
	
	private EV3ColorSensor colorSensor;
	private SampleProvider redValueSampler;

	private LineFollowState() {
		colorSensor = new EV3ColorSensor(COLOR_SENSOR_PORT);
		redValueSampler = colorSensor.getRedMode();
	}

	public static State get() {
		if (instance == null) {
			instance = new LineFollowState();
		}
		return instance;
	}
	
	@Override
	public void onBegin(boolean modeChanged) {
		// TODO
		// example stuff:
		
		LCD.clear();
	    LCD.drawString("Line Follow", 0, 0);
	}

	@Override
	public void onEnd(boolean modeWillChange) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mainloop() {
		float[] sample = new float[redValueSampler.sampleSize()];
        redValueSampler.fetchSample(sample, 0);
        
		// compute the difference between measured light value and should be value.
		float xd = sample[0] - SHOULD_VALUE;
		
		// translate the difference to the control value y.
		float y = translate(xd);
		
		// TODO: Only for tests: Show control values
		System.out.println(y);
		
		// TODO: Replace operators fittingly to direction change needed.
		if (y < (-1 * THRESHOLD)) {
			speedR = GENERAL_MOTOR_SPEED + Math.abs(y);
			//motorL.backward();
			motors.setLeftMotorDirection(Direction.Backward);
			speedL  = 0.5f * speedR;
			//motorR.forward();
			motors.setRightMotorDirection(Direction.Forward);
		} else if (y > THRESHOLD) {
			speedL = GENERAL_MOTOR_SPEED + Math.abs(y);
			//motorL.forward();
			motors.setLeftMotorDirection(Direction.Forward);
			speedR = 0.5f * speedL;
			//motorR.backward();
			motors.setRightMotorDirection(Direction.Backward);
		} else {
			//motorL.forward();
			motors.setLeftMotorDirection(Direction.Forward);
			//motorR.forward();
			motors.setRightMotorDirection(Direction.Forward);
		}
		
		motors.setLeftMotorSpeed(speedL);
		motors.setRightMotorSpeed(speedR);
	}
	
	/**
	 * Method that implements the translation from the difference into the control value.
	 * Description: See exercise sheet 29.
	 * @param xd: Difference between measured light value and should be value
	 * @return translated control value y
	 */
	private float translate(float xd) {
		return xd * 0.5f * K_P_KRIT;
	}
}
