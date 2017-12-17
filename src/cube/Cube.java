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
		Robot.Tray.setSpeed(200);
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
		Robot.Tray.setSpeed(500);
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
		
		private Colors[][] _colors;
		
		private Orientation _dynamic_orientation;

		public Face(Orientation orientation) {
			this._dynamic_orientation = orientation;
			this._colors = new Colors[3][3];
		}

		@Override
		public Colors getColor(int i, int j) {
			return _colors[i][j];
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

}
