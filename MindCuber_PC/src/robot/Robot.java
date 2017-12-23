

package robot;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import application.Logger;
import application.LoggerGroup;
import application.LoggerLevel;
import cube.Orientation;
import cube.RawColor;
import lejos.nxt.LCD;
import lejos.util.Delay;

/**
 * Robot Class
 * 
 * Implements simple API for cube manipulation on NXT2.0
 */
public class Robot {
	private static final int ARM_MOTOR_DEFAULT_SPEED = 550;
	private static final int ARM_POSITION_HOLD = -157;
	private static final int ARM_POSITION_HOLD_EXTRA = 10;
	private static final int ARM_POSITION_TACKLE = ARM_POSITION_HOLD - 43;//47
	private static final int ARM_POSITION_REST = -0;
	private static final int ARM_FLIP_DELAY_MS = 100;
	private static final int SENSOR_MOTOR_SPEED = 400;
	private static final int TRAY_MOTOR_ROTATION_FACTOR = 3;
	private static final int TRAY_MOTOR_DEFAULT_SPEED = 650;
	private static final int TRAY_MOTOR_EXTRA_ROTATION = 15;
	private static final int TRAY_MOTOR_SCAN_SPEED = 200;
	private static final int SENSOR_CENTER_DEFAULT_DEGREE = 170;
	private static final int SENSOR_OUTER_ALLIGN_DEFAULT_DEGREE = 115;
	private static final int SENSOR_OUTER_CORNER_DEFAULT_DEGREE = 95;
	private static final int TRAY_SCAN_STEP_DEGREE = 135;
	private static final int PROXIMITY_SENSOR_THRESHOLD = 15;
	private static final int PROXIMITY_SENSOR_ERROR = 255;
	private static final int[][] COORDINATE_SCAN_ORDER = { { 1, 2 }, { 2, 2 }, { 2, 1 }, { 2, 0 }, { 1, 0 }, { 0, 0 }, { 0, 1 }, { 0, 2 }, { 1, 1 } };
	private static final int[] DEFAULT_WHITE_CALIBRATION = {255, 255, 255};
	protected static final String CALIBRATION_FILE_COLOR_SENSOR = "sensor.dat";
	protected static final String CALIBRATION_FILE_COLOR_MOTOR = "color_motor.dat";
	
	public static void init() {
		NxtCommand.init();
		Arm.init();
		Tray.init();
		ColorDetector.init();
	}
	
	public static void close() {
		Arm.motor.rotateTo(0);
		if (Tray.motor.getTachoCount() % (90 * TRAY_MOTOR_ROTATION_FACTOR) != 0) {
			Tray.motor.rotate(-(Tray.motor.getTachoCount() % (90 * TRAY_MOTOR_ROTATION_FACTOR)));
		}
		ColorDetector.motor.rotateTo(0);
	}
	
	/**
	 * Tray Class
	 * 
	 * Represents the motor which controls the cube tray
	 */
	public static class Tray {
		final static NxtMotor motor = new NxtMotor(0); //A

		private static void init() {
			motor.resetTachoCount();
			motor.setSpeed(TRAY_MOTOR_DEFAULT_SPEED);
		}

		private static void rotate(int degree) {
			motor.rotate(degree * TRAY_MOTOR_ROTATION_FACTOR);
		}
		
	}

	/**
	 * Arm Class
	 * 
	 * Represents the motor responsible for holding and flipping the cube
	 */
	protected static class Arm {
		final static NxtMotor motor = new NxtMotor(1); //B
		
		/**
		 * Initialization
		 */
		private static void init() {
			motor.setSpeed(ARM_MOTOR_DEFAULT_SPEED);
			motor.resetTachoCount();
		}

		
		
		/**
		 * Set the arm to hold position
		 */
		private static void hold() {
			motor.rotateTo(ARM_POSITION_HOLD);
		}

		/**
		 * Set the arm to release position
		 */
		static void release() {
			motor.rotateTo(ARM_POSITION_REST);
		}

		/**
		 * Set the arm to tackle position
		 */
		private static void tackle() {
			motor.rotateTo(ARM_POSITION_TACKLE);
		}

		/**
		 * Flip the cube
		 *
		 * @param method Flip method (SINGLE/DOUBLE/NONE)
		 */
		private static void flip(FlipMethod method) {
			for (int i = 0; i < method.getFlips(); i++) {
				motor.rotateTo(ARM_POSITION_HOLD+ARM_POSITION_HOLD_EXTRA);
				motor.rotateTo(ARM_POSITION_HOLD);
				Delay.msDelay(ARM_FLIP_DELAY_MS);				
				motor.rotateTo(ARM_POSITION_TACKLE);
				//motor.rotateTo(ARM_POSITION_HOLD+ARM_POSITION_HOLD_EXTRA);
				motor.rotateTo(ARM_POSITION_HOLD);
			}			
		}

	}

