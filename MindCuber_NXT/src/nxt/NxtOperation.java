package nxt;

/**
 * This class lists the supported operations by the NXT application
 * 
 * @see NxtCommand
 * @see NxtApplication
 */
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

}
