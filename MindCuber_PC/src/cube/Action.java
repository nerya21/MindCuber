package cube;

import robot.Direction;
import robot.FlipMethod;

public class Action {

	public FlipMethod flips;
	public Direction direction;

	public Action(FlipMethod flips, Direction direction) {
		this.flips = flips;
		this.direction = direction;
	}
}
