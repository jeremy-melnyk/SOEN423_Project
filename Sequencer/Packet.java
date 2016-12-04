package packet;

import java.io.Serializable;
import java.net.InetAddress;

public class Packet implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private InetAddress senderAddress;
	private int senderPort;
	private int sequencernumber;
	private Operation operation;
	private OperationParameters operationParameters;

	public Packet(InetAddress senderAdress, int senderPort, Operation operation, OperationParameters operationParameters) {
		super();
		this.operation = operation;
		this.operationParameters = operationParameters;
		this.senderAddress = senderAdress;
		this.senderPort = senderPort;
	}
	
	public Packet(Operation operation, OperationParameters operationParameters) {
		super();
		this.operation = operation;
		this.operationParameters = operationParameters;
	}

	public Operation getReplicaOperation() {
		return operation;
	}

	public void setReplicaOperation(Operation replicaOperation) {
		this.operation = replicaOperation;
	}

	public OperationParameters getOperationParameters() {
		return operationParameters;
	}

	public void setOperationParameters(OperationParameters operationParameters) {
		this.operationParameters = operationParameters;
	}

	public InetAddress getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(InetAddress senderAddress) {
		this.senderAddress = senderAddress;
	}

	public int getSenderPort() {
		return senderPort;
	}

	public void setSenderPort(int senderPort) {
		this.senderPort = senderPort;
	}

	public int getSequencernumber() {
		return sequencernumber;
	}

	public void setSequencernumber(int sequencernumber) {
		this.sequencernumber = sequencernumber;
	}
	
	
}
