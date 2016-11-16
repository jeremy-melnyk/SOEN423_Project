package packet;

public class Packet {
	private ReplicaOperation replicaOperation;
	private OperationParameters operationParameters;

	public Packet(ReplicaOperation replicaOperation, OperationParameters operationParameters) {
		super();
		this.replicaOperation = replicaOperation;
		this.operationParameters = operationParameters;
	}

	public ReplicaOperation getReplicaOperation() {
		return replicaOperation;
	}

	public void setReplicaOperation(ReplicaOperation replicaOperation) {
		this.replicaOperation = replicaOperation;
	}

	public OperationParameters getOperationParameters() {
		return operationParameters;
	}

	public void setOperationParameters(OperationParameters operationParameters) {
		this.operationParameters = operationParameters;
	}
}
