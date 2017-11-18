package cube;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.remote.RemoteMotor;
import lejos.util.Delay;
import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;

public class Robot {
	private static final boolean PC = true;
	private static final int ARM_MOTOR_DEFAULT_SPEED = 400;
	private static final int ARM_MOTOR_FLIP_SPEED = 500;
	private static final int ARM_MOTOR_STARTUP_DEGREE = 0;
	private static final int SENSOR_MOTOR_SPEED = 400;
	private static final int SENSOR_MOTOR_DEGREE = 0;
	private static final int TRAY_MOTOR_ROTATION_FACTOR = 3;
	private static final int TRAY_MOTOR_EXTRA_ROTATION = 18;
	private static final int TRAY_MOTOR_STARTUP_SPEED = 500;
	private static final int SENSOR_CENTER_DEGREE = 195;
	private static final int SENSOR_OUTER_ALLIGN_DEGREE = 140;
	private static final int SENSOR_OUTER_CORNER_DEGREE = 120;
	private static final int TRAY_SCAN_STEP_DEGREE = 135;
	
	private static final int[][] COORDINATE_SCAN_ORDER = { { 1, 2 }, { 2, 2 }, { 2, 1 }, { 2, 0 }, { 1, 0 }, { 0, 0 },
			{ 0, 1 }, { 0, 2 }, { 1, 1 } };

	public enum FlipMethod {
		SINGLE(1), DOUBLE(2), NONE(0);

		private final int flips;

		private FlipMethod(int flips) {
			this.flips = flips;
		}

		public int getFlips() {
			return flips;
		}
	}

	public static void init() {
		ColorDetector.motor.rotateTo(0);
		Tray.motor.rotateTo(0);
		Arm.init();
		Tray.init();
		ColorDetector.init();
		Delay.msDelay(20);
	}

	private static class Tray {
		final static RemoteMotor motor = Motor.A;

		private static void init() {
			motor.resetTachoCount();
			motor.setPower(100);
			Arm.motor.setSpeed(TRAY_MOTOR_STARTUP_SPEED);
//			if (Tray.motor.getTachoCount() % (90 * TRAY_MOTOR_ROTATION_FACTOR) != 0) {
//				Tray.motor.rotate(-(Tray.motor.getTachoCount() % (90 * TRAY_MOTOR_ROTATION_FACTOR)));
//			}
		}
	}

	private static class Arm {
		final static RemoteMotor motor = Motor.C;
		static boolean hold = false;

		private static void init() {
			motor.setPower(100);
			motor.setSpeed(ARM_MOTOR_DEFAULT_SPEED);
			motor.resetTachoCount();
		}

		private static void flip() {
			motor.setSpeed(ARM_MOTOR_FLIP_SPEED);
			motor.rotate(-160);
			motor.rotate(-50);
			motor.rotate(210);
		}

		private static void hold() {
			hold = true;
			motor.setSpeed(ARM_MOTOR_DEFAULT_SPEED);
			motor.rotate(-160);
		}

		private static void release() {
			if (hold) {
				hold = false;
				motor.setSpeed(ARM_MOTOR_DEFAULT_SPEED);
				motor.rotate(160);
			}
		}
	}

	private static class ColorDetector {
		final static RemoteMotor motor = Motor.B;
		final static ColorSensor sensor = new ColorSensor(SensorPort.S2);

		private static void init() {
			motor.setSpeed(SENSOR_MOTOR_SPEED);
			motor.resetTachoCount();
		}

		private static Colors translateColor(int color) {
			switch (color) {
			case 0:
				return Colors.RED;
			case 1:
				return Colors.GREEN;
			case 2:
				return Colors.BLUE;
			case 3:
				return Colors.YELLOW;
			case 5:
				return Colors.ORANGE;
			case 6:
				return Colors.WHITE;
			default:
				return Colors.NONE;
			}
		}
		
		@SuppressWarnings("unused")
		private static Colors readColor() {
			Colors color = translateColor(sensor.getColorID());
//			if ((!PC) && (color == Colors.RED || color == Colors.ORANGE)) {
//				Color rawColor = sensor.getRawColor();
//				if (512*(rawColor.getGreen()-rawColor.getBackground())/(rawColor.getBlue()-rawColor.getBackground()) > 675){
//					color = Colors.ORANGE;
//				}
//				else {
//					color = Colors.RED;
//				}
//			}
			return color;
		}
	}

	public static Colors[][] scanFace() {
		Colors[][] faceColors = new Colors[3][3];
		int coordinate;
		
		for (coordinate = 0; coordinate < 8; coordinate++) {
			ColorDetector.motor.rotateTo((coordinate % 2) == 0 ? SENSOR_OUTER_ALLIGN_DEGREE : SENSOR_OUTER_CORNER_DEGREE);
			faceColors[COORDINATE_SCAN_ORDER[coordinate][0]][COORDINATE_SCAN_ORDER[coordinate][1]] = ColorDetector.readColor();
			System.out.println(faceColors[COORDINATE_SCAN_ORDER[coordinate][0]][COORDINATE_SCAN_ORDER[coordinate][1]]);
			Tray.motor.rotate(TRAY_SCAN_STEP_DEGREE);
		}
		
		ColorDetector.motor.rotateTo(SENSOR_CENTER_DEGREE);
		faceColors[COORDINATE_SCAN_ORDER[coordinate][0]][COORDINATE_SCAN_ORDER[coordinate][1]] = ColorDetector.readColor();
		System.out.println(faceColors[COORDINATE_SCAN_ORDER[coordinate][0]][COORDINATE_SCAN_ORDER[coordinate][1]]);
		ColorDetector.motor.rotateTo(0);
		return faceColors;
	}

	public static void testColorRgb() {
		ColorDetector.motor.rotateTo(SENSOR_OUTER_ALLIGN_DEGREE);
		Color rawColor;
		
		for (int i = 0; i < 20; i++) {
			rawColor = ColorDetector.sensor.getRawColor();
			System.out.print("(" + (rawColor.getRed() - rawColor.getBackground()) + " "
					+ (rawColor.getGreen() - rawColor.getBackground()) + " "
					+ (rawColor.getBlue() - rawColor.getBackground()) + ")");
			System.out.print(
					"(" + (rawColor.getRed()) + " " + (rawColor.getGreen()) + " " + (rawColor.getBlue()) + ")");
		}
		ColorDetector.motor.rotateTo(0);
	}
	
	public static void flipCube(FlipMethod method) {
		for (int i = 0; i < method.getFlips(); i++) {
			Arm.flip();
		}
	}

	public static void turnFace(Direction direction) {
		Arm.hold();
		Delay.msDelay(0);
		int extraRotation = direction.getDegree() > 0 ? TRAY_MOTOR_EXTRA_ROTATION : (-TRAY_MOTOR_EXTRA_ROTATION);
		Tray.motor.rotate((direction.getDegree() + extraRotation) * TRAY_MOTOR_ROTATION_FACTOR);
		Delay.msDelay(0);
		Arm.release();
		Tray.motor.rotate((-extraRotation) * TRAY_MOTOR_ROTATION_FACTOR);
	}

	public static void rotateCube(Direction direction) {
		Tray.motor.rotate(direction.getDegree() * TRAY_MOTOR_ROTATION_FACTOR);
	}

	public static void main(String[] args) {
		for (int i = 0; i < 5000; i++) {
			//init();
			ColorDetector.sensor.getColorID();
		}
	}
}
