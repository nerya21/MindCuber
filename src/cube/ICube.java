package cube;

import robot.Colors;

public interface ICube {
	
	/*
	 * TODO: Documentation
	 */
	IFace getFace(Orientation orientation);

	/*
	 * TODO: Documentation
	 */
	void setColors();
	
	void setColorsManual(Colors[][] up, Colors[][] down, Colors[][] front, Colors[][] back, Colors[][] left, Colors[][] right);
}
