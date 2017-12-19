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
import lejos.robotics.RegulatedMotor;
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
				Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "Calibration results for " + location + ":");
				if (location == SensorLocation.CORNER) {
					ColorDetector.motor.rotateTo(0);
					Tray.motor.rotateTo(45 * 3);
					ColorDetector.setMotorLocation(SensorLocation.CORNER);
				}
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

	private static void calibrateMotor(NxtMotor motor, boolean reset) {
		calibrateMotor(motor, reset, null);
	}

	private static void calibrateMotor(NxtMotor motor, boolean reset, NxtSensor sensor) {
		Delay.msDelay(200);
		
		for (;;) {
			if (sensor != null) {
				//sensor.setLight();
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
				degree = 0;
				calibrateMotor(ColorDetector.motor, false);
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