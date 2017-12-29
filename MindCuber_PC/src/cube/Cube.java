package cube;

import java.util.ArrayList;
import java.util.Collections;

import application.Logger;
import application.LoggerGroup;
import application.LoggerLevel;
import robot.Colors;
import robot.Direction;
import robot.FlipMethod;
import robot.Robot;

/**
 * 
 * TODO Elad
 *
 */
public class Cube implements ICube {

	protected Face[] faces;
	private Action[] actions;

	/**
	 * TODO Elad
	 */
	private static final Orientation[][] ORIENTATION_MAT = {
			{ Orientation.D, Orientation.L, Orientation.F, Orientation.U, Orientation.R, Orientation.B },
			{ Orientation.L, Orientation.D, Orientation.B, Orientation.R, Orientation.U, Orientation.F },
			{ Orientation.L, Orientation.F, Orientation.D, Orientation.R, Orientation.B, Orientation.U },
			{ Orientation.U, Orientation.R, Orientation.F, Orientation.D, Orientation.L, Orientation.B },
			{ Orientation.L, Orientation.U, Orientation.F, Orientation.R, Orientation.D, Orientation.B },
			{ Orientation.L, Orientation.B, Orientation.U, Orientation.R, Orientation.F, Orientation.D } };

	Colors[] COLORS_ORDERERD_BY_HUE = {Colors.RED, Colors.ORANGE, Colors.YELLOW, Colors.GREEN, Colors.BLUE};
	
	/**
	 * TODO Elad
	 */
	public Cube() {
		
		faces = new Face[6];
		for (Orientation orientation : Orientation.values()) {
			faces[orientation.getValue()] = new Face(orientation);
		}
		
		actions = new Action[6];
		actions[Orientation.U.getValue()] = new Action(FlipMethod.DOUBLE, Direction.NONE);
		actions[Orientation.D.getValue()] = new Action(FlipMethod.NONE, Direction.NONE);
		actions[Orientation.R.getValue()] = new Action(FlipMethod.SINGLE, Direction.MIRROR);
		actions[Orientation.L.getValue()] = new Action(FlipMethod.SINGLE, Direction.NONE);
		actions[Orientation.F.getValue()] = new Action(FlipMethod.SINGLE, Direction.LEFT);
		actions[Orientation.B.getValue()] = new Action(FlipMethod.SINGLE, Direction.RIGHT);
	}

	/**
	 * TODO Elad
	 * 
	 * @param orientation
	 */
	public Face getFace(Orientation orientation) {
		return faces[orientation.getValue()];
	}

	/**
	 * 
	 * @param orientation
	 */
	private void updateOrientations(Orientation orientation) {
		Orientation[] newOrientations = ORIENTATION_MAT[orientation.getValue()];
		for (int i = 0; i < 6; i++) {
			faces[i].dynamicOrientation = newOrientations[faces[i].dynamicOrientation.getValue()];
		}
	}

	/**
	 * TODO Elad
	 * 
	 * @param cubeFlips
	 * @param direction
	 * @param orientation
	 */
	private void changePosition(FlipMethod cubeFlips, Direction direction, Orientation orientation) {
		Robot.rotateCube(direction);
		Robot.flipCube(cubeFlips);
		updateOrientations(orientation);
	}

	/**
	 * Scan and set the cube colors.
	 * 
	 * <p>The method is first to scan all of the cube's colors in RGB mode,
	 * and calculate their HSV representation as well as their distance from
	 * the calibrated white RGB.
	 * <br>Second, sort all the colors by their white distance (since white doesn't have
	 * meaningful Hue value), and place them on the cube.
	 * <br>Last, sort all the colors by their Hue value and place them on the cube
	 * according to their Hue value.
	 */
	public void setColors() {
		ArrayList<RawColor> allColors = new ArrayList<RawColor>();
		
		Robot.setTrayScanSpeed();
		
		Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Scanning face: " + Orientation.F);
		Robot.scanFace(allColors, Orientation.F);
		
		Robot.flipCube(FlipMethod.SINGLE);		
		Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Scanning face: " + Orientation.R);
		Robot.scanFace(allColors, Orientation.R);
		
		Robot.flipCube(FlipMethod.SINGLE);		
		Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Scanning face: " + Orientation.B);
		Robot.scanFace(allColors, Orientation.B);
		
		Robot.flipCube(FlipMethod.SINGLE);		
		Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Scanning face: " + Orientation.L);
		Robot.scanFace(allColors, Orientation.L);
		
		Robot.rotateCube(Direction.RIGHT);
		Robot.flipCube(FlipMethod.SINGLE);		
		Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Scanning face: " + Orientation.D);
		Robot.scanFace(allColors, Orientation.D);
		
		Robot.flipCube(FlipMethod.DOUBLE);
		Robot.rotateCube(Direction.MIRROR);		
		Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Scanning face: " + Orientation.U);
		Robot.scanFace(allColors, Orientation.U);
		
		Robot.setTrayDefaultSpeed();
		
		calcAndSetColors(allColors);
	}
	
