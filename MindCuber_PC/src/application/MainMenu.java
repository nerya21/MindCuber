package application;

/**
 * MainMenu Enum
 * 
 * Represents the main application menu
 */
public enum MainMenu{
	SOLVE(0, "Solve Cube"),
	RCONSOLE(1, "RConsole"),
	TESTS(2, "Tests"),
	CALIBRATION(3, "Calibration"),
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
    	return new String[]{SOLVE.toString(), RCONSOLE.toString(), TESTS.toString(), CALIBRATION.toString(), EXIT.toString()};
    }

	public static String getTitle() {
		return title;
	}
}