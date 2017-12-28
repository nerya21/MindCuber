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

public class Cube implements ICube {

	private ColorCorrector data = new ColorCorrector();
	private Action[] actions;

	private static final Orientation[][] ORIENTATION_MAT = {
			{ Orientation.D, Orientation.L, Orientation.F, Orientation.U, Orientation.R, Orientation.B },
			{ Orientation.L, Orientation.D, Orientation.B, Orientation.R, Orientation.U, Orientation.F },
			{ Orientation.L, Orientation.F, Orientation.D, Orientation.R, Orientation.B, Orientation.U },
			{ Orientation.U, Orientation.R, Orientation.F, Orientation.D, Orientation.L, Orientation.B },
			{ Orientation.L, Orientation.U, Orientation.F, Orientation.R, Orientation.D, Orientation.B },
			{ Orientation.L, Orientation.B, Orientation.U, Orientation.R, Orientation.F, Orientation.D } };

	public Cube() {
		
		data.faces = new Face[6];
		for (Orientation orientation : Orientation.values()) {
			data.faces[orientation.getValue()] = new Face(orientation);
		}
		
		actions = new Action[6];
		actions[Orientation.U.getValue()] = new Action(FlipMethod.DOUBLE, Direction.NONE);
		actions[Orientation.D.getValue()] = new Action(FlipMethod.NONE, Direction.NONE);
		actions[Orientation.R.getValue()] = new Action(FlipMethod.SINGLE, Direction.MIRROR);
		actions[Orientation.L.getValue()] = new Action(FlipMethod.SINGLE, Direction.NONE);
		actions[Orientation.F.getValue()] = new Action(FlipMethod.SINGLE, Direction.LEFT);
		actions[Orientation.B.getValue()] = new Action(FlipMethod.SINGLE, Direction.RIGHT);
	}

	public Face getFace(Orientation orientation) {
		return data.faces[orientation.getValue()];
	}

	private void updateOrientations(Orientation orientation) {
		Orientation[] newOrientations = ORIENTATION_MAT[orientation.getValue()];
		for (int i = 0; i < 6; i++) {
			data.faces[i].dynamicOrientation = newOrientations[data.faces[i].dynamicOrientation.getValue()];
		}
	}

	private void changePosition(FlipMethod cube_flips, Direction direction, Orientation orientation) {
		Robot.rotateCube(direction);
		Robot.flipCube(cube_flips);
		updateOrientations(orientation);
	}

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
	
	private void calcAndSetColors(ArrayList<RawColor> allColors) {
		setWhitesByDistance(allColors);
		setNonWhitesByHue(allColors);
		fixCorners();
		printCubeColorsToLogger(allColors);
	}

