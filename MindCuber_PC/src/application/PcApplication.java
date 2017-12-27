package application;

import java.util.ArrayList;
import java.util.List;
import cube.Cube;
import cube.ICube;
import cube.IFace;
import cube.Orientation;
import robot.Colors;
import robot.NxtCommand;
import robot.Robot;
import twophase.Color;
import twophase.Move;
import twophase.TwoPhase;
import lejos.nxt.LCD;
import lejos.util.TextMenu;

/**
 * Application Class
 * 
 * Implements the main application routine
 */
public class PcApplication {
	
	private static void handleSolution(ICube cube, List<Move> moves) {
		for(Move move: moves) {
			cube.getFace(move.orientation).turn(move.direction);
		}
	}
	
	private static Color convertOrientation2FaceColor(Orientation orientation) {
		switch (orientation) {
		case B:
			return Color.B;
		case D:
			return Color.D;
		case F:
			return Color.F;
		case L:
			return Color.L;
		case R:
			return Color.R;
		case U:
			return Color.U;
		default:
			return null;
		}
	}
	
	private static Orientation convertFaceColor2Orientation(twophase.Color faceColor) {
		switch (faceColor) {
		case B:
			return Orientation.B;
		case D:
			return Orientation.D;
		case F:
			return Orientation.F;
		case L:
			return Orientation.L;
		case R:
			return Orientation.R;
		case U:
			return Orientation.U;
		default:
			return null;
		}
	}
	/**
	 * Run the main application routine.
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
					} else if (selection == TestsMenu.FLIP.getValue()) {
						robot.Tests.flipCube();
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
			} else if (selection == MainMenu.PATTERN.getValue()) {
				for (;;){
					selection = waitForUserSelection(patternMenu);
					if (selection == PatternMenu.CROSS_4.getValue()) {
						solveCube(PatternMenu.CROSS_4.getPattern());
					} else if (selection == PatternMenu.PLUS_MINUS.getValue()) {
						solveCube(PatternMenu.PLUS_MINUS.getPattern());
					} else if (selection == PatternMenu.CUBE_CUBE.getValue()) {
						solveCube(PatternMenu.CUBE_CUBE.getPattern());
					} else if (selection == PatternMenu.CUBE_CUBE_CUBE.getValue()) {
						solveCube(PatternMenu.CUBE_CUBE_CUBE.getPattern());
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
	 * Solve the cube
	 */
	private static void solveCube(String pattern) {
		Logger.log(LoggerLevel.INFO, LoggerGroup.APPLICATION, "Solving cube started");
		ICube cube = new Cube();
		//Robot.waitForCube();
		cube.setColors();
		
		//map colors to face colors
		IFace face;
		
		Color[] colors2FaceColors = new Color[6];
		for(Orientation orientation: Orientation.values()) {
			face = cube.getFace(orientation);
			Colors color = face.getColor(1, 1);
			Color faceColor = convertOrientation2FaceColor(orientation);
			colors2FaceColors[color.getValue()] = faceColor;
		}
		
		//build cube representation for the algorithm
		Color[] facelets = new Color[54];
		Colors realColor;
		Orientation orientation;
		int i,j,c = 0;
		
		for(Color faceColor : Color.values()) {
			orientation = convertFaceColor2Orientation(faceColor);
			face = cube.getFace(orientation);
			
			for (i = 0; i < 3; i++) {				
				for (j = 0; j < 3; j++) {					
					realColor = face.getColor(i, j);
					facelets[c++] = colors2FaceColors[realColor.getValue()];
				}
			}			
		}
		
		List<Move> moves = new ArrayList<>();
		int depth = 24;
		int status;
		do {
			status = TwoPhase.findSolution(facelets, depth - 1, 120, moves, pattern);
			depth = moves.size();
		} while (status == 0 && depth > 0);
		
		//if found a solution (status = 0 when the cube is already solved)
		if(moves.size() != 0 || status == 0) {
			handleSolution(cube, moves);
		}
		else {
			String result = "";
			switch (Math.abs(status)){
				case 1:
					result = "There are not exactly nine facelets of each color!";
					break;
				case 2:
					result = "Not all 12 edges exist exactly once!";
					break;
				case 3:
					result = "Flip error: One edge has to be flipped!";
					break;
				case 4:
					result = "Not all 8 corners exist exactly once!";
					break;
				case 5:
					result = "Twist error: One corner has to be twisted!";
					break;
				case 6:
					result = "Parity error: Two corners or two edges have to be exchanged!";
					break;
				case 7:
					result = "No solution exists for the given maximum move number!";
					break;
				case 8:
					result = "Timeout, no solution found within given maximum time!";
					break;
			}
			
			Logger.log(LoggerLevel.ERROR, LoggerGroup.ALGORITHM, ((status > 0) ? "Pattern error: " : "Cube error") + result);
		}
		
		Logger.log(LoggerLevel.INFO, LoggerGroup.APPLICATION, "Solving cube finished");
		Robot.finish();
	}
	
	private static void solveCube(){
		solveCube("UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB");
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
		registerShutdownHandler();
		
		run();
		
		Logger.close();
	}

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
