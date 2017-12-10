package robot;

/**
 * Represents the Color Sensor location, 
 */
public enum SensorLocation {
	
	private  final int SENSOR_CENTER_DEFAULT_DEGREE = 170;
	private static final int SENSOR_OUTER_ALLIGN_DEFAULT_DEGREE = 115;
	private static final int SENSOR_OUTER_CORNER_DEFAULT_DEGREE = 95;
	CENTER(0, ), ALLIGN(1, ), CORNER(2, );

	private final int value;
	private int degree;

	private SensorLocation(int value) {
		this.value = value;
		this.degree = degree;
	}

	public int getValue() {
		return value;
	}
}