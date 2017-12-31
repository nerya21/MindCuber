package nxt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;
import lejos.util.Delay;

/**
 * The main application of the <i>NXT project</i>
 * <p>The application receives commands from the PC application
 * and act upon them
 */
public class NxtApplication extends NxtOperation {

	/* Motors */
	private final static NXTRegulatedMotor[] motors = new NXTRegulatedMotor[3];

	/* Sensors */
	private final static ColorSensor colorSensor = new ColorSensor(SensorPort.S2);
	private final static UltrasonicSensor ultrasonicSensor = new UltrasonicSensor(SensorPort.S3);

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	/**
	 * Convert byte array to Hexadecimal
	 * 
	 * @param bytes The byte array to print as Hexadecimal
	 * @return Hexadecimal representation of the bytes array
	 */
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	 * Initialize NXT's motors
	 */
	private static void initMotors() {
		motors[0] = new NXTRegulatedMotor(MotorPort.A);
		motors[1] = new NXTRegulatedMotor(MotorPort.B);
		motors[2] = new NXTRegulatedMotor(MotorPort.C);
	}

	/**
	 * Print to LCD
	 * 
	 * @param line0 1st line to print
	 * @param delay Delay time in MS after the print
	 */
	private static void printToLcd(String line0, int delay) {
		printToLcd(line0, "", "", "", delay);
	}

	/**
	 * Print to LCD
	 * 
	 * @param line0 1st line to print
	 * @param line1 2nd line to print
	 * @param delay Delay time in MS after the print
	 */
	private static void printToLcd(String line0, String line1, int delay) {
		printToLcd(line0, line1, "", "", delay);
	}

	/**
	 * Print to LCD
	 * 
	 * @param line0 1st line to print
	 * @param line1 2nd line to print
	 * @param line2 3rd line to print
	 * @param delay Delay time in MS after the print
	 */
	private static void printToLcd(String line0, String line1, String line2, int delay) {
		printToLcd(line0, line1, line2, "", delay);
	}

	/**
	 * Print to LCD
	 * 
	 * @param line0 1st line to print
	 * @param line1 2nd line to print
	 * @param line2 3rd line to print
	 * @param line3 4th line to print
	 * @param delay Delay time in MS after the print
	 */
	private static void printToLcd(String line0, String line1, String line2, String line3, int delay) {
		LCD.clear();
		LCD.drawString(line0, 0, 0);
		LCD.drawString(line1, 0, 1);
		LCD.drawString(line2, 0, 2);
		LCD.drawString(line3, 0, 3);
		Delay.msDelay(delay);
	}

	/**
	 * Ass listener to the ESC button
	 */
	private static void addEscButtonListener() {
		Button.ESCAPE.addButtonListener(new ButtonListener() {
		      public void buttonPressed(Button b) {
		    	  System.exit(0);
		      }

		      public void buttonReleased(Button b) { }
		    });
	}
	
	/**
	 * Read color from sensor
	 * 
	 * @param numberOfSamples Number of reading to perform
	 * @return RGB array, each of the first three values between 0 to 255, last value is background
	 */
	public static int[] readRgbAverage(int numberOfSamples) {
		int[] rgb = { 0, 0, 0, 0 };
		int actualNumberOfSamples = 0;
		Color color;

		for (int i = 0; i < numberOfSamples; i++) {
			color = colorSensor.getColor();
			if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
				continue;
			}
			rgb[0] += color.getRed() > 255 ? 255 : color.getRed();
			rgb[1] += color.getGreen()> 255 ? 255 : color.getGreen();
			rgb[2] += color.getBlue()> 255 ? 255 : color.getBlue();
			rgb[3] += color.getBackground();
			actualNumberOfSamples++;
		}

		if (actualNumberOfSamples == 0) {
			printToLcd("Fatal color", "sensor error", 2000);
			return new int[] { 0, 0, 0, 0 };
		}

		for (int rgbIndex = 0; rgbIndex < 4; rgbIndex++) {
			rgb[rgbIndex] /= actualNumberOfSamples;
		}
		
		return rgb;
	}

	/**
	 * Run the main loop waiting for command from PC
	 * <p>This function exits upon pressing the ESC button on the NXT
	 */
	private static void runCommandListener() {
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
						if (inputBufferLength != 7) {
							printToLcd("Unknown data", "received", 10000);
							continue;
						}
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
							case OPERATION_ID_FORWARD:
								motors[port].forward();
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
								outputBuffer[3] = (byte) rgb[3];
								break;
							case OPERATION_ID_READ_COLOR_ID:								
								int colorId = colorSensor.getColorID();
								outputBuffer[0] = (byte) (colorId >> 0);
								outputBuffer[1] = (byte) (colorId >> 8);
								outputBuffer[2] = (byte) (colorId >> 16);
								outputBuffer[3] = (byte) (colorId >> 24);
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
							printToLcd("Closing connection", 0);
							con.close();
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

	/**
	 * Main function of the NXT application
	 */
	public static void main(String[] args) {
		addEscButtonListener();		
		initMotors();
		runCommandListener();
	}
}
