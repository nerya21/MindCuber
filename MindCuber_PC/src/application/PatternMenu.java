package application;

import java.util.regex.Pattern;

/**
 * CalibrationMenu Enum
 * 
 * Represents the calibration sub-menu
 */
public enum PatternMenu {
	PLUS_MINUS(0, "Plus minus", "UUUUUUUUULLLRRRLLLBFBFFFBFBDDDDDDDDDRRRLLLRRRFBFBBBFBF"),
	CROSS_4(1, "4 cross", "UUUUUUUUULRLRRRLRLBFBFFFBFBDDDDDDDDDRLRLLLRLRFBFBBBFBF"),
	CUBE_CUBE(2, "Cube cube", "FFFFUUFUURRURRUUUURFFRFFRRRBBBDDBDDBDDDLLDLLDLLLLBBLBB"),
	CUBE_CUBE_CUBE(3, "Cube cube cube", "RRRRUURUFURFRRFFFFUFRUFFUUULLLDDLBDLBBBLLBDLBDDDDBBDBL"),
	BACK(4, "Back" , "");

	private final static String title = "Pattern";
	private final int value;
	private final String textRepresentation;
	private final String pattern;

	private PatternMenu(int value, String textRepresentation, String pattern) {
		this.value = value;
		this.textRepresentation = textRepresentation;
		this.pattern = pattern;
	}

	@Override
	public String toString() {
		return textRepresentation;
	}

	int getValue() {
		return value;
	}

	public static String[] getItems() {
		return new String[] { PLUS_MINUS.toString(), CROSS_4.toString(), CUBE_CUBE.toString(), CUBE_CUBE_CUBE.toString(), BACK.toString() };
	}

	public String getPattern() {
		return pattern;
	}
	
	public static String getTitle() {
		return title;
	}
}