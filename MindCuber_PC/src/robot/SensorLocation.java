package robot;

/**
 * Represents the color sensor location
 */
public enum SensorLocation {
	CENTER(0),
	ALLIGN(1), 
	CORNER(2);

	private final int value;

	private SensorLocation(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}