package robot;

import nxt.NxtApplication;
import nxt.NxtOperation;

/**
 * Represents NXT's sensors in the PC project
 * <p>Each operation sent to sensors performed
 * by NxtApplication thru NxtCommand
 * 
 * @see NxtCommand
 * @see NxtApplication
 */
public class NxtSensor {

	/**
	 * Read color from sensor
	 * 
	 * @param numberOfSamples Number of reading to perform
	 * @return RGB array, each value between 0 to 255
	 */
	public int[] readColorRgb(int numberOfSamples) {
		byte[] inputBuffer = NxtCommand.sendCommand(NxtOperation.OPERATION_TYPE_COLOR_SENSOR, 0, NxtOperation.OPERATION_ID_READ_COLOR, numberOfSamples, 3);
		int[] colorRgb = new int[3];
		
		colorRgb[0] = ((int)inputBuffer[0]) & 0x000000FF;
		colorRgb[1] = ((int)inputBuffer[1]) & 0x000000FF;
		colorRgb[2] = ((int)inputBuffer[2]) & 0x000000FF;
		
		return colorRgb;
	}
	
	/**
	 * Get current distance read from the proximity sensor
	 * 
	 * @return Current distance
	 */
	public int getDistance() {
		byte[] inputBuffer = NxtCommand.sendCommand(NxtOperation.OPERATION_TYPE_ULTRASONIC_SENSOR, 0, NxtOperation.OPERATION_ID_GET_DISTANCE, 0, 4);
		int distance = 0;
		
		distance |= (((int)inputBuffer[0]) & 0x000000FF) << 0;
		distance |= (((int)inputBuffer[1]) & 0x000000FF) << 8;
		distance |= (((int)inputBuffer[2]) & 0x000000FF) << 16;
		distance |= (((int)inputBuffer[3]) & 0x000000FF) << 24;
		
		return distance;
	}
}
