package robot;

/**
 * Represents the cube's flip operation
 */
public enum FlipMethod {
	SINGLE(1),
	DOUBLE(2),
	NONE(0);

	private final int flips;

	private FlipMethod(int flips) {
		this.flips = flips;
	}

	public int getFlips() {
		return flips;
	}
}