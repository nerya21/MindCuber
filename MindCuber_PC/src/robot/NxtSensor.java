package robot;

public class NxtSensor {
	
	private int id;
	
	public NxtSensor(int id) {
		this.id = id;
	}
	
	public final int getId() {
		return id;
	}

	public int[] readColorRgb(int numberOfSamples) {
		byte[] inputBuffer = NxtOperation.sendCommand(NxtOperation.OPERATION_TYPE_COLOR_SENSOR, id, NxtOperation.OPERATION_ID_READ_COLOR, numberOfSamples, 3);
		int[] colorRgb = new int[3];
		
		colorRgb[0] = ((int)inputBuffer[0]) & 0x000000FF;
		colorRgb[1] = ((int)inputBuffer[1]) & 0x000000FF;
		colorRgb[2] = ((int)inputBuffer[2]) & 0x000000FF;
		
		return colorRgb;
	}
	
	public int getDistance() {
		byte[] inputBuffer = NxtOperation.sendCommand(NxtOperation.OPERATION_TYPE_ULTRASONIC_SENSOR, id, NxtOperation.OPERATION_ID_GET_DISTANCE, 0, 4);
		int distance = 0;
		
		distance |= (((int)inputBuffer[0]) & 0x000000FF) << 0;
		distance |= (((int)inputBuffer[1]) & 0x000000FF) << 8;
		distance |= (((int)inputBuffer[2]) & 0x000000FF) << 16;
		distance |= (((int)inputBuffer[3]) & 0x000000FF) << 24;
		
		return distance;
	}
}
