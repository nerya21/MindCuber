package application;

/**
 * TestsMenu Enum
 * 
 * Represents the tests sub-menu
 */
public enum TestsMenu {
	BRUTEFORCE(0, "Brute Force"),
	COLOR(1, "Read Color"),
	BACK(2, "Back");

	private final static String title = "Tests";
	private final int value;
	private final String textRepresentation;

	private TestsMenu(int value, String textRepresentation) {
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
		return new String[] { BRUTEFORCE.toString(), COLOR.toString(), BACK.toString() };
	}

	public static String getTitle() {
		return title;
	}
}