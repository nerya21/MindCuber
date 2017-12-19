package robot;

import java.io.InputStream;
import java.io.OutputStream;

import application.Logger;
import application.LoggerGroup;
import application.LoggerLevel;
import lejos.pc.comm.NXTConnector;

public class NxtOperation {

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
		
	static NXTConnector connection;
	
	public static int init() {			
		connection = new NXTConnector();	
		
		if (!connection.connectTo()){
			Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "No NXT found using USB. Abort");
			return 1;
		}
		
		Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "NXT connection initialized successfully");
		return 0;
	}
	
	public static byte[] sendCommand(byte operationType, int port, byte operationId, int argument, int expectedReturnSize) {			
		try {
			OutputStream out = connection.getOutputStream();
			InputStream in = connection.getInputStream();
			byte[] outputBuffer = { operationType, (byte)port, operationId, (byte)(argument >> 3), (byte)(argument >> 2), (byte)(argument >> 1), (byte)(argument >> 0) };
			byte[] inputBuffer = null;
			
			/* Send command */
			out.write(outputBuffer);
			out.flush();
			
			/* Read returned value */
			if (expectedReturnSize != 0) {
				in.read(inputBuffer);					
			}
			
			return inputBuffer;
		} catch (Exception e) {
			Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "Cannot send NXT operation");
			return null;
		} 
	}
}
