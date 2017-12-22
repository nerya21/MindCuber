package robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import application.Logger;
import application.LoggerGroup;
import application.LoggerLevel;
import lejos.pc.comm.NXTConnector;
import nxt.NxtOperation;

public class NxtCommand {
	
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
	
	public static void init() {			
		connection = new NXTConnector();	
		
		if (!connection.connectTo()){
			Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "Cannot connect to NXT application");
			System.exit(1);
		}
		
		Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "NXT application connected successfully");
	}
	
	public static void close() {			
		sendCommand(NxtOperation.OPERATION_TYPE_CLOSE_CONNECTION, 0, NxtOperation.OPERATION_ID_GET_DISTANCE, 0, 0);
		try {
			connection.close();
		} catch (IOException e) {
			Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "NXT application connection failed to close");
		}
		Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "NXT application connection closed successfully");
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
			
			/* Read returned value */
			in.read(inputBuffer);
			
			return inputBuffer;
		} catch (Exception e) {
			Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "Cannot send NXT operation");
			e.printStackTrace();
			return null;
		} 
	}
}
