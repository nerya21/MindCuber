package robot;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import application.Logger;
import application.LoggerGroup;
import application.LoggerLevel;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.util.Delay;

/**
 * Calibration Class
 * 
 * This class contains all the robot's calibration routines
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
			calibrationFile = new File("sensor.dat");
			calibrationFile.createNewFile();
			calibrationFileStream = new DataOutputStream(new FileOutputStream(calibrationFile));
			
			for (SensorLocation location : SensorLocation.values()) {
				if (location == SensorLocation.CORNER) {
					Tray.motor.rotate(45 * 3);
				}
				Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "Calibration results for " + location + ":");
				for (Colors color : Colors.values()) {
					rgb = calibrateColor(color, location);
					
					for (int rgbIndex = 0; rgbIndex < rgb.length; rgbIndex++) {
						calibrationFileStream.writeInt(rgb[rgbIndex]);
						ColorDetector.thresholds[location.getValue()][color.getValue()][rgbIndex] = rgb[rgbIndex];
					}
					Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "---> " + color + ": [" + rgb[0] + "][" + rgb[1] + "][" + rgb[2] + "]");
				}
				Tray.motor.rotateTo(0);
			}
			
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
	 * Helper method for colorSensor()
	 *
	 * @param color the color calibrated
	 * @param location TODO
	 * @return the color average reading
	 */
	private static int[] calibrateColor(Colors color, SensorLocation location) {
		LCD.clear();
		LCD.drawString("Color calibrate", 0, 0);
		LCD.drawString(location.toString(), 0, 1);
		LCD.drawString("Place " + color, 0, 2);
		LCD.drawString("and press Enter", 0, 3);

		ColorDetector.setMotorLocation(SensorLocation.CORNER);
		Button.waitForAnyPress();
		ColorDetector.setMotorLocation(location);
		
		LCD.clear();
		LCD.drawString("Color calibrate", 0, 0);
		LCD.drawString("Scanning " + color, 0, 2);
		LCD.drawString("Please wait", 0, 3);
		
		return ColorDetector.readRgbAverage(100);
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

	private static int calibrateMotor(NXTRegulatedMotor motor, boolean reset) {
		return calibrateMotor(motor, reset, null);
	}

	private static int calibrateMotor(NXTRegulatedMotor motor, boolean reset, ColorSensor sensor) {
		Delay.msDelay(200);
		
		for (;;) {
			if (sensor != null) {
				sensor.getRawColor(); /* Needed for light */
			}
			
			while (Button.RIGHT.isDown()) {
				motor.rotate(1);
			}
			while (Button.LEFT.isDown()) {
				motor.rotate(-1);
			}
			if (Button.ENTER.isDown()) {
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

	public static void colorMotor() {
		File calibrationFile = null;
		DataOutputStream calibrationFileStream = null;
		int degree;
		
		try {
			calibrationFile = new File("color_motor.dat");
			calibrationFile.createNewFile();
			calibrationFileStream = new DataOutputStream(new FileOutputStream(calibrationFile));
			
			Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "Color sensor motor calibration:");
			
			for (SensorLocation location : SensorLocation.values()) {
				LCD.clear();
				LCD.drawString("Color motor cal.", 0, 0);
				LCD.drawString(location.toString(), 0, 1);
				LCD.drawString("Rotate < > or", 0, 2);
				LCD.drawString("Enter to cont.", 0, 3);
				ColorDetector.motor.rotateTo(0);
				Tray.motor.rotateTo(location == SensorLocation.CORNER ? 45 * 3 : 0);
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
			Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "Color motor calibration failed");
		} finally {
			ColorDetector.motor.rotateTo(0);
			ColorDetector.setMotorDefaultDegrees();
			Tray.motor.rotateTo(0);			
			try {
				calibrationFileStream.close();
			} catch (IOException e1) {}
		}			
	}
		
}