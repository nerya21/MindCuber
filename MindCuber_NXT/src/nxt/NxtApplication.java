package nxt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;
import lejos.util.Delay;

public class NxtApplication {

	/* Operation Types */
	public final static byte OPERATION_TYPE_MOTOR = (byte) 0x00;
	public final static byte OPERATION_TYPE_COLOR_SENSOR = (byte) 0x01;
	public final static byte OPERATION_TYPE_ULTRASONIC_SENSOR = (byte) 0x02;
	public final static byte OPERATION_TYPE_CLOSE_CONNECTION = (byte) 0x03;
	
	/* Operation ID */
	public final static byte OPERATION_ID_ROTATE = (byte) 0x00;
	public final static byte OPERATION_ID_ROTATE_TO = (byte) 0x01;
	public final static byte OPERATION_ID_READ_COLOR = (byte) 0x02;
	public final static byte OPERATION_ID_RESET_TACHO_COUNT = (byte) 0x03;
	public final static byte OPERATION_ID_SET_SPEED = (byte) 0x04;
	public final static byte OPERATION_ID_GET_DISTANCE = (byte) 0x05;
	public final static byte OPERATION_ID_GET_TACHO_COUNT = (byte) 0x06;

	/* Motors */
	public final static NXTRegulatedMotor[] motors = new NXTRegulatedMotor[3];

	/* Sensors */
	public final static ColorSensor colorSensor = new ColorSensor(SensorPort.S2);
	public final static UltrasonicSensor ultrasonicSensor = new UltrasonicSensor(SensorPort.S3);

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	private static void initMotors() {
		motors[0] = new NXTRegulatedMotor(MotorPort.A);
		motors[1] = new NXTRegulatedMotor(MotorPort.B);
		motors[2] = new NXTRegulatedMotor(MotorPort.C);
	}

	private static void printToLcd(String line0, int delay) {
		printToLcd(line0, "", "", "", delay, false);
	}

	private static void printToLcd(String line0, String line1, int delay) {
		printToLcd(line0, line1, "", "", delay, false);
	}

	private static void printToLcd(String line0, String line1, String line2, int delay) {
		printToLcd(line0, line1, line2, "", delay, false);
	}

	private static void printToLcd(String line0, String line1, String line2, String line3, int delay, boolean clean) {
		LCD.clear();
		LCD.drawString(line0, 0, 0);
		LCD.drawString(line1, 0, 1);
		LCD.drawString(line2, 0, 2);
		LCD.drawString(line3, 0, 3);
		Delay.msDelay(delay);
	}

	public static int[] readRgbAverage(int numberOfSamples) {
		int[] rgb = { 0, 0, 0 };
		int actualNumberOfSamples = 0;
		Color color;

		for (int i = 0; i < numberOfSamples; i++) {
			color = colorSensor.getColor();
			//printToLcd(""+color.getRed(), ""+color.getGreen(), ""+color.getBlue(), 1000);
			if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
				continue;
			}
			//if (color.getRed() != 0 && color.getGreen() != 0 && color.getBlue() != 0) {
				rgb[0] += color.getRed() > 255 ? 255 : color.getRed();
				rgb[1] += color.getGreen()> 255 ? 255 : color.getGreen();
				rgb[2] += color.getBlue()> 255 ? 255 : color.getBlue();
				actualNumberOfSamples++;
			//}
		}

		if (actualNumberOfSamples == 0) {
			printToLcd("Fatal color", "sensor error", 2000);
			return new int[] { 0, 0, 0 };
		}

		for (int rgbIndex = 0; rgbIndex < 3; rgbIndex++) {
			rgb[rgbIndex] /= actualNumberOfSamples;
		}
		
		//printToLcd(""+rgb[0], ""+rgb[1], ""+rgb[2], 1000);
		return rgb;
	}

	public static void main(String[] args) {
		Button.ESCAPE.addButtonListener(new ButtonListener() {
		      public void buttonPressed(Button b) {
		    	  System.exit(0);
		      }

		      public void buttonReleased(Button b) { }
		    });
		// Logger.init(LoggerLevel.DEBUG);
		initMotors();

		for (;;) {
			printToLcd("Waiting for", "connection", 0);
			NXTConnection con = USB.waitForConnection();
			printToLcd("Connected!", 0);
			DataInputStream in = con.openDataInputStream();
			DataOutputStream out = con.openDataOutputStream();
			byte[] inputBuffer = new byte[7];
			byte[] outputBuffer = new byte[4];
			int inputBufferLength;

			for (;;) {
				try {
					if ((inputBufferLength = in.read(inputBuffer)) > 0) {
						printToLcd("Command recieved:", bytesToHex(inputBuffer), 0);
						byte operationType = inputBuffer[0];
						int port = (int) inputBuffer[1];
						byte operationId = inputBuffer[2];

						int argument = 0;
						argument |= (((int) inputBuffer[3]) & 0x000000FF) << 24;
						argument |= (((int) inputBuffer[4]) & 0x000000FF) << 16;
						argument |= (((int) inputBuffer[5]) & 0x000000FF) << 8;
						argument |= (((int) inputBuffer[6]) & 0x000000FF) << 0;

						if (operationType == OPERATION_TYPE_MOTOR) {
							switch (operationId) {
							case OPERATION_ID_ROTATE:
								motors[port].rotate(argument);
								break;
							case OPERATION_ID_ROTATE_TO:
								motors[port].rotateTo(argument);
								break;
							case OPERATION_ID_RESET_TACHO_COUNT:
								motors[port].resetTachoCount();
								break;
							case OPERATION_ID_SET_SPEED:
								motors[port].setSpeed(argument);
								break;
							case OPERATION_ID_GET_TACHO_COUNT:								
								int tachoCount = motors[port].getTachoCount();
								outputBuffer[0] = (byte) (tachoCount >> 0);
								outputBuffer[1] = (byte) (tachoCount >> 8);
								outputBuffer[2] = (byte) (tachoCount >> 16);
								outputBuffer[3] = (byte) (tachoCount >> 24);
								break;
							default:
								printToLcd("Unsupported", "motor", "operation", 10000);
							}
						} else if (operationType == OPERATION_TYPE_COLOR_SENSOR) {
							switch (operationId) {
							case OPERATION_ID_READ_COLOR:
								int rgb[] = readRgbAverage(argument);
								outputBuffer[0] = (byte) rgb[0];
								outputBuffer[1] = (byte) rgb[1];
								outputBuffer[2] = (byte) rgb[2];
								outputBuffer[3] = (byte) 0;
								break;
							default:
								printToLcd("Unsupported", "color sensor", "operation", 10000);
							}
						} else if (operationType == OPERATION_TYPE_ULTRASONIC_SENSOR) {
							switch (operationId) {
							case OPERATION_ID_GET_DISTANCE:
								int distance = ultrasonicSensor.getDistance();
								outputBuffer[0] = (byte) (distance >> 0);
								outputBuffer[1] = (byte) (distance >> 8);
								outputBuffer[2] = (byte) (distance >> 16);
								outputBuffer[3] = (byte) (distance >> 24);
								break;
							default:
								printToLcd("Unsupported", "ultrasonic", "operation", 10000);
							}
						} else if (operationType == OPERATION_TYPE_CLOSE_CONNECTION) {
							printToLcd("Closing connection", 2000);
							con.close();
							//System.exit(0);
							break;
						} else {
							printToLcd("Unsupported", "operation type", 10000);
						}
						out.write(outputBuffer);
						out.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
