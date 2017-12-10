package robot;

public enum Direction {
	LEFT(-90), RIGHT(90), MIRROR(180), NONE(0);

    private final int degree;
    private Direction(int degree) {
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }
    
    public Direction mirror() {
    	switch (this) {
	    	case LEFT:
				return RIGHT;
			case RIGHT:
				return LEFT;			
			default:
				return this;
		}
    }
}
