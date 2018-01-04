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
 * This class represent the Rubik's cube itself as an object.
 * <br>It has 6 objects of the type Face and a the Action suitable for every face.
 */
public class Cube implements ICube {

	protected Face[] faces;
	private Action[] actions;

	/**
	 * This is a static orientation matrix that represent the new position of every face
	 * <br> whenever any face is brought to be at the bottom (DOWN orientation).
	 * <br>The faces are listed in the following order:
	 * <br>0- Up, 1- Right, 2- Front, 3- Down, 4- Left, 5- Back
	 * <br> Example of use:
	 * <br> Suppose we need to bring the current FRONT face to the bottom (to be the DOWN face),
	 * <br> in order to know how this action affects the other faces, we should go to the 2nd row of
	 * <br> the matrix (as listed above: 2- FRONT), and we will have the new positions of the other faces:
	 * <br> position 0 has the value L -> indicates that the UP face (0 = UP) becomes the LEFT face.
	 * <br> position 1 has the value F -> indicates that the RIGHT face (1 = RIGHT) becomes the FRONT face, etc.
	 * <br> in conclusion we got these transforms: U->L , R->F , F->D , D->R, L->B , B->U
	 * <br> this matrix help us to determine the positions of the faces in any transition.
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
	 * Constructor of an object of the type Cube.
	 * <br>It initializes the faces with all the orientations, and initializes the table of actions.
	 * <br>Table of actions: represents the actions needed to do in order to bring some face to
	 * <br>the bottom (to be the DOWN face).
	 * <br>Example: in order to bring the FRONT face to the bottom,
	 * <br>we have to rotate it to the left, and then perform a flip.
	 * <br>This action is stored on the actions array at position 2 (2 = FRONT)
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
	 * returns the face which is currently in the orientation requested
	 * 
	 * @param orientation The dynamic orientation, the face that currently in this orientation
	 */
	public Face getFace(Orientation orientation) {
		return faces[orientation.getValue()];
	}

	/**
	 * Updates the new orientations of the faces after a transition of the cube.
	 * <br>It uses the static matrix ORIENTATION_MAT in order to get the new orientations.
	 * 
	 * @param orientation The current orientation of the face we bring to the bottom (to be the DOWN face)
	 */
	void updateOrientations(Orientation orientation) {
		Orientation[] newOrientations = ORIENTATION_MAT[orientation.getValue()];
		for (int i = 0; i < 6; i++) {
			faces[i].dynamicOrientation = newOrientations[faces[i].dynamicOrientation.getValue()];
		}
	}

	/**
	 * Move the face from its current orientation to the bottom (to be the DOWN face).
	 * 
	 * @param cubeFlips The number of flips the robot should perform
	 * @param direction The direction the robot should rotate the cube
	 * @param orientation The orientation of the face that goes to the bottom (to be the DOWN face)
	 */
	private void changePosition(FlipMethod cubeFlips, Direction direction, Orientation orientation) {
		Robot.rotateCube(direction);
		Robot.flipCube(cubeFlips);
		updateOrientations(orientation);
	}

	/**
	 * Scan and set the cube colors
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
	 * Set the colors of every face of the cube manually
	 * 
	 * @param up A matrix of colors represents the UP face
	 * @param down A matrix of colors represents the DOWN face
	 * @param front A matrix of colors represents the FRONT face
	 * @param back A matrix of colors represents the BACK face
	 * @param left A matrix of colors represents the LEFT face
	 * @param right A matrix of colors represents the RIGHT face
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
	 * This class represents a face of the Rubik's cube
	 * <br>An object of this class has an initial orientation which is final,
	 * <br>a dynamic orientation which can change every transition,
	 * <br>and a matrix of colors represent the colors on this face in the initial state.
	 */
	public class Face implements IFace {
		
		final Orientation orientation;
		private Colors[][] colors;		
		Orientation dynamicOrientation;

		/**
		 * Constructor of an object of the type face.
		 * <br>It initializes the final and dynamic orientation and the matrix of colors.
		 * 
		 * @param orientation The initial orientation of the face
		 */
		public Face(Orientation orientation) {
			this.orientation = orientation;
			this.dynamicOrientation = orientation;
			this.colors = new Colors[3][3];
		}
 
		/**
		 * Returns the color of a position in a face
		 * 
		 * @param row Row in a face
		 * @param col Column in a face
		 */
		@Override
		public Colors getColor(int row, int col) {
			return colors[row][col];
		}

		/**
		 * Sets a color to a position in a face
		 * 
		 * @param row Row in a face
		 * @param col Column in a face
		 */
		void setColor(int row, int col, Colors color) {
			colors[row][col] = color;
		}
		
		/**
		 * Represents a turn of the face in a desired direction.
		 * <br>It uses the dynamic orientation and the actions table to derive
		 * <br>the number of flips and rotation needed in order to bring this face to the bottom.
		 * <br>after this face is brought to the bottom, it is turn in the desired direction.
		 * 
		 * @param direction The direction that the face should turn
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
