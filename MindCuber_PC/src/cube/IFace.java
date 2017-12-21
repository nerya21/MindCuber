package cube;

import robot.Colors;
import robot.Direction;

public interface IFace {
	
	/*
	 * TODO: Documentation
	 */
	public Colors getColor(int i, int j);

	/*
	 * TODO: Documentation
	 */
	public void turn(Direction direction);
}
