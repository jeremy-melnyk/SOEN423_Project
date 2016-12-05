package packet;

public class ReplicaAliveOperation extends OperationParameters {
	private static final long serialVersionUID = 1L;
	int portToCheck;

	public ReplicaAliveOperation(int portToCheck) {
		super();
		this.portToCheck = portToCheck;
	}
	
	public ReplicaAliveOperation() {
		super();
		this.portToCheck = -1;
	}

	public int getPortToCheck() {
		return portToCheck;
	}

	public void setPortToCheck(int portToCheck) {
		this.portToCheck = portToCheck;
	}
}
