package cube;

import application.Logger;
import application.LoggerGroup;
import application.LoggerLevel;
import cube.Cube.Face;
import robot.Colors;

/**
 * This class intended to correct color sensors error.
 * <br>The NXT's color sensor has difficulties to distinguish between
 * <b>Red</b> and <b>Orange</b> colors. Since the colors at the cube's corners
 * can be determine by its neighbors, we can detect and correct any Red/Orange
 * mix up at the corners.
 * <p>This is how the class represent the corner:
 * <pre>
 *    +--------+--------+
 *    |        |        |
 *    | corner | right  |
 *    |        |        |
 *    |        |        |
 *    +--------+--------+
 *    |        |
 *    |  left  |
 *    |        |
 *    |        |
 *    +--------+</pre>
 * <b>Whereas each of the squares above belong to another cube's face</b>
 */
public class ColorCorrector {
	
	/* All of the Red/Orange cube's corners */
	private static final Colors[][] RED_ORANGE_CORNERS = {
			/*   Left      |    Corner    |    Right    */
			{Colors.YELLOW,	 Colors.RED, 	Colors.BLUE},
			{Colors.WHITE,	 Colors.RED, 	Colors.GREEN},
			{Colors.BLUE,	 Colors.RED, 	Colors.WHITE},
			{Colors.GREEN,	 Colors.RED, 	Colors.YELLOW},
			{Colors.BLUE,	 Colors.ORANGE, Colors.YELLOW},
			{Colors.WHITE,	 Colors.ORANGE, Colors.BLUE},
			{Colors.YELLOW,	 Colors.ORANGE, Colors.GREEN},				
			{Colors.GREEN,	 Colors.ORANGE, Colors.WHITE} };
	
	/* Given our current face (row) and type of corner (column) 
	 * it tell us in which corner is the Left of this corner */
	private static final Corner[][] LEFT_CORNERS_CORNER = {
			/*       UPPER_LEFT       |    UPPER_RIGHT    |    LOWER_LEFT     |    LOWER_RIGHT    */
			/*U*/ {Corner.UPPER_RIGHT, 	Corner.UPPER_RIGHT, Corner.UPPER_RIGHT, Corner.UPPER_RIGHT}, 
			/*R*/ {Corner.LOWER_RIGHT, 	Corner.UPPER_LEFT, 	Corner.LOWER_RIGHT, Corner.LOWER_RIGHT},
			/*F*/ {Corner.LOWER_LEFT, 	Corner.UPPER_LEFT, 	Corner.LOWER_RIGHT, Corner.UPPER_RIGHT},
			/*D*/ {Corner.LOWER_LEFT, 	Corner.LOWER_LEFT, 	Corner.LOWER_LEFT, 	Corner.LOWER_LEFT},
			/*L*/ {Corner.UPPER_LEFT, 	Corner.UPPER_LEFT, 	Corner.LOWER_RIGHT, Corner.UPPER_LEFT},
			/*B*/ {Corner.UPPER_RIGHT, 	Corner.UPPER_LEFT,	Corner.LOWER_RIGHT, Corner.LOWER_LEFT} };
	
	/* Given our current face (row) and type of corner (column) 
	 * it tell us in which face is the Left of this corner */
	private static final Orientation[][] LEFT_CORNERS_FACE = {
			/*      UPPER_LEFT    | UPPER_RIGHT  | LOWER_LEFT   | LOWER_RIGHT */
			/*U*/ {Orientation.B, Orientation.R, Orientation.L ,Orientation.F}, 
			/*R*/ {Orientation.U, Orientation.B, Orientation.F ,Orientation.D},
			/*F*/ {Orientation.U, Orientation.R, Orientation.L ,Orientation.D},
			/*D*/ {Orientation.F, Orientation.R, Orientation.L ,Orientation.B},
			/*L*/ {Orientation.U, Orientation.F, Orientation.B ,Orientation.D},
			/*B*/ {Orientation.U, Orientation.L, Orientation.R ,Orientation.D} };
	
