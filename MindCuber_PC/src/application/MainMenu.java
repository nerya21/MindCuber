package application;

/**
 * Represents the main application menu
 * 
 * @see TestsMenu
 * @see CalibrationMenu
 * @see PatternMenu
 */
public enum MainMenu{
	SOLVE(0, "Solve Cube"),
	TESTS(1, "Tests"),
	CALIBRATION(2, "Calibration"),
	PATTERN(3, "Patterns"),
	EXIT(4, "Exit");
	
	
	private final static String title = "MindCuber";
	private final int value;
    private final String textRepresentation;    

    private MainMenu(int value, String textRepresentation) {
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
    	return new String[]{SOLVE.toString(), TESTS.toString(), CALIBRATION.toString(), PATTERN.toString() ,EXIT.toString()};
    }

	public static String getTitle() {
		return title;
	}
}