	private void printCubeColorsToLogger(ArrayList<RawColor> allColors) {
		for (Orientation orientation: Orientation.values()) {
			for (int row = 0; row < 3; row++) {
				for (int col = 0; col< 3; col++) {
					Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, orientation + "[" + row + "][" + col + "]: " + data.faces[orientation.getValue()].getColor(row, col));
				}
			}
		}
		
	}

	private void setWhitesByDistance(ArrayList<RawColor> allColors) {
		Collections.sort(allColors, RawColor.whiteComparator);
		for (int colorIndex = 0; colorIndex < 9; colorIndex++) {
			RawColor color = allColors.get(0);
			data.faces[color.orientation.getValue()].setColor(color.row, color.col, Colors.WHITE);
			allColors.remove(0);
		}
	}
	
	private void setNonWhitesByHue(ArrayList<RawColor> allColors) {
		Collections.sort(allColors, RawColor.hueComparator);
		for (int colorIndex = 0; colorIndex < 9; colorIndex++) {
			RawColor color = allColors.get(0);
			data.faces[color.orientation.getValue()].setColor(color.row, color.col, Colors.RED);
			allColors.remove(0);
		}
		for (int colorIndex = 0; colorIndex < 9; colorIndex++) {
			RawColor color = allColors.get(0);
			data.faces[color.orientation.getValue()].setColor(color.row, color.col, Colors.ORANGE);
			allColors.remove(0);
		}
		for (int colorIndex = 0; colorIndex < 9; colorIndex++) {
			RawColor color = allColors.get(0);
			data.faces[color.orientation.getValue()].setColor(color.row, color.col, Colors.YELLOW);
			allColors.remove(0);
		}
		for (int colorIndex = 0; colorIndex < 9; colorIndex++) {
			RawColor color = allColors.get(0);
			data.faces[color.orientation.getValue()].setColor(color.row, color.col, Colors.GREEN);
			allColors.remove(0);
		}
		for (int colorIndex = 0; colorIndex < 9; colorIndex++) {
			RawColor color = allColors.get(0);
			data.faces[color.orientation.getValue()].setColor(color.row, color.col, Colors.BLUE);
			allColors.remove(0);
		}
	}
	
	public void setColorsManual(Colors[][] up, Colors[][] down, Colors[][] front, Colors[][] back, Colors[][] left, Colors[][] right){
		data.faces[Orientation.U.getValue()].colors = up;
		data.faces[Orientation.D.getValue()].colors = down;
		data.faces[Orientation.R.getValue()].colors = right;
		data.faces[Orientation.L.getValue()].colors = left;
		data.faces[Orientation.F.getValue()].colors = front;
		data.faces[Orientation.B.getValue()].colors = back;
	}
	
	public class Face implements IFace {
		
		final Orientation orientation;
		private Colors[][] colors;		
		private Orientation dynamicOrientation;

		public Face(Orientation orientation) {
			this.orientation = orientation;
			this.dynamicOrientation = orientation;
			this.colors = new Colors[3][3];
		}
 
		@Override
		public Colors getColor(int i, int j) {
			return colors[i][j];
		}

		void setColor(int i, int j, Colors color) {
			colors[i][j] = color;
		}
		
		@Override
		public void turn(Direction direction) {
			
			FlipMethod cubeFlips = actions[dynamicOrientation.getValue()].flips;			
			Direction cubeRotation = actions[dynamicOrientation.getValue()].direction;
	
			changePosition(cubeFlips, cubeRotation, dynamicOrientation);
			
			Robot.turnFace(direction);
		}
	}

	
	private static final Colors[][] RED_ORANGE_CORNERS = {
			{Colors.YELLOW, Colors.RED, Colors.BLUE},
			{Colors.WHITE, Colors.RED, Colors.GREEN},
			{Colors.BLUE, Colors.RED, Colors.WHITE},
			{Colors.GREEN, Colors.RED, Colors.YELLOW},
			{Colors.BLUE, Colors.ORANGE, Colors.YELLOW},
			{Colors.WHITE, Colors.ORANGE, Colors.BLUE},
			{Colors.YELLOW, Colors.ORANGE, Colors.GREEN},				
			{Colors.GREEN, Colors.ORANGE, Colors.WHITE} };	
	
	private static final Corner[][] LEFT_CORNERS_CORNER = {
			/*U*/ {Corner.UPPER_RIGHT, Corner.UPPER_RIGHT, Corner.UPPER_RIGHT, Corner.UPPER_RIGHT}, 
			/*R*/ {Corner.LOWER_RIGHT, Corner.UPPER_LEFT, Corner.LOWER_RIGHT, Corner.LOWER_RIGHT},
			/*F*/ {Corner.LOWER_LEFT, Corner.UPPER_LEFT, Corner.LOWER_RIGHT, Corner.UPPER_RIGHT},
			/*D*/ {Corner.LOWER_LEFT, Corner.LOWER_LEFT, Corner.LOWER_LEFT, Corner.LOWER_LEFT},
			/*L*/ {Corner.UPPER_LEFT, Corner.UPPER_LEFT, Corner.LOWER_RIGHT, Corner.UPPER_LEFT},
			/*B*/ {Corner.UPPER_RIGHT, Corner.UPPER_LEFT, Corner.LOWER_RIGHT, Corner.LOWER_LEFT} };
	
	private static final Orientation[][] LEFT_CORNERS_FACE = {
			/*U*/ {Orientation.B, Orientation.R, Orientation.L ,Orientation.F}, 
			/*R*/ {Orientation.U, Orientation.B, Orientation.F ,Orientation.D},
			/*F*/ {Orientation.U, Orientation.R, Orientation.L ,Orientation.D},
			/*D*/ {Orientation.F, Orientation.R, Orientation.L ,Orientation.B},
			/*L*/ {Orientation.U, Orientation.F, Orientation.B ,Orientation.D},
			/*B*/ {Orientation.U, Orientation.L, Orientation.R ,Orientation.D} };
	
	private static final Corner[][] RIGHT_CORNERS_CORNER = {
			/*U*/ {Corner.UPPER_LEFT, Corner.UPPER_LEFT, Corner.UPPER_LEFT, Corner.UPPER_LEFT}, 
			/*R*/ {Corner.UPPER_RIGHT, Corner.UPPER_RIGHT, Corner.UPPER_RIGHT, Corner.LOWER_LEFT},
			/*F*/ {Corner.UPPER_RIGHT, Corner.LOWER_RIGHT, Corner.UPPER_LEFT, Corner.LOWER_LEFT},
			/*D*/ {Corner.LOWER_RIGHT, Corner.LOWER_RIGHT, Corner.LOWER_RIGHT, Corner.LOWER_RIGHT},
			/*L*/ {Corner.UPPER_RIGHT, Corner.LOWER_LEFT, Corner.LOWER_LEFT, Corner.LOWER_LEFT},
			/*B*/ {Corner.UPPER_RIGHT, Corner.UPPER_LEFT, Corner.LOWER_RIGHT, Corner.LOWER_LEFT} };

	private static final Orientation[][] RIGHT_CORNERS_FACE = {
			/*U*/ {Orientation.L, Orientation.B, Orientation.F ,Orientation.R}, 
			/*R*/ {Orientation.F, Orientation.U, Orientation.D ,Orientation.B},
			/*F*/ {Orientation.L, Orientation.U, Orientation.D ,Orientation.R},
			/*D*/ {Orientation.L, Orientation.F, Orientation.B ,Orientation.R},
			/*L*/ {Orientation.B, Orientation.U, Orientation.D ,Orientation.F},
			/*B*/ {Orientation.R, Orientation.U, Orientation.D ,Orientation.L} };

	private Colors getCornerLeftColor(Face face, Corner corner) {
		Orientation leftFace = LEFT_CORNERS_FACE[face.orientation.getValue()][corner.getValue()];
		Corner leftCorner = LEFT_CORNERS_CORNER[face.orientation.getValue()][corner.getValue()];
		
		return data.faces[leftFace.getValue()].getColor(leftCorner.getRow(), leftCorner.getCol());
	}

	private Colors getCornerRightColor(Face face, Corner corner) {
		Orientation rightFace = RIGHT_CORNERS_FACE[face.orientation.getValue()][corner.getValue()];
		Corner rightCorner = RIGHT_CORNERS_CORNER[face.orientation.getValue()][corner.getValue()];
		
		return data.faces[rightFace.getValue()].getColor(rightCorner.getRow(), rightCorner.getCol());
	}
	
	private enum Corner {
		UPPER_LEFT(0, 0, 0), 
		UPPER_RIGHT(1, 0, 2),
		LOWER_LEFT(2, 2, 0),
		LOWER_RIGHT(3, 2, 2);
		
		private final int value;
		private final int row;
		private final int col;
		
		private Corner(int value, int row, int col) {
			this.value = value;
			this.row = row;
			this.col = col;
		}

		public int getValue() {
			return value;
		}
		
		public int getRow() {
			return row;
		}
		
		public int getCol() {
			return col;
		}
	}

	/**
	 * @param cube 
	 * 
	 */
	void fixCorners() {
		boolean cubeFixed = false;
		Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Scanning cube for RED/ORANGE corner color error:");
		for (Orientation orientation : Orientation.values()) {
			Face face = getFace(orientation);
			
			for (Corner corner : Corner.values()) {
				if (face.getColor(corner.row, corner.col) == Colors.RED || face.getColor(corner.row, corner.col) == Colors.ORANGE) {
					Colors left = getCornerLeftColor(face, corner);
					Colors right = getCornerRightColor(face, corner);
					
					for (Colors[] cornerColors : RED_ORANGE_CORNERS) {
						if (left == cornerColors[0] && right == cornerColors[2]) {
							Colors scannedColor = face.getColor(corner.row, corner.col);
							Colors cornerColor = cornerColors[1];
							if (scannedColor != cornerColor) {
								Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "---> " + face.orientation + "[" + corner.row + "][" + corner.col + "]: fixing " + scannedColor + " to " + cornerColor);
								face.setColor(corner.row, corner.col, cornerColor);
							}
						}
					}
				}
			}
		}
		
		if (!cubeFixed) {
			Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "---> No error found!");
		}
	}

}
