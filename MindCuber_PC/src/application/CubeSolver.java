package application;

import java.util.ArrayList;
import java.util.List;

import cube.Cube;
import cube.ICube;
import cube.IFace;
import cube.Orientation;
import robot.Colors;
import robot.Robot;
import twophase.Color;
import twophase.Move;
import twophase.TwoPhase;

/**
 * This class contains the methods responsible for solving the cube
 * 
 * @see Robot
 * @see Cube
 * @see TwoPhase
 */
public class CubeSolver {
	
	/**
	 * Solves the cube. If error found in cube's colors - try again
	 * 
	 * @param attempts Number of attempts to solve the cube
	 * @param pattern The desired cube pattern
	 * @return errorCode - 0 for success, otherwise error code as specified in TwoPhase
	 *  or -9 when there are two centerpieces with the same color
	 */
	public static int forceSolveCube(int attempts, String pattern) {
		int currAttempts = 0, status;

		do {
			status = solveCube(pattern);
			currAttempts++;
		} while (status != 0 && currAttempts < attempts);
		
		return status;
	}
	
	/**
	 * Solve the cube to standard pattern (each face different color). 
	 * If error found in cube's colors - try again
	 * 
	 * @param attempts Number of attempts to solve the cube
	 * @return errorCode - 0 for success, otherwise error code as specified in TwoPhase
	 *  or -9 when there are two centerpieces with the same color
	 */
	public static int forceSolveCube(int attempts) {
		return forceSolveCube(attempts, "UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB");
	}
	
	/**
	 * Solve the cube
	 * 
	 * @param pattern The desired cube pattern 
	 * @return errorCode - 0 for success, otherwise error code as specified in TwoPhase
	 *  or -9 when there are two centerpieces with the same color
	 * @see PatternMenu
	 * @see TwoPhase
	 */
	private static int solveCube(String pattern) {
		Logger.log(LoggerLevel.INFO, LoggerGroup.APPLICATION, "Solving cube started");
		ICube cube = new Cube();
		Robot.waitForCube();
		cube.setColors();
		int errorCode = 0;
		String cubeString = createCubeRepForAlgorithm(cube);
		//check if conversion failed
		if (cubeString == null) { 
			//conversion failed, there are two centerpieces with the same color
			Logger.log(LoggerLevel.ERROR, LoggerGroup.APPLICATION, "Two center facelets have the same color");
			errorCode = -9;
		} else {
			Logger.log(LoggerLevel.INFO, LoggerGroup.APPLICATION, "Start calculating moves to solution");
			List<Move> moves = new ArrayList<>();
			int depth = 24; //24 steps are enough to solve the cube
			do {
				errorCode = TwoPhase.findSolution(cubeString, depth, 30, moves, pattern);
				depth = moves.size() - 1; //try solve with less steps
			} while (errorCode == 0 && depth > 0);
			
			//if found a solution (status = 0 when the cube is already solved) 
			if (moves.size() != 0 || errorCode == 0) {
				errorCode = 0;
				Logger.log(LoggerLevel.INFO, LoggerGroup.APPLICATION, "Finish calculating moves, start solving");
				handleSolution(cube, moves);
				Logger.log(LoggerLevel.INFO, LoggerGroup.APPLICATION, "Solving cube finished");
				Robot.finishSolve();
			} else {
				//handle error code
				Logger.log(LoggerLevel.INFO, LoggerGroup.APPLICATION, "Error calculating moves:");
				String result = "";
				switch (Math.abs(errorCode)) {
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
				
				Logger.log(LoggerLevel.ERROR, LoggerGroup.ALGORITHM, "---> " + ((errorCode > 0) ? "Pattern error: " : "Cube error: ") + result);
			}
		}	
		return errorCode;
	}
	
	/**
	 * Solve the cube to standard pattern (each face different color)
	 * 
	 * @return Status - 0 for success, otherwise error
	 * @see #solveCube(String pattern)
	 * 
	 * @deprecated Use forceSolveCube(0)
	 */
	@Deprecated
	public static int solveCube(){
		return solveCube("UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB");
	}
	
	/**
	 * Handle cube solution given the required moves
	 *  
	 * @param cube The rubik's cube
	 * @param moves The required moves to the selected pattern
	 */
	private static void handleSolution(ICube cube, List<Move> moves) {
		for(Move move: moves) {
			cube.getFace(move.orientation).turn(move.direction);
		}
	}
	
	/**
	 * Convert cube's orientation to face's color
	 *  
	 * @param orientation The cube's orientation
	 * @return The face's color
	 */
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
	
	/**
	 * Creates the string representation of the cube.
	 * see {@link twophase.TwoPhase#findSolution cube parameter definition}
	 * Conversion fails when there are two center facelets with the same color
	 * @param cube - the cube to create representation for
	 * @return the string representation of the cube on success, and null on conversion failure.
	 */
	public static String createCubeRepForAlgorithm(ICube cube) {
		IFace face;
		Colors realColor;
		Color faceColor;
		//map colors to face colors
		Color[] colors2FaceColors = new Color[6];
		for(Orientation orientation: Orientation.values()) {
			face = cube.getFace(orientation);
			realColor = face.getColor(1, 1);
			faceColor = convertOrientation2FaceColor(orientation);
			if(colors2FaceColors[realColor.getValue()] == null) {
				colors2FaceColors[realColor.getValue()] = faceColor;
			}
			else {
				//if this array entry is not null, we already have a conversion from this color to face color.
				//this means that two centerpieces have the same color
				return null;
			}
		}	
		//build cube representation for the algorithm
		//string format is 
		StringBuilder cubeString = new StringBuilder(54);
		int i,j;
		for(Orientation orientation : Orientation.values()) {
			face = cube.getFace(orientation);
			//add face to string
			for (i = 0; i < 3; i++) {
				for (j = 0; j < 3; j++) {	
					//convert real color to face color
					realColor = face.getColor(i, j);
					faceColor = colors2FaceColors[realColor.getValue()];
					cubeString.append(faceColor.toString());
				}
			}
		}
		return cubeString.toString();
	}
}
