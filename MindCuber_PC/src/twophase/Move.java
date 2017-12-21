package twophase;

import cube.Orientation;
import robot.Direction;

public class Move {

	public Orientation orientation;
	
	public Direction direction;
	
	public Move(Orientation orientation, Direction direction) {
		this.orientation = orientation;
		this.direction = direction;
	}
}
