package cube;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;
import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;

public class Robot {
	private static final int ARM_MOTOR_DEFAULT_SPEED = 400;
	private static final int ARM_MOTOR_FLIP_SPEED = 500;
	private static final int SENSOR_MOTOR_SPEED = 400;
	private static final int TRAY_MOTOR_ROTATION_FACTOR = 3;
	private static final int TRAY_MOTOR_EXTRA_ROTATION = 18;
	private static final int TRAY_MOTOR_STARTUP_SPEED = 500;
	private static final int SENSOR_CENTER_DEGREE = 180;
	private static final int SENSOR_OUTER_ALLIGN_DEGREE = 125;
	private static final int SENSOR_OUTER_CORNER_DEGREE = 105;
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
		RConsole.openUSB(5000);
		System.setOut(RConsole.getPrintStream());
		Arm.init();
		Tray.init();
		ColorDetector.init();
		Delay.msDelay(20);
	}

	private static class Tray {
		final static NXTRegulatedMotor motor = new NXTRegulatedMotor(MotorPort.A);

		private static void init() {
			motor.resetTachoCount();
			motor.setSpeed(TRAY_MOTOR_STARTUP_SPEED);
			//motor.setAcceleration(10000);
//			if (Tray.motor.getTachoCount() % (90 * TRAY_MOTOR_ROTATION_FACTOR) != 0) {
//				Tray.motor.rotate(-(Tray.motor.getTachoCount() % (90 * TRAY_MOTOR_ROTATION_FACTOR)));
//			}
		}
	}

	private static class Arm {
		final static NXTRegulatedMotor motor = new NXTRegulatedMotor(MotorPort.C);
		static boolean hold = false;

		private static void init() {
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
		final static NXTRegulatedMotor motor = new NXTRegulatedMotor(MotorPort.B);
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
		
		private static Colors readColor() {
			Colors color = translateColor(sensor.getColorID());
			if ((color == Colors.RED || color == Colors.ORANGE || color == Colors.YELLOW)) {
				Color rawColor = sensor.getRawColor();
				if (rawColor.getGreen() < 317) {
					color = Colors.RED;
				}
				else if (rawColor.getGreen() < 465) {
					color = Colors.ORANGE;
				} else {
					color = Colors.YELLOW;
				}
			}
			return color;
		}
	}

	public static Colors[][] scanFace() {
		Colors[][] faceColors = new Colors[3][3];
		int coordinate;
		
		for (coordinate = 0; coordinate < 8; coordinate++) {
			ColorDetector.motor.rotateTo((coordinate % 2) == 0 ? SENSOR_OUTER_ALLIGN_DEGREE : SENSOR_OUTER_CORNER_DEGREE);
			faceColors[COORDINATE_SCAN_ORDER[coordinate][0]][COORDINATE_SCAN_ORDER[coordinate][1]] = ColorDetector.readColor();
			System.out.print(faceColors[COORDINATE_SCAN_ORDER[coordinate][0]][COORDINATE_SCAN_ORDER[coordinate][1]] +" ");
			Tray.motor.rotate(TRAY_SCAN_STEP_DEGREE);
		}
		
		ColorDetector.motor.rotateTo(SENSOR_CENTER_DEGREE);
		faceColors[COORDINATE_SCAN_ORDER[coordinate][0]][COORDINATE_SCAN_ORDER[coordinate][1]] = ColorDetector.readColor();
		System.out.println(faceColors[COORDINATE_SCAN_ORDER[coordinate][0]][COORDINATE_SCAN_ORDER[coordinate][1]]);
		ColorDetector.motor.rotateTo(0);
		return faceColors;
	}

//	public static void testColorRgb() {
//		ColorDetector.motor.rotateTo(SENSOR_OUTER_ALLIGN_DEGREE);
//		Color rawColor;
//		
//		for (int i = 0; i < 100; i++) {
//			rawColor = ColorDetector.sensor.getRawColor();
//			System.out.print("(" + (rawColor.getRed() - rawColor.getBackground()) + " "
//					+ (rawColor.getGreen() - rawColor.getBackground()) + " "
//					+ (rawColor.getBlue() - rawColor.getBackground()) + ")");
//			System.out.println(
//					"(" + (rawColor.getRed()) + " " + (rawColor.getGreen()) + " " + (rawColor.getBlue()) + ")");
//		}
//		ColorDetector.motor.rotateTo(0);
//	}
	
	public static void flipCube(FlipMethod method) {
		for (int i = 0; i < method.getFlips(); i++) {
			Arm.flip();
		}
	}

	public static void turnFace(Direction direction) {
		Arm.hold();
		int extraRotation = direction.getDegree() > 0 ? TRAY_MOTOR_EXTRA_ROTATION : (-TRAY_MOTOR_EXTRA_ROTATION);
		Tray.motor.rotate((direction.getDegree() + extraRotation) * TRAY_MOTOR_ROTATION_FACTOR);
		Arm.release();
		Tray.motor.rotate((-extraRotation) * TRAY_MOTOR_ROTATION_FACTOR);
	}

	public static void rotateCube(Direction direction) {
		Tray.motor.rotate(direction.getDegree() * TRAY_MOTOR_ROTATION_FACTOR);
	}

	public static void main(String[] args) {
		Robot.init();
		for (int i = 0; i < 1; i++) {
			//flipCube(FlipMethod.SINGLE);
			turnFace(Direction.LEFT);
		}
	}
}
