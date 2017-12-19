package robot;

import application.Logger;
import application.LoggerGroup;
import application.LoggerLevel;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.LCD;

/**
 * Tests Class
 * 
 * This class contains all the robot's tests
 */
public class Tests extends Robot {

	/**
	 * This method reads the current color multiple times in order to develop and
	 * test the readColor method
	 */
	public static void readColor() {
		ColorDetector.setMotorLocation(SensorLocation.ALLIGN);
		LCD.clear();
		LCD.drawString("Hold Enter for", 0, 0);
		LCD.drawString("scan, Exit for", 0, 1);
		LCD.drawString("return to menu", 0, 2);

		String color;
		int[] rawColor;
		int red, green, blue;
		while (!Button.ESCAPE.isDown()) {
			while (Button.ENTER.isDown()) {
				rawColor = ColorDetector.readRgbAverage(100);
				red = rawColor[0];
				green = rawColor[1];
				blue = rawColor[2];
				color = "Result: [" + red + "," + green + "," + blue + "]";
				Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, color);
			}
		}

		ColorDetector.motor.rotateTo(0);
	}

	/**
	 * This method makes multiple cube operations (flipping, turning and rotation)
	 * in order to manually check the robustness of the robot
	 */
	public static void bruteForce() {
		for (int i = 0; !Button.ESCAPE.isDown(); i++) {
			rotateCube(i % 5 == 0 ? Direction.RIGHT : Direction.NONE);
			flipCube(i % 4 == 0 ? FlipMethod.DOUBLE : FlipMethod.SINGLE);
			turnFace(i % 3 == 0 ? Direction.RIGHT : Direction.LEFT);
		}
	}
	
	public static void flipCube() {
		//Robot.flipCube(FlipMethod.SINGLE);
		//Robot.ColorDetector.test2();
	}
}
