package robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import application.Logger;
import application.LoggerGroup;
import application.LoggerLevel;
import lejos.pc.comm.NXTConnector;
import nxt.NxtOperation;
import nxt.NxtApplication;

/**
 * This class implements a connection to the NXT application
 * 
 * <p>The class allows sending commands to the NXT in order to control the motors and sensors. 
 * The NXT application must be running before initializing the class.
 * 
 * @see	NxtApplication
 * @see NxtOperation
 */
public class NxtCommand {
	
	static NXTConnector connection;
	
	/**
	 * Initialize connection to the NXT
	 * <p>NXT application must be running before initialization 
	 */
	public static void init() {			
		connection = new NXTConnector();	
		
		if (!connection.connectTo()){
			Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "Cannot connect to NXT application");
			System.exit(1);
		}
		
		Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "NXT application connected successfully");
	}
	
	/**
	 * Close connection to the NXT
	 */
	public static void close() {			
		sendCommand(NxtOperation.OPERATION_TYPE_CLOSE_CONNECTION, 0, NxtOperation.OPERATION_ID_GET_DISTANCE, 0, 0);
		try {
			connection.close();
		} catch (IOException e) {
			Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "NXT application connection failed to close");
		}
		Logger.log(LoggerLevel.INFO, LoggerGroup.ROBOT, "NXT application connection closed successfully");
	}
	
	/**
	 * Send command to the NXT
	 *
	 * @param operationType The operation type
	 * @param port Port of the motor or sensor if relevant
	 * @param operationId The operation ID
	 * @param argument Argument for the requested operation
	 * @param expectedReturnSize The expected return size of the operation (bytes)
	 * @return Response from the NXT if supported by the command
	 */
	public static byte[] sendCommand(byte operationType, int port, byte operationId, int argument, int expectedReturnSize) {			
		try {
			OutputStream out = connection.getOutputStream();
			InputStream in = connection.getInputStream();
			byte[] outputBuffer = {operationType, (byte)port, operationId, (byte)(argument >> 24), (byte)(argument >> 16), (byte)(argument >> 8), (byte)(argument >> 0)};
			byte[] inputBuffer = new byte[4];
			
			/* Send command */
			out.write(outputBuffer);
			out.flush();
			
			/* Read returned value */
			if (in.read(inputBuffer) == -1) {
				Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "Cannot send NXT operation: NXT connection closed");
			}
			
			return inputBuffer;
		} catch (Exception e) {
			Logger.log(LoggerLevel.ERROR, LoggerGroup.ROBOT, "Cannot send NXT operation");
			e.printStackTrace();
			return null;
		} 
	}
}
