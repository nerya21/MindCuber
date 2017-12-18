package robot;

public enum Direction {
	NONE(0),RIGHT(90),MIRROR(180),LEFT(-90);

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
