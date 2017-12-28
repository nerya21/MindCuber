package robot;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import application.Logger;
import application.LoggerGroup;
import application.LoggerLevel;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.util.Delay;

/**
 * This class contains all of the robot's calibration routines
 * 
 * @see Robot
 */
public class Calibration extends Robot {

	/**
	 * Color sensor calibration
	 */
	public static void colorSensor() {
		int rgb[] = new int[3];
		File calibrationFile = null;
		DataOutputStream calibrationFileStream = null;

		try {
			calibrationFile = new File(CALIBRATION_FILE_COLOR_SENSOR);
			calibrationFile.createNewFile();
			calibrationFileStream = new DataOutputStream(new FileOutputStream(calibrationFile));
			
			rgb = calibrateColor();			
			for (int rgbIndex = 0; rgbIndex < rgb.length; rgbIndex++) {
				calibrationFileStream.writeInt(rgb[rgbIndex]);
				ColorDetector.whiteThreshold[rgbIndex] = rgb[rgbIndex];
			}
			Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "--->  White calibration result: [" + rgb[0] + "][" + rgb[1] + "][" + rgb[2] + "]");
			
			Tray.motor.rotateTo(0);
			
		} catch (IOException e) {
			LCD.clear();
			LCD.drawString("Color calibrate", 0, 0);
			LCD.drawString("Calibration fail", 0, 2);
			LCD.drawString("Using defaults", 0, 3);
			Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "Color sensor calibration failed");
			ColorDetector.setDefaultThresholds();
		} finally {
			ColorDetector.motor.rotateTo(0);
			try {
				calibrationFileStream.close();
			} catch (IOException e1) {
			}
		}
	}

	/**
	 * Calibrate single color
	 *
	 * @return the color average reading
	 */
	private static int[] calibrateColor() {
		LCD.clear();
		LCD.drawString("Color calibrate", 0, 0);
		LCD.drawString("and press Enter", 0, 3);

		ColorDetector.setMotorLocation(SensorLocation.CORNER);
		Button.waitForAnyPress();
		ColorDetector.setMotorLocation(SensorLocation.ALLIGN);
		
		LCD.clear();
		LCD.drawString("Color calibrate", 0, 0);
		LCD.drawString("Scanning " , 0, 2);
		LCD.drawString("Please wait", 0, 3);
		
		return ColorDetector.sensor.readColorRgb(100);
	}

	/**
	 * Cube's tray calibration
	 */
	public static void tray() {
		LCD.clear();
		LCD.drawString("Tray calibrate", 0, 0);
		LCD.drawString("Press < or >", 0, 2);
		LCD.drawString("to rotate", 0, 3);

		calibrateMotor(Tray.motor, true);
	}

	/**
	 * Motor calibration
	 * 
	 * @param motor The motor to calibrate
	 * @param reset Should the method reset the motor location upon pressing ENTER 
	 * @return The current motor location
	 * @see tray()
	 */
	private static int calibrateMotor(NxtMotor motor, boolean reset) {
		return calibrateMotor(motor, reset, null);
	}

	/**
	 * Motor calibration
	 * 
	 * @param motor The motor to calibrate
	 * @param reset Should the method reset the motor location upon pressing ENTER 
	 * @param sensor Sensor to activate while calibrating the motor
	 * @return The current motor location
	 * @see colorMotor()
	 */
	private static int calibrateMotor(NxtMotor motor, boolean reset, NxtSensor sensor) {
		Delay.msDelay(200);
		
		for (;;) {
			if (sensor != null) {
				sensor.readColorRgb(1);
			}
			
			int buttons = Button.waitForAnyPress();
			
			if ((buttons & Button.ID_RIGHT) != 0) {
				motor.rotate(1);
			}
			if ((buttons & Button.ID_LEFT) != 0) {
				motor.rotate(-1);
			}
			if ((buttons & Button.ID_ENTER) != 0) {
				if (reset) {
					motor.resetTachoCount();
				}
				return motor.getTachoCount();
			}
		}
	}
	
	/**
	 * Proximity sensor calibration
	 */
	public static void proximitySensor() {
		LCD.clear();
		LCD.drawString("Proximity diag.", 0, 0);

		Delay.msDelay(200);
		while (!Button.ENTER.isDown() && !Button.ESCAPE.isDown()) {
			LCD.drawString("Current dis: " + ProximitySensor.sensor.getDistance() + "   ", 0, 2);
			Delay.msDelay(50);
		}
	}

	/**
	 * Color sensor motor calibration
	 */
	public static void colorMotor() {
		File calibrationFile = null;
		DataOutputStream calibrationFileStream = null;
		int degree;
		
		try {
			calibrationFile = new File(CALIBRATION_FILE_COLOR_MOTOR);
			calibrationFile.createNewFile();
			calibrationFileStream = new DataOutputStream(new FileOutputStream(calibrationFile));
			
			Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "Color sensor motor calibration:");
			Tray.motor.resetTachoCount();
			
			for (SensorLocation location : SensorLocation.values()) {
				LCD.clear();
				LCD.drawString("Color motor cal.", 0, 0);
				LCD.drawString(location.toString(), 0, 1);
				LCD.drawString("Rotate < > or", 0, 2);
				LCD.drawString("Enter to cont.", 0, 3);
				ColorDetector.motor.rotateTo(0);
				Tray.motor.rotateTo(location == SensorLocation.CORNER ? 45 * 3 : 0);
				ColorDetector.setMotorLocation(location);
				degree = calibrateMotor(ColorDetector.motor, false, ColorDetector.sensor);
				calibrationFileStream.writeInt(degree);
				Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "---> " + location + ": " + degree);
				ColorDetector.setMotorDegree(location, degree);
			}
		} catch (IOException e) {
			LCD.clear();
			LCD.drawString("Motor calibrate", 0, 0);
			LCD.drawString("Calibration fail", 0, 2);
			LCD.drawString("Using defaults", 0, 3);
			ColorDetector.setMotorDefaultDegrees();
			Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "Color motor calibration failed");
		} finally {
			ColorDetector.motor.rotateTo(0);			
			Tray.motor.rotateTo(0);			
			try {
				calibrationFileStream.close();
			} catch (IOException e1) {}
		}			
	}
		
}