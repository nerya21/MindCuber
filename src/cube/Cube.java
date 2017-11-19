package cube;

import cube.Robot.FlipMethod;

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
		_faces[0] = new Face(Orientation.U);
		_faces[1] = new Face(Orientation.D);
		_faces[2] = new Face(Orientation.R);
		_faces[3] = new Face(Orientation.L);
		_faces[4] = new Face(Orientation.F);
		_faces[5] = new Face(Orientation.B);
		_actions = new Action[6];
		_actions[0] = new Action(Robot.FlipMethod.DOUBLE, Direction.NONE);
		_actions[1] = new Action(Robot.FlipMethod.NONE, Direction.NONE);
		_actions[2] = new Action(Robot.FlipMethod.SINGLE, Direction.RIGHT);
		_actions[3] = new Action(Robot.FlipMethod.SINGLE, Direction.LEFT);
		_actions[4] = new Action(Robot.FlipMethod.SINGLE, Direction.NONE);
		_actions[5] = new Action(Robot.FlipMethod.SINGLE, Direction.MIRROR);
	}

	public Face getFace(Orientation orientation) {
		return _faces[orientation.getValue()];
	}

	private void updateOrientations(Orientation orientation) {
		Orientation[] dynamicChange = ORIENTATION_MAT[orientation.getValue()];
		for (int i = 0; i < 6; i++) {
			_faces[i]._dynamic_orientation = dynamicChange[_faces[i]._dynamic_orientation.getValue()];
		}
	}

	private void changePosition(FlipMethod cube_flips, Direction direction, Orientation orientation) {
		Robot.rotateCube(direction);
		Robot.flipCube(cube_flips);
		updateOrientations(orientation);
	}

	public void setColors() {
		_faces[Orientation.F.getValue()]._colors = Robot.scanFace();
		Robot.flipCube(FlipMethod.SINGLE);
		_faces[Orientation.R.getValue()]._colors = Robot.scanFace();
		Robot.flipCube(FlipMethod.SINGLE);
		_faces[Orientation.B.getValue()]._colors = Robot.scanFace();
		Robot.flipCube(FlipMethod.SINGLE);
		_faces[Orientation.L.getValue()]._colors = Robot.scanFace();
		Robot.rotateCube(Direction.RIGHT);
		Robot.flipCube(FlipMethod.SINGLE);
		_faces[Orientation.D.getValue()]._colors = Robot.scanFace();
		Robot.flipCube(FlipMethod.DOUBLE);
		Robot.rotateCube(Direction.MIRROR);
		_faces[Orientation.U.getValue()]._colors = Robot.scanFace();
	}

	public class Action {
		public Robot.FlipMethod flips;
		public Direction direction;

		public Action(Robot.FlipMethod flips, Direction direction) {
			this.flips = flips;
			this.direction = direction;
		}
	}

	public class Face implements IFace {
		private Colors[][] _colors;
		private Orientation _dynamic_orientation;

		public Face(Orientation current) {
			this._dynamic_orientation = current;
			this._colors = new Colors[3][3];
		}

		public Colors getColor(int i, int j) {
			return _colors[i][j];
		}

		public void turn(Direction direction) {
			FlipMethod cube_flips = _actions[_dynamic_orientation.getValue()].flips;
			Direction cube_rotation = _actions[_dynamic_orientation.getValue()].direction;
			changePosition(cube_flips, cube_rotation, _dynamic_orientation);
			Robot.turnFace(direction);
		}
	}

	// public static void main(String[] args) {
	// Robot.init();
	// Cube cube = new Cube();
	// cube.setColors();
	// }
}