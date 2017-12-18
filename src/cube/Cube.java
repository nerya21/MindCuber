package cube;

import application.Logger;
import application.LoggerGroup;
import application.LoggerLevel;
import robot.Colors;
import robot.Direction;
import robot.FlipMethod;
import robot.Robot;

public class Cube implements ICube {

	private Face[] _faces;

	private Action[] _actions;

	private static final Orientation[][] ORIENTATION_MAT = {
			{ Orientation.D, Orientation.U, Orientation.R, Orientation.L, Orientation.B, Orientation.F },
			{ Orientation.U, Orientation.D, Orientation.R, Orientation.L, Orientation.F, Orientation.B },
			{ Orientation.F, Orientation.B, Orientation.D, Orientation.U, Orientation.L, Orientation.R },
			{ Orientation.F, Orientation.B, Orientation.U, Orientation.D, Orientation.R, Orientation.L },
			{ Orientation.F, Orientation.B, Orientation.R, Orientation.L, Orientation.D, Orientation.U },
			{ Orientation.F, Orientation.B, Orientation.L, Orientation.R, Orientation.B, Orientation.D } };

	public Cube() {
		
		_faces = new Face[6];
		for (Orientation orientation : Orientation.values()) {
			_faces[orientation.getValue()] = new Face(orientation);
		}
//		_faces[0] = new Face(Orientation.U);
//		_faces[1] = new Face(Orientation.D);
//		_faces[2] = new Face(Orientation.R);
//		_faces[3] = new Face(Orientation.L);
//		_faces[4] = new Face(Orientation.F);
//		_faces[5] = new Face(Orientation.B);
		
		_actions = new Action[6];
		_actions[0] = new Action(FlipMethod.DOUBLE, Direction.NONE);
		_actions[1] = new Action(FlipMethod.NONE, Direction.NONE);
		_actions[2] = new Action(FlipMethod.SINGLE, Direction.RIGHT);
		_actions[3] = new Action(FlipMethod.SINGLE, Direction.LEFT);
		_actions[4] = new Action(FlipMethod.SINGLE, Direction.NONE);
		_actions[5] = new Action(FlipMethod.SINGLE, Direction.MIRROR);
	}

	public Face getFace(Orientation orientation) {
		return _faces[orientation.getValue()];
	}

	private void updateOrientations(Orientation orientation) {
		Orientation[] newOrientations = ORIENTATION_MAT[orientation.getValue()];
		for (int i = 0; i < 6; i++) {
			_faces[i]._dynamic_orientation = newOrientations[_faces[i]._dynamic_orientation.getValue()];
		}
	}

	private void changePosition(FlipMethod cube_flips, Direction direction, Orientation orientation) {
		Robot.rotateCube(direction);
		Robot.flipCube(cube_flips);
		updateOrientations(orientation);
	}

	public void setColors() {
		Robot.setTrayScanSpeed();
		
		Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Scanning face: " + Orientation.F);
		_faces[Orientation.F.getValue()]._colors = Robot.scanFace();
		
		Robot.flipCube(FlipMethod.SINGLE);		
		Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Scanning face: " + Orientation.R);
		_faces[Orientation.R.getValue()]._colors = Robot.scanFace();
		
		Robot.flipCube(FlipMethod.SINGLE);		
		Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Scanning face: " + Orientation.B);
		_faces[Orientation.B.getValue()]._colors = Robot.scanFace();
		
		Robot.flipCube(FlipMethod.SINGLE);		
		Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Scanning face: " + Orientation.L);
		_faces[Orientation.L.getValue()]._colors = Robot.scanFace();
		
		Robot.rotateCube(Direction.RIGHT);
		Robot.flipCube(FlipMethod.SINGLE);		
		Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Scanning face: " + Orientation.D);
		_faces[Orientation.D.getValue()]._colors = Robot.scanFace();
		
		Robot.flipCube(FlipMethod.DOUBLE);
		Robot.rotateCube(Direction.MIRROR);		
		Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Scanning face: " + Orientation.U);
		_faces[Orientation.U.getValue()]._colors = Robot.scanFace();
		
		Robot.setTrayDefaultSpeed();
		
		fixColors();
	}
	
	public void setColorsManual(Colors[][] up, Colors[][] down, Colors[][] front, Colors[][] back, Colors[][] left, Colors[][] right){
		_faces[Orientation.U.getValue()]._colors = up;
		_faces[Orientation.D.getValue()]._colors = down;
		_faces[Orientation.R.getValue()]._colors = right;
		_faces[Orientation.L.getValue()]._colors = left;
		_faces[Orientation.F.getValue()]._colors = front;
		_faces[Orientation.B.getValue()]._colors = back;
	}
	
	public class Face implements IFace {
		
		private final Orientation orientation;
		private Colors[][] _colors;		
		private Orientation _dynamic_orientation;

		public Face(Orientation orientation) {
			this.orientation = orientation;
			this._dynamic_orientation = orientation;
			this._colors = new Colors[3][3];
		}
 
		@Override
		public Colors getColor(int i, int j) {
			return _colors[i][j];
		}

		private void setColor(int i, int j, Colors color) {
			_colors[i][j] = color;
		}
		
		@Override
		public void turn(Direction direction) {
			
			FlipMethod cubeFlips = _actions[_dynamic_orientation.getValue()].flips;			
			Direction cubeRotation = _actions[_dynamic_orientation.getValue()].direction;
			
			// If a cube flip is required, we must mirror the face turning direction:
			if (cubeFlips != FlipMethod.NONE){
				cubeRotation = cubeRotation.mirror();
			}
			
			changePosition(cubeFlips, cubeRotation, _dynamic_orientation);
			
			Robot.turnFace(direction);
		}
	}

