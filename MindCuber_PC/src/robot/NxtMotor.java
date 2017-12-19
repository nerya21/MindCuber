package robot;

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
		NxtOperation.sendCommand(NxtOperation.OPERATION_TYPE_MOTOR, id, NxtOperation.OPERATION_ID_SET_SPEED, speed, 0);
	}
	
	public void rotate(int count) {
		NxtOperation.sendCommand(NxtOperation.OPERATION_TYPE_MOTOR, id, NxtOperation.OPERATION_ID_ROTATE, count, 0);
	}
	
	public void rotateTo(int limitAngle) {
		NxtOperation.sendCommand(NxtOperation.OPERATION_TYPE_MOTOR, id, NxtOperation.OPERATION_ID_ROTATE_TO, limitAngle, 0);		
	}
	
	public void resetTachoCount() {
		NxtOperation.sendCommand(NxtOperation.OPERATION_TYPE_MOTOR, id, NxtOperation.OPERATION_ID_RESET_TACHO_COUNT, 0, 0);
	}
}
