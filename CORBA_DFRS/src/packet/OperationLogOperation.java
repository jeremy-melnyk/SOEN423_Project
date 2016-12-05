package packet;

public class OperationLogOperation extends OperationParameters {
	private static final long serialVersionUID = 1L;
	private int replicaManagerPort;

	public OperationLogOperation(int replicaManagerPort) {
		super();
		this.replicaManagerPort = replicaManagerPort;
	}

	public int getReplicaManagerPort() {
		return replicaManagerPort;
	}

	public void setReplicaManagerPort(int replicaManagerPort) {
		this.replicaManagerPort = replicaManagerPort;
	}
}
