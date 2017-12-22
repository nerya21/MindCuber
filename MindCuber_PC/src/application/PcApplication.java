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
			} else if (selection == MainMenu.EXIT.getValue() || selection == -1) {
				return;
			}
		}
	}

	/**
	 * Solve the cube
	 */
	private static void solveCube() {
		ICube cube = new Cube();
		//Robot.waitForCube();
		cube.setColors();
		
//		Colors[][] front = {{Colors.RED, Colors.RED, Colors.WHITE}, {Colors.GREEN, Colors.GREEN, Colors.BLUE}, {Colors.ORANGE, Colors.WHITE, Colors.WHITE}};
//		Colors[][] right = {{Colors.BLUE, Colors.YELLOW, Colors.ORANGE}, {Colors.YELLOW, Colors.ORANGE, Colors.WHITE}, {Colors.GREEN, Colors.ORANGE, Colors.WHITE}};
//		Colors[][] back = {{Colors.YELLOW, Colors.YELLOW, Colors.YELLOW}, {Colors.BLUE, Colors.BLUE, Colors.GREEN}, {Colors.BLUE, Colors.WHITE, Colors.BLUE}};
//		Colors[][] up = {{Colors.GREEN, Colors.GREEN, Colors.GREEN}, {Colors.ORANGE, Colors.YELLOW, Colors.RED}, {Colors.WHITE, Colors.BLUE, Colors.ORANGE}};
//		Colors[][] left = {{Colors.RED, Colors.YELLOW, Colors.GREEN}, {Colors.RED, Colors.RED, Colors.ORANGE}, {Colors.YELLOW, Colors.WHITE, Colors.BLUE}};
//		Colors[][] down = {{Colors.YELLOW, Colors.RED, Colors.ORANGE}, {Colors.GREEN, Colors.WHITE, Colors.BLUE}, {Colors.RED, Colors.ORANGE, Colors.RED}};
//		
//		cube.setColorsManual(up, down, front, back, left, right);
//		
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

		String s = "";
		for (int k =0; k<54;k++) {
			if (facelets[k] == Color.U) {
				s+="U";
			}
			if (facelets[k] == Color.B) {
				s+="B";
			}
			if (facelets[k] == Color.F) {
				s+="F";
			}
			if (facelets[k] == Color.R) {
				s+="R";
			}
			if (facelets[k] == Color.L) {
				s+="L";
			}
			if (facelets[k] == Color.D) {
				s+="D";
			}
		}
		//String s = "ULUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDUDLLLDLLLLLBBBBBBBBB";
		for (int l = 0; l<54; l++) {
			if (s.charAt(l) == 'U')
				facelets[l] = Color.U;
			else if (s.charAt(l) == 'R')
				facelets[l] = Color.R;
			else if (s.charAt(l) == 'L')
				facelets[l] = Color.L;
			else if (s.charAt(l) == 'D')
				facelets[l] = Color.D;
			else if (s.charAt(l) == 'F')
				facelets[l] = Color.F;
			else if (s.charAt(l) == 'B')
				facelets[l] = Color.B;
		}
		
		List<Move> moves = new ArrayList<>();
		int depth = 24;
		int status;
		do {
			status = TwoPhase.findSolution(facelets, depth, 120, moves);
			
			depth = moves.size() - 1;
			
		} while (status == 0);
		
		
		if(moves.size() != 0) {
			handleSolution(cube, moves);
		}
		else {
			//handle error
		}
		Robot.finish();
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
		
		NxtCommand.close();
		Logger.close();
	}

}
