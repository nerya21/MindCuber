package robot;

import application.Logger;
import application.LoggerGroup;
import application.LoggerLevel;
import cube.Orientation;
import cube.RawColor;
import lejos.nxt.Button;
import lejos.nxt.LCD;

/**
 * This class contains all the robot's tests
 */
public class Tests extends Robot {

	/**
	 * This method reads the current color multiple times in order to develop and
	 * test color sensor's methods
	 */
	public static void readColor() {
		ColorDetector.setMotorLocation(SensorLocation.ALLIGN);
		LCD.clear();
		LCD.drawString("Hold Enter for", 0, 0);
		LCD.drawString("scan, Exit for", 0, 1);
		LCD.drawString("return to menu", 0, 2);

		String colorFormatted;
		int[] color;
		int colorId;
		for (;;) {
			int buttons = Button.waitForAnyPress();
			
			if ((buttons & Button.ID_ENTER) != 0) {
				color = ColorDetector.sensor.readColorRgb(SENSOR_NUMBER_OF_SAMPLES);
				colorId = ColorDetector.sensor.readColorId();
				RawColor rawColor = new RawColor(Orientation.B, 0, 0, color);
				colorFormatted = "Read color: ID: " + colorId + "\tRed: " + rawColor.red + "\tGreen: " + rawColor.green + "\tBlue: " + rawColor.blue + "\tBackground: " + rawColor.background + "\tHue: " + rawColor.hue + "\tWhite distance: " + rawColor.whiteDistance;
				Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, colorFormatted);
			}
			
			if ((buttons & Button.ID_ESCAPE) != 0) {
				break;
			}
		}
		ColorDetector.motor.rotateTo(0);
	}

	/**
	 * This method makes multiple cube operations (flip, turn and rotate)
	 * in order to manually check the robustness of the robot
	 * 
	 * @param limit Number of operations
	 */
	public static void bruteForce(int limit) {
		for (int i = 0; i < limit; i++) {
			if (Button.ESCAPE.isDown()) {
				break;
			}
			rotateCube(i % 3 == 0 ? Direction.RIGHT : Direction.NONE);
			flipCube(i % 4 == 0 ? FlipMethod.DOUBLE : FlipMethod.SINGLE);
			turnFace(i % 3 == 0 ? Direction.RIGHT : Direction.LEFT);
		}
		Robot.Arm.release();
	}
	
	/**
	 * This method makes only flip operations
	 * in order to manually check the robustness of the robot
	 */
	public static void flipCube() {
		for (int i = 0; !Button.ESCAPE.isDown(); i++) {
			flipCube(i % 4 == 0 ? FlipMethod.DOUBLE : FlipMethod.SINGLE);
			Robot.Arm.release();
		}
	}
}