	/**
	 * ColorDetector Class
	 * 
	 * Represents the color detector unit, both motor and sensor
	 */
	public static class ColorDetector {
		final static NxtMotor motor = new NxtMotor(2); //C
		final static NxtSensor sensor = new NxtSensor(0); //1

		public static int[] whiteThreshold = new int[3];
		static int centerDegree;
		static int allignDegree;
		static int cornerDegree;
		
		/**
		 * Initialization
		 */
		private static void init() {
			motor.setSpeed(SENSOR_MOTOR_SPEED);
			motor.resetTachoCount();
			loadThresholds();
			loadDegrees();
		}

		protected static void setMotorDefaultDegrees() {
			setMotorDegree(SensorLocation.CENTER, SENSOR_CENTER_DEFAULT_DEGREE);
			setMotorDegree(SensorLocation.ALLIGN, SENSOR_OUTER_ALLIGN_DEFAULT_DEGREE);
			setMotorDegree(SensorLocation.CORNER, SENSOR_OUTER_CORNER_DEFAULT_DEGREE);
		}
		
		protected static void setMotorLocation(SensorLocation location) {
			int degree;
			
			switch (location) {
			case CENTER:
				degree = centerDegree;
				break;
			case ALLIGN:
				degree = allignDegree;
				break;
			case CORNER:
				degree = cornerDegree;
				break;
			default:
				degree = 0;
			}
			motor.rotateTo(degree);
		}
		
		protected static void setMotorDegree(SensorLocation location, int degree) {
			switch (location) {
			case CENTER:
				centerDegree = degree;
				break;
			case ALLIGN:
				allignDegree = degree;
				break;
			case CORNER:
				cornerDegree = degree;
				break;
			default:
			}
		}
		
