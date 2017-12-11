package robot;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import application.Logger;
import application.LoggerGroup;
import application.LoggerLevel;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;

/**
 * Robot Class
 * 
 * Implements simple API for cube manipulation on NXT2.0
 */
public class Robot {
	private static final int ARM_MOTOR_DEFAULT_SPEED = 400;
	private static final int ARM_POSITION_TACKLE = -210;
	private static final int ARM_POSITION_HOLD = -160;
	private static final int ARM_POSITION_REST = 0;
	private static final int SENSOR_MOTOR_SPEED = 400;
	private static final int TRAY_MOTOR_ROTATION_FACTOR = 3;
	private static final int TRAY_MOTOR_STARTUP_SPEED = 500;
	private static final int SENSOR_CENTER_DEFAULT_DEGREE = 170;
	private static final int SENSOR_OUTER_ALLIGN_DEFAULT_DEGREE = 115;
	private static final int SENSOR_OUTER_CORNER_DEFAULT_DEGREE = 95;
	private static final int TRAY_SCAN_STEP_DEGREE = 135;
	private static final int PROXIMITY_SENSOR_THRESHOLD = 15;
	private static final int PROXIMITY_SENSOR_ERROR = 255;
	private static final int[][] COORDINATE_SCAN_ORDER = { { 1, 2 }, { 2, 2 }, { 2, 1 }, { 2, 0 }, { 1, 0 }, { 0, 0 },
			{ 0, 1 }, { 0, 2 }, { 1, 1 } };
	private static final int[][] DEFAULT_COLOR_SENSOR_THRESHOLD = { { 602, 599, 540 }, { 587, 537, 387 },
			{ 520, 320, 230 }, { 255, 368, 404 }, { 318, 456, 318 }, { 566, 345, 224 } };

	/**
	 * Initialization
	 */
	public static void init() {
		Arm.init();
		Tray.init();
		ColorDetector.init();
	}

	/**
	 * Tray Class
	 * 
	 * Represents the motor which controls the cube tray
	 */
	protected static class Tray {
		final static NXTRegulatedMotor motor = new NXTRegulatedMotor(MotorPort.A);

		private static void init() {
			motor.resetTachoCount();
			motor.setSpeed(TRAY_MOTOR_STARTUP_SPEED);
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
		final static NXTRegulatedMotor motor = new NXTRegulatedMotor(MotorPort.B);

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
		private static void release() {
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
		 * @param flipping method (SINGLE/DOUBLE/NONE)
		 */
		private static void flip(FlipMethod method) {
			for (int i = 0; i < method.getFlips(); i++) {
				hold();
				tackle();
				release();
			}			
		}

	}

	/**
	 * ColorDetector Class
	 * 
	 * Represents the color detector unit, both motor and sensor
	 */
	protected static class ColorDetector {
		final static NXTRegulatedMotor motor = new NXTRegulatedMotor(MotorPort.C);
		final static ColorSensor sensor = new ColorSensor(SensorPort.S2);

		static int[][][] thresholds = new int[3][6][3];
		static int centerDegree = SENSOR_CENTER_DEFAULT_DEGREE;
		static int allignDegree = SENSOR_OUTER_ALLIGN_DEFAULT_DEGREE;
		static int cornerDegree = SENSOR_OUTER_CORNER_DEFAULT_DEGREE;
		
		/**
		 * Initialization
		 */
		private static void init() {
			motor.setSpeed(SENSOR_MOTOR_SPEED);
			motor.resetTachoCount();
			updateThresholds();
			updateDegrees();
		}

//		protected static void setMotorDegrees(int center, int allign, int corner) {
//			centerDegree = center;
//			allignDegree = allign;
//			cornerDegree = corner;
//		}
		
		protected static void setMotorDefaultDegrees() {
			setMotorDegree(SensorLocation.CENTER, SENSOR_CENTER_DEFAULT_DEGREE);
			setMotorDegree(SensorLocation.ALLIGN, SENSOR_OUTER_ALLIGN_DEFAULT_DEGREE);
			setMotorDegree(SensorLocation.CORNER, SENSOR_OUTER_CORNER_DEFAULT_DEGREE);
		}
		
//		protected static void setMotorAlligned() {
//			motor.rotateTo(allignDegree);
//		}
//		
//		protected static void setMotorCorner() {
//			motor.rotateTo(cornerDegree);
//		}
//		
//		protected static void setMotorCenter() {
//			motor.rotateTo(centerDegree);
//		}
		
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
		 * Read color from RGB raw data
		 * @param location TODO
		 * 
		 * @return the detected color
		 */
		private static Colors readColor(int[] rawColorRgb, SensorLocation location) {
			double minDistance = 0;
			double distance;
			Colors closestColor = Colors.RED;
			boolean firstRun = true;
			
			Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "Raw reading: [" + rawColorRgb[0] + "][" + rawColorRgb[1] + "][" + rawColorRgb[2] + "]");
			
			for (Colors color : Colors.values()) {
				distance = 0;
				for (int rgbIndex = 0; rgbIndex < 3; rgbIndex++) {
					distance += Math.pow((thresholds[location.getValue()][color.getValue()][rgbIndex] - rawColorRgb[rgbIndex]),2);
				}
				
				Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT,
						"---> Comparing to " + color + ". Threshold: [" + thresholds[location.getValue()][color.getValue()][0] + "]["
								+ thresholds[location.getValue()][color.getValue()][1] + "][" + thresholds[location.getValue()][color.getValue()][2]
								+ "] Distance: " + distance);
				
				if (firstRun || distance < minDistance) {
					firstRun = false;
					Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "------> This is the closest color yet");
					minDistance = distance;
					closestColor = color;
				}
			}
			
