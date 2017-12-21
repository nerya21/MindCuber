package application;

public enum LoggerGroup{
	ALGORITHM("Algorithm"), CUBE("Cube"), ROBOT("Robot"), APPLICATION("Application");
	
    private final String textRepresentation;    

    private LoggerGroup(String textRepresentation) {
        this.textRepresentation = textRepresentation;
    }

    @Override
    public String toString() {
         return textRepresentation;
    }
}