	private static final Corner[][] LEFT_CORNERS_CORNER = {/*U*/ {Corner.UPPER_RIGHT, Corner.UPPER_RIGHT, Corner.UPPER_RIGHT, Corner.UPPER_RIGHT}, 
															/*R*/ {Corner.LOWER_RIGHT, Corner.UPPER_LEFT, Corner.LOWER_RIGHT, Corner.LOWER_RIGHT},
															/*F*/ {Corner.LOWER_LEFT, Corner.UPPER_LEFT, Corner.LOWER_RIGHT, Corner.UPPER_RIGHT},
															/*D*/ {Corner.LOWER_LEFT, Corner.LOWER_LEFT, Corner.LOWER_LEFT, Corner.LOWER_LEFT},
															/*L*/ {Corner.UPPER_LEFT, Corner.UPPER_LEFT, Corner.LOWER_RIGHT, Corner.UPPER_LEFT},
															/*B*/ {Corner.UPPER_RIGHT, Corner.UPPER_LEFT, Corner.LOWER_RIGHT, Corner.LOWER_LEFT}};
	
	private static final Orientation[][] LEFT_CORNERS_FACE = {/*U*/ {Orientation.B, Orientation.R, Orientation.L ,Orientation.F}, 
																/*R*/ {Orientation.U, Orientation.B, Orientation.F ,Orientation.D},
																/*F*/ {Orientation.U, Orientation.R, Orientation.L ,Orientation.D},
																/*D*/ {Orientation.F, Orientation.R, Orientation.L ,Orientation.B},
																/*L*/ {Orientation.U, Orientation.F, Orientation.B ,Orientation.D},
																/*B*/ {Orientation.U, Orientation.L, Orientation.R ,Orientation.D}};
	
	private static final Corner[][] RIGHT_CORNERS_CORNER = {/*U*/ {Corner.UPPER_LEFT, Corner.UPPER_LEFT, Corner.UPPER_LEFT, Corner.UPPER_LEFT}, 
															/*R*/ {Corner.UPPER_RIGHT, Corner.UPPER_RIGHT, Corner.UPPER_RIGHT, Corner.LOWER_LEFT},
															/*F*/ {Corner.UPPER_RIGHT, Corner.LOWER_RIGHT, Corner.UPPER_LEFT, Corner.LOWER_LEFT},
															/*D*/ {Corner.LOWER_RIGHT, Corner.LOWER_RIGHT, Corner.LOWER_RIGHT, Corner.LOWER_RIGHT},
															/*L*/ {Corner.UPPER_RIGHT, Corner.LOWER_LEFT, Corner.LOWER_LEFT, Corner.LOWER_LEFT},
															/*B*/ {Corner.UPPER_RIGHT, Corner.UPPER_LEFT, Corner.LOWER_RIGHT, Corner.LOWER_LEFT}};

	private static final Orientation[][] RIGHT_CORNERS_FACE = {/*U*/ {Orientation.L, Orientation.B, Orientation.F ,Orientation.R}, 
																/*R*/ {Orientation.F, Orientation.U, Orientation.D ,Orientation.B},
																/*F*/ {Orientation.L, Orientation.U, Orientation.D ,Orientation.R},
																/*D*/ {Orientation.L, Orientation.F, Orientation.B ,Orientation.R},
																/*L*/ {Orientation.B, Orientation.U, Orientation.D ,Orientation.F},
																/*B*/ {Orientation.R, Orientation.U, Orientation.D ,Orientation.L}};

	public Colors getCornerLeftColor(Face face, Corner corner) {
		Orientation leftFace = LEFT_CORNERS_FACE[face.orientation.getValue()][corner.getValue()];
		Corner leftCorner = LEFT_CORNERS_CORNER[face.orientation.getValue()][corner.getValue()];
		
		return _faces[leftFace.getValue()].getColor(leftCorner.getRow(), leftCorner.getCol());
	}

	public Colors getCornerRightColor(Face face, Corner corner) {
		Orientation rightFace = RIGHT_CORNERS_FACE[face.orientation.getValue()][corner.getValue()];
		Corner rightCorner = RIGHT_CORNERS_CORNER[face.orientation.getValue()][corner.getValue()];
		
		return _faces[rightFace.getValue()].getColor(rightCorner.getRow(), rightCorner.getCol());
	}
	
	private enum Corner {
		UPPER_LEFT(0, 0, 0), UPPER_RIGHT(1, 0, 2), LOWER_LEFT(2, 2, 0), LOWER_RIGHT(3, 2, 2);
		
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
	
	private static final Colors[][] RED_ORANGE_CORNERS = {{Colors.YELLOW, Colors.RED, Colors.BLUE},
															{Colors.WHITE, Colors.RED, Colors.GREEN},
															{Colors.BLUE, Colors.RED, Colors.WHITE},
															{Colors.GREEN, Colors.RED, Colors.YELLOW},
															{Colors.BLUE, Colors.ORANGE, Colors.YELLOW},
															{Colors.WHITE, Colors.ORANGE, Colors.BLUE},
															{Colors.YELLOW, Colors.ORANGE, Colors.GREEN},				
															{Colors.GREEN, Colors.ORANGE, Colors.WHITE}};  
	
	/**
	 * 
	 */
	private void fixColors() {
		Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Scanning cube for RED/ORANGE color error");
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
								Logger.log(LoggerLevel.INFO, LoggerGroup.CUBE, "Color error detected at " + face.orientation + "[" + corner.row + "][" + corner.col + "]: fixing " + scannedColor + " to " + cornerColor);
								face.setColor(corner.row, corner.col, cornerColor);
							}
						}
					}
				}
			}
		}
	}
}
