package application;

/**
 * Represents the calibration sub-menu
 * <p>Used for executing calibration routines
 * 
 * @see MainMenu
 */
public enum CalibrationMenu {
	SENSOR(0, "Color Sensor"),
	COLOR_MOTOR(1, "Color Motor"),
	TRAY(2, "Cube Tray"),
	PROXIMITY(3, "Proximity"),
	COLOR_LIGHT(4, "Color Light"),
	BACK(5, "Back");

	private final static String title = "Calibration";
	private final int value;
	private final String textRepresentation;

	private CalibrationMenu(int value, String textRepresentation) {
		this.value = value;
		this.textRepresentation = textRepresentation;
	}

	@Override
	public String toString() {
		return textRepresentation;
	}

	int getValue() {
		return value;
	}

	public static String[] getItems() {
		return new String[] { SENSOR.toString(), COLOR_MOTOR.toString(), TRAY.toString(), PROXIMITY.toString(), COLOR_LIGHT.toString(), BACK.toString() };
	}

	public static String getTitle() {
		return title;
	}
}