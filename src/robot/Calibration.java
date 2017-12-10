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
			
			ColorDetector.setMotorAlligned();
			Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "Calibration results:");
			for (Colors color : Colors.values()) {
				rgb = calibrateColor(color);
				for (int rgbIndex = 0; rgbIndex < rgb.length; rgbIndex++) {
					calibrationFileStream.writeInt(rgb[rgbIndex]);
					ColorDetector.thresholds[color.getValue()][rgbIndex] = rgb[rgbIndex];
				}
				Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT,
						"---> " + color + ": [" + rgb[0] + "][" + rgb[1] + "][" + rgb[2] + "]");
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
	 * @return the color average reading
	 */
	private static int[] calibrateColor(Colors color) {
		LCD.clear();
		LCD.drawString("Color calibrate", 0, 0);
		LCD.drawString("Place " + color, 0, 2);
		LCD.drawString("and press Enter", 0, 3);

		Button.waitForAnyPress();

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
		int centerDegree, allignDegree, cornerDegree;
		
		try {
			calibrationFile = new File("color_motor.dat");
			calibrationFile.createNewFile();
			calibrationFileStream = new DataOutputStream(new FileOutputStream(calibrationFile));
			
			Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "Color sensor motor calibration:");
			
			LCD.clear();
			LCD.drawString("Color motor cal.", 0, 0);
			LCD.drawString("Center", 0, 1);
			LCD.drawString("Rotate < > or", 0, 2);
			LCD.drawString("Enter to cont.", 0, 3);
			ColorDetector.motor.rotateTo(0);
			Tray.motor.rotateTo(0);
			centerDegree = calibrateMotor(ColorDetector.motor, false, ColorDetector.sensor);
			calibrationFileStream.writeInt(centerDegree);
			Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "---> Center: " + centerDegree);
			
			LCD.clear();
			LCD.drawString("Color motor cal.", 0, 0);
			LCD.drawString("Outer", 0, 1);
			LCD.drawString("Rotate < > or", 0, 2);
			LCD.drawString("Enter to cont.", 0, 3);
			ColorDetector.motor.rotateTo(0);
			allignDegree = calibrateMotor(ColorDetector.motor, false, ColorDetector.sensor);		
			calibrationFileStream.writeInt(allignDegree);
			Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "---> Outer: " + allignDegree);
			
			LCD.clear();
			LCD.drawString("Color motor cal.", 0, 0);
			LCD.drawString("Corner", 0, 1);
			LCD.drawString("Rotate < > or", 0, 2);
			LCD.drawString("Enter to cont.", 0, 3);
			ColorDetector.motor.rotateTo(0);
			Tray.motor.rotateTo(45 * 3);
			cornerDegree = calibrateMotor(ColorDetector.motor, false, ColorDetector.sensor);
			calibrationFileStream.writeInt(cornerDegree);
			Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "---> Corner: " + cornerDegree);
			
			ColorDetector.setMotorDegrees(centerDegree, allignDegree, cornerDegree);
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