	/* Given our current face (row) and type of corner (column) 
	 * it tell us in which corner is the Right of this corner */
	private static final Corner[][] RIGHT_CORNERS_CORNER = {
			/*       UPPER_LEFT       |    UPPER_RIGHT    |    LOWER_LEFT     |    LOWER_RIGHT    */
			/*U*/ {Corner.UPPER_LEFT,  Corner.UPPER_LEFT,  Corner.UPPER_LEFT,  Corner.UPPER_LEFT}, 
			/*R*/ {Corner.UPPER_RIGHT, Corner.UPPER_RIGHT, Corner.UPPER_RIGHT, Corner.LOWER_LEFT},
			/*F*/ {Corner.UPPER_RIGHT, Corner.LOWER_RIGHT, Corner.UPPER_LEFT,  Corner.LOWER_LEFT},
			/*D*/ {Corner.LOWER_RIGHT, Corner.LOWER_RIGHT, Corner.LOWER_RIGHT, Corner.LOWER_RIGHT},
			/*L*/ {Corner.UPPER_RIGHT, Corner.LOWER_LEFT,  Corner.LOWER_LEFT,  Corner.LOWER_LEFT},
			/*B*/ {Corner.UPPER_RIGHT, Corner.UPPER_LEFT,  Corner.LOWER_RIGHT, Corner.LOWER_LEFT} };

	/* Given our current face (row) and type of corner (column) 
	 * it tell us in which face is the Right of this corner */
	private static final Orientation[][] RIGHT_CORNERS_FACE = {
			/*      UPPER_LEFT    | UPPER_RIGHT  | LOWER_LEFT   | LOWER_RIGHT */
			/*U*/ {Orientation.L, Orientation.B, Orientation.F ,Orientation.R}, 
			/*R*/ {Orientation.F, Orientation.U, Orientation.D ,Orientation.B},
			/*F*/ {Orientation.L, Orientation.U, Orientation.D ,Orientation.R},
			/*D*/ {Orientation.L, Orientation.F, Orientation.B ,Orientation.R},
			/*L*/ {Orientation.B, Orientation.U, Orientation.D ,Orientation.F},
			/*B*/ {Orientation.R, Orientation.U, Orientation.D ,Orientation.L} };

	/**
	 * This enumeration represent all corners of cube's face:
	 * <pre>
     * +--------+--------+--------+
     * |        |        |        |
     * | UPPER  |        | UPPER  |
     * | LEFT   |        | RIGHT  |
     * |        |        |        |
     * +--------+--------+--------+
     * |        |        |        |
     * |        |        |        |
     * |        |        |        |
     * |        |        |        |
     * +--------+--------+--------+
     * |        |        |        |
     * | LOWER  |        | LOWER  |
     * | LEFT   |        | RIGHT  |
     * |        |        |        |
     * +--------+--------+--------+</pre>
	 */
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
	 * Get the left color given the corner
	 * 
	 * @param faces All cube's faces
	 * @param face Current face we're handling
	 * @param corner For which corner we want its left 
	 * @return The color to the left of the corner
	 * @see Corner
	 */
	private static Colors getCornerLeftColor(Face[] faces, Face face, Corner corner) {
		Orientation leftFace = LEFT_CORNERS_FACE[face.orientation.getValue()][corner.getValue()];
		Corner leftCorner = LEFT_CORNERS_CORNER[face.orientation.getValue()][corner.getValue()];
		
		return faces[leftFace.getValue()].getColor(leftCorner.getRow(), leftCorner.getCol());
	}

	/**
	 * Get the right color given the corner
	 * 
	 * @param faces All cube's faces
	 * @param face Current face we're handling
	 * @param corner For which corner we want its right 
	 * @return The color to the right of the corner
	 * @see Corner
	 */
	private static Colors getCornerRightColor(Face[] faces, Face face, Corner corner) {
		Orientation rightFace = RIGHT_CORNERS_FACE[face.orientation.getValue()][corner.getValue()];
		Corner rightCorner = RIGHT_CORNERS_CORNER[face.orientation.getValue()][corner.getValue()];
		
		return faces[rightFace.getValue()].getColor(rightCorner.getRow(), rightCorner.getCol());
	}

	/**
	 * The fix routine.
	 * <p>The method goes over all the cube's Red/Orange corners
	 * and fix them according to their neighbors
	 * @param faces All cube's faces 
	 */
	static void fixCorners(Face[] faces) {
		boolean cubeFixed = false;
		Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Scanning cube for RED/ORANGE corner color error:");
		for (Orientation orientation : Orientation.values()) {
			Face face = faces[orientation.getValue()];
			
			for (Corner corner : Corner.values()) {
				if (face.getColor(corner.row, corner.col) == Colors.RED || face.getColor(corner.row, corner.col) == Colors.ORANGE) {
					Colors left = getCornerLeftColor(faces, face, corner);
					Colors right = getCornerRightColor(faces, face, corner);
					
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