	/**
	 * Place all white colors on the cube according to their
	 * distance from the calibrated white RGB
	 * 
	 * @param allColors All scanned colors
	 * @see #setColors()
	 */
	private void setWhitesByDistance(ArrayList<RawColor> allColors) {
		Collections.sort(allColors, RawColor.whiteComparator);
		for (int colorIndex = 0; colorIndex < 9; colorIndex++) {
			RawColor color = allColors.get(0);
			faces[color.orientation.getValue()].setColor(color.row, color.col, Colors.WHITE);
			allColors.remove(0);
		}
	}
	
	/**
	 * Place all non-white colors on the cube according to their
	 * Hue value
	 * 
	 * @param allColors All scanned colors
	 * @see #setColors()
	 */
	private void setNonWhitesByHue(ArrayList<RawColor> allColors) {
		Collections.sort(allColors, RawColor.hueComparator);
		
		for (int colorIndex = 0; colorIndex < 45; colorIndex++) {
			RawColor color = allColors.get(0);
			faces[color.orientation.getValue()].setColor(color.row, color.col, COLORS_ORDERERD_BY_HUE[colorIndex / 9]);
			allColors.remove(0);
		}
	}
	
	/**
	 * Place all scanned colors on the cube, fix red/orange corners
	 * and print result to logger 
	 * 
	 * @param allColors All scanned colors
	 */
	private void calcAndSetColors(ArrayList<RawColor> allColors) {
		setWhitesByDistance(allColors);
		setNonWhitesByHue(allColors);
		ColorCorrector.fixCorners(faces);
		printCubeColorsToLogger();
	}

	/**
	 * Print all cube's colors to logger
	 */
	private void printCubeColorsToLogger() {
		for (Orientation orientation: Orientation.values()) {
			for (int row = 0; row < 3; row++) {
				for (int col = 0; col< 3; col++) {
					Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, 
							orientation + "[" + row + "][" + col + "]: " + faces[orientation.getValue()].getColor(row, col));
				}
			}
		}		
	}
	
	/**
	 * TODO Elad
	 * 
	 * @param up
	 * @param down
	 * @param front
	 * @param back
	 * @param left
	 * @param right
	 */
	public void setColorsManual(Colors[][] up, Colors[][] down, Colors[][] front, Colors[][] back, Colors[][] left, Colors[][] right){
		faces[Orientation.U.getValue()].colors = up;
		faces[Orientation.D.getValue()].colors = down;
		faces[Orientation.R.getValue()].colors = right;
		faces[Orientation.L.getValue()].colors = left;
		faces[Orientation.F.getValue()].colors = front;
		faces[Orientation.B.getValue()].colors = back;
	}
	
	/**
	 * TODO Elad
	 *
	 */
	public class Face implements IFace {
		
		final Orientation orientation;
		private Colors[][] colors;		
		private Orientation dynamicOrientation;

		/**
		 * TODO elad
		 * 
		 * @param orientation
		 */
		public Face(Orientation orientation) {
			this.orientation = orientation;
			this.dynamicOrientation = orientation;
			this.colors = new Colors[3][3];
		}
 
		/**
		 * TODO Elad
		 * 
		 * @param row
		 * @param col
		 */
		@Override
		public Colors getColor(int row, int col) {
			return colors[row][col];
		}

		/**
		 * TODO Elad
		 * 
		 * @param row
		 * @param col
		 */
		void setColor(int row, int col, Colors color) {
			colors[row][col] = color;
		}
		
		/**
		 * TODO Elad
		 * 
		 * @param direction
		 */
		@Override
		public void turn(Direction direction) {
			
			FlipMethod cubeFlips = actions[dynamicOrientation.getValue()].flips;			
			Direction cubeRotation = actions[dynamicOrientation.getValue()].direction;
	
			changePosition(cubeFlips, cubeRotation, dynamicOrientation);
			
			Robot.turnFace(direction);
		}
	}

}
