package application;

import lejos.nxt.LCD;
import lejos.util.TextMenu;
import robot.Calibration;
import robot.NxtCommand;
import robot.Robot;
import robot.Tests;

/**
 * The main application of the <i>PC project</i>
 * <p>The application responsible for the user
 * interface using the Lejos LCD's API, as well as initializing
 * the logger and the robot
 * 
 * @see Logger
 * @see Robot
 */
public class PcApplication {
	
	/**
	 * The main method
	 */
	public static void main(String[] args) {
		Logger.init(LoggerLevel.DEBUG);
		Robot.init();		
		registerShutdownHandler();
		
		run();
		
		Logger.close();
	}
	
	/**
	 * Run the application's user interface
	 * 
	 * @see MainMenu
	 */
	private static void run() {
		Logger.log(LoggerLevel.INFO, LoggerGroup.APPLICATION, "Application started");
		TextMenu mainMenu = new TextMenu(MainMenu.getItems(), 1, MainMenu.getTitle());
		TextMenu testsMenu = new TextMenu(TestsMenu.getItems(), 1, TestsMenu.getTitle());
		TextMenu calibrationMenu = new TextMenu(CalibrationMenu.getItems(), 1, CalibrationMenu.getTitle());
		TextMenu patternMenu = new TextMenu(PatternMenu.getItems(), 1, PatternMenu.getTitle());

		int selection;
		for (;;) {
			selection = waitForUserSelection(mainMenu);
			if (selection == MainMenu.SOLVE.getValue()) {
				CubeSolver.solveCube();
			} else if (selection == MainMenu.TESTS.getValue()) {
				for (;;) {
					selection = waitForUserSelection(testsMenu);
					if (selection == TestsMenu.BRUTEFORCE.getValue()) {
						Tests.bruteForce();
					} else if (selection == TestsMenu.COLOR.getValue()) {
						Tests.readColor();
					} else if (selection == TestsMenu.FLIP.getValue()) {
						Tests.flipCube();
					} else if (selection == TestsMenu.BACK.getValue() || selection == -1) {
						break;
					}
				}
			} else if (selection == MainMenu.CALIBRATION.getValue()) {
				for (;;) {
					selection = waitForUserSelection(calibrationMenu);
					if (selection == CalibrationMenu.SENSOR.getValue()) {
						Calibration.colorSensor();
					} else if (selection == CalibrationMenu.COLOR_MOTOR.getValue()) {
						Calibration.colorMotor();
					} else if (selection == CalibrationMenu.TRAY.getValue()) {
						Calibration.tray();
					} else if (selection == CalibrationMenu.PROXIMITY.getValue()) {
						Calibration.proximitySensor();
					} else if (selection == CalibrationMenu.BACK.getValue() || selection == -1) {
						break;
					}
				}
			} else if (selection == MainMenu.PATTERN.getValue()) {
				for (;;){
					selection = waitForUserSelection(patternMenu);
					if (selection == PatternMenu.CROSS_4.getValue()) {
						CubeSolver.solveCube(PatternMenu.CROSS_4.getPattern());
					} else if (selection == PatternMenu.PLUS_MINUS.getValue()) {
						CubeSolver.solveCube(PatternMenu.PLUS_MINUS.getPattern());
					} else if (selection == PatternMenu.CUBE_CUBE.getValue()) {
						CubeSolver.solveCube(PatternMenu.CUBE_CUBE.getPattern());
					} else if (selection == PatternMenu.CUBE_CUBE_CUBE.getValue()) {
						CubeSolver.solveCube(PatternMenu.CUBE_CUBE_CUBE.getPattern());
					} else if (selection == CalibrationMenu.BACK.getValue() || selection == -1) {
						break;
					}
				}
			} else if (selection == MainMenu.EXIT.getValue() || selection == -1) {
				return;
			}
		}
	}

	/**
	 * Draw menu and wait for user selection
	 * 
	 * @param menu The requested menu
	 */
	private static int waitForUserSelection(TextMenu menu) {
		LCD.clear();
		int selection = menu.select();
		return selection;
	}

	/**
	 * Register shutdown handler designated to close 
	 * connection to the NXT in case of forced quit
	 */
	private static void registerShutdownHandler() {
		Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
            	//Robot.close();
            	NxtCommand.close();
            }
        });
		
	}

}
