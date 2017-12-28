package cube;

/**
 * Represents the cube's orientation
 */
public enum Orientation {	
	U(0),
	R(1),
	F(2),
	D(3),
	L(4),
	B(5);
	
	private int value;

	private Orientation(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