			return closestColor;
		}

		/**
		 * Read color from Color raw data
		 * @param location TODO
		 * 
		 * @return the detected color
		 */
		protected static Colors readColor(Color rawColor, SensorLocation location) {
			int rawColorRgb[] = new int[3];
			rawColorRgb[0] = rawColor.getRed();
			rawColorRgb[1] = rawColor.getGreen();
			rawColorRgb[2] = rawColor.getBlue();
			
			return readColor(rawColorRgb, location);
		}
		
		/**
		 * Read color at current position
		 * @param location TODO
		 * 
		 * @return the detected color
		 */
		private static Colors readColor(SensorLocation location) {
			int[] rawColor = readRgbAverage(100);
			return readColor(rawColor, location);
		}
		
		/**
		 * Read RGB averaged
		 *
		 * @param numberOfSamples
		 * @return RGB color
		 */
		public static int[] readRgbAverage(int numberOfSamples) {
			int[] rgb = { 0, 0, 0 };
			int actualNumberOfSamples = 0;
			Color color;
			
			for (int i = 0; i < numberOfSamples; i++) {
				color = sensor.getRawColor();
				if (color.getRed() != 0 && color.getGreen() != 0 && color.getBlue() != 0) {
					rgb[0] += color.getRed();
					rgb[1] += color.getGreen();
					rgb[2] += color.getBlue();
					actualNumberOfSamples++;
				}
			}

			if (actualNumberOfSamples == 0) {
				Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "Fatal color sensor error");
				return new int[] {-1 , -1, -1};
			}
			
			for (int rgbIndex = 0; rgbIndex < 3; rgbIndex++) {
				rgb[rgbIndex] /= actualNumberOfSamples;
			}

