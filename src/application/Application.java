package application;

import cube.Cube;
import robot.Robot;
import lejos.nxt.LCD;
import lejos.util.TextMenu;

/**
 * Application Class
 * 
 * Implements the main application routine
 */
public class Application {

	/**
	 * Run the main application routine.
	 */
	private static void run() {
		Logger.log(LoggerLevel.INFO, LoggerGroup.APPLICATION, "Application started");
		TextMenu mainMenu = new TextMenu(MainMenu.getItems(), 1, MainMenu.getTitle());
		TextMenu testsMenu = new TextMenu(TestsMenu.getItems(), 1, TestsMenu.getTitle());
		TextMenu calibrationMenu = new TextMenu(CalibrationMenu.getItems(), 1, CalibrationMenu.getTitle());

		int selection;
		for (;;) {
			selection = waitForUserSelection(mainMenu);
			if (selection == MainMenu.SOLVE.getValue()) {
				solveCube();
			} else if (selection == MainMenu.RCONSOLE.getValue()) {
				Logger.connectConsole();
			} else if (selection == MainMenu.TESTS.getValue()) {
				for (;;) {
					selection = waitForUserSelection(testsMenu);
					if (selection == TestsMenu.BRUTEFORCE.getValue()) {
						robot.Tests.bruteForce();
					} else if (selection == TestsMenu.COLOR.getValue()) {
						robot.Tests.readColor();
					} else if (selection == TestsMenu.BACK.getValue() || selection == -1) {
						break;
					}
				}
			} else if (selection == MainMenu.CALIBRATION.getValue()) {
				for (;;) {
					selection = waitForUserSelection(calibrationMenu);
					if (selection == CalibrationMenu.SENSOR.getValue()) {
						robot.Calibration.colorSensor();
					} else if (selection == CalibrationMenu.COLOR_MOTOR.getValue()) {
						robot.Calibration.colorMotor();
					} else if (selection == CalibrationMenu.TRAY.getValue()) {
						robot.Calibration.tray();
					} else if (selection == CalibrationMenu.PROXIMITY.getValue()) {
						robot.Calibration.proximitySensor();
					} else if (selection == CalibrationMenu.BACK.getValue() || selection == -1) {
						break;
					}
				}
			} else if (selection == MainMenu.EXIT.getValue()) {
				return;
			}
		}
	}

	/**
	 * Solve the cube
	 */
	private static void solveCube() {
		Cube cube = new Cube();
		
		Robot.waitForCube();
		
		cube.setColors();
	}

	/**
	 * Draw menu and wait for user selection
	 * 
	 * @param the
	 *            required menu
	 */
	private static int waitForUserSelection(TextMenu menu) {
		LCD.clear();
		int selection = menu.select();
		return selection;
	}

	/**
	 * The main method
	 */
	public static void main(String[] args) {
		Logger.init(LoggerLevel.DEBUG);
		Robot.init();		
		
		run();

		Logger.close();
	}

}
