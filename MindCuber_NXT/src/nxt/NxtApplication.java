package nxt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
	public final static byte OPERATION_TYPE_MOTOR = (byte)0x00;
	public final static byte OPERATION_TYPE_COLOR_SENSOR = (byte)0x01;
	public final static byte OPERATION_TYPE_ULTRASONIC_SENSOR = (byte)0x02;

	/* Operation ID */
	public final static byte OPERATION_ID_ROTATE = (byte)0x00;
	public final static byte OPERATION_ID_ROTATE_TO = (byte)0x01;
	public final static byte OPERATION_ID_READ_COLOR = (byte)0x02;
	public final static byte OPERATION_ID_RESET_TACHO_COUNT = (byte)0x03;
	public final static byte OPERATION_ID_SET_SPEED = (byte)0x04;
	public final static byte OPERATION_ID_GET_DISTANCE = (byte)0x05;
	
	private static void printToLcd(String line0, int delay) {
		printToLcd(line0, "", "", "", delay);
	}
	
	private static void printToLcd(String line0, String line1, String line2, String line3, int delay) {
		LCD.clear();
		LCD.drawString(line0, 0, 0);
		LCD.drawString(line1, 0, 1);
		LCD.drawString(line2, 0, 2);
		LCD.drawString(line3, 0, 3);
		Delay.msDelay(delay);
	}
	
	@SuppressWarnings("null")
	public static void main(String[] args) {
		final NXTRegulatedMotor[] motors = new NXTRegulatedMotor[3];
		motors[0] =  new NXTRegulatedMotor(MotorPort.A);
		motors[1] =  new NXTRegulatedMotor(MotorPort.B);
		motors[2] =  new NXTRegulatedMotor(MotorPort.C);
		
		final ColorSensor colorSensor = new ColorSensor(SensorPort.S2);
		final UltrasonicSensor ultrasonicSensor = new UltrasonicSensor(SensorPort.S3);
		
		for(;;) {
			printToLcd("wait for con", 200);
			NXTConnection con = USB.waitForConnection();
			printToLcd("connected", 500);
			DataInputStream in = con.openDataInputStream();
			DataOutputStream out = con.openDataOutputStream();
			byte[] inputBuffer = null;
			int inputBufferLength;			
			
			try {
				while ((inputBufferLength = in.read(inputBuffer)) > 0) {
					byte[] outputBuffer = null;
					byte operationType = inputBuffer[0];
					int port = (int)inputBuffer[1];
					byte operationId = inputBuffer[2];
					
					int argument = 0;
					argument |= (inputBuffer[3] << 0)  & (0x000000FF);
					argument |= (inputBuffer[4] << 8)  & (0x0000FF00);
					argument |= (inputBuffer[5] << 16) & (0x00FF0000);
					argument |= (inputBuffer[6] << 24) & (0xFF000000);
					
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
						default:
							//handle error unsupported operation id								
						}
					} else if (operationType == OPERATION_TYPE_COLOR_SENSOR) {
						switch (operationId) {
						case OPERATION_ID_READ_COLOR:
							break;
						default:
							//handle error unsupported operation id								
						}
					} else if (operationType == OPERATION_TYPE_ULTRASONIC_SENSOR) {
						switch (operationId) {
						case OPERATION_ID_GET_DISTANCE:
							int distance = ultrasonicSensor.getDistance();							
							outputBuffer = new byte[4];
							outputBuffer[0] = (byte)(distance >> 0);
							outputBuffer[1] = (byte)(distance >> 1);
							outputBuffer[2] = (byte)(distance >> 2);
							outputBuffer[3] = (byte)(distance >> 3);
							break;
						default:
							//handle error unsupported operation id								
						}
					} else {
						//error unsupported operation type
					}
					out.flush();
					out.write(outputBuffer);
				}
			} catch (IOException e) {
				printToLcd("exception", 1000);
			}
						
			con.close();
		}		
	}	
}
