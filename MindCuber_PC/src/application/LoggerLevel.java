package application;

/**
 * Represents the logger's severity level
 * 
 * @see Logger
 */
public enum LoggerLevel{
	DEBUG(0, "Debug"), ERROR(1, "Error"), WARNING(2, "Warning"), INFO(3, "Info");
	
	private final int value;
    private final String textRepresentation;
    

    private LoggerLevel(int value, String textRepresentation) {
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
}