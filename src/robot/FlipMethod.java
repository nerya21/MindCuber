package robot;

/**
 * Represents the flip operation, SINGLE for one flip, DOUBLE for two and NONE
 * for no operation
 */
public enum FlipMethod {
	SINGLE(1), DOUBLE(2), NONE(0);

	private final int flips;

	private FlipMethod(int flips) {
		this.flips = flips;
	}

	public int getFlips() {
		return flips;
	}
}