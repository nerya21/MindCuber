package robot;

import nxt.NxtOperation;

public class NxtMotor {
	
	private int id;
	
	public NxtMotor(int id) {
		this.id = id;
	}
	
	public final char getId() {
		
		char port = 'A';
		switch(id) {
			case 0:
				port='A';
				break;
			case 1:
				port='B';
				break;
			case 2:
				port='C';
				break;	
		}
		return port;
	}

	public void setSpeed(int speed) {
		NxtCommand.sendCommand(NxtOperation.OPERATION_TYPE_MOTOR, id, NxtOperation.OPERATION_ID_SET_SPEED, speed, 0);
	}
	
	public void rotate(int count) {
		NxtCommand.sendCommand(NxtOperation.OPERATION_TYPE_MOTOR, id, NxtOperation.OPERATION_ID_ROTATE, count, 0);
	}
	
	public void rotateTo(int limitAngle) {
		NxtCommand.sendCommand(NxtOperation.OPERATION_TYPE_MOTOR, id, NxtOperation.OPERATION_ID_ROTATE_TO, limitAngle, 0);		
	}
	
	public void resetTachoCount() {
		NxtCommand.sendCommand(NxtOperation.OPERATION_TYPE_MOTOR, id, NxtOperation.OPERATION_ID_RESET_TACHO_COUNT, 0, 0);
	}

	public int getTachoCount() {
		byte[] inputBuffer = NxtCommand.sendCommand(NxtOperation.OPERATION_TYPE_MOTOR, id, NxtOperation.OPERATION_ID_GET_TACHO_COUNT, 0, 4);
		int tachoCount = 0;
		
		tachoCount |= (((int)inputBuffer[0]) & 0x000000FF) << 0;
		tachoCount |= (((int)inputBuffer[1]) & 0x000000FF) << 8;
		tachoCount |= (((int)inputBuffer[2]) & 0x000000FF) << 16;
		tachoCount |= (((int)inputBuffer[3]) & 0x000000FF) << 24;
		
		return tachoCount;
	}
}