			return rgb;
		}
		
		/**
		 * Update thresholds from existing file
		 */
		public static void updateThresholds() {
			File calibrationFile;
			DataInputStream calibrationFileStream = null;
			
			calibrationFile = new File("sensor.dat");
			if (!calibrationFile.exists()) {
				Logger.log(LoggerLevel.WARNING, LoggerGroup.ROBOT, "No calibration file found, default thresholds will be used");
				setDefaultThresholds();
				return;
			}
			
			try {				
				calibrationFileStream = new DataInputStream(new FileInputStream(calibrationFile));
				Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "Loading thresholds from previous calibration:");
				for (SensorLocation location : SensorLocation.values()) {
					for (Colors color : Colors.values()) {
						for (int rgbIndex = 0; rgbIndex < 3; rgbIndex++) {
							thresholds[location.getValue()][color.getValue()][rgbIndex] = calibrationFileStream.readInt();
						}
						Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "---> " + color + ": [" + thresholds[location.getValue()][color.getValue()][0] + "][" + thresholds[location.getValue()][color.getValue()][1] + "][" + thresholds[location.getValue()][color.getValue()][2] + "]");
					}
				}
			} catch (IOException e) {
				Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "Failed loading thresholds from file, default thresholds will be used");
				setDefaultThresholds();
			} finally {
				try { calibrationFileStream.close(); } catch (IOException e1) { }
			}
		}

		/**
		 * Update degrees from existing file
		 */
		public static void updateDegrees() {
			File calibrationFile;
			DataInputStream calibrationFileStream = null;
			
			calibrationFile = new File("color_motor.dat");
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
			Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "Setting default thresholds. Note: this thresholds are not optimised, please run the calibration routine from the menu");
			for (SensorLocation location : SensorLocation.values()) {
				for (Colors color : Colors.values()) {
					for (int rgbIndex = 0; rgbIndex < 3; rgbIndex++) {
						ColorDetector.thresholds[location.getValue()][color.getValue()][rgbIndex] = DEFAULT_COLOR_SENSOR_THRESHOLD[color.getValue()][rgbIndex];
					}
				}
			}		
		}
	}

	/**
	 * ProximitySensor Class
	 * 
	 * Represents the proximity sensor unit
	 */
	protected static class ProximitySensor {
		final static UltrasonicSensor sensor = new UltrasonicSensor(SensorPort.S3);
	}
	
	/**
	 * Scan cube's face
	 *
	 * @return 2d array representing the face's colors
	 */
	public static Colors[][] scanFace() {
		Colors[][] faceColors = new Colors[3][3];
		int coordinate, row, col;
		SensorLocation location;
		
		for (coordinate = 0; coordinate < 8; coordinate++) {
			row = COORDINATE_SCAN_ORDER[coordinate][0];
			col = COORDINATE_SCAN_ORDER[coordinate][1];
			location = coordinate % 2 == 0 ? SensorLocation.ALLIGN : SensorLocation.CORNER;
			ColorDetector.setMotorLocation(location);
			faceColors[row][col] = ColorDetector.readColor(location);
			Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "Color at [" + row + "][" + col + "] is " + faceColors[row][col]);
			Tray.motor.rotate(TRAY_SCAN_STEP_DEGREE);
		}

		row = COORDINATE_SCAN_ORDER[coordinate][0];
		col = COORDINATE_SCAN_ORDER[coordinate][1];
		ColorDetector.setMotorLocation(SensorLocation.CENTER);
		faceColors[row][col] = ColorDetector.readColor(SensorLocation.CENTER);
		Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "Color at [" + row + "][" + col + "] is " + faceColors[row][col]);
		ColorDetector.motor.rotateTo(0);
		return faceColors;
	}

	/**
	 * Flip the cube
	 *
	 * @param flipping method (SINGLE/DOUBLE/NONE)
	 */
	public static void flipCube(FlipMethod method) {
		Arm.flip(method);
	}

	/**
	 * Rotate the face currently pointing down
	 *
	 * @param turning direction (LEFT/RIGHT/MIRROR/NONE)
	 */
	public static void turnFace(Direction direction) {
		Arm.hold();
		Tray.motor.rotate(direction.getDegree() * TRAY_MOTOR_ROTATION_FACTOR);
		Arm.release();
	}

	/**
	 * Rotate the entire cube
	 *
	 * @param rotate direction (LEFT/RIGHT/MIRROR/NONE)
	 */
	public static void rotateCube(Direction direction) {
		Tray.rotate(direction.getDegree());
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
}
