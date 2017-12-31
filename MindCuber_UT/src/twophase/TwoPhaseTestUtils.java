package twophase;

import robot.Direction;
import cube.Orientation;

public class TwoPhaseTestUtils {
	
	public static final String TEST_FILES_DIR = "testsFiles"; 
	
	/**
	 * Parse argument as a byte array.
	 * The string should contain byte values separated by a comma
	 * @param str - a String containing the byte array representation to be parsed
	 * @return the byte array value represented by the argument
	 */
	public static byte[] parseByteArray(String str) {
		String[] entries = str.split(",");
		byte[] byteArray = new byte[entries.length];
		for(int i = 0; i < entries.length; i++) {
			byteArray[i] = Byte.parseByte(entries[i]);
		}
		return byteArray;
	}
	
	/**
	 * Parse argument as a Corner array.
	 * The string should contain Corner enum values separated by a comma
	 * @param str - a String containing the Corner array representation to be parsed
	 * @return the Corner array value represented by the argument
	 */
	public static Corner[] parseCornerArray(String str) {
		String[] entries = str.split(",");
		Corner[] cornerArray = new Corner[entries.length];
		for(int i = 0; i < entries.length; i++) {
			cornerArray[i] = Corner.valueOf(entries[i]);
		}
		return cornerArray;
	}
	
	/**
	 * Parse argument as a Edge array.
	 * The string should contain Edge enum values separated by a comma
	 * @param str - a String containing the Edge array representation to be parsed
	 * @return the Edge array value represented by the argument
	 */
	public static Edge[] parseEdgeArray(String str) {
		String[] entries = str.split(",");
		Edge[] edgeArray = new Edge[entries.length];
		for(int i = 0; i < entries.length; i++) {
			edgeArray[i] = Edge.valueOf(entries[i]);
		}
		return edgeArray;
	}
	
	/**
	 * Parse argument as a Color array.
	 * The string should contain Color enum values separated by a comma
	 * @param str - a String containing the Edge array representation to be parsed
	 * @return the Color array value represented by the argument
	 */
	public static Color[] parseColorsArray(String str) {
		int arraySize = str.length();
		Color[] colorArray = new Color[arraySize];
		for(int i = 0; i < str.length(); i++) {
			colorArray[i] = Color.valueOf(str.substring(i, i+1));
		}
		return colorArray;
	}
	
	/**
	 * Parse argument as a Moves array.
	 * The string should contain Orientation and Direction values separated by a comma
	 * @param str - a String containing the Moves array representation to be parsed
	 * @return the Moves array value represented by the argument
	 */
	public static Move[] parseMovesArray(String str) {
		String[] entries = str.split(",");
		Move[] movesArray = new Move[entries.length];
		for(int i = 0; i < entries.length; i++) {
			String[] moveData = entries[i].split(" ");
			movesArray[i] = new Move(Orientation.valueOf(moveData[0]), Direction.valueOf(moveData[1]));
		}
		return movesArray;
	}
}
