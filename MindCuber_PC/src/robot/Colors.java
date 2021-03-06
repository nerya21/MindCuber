package robot;

/**
 * Represent the colors of the rubik's cube
 */
public enum Colors {
	WHITE(0), 
	YELLOW(1),
	RED(2),
	BLUE(3),
	GREEN(4),
	ORANGE(5);
	
	private final int color;
	
    private Colors(int color) {
    	this.color = color;
    }
    
    public int getValue() {
        return color;
    }
}
