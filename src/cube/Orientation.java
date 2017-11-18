package cube;

public enum Orientation {
	U(0), D(1), R(2), L(3), F(4), B(5);
	private int value;

	private Orientation(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