		/**
		 * Load thresholds from existing file
		 */
		public static void loadThresholds() {
			File calibrationFile;
			DataInputStream calibrationFileStream = null;
			
			calibrationFile = new File(CALIBRATION_FILE_COLOR_SENSOR);
			if (!calibrationFile.exists()) {
				Logger.log(LoggerLevel.WARNING, LoggerGroup.ROBOT, "No calibration file found, default thresholds will be used");
				setDefaultThresholds();
				return;
			}
			
			try {				
				calibrationFileStream = new DataInputStream(new FileInputStream(calibrationFile));
				Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "Loading thresholds from previous calibration:");
				for (int rgbIndex = 0; rgbIndex < 3; rgbIndex++) {
					ColorDetector.whiteThreshold[rgbIndex] = calibrationFileStream.readInt();
				}
				Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "---> [" + ColorDetector.whiteThreshold[0] + "][" + ColorDetector.whiteThreshold[1] + "]["+ ColorDetector.whiteThreshold[2] + "]");
			} catch (IOException e) {
				Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "Failed loading thresholds from file, default thresholds will be used");
				setDefaultThresholds();
			} finally {
				try { calibrationFileStream.close(); } catch (IOException e1) { }
			}
		}

		/**
		 * Load degrees from existing file
		 */
		public static void loadDegrees() {
			File calibrationFile;
			DataInputStream calibrationFileStream = null;
			
			calibrationFile = new File(CALIBRATION_FILE_COLOR_MOTOR);
			if (!calibrationFile.exists()) {
				Logger.log(LoggerLevel.WARNING, LoggerGroup.ROBOT, "No calibration file found, default degrees will be used");
				setMotorDefaultDegrees();
				return;
			}
			
			try {				
				calibrationFileStream = new DataInputStream(new FileInputStream(calibrationFile));
				Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "Loading degrees from previous calibration:");
				
				int degree;
				for (SensorLocation location : SensorLocation.values()) {
					degree = calibrationFileStream.readInt();
					setMotorDegree(location, degree);
					Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "---> " + location + ": " + degree);
				}
			} catch (IOException e) {
				Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "Failed loading thresholds from file, default degrees will be used");
				setMotorDefaultDegrees();
			} finally {
				try { calibrationFileStream.close(); } catch (IOException e1) { }
			}
		}
		
		/**
		 * Set thresholds to defaults 
		 */
		public static void setDefaultThresholds() {
			Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "Setting default thresholds. Note: those thresholds are not optimised, please run the calibration routine from the menu");
			for (int rgbIndex = 0; rgbIndex < 3; rgbIndex++) {
				ColorDetector.whiteThreshold[rgbIndex] = DEFAULT_WHITE_CALIBRATION[rgbIndex];
			}
		}		
	}

	/**
	 * ProximitySensor Class
	 * 
	 * Represents the proximity sensor unit
	 */
	protected static class ProximitySensor {
		final static NxtSensor sensor = new NxtSensor(2); //3
	}	

	public static void setTrayScanSpeed() {
		Tray.motor.setSpeed(TRAY_MOTOR_SCAN_SPEED);
	}
	
	public static void setTrayDefaultSpeed() {
		Tray.motor.setSpeed(TRAY_MOTOR_DEFAULT_SPEED);
	}
	
	/**
	 * Scan cube's face
	 * 
	 * @param allColors List of all scanned colors 
	 * @param orientation Current cube orientation
	 */
	public static void scanFace(ArrayList<RawColor> allColors, Orientation orientation) {
		int coordinate, row, col;
		int[] rgb;
		SensorLocation location;
		Arm.release();
		for (coordinate = 0; coordinate < 8; coordinate++) {
			row = COORDINATE_SCAN_ORDER[coordinate][0];
			col = COORDINATE_SCAN_ORDER[coordinate][1];
			location = coordinate % 2 == 0 ? SensorLocation.ALLIGN : SensorLocation.CORNER;
			ColorDetector.setMotorLocation(location);
			rgb = ColorDetector.sensor.readColorRgb(100);
			RawColor rawColor = new RawColor(orientation, row, col, rgb);
			allColors.add(rawColor);
			String colorFormatted = orientation + "[" + row + "][" + col + "]: RGB=[" + rawColor.red + "][" + rawColor.green + "][" + rawColor.blue + "] Hue=" + rawColor.hue + " whiteDistance=" + rawColor.whiteDistance;
			Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, colorFormatted);
			Tray.motor.rotate(TRAY_SCAN_STEP_DEGREE);
		}

		row = COORDINATE_SCAN_ORDER[coordinate][0];
		col = COORDINATE_SCAN_ORDER[coordinate][1];
		ColorDetector.setMotorLocation(SensorLocation.CENTER);
		//faceColors[row][col] = ColorDetector.readColor(SensorLocation.CENTER);
		rgb = ColorDetector.sensor.readColorRgb(100);
		RawColor rawColor = new RawColor(orientation, row, col, rgb);
		allColors.add(rawColor);
		String colorFormatted = orientation + "[" + row + "][" + col + "]: RGB=[" + rawColor.red + "][" + rawColor.green + "][" + rawColor.blue + "] Hue=" + rawColor.hue + " whiteDistance=" + rawColor.whiteDistance;
		Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, colorFormatted);
		ColorDetector.motor.rotateTo(0);
	}

	/**
	 * Flip the cube
	 *
	 * @param method Flip method (SINGLE/DOUBLE/NONE)
	 */
	public static void flipCube(FlipMethod method) {
		Arm.flip(method);
	}

	/**
	 * Rotate the face currently pointing down
	 *
	 * @param direction Turning direction (LEFT/RIGHT/MIRROR/NONE)
	 */
	public static void turnFace(Direction direction) {
		Arm.motor.rotateTo(ARM_POSITION_HOLD);
		int extraRotation = direction.getDegree() > 0 ? TRAY_MOTOR_EXTRA_ROTATION : (-TRAY_MOTOR_EXTRA_ROTATION);
		Tray.motor.rotate((direction.getDegree() + extraRotation) * TRAY_MOTOR_ROTATION_FACTOR);
		Tray.motor.rotate((-extraRotation) * TRAY_MOTOR_ROTATION_FACTOR);
		//Tray.motor.rotate(direction.getDegree() * TRAY_MOTOR_ROTATION_FACTOR);
	}

	/**
	 * Rotate the entire cube
	 *
	 * @param direction Rotate direction (LEFT/RIGHT/MIRROR/NONE)
	 */
	public static void rotateCube(Direction direction) {
		if (direction != Direction.NONE) {
			Arm.release();
			Tray.rotate(direction.getDegree());
		}
	}
	
	/**
	 * Wait for cube to be placed on the tray 
	 */
	public static void waitForCube() {
		Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "Waiting for cube");
		LCD.clear();
		LCD.drawString("Place cube on", 0, 0);
		LCD.drawString("the tray", 0, 1);
		
		for (;;) {
			if (ProximitySensor.sensor.getDistance() <= PROXIMITY_SENSOR_THRESHOLD) {
				Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "Cube detected");
				break;
			}
			
			if (ProximitySensor.sensor.getDistance() == PROXIMITY_SENSOR_ERROR) {
				Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "Proximity sensor returned error. Assuming cube placed");
				break;
			}
		}
		
		LCD.clear();
		LCD.drawString("Cube detected,", 0, 0);
		LCD.drawString("Please wait", 0, 1);
		
		Delay.msDelay(2000);
	}

	public static void finish() {
		Robot.Arm.release();
	}
}
