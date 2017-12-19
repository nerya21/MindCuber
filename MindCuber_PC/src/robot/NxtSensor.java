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
		
		colorRgb[0] = (int)inputBuffer[0];
		colorRgb[1] = (int)inputBuffer[1];
		colorRgb[2] = (int)inputBuffer[2];
		
		return colorRgb;
	}
	
	public int getDistance() {
		byte[] inputBuffer = NxtOperation.sendCommand(NxtOperation.OPERATION_TYPE_ULTRASONIC_SENSOR, id, NxtOperation.OPERATION_ID_GET_DISTANCE, 0, 4);
		int distance = 0;
		
		distance |= (inputBuffer[0] << 0)  & (0x000000FF);
		distance |= (inputBuffer[1] << 8)  & (0x0000FF00);
		distance |= (inputBuffer[2] << 16) & (0x00FF0000);
		distance |= (inputBuffer[3] << 24) & (0xFF000000);
		
		return distance;
	}
}
