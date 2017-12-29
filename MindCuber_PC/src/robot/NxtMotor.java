package robot;

import nxt.NxtOperation;
import nxt.NxtApplication;

/**
 * Represents NXT's motors in the PC project
 * <p>Each operation sent to motors performed
 * by NxtApplication thru NxtCommand
 * 
 * @see NxtCommand
 * @see NxtApplication
 */
public class NxtMotor {
	
	private int id;
	
	/**
	 * New motor c'tor
	 * @param id Motor's id
	 */
	public NxtMotor(int id) {
		this.id = id;
	}

	/**
	 * Set motor's speed
	 * @param speed Speed to set
	 */
	public void setSpeed(int speed) {
		NxtCommand.sendCommand(NxtOperation.OPERATION_TYPE_MOTOR, id, NxtOperation.OPERATION_ID_SET_SPEED, speed, 0);
	}
	
	/**
	 * Rotate motor
	 * @param count Degrees to rotate
	 */
	public void rotate(int count) {
		NxtCommand.sendCommand(NxtOperation.OPERATION_TYPE_MOTOR, id, NxtOperation.OPERATION_ID_ROTATE, count, 0);
	}
	
	/**
	 * Rotate motor to specified location
	 * @param limitAngle Degrees limit to rotate
	 */
	public void rotateTo(int limitAngle) {
		NxtCommand.sendCommand(NxtOperation.OPERATION_TYPE_MOTOR, id, NxtOperation.OPERATION_ID_ROTATE_TO, limitAngle, 0);		
	}
	
	/**
	 * Reset the current motor's location to 0
	 */
	public void resetTachoCount() {
		NxtCommand.sendCommand(NxtOperation.OPERATION_TYPE_MOTOR, id, NxtOperation.OPERATION_ID_RESET_TACHO_COUNT, 0, 0);
	}

	/**
	 * Get the current motor's location
	 * @return Current motor's location
	 */
	public int getTachoCount() {
		byte[] inputBuffer = NxtCommand.sendCommand(NxtOperation.OPERATION_TYPE_MOTOR, id, NxtOperation.OPERATION_ID_GET_TACHO_COUNT, 0, 4);
		int tachoCount = 0;
		
		tachoCount |= (((int)inputBuffer[0]) & 0x000000FF) << 0;
		tachoCount |= (((int)inputBuffer[1]) & 0x000000FF) << 8;
		tachoCount |= (((int)inputBuffer[2]) & 0x000000FF) << 16;
		tachoCount |= (((int)inputBuffer[3]) & 0x000000FF) << 24;
		
		return tachoCount;
	}
	
	/**
	 * Set the motor on
	 */
	public void forward() {
		NxtCommand.sendCommand(NxtOperation.OPERATION_TYPE_MOTOR, id, NxtOperation.OPERATION_ID_FORWARD, 0, 0);
	}
}
