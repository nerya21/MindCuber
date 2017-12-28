package cube;

import robot.Direction;
import robot.FlipMethod;

/**
 * Encapsulate two robot's operations - flip and rotate
 * 
 * @see FlipMethod
 * @see Direction
 */
public class Action {

	public FlipMethod flips;
	public Direction direction;

	public Action(FlipMethod flips, Direction direction) {
		this.flips = flips;
		this.direction = direction;
	}
}
