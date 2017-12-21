package cube;

import java.util.Comparator;

import application.Logger;
import application.LoggerGroup;
import application.LoggerLevel;
import robot.Robot;

public class RawColor {

	static final int[] WHITE_DEFAULT_RGB_CALIBRATION = { 255, 255, 255 };
	static int[] whiteRgbCalibration = WHITE_DEFAULT_RGB_CALIBRATION;

	public RawColor(Orientation orientation, int row, int col, int[] rawColor) {
		this.orientation = orientation;
		this.row = row;
		this.col = col;

		this.red = rawColor[0];
		this.green = rawColor[1];
		this.blue = rawColor[2];

		double[] hsv = rgbToHsv(rawColor);
		hue = hsv[0] + 60;
		saturation = hsv[1];
		value = hsv[2];

		whiteDistance = calcRgbDistance(Robot.ColorDetector.whiteThreshold, rawColor);
	}

	Orientation orientation;
	public int row;
	public int col;

	public int red;
	public int green;
	public int blue;

	public double hue;
	public double saturation;
	public double value;

	public int whiteDistance;

	public static Comparator<RawColor> whiteComparator = new Comparator<RawColor>() {

		public int compare(RawColor color1, RawColor color2) {
			return color1.whiteDistance - color2.whiteDistance;
		}
	};

	public static Comparator<RawColor> hueComparator = new Comparator<RawColor>() {

		public int compare(RawColor color1, RawColor color2) {
			//TODO
			return (color1.hue > color2.hue) ? 1 : -1;
		}
	};
	
	private static int calcRgbDistance(int[] rgb1, int[] rgb2) {
		int distance = 0;
		for (int rgbIndex = 0; rgbIndex < 3; rgbIndex++) {
			distance += Math.pow((rgb1[rgbIndex] - rgb2[rgbIndex]), 2);
		}

		return distance;
	}

	private static double[] rgbToHsv(int[] rgb) {
		double[] rgbNorm = { (double) rgb[0] / 255, (double) rgb[1] / 255, (double) rgb[2] / 255 };
		double cMax = 0, cMin = 1;
		double cDelta;

		/* Calc cMin, cMax and cDelta */
		for (int baseColor = 0; baseColor < 3; baseColor++) {
			if (rgbNorm[baseColor] < cMin) {
				cMin = rgbNorm[baseColor];
			}
			if (rgbNorm[baseColor] > cMax) {
				cMax = rgbNorm[baseColor];
			}
		}
		cDelta = cMax - cMin;

		/* Calc Hue */
		double hue;
		if (cDelta == 0) {
			hue = 0;
		} else if (cMax == rgbNorm[0]) {
			hue = 60 * (((rgbNorm[1] - rgbNorm[2]) / cDelta) % 6);
		} else if (cMax == rgbNorm[1]) {
			hue = 60 * (((rgbNorm[2] - rgbNorm[0]) / cDelta) + 2);
		} else if (cMax == rgbNorm[2]) {
			hue = 60 * (((rgbNorm[0] - rgbNorm[1]) / cDelta) + 4);
		} else {
			hue = 0;
			// handle error
		}

		/* Calc Saturation */
		double saturation;
		if (cMax == 0) {
			saturation = 0;
		} else {
			saturation = cDelta / cMax;
		}

		/* Calc Value */
		double value = cMax;

		/* Handle error */
		if (hue == 0 || saturation == 0 || value == 0) {
			Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "HSV calculation error");
		}

		return new double[] { hue, saturation, value };
	}
}
