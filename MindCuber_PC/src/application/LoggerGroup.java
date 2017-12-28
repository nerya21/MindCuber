package application;

/**
 * Represents the logger's group
 * 
 * @see Logger
 */
public enum LoggerGroup{											
	ALGORITHM("Algorithm"), CUBE("Cube"), ROBOT("Robot"), APPLICATION("App  ");
	
    private final String textRepresentation;    

    private LoggerGroup(String textRepresentation) {
        this.textRepresentation = textRepresentation;
    }

    @Override
    public String toString() {
         return textRepresentation;
    }
}