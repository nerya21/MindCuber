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
	 * Solve the cube
	 * 
	 * @param pattern The desired cube pattern 
	 * @see PatternMenu
	 * @see TwoPhase
	 */
	public static void solveCube(String pattern) {
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
		
		Logger.log(LoggerLevel.INFO, LoggerGroup.APPLICATION, "Start calculating moves to solution");
		List<Move> moves = new ArrayList<>();
		int depth = 24;
		int status;
		do {
			status = TwoPhase.findSolution(facelets, depth - 1, 120, moves, pattern);
			depth = moves.size();
		} while (status == 0 && depth > 0);
		
		//if found a solution (status = 0 when the cube is already solved)
		if(moves.size() != 0 || status == 0) {
			Logger.log(LoggerLevel.INFO, LoggerGroup.APPLICATION, "Finish calculating moves, start solving");
			handleSolution(cube, moves);
			Logger.log(LoggerLevel.INFO, LoggerGroup.APPLICATION, "Solving cube finished");
			Robot.finishSolve();
		}
		else {
			Logger.log(LoggerLevel.INFO, LoggerGroup.APPLICATION, "Error calculating moves:");
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
			
			Logger.log(LoggerLevel.ERROR, LoggerGroup.ALGORITHM, "---> " + ((status > 0) ? "Pattern error: " : "Cube error: ") + result);
		}
	}
	
	/**
	 * Solve the cube to standard pattern (each face different color)
	 *  
	 * @see #solveCube(String pattern)
	 */
	public static void solveCube(){
		solveCube("UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB");
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
	 * @see #convertFaceColor2Orientation(Color faceColor)
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
	 * Convert  face's color to cube's orientation
	 *  
	 * @param faceColor The face's color
	 * @return The cube's orientation
	 * @see #convertOrientation2FaceColor(Orientation orientation)
	 */
	private static Orientation convertFaceColor2Orientation(Color faceColor) {
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
}
