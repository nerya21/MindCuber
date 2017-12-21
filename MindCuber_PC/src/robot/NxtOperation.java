package robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import application.Logger;
import application.LoggerGroup;
import application.LoggerLevel;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTConnector;

public class NxtOperation {

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
	
	static NXTConnector connection;
	
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static void init() throws Exception {			
		connection = new NXTConnector();	
//		
//		if (!connection.connectTo()){
//			throw new Exception("No NXT found using USB");
//		}
//		Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "NXT connection initialized successfully");
//		NXTCommand command = new NXTCommand(connection.getNXTComm());
//		command.startProgram("NxtApplication.nxj");		
//		connection.close();
		
		if (!connection.connectTo()){
			throw new Exception("No NXT found using USB");
		}
		Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "NXT application connected successfully");
	}
	
	public static void close() {			
		sendCommand(NxtOperation.OPERATION_TYPE_CLOSE_CONNECTION, 0, NxtOperation.OPERATION_ID_GET_DISTANCE, 0, 0);
		try {
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static byte[] sendCommand(byte operationType, int port, byte operationId, int argument, int expectedReturnSize) {			
		try {
			OutputStream out = connection.getOutputStream();
			InputStream in = connection.getInputStream();
			byte[] outputBuffer = { operationType, (byte)port, operationId, (byte)(argument >> 24), (byte)(argument >> 16), (byte)(argument >> 8), (byte)(argument >> 0) };
			byte[] inputBuffer = new byte[4];
			
			/* Send command */
			out.write(outputBuffer);
			out.flush();
			Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "Send command to NXT: " + bytesToHex(outputBuffer));
			
			/* Read returned value */
			in.read(inputBuffer);
			if (expectedReturnSize != 0) {
				Logger.log(LoggerLevel.DEBUG, LoggerGroup.ROBOT, "---> Response: " + bytesToHex(inputBuffer));
			}
			
			return inputBuffer;
		} catch (Exception e) {
			Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "Cannot send NXT operation");
			e.printStackTrace();
			return null;
		} 
	}
}
